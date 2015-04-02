/*
package com.haab.trackme.utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.haab.trackme.MainActivity;
import com.haab.trackme.database.DatabaseAdapter;

*/
/**
 * Created by abara on 3/29/2015.
 *//*

public class TrackService extends Service {

    private SharedPreferences prefs;
    private DatabaseAdapter mAdapter;
    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mAdapter= new DatabaseAdapter(getApplicationContext());
        tracker = new Tracker(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(), "calling onStartCommand", Toast.LENGTH_SHORT).show();


            if(!prefs.getString(MainActivity.LAST_LOCATION,"0 , 0").contentEquals(tracker.getLocationString())
                    && !prefs.getString(MainActivity.LAST_ADDRESS, "not available :(" ).
                    contentEquals(tracker.getAddressLine(getApplicationContext()))){
                long id = mAdapter.insertData(tracker.getLatitude(), tracker.getLongitude(), tracker.getAddressLine(this));
                if(id < 0){
                    Toast.makeText(getApplicationContext(), "Tracking error!", Toast.LENGTH_SHORT).show();
                }
                stopSelf();
                Toast.makeText(getApplicationContext(), "Created data and stopped service", Toast.LENGTH_SHORT).show();
            }
    return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
*/
