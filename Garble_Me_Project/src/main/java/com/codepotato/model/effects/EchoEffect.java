package com.codepotato.model.effects;

/**
 * Created by michael on 4/11/14.
 *
 * EchoEffect class is a user friendly wrapper for Delay.
 *
 */

public class EchoEffect extends TimeBasedEffect
{
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
}
