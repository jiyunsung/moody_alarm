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

public class AlarmEntryDbHelper extends SQLiteOpenHelper {

    // Database fields
    public final static String DATABASE_NAME = "database";
    private static final Integer DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private String[] allColumns = { KEY_ROWID, KEY_ONOFF, KEY_HOUR, KEY_MINUTE, KEY_REPEAT, KEY_DAYSOFWEEK};

    public final static String TABLE_ENTRIES = "AlarmsTable";
    public final static String KEY_ROWID = "_id";
    public final static String KEY_ONOFF = "mOnOff";
    public final static String KEY_HOUR = "mHour";
    public final static String KEY_MINUTE = "mMinute";
    public final static String KEY_REPEAT = "mRepeat";
    public final static String KEY_DAYSOFWEEK = "mDaysOfWeek";

    // SQL query to create the table for the first time
    // Data types are defined below
    public static final String CREATE_TABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ENTRIES
            + " ("
            + KEY_ROWID
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


    // Constructor
    public AlarmEntryDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    public void close() {close();}

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AlarmsFragment.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS '" + TABLE_ENTRIES + "'");
        onCreate(database);
    }

    // Insert a item given each column value
    public AlarmEntry insertEntry(AlarmEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_ONOFF, entry.getOnOff());
        values.put(KEY_HOUR, entry.getHour());
        values.put(KEY_MINUTE, entry.getMinute());
        values.put(KEY_REPEAT, entry.getRepeated());

        ArrayList<Boolean> daysOfWeek = entry.getDaysofweek(); // convert into byte array format
        Gson gson = new Gson();
        values.put(KEY_DAYSOFWEEK, gson.toJson(daysOfWeek).getBytes());

        database = getWritableDatabase();
        long insertId = database.insert(TABLE_ENTRIES, null, values);
        Cursor cursor = database.query(TABLE_ENTRIES,
                allColumns,
                KEY_ROWID + " = " + insertId,
                null,null, null, null);

        cursor.moveToLast();
        AlarmEntry newEntry = cursorToEntry(cursor);
        cursor.close();
        return newEntry;
    }

    public void updateEntry(AlarmEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_ONOFF, entry.getOnOff());
        values.put(KEY_HOUR, entry.getHour());
        values.put(KEY_MINUTE, entry.getMinute());
        values.put(KEY_REPEAT, entry.getRepeated());

        ArrayList<Boolean> daysOfWeek = entry.getDaysofweek(); // convert into byte array format
        Gson gson = new Gson();
        values.put(KEY_DAYSOFWEEK, gson.toJson(daysOfWeek).getBytes());

        database.update(TABLE_ENTRIES, values, "_id="+entry.getId(), null);
    }

    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        database.delete(TABLE_ENTRIES, KEY_ROWID	+ " = " + rowIndex, null);
    }

    // Query a specific entry by its index.
    public AlarmEntry fetchEntryByIndex(long rowId) {
        Cursor cursor = database.query(TABLE_ENTRIES,
                allColumns,
                KEY_ROWID + " = " + rowId,
                null,null, null, null);
        AlarmEntry entry = cursorToEntry(cursor);
        cursor.close();
        return entry;
    }

    // Query the entire table, return all rows
    public ArrayList<AlarmEntry> fetchEntries() {

        ArrayList<AlarmEntry> entries = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ENTRIES, allColumns, null,
                null,null, null, null);
        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            AlarmEntry entry = cursorToEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private AlarmEntry cursorToEntry(Cursor cursor) {

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
}