package com.voime.ohuala;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

class CircleOverlay extends MyLocationOverlay {

    private GeoPoint gp1;
    private float radius;
    private Paint circlePainter;
	private MapView mapView;
	
    public CircleOverlay(Context context, MapView mapView, GeoPoint gp1, float radius) {
		super(context, mapView);
    	this.gp1 = gp1;
        this.radius = radius;
        this.mapView = mapView;  
    }

    @Override
    public synchronized boolean draw(Canvas canvas, MapView mapView, boolean shadow,
            long when) {
        // TODO Auto-generated method stub
        com.google.android.maps.Projection projection = mapView.getProjection();
        if (shadow == false) {
        	
        	circlePainter = new Paint();
        	circlePainter.setAntiAlias(true);
        	//circlePainter.setStrokeWidth(10.0f);
        	circlePainter.setColor(Color.RED);
        	circlePainter.setStyle(Style.FILL);
        	circlePainter.setAlpha(50);
        	         
            Point point = new Point();
            projection.toPixels(gp1, point);

            int circle_radius = metersToRadius(gp1.getLatitudeE6() /1000000);
            
            canvas.drawCircle((float) point.x, (float) point.y, circle_radius, circlePainter);
        }
        return super.draw(canvas, mapView, shadow, when);
    }
	public int metersToRadius(double latitude) {
	    return (int) (mapView.getProjection().metersToEquatorPixels(radius) * (1/ Math.cos(Math.toRadians(latitude))));         
	}
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // TODO Auto-generated method stub

        super.draw(canvas, mapView, shadow);
    }

}

