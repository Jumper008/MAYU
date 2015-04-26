package com.brackeen.javagamebook.sound;

/**
 * FilterSequence
 * 
 * It manages the definition of each object of type <code>FilterSequence</code>
 * The FilterSequence class is a SoundFilter that combines
 * several SoundFilters at once.
 * 
 * @author Quazar Volume
 */
public class FilterSequence extends SoundFilter {

    private SoundFilter[] sfArrFilters;
    
    /**
     * FilterSequence
     * 
     * Parameterized Constructor
     * 
     * Creates a new FilterSequence object with the specified
     * array of SoundFilters.
     * 
     * @param sfArrFilters is an object of class <code>SoundFilter</code>
     */
    public FilterSequence(SoundFilter[] sfArrFilters) {
        this.sfArrFilters = sfArrFilters;
    }

    /**
     * getRemainingSize
     * 
     * Returns the maximum remaining size of all SoundFilters
     * in this FilterSequence.
     * 
     * @return object of class <code>ineger</code> 
     */
    public int getRemainingSize() {
        int iMax = 0;
        for (int iI=0; iI<sfArrFilters.length; iI++) {
            iMax = Math.max(iMax, sfArrFilters[iI].getRemainingSize());
        }
        return iMax;
    }


    /**
     * reset
     * 
     * Resets each SoundFilter in this FilterSequence.
     */
    public void reset() {
        for (int iI=0; iI<sfArrFilters.length; iI++) {
            sfArrFilters[iI].reset();
        }
    }



    /**
     * filter
     * 
     *  Filters the sound simple through each SoundFilter in this
     *  FilterSequence.
     * 
     * @param byteArrSamples is an array of class <code>byte</code>
     * @param iOffset is an object of class <code>integer</code>
     * @param iLength is an object of class <code>integer</code>
     */
    public void filter(byte[] byteArrSamples, int iOffset, int iLength) {
        for (int iI=0; iI<sfArrFilters.length; iI++) {
            sfArrFilters[iI].filter(byteArrSamples, iOffset, iLength);
        }
    }
}
