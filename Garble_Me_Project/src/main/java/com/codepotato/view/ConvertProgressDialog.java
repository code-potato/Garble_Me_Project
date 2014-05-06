package com.codepotato.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by senatori on 5/6/14.
 */
public class ConvertProgressDialog extends DialogFragment {

    protected ProgressBar progressBar;

    public ConvertProgressDialog(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //instantiate & associate AlertDialog with the activity that called it
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();  //gets layout inflater instance from the activity

        View dialogView= inflater.inflate(R.layout.wav_convert_progress, null);
        builder.setView(dialogView);
        progressBar= (ProgressBar) dialogView.findViewById(R.id.progressBar);  //TODO-senatori possible nullpointer exception. Add try catch
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if cancel, not sure what to do here yet.
                //ConvertProgressDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    //Getters/Setters
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(int progress) {
        this.progressBar.setProgress(progress);
    }
}
