package com.brackeen.javagamebook.sound;

/**
 * Sound
 *
 * It manages the definition of each object of type <code>Sound</code>
 *
 * The Sound class is a container for sound samples. The sound
 * samples are format-agnostic and are stored as a byte array.
 * 
 * @author Quazar Volume
 *
 */
public class Sound {

    private byte[] byteArrSamples;
  
    /**
     * Sound
     * 
     * Parameterized Constructor
     * 
     * Create a new Sound object with the specified byte array.
     * The array is not copied.
     * 
     * @param byteArrSamples is an object of class <code>byte</code>
     */
    public Sound(byte[] byteArrSamples) {
        this.byteArrSamples = byteArrSamples;
    }

    /**
     * getSamples
     * 
     * Returns this Sound's objects samples as a byte array.
     * 
     * @return an object of class <code>Sound</code>
     */
    public byte[] getSamples() {
        return byteArrSamples;
    }

}
