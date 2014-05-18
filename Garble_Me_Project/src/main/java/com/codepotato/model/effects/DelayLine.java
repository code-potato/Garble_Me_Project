package com.codepotato.model.effects;

import android.util.Log;

/**
 * @author Michael Santer
 * The basic unit for the delay.
 * Saves audio samples into a buffer for later playback.
 */
public class DelayLine
{
    private double[] buffer;
    private int maxDelay;
    private double currentDelay;
    private int currentIndex;

    public DelayLine()
    {
        maxDelay = 1024;
        buffer = new double[maxDelay+2];
        currentDelay = maxDelay;
        currentIndex = 0;
    }

    public DelayLine(int maxDelay)
    {
        this.maxDelay = maxDelay;
        buffer = new double[maxDelay+2];
        currentDelay = maxDelay;
        currentIndex = 0;
    }

    public void setDelayLineDelay(double newDelay)
    {
        if (newDelay > maxDelay) {
            Log.d("delayLine", "Error: setting delay greater than max delay...");
            return;
        }
        else {
            currentDelay = newDelay;
        }
    }

    public double getDelayLineDelay()
    {
        return currentDelay;
    }

    /**
     * @return
     * Returns current output sample, based on the delay length.
     * If current playback index is 300, and the delay is 200, then return the
     * sample stored at index 100.
     */
    public double getCurrentOut()
    {
        double delayIndex;
        double output;

        delayIndex = currentIndex - currentDelay;
        if (delayIndex < 0.0)
            delayIndex = delayIndex + ((double) maxDelay + 1.0);

        if (delayIndex > (double) maxDelay) {
            output = buffer[(int) delayIndex] + (buffer[0] - buffer[(int) delayIndex]) * (delayIndex - (double) ((int) delayIndex));
        }
        else {
            output = buffer[(int) delayIndex] + (buffer[(int) delayIndex + 1] - buffer[(int) delayIndex]) * (delayIndex - (double) ((int) delayIndex));
        }

        //resolve clipping issues
        if (output > 1.0)
            output = 1.0;
        else if (output < -1.0)
            output = -1.0;

        return output;
    }


    /**
     * Saves input sample into buffer, and returns the current output sample.
     * @param input
     * @return current output sample
     */
    public double tick(double input)
    {
        double output;

        buffer[currentIndex] = input;
        output = getCurrentOut();

        currentIndex = (currentIndex + 1) % (maxDelay + 1);
        return output;

    }

}
