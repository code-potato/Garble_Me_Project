package com.codepotato.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Michael Santer
 *
 * SampleReader is responsible for reading data from an audio file
 * in the form of samples (type double).
 * Used by Player.
 */
public class SampleReader
{

    private int sampleRate;  // Hz
    private int bitResolution; // 8 or 16
    private int numChannels; // 1 or 2

    private File audioFile;
    private FileInputStream fis;
    private BufferedInputStream inputStream;
    private byte[] byteBuff;
    private long position;

    /**
     * Constructor opens an audio file for reading.
     * @param file an audio file.
     * @param sampleRate in Hz
     * @param bitRate 8 or 16
     * @param numChan 1 or 2
     * @throws IOException
     */
    public SampleReader (File file, int sampleRate, int bitRate, int numChan) throws IOException
    {
        audioFile = file;

        this.sampleRate = sampleRate;
        bitResolution = bitRate;
        numChannels = numChan;

        fis = new FileInputStream(audioFile);
        inputStream = new BufferedInputStream(fis);

        position = 0;

        byteBuff = new byte[2];
    }

    /**
     * @return length of audio file in bytes
     */
    public long length() {
        return audioFile.length();
    }

    /**
     *
     * @return number of available bytes to read.
     * @throws IOException
     */
    public int available() throws IOException {
        return inputStream.available();
    }

    /**
     *
     * @return
     * The next available sample. If no samples are available, it returns 0.
     * @throws IOException
     */
    public double nextSample() throws IOException
    {
        if (inputStream.available() > 0){
            inputStream.read(byteBuff, 0, 2); //returns -1 end of stream. Not doing anything with this.
            position += 2;
        }
        else {
            byteBuff[0] = 0; byteBuff[1] = 0;
        }

        //                Low Byte          MASK        High Byte     Left Shift
        double sample= ((byteBuff[0] & 0xFF) | (byteBuff[1] << 8) );
        /*Explanation: The first part, we take the Low Byte and use an AND mask of 1111 1111 to ensure that we just get
        * the first 8 bits(NOTE: this is redundant since a byte is 8 bits to begin with). (1011 0000) & (1111 1111)= 1011 0000
        * NEXT: We take the High Byte and shift it 8 Bytes to the left. In Java, this promotes it to an integer(32 bits).
        * Next we merge the int representing High Byte and the Low Byte with a bitwise OR operator
        * 00000000 0000000 10101111 00000000
        *                       OR  10110000 =
        * 00000000 0000000 10101111 10110000 Now our bytes are in the Big Endian order required for primitive types */

         //since 2 bytes is a short which has range -32768 to +32767, we divide by 32768 to normalize to -1.0 to +1.0 range for DSP
        sample = sample /32768.0;

        return sample;
    }

    /**
     * Converts sample of type double into 2 bytes,
     * and stores into the byte buffer starting at the given offset.
     * @param sample audio sample of type double
     * @param buff an array of bytes
     * @param offset the offset of the buffer to write to.
     */
    public void sampleToBytes(double sample, byte[] buff, int offset)
    {
        sample = Math.min(1.0, Math.max(-1.0, sample));  //ensures that our double is within the -1.0 to +1.0 range
        int nsample = (int) Math.round(sample * 32767.0);//expands it to the range of -32768 to 32767 range of short, round, & truncate
        buff[offset + 1] = (byte) ((nsample >> 8) & 0xFF); //isolate and extract the high byte
        buff[offset + 0] = (byte) (nsample & 0xFF);        //isolate the low byte with MASK
    }

    /**
     * Moves the read position to a given offset in bytes.
     * @param offset
     * @throws IOException
     */
    public void seek(long offset) throws IOException {
        /* note this may not be the most effecient way to do this */

        // since samples are two bytes, the offset must be even.
        if (offset % 2 == 1)
            offset--;

        //reinitialize stream
        fis = new FileInputStream(audioFile);
        inputStream = new BufferedInputStream(fis);

        inputStream.skip(offset);
        position = offset;
    }

    /**
     *
     * @return the current position in bytes.
     */
    public long getPosition(){
        return position;
    }
}
