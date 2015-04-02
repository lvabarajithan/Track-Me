package com.haab.trackme;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.haab.trackme.database.DatabaseAdapter;
import com.haab.trackme.utils.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by abara on 3/29/2015.
 */
public class MyPlaces extends ActionBarActivity{

    private RecyclerView places;
    private ArrayList<String> my_places,my_dates;
    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplaces);
        getSupportActionBar().setTitle("My Places");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        places = (RecyclerView) findViewById(R.id.places);
        my_places = new ArrayList<>();
        my_dates = new ArrayList<>();
        adapter = new DatabaseAdapter(this);

        places.setHasFixedSize(false);
        places.setItemAnimator(new DefaultItemAnimator());
        places.setLayoutManager(new LinearLayoutManager(this));

        my_places = adapter.getLocality();
        my_dates = adapter.getDate();

        Collections.reverse(my_places);
        Collections.reverse(my_dates);

        if(adapter.getLocality() != null && adapter.getDate() != null)
            places.setAdapter(new ListAdapter(this,my_places,my_dates));
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
