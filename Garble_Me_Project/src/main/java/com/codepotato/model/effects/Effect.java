package com.codepotato.model.effects;

/**
 * Abstract base class for all effects.
 * All effects must have a tick method and an id.
 *
 * @author Michael Santer
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

    public abstract double tick(double inputSample);

    public String getName() {
        return name;
    }
}
