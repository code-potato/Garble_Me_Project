package com.codepotato.model.effects;

/**
 * EchoEffect class is a user friendly wrapper for Delay.
 * It is an effect that, basically, saves samples for later playback.
 * Depending on the delay amount, it will playback the current input sound along with
 * a sound that was previously played and stored.
 *
 * @author Michael Santer
 */

public class EchoEffect extends TimeBasedEffect {
    /**
     * Initialize effect with a set of default parameters.
     * These parameters can be retrieved using the get methods.
     */
    public EchoEffect() {
        name = "Echo";

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

    /**
     * Given an audio sample as input, it combines
     * the input*drygain with the previous echo*feedbackGain
     * and the current echo*wetgain.
     * This creates a basic echo effect.
     *
     * @param input sample.
     * @return affected audio sample.
     */
    @Override
    public double tick(double input) {
        return delay.tick(input);
    }
}
