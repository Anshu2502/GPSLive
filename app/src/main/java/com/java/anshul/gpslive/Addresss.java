package com.java.anshul.gpslive;

/**
 * Created by Anshul on 6/16/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Anshul on 6/16/2016.
 */
public class Addresss extends FragmentActivity implements LocationListener {
    TextView myLatitude,myLongitude,myAddress;
    LocationManager locationManager ;
    String provider;
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.address);
        myLatitude = (TextView)findViewById(R.id.mylatitude);
        myLongitude = (TextView)findViewById(R.id.mylongitude);
        myAddress = (TextView)findViewById(R.id.myaddress);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if(provider!=null && !provider.equals("")){

            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 2000, 1, this);

            if(location!=null) {
                onLocationChanged(location);
                Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");
                        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        myAddress.setText(strReturnedAddress.toString());
                    } else {
                        myAddress.setText("No Address returned!");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    myAddress.setText("Canont get Address!");
                }
            }
            else {
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(Addresss.this, MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    /*@Override
    public void onStop(){
        super.onStop();
        if(locationManager !=null)
            locationManager.removeUpdates(this);
    }*/



    @Override
    public void onLocationChanged(Location location) {
        myLatitude.setText("Latitude: " + location.getLatitude());

        myLongitude.setText("Longitude: " + location.getLongitude());


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
