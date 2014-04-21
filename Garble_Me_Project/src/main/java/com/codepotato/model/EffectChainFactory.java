package com.codepotato.model;

/**
 * Created by David on 4/20/2014.
 */

//Factory class for EffectChain: keeps track of whether an instance of an EffectChain already exists
public class EffectChainFactory {
    private static EffectChain instance = null;

    public static EffectChain initEffectChain() {
        if (instance == null) {
            instance = new EffectChain();
        }
        return instance;
    }
}


