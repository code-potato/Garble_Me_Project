package com.codepotato.model.effects;

/**
 * @author Michael Santer
 * SinWave allows for the stepping through of sin wave.
 * Used by chorus and flanger to alternate delay time.
 * This could also be used to generate a sin wave tone.
 */
class SinWave {

    long currTimeIndex;  // internal time index n; initialized to 0
    double frequency;    // current frequency
    double initialPhase; // initial phase phi
    double sampleRate;

    double out; // declared as instance variable for efficiency.

    public SinWave(){
        currTimeIndex = 0;
        frequency = 440;
        initialPhase = 0;
        sampleRate = 44100;
    }

    SinWave(double freq){
        currTimeIndex = 0;
        frequency = freq;
        initialPhase = 0;
        sampleRate = 44100;
    }

    SinWave(double freq, double initPhase, double sr){
        currTimeIndex = 0;
        frequency = freq;
        initialPhase = initPhase;
        sampleRate = sr;
    }

    void setSinFreq(double freq){
        frequency = freq;
    }

    double getSinFreq(){
        return frequency;
    }

    void setSinInitPhase(double phase){
        initialPhase = phase;
    }

    double getSinInitPhase(){
        return initialPhase;
    }

    void setSampleRate(double sr){
        sampleRate = sr;
    }

    /**
     * @return the next sin wav value.
     */
    double tick(){
        out = Math.sin(2.*3.14*frequency*((double)currTimeIndex/sampleRate) + initialPhase);
        currTimeIndex++;
        return out;
    }
}
