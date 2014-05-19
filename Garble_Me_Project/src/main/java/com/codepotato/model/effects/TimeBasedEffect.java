package com.codepotato.model.effects;

/**
 * TimeBasedEffect is an abstract base class for any effect that can be
 * considered "time based".
 * Time based effects all require a delay as an elementary component.
 *
 * @author Michael Santer
 * @see EchoEffect, ChorusEffect, and FlangerEffect
 */
abstract public class TimeBasedEffect extends Effect {
    protected Delay delay;
    protected double delayTime; // in milliseconds

    protected int delaySamples;
    protected double wetGain;
    protected double dryGain;
    protected double feedbackGain;

    protected final double MAX_DELAY_TIME = 1000;
    protected final double MAX_GAIN = 1;

    /**
     * Delay time is the amount of time between echos.
     * The Max Delay Time is 1000ms.
     *
     * @return Delay time as a percent of the MAX_DELAY_TIME from 0 - 100.
     */
    public int getDelayTime() {
        return (int) (delayTime / MAX_DELAY_TIME * 100);
    }

    /**
     * Delay time is the amount of time between echos.
     * The Max Delay Time is 1000ms.
     * Given a percent from 0-100, sets the delay time.
     *
     * @param percent
     */
    public void setDelayTime(int percent) {
        delayTime = MAX_DELAY_TIME * (double) percent / 100.;
        delaySamples = convertMilliSecsToSamples(this.delayTime);
        delay.setDelayAmt(delaySamples);
    }

    /**
     * Wet Gain is the volume of the affected sound.
     *
     * @return as a percent from 0-100 of the MaxGain
     */
    public int getWetGain() {
        return (int) (wetGain / MAX_GAIN * 100);
    }

    /**
     * Wet Gain is the volume of the affected sound.
     * Set wetGain given a percent from 0-100
     *
     * @param percent
     */
    public void setWetGain(int percent) {
        wetGain = MAX_GAIN * (double) percent / 100.;
        delay.setWetGain(this.wetGain);
    }

    /**
     * Dry gain is the volume of the un-affected (original) sound.
     *
     * @return dry gain as a percent from 0-100 of the MaxGain
     */
    public int getDryGain() {
        return (int) (dryGain / MAX_GAIN * 100);
    }

    /**
     * Dry gain is the volume of the un-affected (original) sound.
     * Set dryGain given a percent from 0-100
     *
     * @param percent
     */
    public void setDryGain(int percent) {
        dryGain = MAX_GAIN * (double) percent / 100.;
        delay.setDryGain(this.dryGain);
    }

    /**
     * Feedback gain is the amount in which the affected sound
     * gets quieter and disappears. With a feedback gain of 0, there
     * will be no repeats of the effected sound. With a feedback gain of 100,
     * the effected sound will repeat forever.
     *
     * @return feedbackGain as a percent of the MaxGain
     */
    public int getFeedbackGain() {
        return (int) (feedbackGain / MAX_GAIN * 100);
    }

    /**
     * Feedback gain is the amount in which the affected sound
     * gets quieter and disappears. With a feedback gain of 0, there
     * will be no repeats of the effected sound. With a feedback gain of 100,
     * the effected sound will repeat forever.
     * Set feedback given a percent from 0-100 of the MaxGain
     *
     * @param percent
     */
    public void setFeedbackGain(int percent) {
        feedbackGain = MAX_GAIN * (double) percent / 100.;
        delay.setFeedbackGain(this.feedbackGain);
    }

    protected int convertMilliSecsToSamples(double milliSecs) {
        return (int) (milliSecs * sampleRate / 1000);
    }
}
