package com.codepotato.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ToggleButton;
import android.view.View;
import android.view.View.OnClickListener;

public class InitialScr extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_scr);
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        ToggleButton Record = (ToggleButton) findViewById(R.id.recordButton);
        boolean on = Record.isChecked();

        if (on) {
            Record.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(InitialScr.this, EffectsConfigScr.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {

        }
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
