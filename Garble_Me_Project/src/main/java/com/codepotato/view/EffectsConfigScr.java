package com.codepotato.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.codepotato.controller.AudioController;

import java.io.*;


public class EffectsConfigScr extends Activity {

    public static AudioController audioController;
    private File audioFile;
    private String filepath;
    private boolean isAudioPlaying = false;
    static final int ADD_EFFECT_REQUEST = 1;  // The request code
    SeekBar audioPlayerBar;

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

    public void addButtonOnClick(View V) {
        Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
        startActivityForResult(intent, ADD_EFFECT_REQUEST);
    }

    public void restartButtonOnClick(View V) {
        try {
            audioController.returnPlayerToBeginning();
        } catch (IOException e) {
            Log.d(InitialScr.LOG_TAG, "Audio controller can't be restarted!");
        }
    }

    public void initAudioPlayerBar() {
        // SeekBar for the audio player
        audioPlayerBar = (SeekBar) findViewById(R.id.audioPlayerBar);
        audioPlayerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    audioController.seekPlayer(progressChanged);
                    Toast.makeText(EffectsConfigScr.this, "Audio Player Bar Progress: " + progressChanged,
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_EFFECT_REQUEST) {
            if (resultCode == RESULT_OK) {
                createButtons(data);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Log.d(InitialScr.LOG_TAG, "No effect ID is returned!");
            }
        }
    }//onActivityResult

    // Create dynamic buttons
    public void createButtons(Intent intent) {
        final int effectID = Integer.parseInt(intent.getStringExtra("AudioEffectID"));
        String effectName = audioController.getEffect(effectID).getName();
        Log.d("AudioEffectID:", intent.getStringExtra("AudioEffectID"));
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
            final Button effectButton = new Button(this);
            row.addView(effectButton, 300, 100);
            effectButton.setText(effectName);
            effectButton.setGravity(Gravity.CENTER);
            effectButton.setId(effectID);
            effectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Effect button is pressed! ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
                    intent.putExtra("EffectID", String.valueOf(effectButton.getId()));
                    startActivity(intent);
                }
            });
            final Button removeButton = new Button(this);
            row.addView(removeButton);
            removeButton.setBackground(this.getResources().getDrawable(R.drawable.remove_button));
            removeButton.setId(effectID);
            removeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ViewGroup layout = (ViewGroup) removeButton.getParent();
                    layout.removeView(removeButton);
                    layout.removeView(effectButton);
                    audioController.removeEffect(effectID);
                    Toast toast = Toast.makeText(getApplicationContext(), "Effect is removed! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_config_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Load recorded audio file
        audioFile = new File(getIntent().getStringExtra("AudioFilePath"));
        // Use test audio file from assets instead
        /*try {
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
        initAudioPlayerBar();
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
                try {
                    audioController.pause();
                    Log.d(InitialScr.LOG_TAG, "Audio controller is stopped!");
                } catch (Exception e) {
                    Log.d(InitialScr.LOG_TAG, "Audio controller throws an exception!");
                }
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // do something on back.
        try {
            audioController.pause();
            Log.d(InitialScr.LOG_TAG, "Audio controller is stopped!");
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "Audio controller throws an exception!");
        }
        this.finish();
    }
}
