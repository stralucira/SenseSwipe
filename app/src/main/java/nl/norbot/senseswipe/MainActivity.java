package nl.norbot.senseswipe;

import android.content.Context;
import android.content.Intent;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Integer subjectNumber;
    Button maze, camera, typing, saveButton;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*

        DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_NAME_TASK, "Taak");
        values.put(DBContract.DBEntry.COLUMN_NAME_VALUE, "Waarde");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBContract.DBEntry.TABLE_NAME, null, values);

        */

        editText = findViewById(R.id.edittext_subject_number);
        saveButton = findViewById(R.id.button_save_subject_number);
        maze = findViewById(R.id.button_maze);
        camera = findViewById(R.id.button_camera);
        typing = findViewById(R.id.button_typing);

        subjectNumber = prefs.getInt("subjectnr", -1);
        if(subjectNumber > 0){
            maze.setEnabled(true);
            camera.setEnabled(true);
            typing.setEnabled(true);
            editText.setText("" + subjectNumber);
        }

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

    public void saveSubjectNumber(View view) {
        subjectNumber = Integer.parseInt(editText.getText().toString());
        prefs.edit().putInt("subjectnr", subjectNumber).commit();
        editText.setInputType(InputType.TYPE_NULL);
        editText.setText("Subject number set to " + subjectNumber);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        maze.setEnabled(true);
        camera.setEnabled(true);
        typing.setEnabled(true);
    }

}
