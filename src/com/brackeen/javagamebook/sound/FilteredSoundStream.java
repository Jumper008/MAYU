package com.brackeen.javagamebook.sound;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * FilteredSoundStream
 * 
 * The FilteredSoundStream class is a FilterInputStream that
 * applies a SoundFilter to the underlying input stream.
 * 
 * @author Quazar Volume
 */
public class FilteredSoundStream extends FilterInputStream {

    private static final int iREMAINING_SIZE_UNKNOWN = -1;

    private SoundFilter sfSoundFilter;
    private int iRemainingSize;

    /**
        Creates a new FilteredSoundStream object with the
        specified InputStream and SoundFilter.
    */
    
    /**
     * FilteredSoundStream
     * 
     * Parameterized Constructor
     * 
     * Creates a new FilteredSoundStream object with the
     * specified InputStream and SoundFilter.
     * 
     * @param isIn is an object of class <code>InputStream</code>
     * @param sfSoundFilter is an object of class <code>SoundFilter</code>
     */
    public FilteredSoundStream(InputStream isIn,
        SoundFilter sfSoundFilter)
    {
        super(isIn);
        this.sfSoundFilter = sfSoundFilter;
        iRemainingSize = iREMAINING_SIZE_UNKNOWN;
    }

    /**
     * read
     * 
     * Overrides the FilterInputStream method to apply this
     * filter whenever bytes are read
     * 
     * @param byteArrSamples is an array of class <code>byte</code>
     * @param iOffset is an object of class <code>integer</code>
     * @param iLength is an object of class <code>integer</code>
     * @return an object of class <code>integer</code>
     * @throws IOException 
     */
    public int read(byte[] byteArrSamples, int iOffset, int iLength)
        throws IOException
    {
        // read and filter the sound samples in the stream
        int iBytesRead = super.read(byteArrSamples, iOffset, iLength);
        if (iBytesRead > 0) {
            sfSoundFilter.filter(byteArrSamples, iOffset, iBytesRead);
            return iBytesRead;
        }

        // if there are no remaining bytes in the sound stream,
        // check if the filter has any remaining bytes ("echoes").
        if (iRemainingSize == iREMAINING_SIZE_UNKNOWN) {
            iRemainingSize = sfSoundFilter.getRemainingSize();
            // round down to nearest multiple of 4
            // (typical frame size)
            iRemainingSize = iRemainingSize / 4 * 4;
        }
        if (iRemainingSize > 0) {
            iLength = Math.min(iLength, iRemainingSize);

            // clear the buffer
            for (int iI=iOffset; iI<iOffset+iLength; iI++) {
                byteArrSamples[iI] = 0;
            }

            // filter the remaining bytes
            sfSoundFilter.filter(byteArrSamples, iOffset, iLength);
            iRemainingSize-=iLength;

            // return
            return iLength;
        }
        else {
            // end of stream
            return -1;
        }
    }

}
