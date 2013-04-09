package com.voime.ohuala;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Spinner;


public class Popup extends Activity {
	public static final String PREFS_NAME = "ohuala";
	float radius;
	boolean radius_changed;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
          return;
        }
        String[] coordinates = extras.getString("coordinates").split(",");
        if (coordinates != null) {
          EditText txtLat = (EditText) findViewById(R.id.txtLat);
          EditText txtLng = (EditText) findViewById(R.id.txtLng);
          txtLat.setText(coordinates[0]);
          txtLng.setText(coordinates[1]);        
        }
        
        EditText txtRadius = (EditText) findViewById(R.id.txtRadius);
        EditText txtTNT = (EditText) findViewById(R.id.txtTNT);
        Spinner spnValem = (Spinner) findViewById(R.id.spnValem);
        txtRadius.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            	radius_changed=true;
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
         });
        txtTNT.setOnFocusChangeListener(new OnFocusChangeListener() {
        	public void onFocusChange(View v, boolean hasFocus) {
        		//Toast.makeText(getApplicationContext(), "tntChange", Toast.LENGTH_SHORT).show();
        		if(!hasFocus)
                    arvuta();
            }
        });
        
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    String def_radius = settings.getString("radius", "0");
	    String def_TNT = settings.getString("TNT", "0");
	    int def_valem = settings.getInt("valem", 0);
	    txtRadius.setText(def_radius);
        txtTNT.setText(def_TNT);
        spnValem.setSelection(def_valem);
    }
    public void send(View view) {
        finish();
    }
    @Override
    public void finish() {
      
      EditText txtLat = (EditText) findViewById(R.id.txtLat);
      EditText txtLng = (EditText) findViewById(R.id.txtLng);      
      String coordinates = txtLat.getText().toString() + "," +  txtLng.getText().toString();
      
      if (!radius_changed){
    	  arvuta();
      }
      EditText txtTNT = (EditText) findViewById(R.id.txtTNT);  
      EditText txtRadius = (EditText) findViewById(R.id.txtRadius);
      Spinner spnValem = (Spinner) findViewById(R.id.spnValem);
      
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString("radius", txtRadius.getText().toString());
      editor.putString("TNT", txtTNT.getText().toString());
      editor.putInt("valem", spnValem.getSelectedItemPosition());
      editor.commit();
      
      radius = Float.parseFloat(txtRadius.getText().toString());
      Intent data = new Intent();
      data.putExtra("coordinates", coordinates);
      data.putExtra("radius", radius);
      setResult(RESULT_OK, data);
      super.finish();
    }
    public void arvuta() {
        EditText txtTNT = (EditText) findViewById(R.id.txtTNT);
        Spinner spnValem = (Spinner) findViewById(R.id.spnValem);
        EditText txtRadius = (EditText) findViewById(R.id.txtRadius);
        //String spin = spnValem.getItemAtPosition(spnValem.getSelectedItemPosition()).toString();
        int spn_pos = spnValem.getSelectedItemPosition();
        Float tnt = Float.parseFloat(txtTNT.getText().toString());
        //Toast.makeText(getApplicationContext(), String.valueOf(tnt) ,Toast.LENGTH_SHORT).show();
        //Log.i("message", String.valueOf(spn_pos));
        switch (spn_pos){
	        case 0:
	            radius = (float) Math.pow(tnt,1.0/3.0) * 300;
	            break;
	        case 1:
	            radius = (float) Math.pow(tnt,1.0/3.0) * 500;
	            break;
	        case 2:
	            radius = (float) Math.pow(tnt,1.0/6.0) * 634;
	            break;
	        case 3:
	            radius = (float) Math.pow(tnt,1.0/6.0) * 444;
	            break;
        }
        txtRadius.setText(String.valueOf(radius));
    }

}
