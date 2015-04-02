package com.haab.trackme.utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import java.io.IOException;
import java.util.List;


/*
Created by abara on 3/28/2015.
*/


public class Tracker extends Service implements LocationListener {

    private final Context mContext;
    private static final String TAG = "Track me";
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    private Location mLocation;
    private double latitude;
    private double longitude;

    private static final long DISTANCE_FOR_UPDATE = 1; // meters

    private static final long TIME_BTW_UPDATES = 1000 * 5 ; // 1000 millisecs = 1 sec

    private LocationManager locationManager;

    public Tracker(Context context){
        mContext = context;
        getLocation();
    }

    public Location getLocation() {

        try{
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                //Log.d(TAG,"No provider is available");
            }else{
                canGetLocation = true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            TIME_BTW_UPDATES,
                            DISTANCE_FOR_UPDATE,this);
                    //Log.d(TAG,"Network is the provider");

                    if(locationManager != null ){
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPS();
                    }
                }

                if(isGPSEnabled){
                    if(mLocation != null){

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                TIME_BTW_UPDATES,
                                DISTANCE_FOR_UPDATE,this
                        );
                        //Log.d(TAG,"GPS is the provider");
                        if(locationManager != null ){
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateGPS();
                        }
                    }
                }
            }

        }catch (Exception e){
            //Log.d(TAG,"Cannot connect to LocationManager :P");
        }

        return mLocation;
    }

    private void updateGPS() {

        if(mLocation != null){
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        }

    }

    public void stopTracker(){
        if(locationManager != null){
            locationManager.removeUpdates(Tracker.this);
        }
    }

    public double getLatitude()
    {
        if (mLocation != null)
        {
            latitude = mLocation.getLatitude();
        }

        return latitude;
    }
    public double getLongitude()
    {
        if (mLocation != null)
        {
            longitude = mLocation.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation(){
        return canGetLocation;
    }

    public void showSettingAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Location Service");
        builder.setMessage("Enable the location service now ?");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public String getAddressLine(Context context)
    {
        String result= "not available :(";
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                //sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
