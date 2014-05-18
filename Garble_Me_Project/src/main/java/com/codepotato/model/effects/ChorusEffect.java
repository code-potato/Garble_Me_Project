package com.codepotato.model.effects;

/**
 * @author Michael Santer
 * Chorus Effect is short delay (35ms) that fluctuates up and down
 * on a sin wave pattern. This makes the audio sound as though the pitch is shifting
 * up and down during playback.
 */
public class ChorusEffect extends TimeBasedEffect
{
    private SinWave sin;
    private double minDelay; // ms
    private double depth; // 0-50 ms
    private double rate; // 0-5 hz

    final private double MAX_DEPTH = 50;
    final private double MAX_RATE = 5;

    private double tempDelay; // setting as an instance variable for efficiency reasons.

    /**
     * Initialize effect with a set of default parameters.
     * These parameters can be retrieved using the get methods.
     */
    public ChorusEffect()
    {
        name = "Chorus";
        rate = 2;
        depth = 5.;
        minDelay = 35.;

        delayTime = minDelay + depth;
        wetGain = .7;
        dryGain = .3;
        feedbackGain = 0.;
        delaySamples = convertMilliSecsToSamples(delayTime);

        delay = new Delay(2 * delaySamples); //delay buffer is twice delay time
        delay.setDelayAmt(delaySamples);
        delay.setDryGain(dryGain);
        delay.setWetGain(wetGain);
        delay.setFeedbackGain(feedbackGain);

        sin = new SinWave(rate, Math.PI/2., sampleRate);
    }

    @Override
    /**
     * Changes the delay based on rate and depth, and then calls the
     * tick() method on Delay.
     */
    public double tick(double inputSample) {
        tempDelay = depth/2. * (sin.tick() + 1.) + minDelay;
        delay.setDelayLineDelay(tempDelay * sampleRate / 1000.);
        return delay.tick(inputSample);
    }

    /**
     *
     * @return depth as a percent of max depth
     */
    public int getDepth() {
        return (int) (depth/MAX_DEPTH*100);
    }

    /**
     * Depth represents the amount in which the delay changes.
     * Ex: If depth is 20, the delay will fluctuate between 35ms and 55ms.
     * @param percent
     * Set depth as a percent from 0-100 of max Depth.
     * Max Depth is 50.
     */
    public void setDepth(int percent) {
        depth = MAX_DEPTH * (double)percent / 100.;
    }

    /**
     *
     * @return rate as a percent of max rate.
     */
    public int getRate() {
        return (int) (rate/MAX_RATE*100);
    }

    /**
     * Rate represents the frequency in which the delay is changed (fast or slow).
     * @param percent
     * Set rate as a percent from 0-100 of max rate.
     * Max rate is 5.
     */
    public void setRate(int percent) {
        rate = MAX_RATE * (double)percent / 100.;
        sin.setSinFreq(rate);
    }
}
