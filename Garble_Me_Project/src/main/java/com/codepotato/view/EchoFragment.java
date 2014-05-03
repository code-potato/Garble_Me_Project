package com.codepotato.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.codepotato.model.effects.EchoEffect;
import com.codepotato.model.effects.Effect;

/**
 * Created by terrywong on 4/24/14.
 */
public class EchoFragment extends Fragment {
    private EchoEffect effect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_echo, container, false);

        // SeekBar for the delayBar
        SeekBar delayBar = (SeekBar) view.findViewById(R.id.delayBar);
        if (effect != null)
            delayBar.setProgress(effect.getDelayTime());
        else
            delayBar.setProgress(50);
        delayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (effect != null) {
                    effect.setDelayTime(progressChanged);
                }
            }
        });
        SeekBar wetBar = (SeekBar) view.findViewById(R.id.wetBar);
        if (effect != null)
            wetBar.setProgress(effect.getWetGain());
        else
            wetBar.setProgress(50);
        wetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (effect != null) {
                    effect.setWetGain(progressChanged);
                }
            }
        });

        SeekBar dryBar = (SeekBar) view.findViewById(R.id.dryBar);
        if (effect != null)
            dryBar.setProgress(effect.getDryGain());
        else
            dryBar.setProgress(50);
        dryBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (effect != null) {
                    effect.setDryGain(progressChanged);
                }
            }
        });

        SeekBar feedbackBar = (SeekBar) view.findViewById(R.id.feedbackBar);
        if (effect != null)
            feedbackBar.setProgress(effect.getFeedbackGain());
        else
            feedbackBar.setProgress(50);
        feedbackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (effect != null) {
                    effect.setFeedbackGain(progressChanged);
                }
            }
        });

        return view;
    }

    public void setEffect(Effect effect) {
        this.effect = (EchoEffect) effect;
        Log.d(InitialScr.LOG_TAG, "Effect is set!");
    }

    public Effect getEffect() {
        return (Effect) effect;
    }
}
