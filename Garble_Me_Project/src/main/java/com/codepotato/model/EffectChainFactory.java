package com.codepotato.model;

/**
 * Factory class for EffectChain: keeps track of whether an instance of an EffectChain already exists
 * Preserves a singleton instance of EffectChain, used in Player, Recorder, and FileManager
 * @author David Hua on 4/20/2014.
 */

public class EffectChainFactory {
    private static EffectChain instance = null;

    /**
     * Checks if an EffectChain has been initialized
     * @return instance of EffectChain
     */
    public static EffectChain initEffectChain() {
        if (instance == null) {
            instance = new EffectChain();
        }
        return instance;
    }
}


