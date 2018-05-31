package nl.norbot.senseswipe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SenseSwipe.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.COLUMN_NAME_CREATEDAT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    DBContract.DBEntry.COLUMN_NAME_SUBJECT + " TEXT," +
                    DBContract.DBEntry.COLUMN_NAME_INPUTMETHOD + " TEXT," +
                    DBContract.DBEntry.COLUMN_NAME_TASK + " TEXT," +
                    DBContract.DBEntry.COLUMN_NAME_SUBTASK + " TEXT," +
                    DBContract.DBEntry.COLUMN_NAME_VALUE + " FLOAT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

        /*

        DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_NAME_SUBJECT, "SubTask");
        values.put(DBContract.DBEntry.COLUMN_NAME_INPUTMETHOD, "InputMethod");
        values.put(DBContract.DBEntry.COLUMN_NAME_TASK, "Task");
        values.put(DBContract.DBEntry.COLUMN_NAME_SUBTASK, "SubTask");
        values.put(DBContract.DBEntry.COLUMN_NAME_VALUE, "Value");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBContract.DBEntry.TABLE_NAME, null, values);

        */