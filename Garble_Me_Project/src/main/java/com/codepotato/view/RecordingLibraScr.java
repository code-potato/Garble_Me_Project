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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.codepotato.controller.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity for loading/deleting a previously saved audio file
 */
public class RecordingLibraScr extends Activity {
    private static final String LOGTAG = "CodePotatoRecLib";
    private FileManager fileManager = new FileManager();
    private File recordingFiles[];
    private int selectedFileIndex; //will contain index/id of the selected listview element
    private ArrayList<String> recordingsList;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_libra_scr);

        recordingFiles = fileManager.getRawFiles(this.getApplicationContext()); //gets a list of raw Files objects from recording manager
        String[] nameList = fileManager.listRawFiles(this.getApplicationContext()); //gets an array of file names
        recordingsList = new ArrayList<String>(Arrays.asList(nameList)); //must convert it to ArrayList to enable certain ListView funcitonality

        // We get the ListView component from the layout
        final ListView listView = (ListView) findViewById(R.id.listView);  //made final so it can be accessed from the overriden onItemClick mehtod

        // an adapter is(in this case) simply an array wrapper for the ListView that creates a view for each element
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordingsList);
        listView.setAdapter(arrayAdapter);

        //Needs to be called to associate the list view with a Pop Up context Menu
        this.registerForContextMenu(listView);

        // React to ListView click events(selecting a recording) by implementing AdapterView's OnItemClickListener interface
        //which acts as a callback for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                /*TextView clickedView = (TextView) view;
                Toast.makeText(RecordingLibraScr.this, "Keep pressing on Recording [" + clickedView.getText() + "] for more options!", Toast.LENGTH_SHORT).show();
                Log.d(LOGTAG, "Position: " + Integer.toString(position));
                Log.d(LOGTAG, "id: " + Long.toString(id));*/
                openContextMenu(view); //launches the context menu
            }
        });
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


    // Creates a context Menu. Method is called when the user sends a long click event by long clicking on an recording/ListView element
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        // We know that each row in the adapter is a String
        String fileName = (String) arrayAdapter.getItem(aInfo.position);
        selectedFileIndex = aInfo.position;

        menu.setHeaderTitle("Options for " + fileName);
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

                            String selectedFilePath = recordingFiles[selectedFileIndex].toString();
                            Intent intent = new Intent(RecordingLibraScr.this, EffectsConfigScr.class);
                            /* An Intent can carry a payload of various data types as key-value pairs called extras.
                            The putExtra() method takes the key name in the first arg and the value in the second arg
                            */
                            intent.putExtra("AudioFilePath", selectedFilePath);
                            startActivity(intent);
                            finish();
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

                            fileManager.deleteFile(recordingFiles[selectedFileIndex]);  //deletes the file from folder/storage
                            recordingsList.remove(selectedFileIndex);
                            arrayAdapter.notifyDataSetChanged(); //refreshes the ListView


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
