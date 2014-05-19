package com.codepotato.model.effects;

import android.util.Log;

/**
 * Flanger is a very short delay, usually less than 15ms. Just like Chorus,
 * the delay fluctuates up and down on a sin wave pattern. When this is done
 * with such a short delay, it creates what some people call the "airplane" effect.
 * It can also create a sort of robotic sound.
 *
 * @author Michael Santer
 */
public class FlangerEffect extends TimeBasedEffect {
    private SinWave sin;
    private double minDelay;
    private double depth;
    private double rate;

    final private double MAX_DEPTH = 15;
    final private double MAX_RATE = 3;

    private double tempDelay; // setting as an instance variable for efficiency reasons.

    /**
     * Initialize effect with a set of default parameters.
     * These parameters can be retrieved using the get methods.
     */
    public FlangerEffect() {
        name = "Flanger";
        rate = 1;
        depth = 5.;
        minDelay = 0.;

        delayTime = minDelay + depth;
        wetGain = .7;
        dryGain = 0.5;
        feedbackGain = 0.7;
        delaySamples = convertMilliSecsToSamples(delayTime);

        delay = new Delay(2 * delaySamples); //delay buffer is twice delay time
        delay.setDelayAmt(delaySamples);
        delay.setDryGain(dryGain);
        delay.setWetGain(wetGain);
        delay.setFeedbackGain(feedbackGain);

        sin = new SinWave(rate, Math.PI / 2., sampleRate);
    }


    /**
     * Changes the delay based on rate and depth, and then calls the
     * tick() method on Delay.
     */
    @Override
    public double tick(double inputSample) {
        tempDelay = depth / 2. * (sin.tick() + 1.) + minDelay;
        delay.setDelayLineDelay(tempDelay * sampleRate / 1000.);
        return delay.tick(inputSample);
    }

    /**
     * @return depth as a percent of max depth
     */
    public int getDepth() {
        return (int) (depth / MAX_DEPTH * 100);
    }

    /**
     * Depth represents the amount in which the delay changes.
     * Ex: If depth is 10, the delay will fluctuate between 0ms and 10ms.
     *
     * @param percent Set depth as a percent from 0-100 of max Depth.
     *                Max Depth is 50.
     */
    public void setDepth(int percent) {
        depth = MAX_DEPTH * (double) percent / 100.;
    }

    /**
     * @return rate as a percent of max rate.
     */
    public int getRate() {
        return (int) (rate / MAX_RATE * 100);
    }

    /**
     * Rate represents the frequency in which the delay is changed (fast or slow).
     *
     * @param percent Set rate as a percent from 0-100 of max rate.
     *                Max rate is 5.
     */
    public void setRate(int percent) {
        rate = MAX_RATE * (double) percent / 100.;
        sin.setSinFreq(rate);
        Log.d("Flange", "rate = " + rate);
    }
}
