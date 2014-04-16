package com.codepotato.model.effects;

/**
 * Created by michael on 4/12/14.
 */
public abstract class Effect {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract double tick(double inputSample);

}
