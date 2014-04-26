package com.codepotato.model.effects;

/**
 * Created by michael on 4/12/14.
 */
abstract public class TimeBasedEffect extends Effect
{
    protected Delay delay;
    protected double delayTime; // in milliseconds

    protected int delaySamples;
    protected double wetGain;
    protected double dryGain;
    protected double feedbackGain;

    protected final double MAX_DELAY_TIME = 2000;
    protected final double MAX_GAIN = 1;

    public int getDelayTime() {
        return (int) (delayTime / MAX_DELAY_TIME * 100);
    }

    public void setDelayTime(int percent) {
        delayTime = MAX_DELAY_TIME * (double)percent / 100.;
        delaySamples = convertMilliSecsToSamples(this.delayTime);
        delay.setDelayAmt(delaySamples);
    }

    public int getWetGain() {
        return (int) (wetGain / MAX_GAIN * 100);
    }

    public void setWetGain(int percent) {
        wetGain = MAX_GAIN * (double)percent / 100.;
        delay.setWetGain(this.wetGain);
    }

    public int getDryGain() {
        return (int) (dryGain / MAX_GAIN * 100);
    }

    public void setDryGain(int percent) {
        dryGain = MAX_GAIN * (double)percent / 100.;
        delay.setDryGain(this.dryGain);
    }

    public int getFeedbackGain() {
        return (int) (feedbackGain / MAX_GAIN * 100);
    }

    public void setFeedbackGain(int percent) {
        feedbackGain = MAX_GAIN * (double)percent / 100.;
        delay.setFeedbackGain(this.feedbackGain);
    }

    protected int convertMilliSecsToSamples(double milliSecs)
    {
        return (int) (milliSecs * sampleRate / 1000);
    }
}
