package com.codepotato.model.effects;

/**
 * Created by michael on 4/17/14.
 */
public class ChorusEffect extends TimeBasedEffect
{
    private SinWave sin;
    private double minDelay; // ms
    private double depth; // 0-50 ms
    private double rate; // 0-5 hz

    private double tempDelay; // setting as an instance variable for efficiency reasons.

    public ChorusEffect()
    {
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
    public double tick(double inputSample) {
        tempDelay = depth/2. * (sin.tick() + 1.) + minDelay;
        delay.setDelayLineDelay(tempDelay * sampleRate / 1000.);
        return delay.tick(inputSample);
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
        sin.setSinFreq(rate);
    }
}
