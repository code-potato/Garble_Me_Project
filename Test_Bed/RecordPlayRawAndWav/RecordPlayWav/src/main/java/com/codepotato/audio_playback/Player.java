package com.codepotato.audio_playback;

import android.content.res.*;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import com.codepotato.AudioEffects.Delay;
import com.codepotato.AudioEffects.DelayLine;
import com.codepotato.AudioEffects.EchoEffect;

import java.io.*;


public class Player implements Runnable{
    private boolean isPlaying;
    private int buff_size; //determined at runtime based on hardware, sample rate, channelConfig, audioFormat
    //Activity activity;

    private InputStream is;
    private BufferedInputStream bis;
    private DataInputStream dis;
    private byte[] buff;

    private AudioTrack track;
    private Thread audioThread;
    private boolean isStereo; //stereo or mono
    private static final String LOG_TAG= "XPlayer";

    EchoEffect echo;


    /**for the song file that michael has been using, which I assume is stereo
     *
     * @param descriptor
     * @throws IOException
     */
    public Player(AssetFileDescriptor descriptor) throws IOException {
        // setup input stream from given file
        is = new FileInputStream(descriptor.getFileDescriptor());
        //isStereo= true;
        buff_size= AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        //Log.d(LOG_TAG, "Mikes buff_size: " + Integer.toString(buff_size));
        //Log.d(LOG_TAG, descriptor.toString());
        prepare();


    }

    public Player(File audioFile) throws IOException {

        // setup input stream from given file
        is = new FileInputStream(audioFile);
        //audio_format= AudioFormat.CHANNEL_IN_MONO;
        //isStereo= false;
        buff_size= AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //Log.d(LOG_TAG, "Recorded Audio buff_size: " + Integer.toString(buff_size));

        prepare();

    }

    private void prepare() throws IOException{

        isPlaying = false;
        bis = new BufferedInputStream(is);
        dis = new DataInputStream(bis); //has to do with endian stuff

        // create byte buffer
        buff = new byte[buff_size];

        //setup audio track
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                32000, AudioTrack.MODE_STREAM);

        audioThread= new Thread(this, "Player: Audio Playback Thread");

        //set echo effect
        echo = new EchoEffect();
        echo.setFeedbackGain(0);
        echo.setDelayTime(1000);
        echo.setWetGain(.5);
        echo.setDryGain(1.2);

    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void play() {
        audioThread.start(); //executes the code in the Player.run() method
    }

    @Override
    /**
     * This code runs in it's own thread.
     */
    public void run() {
        Log.d("player", "play");

        //set to true
        isPlaying = true;

        //tell track to be ready to play audio
        track.play();

        while(isPlaying){
            try {
                //fill buffer with bytes from file reader
                double sample;
                for(int i=0; i < buff_size; i+= 2)  //increment index by two because 16bit mono sample is 2 bytes long
                {
                    //reads 2 bytes from stream and stores them in buff at offset i. returns number of bytes read.
                    //if bytes are read, condition is TRUE
                    if (bis.available() > 0){
                        bis.read(buff,i, 2);
                    }
                    else {
                        buff[i] = 0; buff[i+1] = 0;
                    }

                    sample = bytesToSample(buff, i);

                    sample = echo.tick(sample);

                    sampleToBytes(sample, buff, i);

//                    else { //EOF: no more bytes to read in bytestream
//
//                        this.pause(); //CAUTION. Might cause a minor bug if pause() just pauses. Might need to define stop();
//                        break;
//
//                    }

                }

                //write buffer to track to play
                track.write(buff, 0, buff_size);

            } catch (IOException e) {
                break; //when eof is reached
            }
        }
        //track.pause();
        //track.flush();
    }

    public void pause() {
        Log.d("player", "pause");
        isPlaying = false;
    }

    /**
     * Converts 2 bytes from the buffer (in small endian format), starting at the offset,
     * into an audio sample of type double (big endian).
     */
    private double bytesToSample(byte[] buff, int offset)
    {
        //                Low Byte          MASK        High Byte     Left Shift
        double sample= ((buff[offset + 0] & 0xFF) | (buff[offset + 1] << 8) );
        /*Explanation: The first part, we take the Low Byte and use an AND mask of 1111 1111 to ensure that we just get
        * the first 8 bits(NOTE: this is redundant since a byte is 8 bits to begin with). (1011 0000) & (1111 1111)= 1011 0000
        * NEXT: We take the High Byte and shift it 8 Bytes to the left. In Java, this promotes it to an integer(32 bits).
        * Next we merge the int representing High Byte and the Low Byte with a bitwise OR operator
        * 00000000 0000000 10101111 00000000
        *                       OR  10110000 =
        * 00000000 0000000 10101111 10110000 Now our bytes are in the Big Endian order required for primitive types */

        //since 2 bytes is a short which has range -32768 to +32767, we divide by 32768 to normalize to -1.0 to +1.0 range for DSP
        sample = sample /32768.0;

        return sample;

    }

    /**
     * Converts sample of type double into 2 bytes,
     * and stores into the byte buffer starting at the given offset.
     */
    private void sampleToBytes(double sample, byte[] buff, int offset)
    {
        sample = Math.min(1.0, Math.max(-1.0, sample));  //ensures that our double is within the -1.0 to +1.0 range
        int nsample = (int) Math.round(sample * 32767.0);//expands it to the range of -32768 to 32767 range of short, round, & truncate
        buff[offset + 1] = (byte) ((nsample >> 8) & 0xFF); //isolate and extract the high byte
        buff[offset + 0] = (byte) (nsample & 0xFF);        //isolate the low byte with MASK
    }
}
