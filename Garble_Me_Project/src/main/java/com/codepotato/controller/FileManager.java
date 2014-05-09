package com.codepotato.controller;

import android.content.Context;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.codepotato.model.EffectChainFactory;
import com.codepotato.model.Recorder;
import com.codepotato.model.SampleReader;
import com.codepotato.model.EffectChain;
import com.codepotato.view.ConvertProgressDialog;
import com.codepotato.view.R;


import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by senatori on 4/20/14.
 *
 */
public class FileManager extends AsyncTask<Object, Integer, Void> {
    private static final String LOGTAG = "CodePotatoFileManager";
    //Audio format related variables
    private static final int SAMPLERATE = 44100; //Hz
    private static final int NUM_CHANNELS = 1;
    private static final int BITRATE = 16;

    ConvertProgressDialog progressDialog; //for editing the wav conversion progress AlertView. Via API magic, this will run in the GUI thread
    private Context tmpContext; //will only be used for a function call in the thread. The context actually pertains to the effects config activity
    File tmpAudioFile;

    private EffectChain effectChain;

    public FileManager() {
        effectChain= EffectChainFactory.initEffectChain(); //this was just for testing
        //effectChain.addEffect(new EchoEffect());
        // wavConvertExportThread = new Thread(this, "Wave Convert And Export Thread");


    }



    /**
     * Deletes the raw audio file and its corresponding wav file (if it exists)
     * @param audioFile File object
     * @return boolean True if deletion was succesful
     */
    public boolean deleteFile(File audioFile){
        boolean rsuccess;
        //File parentDir= audioFile.getParentFile();
        //String fileString= this.removeExtension(audioFile); //audio file without extension
        rsuccess = audioFile.delete(); //delete raw file

        /*fileString= fileString.concat(".wav");
        File wavFile= new File(parentDir, fileString);
        if (wavFile.exists())
            wsuccess= wavFile.delete();*/


        return rsuccess;
    }
    /**
     * Retrieves the recorded raw files
     * @param appContext an instance of the Application context. Can be retrieved by Context.getApplicationContext in a
     *                   GUI Activity Class via this.getApplicationContext.
     * @return File[] an array of File objects
     */
    public File[] getRawFiles(Context appContext){
        //Log.d(LOGTAG, "about to List files");
        File searchDir = new File(appContext.getFilesDir(), Recorder.SAVED_RAW_FOLDER);
        appContext= null;
        //Log.d(LOGTAG, "Search Dir: " + searchDir.toString());
        File fileList[]= searchDir.listFiles(new FilenameFilter() { //will only retrieve files ending in .raw
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".raw");
            }
        });


        /*Log.d(LOGTAG, "fileList size: "+ Integer.toString(fileList.length)+ " STRING: " + fileList.toString());
        for(File file_iterator: fileList){
            Log.d(LOGTAG, file_iterator.getName());
        }*/


        return fileList;
    }

    /**
     * Retrieves a String based list of the raw file's names
     * @param appContext
     * @return String[] list of raw file names
     */
    public String[] listRawFiles(Context appContext){
        File temp[]= this.getRawFiles(appContext);
        appContext= null;

        String[] list= new String[temp.length];
        for (int i=0; i < temp.length; i++)
            list[i]= temp[i].getName();
        return list;
    }

    /**
     *
     * equivalent to run() method in a runnable implementation. is called by the execute() method on the AsyncTask instance from the GUI Activity class
     * This is so the logic can run it its own thread, and the GUI updates will run exclusively on the GUI thread. Android docs were adamant about that.
     * @param params a Java varargs. you will pass (ConvertProgressDialog, Context, File) in that order.
     * @see android.os.AsyncTask
     */
    @Override
    protected Void doInBackground(Object... params) {
        progressDialog= (ConvertProgressDialog) params[0];
        tmpContext= (Context) params[1];
        tmpAudioFile= (File) params[2];

        try {
            this.exportAsWav(tmpAudioFile, tmpContext);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Called via publishProgress() method in/during the doInBackground() execution thread
     * @param values a java vararg. only pass it a single Integer value ranging from 0-100 representing progress
     */
    @Override
    protected void onProgressUpdate(Integer... values){
        progressDialog.setProgressBar(values[0]);
    }


    protected void onPostExecute(Void result){
        progressDialog.dismiss();

        CharSequence text = tmpContext.getText(R.string.export_toast);
        Toast toast = Toast.makeText(tmpContext, text, Toast.LENGTH_SHORT);
        toast.show();

        tmpContext= null;

        //return true;
    }

    /**
     * Called when the user triggered a cancel() event on the AsyncTask instance.
     */
    protected void onCancelled(){
        tmpContext=null;
    }

    /**
     * Makes copy of a raw audio file in the .wav format. Is placed in the same directory as the raw file.
     *
     * @param rawAudioFile The File object representing the raw file you want to convert
     * @return File object representing the wav file.
     * @throws java.io.FileNotFoundException
     * @see java.io.File
     */

    private boolean exportAsWav(File rawAudioFile, Context appContext)throws IOException{

        int BUFF_SIZE= 10000; //10KB buffer
        //FileInputStream raw_in;
        FileOutputStream wav_out;

        byte data_buffer[] = new byte[BUFF_SIZE];
        int bytesRead = 0;
        int byteCountOffset = 0;
        long sampleCounter=0; //FOR DEBUGING PURPOSES

        int totalBytesCounted=0; //to Track Progress
        boolean comparison= false;
        //Handler mHandler;
        int filesize= (int)(rawAudioFile.length());
        int one_percent = (int) (filesize * .01);
        one_percent = (one_percent % 2) + one_percent; //make sure its even for modulus to work since loop increments by two;

        int percentage_progress=0;


        //remove the .raw extension so we can add .wav
        String waveFileNameString = removeExtension(rawAudioFile);
        waveFileNameString= waveFileNameString.concat(".wav");
        Log.d(LOGTAG, "raw filesize: " + Long.toString(rawAudioFile.length()));

        //------------------Retrieve and Set up External Directory-------------------------------------------------------


        String stringState = Environment.getExternalStorageState(); //to make sure that there is an SD or emulated SD
        File path;

        boolean overalSuccess= true;
        File garbleMeDirectory;
        if(Environment.MEDIA_MOUNTED.equals(stringState)) { //checks if we can access the SD

            path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getAbsoluteFile()); //returns the path of the Android Music Dir

            garbleMeDirectory = new File(path, "GarbleMe"); //A folder in the Android Music dir to put the wav files
            if (!garbleMeDirectory.exists()) {  //create Dir if it doesn't exist
                overalSuccess = garbleMeDirectory.mkdirs(); //returns false if directory creation failed
                //Log.d(LOGTAG, "Attempt to create dir: " + Boolean.toString(overalSuccess));
                return false;
            }
        }
        else
            return false;

        File wavFile = new File(garbleMeDirectory, waveFileNameString); //file will be located in Android music dir/GarbleMe
        //-------------------------------------------------------------------------------------------------------------------

        //************************PROCESSING AUDIO AND WRITING IT TO A FILE****************************************
        SampleReader sampleReader= new SampleReader(rawAudioFile, SAMPLERATE, 16, 1);
        //raw_in= new FileInputStream(rawAudioFile);
        wav_out= new FileOutputStream(wavFile);

        insertWaveFileHeader(wav_out, rawAudioFile);

        double sample;
        int bytesProcessed=0;
        while(!isCancelled()){ //Terminates after effects go full quieting or user triggers a cancel event on the event sync instance
            try {
                int zeroCounter=0; //keeps track of the 0.0 double values returned by nextSample() to determine if were at end of file
                for(bytesProcessed = 0; bytesProcessed < BUFF_SIZE; bytesProcessed+= 2){ //in 16bit mono, a sample is 2 bytes. thus increment by 2

                    sample= sampleReader.nextSample();
                    sampleCounter++;
                    totalBytesCounted+=2;

                    sample = effectChain.tickAll(sample); //run the sample through the effects

                    //********UPDATE PROGRESS HERE!!!!!
                    if(((totalBytesCounted % one_percent)==0) && (percentage_progress<=100)){ //TRUE every 1% of File read

                        percentage_progress++;
                        publishProgress(percentage_progress);


                    }


                    //int comparison= Double.compare(0.0, sample); //if sample is equal to 0.0, it could be eof
                    comparison= Math.abs(sample)< 1E-8 ;
                    if(comparison){ //so we check if it's zero(or close enough to it)
                        zeroCounter++; //if it is, we increment the counter. We want 120 of these zeros to determine if the echo/effect has diminished
                    }
                    else
                        zeroCounter=0; //we want consecutive 0.0's

                    if (zeroCounter == 120){ //EOF (this is a heuristical guess really)
                        break;
                    }


                    sampleReader.sampleToBytes(sample, data_buffer, bytesProcessed); //bytesProcessed is the offset

                }
                if (zeroCounter >= 120) {

                    break; //propagating the break command through the loops

                }

                wav_out.write(data_buffer, 0, bytesProcessed); //writes BUFF_SIZE(or bytesProcessed) number of bytes ot the wav_out stream

            }catch(IOException ioe){
                //end of file handling
                break;
            }
        }

        try{
            wav_out.write(data_buffer, 0, bytesProcessed); //write what remains in the buffer upon break/interupt.
            wav_out.close();
            Log.d(LOGTAG, "Wave File Size: " + Long.toString(wavFile.length()));

        }catch(IOException ioe){
            //add useless debug logcat statement here
            overalSuccess= false;
            return overalSuccess;
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user. This snippet was taken verbatim from the Android Documentation
        MediaScannerConnection.scanFile(appContext,
                new String[] { wavFile.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        appContext= null; //for garbage collection purposes.


        return overalSuccess;
    }



    /**
     * Inserts the wave header into the ouputstream
     * Wave header was based of Stanford's description of Microsoft's WAVE PCM format at
     * https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
     *
     * @param wave_out The output stream
     * @param rawAudioFile the File object representing the raw audio file
     * @throws java.io.IOException
     */
    private void insertWaveFileHeader(FileOutputStream wave_out, File rawAudioFile) throws IOException {

        long file_size= rawAudioFile.length() + 36; //including the 44 header bytes except for RIFF and File Size header bytes
        long totalAudioLen= rawAudioFile.length(); //the size of just the audio
        int byteRate = BITRATE * SAMPLERATE * NUM_CHANNELS/8; //(bits per sample * Samples per second * channels) / 8 = bytes per second
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (file_size & 0xff);  //converting long to a 16 bit int small endian.
        header[5] = (byte) ((file_size >> 8) & 0xff);
        header[6] = (byte) ((file_size >> 16) & 0xff);
        header[7] = (byte) ((file_size >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // subchunk size(4 bytes). its 16 for PCM. Not sure how it's derived, but this val does NOT represent bitrate
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // Audio Format(2 bytes). PCM = 1. Values other than 1 indicates some form of compression
        header[21] = 0;
        header[22] = (byte) NUM_CHANNELS;
        header[23] = 0;

        //Converting Long int into array of bytes the old fashioned way
        // using & 0xff forces you to just read the 8 bits (in this case the first 8 bits)
        //Also, the endianes is being changed from Big Endian to Little Endian(MS Wave files use little endian)
        header[24] = (byte) (SAMPLERATE & 0xff);//11000000000000011110101 & 11111111 = 11110101 (just the first 8 bits!) remember, 0xFF = 1111 1111
        header[25] = (byte) ((SAMPLERATE >> 8) & 0xff); //shift longint 8 bits to the right to read the next 8 bits,
        header[26] = (byte) ((SAMPLERATE >> 16) & 0xff);//shift longint 16 bits to the right to read the next 8 bits.
        header[27] = (byte) ((SAMPLERATE >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);

        // block align. Number of bytes an audio frame consists of. Since 16bit mono, a frame consists of one 16 sample, which = 2 bytes
        header[32] = (byte) (NUM_CHANNELS * BITRATE / 8);
        header[33] = 0;
        header[34] = BITRATE;  // bits per sample
        header[35] = 0;
        //data subchunk2
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        wave_out.write(header, 0, 44);

    }

    /**
     * Takes a file and removes any .raw, .wav extensions
     * @param AudioFile
     * @return a string of the file's name
     */
    private String removeExtension(File AudioFile) {
        String FileNameString= AudioFile.getName();
        StringTokenizer stringTokenizer= new StringTokenizer(FileNameString, ".");
        FileNameString = stringTokenizer.nextToken(); //now we have our audio file without .raw
        return FileNameString;
    }

    /*public void export(File audioFile, Context tmpContext){
        this.tmpAudioFile = audioFile;
        this.tmpContext = tmpContext;
        ecsHandler= new Handler(Looper.getMainLooper()); //hopefully this will be used to send progress updates to the Activity/View thread
        wavConvertExportThread.start();
    }*/

    /*

    @Override
    public void run() {
        this.exportToExternalMusicDir(tmpAudioFile, tmpContext);

    }*/
}

