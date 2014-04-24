package com.codepotato.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.codepotato.controller.AudioController;

import java.io.*;


public class EffectsConfigScr extends Activity {

    public static final String LOG_TAG = "CodePotatoAudioRecordingTest"; //for debugging purposes
    private AudioController audioController;
    private File audioFile;
    private String filepath;
    private boolean isAudioPlaying = false;

    /**
     * This function is called when the Play button is pressed in the view.
     *
     * @param view is passed implicitly by the GUI.
     */
    public void togglePlaying(View view) {
        ToggleButton playToggle = (ToggleButton) view;

        // Is audio playing?
        if (playToggle.isChecked()) {
            playToggle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.pause_button)); //changes the buttons background image
            startPlayingAudio();
            Toast.makeText(EffectsConfigScr.this, "Start playing audio!", Toast.LENGTH_SHORT).show();
        } else {
            playToggle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.play_button)); //changes the buttons background image
            stopPlayingAudio();
            Toast.makeText(EffectsConfigScr.this, "Stop playing audio!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_config_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Button add = (Button) findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
                intent.putExtra("AUDIOFILEPATH", filepath);//a hash...read bellow
                startActivity(intent);
            }
        });
        // Load recorded audio file
        //audioFile = new File(getIntent().getStringExtra("AUDIOFILEPATH"));
        try {
            filepath = this.getFilesDir() + "/emma16.raw";
            Log.d("emma16.raw", filepath);
            InputStream stream = getAssets().open("emma16.raw");
            audioFile = new File(filepath);
            InputStreamToFile(stream, audioFile);
        } catch (IOException e) {
            //Logging exception
            Log.d("emma16.raw", "Error loading test file!");

        }
        audioController = new AudioController(audioFile);

    }

    private void InputStreamToFile(InputStream is, File file) {
        OutputStream os = null;

        try {
            // write the inputStream to a FileOutputStream
            os = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    // os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startPlayingAudio() {
        audioController.play();
        isAudioPlaying = audioController.isPlaying();
    }

    public void stopPlayingAudio() {
        audioController.pause();
        isAudioPlaying = audioController.isPlaying();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.effects_config_scr, menu);
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
                intent = new Intent(EffectsConfigScr.this, RecordingLibraScr.class);
                startActivity(intent);
                return true;
            case R.id.about:
                intent = new Intent(EffectsConfigScr.this, AboutScr.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
