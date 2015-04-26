package com.brackeen.javagamebook.sound;

/**
 * SoundFilter
 *
 * It manages the definition of each object of type <code>SoundFilter</code>
 *
 *  A abstract class designed to filter sound samples.
 *  Since SoundFilters may use internal buffering of samples,
 *  a new SoundFilter object should be created for every sound
 *  played.
 *
 * @author Quazar Volume
 *
 */

public abstract class SoundFilter{


    /**
     * reset
     * 
     * Resets this SoundFilter. Does nothing by default.
     * 
     */
    public void reset() {
        // do nothing
    }

    /**
     * getRemainingSize
     * 
     * Gets the remaining size, in bytes, that this filter
     * plays after the sound is finished. An example would
     * be an echo that plays longer than it's original sound.
     * This method returns 0 by default.
     * 
     * @return object of class <code>static ineger</code> 
     */
    public int getRemainingSize() {
        return 0;
    }

    /**
     * filter
     * 
     * Filters an array of samples. Samples should be in
     * 16-bit, signed, little-endian format.
     * 
     * @param byteArrSamples is an array of class <code>byte</code> 
     */
    public void filter(byte[] byteArrSamples) {
        filter(byteArrSamples, 0, byteArrSamples.length);
    }

    /**
     * filter
     * 
     * Filters an array of samples. Samples should be in
     * 16-bit, signed, little-endian format. This method
     * should be implemented by subclasses.
     * 
     * @param byteArrSamples is an array of class <code>byte</code>
     * @param iOffset is an object of class <code>Integer</code>
     * @param iLength is an object of class <code>Integer</code>
     */
    public abstract void filter(
        byte[] byteArrSamples, int iOffset, int iLength);

    /**
     * getSample
     * 
     * Convenience method for getting a 16-bit sample from a
     * byte array. Samples should be in 16-bit, signed,
     * little-endian format.
     * 
     * @param byteArrBuffer is an array of class <code>byte</code>
     * @param iPosition is an object of class <code>Integer</code>
     * @return object of class <code>short</code>
     */
    public static short getSample(byte[] byteArrBuffer, int iPosition) {
        return (short)(
            ((byteArrBuffer[iPosition+1] & 0xff) << 8) |
            (byteArrBuffer[iPosition] & 0xff));
    }

    /**
     * setSample
     * 
     * Convenience method for setting a 16-bit sample in a
     * byte array. Samples should be in 16-bit, signed,
     * little-endian format.
     *
     * @param byteArrBuffer is an array of class <code>byte</code>
     * @param iPosition is an object of class <code>Integer</code>
     * @param shoSample is an object of class <code>short</code>
     */
    public static void setSample(byte[] byteArrBuffer, int iPosition,
        short shoSample)
    {
        byteArrBuffer[iPosition] = (byte)(shoSample & 0xff);
        byteArrBuffer[iPosition+1] = (byte)((shoSample >> 8) & 0xff);
    }

}
