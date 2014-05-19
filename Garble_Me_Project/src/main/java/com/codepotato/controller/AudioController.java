package com.codepotato.controller;

import com.codepotato.model.EffectChain;
import com.codepotato.model.EffectChainFactory;
import com.codepotato.model.Player;
import com.codepotato.model.effects.Effect;

import java.io.File;
import java.io.IOException;

/**
 * Controller class, used as the centerpiece for view to use
 * Contains a Player and an EffectChain, instantiated 
 * @author David Hua on 4/16/2014.
 */
public class AudioController {
    private Player audioPlayer;
    private EffectChain effectChain;

    /**
     * AudioController: creates new instance of Player and EffectChain
     * @param filename File used to import the recording into the player
     */
    public AudioController(File filename){

        effectChain = EffectChainFactory.initEffectChain(); //new EffectChain(filename);

        try {
            initPlayer(filename);
        }catch(Exception IOException){
            System.out.println("ERROR: File does not exist!"); //choose another file? how to do this in Android?
        }
    }

    /**
     * Initializes new player, and reinitializes EffectChain
     * @param filename File used to import the recording into the player
     */
    public void initPlayer(File filename) throws IOException{
        audioPlayer = new Player(filename); //(re)initializes player with a File
        effectChain.deleteAllEffects();     //(re)"initializes" effectChain
    }

    /**
     * Calls on EffectChain's addEffect (which adds an effect to list of Effects in EffectChain)
     * @param eff Effect to be added
     */
    public int addEffect(Effect eff){
        return effectChain.addEffect(eff);
    }

    /**
     * Calls on EffectChain's getEffect (which gets an effect from list of Effects in EffectChain)
     * @param id Effect's ID, used to identify the Effect being requested from the list of effects
     * @return Effect being searched
     */
    public Effect getEffect(int id){
        return effectChain.getEffect(id);
    }

    /**
     * Calls on EffectChain's removeEffect (which removes an effect from list of Effects in EffectChain)
     * @param effID Effect's ID, used to identify the Effect being removed from the list of effects
     * @return boolean Checking if Effect was found in EffectChain, and removed (null if not found)
     */
    public boolean removeEffect(int effID){
        return effectChain.removeEffect(effID); //returns true if effect's ID has been found
    }                                           // with effect being removed


    /**
     * Calls on audioPlayer's play (which creates and runs new thread for playback)
     */
    public void play(){
        audioPlayer.play();
    }


    /**
     * Calls on audioPlayer's pause (which kill playback thread)
     */
    public void pause(){
        audioPlayer.pause();
    }

    /**
     * Calls on audioPlayer's isPlaying (which checks if Player is playing or paused
     * @return boolean to check if player is playing or paused
     */
    public boolean isPlaying(){
        return audioPlayer.isPlaying();
    }

    /**
     * @param location
     * Called from audioPlayer's seek()
     * Given a location between 0 and 100 %, seek() will move playback
     * to the appropriate position in the audio file.
     * @throws IOException
     */
    public void seekPlayer(int location) throws IOException {
        audioPlayer.seek(location);
    }

    /**
     * Calls on audioPlayer's seekToBeginning()
     * A convenient method that returns the playback to the beginning.
     * @throws IOException
     */
    public void returnPlayerToBeginning() throws IOException {
        audioPlayer.seekToBeginning();
    }

    /**
     * @return length of current audio track, in seconds
     */
    public int audioLength()
    {
        return audioPlayer.audioLength();
    }

    /**
     * @return current position of playback in percent
     * from 0-100
     */
    public int currAudioPosition(){
        return audioPlayer.currPositionPercent();
    }
}
