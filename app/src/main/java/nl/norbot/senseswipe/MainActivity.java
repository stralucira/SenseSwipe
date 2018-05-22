package nl.norbot.senseswipe;

import android.content.Intent;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_NAME_TITLE, "Title");
        values.put(DBContract.DBEntry.COLUMN_NAME_SUBTITLE, "Subtitle");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBContract.DBEntry.TABLE_NAME, null, values);

    }

    public void startMazeActivity(View view) {
        Intent intent = new Intent(this, MazeActivity.class);
        startActivity(intent);
    }

    public void startCameraActivity(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void startTypingActivity(View view) {
        Intent intent = new Intent(this, TypingActivity.class);
        startActivity(intent);
    }
}
