package com.haab.trackme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haab.trackme.utils.Tracker;

/**
 * Created by abara on 3/31/2015.
 */
public class DetailsActivity extends ActionBarActivity {

    private TextView mLocation,mAddress,mDate;
    private Button mMap,mNavigate;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLocation = (TextView) findViewById(R.id.loc_detail);
        mAddress = (TextView) findViewById(R.id.address_det);
        mDate = (TextView) findViewById(R.id.date_det);

        mMap = (Button) findViewById(R.id.map_details);
        mNavigate = (Button) findViewById(R.id.nav_details);

        tracker = new Tracker(this);

        String str = getIntent().getStringExtra("location");
        mLocation.setText(str);
        String str1 = getIntent().getStringExtra("address");
        mAddress.setText(str1);
        String str2 = getIntent().getStringExtra("date");
        mDate.setText(str2);

        mMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String location = mLocation.getText().toString();
                String lat = location.substring(0, location.indexOf(" ,"));
                String lon = location.substring(location.indexOf(", ")+1);

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/?q=" + lat + "," + lon));
                startActivity(intent);
            }
        });
        mNavigate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String location = mLocation.getText().toString();
                String lat = location.substring(0, location.indexOf(" ,"));
                String lon = location.substring(location.indexOf(", ")+1);
                tracker.getLocation();
                if(tracker.canGetLocation()) {
                    Intent navigate = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + tracker.getLatitude() + "," + tracker.getLongitude() +
                                    "&daddr=" + lat + "," + lon));
                    startActivity(navigate);
                    tracker.stopTracker();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
