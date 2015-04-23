package com.haab.trackme;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.haab.trackme.database.DatabaseAdapter;
import com.haab.trackme.utils.Tracker;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LocationListener {

    private DatabaseAdapter mAdapter;
    private TextView mLocation,mAddress,lastLoc,lastAdd;
    private Button start_track,nav_here,share_btn;
    private SharedPreferences prefs;
    private CardView lastCard,mainCard;
    private FloatingActionButton mFab;

    private static NotificationManagerCompat manager ;
    private static NotificationCompat.Builder builder ;
    private static PendingIntent pendingIntent;
    private static Intent intent;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    private Location location;
    private double latitude;
    private double longitude;

    private static final long DISTANCE_FOR_UPDATE = 10; // meters
    private static final long TIME_BTW_UPDATES = 1000 * 60 ; // 1000 millisecs = 1 sec
    public static final int NOTIFICATION_KEY = 9954;

    private LocationManager locationManager;

    public static final String LAST_LOCATION = "last_loc";
    public static final String LAST_ADDRESS = "last_add";
    public static final String IS_TRACKING = "is_tracking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new DatabaseAdapter(this);
        mLocation = (TextView) findViewById(R.id.location_txt);
        mAddress = (TextView) findViewById(R.id.address_txt);
        lastLoc = (TextView) findViewById(R.id.last_location);
        lastAdd = (TextView) findViewById(R.id.address_txt_last);
        start_track = (Button) findViewById(R.id.startTrack);
        share_btn = (Button) findViewById(R.id.share_btn);
        nav_here = (Button) findViewById(R.id.nav_there);
        lastCard = (CardView) findViewById(R.id.last_card);
        mainCard = (CardView) findViewById(R.id.card_main);
        mFab = (FloatingActionButton) findViewById(R.id.get_location);

        manager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this);
        intent = new Intent(this,MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        lastAdd.setText(prefs.getString(LAST_ADDRESS,"not available :("));
        lastLoc.setText(prefs.getString(LAST_LOCATION,"0 , 0"));
        if(prefs.getBoolean(IS_TRACKING,true)){
            start_track.setText("STOP TRACKING");
        }else{
            start_track.setText("START TRACKING");
        }

        if(savedInstanceState != null){
            mLocation.setText(savedInstanceState.getString("location_change"));
            mAddress.setText(savedInstanceState.getString("address_change"));
        }

        mFab.setColorNormalResId(R.color.colorAccent);
        mFab.setColorPressedResId(R.color.colorAccentPressed);
        mFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_get_location));

        /*if(prefs.getBoolean(IS_TRACKING,true)) {
            if(!mLocation.getText().toString().contentEquals(lastLoc.getText().toString())
                    && !mAddress.getText().toString().contentEquals(lastAdd.getText().toString())){
                long id = mAdapter.insertData(tracker.getLatitude(), tracker.getLongitude(), tracker.getAddressLine(this));
                if(id < 0){
                    Toast.makeText(MainActivity.this,"Tracking error!",Toast.LENGTH_SHORT).show();
                }
        }
        }*/
        start_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getBoolean(IS_TRACKING,true)){
                    //stopService(new Intent(getApplicationContext(),TrackService.class));
                    stopTracker();
                    prefs.edit().putBoolean(IS_TRACKING,false).commit();
                    start_track.setText("START TRACKING");
                    cancelNotification();
                    Toast.makeText(MainActivity.this,"Tracking stopped",Toast.LENGTH_SHORT).show();
                }else{
                    //startService(new Intent(getApplicationContext(), TrackService.class));
                    prefs.edit().putBoolean(IS_TRACKING, true).commit();
                    getLocation();
                    start_track.setText("STOP TRACKING");
                    if(prefs.getBoolean(Settings.SHOW_NOTIFICATION,true))
                        showNotification();
                    Toast.makeText(MainActivity.this,"Tracking...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mAddress.getText().toString() != getResources().getString(R.string.location_txt)) {

                    String location = mLocation.getText().toString();
                    String lat = location.substring(0, location.indexOf(" ,"));
                    String lon = location.substring(location.indexOf(", ")+1);

                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_TEXT, "Hey, I'm at "+mAddress.getText().toString()+"\n"
                                    +"http://maps.google.com/?q=" + lat + "," + lon+"\nSent from Track me");
                    share.setType("text/plain");
                    try {
                        startActivity(share);
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this,"Oops, No apps to share",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mainCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (mLocation.getText().toString() != getResources().getString(R.string.location_txt)) {

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/?q=" + getLatitude() + "," + getLongitude()));
                    startActivity(intent);

                }
            }
        });

        nav_here.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!lastLoc.getText().toString().contentEquals("0 , 0")) {

                    String location = lastLoc.getText().toString();
                    String last_lat = location.substring(0, location.indexOf(" ,"));
                    String last_long = location.substring(location.indexOf(", ")+1);
                Intent navigate = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+getLatitude()+","+getLongitude()+
                                "&daddr="+last_lat+","+last_long));
                    startActivity(navigate);
                }
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = new Tracker(MainActivity.this);
                tracker.getLocation();
                if(tracker.canGetLocation()){
                    mLocation.setText(tracker.getLatitude()+" , "+tracker.getLongitude());
                    mAddress.setText(tracker.getAddressLine(MainActivity.this));
                    tracker.stopTracker();
                }else{
                    tracker.showSettingAlert();
                    tracker.stopTracker();
                }
            }
        });

        lastCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!lastLoc.getText().toString().contentEquals("0 , 0")) {

                    String location = lastLoc.getText().toString();
                    String last_lat = location.substring(0, location.indexOf(" ,"));
                    String last_long = location.substring(location.indexOf(", ")+1);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/?q=" + last_lat + "," + last_long));
                    startActivity(intent);
                }
            }
        });

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toolbar appBar = (Toolbar) findViewById(R.id.tabBar);
            setSupportActionBar(appBar);
        }
    }

    public static void cancelNotification() {

        manager.cancel(NOTIFICATION_KEY);

    }

    public static void showNotification() {

            builder.setContentTitle("Track Me")
                    .setContentText("Tracking your location...")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(Color.parseColor("#2196F3"))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            manager.notify(NOTIFICATION_KEY, builder.build());


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs.getBoolean(IS_TRACKING,true))
            getLocation();
        if(canGetLocation()){
            String loc = getLatitude()+" , "+getLongitude();
            mLocation.setText(loc);
            if(prefs.getBoolean(Settings.SHOW_KEY,true)) {
                mAddress.setVisibility(View.VISIBLE);
                mAddress.setText(getAddressLine());

                prefs.edit().putString(LAST_ADDRESS,getAddressLine()).apply();
                prefs.edit().putString(LAST_LOCATION,loc).apply();
            }else{
                mAddress.setVisibility(View.GONE);
            }
        }else{

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("location_change",mLocation.getText().toString());
        outState.putString("address_change",mAddress.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this,Settings.class));
            return true;
            case R.id.action_myplaces:
                startActivity(new Intent(MainActivity.this,MyPlaces.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Location getLocation() {

        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                //Log.d(TAG,"No provider is available");
                this.showSettingAlert();
            }else{
                if(prefs.getBoolean(IS_TRACKING,true))
                    startTracking();
            }

        }catch (Exception e){
            //Log.d(TAG,"Cannot connect to LocationManager :P");
        }

        return location;
    }

    private void startTracking() {

        canGetLocation = true;
        if(isNetworkEnabled){
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    TIME_BTW_UPDATES,
                    DISTANCE_FOR_UPDATE,this);
            //Log.d(TAG,"Network is the provider");

            if(locationManager != null ){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                updateGPS();
                prefs.edit().putBoolean(IS_TRACKING,true).commit();
            }
        }

        if(isGPSEnabled){

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    TIME_BTW_UPDATES,
                    DISTANCE_FOR_UPDATE,this
            );
            //Log.d(TAG,"GPS is the provider");
            if(locationManager != null ){
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateGPS();
                prefs.edit().putBoolean(IS_TRACKING,true).commit();
            }
        }

    }

    private void updateGPS() {

        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

    }

    public void stopTracker(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
            prefs.edit().putBoolean(IS_TRACKING,false).commit();
        }
    }

    public double getLatitude()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
        }

        return latitude;
    }
    public double getLongitude()
    {
        if (location != null)
        {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation(){
        return canGetLocation;
    }

    public String getLocationString(){
        return getLatitude()+" , "+getLongitude();
    }

    public void showSettingAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Service");
        builder.setMessage("Enable the location service now ?");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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


    public String getAddressLine()
    {
        String result= "not available :(";
        Geocoder geocoder = new Geocoder(this);
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

        mLocation.setText(getLocationString());
        mAddress.setText(getAddressLine());
        if(!mLocation.getText().toString().contentEquals(getResources().getString(R.string.location_txt)) &&
                !mAddress.getText().toString().contentEquals(getResources().getString(R.string.location_txt))) {
            String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            long id = mAdapter.insertData(getLatitude(), getLongitude(), getAddressLine(),mydate);
            /*if (id >= 0) {
                Toast.makeText(this, "Data inserted", Toast.LENGTH_SHORT).show();
            }*/
        }
        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();
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
