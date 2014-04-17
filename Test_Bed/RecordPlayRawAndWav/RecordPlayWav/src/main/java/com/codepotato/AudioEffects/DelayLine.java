package com.codepotato.AudioEffects;

import android.util.Log;

/**
 * Created by michael on 3/18/14.
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

    public double tick(double input)
    {
        double output;

        buffer[currentIndex] = input;
        output = getCurrentOut();

        currentIndex = (currentIndex + 1) % (maxDelay + 1);
        return output;

    }

}
