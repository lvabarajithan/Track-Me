package com.haab.trackme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by abara on 3/28/2015.
 */
public class DatabaseAdapter {

    LocationDatabaseHelper helper;

    public DatabaseAdapter(Context context){
        helper = new LocationDatabaseHelper(context);
    }

    public long insertData(double lat, double lon, String address,String date){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationDatabaseHelper.LATITUDE,lat);
        values.put(LocationDatabaseHelper.LONGITUDE,lon);
        values.put(LocationDatabaseHelper.ADDRESS,address);
        values.put(LocationDatabaseHelper.DATE,date);
        long id = db.insert(LocationDatabaseHelper.TABLE_NAME,null,values);
        return id;
    }

   /*
    // to get latitude
   public List<Double> getLatitude(){
        String[] columns = {LocationDatabaseHelper.LATITUDE};
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(LocationDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        List<Double> lats = new ArrayList<>();
        while(cursor.moveToNext()){
            double latitude = cursor.getDouble(0);
            lats.add(latitude);
        }
        return lats;
    }*/

    public ArrayList<String> getLocation(){
        String[] columns = {LocationDatabaseHelper.LATITUDE,LocationDatabaseHelper.LONGITUDE};
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(LocationDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<String> location = new ArrayList<>();
        while(cursor.moveToNext()){
            double latitude = cursor.getDouble(0);
            double longitude = cursor.getDouble(1);
            location.add(latitude + " , "+ longitude);
        }
        return location;
    }

    public ArrayList<String> getAddresses(){
        String[] columns = {LocationDatabaseHelper.ADDRESS};
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(LocationDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<String> addresses = new ArrayList<>();
        while(cursor.moveToNext()){
            String address = cursor.getString(0);
            addresses.add(address);
        }
        return addresses;
    }

    public ArrayList<String> getLocality(){
        String[] columns = {LocationDatabaseHelper.ADDRESS};
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(LocationDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<String> locality = new ArrayList<>();
        while(cursor.moveToNext()){
            String address = cursor.getString(0);
            String loc = address.substring(0,address.indexOf(" "));
            locality.add(loc);
        }
        return locality;
    }

    public ArrayList<String> getDate(){
        String[] columns = {LocationDatabaseHelper.DATE};
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(LocationDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<String> myDate = new ArrayList<>();
        while(cursor.moveToNext()){
            String date = cursor.getString(0);
            myDate.add(date);
        }
        return myDate;
    }

   class LocationDatabaseHelper extends SQLiteOpenHelper{

       private static final String DATABASE_NAME="location_data";
       private static final String TABLE_NAME="location_table";
       private static final int DATABASE_VERSION=1;
       private static final String UID = "_id";
       private static final String LATITUDE = "latitude";
       private static final String LONGITUDE = "longitude";
       private static final String ADDRESS = "address";
       private static final String DATE = "date";

       private static final String TAG = "Track me-Helper";

       private String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME
               +" ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
               + LATITUDE +" DOUBLE PRECISION , "
               + LONGITUDE + " DOUBLE PRECISION , "
               + ADDRESS + " VARCHAR(255) ,"
               + DATE+" VARCHAR(255));";

       private String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

       private LocationDatabaseHelper(Context context){
           super(context,DATABASE_NAME,null,DATABASE_VERSION );
       }

       @Override
       public void onCreate(SQLiteDatabase db) {
           try {
               db.execSQL(CREATE_TABLE);
           } catch (SQLException e) {
               Log.e(TAG,e.toString());
           }
       }

       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           try {
               db.execSQL(DROP_TABLE);
               onCreate(db);
           } catch (SQLException e) {
               Log.e(TAG, e.toString());
           }
       }
   }

}
