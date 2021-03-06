package com.codepotato.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.codepotato.controller.AudioController;
import com.codepotato.controller.FileManager;

import java.io.*;
import java.util.HashMap;

/**
 * Activity for:
 * adding/removing effects to an audio file;
 * playback of the audio file;
 *
 * @author Terry C. Wong
 */
public class EffectsConfigScr extends Activity {
    private static final String LOGTAG = "CodePotatoEffectsConfigScr";
    public static AudioController audioController;
    private File audioFile;
    private String filepath;
    static final int ADD_EFFECT_REQUEST = 1;  // The add request code
    static final int UPDATE_EFFECT_REQUEST = 2;  // The update request code
    SeekBar audioPlayerBar;
    ToggleButton playToggle;
    private static HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
    private Handler myHandler = new Handler();
    private static int NumOfEffects = 0;
    private static final int MaxNumOfEffects = 10;

    /**
     * This function is called when the Play button is pressed in the view.
     *
     * @param view is passed implicitly by the GUI.
     */
    public void togglePlaying(View view) {
        playToggle = (ToggleButton) view;

        // Is audio playing?
        if (playToggle.isChecked()) {
            startPlayingAudio();
            playToggle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.pause_button)); //changes the buttons background image
        } else {
            stopPlayingAudio();
            playToggle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.play_button)); //changes the buttons background image
        }
    }

    /**
     * Add button click event handler
     *
     * @param V
     */
    public void addButtonOnClick(View V) {
        if (NumOfEffects < MaxNumOfEffects) {
            Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
            startActivityForResult(intent, ADD_EFFECT_REQUEST);
        } else {
            Toast toast = Toast.makeText(EffectsConfigScr.this, "You have reached the maximum number of effects!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * Export button click event handler
     *
     * @param V
     */
    public void exportButtonOnClick(View V) {
        convertProgress();
    }

    /**
     * Function for displaying the conversion progress of "Export"
     */
    public void convertProgress() {
        FileManager asyncFileManager = new FileManager();
        ConvertProgressDialog progressDialog = new ConvertProgressDialog(asyncFileManager); //create an instance of my custom Alert View fragment/Dialog
        FragmentManager fragmentManager = getFragmentManager();
        progressDialog.show(fragmentManager, "Wav Progress"); // show the dialog

        //forces the progressDialog obj to instantiate all its variables, otherwise I get a freakin' nullpointer exception
        fragmentManager.executePendingTransactions();
        asyncFileManager.execute(progressDialog, this.getApplicationContext(), audioFile);

    }

    /**
     * Restart button click event handler
     *
     * @param V
     */
    public void restartButtonOnClick(View V) {
        try {
            audioController.returnPlayerToBeginning();
            audioPlayerBar.setProgress(audioController.currAudioPosition());
        } catch (IOException e) {
            Log.d(InitialScr.LOG_TAG, "Audio player can't be restarted!");
        }
    }

    // Function for constructing the audio player bar
    private void initAudioPlayerBar() {
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
                    Toast.makeText(EffectsConfigScr.this, "Audio Player Progress: " + progressChanged, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Function for operations on results returned from
    // an effect settings
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_EFFECT_REQUEST) {
            if (resultCode == RESULT_OK) {
                createButtons(data);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Log.d(InitialScr.LOG_TAG, "No added effect ID is returned!");
            }
        }
        if (requestCode == UPDATE_EFFECT_REQUEST) {
            if (resultCode == RESULT_OK) {
                final int previousEffectID = Integer.parseInt(data.getStringExtra("PreviousAudioEffectID"));
                Log.d(InitialScr.LOG_TAG, "Previous AudioEffectID: " + data.getStringExtra("PreviousAudioEffectID"));
                final int effectID = Integer.parseInt(data.getStringExtra("AudioEffectID"));
                Log.d(InitialScr.LOG_TAG, "Updated AudioEffectID: " + data.getStringExtra("AudioEffectID"));
                if (effectID != previousEffectID) {
                    audioController.removeEffect(previousEffectID);
                    Log.d(InitialScr.LOG_TAG, "Removed previous AudioEffectID: " + data.getStringExtra("PreviousAudioEffectID"));
                    String effectName = audioController.getEffect(effectID).getName();
                    final Button effectButton = buttons.get(previousEffectID);
                    if (effectButton != null) {
                        effectButton.setId(effectID);
                        effectButton.setText(effectName);
                        buttons.remove(previousEffectID);
                        buttons.put(effectID, effectButton);
                        Log.d(InitialScr.LOG_TAG, "Updated effect button text:" + effectName);
                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Log.d(InitialScr.LOG_TAG, "No updated effect ID is returned!");
            }
        }
    }//onActivityResult

    // Function for creating dynamic buttons
    private void createButtons(Intent intent) {
        final int effectID = Integer.parseInt(intent.getStringExtra("AudioEffectID"));
        Log.d(InitialScr.LOG_TAG, "Added AudioEffectID: " + intent.getStringExtra("AudioEffectID"));
        String effectName = audioController.getEffect(effectID).getName();
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
            buttons.put(effectID, effectButton);
            effectButton.setId(effectID);
            effectButton.setText(effectName);
            effectButton.setGravity(Gravity.CENTER);
            effectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(EffectsConfigScr.this, EffectSettingsScr.class);
                    intent.putExtra("EffectID", String.valueOf(effectButton.getId()));
                    startActivityForResult(intent, UPDATE_EFFECT_REQUEST);
                }
            });
            final Button removeButton = new Button(this);
            row.addView(removeButton);
            removeButton.setId(effectID);
            removeButton.setBackground(this.getResources().getDrawable(R.drawable.remove_button));
            removeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ViewGroup layout = (ViewGroup) removeButton.getParent();
                    layout.removeView(removeButton);
                    layout.removeView(effectButton);
                    buttons.remove(effectButton.getId());
                    audioController.removeEffect(effectButton.getId());
                    NumOfEffects--;
                    Log.d(InitialScr.LOG_TAG, "Removed AudioEffectID: " + effectButton.getId());
                    Log.d(InitialScr.LOG_TAG, "NumOfEffects: " + NumOfEffects);
                }
            });
        }
        NumOfEffects++;
        Log.d(InitialScr.LOG_TAG, "NumOfEffects: " + NumOfEffects);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_config_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Load recorded audio file
        audioFile = new File(getIntent().getStringExtra("AudioFilePath"));
        // Code to use test audio file from assets instead
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

    // Function to convert an InputStream to a File Object
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

    // Start playing the audio file
    private void startPlayingAudio() {
        audioController.play();
        myHandler.postDelayed(updateAudioPlayerBar, 5);
    }

    // Stop playing the audio file
    private void stopPlayingAudio() {
        audioController.pause();
        myHandler.removeCallbacks(updateAudioPlayerBar); //stops the seekBar update
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
                finish();
                return true;
            case R.id.about:
                intent = new Intent(EffectsConfigScr.this, AboutScr.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                try {
                    audioController.pause();
                } catch (Exception e) {
                    Log.d(InitialScr.LOG_TAG, "Audio controller termination causes an exception!");
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
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "Audio controller termination causes an exception!");
        }
        this.finish();
    }

    /**
     * A seekBar thread for updating the AudioPlayerBar.
     */
    private Runnable updateAudioPlayerBar = new Runnable() {

        @Override
        public void run() {
            audioPlayerBar.setProgress(audioController.currAudioPosition());
            //Log.d(InitialScr.LOG_TAG, "Audio controller position:" + audioController.currAudioPosition());
            if (!audioController.isPlaying()) {
                playToggle.setChecked(false);
                playToggle.setBackgroundDrawable(playToggle.getContext().getResources().getDrawable(R.drawable.play_button)); //changes the buttons background image
                return;
            }
            myHandler.postDelayed(this, 5);
        }
    };
}
