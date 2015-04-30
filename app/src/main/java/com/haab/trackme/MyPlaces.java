package com.haab.trackme;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.haab.trackme.data.LocationContract;
import com.haab.trackme.utils.ListAdapter;

import java.util.ArrayList;

/**
 * Created by abara on 3/29/2015.
 */
public class MyPlaces extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView places;
    String URL = LocationContract.URL;
    private static final int LOADER_ID=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplaces);
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toolbar bar = (Toolbar) findViewById(R.id.places_bar);
            setSupportActionBar(bar);
        }
            getSupportActionBar().setTitle("My Places");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        places = (RecyclerView) findViewById(R.id.places);

        places.setHasFixedSize(false);
        places.setItemAnimator(new DefaultItemAnimator());
        places.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
        //places.setAdapter(new ListAdapter(this,getLocality(),getDate(),getAddresses(),getLocation()));
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

    private ArrayList<String> getLocation(){
        Uri location = Uri.parse(URL);
        Cursor cursor = managedQuery(location,null,null,null,null);
        ArrayList<String> mAllLocation = new ArrayList<>();
        while(cursor.moveToNext()){
            String latitude = cursor.getString(cursor.getColumnIndex(LocationContract.COLUMN_LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndex(LocationContract.COLUMN_LONGITUDE));
            mAllLocation.add(latitude + " , "+ longitude);
        }
        return mAllLocation;
    }

    private ArrayList<String> getAddresses(){
        Uri location = Uri.parse(URL);
        Cursor cursor = managedQuery(location, null, null, null, null);
        ArrayList<String> addresses = new ArrayList<>();
        while(cursor.moveToNext()){
            String address = cursor.getString(cursor.getColumnIndex(LocationContract.COLUMN_ADDRESS));
            addresses.add(address);
        }
        return addresses;
    }
    private ArrayList<String> getLocality(){
        Uri location = Uri.parse(URL);
        Cursor cursor = managedQuery(location, null, null, null, null);
        ArrayList<String> locality = new ArrayList<>();
        while(cursor.moveToNext()){
            String address = cursor.getString(cursor.getColumnIndex(LocationContract.COLUMN_ADDRESS));
            String loc = address.substring(0,address.indexOf(" "));
            locality.add(loc);
        }
        return locality;
    }


    private ArrayList<String> getDate(){
        Uri location = Uri.parse(URL);
        Cursor cursor = managedQuery(location, null, null, null, null);
        ArrayList<String> myDate = new ArrayList<>();
        while(cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndex(LocationContract.COLUMN_DATE));
            myDate.add(date);
        }
        return myDate;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                LocationContract.CONTENT_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        places.setAdapter(new ListAdapter(this,getLocality(),getDate(),getAddresses(),getLocation()));
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        places.setAdapter(new ListAdapter(this,getLocality(),getDate(),getAddresses(),getLocation()));
    }
}
