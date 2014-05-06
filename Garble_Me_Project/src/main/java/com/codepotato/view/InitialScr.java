package com.codepotato.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.codepotato.model.Recorder;

import java.io.File;

/**
 * Main activity that enables recording
 * a new audio file and counting the time elapsed.
 */
public class InitialScr extends Activity {

    public static final String LOG_TAG = "CodePotato"; //for debugging purposes
    private Recorder recorder;
    private File audioFile;
    private TextView textTimer;
    private long startTime = 0L;
    private Handler myHandler = new Handler(); //for sending and retrieving events to a Looper/MessageQueue(every Activity instance has one)
    long elapsedTime = 0L;
    InputMethodManager imm;

    /**
     * This function is called when the Record button is pressed in the main view. This is the insertion point
     *
     * @param view is passed implicitly by the GUI.
     */
    public void toggleRecording(View view) {
        ToggleButton recordToggle = (ToggleButton) view;

        //Start Recording is pressed
        if (recordToggle.isChecked()) {
            recordToggle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.done_button)); //changes the buttons background image
            startRecording();
        }
        //Stop Recording is pressed
        else {
            recordToggle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.record_button));//changes the button background image
            stopRecording();
        }
    }

    /**
     * Function for starting to record
     */
    private void startRecording() {
        File filepath = this.getFilesDir();  //returns us the root of the apps private sandboxed directory
        recorder = new Recorder(filepath);
        recorder.start();
        //Starts the Stopwatch/Timer
        elapsedTime = 0L;
        startTime = SystemClock.uptimeMillis();
        myHandler.postDelayed(updateTimer, 1000); //sends an updateTimer thread to the messageQueue after 1 second
    }

    /**
     * Function for stopping to record
     */
    private void stopRecording() {
        recorder.stop();
        myHandler.removeCallbacks(updateTimer); //removes any threads in the MessageQueue of type updateTimer. This essentially stops the timer.
        promptUserForSaveFileName();//prompts user for File Name via an Alert Dialogue box.
    }

    /**
     * Function for filename prompt
     */
    private void promptUserForSaveFileName() {

        // get activity_filename_prompt.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.activity_filename_prompt, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this); //
        alert.setTitle("Enter File Name:");
        alert.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); // show the soft keyboard
        alert.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    //IF THE USER CLICKED ON SAVE BUTTON
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String filename = String.valueOf(input.getText());
                        if (filename.isEmpty()) {
                            Toast toast = Toast.makeText(InitialScr.this, "You need to enter a file name!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            dialog.dismiss();
                            promptUserForSaveFileName();
                        } else {
                            Log.d(LOG_TAG, "The file name is: " + filename);
                            audioFile = recorder.save(filename);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide the soft keyboard
                            textTimer.setText("00:00");
                            Intent intent = new Intent(InitialScr.this, EffectsConfigScr.class);
                            /* An Intent can carry a payload of various data types as key-value pairs called extras.
                            The putExtra() method takes the key name in the first arg and the value in the second arg
                            */
                            intent.putExtra("AudioFilePath", audioFile.toString());
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // If the User clicked on the cancel button
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide the soft keyboard
                        textTimer.setText("00:00");
                    }
                });
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_scr);
        textTimer = (TextView) findViewById(R.id.stopwatch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.initial_scr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {
            case R.id.recordings:
                intent = new Intent(InitialScr.this, RecordingLibraScr.class);
                startActivity(intent);
                return true;
            case R.id.about:
                intent = new Intent(InitialScr.this, AboutScr.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A stopwatch thread for the audio recording.
     */
    private Runnable updateTimer = new Runnable() {

        @Override
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            String minutesPrefix = "";
            if (minutes < 10)
                minutesPrefix = "0";
            textTimer.setText(minutesPrefix + minutes + ":"
                    + String.format("%02d", seconds)); // output the elapsed time in "00:00" format
            myHandler.postDelayed(this, 1000);  // wait a second, then send this thread instance to the MessageQue to be executed again
        }
    };

}
