package com.codepotato.model;

import com.codepotato.model.effects.*;
import java.util.*;

/**
 * Maintains the list of audio effects being applied to the recording
 *
 * @author David Hua on 4/16/2014
 *
 */

public class EffectChain {

    private static int id_count = 0; //used to set unique ID for effect
    private ArrayList<Effect> effectList;
    private int numOfEffects;

    public EffectChain(){
        effectList = new ArrayList<Effect>();
        numOfEffects = 0;
    }

    public int getNumOfEffects(){ //we may never use this...?
        return numOfEffects;
    }

    /**
     * @param eff The effect being added to the list of effects in EffectChain
     * @return int, effect's ID used in view manipulation (displaying what effects exist)
     */
    public int addEffect(Effect eff){ //to end of array list
        eff.setId(id_count);  //effect ids are arbitrary as long as each effect has a unique id
        effectList.add(eff);

        id_count++;
        numOfEffects++;

        return eff.getId();
    }

    /**
     * @param effID The ID of the effect being removed from EffectChain
     * @return boolean, used to check if effect ID was removed or not
     */
    public boolean removeEffect (int effID){

        boolean idFound = false; //check for exception (id does not exist)
        for(int i=0; i<effectList.size(); i++){
            if(effectList.get(i).getId() == effID){
                effectList.remove(i); //remove effect from chain/list
                numOfEffects--;
                idFound = true; //effID has been found, and effect has been dealt with
                break;
            }
        }

        // if didn't find effID -> idFound = false
        // else idFound = true (set during search in effect chain)
        return idFound;
    }

    /**
     * Gets an effect in EffectChain, given the effID
     *
     * @param effID The ID of the effect being
     * @return Effect or null if not found
     */

    public Effect getEffect(int effID){

        for(Effect eff : effectList){
            if(eff.getId() == effID){
                return eff; //returns effect with matching effect ID
            }
        }
        //went though entire effect list, didn't find effID
        //should never bump into this error during app run
        return null;
    }

    /**
     * Deletes all effects in EffectChain
     */
    public void deleteAllEffects(){
        for(Effect eff : effectList){
            removeEffect(eff.getId());
        }
    }

    /**
     * Ticks/applies all of the effects in EffectChain into the input
     *
     * @param input The input sample (a small section of the recording)
     *		        which is modified by "ticking"/applying all of the effects
     * 				in the EffectChain's list of effects
     * @return input The same input sample, but modified after going through here
     */
    public double tickAll(double input) {
        for(Effect eff : effectList){
            input = eff.tick(input);
        }
        return input;
    }
}
