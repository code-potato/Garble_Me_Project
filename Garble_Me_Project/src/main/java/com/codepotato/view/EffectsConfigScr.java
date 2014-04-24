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

    public static AudioController audioController;
    private File audioFile;
    private String filepath;
    private boolean isAudioPlaying = false;
    static final int PICK_EFFECT_REQUEST = 1;  // The request code

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
        Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
        startActivityForResult(intent, PICK_EFFECT_REQUEST);
        Log.d(InitialScr.LOG_TAG, "Start!");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(InitialScr.LOG_TAG, "Return!");
        if (requestCode == PICK_EFFECT_REQUEST) {
            if (resultCode == RESULT_OK) {
                createButtons(data);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public void createButtons(Intent intent) {
        int effectID = Integer.parseInt(intent.getStringExtra("AudioEffectID"));
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
            params.setMargins(50, 0, 50, 0);
            dynamicLayout.addView(row, params);
            row.setGravity(Gravity.CENTER);
            buttonsInRow = 0;
        }
        if (buttonsInRow < 2) {
            Button effectButton = new Button(this);
            row.addView(effectButton, 300, 100);
            effectButton.setText("Effect" + effectID);
            effectButton.setGravity(Gravity.CENTER);
            effectButton.setId(effectID);
            effectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Effect button" + v.getId() + " is clicked! ", Toast.LENGTH_SHORT).show();
                }
            });
            Button removeButton = new Button(this);
            row.addView(removeButton);
            removeButton.setBackground(this.getResources().getDrawable(R.drawable.remove_button));
            removeButton.setId(effectID);
            removeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Remove button" + v.getId() + " is clicked! ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_config_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        audioFile = new File(getIntent().getStringExtra("AudioFilePath"));
        /*try {
            // Load recorded audio file
            filepath = this.getFilesDir() + "/emma16.raw";
            Log.d("emma16.raw", filepath);
            InputStream stream = getAssets().open("emma16.raw");
            audioFile = new File(filepath);
            InputStreamToFile(stream, audioFile);
        } catch (IOException e) {
            //Logging exception
            Log.d("emma16.raw", "Error loading test file!");
        }*/
        TextView fileNameText = (TextView) findViewById(R.id.filenameText);
        fileNameText.setText("Filename: " + audioFile.getName());
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
                audioController.pause();
                Log.d(InitialScr.LOG_TAG, "Audio controller stopped!");
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
