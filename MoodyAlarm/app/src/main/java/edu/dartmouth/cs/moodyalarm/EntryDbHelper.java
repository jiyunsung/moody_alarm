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


    private String[] allSpotifyColumns = { KEY_ROWID_SPOTIFY, KEY_PLAYLISTID, KEY_IMAGEURL, KEY_IMAGEBITMAP, KEY_TRACKINFO};

    public final static String TABLE_ENTRIES_SPOTIFY = "SpotifyTable";
    public final static String KEY_ROWID_SPOTIFY = "_id";
    public final static String KEY_PLAYLISTID = "mPlaylistId";
    public final static String KEY_IMAGEURL = "mImageUrl";
    public final static String KEY_IMAGEBITMAP = "mImageBitmap";
    public final static String KEY_TRACKINFO = "mTrackInfo";


    private String[] allDayColumns = { KEY_ROWID_DAY, KEY_DAYNAME, KEY_DAYPLAYLIST};


    public final static String TABLE_ENTRIES_DAYS = "DaysTable";
    public final static String KEY_ROWID_DAY = "_id";
    public final static String KEY_DAYNAME = "mDayName";
    public final static String KEY_DAYPLAYLIST = "mDayPlaylist";


    private String[] allWeatherColumns = { KEY_ROWID_WEATHER, KEY_WEATHERNAME, KEY_WEATHERPLAYLIST};

    public final static String TABLE_ENTRIES_WEATHER = "WeatherTable";
    public final static String KEY_ROWID_WEATHER = "_id";
    public final static String KEY_WEATHERNAME = "mWeatherName";
    public final static String KEY_WEATHERPLAYLIST = "mWeatherPlaylist";


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
            + KEY_IMAGEBITMAP
            + " BLOB, "
            + KEY_TRACKINFO
            + " STRING "
            + ");";

    public static final String CREATE_TABLE_ENTRIES_DAY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ENTRIES_DAYS
            + " ("
            + KEY_ROWID_DAY
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_DAYNAME
            + " STRING, "
            + KEY_DAYPLAYLIST
            + " BLOB "
            + ");";

    public static final String CREATE_TABLE_ENTRIES_WEATHER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ENTRIES_WEATHER
            + " ("
            + KEY_ROWID_WEATHER
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_WEATHERNAME
            + " STRING, "
            + KEY_WEATHERPLAYLIST
            + " BLOB "
            + ");";

    // Constructor
    public EntryDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    public void close() { close();}

    public boolean isOpen(){
        return isOpen();
    }

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ENTRIES_ALARM);
        db.execSQL(CREATE_TABLE_ENTRIES_SPOTIFY);
        db.execSQL(CREATE_TABLE_ENTRIES_DAY);
        db.execSQL(CREATE_TABLE_ENTRIES_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AlarmsFragment.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES_ALARM + "'");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES_SPOTIFY + "'");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES_DAYS + "'");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES_WEATHER + "'");
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
    public SpotifyPlaylist insertSpotifyEntry(SpotifyPlaylist entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLISTID, entry.getPlaylistId());
        values.put(KEY_IMAGEURL, entry.getImageUrl());
        values.put(KEY_IMAGEBITMAP, entry.getImageBitmap());

        long insertId = database.insert(TABLE_ENTRIES_SPOTIFY, null, values);
        Cursor cursor = database.query(TABLE_ENTRIES_SPOTIFY,
                allSpotifyColumns,
                KEY_ROWID_SPOTIFY + " = " + insertId,
                null,null, null, null);

        cursor.moveToLast();
        SpotifyPlaylist newEntry = cursorToEntrySpotify(cursor);
        Log.d("insertEntry", "new spotify entry url is "+ newEntry.getImageUrl());
        cursor.close();
        return newEntry;
    }

    // Insert a item given each column value
    public Day insertDayEntry(Day entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_DAYNAME, entry.getName());
        SpotifyPlaylist playlist = entry.getSpotifyPlaylist();
        Gson gson = new Gson();

        values.put(KEY_DAYPLAYLIST, gson.toJson(playlist).getBytes());



        database = getWritableDatabase();
        long insertId = database.insert(TABLE_ENTRIES_DAYS, null, values);
        Cursor cursor = database.query(TABLE_ENTRIES_DAYS,
                allDayColumns,
                KEY_ROWID_DAY + " = " + insertId,
                null,null, null, null);

        cursor.moveToLast();
        Day newEntry = cursorToEntryDay(cursor);
        Log.d("insertEntry", "new entry day is "+ newEntry.getName());
        cursor.close();
        return newEntry;
    }

    // Insert a item given each column value
    public Weather insertWeatherEntry(Weather entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_WEATHERNAME, entry.getName());
        SpotifyPlaylist playlist = entry.getSpotifyPlaylist();
        Gson gson = new Gson();

        values.put(KEY_WEATHERPLAYLIST, gson.toJson(playlist).getBytes());



        database = getWritableDatabase();
        long insertId = database.insert(TABLE_ENTRIES_WEATHER, null, values);
        Cursor cursor = database.query(TABLE_ENTRIES_WEATHER,
                allWeatherColumns,
                KEY_ROWID_WEATHER + " = " + insertId,
                null,null, null, null);

        cursor.moveToLast();
        Weather newEntry = cursorToEntryWeather(cursor);
        Log.d("insertEntry", "new entry weather is "+ newEntry.getName());
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

    public void updateSpotifyEntry(SpotifyPlaylist entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_TRACKINFO, entry.getTrackInfo());
        values.put(KEY_IMAGEBITMAP, entry.getImageBitmap());


        database.update(TABLE_ENTRIES_SPOTIFY, values, "_id="+entry.getId(), null);
    }


    public void updateDayEntry(Day entry) {
        ContentValues values = new ContentValues();
        SpotifyPlaylist playlist = entry.getSpotifyPlaylist();
        Log.d("updateDayEntry", "playlist url is "+ playlist.getImageUrl());
        Gson gson = new Gson();

        values.put(KEY_DAYPLAYLIST, gson.toJson(playlist).getBytes());

        database.update(TABLE_ENTRIES_DAYS, values, "_id="+entry.getId(), null);
    }

    public void updateWeatherEntry(Weather entry) {
        ContentValues values = new ContentValues();
        SpotifyPlaylist playlist = entry.getSpotifyPlaylist();
        Log.d("updateWeatherEntry", "playlist url is "+ playlist.getImageUrl());
        Gson gson = new Gson();

        values.put(KEY_WEATHERPLAYLIST, gson.toJson(playlist).getBytes());

        database.update(TABLE_ENTRIES_WEATHER, values, "_id="+entry.getId(), null);
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
    public SpotifyPlaylist fetchEntryByIndexSpotify(long rowId) {
        Log.d("fetchEntryByIndex", "id is " + rowId);
        Cursor cursor = database.query(TABLE_ENTRIES_SPOTIFY,
                allSpotifyColumns,
                KEY_ROWID_SPOTIFY + " = " + rowId,
                null,null, null, null);
        SpotifyPlaylist e = new SpotifyPlaylist();
        if (cursor.moveToFirst()){
           e = cursorToEntrySpotify(cursor);
        }

        cursor.close();
        return e;
    }

    // Query a specific entry by its index.
    public Day fetchEntryByIndexDay(long rowId) {
        Log.d("fetchEntryByIndex", "id is " + rowId);
        Cursor cursor = database.query(TABLE_ENTRIES_DAYS,
                allDayColumns,
                KEY_ROWID_DAY + " = " + rowId,
                null,null, null, null);
        Day d = new Day();
        if (cursor.moveToFirst()){
            d = cursorToEntryDay(cursor);
        }

        cursor.close();
        return d;
    }


    // Query a specific entry by its index.
    public Weather fetchEntryByIndexWeather(long rowId) {
        Log.d("fetchEntryByIndex", "id is " + rowId);
        Cursor cursor = database.query(TABLE_ENTRIES_WEATHER,
                allWeatherColumns,
                KEY_ROWID_WEATHER + " = " + rowId,
                null,null, null, null);
        Weather w= new Weather();
        if (cursor.moveToFirst()){
            w = cursorToEntryWeather(cursor);
        }

        cursor.close();
        return w;
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
    public ArrayList<SpotifyPlaylist> fetchSpotifyEntries() {
        Log.d("fetchEntries", "in fetch entries in db helper");
        ArrayList<SpotifyPlaylist> entries = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ENTRIES_SPOTIFY, allSpotifyColumns, null,
                null,null, null, null);
        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            SpotifyPlaylist entry = cursorToEntrySpotify(cursor);
            Log.d("fetch Entries", "row id for entry is: " + entry.getId());
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private SpotifyPlaylist cursorToEntrySpotify(Cursor cursor) {

        SpotifyPlaylist entry = new SpotifyPlaylist();

        entry.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        entry.setPlaylistId(cursor.getString(cursor.getColumnIndex("mPlaylistId")));
        entry.setImageUrl(cursor.getString(cursor.getColumnIndex("mImageUrl")));
        entry.setTrackInfo(cursor.getString(cursor.getColumnIndex("mTrackInfo")));
        entry.setImageBitmap(cursor.getBlob(cursor.getColumnIndex("mImageBitmap")));



        return entry;
    }


    // Query the entire table, return all rows
    public ArrayList<Day> fetchDayEntries() {
        Log.d("fetchDayEntries", "in fetch day entries in db helper");
        ArrayList<Day> entries = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ENTRIES_DAYS, allDayColumns, null,
                null,null, null, null);
        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            Day entry = cursorToEntryDay(cursor);
            Log.d("fetch day Entries", "row id for day entry is: " + entry.getId());
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private Day cursorToEntryDay(Cursor cursor) {

        Day entry = new Day();

        entry.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        entry.setName(cursor.getString(cursor.getColumnIndex("mDayName")));


        byte[] blob = cursor.getBlob(cursor.getColumnIndex("mDayPlaylist"));
        if (blob != null) {
            Log.d("cursorToentryDay", "blob not null");
            String json = new String(blob);
            Gson gson = new Gson();
            SpotifyPlaylist playlist = gson.fromJson(json, SpotifyPlaylist.class);
            entry.setSpotifyPlaylist(playlist);
            if(playlist != null)
                Log.d("cursorToentryDay", "playlist not null url is" + playlist.getImageUrl());

        }

        return entry;
    }


    // Query the entire table, return all rows
    public ArrayList<Weather> fetchWeatherEntries() {
        Log.d("fetchWeatherEntries", "in fetch weather entries in db helper");
        ArrayList<Weather> entries = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ENTRIES_WEATHER, allWeatherColumns, null,
                null,null, null, null);
        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            Weather entry = cursorToEntryWeather(cursor);
            Log.d("fetch weather Entries", "row id for weather entry is: " + entry.getId());
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private Weather cursorToEntryWeather(Cursor cursor) {

        Weather entry = new Weather();

        entry.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        entry.setName(cursor.getString(cursor.getColumnIndex("mWeatherName")));


        byte[] blob = cursor.getBlob(cursor.getColumnIndex("mWeatherPlaylist"));
        if (blob != null) {
            Log.d("cursorToentryWeather", "blob not null");
            String json = new String(blob);
            Gson gson = new Gson();
            SpotifyPlaylist playlist = gson.fromJson(json, SpotifyPlaylist.class);
            entry.setSpotifyPlaylist(playlist);
            if(playlist != null)
                Log.d("cursorToentryWeather", "playlist not null url is" + playlist.getImageUrl());

        }

        return entry;
    }


}


