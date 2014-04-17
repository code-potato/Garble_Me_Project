package com.codepotato.audio_recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Encapsulates an AudioRecord instance so it runs in its own recordingThread. None of the main AudioRecord methods are
 *
 * from this object.
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
    private static final String LOGTAG = "Recorder";

    //Audio format related variables
    private static final int RECORDER_SAMPLERATE= 44100; //Hz
    private static final int RECORDER_CHANNELS= AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_ENCODING= AudioFormat.ENCODING_PCM_16BIT;
    private int minBufferSizeInBytes; //is determined at runtime

    /**
     *
     * @param filepath the file path of the apps sandboxed directory. Can be retrieved via context.getFilesDir in
     *                 an Activity Class
     * @see android.content.Context.getFilesDir();
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
     * Not Yet finished
     * @param waveFileNameString
     */
    public void convertToWavFile(String waveFileNameString){

        FileInputStream raw_in;
        FileOutputStream wav_out;
        int totalAudioLen;
        int totalDataLen;
        //int longSampleRate = RECORDER_SAMPLERATE; //WAV Header info requires Long datatype?
        int channels = 1; //we're recording in mono
        int recorderBitsPerSample;

        //this if statement is just for the sake of coding defensively.
        if(RECORDER_ENCODING== AudioFormat.ENCODING_PCM_16BIT)
            recorderBitsPerSample = 16;
        else if (RECORDER_ENCODING== AudioFormat.ENCODING_PCM_8BIT)
            recorderBitsPerSample = 8;

        int byteRate = recorderBitsPerSample * RECORDER_SAMPLERATE * channels/8; //(bits per sample * Samples per second * channels) / 8 = bytes per second
        byte data_buffer[] = new byte[minBufferSizeInBytes];
        int bytesRead = 0;
        int byteCountOffset = 0;

        File wavFile = new File(getWavDirectory(), waveFileNameString);

        try {
            raw_in= new FileInputStream(rawAudioFile);
            wav_out= new FileOutputStream(wavFile);

            insertWaveFileHeader();
            //------------------------

            while(raw_in.read(data_buffer) != -1){ //FileInputStream.read returns -1 if end of stream is reached
                wav_out.write(data_buffer);

            }
            raw_in.close();
            wav_out.close();

        } catch (FileNotFoundException fnfe) {
            Log.e(LOGTAG, "Something went wrong with finding or creating a file");
            Log.e(LOGTAG, Log.getStackTraceString(fnfe));
        } catch (IOException ioe) {
            Log.e(LOGTAG, "Something went wrong with writing wav file");
            Log.e(LOGTAG, Log.getStackTraceString(ioe));
        }

    }

    /**
     * Retrieves the dir/path used to store wav files
     * @return
     */
    private File getWavDirectory() {

        File folder= new File(filepath, SAVED_WAV_FOLDER);
        if (!folder.exists())
            folder.mkdir();

        return folder;
    }

    private void deleteTempRawFile() {
        rawAudioFile.delete();
    }

    /**
     * NOT YET COMPLETED
     */
    private void insertWaveFileHeader() {

    }



}
