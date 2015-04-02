package com.haab.trackme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by abara on 3/29/2015.
 */
public class Settings extends ActionBarActivity{

    private SharedPreferences prefs;
    private CheckBox box,noti;
    private NotificationManagerCompat manager;

    public static final String SHOW_KEY="show_address";
    public static final String SHOW_NOTIFICATION = "show_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        manager= NotificationManagerCompat.from(this);

        box = (CheckBox) findViewById(R.id.enableBox);
        noti = (CheckBox) findViewById(R.id.enablenoti);
        box.setChecked(prefs.getBoolean(SHOW_KEY,true));
        noti.setChecked(prefs.getBoolean(SHOW_NOTIFICATION,true));
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean(SHOW_KEY,true).apply();
                }else
                    prefs.edit().putBoolean(SHOW_KEY,false).apply();
            }
        });

        noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   prefs.edit().putBoolean(SHOW_NOTIFICATION,true).commit();
                   if(prefs.getBoolean(MainActivity.IS_TRACKING,false)){
                       MainActivity.showNotification();
                   }
               }else{
                   prefs.edit().putBoolean(SHOW_NOTIFICATION,false).commit();
                   if(prefs.getBoolean(MainActivity.IS_TRACKING,false)){
                       MainActivity.cancelNotification();
                   }
               }
            }
        });

    }
}
