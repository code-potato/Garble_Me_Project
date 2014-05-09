package com.codepotato.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codepotato.controller.FileManager;

/**
 * This class displays an AlertView with a progress bar. Very similar to ProgressDialog class in functionality. Inherits from
 * Dialog Fragment, so youwill need to call its methods for displaying the Alert View.
 * Created by senatori on 5/6/14.
 * @author Steven Senatori
 */
public class ConvertProgressDialog extends DialogFragment {

    protected ProgressBar progressBar;
    protected TextView textView;
    protected AsyncTask exportTask; //this will be the FileManager instance that is converting and exporting the raw file

    /**
     * Creates an instance of the ConvertProgressDialog. Does not display it. Call show() to have the alertview displayed.
     * This class was intended to be only used for updating the raw to wav conversion progress.
     *
     * @param exportTask an instance of FileManager
     */
    public ConvertProgressDialog(FileManager exportTask){
        this.exportTask= exportTask;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //instantiate & associate AlertDialog with the activity that called it
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();  //gets layout inflater instance from the activity

        View dialogView= inflater.inflate(R.layout.wav_convert_progress, null);
        builder.setView(dialogView);
        progressBar= (ProgressBar) dialogView.findViewById(R.id.progressBar);  //TODO-senatori possible nullpointer exception. Add try catch
        textView= (TextView) dialogView.findViewById(R.id.progressText);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if cancel, not sure what to do here yet.
                exportTask.cancel(false); //true to interrupt the AsyncThread, false to send cancel request and let it safely terminate
            }
        });

        return builder.create();
    }

    /**
     * Sets the value of the progress bar. You should not have to call it. Call execute() on the FileManager instance with
     * the params required by FileManager.doInBackground() instead for the progess to be updated automatically
     * @param progress
     */
    public void setProgressBar(int progress) {
        if(progress <= 99) {
            this.progressBar.setProgress(progress);
            String tmp = Integer.toString(progress) + "%";
            textView.setText(tmp);

        }
        else{
            textView.setText("Finalizing...");
            progressBar.setIndeterminate(true);

        }

    }
}
