package com.codepotato.model.effects;

/**
 * @author Michael Santer
 * Abstract base class for all effects.
 * All effects must have a tick method and an id.
 */
public abstract class Effect {

    final protected int sampleRate = 44100;

    protected int id;
    protected String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Given an audio sample, tick should make the appropriate
     * changes (depending on the effect) to the sample, and return it.
     * @param inputSample
     * @return
     */
    public abstract double tick(double inputSample);

    public String getName() {
        return name;
    }
}
