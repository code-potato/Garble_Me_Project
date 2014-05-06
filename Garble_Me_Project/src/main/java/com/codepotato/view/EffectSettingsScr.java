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
import com.codepotato.model.effects.FlangerEffect;

import java.util.HashMap;

/**
 * Activity for setting parameters for a specific effect
 */
public class EffectSettingsScr extends Activity {
    private Spinner spinner;
    private HashMap<String, String> effectsList;
    private Integer loadedEffectID = null;
    private Integer currentEffectID = null;
    private boolean effectLoaded = false;

    /*
     * Function for saving the selected effect
     * and its parameters.
     */
    public void saveEffect() {
        String effectName = spinner.getSelectedItem().toString();
        String effectClassName = effectsList.get(effectName);
        Effect loadedEffect = null;
        if (loadedEffectID != null)
            loadedEffect = EffectsConfigScr.audioController.getEffect(loadedEffectID);
        if (loadedEffect != null && loadedEffect.getName().equals(effectName)) {
            // Update the existing effect
            updateEffect(loadedEffect);
            Intent returnIntent = new Intent();
            if (loadedEffectID != null) {
                returnIntent.putExtra("PreviousAudioEffectID", loadedEffectID.toString());
                returnIntent.putExtra("AudioEffectID", loadedEffectID.toString());
                setResult(RESULT_OK, returnIntent);
                Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " is updated!");
                Toast toast = Toast.makeText(EffectSettingsScr.this, "The effect is saved!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                setResult(RESULT_CANCELED, returnIntent);
            }
        } else if (loadedEffect == null || (loadedEffect != null && !loadedEffect.getName().equals(effectName))) {
            {
                Intent returnIntent = new Intent();
                if (loadedEffectID != null) {
                    returnIntent.putExtra("PreviousAudioEffectID", loadedEffectID.toString());
                }
                try {
                    Effect effect = (Effect) (Class.forName("com.codepotato.model.effects." + effectClassName)).newInstance();
                    if (currentEffectID != null && EffectsConfigScr.audioController.getEffect(currentEffectID).getName() != effect.getName()) {
                        EffectsConfigScr.audioController.removeEffect(currentEffectID);
                        Log.d(InitialScr.LOG_TAG, "saved: currentEffectID changed!");
                        // Edit the added effect
                        updateEffect(effect);
                        currentEffectID = EffectsConfigScr.audioController.addEffect(effect);
                    } else {
                        updateEffect(EffectsConfigScr.audioController.getEffect(currentEffectID));
                    }
                    if (currentEffectID != null) {
                        returnIntent.putExtra("AudioEffectID", currentEffectID.toString());
                        setResult(RESULT_OK, returnIntent);
                        Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " is added!");
                        Toast toast = Toast.makeText(EffectSettingsScr.this, "The effect is saved!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        setResult(RESULT_CANCELED, returnIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " is not saved!");
                    Toast toast = Toast.makeText(EffectSettingsScr.this, "The effect is not saved!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    /*
     * Function for updating an effect's parameters
     */
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
        } else if (effect instanceof FlangerEffect) {
            FlangerEffect flangerEffect = (FlangerEffect) effect;
            SeekBar rateBar = (SeekBar) findViewById(R.id.rateBar);
            flangerEffect.setRate(rateBar.getProgress());
            SeekBar depthBar = (SeekBar) findViewById(R.id.depthBar);
            flangerEffect.setDepth(depthBar.getProgress());
            SeekBar wetBar = (SeekBar) findViewById(R.id.wetBar);
            flangerEffect.setWetGain(wetBar.getProgress());
            SeekBar dryBar = (SeekBar) findViewById(R.id.dryBar);
            flangerEffect.setDryGain(dryBar.getProgress());
            SeekBar feedbackBar = (SeekBar) findViewById(R.id.feedbackBar);
            flangerEffect.setFeedbackGain(feedbackBar.getProgress());
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
                if (!effectLoaded) {
                    Effect effect = null;
                    String effectClassName = spinner.getSelectedItem().toString() + "Effect";
                    try {
                        effect = (Effect) (Class.forName("com.codepotato.model.effects." + effectClassName)).newInstance();
                        Log.d(InitialScr.LOG_TAG, "com.codepotato.view.effects." + effectClassName + " is created!");
                        if (currentEffectID == null) {
                            currentEffectID = EffectsConfigScr.audioController.addEffect(effect);
                        } else if (currentEffectID != null && !EffectsConfigScr.audioController.getEffect(currentEffectID).getName().equals(effect.getName())) {
                            EffectsConfigScr.audioController.removeEffect(currentEffectID);
                            Log.d(InitialScr.LOG_TAG, "replaced: currentEffectID changed!");
                            currentEffectID = EffectsConfigScr.audioController.addEffect(effect);
                        }
                    } catch (Exception e) {
                        Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " didn't get created!");
                        e.printStackTrace();
                    }
                    replaceFragment(effect);
                } else {
                    effectLoaded = false;
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
        effectsList.put("Flanger", "FlangerEffect");
        // Load the default or previously saved fragment
        Effect effect = null;
        String id = getIntent().getStringExtra("EffectID");
        if (id != null) {
            Log.d(InitialScr.LOG_TAG, "Loaded effectID: " + id);
            loadedEffectID = Integer.parseInt(id);
            effect = EffectsConfigScr.audioController.getEffect(loadedEffectID);
            if (effect instanceof EchoEffect) {
                spinner.setSelection(1); //Echo is at item 1
            } else if (effect instanceof ChorusEffect) {
                spinner.setSelection(2); //Chorus is at item 2
            } else if (effect instanceof FlangerEffect) {
                spinner.setSelection(3); //Flanger is at item 3
            }
            // Initialize the fragment
            replaceFragment(effect);
            currentEffectID = loadedEffectID;
            effectLoaded = true;
        } else {
            String effectClassName = spinner.getSelectedItem().toString() + "Effect";
            try {
                effect = (Effect) (Class.forName("com.codepotato.model.effects." + effectClassName)).newInstance();
                Log.d(InitialScr.LOG_TAG, "com.codepotato.view.effects." + effectClassName + " is created!");
            } catch (Exception e) {
                Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectClassName + " didn't get created!");
                e.printStackTrace();
            }
            // Initialize the fragment
            replaceFragment(effect);
        }
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Save the effect and exit current activity on click
                saveEffect();
                Log.d(InitialScr.LOG_TAG, "effectID: " + currentEffectID);
                finish();
            }
        });
    }

    /* Load the parameters of sliders
     * with previously saved parameters
     * when user presses a specific effect
     * or with default parameters
     * when user changes the drop-down menu item.
     */
    public void replaceFragment(Effect effect) {
        String fragmentClassName = spinner.getSelectedItem().toString() + "Fragment";
        try {
            Fragment fragment = (Fragment) (Class.forName("com.codepotato.view." + fragmentClassName)).newInstance();
            Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " is created!");
            if (effect != null) {
                if (effect instanceof EchoEffect) {
                    EchoFragment echoFragment = (EchoFragment) fragment;
                    echoFragment.setEffect(effect);
                } else if (effect instanceof ChorusEffect) {
                    ChorusFragment chorusFragment = (ChorusFragment) fragment;
                    chorusFragment.setEffect(effect);
                } else if (effect instanceof FlangerEffect) {
                    FlangerFragment flangerFragment = (FlangerFragment) fragment;
                    flangerFragment.setEffect(effect);
                }
            }
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "com.codepotato.view." + fragmentClassName + " didn't get created!");
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
                Log.d(InitialScr.LOG_TAG, "effectID: " + currentEffectID);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the effect on "Back" key press
     */
    @Override
    public void onBackPressed() {
        // do something on back.
        saveEffect();
        Log.d(InitialScr.LOG_TAG, "effectID: " + currentEffectID);
        this.finish();
    }

}
