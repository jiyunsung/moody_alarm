package edu.dartmouth.cs.moodyalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

/**
 * Created by jiyunsung on 1/30/18.
 * SQLite helper to communicate with the database
 */

public class EntryDbHelper extends SQLiteOpenHelper {

    // Database fields
    public final static String DATABASE_NAME = "database";
    private static final Integer DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private String[] allAlarmColumns = { KEY_ROWID_ALARM, KEY_ONOFF, KEY_HOUR, KEY_MINUTE, KEY_REPEAT, KEY_DAYSOFWEEK};

    public final static String TABLE_ENTRIES_ALARM = "AlarmsTable";
    public final static String KEY_ROWID_ALARM = "_id";
    public final static String KEY_ONOFF = "mOnOff";
    public final static String KEY_HOUR = "mHour";
    public final static String KEY_MINUTE = "mMinute";
    public final static String KEY_REPEAT = "mRepeat";
    public final static String KEY_DAYSOFWEEK = "mDaysOfWeek";


    private String[] allSpotifyColumns = { KEY_ROWID_SPOTIFY, KEY_PLAYLISTID, KEY_IMAGEURL,KEY_TRACKINFO};

    public final static String TABLE_ENTRIES_SPOTIFY = "SpotifyTable";
    public final static String KEY_ROWID_SPOTIFY = "_id";
    public final static String KEY_PLAYLISTID = "mPlaylistId";
    public final static String KEY_IMAGEURL = "mImageUrl";
    public final static String KEY_TRACKINFO = "mTrackInfo";

    // SQL query to create the table for the first time
    // Data types are defined below
    public static final String CREATE_TABLE_ENTRIES_ALARM = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ENTRIES_ALARM
            + " ("
            + KEY_ROWID_ALARM
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ONOFF
            + " INTEGER NOT NULL, "
            + KEY_HOUR
            + " INTEGER NOT NULL, "
            + KEY_MINUTE
            + " INTEGER NOT NULL, "
            + KEY_REPEAT
            + " INTEGER NOT NULL, "
            + KEY_DAYSOFWEEK
            + " BLOB" + ");";

    public static final String CREATE_TABLE_ENTRIES_SPOTIFY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ENTRIES_SPOTIFY
            + " ("
            + KEY_ROWID_SPOTIFY
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PLAYLISTID
            + " STRING, "
            + KEY_IMAGEURL
            + " STRING, "
            + KEY_TRACKINFO
            + " STRING "
            + ");";

    // Constructor
    public EntryDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    public void close() {close();}

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ENTRIES_ALARM);
        db.execSQL(CREATE_TABLE_ENTRIES_SPOTIFY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AlarmsFragment.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES_ALARM + "'");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES_SPOTIFY + "'");
        onCreate(database);
    }

    // Insert a item given each column value
    public AlarmEntry insertAlarmEntry(AlarmEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_ONOFF, entry.getOnOff());
        values.put(KEY_HOUR, entry.getHour());
        values.put(KEY_MINUTE, entry.getMinute());
        values.put(KEY_REPEAT, entry.getRepeated());

        ArrayList<Boolean> daysOfWeek = entry.getDaysofweek(); // convert into byte array format
        Gson gson = new Gson();
        values.put(KEY_DAYSOFWEEK, gson.toJson(daysOfWeek).getBytes());

        database = getWritableDatabase();
        long insertId = database.insert(TABLE_ENTRIES_ALARM, null, values);
        Cursor cursor = database.query(TABLE_ENTRIES_ALARM,
                allAlarmColumns,
                KEY_ROWID_ALARM + " = " + insertId,
                null,null, null, null);

        cursor.moveToLast();
        AlarmEntry newEntry = cursorToEntryAlarm(cursor);
        cursor.close();
        return newEntry;
    }

    // Insert a item given each column value
    public SpotifyEntry insertSpotifyEntry(SpotifyEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLISTID, entry.getPlaylistId());
        values.put(KEY_IMAGEURL, entry.getImageUrl());


        database = getWritableDatabase();
        long insertId = database.insert(TABLE_ENTRIES_SPOTIFY, null, values);
        Cursor cursor = database.query(TABLE_ENTRIES_SPOTIFY,
                allSpotifyColumns,
                KEY_ROWID_SPOTIFY + " = " + insertId,
                null,null, null, null);

        cursor.moveToLast();
        SpotifyEntry newEntry = cursorToEntrySpotify(cursor);
        Log.d("insertEntry", "new entry url is "+ newEntry.getImageUrl());
        cursor.close();
        return newEntry;
    }

    public void updateAlarmEntry(AlarmEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_ONOFF, entry.getOnOff());
        values.put(KEY_HOUR, entry.getHour());
        values.put(KEY_MINUTE, entry.getMinute());
        values.put(KEY_REPEAT, entry.getRepeated());

        ArrayList<Boolean> daysOfWeek = entry.getDaysofweek(); // convert into byte array format
        Gson gson = new Gson();
        values.put(KEY_DAYSOFWEEK, gson.toJson(daysOfWeek).getBytes());

        database.update(TABLE_ENTRIES_ALARM, values, "_id="+entry.getId(), null);
    }

    public void updateSpotifyEntry(SpotifyEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_TRACKINFO, entry.getTrackInfo());

        database.update(TABLE_ENTRIES_SPOTIFY, values, "_id="+entry.getId(), null);
    }


    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        database.delete(TABLE_ENTRIES_ALARM, KEY_ROWID_ALARM	+ " = " + rowIndex, null);
    }

    // Query a specific entry by its index.
    public AlarmEntry fetchEntryByIndexAlarm(long rowId) {
        Cursor cursor = database.query(TABLE_ENTRIES_ALARM,
                allAlarmColumns,
                KEY_ROWID_ALARM + " = " + rowId,
                null,null, null, null);
        AlarmEntry entry = cursorToEntryAlarm(cursor);
        cursor.close();
        return entry;
    }

    // Query a specific entry by its index.
    public SpotifyEntry fetchEntryByIndexSpotify(long rowId) {
        Log.d("fetchEntryByIndex", "id is " + rowId);
        Cursor cursor = database.query(TABLE_ENTRIES_SPOTIFY,
                allSpotifyColumns,
                KEY_ROWID_SPOTIFY + " = " + rowId,
                null,null, null, null);
        SpotifyEntry e = new SpotifyEntry();
        if (cursor.moveToFirst()){
           e = cursorToEntrySpotify(cursor);
        }

        cursor.close();
        return e;
    }

    // Query the entire table, return all rows
    public ArrayList<AlarmEntry> fetchAlarmEntries() {

        ArrayList<AlarmEntry> entries = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ENTRIES_ALARM, allAlarmColumns, null,
                null,null, null, null);
        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            AlarmEntry entry = cursorToEntryAlarm(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private AlarmEntry cursorToEntryAlarm(Cursor cursor) {

        AlarmEntry entry = new AlarmEntry();
        entry.setId(cursor.getLong(0));//Returns the value of the requested column as a long.
        entry.setOnOff(cursor.getInt(1));
        entry.setHour(cursor.getInt(2));
        entry.setMinute(cursor.getInt(3));
        entry.setRepeat(cursor.getInt(4));


        // get location list
        byte[] blob = cursor.getBlob(5);
        if (blob != null) {
            String json = new String(blob);
            Gson gson = new Gson();
            ArrayList<Boolean> daysOfWeek = gson.fromJson(json.trim(), new TypeToken<ArrayList<Boolean>>() {}.getType());
            entry.setDaysofweek(daysOfWeek);

        }
        return entry;
    }


    // Query the entire table, return all rows
    public ArrayList<SpotifyEntry> fetchSpotifyEntries() {
        Log.d("fetchEntries", "in fetch entries in db helper");
        ArrayList<SpotifyEntry> entries = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ENTRIES_SPOTIFY, allSpotifyColumns, null,
                null,null, null, null);
        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            SpotifyEntry entry = cursorToEntrySpotify(cursor);
            Log.d("fetch Entries", "row id for entry is: " + entry.getId());
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private SpotifyEntry cursorToEntrySpotify(Cursor cursor) {

        SpotifyEntry entry = new SpotifyEntry();

        if (cursor != null) {
            Log.d("cursorToentrySpotify", "cursor not null");
            Log.d("cursorToentrySpotify", "id column index is " + cursor.getColumnIndex("_id"));
            //Log.d("cursorToentrySpotify", "long id is  " + cursor.getLong(1));
        } else{
            Log.d("cursorToentrySpotify", "cursor is null");
        }
        entry.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        entry.setPlaylistId(cursor.getString(cursor.getColumnIndex("mPlaylistId")));
        entry.setImageUrl(cursor.getString(cursor.getColumnIndex("mImageUrl")));


        return entry;
    }

}


