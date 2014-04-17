package com.codepotato.controller;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import com.codepotato.audio_recording.Recorder;

import java.io.File;

public class RecordMain extends Activity {

    public static final String LOG_TAG= "AudioRecordingTest"; //for debugging purposes
    private Recorder recorder;


    private boolean is_recording_flag= false; //mainly to change the button state from start recording -> stop recording

    /**
     * This function is called upon a button press in the main view.
     * @param view is passed implicitly by the GUI.
     */
    public void toggleRecording(View view){
        Button recordButton= (Button) view;
        String button_text;
        is_recording_flag= !is_recording_flag; //inverting the flags bool value

        //Start Recording is pressed
        if(is_recording_flag){
            button_text = this.getString(R.string.stop_recording); //gets the string associated with stop_recording in strings.xml file
            recordButton.setText(button_text);
            startRecording();

        }
        //Stop Recording is pressed
        else{
            button_text = this.getString(R.string.start_recording);
            recordButton.setText(button_text);
            stopRecording();

        }


        //context.getString(R.string.some_text);
    }

    public void startRecording(){

        File filepath= this.getExternalFilesDir(null);  //passing null brings us the root of the apps private sandboxed directory
        Log.d(LOG_TAG, filepath.toString());
        recorder= new Recorder(filepath);
        recorder.start();



    }

    public void stopRecording(){
        recorder.stop();
        //convert to wave and rename calls should go here
        File recordedRawFile= new File(this.getExternalFilesDir(null), "recorded_audio_file.raw");
        Log.d(LOG_TAG, recordedRawFile.toString());
        goToPlaySoundView(recordedRawFile.toString());
    }

    /** switches to a different view/activity after recording has finished     */
    private void goToPlaySoundView(String filepath) {

        //In order to switch Activity/view, you must use an Intent
        Intent intent = new Intent(this, PlaySound.class);//this is the current context, PlaySound.class is the activity we want to switch to

        intent.putExtra("FILEPATH", filepath);//a hash...read bellow
        /* An Intent can carry a payload of various data types as key-value pairs called extras.
        The putExtra() method takes the key name in the first arg and the value in the second arg
         */
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_record_main, container, false);
            return rootView;
        }
    }

}
