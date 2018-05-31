package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, Integer.toString(intent.getIntExtra("gesture_id", 0)));

                // TODO: Implement input handling here.
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
            } catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    };

    EditText editText;
    Integer subjectNumber;
    Button mazescreen, mazefingerprint, camera, typing, ddr saveButton;
    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        Log.d(TAG, "Main Activity created.");
        Intent fingerprintGestureIntent = new Intent(this, FingerprintGestureService.class);
        startService(fingerprintGestureIntent);
      
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        editText = findViewById(R.id.edittext_subject_number);
        saveButton = findViewById(R.id.button_save_subject_number);
        mazescreen = findViewById(R.id.button_maze_screen);
        mazefingerprint = findViewById(R.id.button_maze_fingerprint);
        camera = findViewById(R.id.button_camera);
        typing = findViewById(R.id.button_typing);
        ddr = findViewById(R.id.button_ddr);

        subjectNumber = prefs.getInt("subjectnr", -1);
        if(subjectNumber > 0){
            mazescreen.setEnabled(true);
            mazefingerprint.setEnabled(true);
            camera.setEnabled(true);
            typing.setEnabled(true);
            ddr.setEnabled(true);
            editText.setText("" + subjectNumber);
        }

        // Example database push code
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }

    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter("FINGERPRINT_GESTURE_DETECTED"));
    }

    protected void onPause() {
        super.onPause();

        Log.d(TAG, "Unregistering receiver.");
        unregisterReceiver(receiver);
    }

    public void startMazeActivityScreen(View view) {
        Intent intent = new Intent(this, MazeActivity.class);
        intent.putExtra("id", subjectNumber);
        intent.putExtra("useFingerprint", false);
        startActivity(intent);
    }

    public void startMazeActivityFingerprint(View view) {
        Intent intent = new Intent(this, MazeActivity.class);
        intent.putExtra("id", subjectNumber);
        intent.putExtra("useFingerprint", true);
        startActivity(intent);
    }

    public void startCameraActivity(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void startTypingActivity(View view) {
        Intent intent = new Intent(this, TypingActivity.class);
        intent.putExtra("id", subjectNumber);
        intent.putExtra("useFingerprint", false);
        startActivity(intent);
    }

    public void startDDRActivity(View view) {
        Intent intent = new Intent(this, DDRActivity.class);
        startActivity(intent);
    }

    public void saveSubjectNumber(View view) {
        subjectNumber = Integer.parseInt(editText.getText().toString());
        prefs.edit().putInt("subjectnr", subjectNumber).commit();
        editText.setInputType(InputType.TYPE_NULL);
        editText.setText("Subject number set to " + subjectNumber);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        mazescreen.setEnabled(true);
        mazefingerprint.setEnabled(true);
        camera.setEnabled(true);
        typing.setEnabled(true);
        ddr.setEnabled(true);
    }

}
