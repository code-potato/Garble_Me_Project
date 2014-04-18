package com.codepotato.controller;

import com.codepotato.model.*;
import com.codepotato.model.effects.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by David on 4/16/2014.
 */
public class AudioController {
    private Player audioPlayer;
    private EffectChain effectChain;

    public AudioController(File filename){
        try {
            audioPlayer = new Player(filename);
        }catch(Exception IOException){
            System.out.println("ERROR: File does not exist!");
        }
//        effectChain = new EffectChain(filename);  //TO DO: MAKE EFFECTCHAINFACTORY FOR SINGLETON OBJECT SHARING
    }

    public void addEffect(Effect eff){
        effectChain.addEffect(eff);
    }

    public void removeEffect(int effID){
        effectChain.removeEffect(effID);
    }

    public void play(){
        audioPlayer.play();
    }

    public void pause(){
        audioPlayer.pause();;
    }

    public boolean isPlaying(){
        return audioPlayer.isPlaying();
    }


}
