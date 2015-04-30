package com.haab.trackme.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.haab.trackme.data.LocationContract;

/**
 * Created by abara on 3/28/2015.
 */
public class LocationDatabaseHelper extends SQLiteOpenHelper{

       private static final String DATABASE_NAME="location_data";
       private static final int DATABASE_VERSION=1;

       private static final String TAG = "Track me-Helper";

       private String CREATE_TABLE = "CREATE TABLE "+ LocationContract.TABLE_NAME
               +" ("+ LocationContract.UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
               + LocationContract.COLUMN_LATITUDE +" DOUBLE PRECISION , "
               + LocationContract.COLUMN_LONGITUDE + " DOUBLE PRECISION , "
               + LocationContract.COLUMN_ADDRESS + " VARCHAR(255) ,"
               + LocationContract.COLUMN_DATE+" VARCHAR(255));";

       private String DROP_TABLE = "DROP TABLE IF EXISTS "+ LocationContract.TABLE_NAME;

       public LocationDatabaseHelper(Context context){
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


