package com.codepotato.model;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Player is responsible for controlling playback of an audiofile. It contains an instance of
 * the EffectChain so that effects can be applied during playback.
 *
 * @author Michael Santer
 */
public class Player implements Runnable {
    private boolean isPlaying;
    private int buff_size; //determined at runtime based on hardware, sample rate, channelConfig, audioFormat
    private int zeroCounter;

    SampleReader sampleReader;
    private byte[] buff;
    EffectChain effectChain;

    private AudioTrack track;
    private Thread audioThread;

    private static final String LOG_TAG = "XPlayer";


    /**
     * @param audioFile Given an audio file, of type File, the constructor initializes a new sampleReader
     *                  for reading the file, a new AudioTrack for playback, and gets the current instance of
     *                  the EffectChain.
     * @throws IOException
     */
    public Player(File audioFile) throws IOException {

        isPlaying = false;
        // setup input stream from given file
        sampleReader = new SampleReader(audioFile, 44100, 16, 1);

        // setup byte buffer
        buff_size = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buff = new byte[buff_size];

        //setup audio track
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                32000, AudioTrack.MODE_STREAM);

        effectChain = EffectChainFactory.initEffectChain();
    }

    /**
     * @return True if audio is playing, false if it is not playing.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Tells the player to start playing. This automatically spawns a new thread.
     * play() calls run() to start playback in a new thread. During playback this method
     * reads a sample from SampleReader, runs that sample through the effect chain, then
     * writes it to the AudioTrack for plaback.
     */
    public void play() {
        // create and run new thread for playback
        audioThread = new Thread(this, "Player: Audio Playback Thread");
        audioThread.start(); //executes the code in the Player.run() method
    }

    @Override
    /**
     *
     * This code runs in it's own thread.
     * Do not call this method directly, it is used by the play method.
     * play() calls run() to start playback in a new thread. During playback this method
     * reads a sample from SampleReader, runs that sample through the effect chain, then
     * writes it to the AudioTrack for plaback.
     */
    public void run() {
        Log.d("player", "play");

        //set to true
        isPlaying = true;

        //tell track to be ready to play audio
        track.play();

        while (isPlaying) {
            try {

                double sample;
                //fill buffer with bytes from sampleReader
                for (int i = 0; i < buff_size; i += 2)  //increment index by two because 16bit mono sample is 2 bytes long
                {
                    sample = sampleReader.nextSample();

                    sample = effectChain.tickAll(sample);

                    if (Math.abs(sample) < 1E-6) { //so we check that there's been at least 120 consecutive zeros
                        zeroCounter++;
                    } else
                        zeroCounter = 0; //we want consecutive 0.0's

                    if (zeroCounter == 120) { //EOF (this is a heuristical guess really)
                        pause();
                        seekToBeginning();
                        break;
                    }

                    sampleReader.sampleToBytes(sample, buff, i);
                }

                //write buffer to track to play
                track.write(buff, 0, buff_size);

            } catch (IOException e) {
                break; //when eof is reached
            }
        }
    }

    /**
     * Stops playback, while maintaining the current playback position.
     * Kills the thread that was started by play().
     */
    public void pause() {
        Log.d("player", "pause");

        track.pause();
        isPlaying = false;

        // kill playback thread
        audioThread.interrupt();
        audioThread = null;
    }

    /**
     * @param location Given a location between 0 and 100 %, seek() will move playback
     *                 to the appropriate position in the audio file.
     * @throws IOException
     */
    public void seek(int location) throws IOException {
        long offset = (long) (sampleReader.length() * (location / 100.));
        sampleReader.seek(offset);
        track.flush();
    }

    /**
     * A convenience method that returns the playback to the beginning.
     *
     * @throws IOException
     */
    public void seekToBeginning() throws IOException {
        sampleReader.seek(0);
        track.flush();
    }

    /**
     * @return Total length of current audio track in seconds.
     */
    public int audioLength() {
        return Math.round(sampleReader.length() / 2 / 44100);
    }

    /**
     * @return current position of playback in percent
     * from 0-100
     */
    public int currPositionPercent() {
        return (int) Math.round((double) sampleReader.getPosition() / (double) sampleReader.length() * 100);
    }
}


