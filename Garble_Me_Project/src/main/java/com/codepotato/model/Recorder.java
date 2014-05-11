package com.codepotato.model;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Encapsulates an AudioRecord instance so it runs in its own recordingThread. None of the main AudioRecord methods available
 * via this class.
 *
 * @author Steven Senatori on 3/22/14.
 */
public class Recorder implements Runnable{ //Runnable must be implemented for creating threads

    AudioRecord recorder; //android API class that handles recording
    Thread recordingThread;
    private File filepath;  //will hold the sandboxed file path name
    private File rawAudioFile;

    private boolean isRecording = false; //a flag for the thread to determine when to stop recording
    private static final String TEMP_FILE_NAME= "recorded_audio_file.raw"; //temp file name for initial audiofile creation. will be renamed to .wav later
    private static final String SAVED_WAV_FOLDER = "SavedWavFiles";  //where our .wav files are saved
    public static final String SAVED_RAW_FOLDER="SavedRawFiles"; //where our .raw files are stored
    private static final String LOGTAG = "Recorder";

    //Audio format related variables
    private static final int RECORDER_SAMPLERATE= 44100; //Hz
    private static final int RECORDER_CHANNELS= AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_ENCODING= AudioFormat.ENCODING_PCM_16BIT;
    private int minBufferSizeInBytes; //is determined at runtime

    /**
     *
     * @param filepath the file path of the apps sandboxed directory. Can be retrieved via Context.getFilesDir in
     *                 an Activity Class using the this.getFilesDir() method
     * @see //android.content.Context.getFilesDir();
     */
    public Recorder(File filepath){

        this.filepath = filepath;

        //the minimum buffer size needed to store one chunk of audio based on sample rate, audiochannels, and format
        minBufferSizeInBytes = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_ENCODING);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_ENCODING, minBufferSizeInBytes);

        recordingThread = new Thread(this, "Recorder: Audio Recording Thread"); //last param is the recordingThread name
    }

    /**
     * Starts recording audio and saves it to temp file.
     *
     */
    public void start(){
        recordingThread.start(); //invokes the run() method in a new thread
    }

    @Override
    /**
     * Implemented interface method. DO NOT CALL. Use start() instead.
     */
    public void run() {
        filePrepare();
        startRecording();


    }
    //creates the needed filename/path info and housekeeping
    private void filePrepare(){

        rawAudioFile = new File(filepath, TEMP_FILE_NAME); // PATH/recorded_audio_file.raw
        if (rawAudioFile.exists())
            rawAudioFile.delete(); //delete previous temp file

    }

    private void startRecording(){
        isRecording = true;

        try{
            ByteBuffer audioBuffer= ByteBuffer.allocateDirect(minBufferSizeInBytes); //must use allocateDirect or AudioRecord.read method wont work properly

            recorder.startRecording(); //throws IllegalStateException if something goes wrong
            FileOutputStream fileOutputStream= new FileOutputStream(rawAudioFile);

            int bytes_read;

            if(fileOutputStream != null){
                while(isRecording){ //the isRecording flag will be toggled outside of the thread
                    //places audio data in audioBuffer. Returns number of bytes read, if bytes less than zero, there's an error
                    bytes_read= recorder.read(audioBuffer, minBufferSizeInBytes);

                    if(bytes_read > 0){
                        fileOutputStream.write(audioBuffer.array(), 0, bytes_read); //throws IOException TODO investigate using FileChannel to use ByteBuffer without array conversion
                    }
                    else{
                        Log.d(LOGTAG, "bytes_read is less then or equal zero, end of recording or some kind of error" +
                                " returned by AudioRecord.read");
                        isRecording= false;
                    }

                }

                fileOutputStream.close(); //Closes our .raw file

            }

        } catch (IllegalStateException ise){
            Log.e(LOGTAG, "Android AudioRecords startRecording method failed");
            Log.e(LOGTAG, Log.getStackTraceString(ise));
        } catch (FileNotFoundException fnfe) {
            Log.e(LOGTAG, "FileOutputStream failed to create new instance/file");
        } catch (IOException ioe) {
            Log.e(LOGTAG, "FileOutputStream encountered an error while writing or closing");
            Log.e(LOGTAG, Log.getStackTraceString(ioe));
        }

    }

    public void stop(){

        isRecording= false;

        recorder.stop();
        recorder.release(); //frees up AudioRecording resources. The object can no longer be used and the reference should be set to null
        recorder= null;

        //recordingThread= null;

        //should probably call convert to wav code here


        //deleteTempRawFile();

    }

    /**
     * Saves the recorded audio file to the dir app sandbox root/SavedRawFiles/ dir
     * @param fileName String containing the name of audio file
     * @return File containing the full path of the
     */
    public File save(String fileName){
        fileName = fileName.concat(".raw"); //adding the .raw file extension to the file name
        File completeSavePath= new File(this.getSavedRawDirectory(), fileName);

        rawAudioFile.renameTo(completeSavePath);

        return completeSavePath;
    }



    private File getSavedRawDirectory(){
        File folder= new File(filepath, SAVED_RAW_FOLDER);
        if(!folder.exists())
            folder.mkdir();
        return folder;
    }

}