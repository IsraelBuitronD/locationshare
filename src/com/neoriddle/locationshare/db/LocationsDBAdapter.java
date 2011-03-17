package com.neoriddle.locationshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class LocationsDBAdapter {

    public static final String KEY_ROWID = "id";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ACCURACY = "accuracy";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";

    private static final String DEBUG_TAG = "LocationsDBAdapter";

    private static final String DATABASE_TABLE = "locations";

    private SQLiteDatabase db;
    private final DatabaseHelper helper;

    /**
     * @param context
     */
    public LocationsDBAdapter(Context context) {
        helper = new DatabaseHelper(context);
    }

    /**
     * Open connection to database.
     * @return
     * @throws SQLException If the database cannot be opened.
     */
    public LocationsDBAdapter open() throws SQLException {
        return open(false);
    }

    /**
     * Open connection to database.
     * @param readonly Specify to open as readonly connection.
     * @return
     * @throws SQLException If the database cannot be opened.
     */
    public LocationsDBAdapter open(boolean readonly)
        throws SQLException {

        if(readonly) {
            Log.d(DEBUG_TAG, "Opening connection as read mode");
            db = helper.getReadableDatabase();
        } else {
            Log.d(DEBUG_TAG, "Opening connection as read/write mode");
            db = helper.getWritableDatabase();
        }
        return this;
    }

    /**
     * Close connection to database.
     */
    public void close() {
        Log.d(DEBUG_TAG, "Closing connection");
        helper.close();
    }

    public long insertLocation(Location location) {
        final ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, location.getLatitude());
        values.put(KEY_LONGITUDE, location.getLongitude());
        values.put(KEY_ACCURACY, location.getAccuracy());
        values.put(KEY_SPEED, location.getSpeed());
        values.put(KEY_ALTITUDE, location.getAltitude());
        values.put(KEY_PROVIDER, location.getProvider());
        values.put(KEY_CREATED_AT, location.getTime());
        Log.d(DEBUG_TAG, "Inserting new location [" +
                "latitude=" + location.getLatitude() + "," +
                "longitude=" + location.getLongitude() + "," +
                "accuracy=" + location.getAccuracy()+ "," +
                "speed=" + location.getSpeed() + "," +
                "altitude=" + location.getAltitude() + "," +
                "provider=" + location.getProvider() + "," +
                "timestamp=" + location.getTime() +
                "]");

        return db.insert(DATABASE_TABLE, null, values);
    }

    /**
     * Delete a specific location.
     * @param rowId
     * @return
     */
    public boolean deleteLocation(long rowId) {
        Log.d(DEBUG_TAG, "Deleting location with id="+rowId);
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DEBUG_TAG = "DatabaseHelper";

        private static final String DATABASE_NAME = "locations";
        private static final int DATABASE_VERSION = 2;

        private static final String DATABASE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_LATITUDE + " TEXT NOT NULL, " +
            KEY_LONGITUDE + " TEXT NOT NULL, " +
            KEY_ACCURACY + " NUMERIC NULL, " +
            KEY_SPEED + " NUMERIC NOT NULL, " +
            KEY_ALTITUDE + " TEXT NULL, " +
            KEY_PROVIDER + " TEXT NOT NULL, " +
            KEY_CREATED_AT + " TEXT NOT NULL, " +
            KEY_UPDATED_AT + " TEXT NULL" +
            ");";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(DEBUG_TAG, "Creating table " + DATABASE_TABLE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DEBUG_TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

}
