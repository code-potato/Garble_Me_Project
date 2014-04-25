package com.codepotato.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.codepotato.model.effects.Effect;

import java.util.HashMap;


public class EffectSettingsScr extends Activity {
    private Spinner spinner;
    private HashMap<String, String> effectsList;
    private Integer effectID = null;

    public void saveEffect() {
        String effectClassName = effectsList.get(spinner.getSelectedItem().toString());
        if (effectID != null) {
            EffectsConfigScr.audioController.removeEffect(effectID);
        }
        try {
            Effect effect = (Effect) (Class.forName("com.codepotato.model.effects." + effectClassName)).newInstance();
            Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " is created!");
            effectID = EffectsConfigScr.audioController.addEffect(effect);
            Intent returnIntent = new Intent();
            if (effectID != null) {
                returnIntent.putExtra("AudioEffectName", spinner.getSelectedItem().toString());
                returnIntent.putExtra("AudioEffectID", effectID.toString());
                setResult(RESULT_OK, returnIntent);
            } else {
                setResult(RESULT_CANCELED, returnIntent);
            }
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " didn't get created!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_settings_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Fragment fragment = new EchoFragment();
        replaceFragment(fragment);
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.effects_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When a drop-down menu item is selected
                String fragmentClassName = spinner.getSelectedItem().toString() + "Fragment";
                try {
                    Fragment fragment = (Fragment) (Class.forName("com.codepotato.view." + fragmentClassName)).newInstance();
                    Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " is created!");
                    replaceFragment(fragment);
                } catch (Exception e) {
                    Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " didn't get created!");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // When nothing is selected
            }

        });
        effectsList = new HashMap<String, String>();
        effectsList.put("Echo", "EchoEffect");
        effectsList.put("Chorus", "ChorusEffect");
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                saveEffect();
            }
        });

    }

    /* Change the parameter sliders while user
     * changes the drop-down menu item.
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.effect_settings_scr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                saveEffect();
                Log.d(InitialScr.LOG_TAG, "effectID: " + effectID);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // do something on back.
        saveEffect();
        Log.d(InitialScr.LOG_TAG, "effectID: " + effectID);
        this.finish();
    }

}
