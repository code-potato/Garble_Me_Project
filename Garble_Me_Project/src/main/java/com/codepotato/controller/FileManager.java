package com.codepotato.controller;

import android.content.Context;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.codepotato.model.effects.EchoEffect;
import com.codepotato.model.SampleReader;
import com.codepotato.model.EffectChain;
import com.codepotato.model.EffectChainFactory;


import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by senatori on 4/20/14.
 */
public class FileManager {
    private static final String LOGTAG = "CodePotatoFileManager";
    //Audio format related variables
    private static final int SAMPLERATE = 44100; //Hz
    private static final int NUM_CHANNELS = 1;
    private static final int BITRATE = 16;

    EffectChain effectChain;

    public FileManager() {
        effectChain= EffectChainFactory.initEffectChain();
        effectChain.addEffect(new EchoEffect());
    }

    /**
     * Moves a file to the Android Music Directory. If it is not a WAV, the android music player may not recognize it.
     * @param wavFile The File object representing the wave file to be exported.
     * @param appContext an instance of the Application context. Can be retrieved by Context.getApplicationContext in a
     *                   GUI Activity Class via this.getApplicationContext.
     * @return true if file was successfully exported (propt user to let them know, etc)
     */
    public boolean exportToExternalMusicDir(File wavFile, Context appContext){
        String stringState = Environment.getExternalStorageState(); //to make sure that there is an SD or emulated SD
        File path;
        File externalWavFile= new File(wavFile.getParent());
        boolean overalSuccess= true;

        int WRITE_BUFF_SIZE = 10000;
        if(Environment.MEDIA_MOUNTED.equals(stringState)){

            Log.d(LOGTAG, "File path before retreriving external Dir : "+ externalWavFile.toString());
            path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsoluteFile()); //returns the path of the Android Music Dir

            File garbleMeDirectory= new File(path, "GarbleMe"); //A folder in the Android Music dir to put the wav files
            if(!garbleMeDirectory.exists()){  //create Dir if it doesn't exist
                overalSuccess = garbleMeDirectory.mkdirs(); //returns false if directory creation failed
                //Log.d(LOGTAG, "Attempt to create dir: " + Boolean.toString(overalSuccess));
            }

            //path.mkdirs();
            //overalSuccess = garbleMeDirectory.exists();
            //overalSuccess= path.exists();
            //Log.d(LOGTAG, "Directory Created or Exists: "+ overalSuccess);

            externalWavFile = new File(garbleMeDirectory, wavFile.getName());
            //Log.d(LOGTAG, "externalWavFile.toString: " + externalWavFile.toString() );

            byte data_buffer [] = new byte[WRITE_BUFF_SIZE];
            try{
                FileInputStream fis= new FileInputStream(wavFile); //the wave file to be copied
                FileOutputStream fos= new FileOutputStream(externalWavFile);
                int bytesRead=0;

                //copying file 10KB at a time via Input & Output streams
                do {
                    bytesRead = fis.read(data_buffer,0, WRITE_BUFF_SIZE);
                    fos.write(data_buffer, 0, bytesRead);
                }while(bytesRead == WRITE_BUFF_SIZE);  //if bytes read is less then buff size, that was the last read iteration

                fos.close();
                fis.close();

                //Log.d(LOGTAG, "externalWavFile size: " + Long.toString(externalWavFile.length()));

            }catch(IOException ioe){
                overalSuccess= false;
                Log.d(LOGTAG, Log.getStackTraceString(ioe));
                return overalSuccess;

            }

            // Tell the media scanner about the new file so that it is
            // immediately available to the user. This snippet was taken verbatim from the Android Documentation
            MediaScannerConnection.scanFile(appContext,
                    new String[] { externalWavFile.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            appContext= null; //for garbage collection purposes.
        }
        else{
            //external media/public storage is not mounted. This could be because the device's storage is already
            //mounted to a computer by USB
            overalSuccess= false;
            return overalSuccess;
        }
        return overalSuccess;
    }

    /**
     * Makes copy of a raw audio file in the .wav format. Is placed in the same directory as the raw file.
     *
     * @param rawAudioFile The File object representing the raw file you want to convert
     * @return File object representing the wav file.
     * @throws java.io.FileNotFoundException
     * @see java.io.File
     */

    public File convertToWavFile(File rawAudioFile)throws IOException{

        int BUFF_SIZE= 10000; //10KB buffer
        //FileInputStream raw_in;
        FileOutputStream wav_out;

        byte data_buffer[] = new byte[BUFF_SIZE];
        //int bytesRead = 0;
        //int byteCountOffset = 0;
        long sampleCounter=0; //FOR DEBUGING PURPOSES
        boolean comparison= false;

        //remove the .raw extension**** should probably refactor?
        String waveFileNameString= rawAudioFile.getName();
        StringTokenizer stringTokenizer= new StringTokenizer(waveFileNameString, ".");
        waveFileNameString = stringTokenizer.nextToken(); //now we have our audio file without .raw
        waveFileNameString= waveFileNameString.concat(".wav");
        Log.d(LOGTAG, "convertToWavFile filename: " + waveFileNameString);
        Log.d(LOGTAG, "raw filesize: " + Long.toString(rawAudioFile.length()));
        //---------------

        File wavFile = new File(rawAudioFile.getParent(), waveFileNameString); //TODO-senatori implement a method of deleting the file after it has been shared
        SampleReader sampleReader= new SampleReader(rawAudioFile, SAMPLERATE, 16, 1);
        //raw_in= new FileInputStream(rawAudioFile);
        wav_out= new FileOutputStream(wavFile);

        insertWaveFileHeader(wav_out, rawAudioFile);
        //------------------------
        double sample;
        int bytesProcessed=0;
        while(true){ //Terminates after SampleReader.nextSample() returns 20 consecutive 0.0's
            try {
                int zeroCounter=0; //keeps track of the 0.0 double values returned by nextSample() to determine if were at end of file
                for(bytesProcessed = 0; bytesProcessed < BUFF_SIZE; bytesProcessed+= 2){ //in 16bit mono, a sample is 2 bytes. thus increment by 2
                    sample= sampleReader.nextSample();
                    sampleCounter++;
                    sample = effectChain.tickAll(sample); //run the sample through the effects
                    //Log.d(LOGTAG, "Time: " + Long.toString(sampleCounter/44100)+  " EchoSample Val: "+ Double.toString(sample));
                    //TODO-senatori The eof heuristic based code should probably be moved to another function for readability
                    //int comparison= Double.compare(0.0, sample); //if sample is equal to 0.0, it could be eof
                    comparison= Math.abs(sample)< 1E-8 ;
                    if(comparison){ //so we check that there's been at least 120 consecutive values less then 1E-8, which isnt zero but inaudible
                        zeroCounter++;
                    }
                    else
                        zeroCounter=0; //we want consecutive 0.0's

                    if (zeroCounter == 120){ //EOF (this is a heuristical guess really)
                        break;
                    }
                    //***** add effect chain based code here******


                    sampleReader.sampleToBytes(sample, data_buffer, bytesProcessed); //bytesProcessed is the offset

                }
                if (zeroCounter >= 120) //propagating the break through the loops
                    break;

                wav_out.write(data_buffer, 0, bytesProcessed); //writes BUFF_SIZE number of bytes ot the wav_out stream

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
        }

        return wavFile;
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

}
