package com.codepotato.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InitialScr extends Activity {
    private TextView textTimer;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long elapsedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_scr);
        textTimer = (TextView) findViewById(R.id.stopwatch);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.recordButton);
        toggle.setText(null);
        toggle.setTextOn(null);
        toggle.setTextOff(null);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ToggleButton toggle = (ToggleButton) findViewById(R.id.recordButton);
                    toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.done_button));
                    elapsedTime = 0L;
                    startTime = SystemClock.uptimeMillis();
                    myHandler.postDelayed(updateTimer, 1000);
                } else {
                    // The toggle is disabled
                    ToggleButton toggle = (ToggleButton) findViewById(R.id.recordButton);
                    toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.record_button));
                    myHandler.removeCallbacks(updateTimer);
                    // get activity_initial_scr_prompt.xml view
                    LayoutInflater layoutInflater = LayoutInflater.from(InitialScr.this);
                    View promptView = layoutInflater.inflate(R.layout.activity_initial_scr_prompt, null);

                    AlertDialog.Builder alert = new AlertDialog.Builder(InitialScr.this);
                    alert.setTitle("Enter File Name:");
                    alert.setView(promptView);
                    final EditText input = (EditText) promptView.findViewById(R.id.userInput);
                    alert.setCancelable(false)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Editable value = input.getText();
                                    // Do something with value!
                                    Intent intent = new Intent(InitialScr.this, EffectsConfigScr.class);
                                    startActivity(intent);
                                    textTimer.setText("00:00");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                    dialog.cancel();
                                    textTimer.setText("00:00");
                                }
                            });
                    alert.show();
                }
            }
        });

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

    // A stopwatch thread for the audio recording.
    private Runnable updateTimer = new Runnable() {

        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            String minutesPrefix = "";
            if (minutes < 10)
                minutesPrefix = "0";
            textTimer.setText(minutesPrefix + minutes + ":"
                    + String.format("%02d", seconds));
            myHandler.postDelayed(this, 1000);
        }
    };

}
