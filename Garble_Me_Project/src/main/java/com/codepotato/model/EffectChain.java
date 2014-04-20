package com.codepotato.model;

import com.codepotato.model.effects.*;
import java.util.*;

/**
 * Created by David on 4/16/2014.
 */

//def chainsize 10

public class EffectChain {

    private ArrayList<Effect> effectList;
    private int numOfEffects;


    public EffectChain(){
        effectList = new ArrayList<Effect>();
        numOfEffects = 0;
    }

    public void addEffect(Effect eff){ //to end of array list
        effectList.add(eff);
        numOfEffects++;
    }

    //NOTE: //effect ids are arbitrary as long as each effect has a unique id
    public void removeEffect (int effID){

        boolean idFound = false; //check for exception (id does not exist)
        for(int i=0; i<effectList.size(); i++){
            if(effectList.get(i).getId() == effID){
                effectList.set(i, null); //remove()
                numOfEffects--;
                idFound = true;
                break;
            }
        }

        //went though entire effect list, didn't find effID
        //should never bump into this error during app run
        if(!idFound){
            System.out.println("Error: Effect ID not found.");
        }
    }

    /* this remove function is for manually deleting an effect from its index in list
    public void removeEffect (int index){
        if(index >= numOfEffects{
            System.out.println("Error: Array out of bounds");
        }
        else{
            effectList.set(index,null); //remove()
            numOfEffects--;
        }
    }

    */
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

    public double tickAll(double input) {
        for(Effect eff : effectList){
            if(eff == null){
                continue;
            }
            input = eff.tick(input);
        }
        return input;
    }
}
