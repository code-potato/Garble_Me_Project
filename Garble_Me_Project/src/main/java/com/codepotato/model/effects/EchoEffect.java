package com.codepotato.model.effects;

import  com.codepotato.model.Effect;

/**
 * Created by michael on 4/11/14.
 *
 * EchoEffect class is a user friendly wrapper for Delay.
 *
 */

public class EchoEffect extends Effect
{
    private Delay delay;
    private double delayTime; // in milliseconds
    private int delaySamples;
    private double wetGain;
    private double dryGain;
    private double feedbackGain;
    final private int sampleRate = 44100;

    public EchoEffect()
    {
        delayTime = 200;
        wetGain = 0.8;
        dryGain = 1.0;
        feedbackGain = .5;
        delaySamples = convertMilliSecsToSamples(delayTime);

        delay = new Delay(2 * delaySamples); //delay buffer is twice delay time
        delay.setDelayAmt(delaySamples);
        delay.setDryGain(dryGain);
        delay.setWetGain(wetGain);
        delay.setFeedbackGain(feedbackGain);
    }

    public double tick(double input)
    {
        return delay.tick(input);
    }

    public double getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(double delayTime) {
        this.delayTime = delayTime;
        delaySamples = convertMilliSecsToSamples(this.delayTime);
        delay.setDelayAmt(delaySamples);
    }

    public double getWetGain() {
        return wetGain;
    }

    public void setWetGain(double wetGain) {
        this.wetGain = wetGain;
        delay.setWetGain(this.wetGain);
    }

    public double getDryGain() {
        return dryGain;
    }

    public void setDryGain(double dryGain) {
        this.dryGain = dryGain;
        delay.setDryGain(this.dryGain);
    }

    public double getFeedbackGain() {
        return feedbackGain;
    }

    public void setFeedbackGain(double feedbackGain) {
        this.feedbackGain = feedbackGain;
        delay.setFeedbackGain(this.feedbackGain);
    }

    private int convertMilliSecsToSamples(double milliSecs)
    {
        return (int) (milliSecs * sampleRate / 1000);
    }

}
