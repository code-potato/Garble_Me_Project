package com.codepotato.model;

import com.codepotato.model.effects.*;

/**
 * Created by David on 4/20/2014.
 */

//factory for effect chain keeps track if an instance of an effectChain already exists
public class EffectChainFactory {
    private static EffectChain instance = null;

    public static void init() {
        if (instance == null) {
            instance = new EffectChain();
        }
    }

    public static void addEffect(Effect eff){
        instance.addEffect(eff);
    }

    public static void removeEffect(int effID){
        instance.removeEffect(effID);
    }

    public static Effect getEffect(int index){
        return instance.getEffect(index);
    }

    public static double tickAll(double input){
        return instance.tickAll(input);
    }


}


