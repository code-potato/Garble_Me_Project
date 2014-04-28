package com.codepotato.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.codepotato.model.effects.ChorusEffect;
import com.codepotato.model.effects.EchoEffect;
import com.codepotato.model.effects.Effect;

import java.util.HashMap;


public class EffectSettingsScr extends Activity {
    private Spinner spinner;
    private HashMap<String, String> effectsList;
    private Integer effectID = null;
    private boolean effectLoaded = false;

    /*
     * Function for saving the selected effect
     * and its parameters.
     */
    public void saveEffect() {
        String effectName = spinner.getSelectedItem().toString();
        String effectClassName = effectsList.get(effectName);
        if (effectID != null && EffectsConfigScr.audioController.getEffect(effectID).getName().equals(effectName)) {
            Effect effect = EffectsConfigScr.audioController.getEffect(effectID);
            // Update the existing effect
            updateEffect(effect);
            Intent returnIntent = new Intent();
            if (effectID != null) {
                returnIntent.putExtra("AudioEffectID", effectID.toString());
                setResult(RESULT_OK, returnIntent);
                Toast toast = Toast.makeText(EffectSettingsScr.this, "The effect is updated and saved!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                setResult(RESULT_CANCELED, returnIntent);
            }
        } else if (effectID == null || (effectID != null && !EffectsConfigScr.audioController.getEffect(effectID).getClass().toString().equals(effectName))) {
            {
                if (effectID != null)
                    EffectsConfigScr.audioController.removeEffect(effectID);
                try {
                    Effect effect = (Effect) (Class.forName("com.codepotato.model.effects." + effectClassName)).newInstance();
                    Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " is created!");
                    // Edit the added effect
                    updateEffect(effect);
                    effectID = EffectsConfigScr.audioController.addEffect(effect);
                    Intent returnIntent = new Intent();
                    if (effectID != null) {
                        returnIntent.putExtra("AudioEffectID", effectID.toString());
                        setResult(RESULT_OK, returnIntent);
                        Toast toast = Toast.makeText(EffectSettingsScr.this, "The effect is added and saved!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        setResult(RESULT_CANCELED, returnIntent);
                    }
                } catch (Exception e) {
                    Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " didn't get created!");
                    Toast toast = Toast.makeText(EffectSettingsScr.this, "The effect is not saved!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    // Function for updating an effect's parameters
    private void updateEffect(Effect effect) {
        if (effect instanceof EchoEffect) {
            EchoEffect echoEffect = (EchoEffect) effect;
            SeekBar delayBar = (SeekBar) findViewById(R.id.delayBar);
            echoEffect.setDelayTime(delayBar.getProgress());
            SeekBar wetBar = (SeekBar) findViewById(R.id.wetBar);
            echoEffect.setWetGain(wetBar.getProgress());
            SeekBar dryBar = (SeekBar) findViewById(R.id.dryBar);
            echoEffect.setDryGain(dryBar.getProgress());
            SeekBar feedbackBar = (SeekBar) findViewById(R.id.feedbackBar);
            echoEffect.setFeedbackGain(feedbackBar.getProgress());
        } else if (effect instanceof ChorusEffect) {
            ChorusEffect chorusEffect = (ChorusEffect) effect;
            SeekBar rateBar = (SeekBar) findViewById(R.id.rateBar);
            chorusEffect.setRate(rateBar.getProgress());
            SeekBar depthBar = (SeekBar) findViewById(R.id.depthBar);
            chorusEffect.setDepth(depthBar.getProgress());
            SeekBar wetBar = (SeekBar) findViewById(R.id.wetBar);
            chorusEffect.setWetGain(wetBar.getProgress());
            SeekBar dryBar = (SeekBar) findViewById(R.id.dryBar);
            chorusEffect.setDryGain(dryBar.getProgress());
            SeekBar feedbackBar = (SeekBar) findViewById(R.id.feedbackBar);
            chorusEffect.setFeedbackGain(feedbackBar.getProgress());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_settings_scr);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                // Replace fragment when a drop-down menu item is selected
                if (!effectLoaded)
                    replaceFragment();
                else
                    effectLoaded = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // When nothing is selected
            }

        });
        effectsList = new HashMap<String, String>();
        effectsList.put("Echo", "EchoEffect");
        effectsList.put("Chorus", "ChorusEffect");
        // Load the default or previously saved fragment
        Effect effect = null;
        String id = getIntent().getStringExtra("EffectID");
        if (id != null) {
            Log.d(InitialScr.LOG_TAG, "Loaded effectID: " + id);
            effectID = Integer.parseInt(id);
            effect = EffectsConfigScr.audioController.getEffect(effectID);
            if (effect instanceof EchoEffect) {
                spinner.setSelection(1); //Echo is at item 1
            } else if (effect instanceof ChorusEffect) {
                spinner.setSelection(2); //Chorus is at item 2
            }
            Toast toast = Toast.makeText(EffectSettingsScr.this, "The " + EffectsConfigScr.audioController.getEffect(effectID).getName() + " effect is loaded!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            initFragment(effect);
            effectLoaded = true;
        } else {
            replaceFragment();
        }
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                saveEffect();
            }
        });
    }

    /* Load the previous saved parameters while user
    *  loads an effect
    */
    public void initFragment(Effect effect) {
        String fragmentClassName = spinner.getSelectedItem().toString() + "Fragment";
        try {
            Fragment fragment = (Fragment) (Class.forName("com.codepotato.view." + fragmentClassName)).newInstance();
            Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " is created!");
            if (effect != null) {
                if (effect instanceof EchoEffect) {
                    EchoFragment echoFragment = (EchoFragment) fragment;
                    echoFragment.setEffect(effect);
                }
                if (effect instanceof ChorusEffect) {
                    ChorusFragment chorusFragment = (ChorusFragment) fragment;
                    chorusFragment.setEffect(effect);
                }
            }
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " error!");
            e.printStackTrace();
        }
    }

    /* Change the parameter sliders
     * with default parameters while user
     * changes the drop-down menu item.
     */
    public void replaceFragment() {
        String fragmentClassName = spinner.getSelectedItem().toString() + "Fragment";
        try {
            Fragment fragment = (Fragment) (Class.forName("com.codepotato.view." + fragmentClassName)).newInstance();
            Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " is created!");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " error!");
            e.printStackTrace();
        }
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
