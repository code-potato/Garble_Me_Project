package com.codepotato.model.effects;

/**
 * Created by michael on 4/12/14.
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
