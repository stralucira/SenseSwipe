package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    Integer subjectNumber;
    Button mazescreen, mazefingerprint, camera, typing, ddr, saveButton;
    SharedPreferences prefs;
    TextView nrHint;

    private AlertDialog.Builder alertbuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        Log.d(TAG, "Main Activity created.");
        Intent fingerprintGestureIntent = new Intent(this, FingerprintGestureService.class);
        startService(fingerprintGestureIntent);
      
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        saveButton = findViewById(R.id.button_save_subject_number);
        mazescreen = findViewById(R.id.button_maze_screen);
        mazefingerprint = findViewById(R.id.button_maze_fingerprint);
        //camera = findViewById(R.id.button_camera);
        //typing = findViewById(R.id.button_typing);
        //ddr = findViewById(R.id.button_ddr);

        nrHint = findViewById(R.id.nrHint);

        subjectNumber = prefs.getInt("subjectnr", -1);
        if(subjectNumber > 0){
            mazescreen.setEnabled(true);
            mazefingerprint.setEnabled(true);
            //camera.setEnabled(true);
            //typing.setEnabled(true);
            //ddr.setEnabled(true);
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

    public void startDDRScrollActivity(View view) {
        Intent intent = new Intent(this, DDR_scrollingActivity.class);
        intent.putExtra("id", subjectNumber);
        intent.putExtra("useFingerprint", false);
        startActivity(intent);
    }

    public void startDDRActivity(View view) {
        Intent intent = new Intent(this, DDRActivity.class);
        startActivity(intent);
    }

    public void saveSubjectNumber(View view) {
        alertbuilder = new AlertDialog.Builder(this);
        alertbuilder.setMessage("Are you sure?");
        alertbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                FirebaseDatabase database;
                final DatabaseReference databasereference;

                database = FirebaseDatabase.getInstance();
                databasereference = database.getReference();

                databasereference.child("latestID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                        Log.d("MW", "latest id: " + snapshot.getValue());



                        subjectNumber = Integer.parseInt(snapshot.getValue().toString()) + 1;

                        databasereference.child("latestID").setValue(subjectNumber);

                        prefs.edit().putInt("subjectnr", subjectNumber).commit();
                        //editText.setInputType(InputType.TYPE_NULL);
                        nrHint.setText("Subject number: " + subjectNumber);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        mazescreen.setEnabled(true);
                        mazefingerprint.setEnabled(true);
                        //camera.setEnabled(true);
                        //typing.setEnabled(true);
                        //ddr.setEnabled(true);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

        AlertDialog alert11 = alertbuilder.create();
        alert11.show();





    }

}
