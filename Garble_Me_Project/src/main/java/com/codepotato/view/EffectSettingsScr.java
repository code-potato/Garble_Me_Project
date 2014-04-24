package com.codepotato.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.codepotato.model.effects.Effect;

import java.util.EnumMap;


public class EffectSettingsScr extends Activity {
    private Spinner spinner;
    private EnumMap<Effects, String> effectsList;

    public enum Effects {
        Echo,
        Chorus;
    }

    public Integer saveEffect() {
        String effectName = spinner.getSelectedItem().toString();
        try {
            Effect effect = (Effect) (Class.forName("com.codepotato.model.effects." + effectsList.get(effectName))).newInstance();
            Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectsList.get(effectName) + " is created!");
            return EffectsConfigScr.audioController.addEffect(effect);
        } catch (Exception e) {
            Log.d(InitialScr.LOG_TAG, "com.codepotato.model.effects." + effectsList.get(effectName) + " didn't get created!");
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_settings_scr);
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.effects_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        effectsList = new EnumMap<Effects, String>(Effects.class);
        effectsList.put(Effects.Echo, "EchoEffect");
        effectsList.put(Effects.Chorus, "ChorusEffect");
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
                Integer effectID = saveEffect();
                Intent returnIntent = new Intent();
                if (effectID != null) {
                    returnIntent.putExtra("AudioEffectID", effectID);
                    setResult(RESULT_OK, returnIntent);
                } else {
                    setResult(RESULT_CANCELED, returnIntent);
                }
                Log.d(InitialScr.LOG_TAG, "AudioEffectID: " + getIntent().getIntExtra("AudioEffectID", 999));
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
