package com.codepotato.model.effects;

/**
 * @author Michael Santer
 * Delay is the elementary unit for all of the time based effects.
 * It is an effect that, basically, saves samples for later playback.
 * Depending on the delay amount, it will playback the current input sound along with
 * a sound that was previously played and stored.
 */
public class Delay {

    private DelayLine delayBuf;
    private double wetGain;
    private double dryGain;
    private double feedbackGain;

    /**
     * Sets the amount of Delay, in samples.
     * @param inDelay
     */
    public Delay(int inDelay)
    {
        delayBuf = new DelayLine(inDelay);
    }

    /**
     * Change the delay amount.
     * Less efficient than setDelayLineDelay()
     * Use this method if the delay needs to be increased by more than double.
     * @param newDelay number of samples
     */
    public void setDelayAmt(double newDelay)
    {
        delayBuf = new DelayLine((int)(newDelay * 2));
        delayBuf.setDelayLineDelay(newDelay);
    }

    /**
     * Changes the delay amount.
     * More efficient than setDelayAmt().
     * Use this method if the delay needs to be increased by less than double.
     * @param newDelay
     */
    public void setDelayLineDelay(double newDelay)
    {
        delayBuf.setDelayLineDelay(newDelay);
    }

    /**
     * @return delay amount in samples.
     */
    public double getDelay()
    {
        return delayBuf.getDelayLineDelay();
    }

    public void setWetGain(double newGain)
    {
        wetGain = newGain;
    }

    public double getWetGain()
    {
        return wetGain;
    }

    public void setDryGain(double newGain)
    {
        dryGain = newGain;
    }

    public double getDryGain()
    {
        return dryGain;
    }

    public void setFeedbackGain(double newGain)
    {
        feedbackGain = newGain;
    }

    public double getFeedbackGain()
    {
        return feedbackGain;
    }


    /**
     * Given an audio sample as input, it combines
     * the input*drygain with the previous echo*feedbackGain
     * and the current echo*wetgain.
     * This creates a basic echo effect.
     * @param input
     * @return the affected sample.
     */
    public double tick(double input)
    {
        double output;
        double temp = feedbackGain * delayBuf.getCurrentOut();
        output = delayBuf.tick(input + temp);
        output = (input * dryGain) + (output * wetGain);

        //adjust for clipping
        if (output > 1.0)
            output = 1.0;
        else if (output < -1.0)
            output = -1.0;

        return output;
    }
}
