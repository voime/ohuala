package com.voime.ohuala;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;


public class MainActivity extends MapActivity {
	public static final String PREFS_NAME = "ohuala";
	private static final int REQUEST_CODE = 10;

	public MapController mapController;
	private MyCustomMapView mapView = null;
	//private MapView mapView = null;
	private LocationManager locationManager;
	private MyLocationOverlay myLocationOverlay;
	public GeoPoint my_position;
	public GeoPoint circle_pos;
	public float radius;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = (MyCustomMapView)findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    int zoom = settings.getInt("zoom", 14);
		mapController.setZoom(zoom); // Zoon 1 is world view
		mapView.setSatellite(true);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler());
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(
						myLocationOverlay.getMyLocation());
			}
		});
		my_position = myLocationOverlay.getMyLocation();
		mapView.setOnLongpressListener(new MyCustomMapView.OnLongpressListener() {
	        public void onLongpress(final MapView view, final GeoPoint longpressLocation) {
	            runOnUiThread(new Runnable() {
		            public void run() {
		            	Intent popup = new Intent(getBaseContext(), Popup.class);
		            	popup.putExtra("coordinates", longpressLocation.toString());
		            	startActivityForResult(popup, REQUEST_CODE);
		            	
		            }
	            });
	        }
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("coordinates") && data.hasExtra("radius")) {
				radius=data.getExtras().getFloat("radius");
				String[] point_t = data.getExtras().getString("coordinates").split(",");
				int x = Integer.parseInt(point_t[0]);
				int y = Integer.parseInt(point_t[1]);
				GeoPoint circle_position = new GeoPoint(x,y);
				mapView.getOverlays().add(new CircleOverlay(this, mapView, circle_position, radius));
	      	}
		}
	}
	  
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public class GeoUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapController.animateTo(point); // mapController.setCenter(point);	
			my_position = point;			
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	@Override
	protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String maptype = settings.getString("maptype", "1");
		Boolean compass = settings.getBoolean("compass", true);
		if (compass) {
			myLocationOverlay.enableCompass();
		}
		if (maptype.equals("2")){
			mapView.setSatellite(true);
		}else{
			mapView.setSatellite(false);			
		}
        
	}
	@Override
    protected void onStop(){
       super.onStop();
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("zoom", mapView.getZoomLevel());
      editor.commit();
    }
	@Override
	protected void onPause() {
        //---unregister the receiver---
     //   unregisterReceiver(intentReceiver);
		super.onPause();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	    System.runFinalizersOnExit(true);
	    System.exit(0);
	}


}
