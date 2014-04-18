package com.codepotato.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordingLibraScr extends Activity {
    // The list of recordings
    List<Map<String, String>> recordingsList = new ArrayList<Map<String, String>>();
    // This is the Adapter being used to display the list's data
    SimpleAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_libra_scr);

        initList();
        // We get the ListView component from the layout
        ListView lv = (ListView) findViewById(R.id.listView);
        // React to user clicks on a recording
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                TextView clickedView = (TextView) view;
                Toast.makeText(RecordingLibraScr.this, "Keep pressing on Recording [" + clickedView.getText() + "] for more options!", Toast.LENGTH_SHORT).show();
            }
        });
        // This is a simple adapter
        Adapter = new SimpleAdapter(this, recordingsList, android.R.layout.simple_list_item_1, new String[]{"recording"}, new int[]{android.R.id.text1});
        lv.setAdapter(Adapter);

        // Register for the ContextMenu
        registerForContextMenu(lv);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recording_libra_scr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(RecordingLibraScr.this, AboutScr.class);
                startActivity(intent);
                finish();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initList() {
        // We populate the recordings
        recordingsList.add(loadRecording("recording", "sample1"));
        recordingsList.add(loadRecording("recording", "sample2"));
        recordingsList.add(loadRecording("recording", "sample3"));
        recordingsList.add(loadRecording("recording", "sample4"));
        recordingsList.add(loadRecording("recording", "sample5"));
        recordingsList.add(loadRecording("recording", "sample6"));
        recordingsList.add(loadRecording("recording", "sample7"));

        recordingsList.add(loadRecording("recording", "sample1"));
        recordingsList.add(loadRecording("recording", "sample2"));
        recordingsList.add(loadRecording("recording", "sample3"));
        recordingsList.add(loadRecording("recording", "sample4"));
        recordingsList.add(loadRecording("recording", "sample5"));
        recordingsList.add(loadRecording("recording", "sample6"));
        recordingsList.add(loadRecording("recording", "sample7"));

        recordingsList.add(loadRecording("recording", "sample1"));
        recordingsList.add(loadRecording("recording", "sample2"));
        recordingsList.add(loadRecording("recording", "sample3"));
        recordingsList.add(loadRecording("recording", "sample4"));
        recordingsList.add(loadRecording("recording", "sample5"));
        recordingsList.add(loadRecording("recording", "sample6"));
        recordingsList.add(loadRecording("recording", "sample7"));

    }

    private HashMap<String, String> loadRecording(String key, String name) {
        HashMap<String, String> recordings = new HashMap<String, String>();
        recordings.put(key, name);

        return recordings;
    }

    // We want to create a context Menu when the user long click on an recording
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        // We know that each row in the adapter is a Map
        HashMap map = (HashMap) Adapter.getItem(aInfo.position);
        menu.setHeaderTitle("Options for " + map.get("recording"));
        menu.add(1, 1, 1, "Select");
        menu.add(1, 2, 2, "Delete");
    }

    // This method is called when user selects an Item in the Context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // Item is "Select"
        if (itemId == 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(RecordingLibraScr.this);
            alert.setTitle("Are you sure you want to load this recording?");
            alert.setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    });
            alert.show();
        }
        // Item is "Delete"
        else if (itemId == 2) {
            AlertDialog.Builder alert = new AlertDialog.Builder(RecordingLibraScr.this);
            alert.setTitle("Are you sure you want to delete this recording?");
            alert.setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            dialog.cancel();
                        }
                    });
            alert.show();
        }

        return true;
    }
}
