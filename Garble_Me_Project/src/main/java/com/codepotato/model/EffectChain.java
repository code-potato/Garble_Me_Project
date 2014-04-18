package com.codepotato.model;

import com.codepotato.model.effects.*;


/**
 * Created by David on 4/16/2014.
 */

//def chainsize 10

public class EffectChain {
    private final int CHAINSIZE = 10;

    private Effect[] effectList;
    private int numOfEffects;


    public EffectChain(){
        effectList = new Effect[CHAINSIZE];
        numOfEffects = 0;
    }

    public void addEffect(Effect eff){ //to end of array list
        if(numOfEffects == CHAINSIZE){
            System.out.println("Error: Effect chain is full!");
        }
        else {
            effectList[numOfEffects] = eff;
            numOfEffects++;
        }
    }

    //NOTE: //effect ids are arbitrary as long as each effect has a unique id
    public void removeEffect (int effID){

        boolean idFound = false; //check for exception (id does not exist)
        for(int i=0; i<numOfEffects; i++){
            if(effectList[i].getId() == effID){
                for(int j = i; j<numOfEffects-1; j++){
                    effectList[j] = effectList[j+1];
                }
                effectList[numOfEffects-1] = null; //remove()
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
            for(int i = index; i<numOfEffects-1; i++){
                effectList[i] = effectList[i+1];
            }
            effectList[numOfEffects-1] = null; //remove()
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
            return effectList[index];
        }
    }

    public double tickAll(double input) {
        for(int i = 0; i < numOfEffects; i++){
            input = effectList[i].tick(input);
        }
        /*
        for (each Effect effect : effectList){
            input = effect.tick(input);
        }*/
        return input;
    }
}
