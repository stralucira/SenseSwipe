package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.view.inputmethod.*;
import android.widget.Button;
import android.view.View;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



public class TypingActivity extends AppCompatActivity {

    EditText editText;
    int textLength;
    int cursorOffsetFromEnd = 0;
    long start, end;
    boolean lastRound;

    TextView infoText, correctWordText, correct, usageText;

    boolean useFingerPrintGestures;

    HashMap<String,String> typoList;
    Iterator it;
    int word_count = 0;
    HashMap.Entry currentPair;

    long[] measurements = new long[15];

    private int id;

    private FirebaseDatabase database;
    private DatabaseReference databasereference;
    DatabaseReference wordIndex;

    private AlertDialog.Builder alertbuilder;
    AlertDialog endAlert, practiceAlert;

    private static final String TAG = TypingActivity.class.getSimpleName();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, Integer.toString(intent.getIntExtra("gesture_id", 0)));

                // TODO: Implement input handling here.
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
                int direction = intent.getIntExtra("gesture_id", 0);

                if(direction == 2){
                    cursorOffsetFromEnd++;
                    if(cursorOffsetFromEnd >= textLength){
                        cursorOffsetFromEnd = textLength;
                    }
                    moveCursor();
                }
                if(direction == 1){
                    if(cursorOffsetFromEnd>0)
                        cursorOffsetFromEnd--;
                    moveCursor();
                }

            } catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing);
        Log.d(TAG, "Typing Activity created.");

        id = getIntent().getIntExtra("id", 0);
        useFingerPrintGestures = getIntent().getBooleanExtra("useFingerprint", false);

        database = FirebaseDatabase.getInstance();
        databasereference = database.getReference();

        alertbuilder = new AlertDialog.Builder(this);
        alertbuilder.setMessage("You have finished the typing activity. Press Next to move on to the next activity.");
        alertbuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(getBaseContext(), DDRActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("useFingerprint", useFingerPrintGestures);
                startActivity(intent);
            }
        });
        endAlert = alertbuilder.create();

        if(useFingerPrintGestures) {
            alertbuilder.setMessage("Please practice moving the cursor over the presented word using the fingerprint scanner!");
        } else {
            alertbuilder.setMessage("Please practice moving the cursor over the presented word using the screen!");
        }

        typoList = new HashMap<>();

        typoList.put("mistyped", "misytped");
        typoList.put("experiment", "eperimennt");
        typoList.put("utrecht", "utracht");
        typoList.put("phone", "phine");
        typoList.put("hello", "hhhello");

        it = typoList.entrySet().iterator();

        editText = findViewById(R.id.mistyped_word);
        textLength = editText.getText().length();
        editText.setSelection(textLength, textLength);
        infoText = findViewById(R.id.usage);
        usageText = findViewById(R.id.info);
        correctWordText = findViewById(R.id.correctWord);
        correct = findViewById(R.id.correct);
        correct.setVisibility(View.INVISIBLE);
        infoText.setVisibility(View.VISIBLE);
        usageText.setVisibility(View.VISIBLE);

        alertbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                usageText.setText("If you are done practicing, press Start and");
                if (useFingerPrintGestures) {
                    infoText.setText("start fixing the typos using the SENSOR!");
                } else {
                    infoText.setText("start fixing the typos using the SCREEN!");
                }
            }
        });
        practiceAlert = alertbuilder.create();
        practiceAlert.show();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        // logic used to be here

        startButton();
    }

    private void startButton(){
        final Button startButton = findViewById(R.id.button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Start Button", "start button clicked");
                presentNextWord();
                infoText.setVisibility(View.INVISIBLE);
                usageText.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.INVISIBLE);

                if (useFingerPrintGestures) {
                    infoText.setText("Use the SENSOR to fix the typo!");
                } else {
                    infoText.setText("Use the SCREEN to fix the typo!");
                }

                editText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d(TAG, "Text changed to: " + editText.getText().toString());

                        textLength = editText.getText().length();
                        if (editText.getText().toString().equals(currentPair.getKey())){
                            Log.d(TAG, "Success ");
                            end = System.currentTimeMillis();
                            long time = end - start;
                            Log.d(TAG, "Typo fixed in " + time + " msecs.");
                            measurements[word_count-1] = time;

                            if(word_count < 5) {
                                presentNextWord();
                            } else {
                                dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks();
                                endAlert.show();
                            }
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {

                    }
                });

                //finish();
            }
        });
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

    public void moveCursor(){
        Log.d("MW", "Cursoroffset: " + cursorOffsetFromEnd);
        Log.d("MW", "Cursorposition: " + (textLength - cursorOffsetFromEnd));
        editText.setSelection(textLength - cursorOffsetFromEnd);
    }

    public void dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks(){

        String inputmethod;
        if(useFingerPrintGestures){
            inputmethod = "fingerprint";
        }
        else{
            inputmethod = "screen";
        }

        for(int i = 0 ; i < 5 ; i++) {
            Long longWrap = new Long(measurements[i]);

            wordIndex = databasereference.child(Integer.toString(id)).child(inputmethod).child("Typing").child(Integer.toString(i));

            wordIndex.child("completionTime").setValue(longWrap.toString(measurements[i]));
        }
    }

    protected void presentNextWord() {
        currentPair = (HashMap.Entry) it.next();
        word_count++;
        start = System.currentTimeMillis();
        editText.setText((CharSequence) currentPair.getValue(), TextView.BufferType.EDITABLE);
        textLength = editText.getText().length();
        editText.setSelection(editText.getText().length());
        textLength = editText.getText().length();

        correctWordText.setText((CharSequence) currentPair.getKey());
        correct.setVisibility(View.VISIBLE);
    }
}
