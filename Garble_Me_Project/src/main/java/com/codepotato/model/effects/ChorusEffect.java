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

    final private double MAX_DEPTH = 50;
    final private double MAX_RATE = 5;

    private double tempDelay; // setting as an instance variable for efficiency reasons.

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
    public double tick(double inputSample) {
        tempDelay = depth/2. * (sin.tick() + 1.) + minDelay;
        delay.setDelayLineDelay(tempDelay * sampleRate / 1000.);
        return delay.tick(inputSample);
    }

    public int getDepth() {
        return (int) (depth/MAX_DEPTH*100);
    }

    public void setDepth(int percent) {
        depth = MAX_DEPTH * (double)percent / 100.;
    }

    public int getRate() {
        return (int) (rate/MAX_RATE*100);
    }

    public void setRate(int percent) {
        rate = MAX_RATE * (double)percent / 100.;
        sin.setSinFreq(rate);
    }
}
