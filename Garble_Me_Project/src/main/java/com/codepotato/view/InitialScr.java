package com.codepotato.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class InitialScr extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_scr);
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

                } else {
                    // The toggle is disabled
                    ToggleButton toggle = (ToggleButton) findViewById(R.id.recordButton);
                    toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.record_button));

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
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                    dialog.cancel();
                                }
                            });
                    //alert.create();
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

}
