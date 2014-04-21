package com.codepotato.model;

import com.codepotato.model.effects.*;
import java.util.*;

/**
 * Created by David on 4/16/2014.
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

    public int addEffect(Effect eff){ //to end of array list
        eff.setId(id_count);  //effect ids are arbitrary as long as each effect has a unique id
        effectList.add(eff);

        id_count++;
        numOfEffects++;

        return eff.getId();
    }

    //RETURN TYPE: boolean, used to check if effect ID was removed or not
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

    /* this remove function is for manually deleting an effect from its index in list
    public void removeEffect (int index){
        if(index >= numOfEffects{
            System.out.println("Error: Array out of bounds");
        }
        else{
            effectList.remove(index); //remove()
            numOfEffects--;
        }
    }

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

    /*  this get function is for manually getting an effect from its index in list
    public Effect getEffect(int index){
        if(index >= numOfEffects){
            //index over array size
            System.out.println("Error: Trying to access index beyond number given.");
            return null;
        }

        else {
            return effectList.get(index);
        }
    }
    */

    public void deleteAllEffects(){
        for(Effect eff : effectList){
            removeEffect(eff.getId());
        }
    }

    public double tickAll(double input) {
        for(Effect eff : effectList){
            input = eff.tick(input);
        }
        return input;
    }
}
