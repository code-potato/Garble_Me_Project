package com.codepotato.controller;



import com.codepotato.model.EffectChain;
import com.codepotato.model.EffectChainFactory;
import com.codepotato.model.Player;
import com.codepotato.model.effects.Effect;

import java.io.File;
import java.io.IOException;

/**
 * Created by David on 4/16/2014.
 */
public class AudioController {
    private Player audioPlayer;
    private EffectChain effectChain;

    /* AudioController: creates new instance of Player and EffectChain */
    public AudioController(File filename){

        effectChain = EffectChainFactory.initEffectChain(); //new EffectChain(filename);

        try {
            initPlayer(filename);
        }catch(Exception IOException){
            System.out.println("ERROR: File does not exist!"); //choose another file? how to do this in Android?
        }
    }

    public void initPlayer(File filename) throws IOException{
        audioPlayer = new Player(filename); //(re)initializes player with a File
        effectChain.deleteAllEffects();     //(re)"initializes" effectChain
    }

    public int addEffect(Effect eff){
        return effectChain.addEffect(eff);
    }

    public Effect getEffect(int id){
        return effectChain.getEffect(id);
    }

    public boolean removeEffect(int effID){
        return effectChain.removeEffect(effID); //returns true if effect's ID has been found
    }                                           // with effect being removed

    public void play(){
        audioPlayer.play();
    }

    public void pause(){
        audioPlayer.pause();
    }

    public boolean isPlaying(){
        return audioPlayer.isPlaying();
    }

    public void seekPlayer(int location) throws IOException {
        audioPlayer.seek(location);
    }

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
    public int currPosition(){
        return audioPlayer.currPositionPercent();
    }

}
