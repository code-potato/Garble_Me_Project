package com.codepotato.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.codepotato.controller.AudioController;

import java.io.*;


public class EffectsConfigScr extends Activity {

    public static final String LOG_TAG = "CodePotatoAudioRecordingTest"; //for debugging purposes
    private AudioController audioController;
    private File audioFile;
    private String filepath;
    private boolean isAudioPlaying = false;
    private int i = 0;

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

    // Create dynamic buttons
    public void addButtonOnClick(View V) {
        TableLayout dynamicLayout = (TableLayout) findViewById(R.id.tableDynamic);
        int buttonsInRow = 0;
        int numRows = dynamicLayout.getChildCount();
        TableRow row = null;
        if (numRows > 0) {
            row = (TableRow) dynamicLayout.getChildAt(numRows - 1);
            buttonsInRow = row.getChildCount();
        }
        if (numRows == 0 || buttonsInRow == 2) {
            row = new TableRow(this);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(150, 5, 150, 0);
            dynamicLayout.addView(row, params);
            row.setGravity(Gravity.CENTER);
            buttonsInRow = 0;
        }
        if (buttonsInRow < 2) {
            Button addButton = new Button(this);
            row.addView(addButton, 500, 100);
            addButton.setText("Effect" + i);
            addButton.setId(i++);
            addButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Add button is clicked! " + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
            Button removeButton = new Button(this);
            row.addView(removeButton, 300, 100);
            removeButton.setText("-" + i);
            removeButton.setId(i++);
            removeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Remove button is clicked! " + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        /*Button addButton = new Button(this);
        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addParams.setMargins(150, 0, 550, 0);
        dynamicLayout.addView(addButton, addParams);
        addButton.setText("Effect" + i);
        addButton.setId(i++);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add button is clicked! " + v.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        Button removeButton = new Button(this);
        LinearLayout.LayoutParams removeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        removeParams.setMargins(550, 0, 150, 0);
        dynamicLayout.addView(removeButton, removeParams);
        removeButton.setText("-" + i);
        removeButton.setId(i++);
        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Remove button is clicked! " + v.getId(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_config_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        /*final Button add = (Button) findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
                intent.putExtra("AUDIOFILEPATH", filepath);//a hash...read bellow
                startActivity(intent);

            }
        });*/
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
