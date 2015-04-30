package com.haab.trackme.data;

import android.net.Uri;

/**
 * Created by abara on 4/23/2015.
 */
public class LocationContract {


    public static final String UID = "_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_DATE = "date";
    public static final String TABLE_NAME="location_table";

    public static final String PROVIDER_NAME ="com.haab.trackme.Location";
    public static final String URL= "content://"+PROVIDER_NAME+"/location_table";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final int LOCATIONS= 1;
    public static final int LOCATIONS_ID= 2;

}
