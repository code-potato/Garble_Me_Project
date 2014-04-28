package com.codepotato.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by terrywong on 4/24/14.
 */
public class EchoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.echo_fragment, container, false);
        // SeekBar for the delayBar
        SeekBar delayBar = (SeekBar) view.findViewById(R.id.delayBar);
        if (delayBar != null)
            Log.d(InitialScr.LOG_TAG, "Delay bar is found!");
        delayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(InitialScr.LOG_TAG, "Delay bar is touched!");
                Toast.makeText(getActivity().getBaseContext(), "Delay Bar Progress: " + progressChanged, Toast.LENGTH_SHORT).show();
            }
        });
        SeekBar wetBar = (SeekBar) view.findViewById(R.id.wetBar);
        if (wetBar != null)
            Log.d(InitialScr.LOG_TAG, "Wet bar is found!");
        wetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(InitialScr.LOG_TAG, "Wet bar is touched!");
                Toast.makeText(getActivity().getBaseContext(), "wet Bar Progress: " + progressChanged, Toast.LENGTH_SHORT).show();
            }
        });

        SeekBar dryBar = (SeekBar) view.findViewById(R.id.dryBar);
        if (dryBar != null)
            Log.d(InitialScr.LOG_TAG, "dry bar is found!");
        dryBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(InitialScr.LOG_TAG, "dry bar is touched!");
                Toast.makeText(getActivity().getBaseContext(), "dry Bar Progress: " + progressChanged, Toast.LENGTH_SHORT).show();
            }
        });

        SeekBar feedbackBar = (SeekBar) view.findViewById(R.id.feedbackBar);
        if (feedbackBar != null)
            Log.d(InitialScr.LOG_TAG, "Feedback bar is found!");
        feedbackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(InitialScr.LOG_TAG, "Feedback bar is touched!");
                Toast.makeText(getActivity().getBaseContext(), "Feedback Bar Progress: " + progressChanged, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
