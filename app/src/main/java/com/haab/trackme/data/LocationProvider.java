package com.haab.trackme.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.haab.trackme.database.LocationDatabaseHelper;

import java.util.HashMap;

/**
 * Created by abara on 4/23/2015.
 */
public class LocationProvider extends ContentProvider {

    private static HashMap<String,String> LOCATION_PROJECTION_MAP;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(LocationContract.PROVIDER_NAME,"location_table",LocationContract.LOCATIONS);
        uriMatcher.addURI(LocationContract.PROVIDER_NAME,"location_table/#",LocationContract.LOCATIONS_ID);
    }
    private SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        LocationDatabaseHelper mAdapter = new LocationDatabaseHelper(getContext());
        db = mAdapter.getWritableDatabase();
        return (db == null)?false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LocationContract.TABLE_NAME);

        switch(uriMatcher.match(uri)){
            case LocationContract.LOCATIONS:
                queryBuilder.setProjectionMap(LOCATION_PROJECTION_MAP);
                break;
            case LocationContract.LOCATIONS_ID:
                queryBuilder.appendWhere(LocationContract.UID+" = "+uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }
        Cursor c = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        /*switch(uriMatcher.match(uri)){

        }*/
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(LocationContract.TABLE_NAME,"",values);
        if(id > 0){
            Uri _uri = ContentUris.withAppendedId(LocationContract.CONTENT_URI,id);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        throw new SQLException("Failed to insert into "+uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LocationContract.LOCATIONS:
                count = db.delete(LocationContract.TABLE_NAME,selection,selectionArgs);
                break;
            case LocationContract.LOCATIONS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(LocationContract.TABLE_NAME, LocationContract.UID +" = "+id+
                        (!TextUtils.isEmpty(selection)?" AND ("+
                        selection + ')' : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int count = 0;
        switch(uriMatcher.match(uri)){
            case LocationContract.LOCATIONS:
                count = db.update(LocationContract.TABLE_NAME,values,selection,selectionArgs);
                break;
            case LocationContract.LOCATIONS_ID:
                count = db.update(LocationContract.TABLE_NAME,values,LocationContract.UID +
                                " = "+uri.getPathSegments().get(1)+ (!TextUtils.isEmpty(selection) ? " AND ("+selection+')' : "") ,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri );
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
