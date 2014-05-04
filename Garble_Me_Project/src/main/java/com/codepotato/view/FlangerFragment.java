package com.codepotato.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.codepotato.model.effects.Effect;
import com.codepotato.model.effects.FlangerEffect;

/**
 * Created by terrywong on 4/25/14.
 */
public class FlangerFragment extends Fragment {
    private FlangerEffect effect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flanger, container, false);
        // SeekBar for the rateBar
        SeekBar rateBar = (SeekBar) view.findViewById(R.id.rateBar);
        if (effect != null)
            rateBar.setProgress(effect.getRate());
        else
            rateBar.setProgress(50);
        rateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (effect != null) {
                    effect.setRate(progressChanged);
                }
            }
        });

        SeekBar depthBar = (SeekBar) view.findViewById(R.id.depthBar);
        if (effect != null)
            depthBar.setProgress(effect.getDepth());
        else
            depthBar.setProgress(50);
        depthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (effect != null) {
                    effect.setDepth(progressChanged);
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
        this.effect = (FlangerEffect) effect;
        Log.d(InitialScr.LOG_TAG, "Effect is set!");
    }

    public Effect getEffect() {
        return (Effect) effect;
    }
}