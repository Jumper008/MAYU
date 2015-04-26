package com.brackeen.javagamebook.sound;


/**
 * EchoFilter
 *
 * It manages the definition of each object of type <code>EchoFilter</code>
 *
 * The EchoFilter class is a SoundFilter that emulates an echo.
 *
 * @author Quazar Volume
 *
 */
public class EchoFilter extends SoundFilter {

    private short[] shoArrDelayBuffer;
    private int iDelayBufferPos;
    private float fDecay;

    /**
     * EchoFilter
     * 
     * Parameterized Constructor 
     * 
     * Creates an EchoFilter with the specified number of delay
     * samples and the specified decay rate.
     * 
     * @param iNumDelaySamples is an object of class <code>integer</code>
     * @param fDecay is an object of class <code>float</code>
     */
    public EchoFilter(int iNumDelaySamples, float fDecay) {
        shoArrDelayBuffer = new short[iNumDelaySamples];
        this.fDecay = fDecay;
    }
      
    /**
    * getRemainingSize
    * 
    * Gets the remaining size, in bytes, of samples that this
    * filter can echo after the sound is done playing.
    * Ensures that the sound will have decayed to below 1%
    * of maximum volume (amplitude).
    * 
    * @return object of class <code>ineger</code> 
    */
    public int getRemainingSize() {
        float fFinalDecay = 0.01f;
        // derived from Math.pow(decay,x) <= finalDecay
        int iNumRemainingBuffers = (int)Math.ceil(Math.log(fFinalDecay) / Math.log(fDecay));
        int iBufferSize = shoArrDelayBuffer.length * 2;

        return iBufferSize * iNumRemainingBuffers;
    }

    
    /**
     * reset
     * 
     * Clears this EchoFilter's internal delay buffer.
     * 
     */
    public void reset() {
        for (int i=0; i<shoArrDelayBuffer.length; i++) {
            shoArrDelayBuffer[i] = 0;
        }
        iDelayBufferPos = 0;
    }
    
    /**
     * filter
     * 
     * Filters the sound samples to add an echo. The samples
     * played are added to the sound in the delay buffer
     * multipied by the decay rate. The result is then stored in
     * the delay buffer, so multiple echoes are heard.
     * 
     * @param byteArrSamples is an array of class <code>byte</code>
     * @param iOffset is an object of class <code>integer</code>
     * @param iLength is an object of class <code>integer</code>
     */
    public void filter(byte[] byteArrSamples, int iOffset, int iLength) {

        for (int i=iOffset; i<iOffset+iLength; i+=2) {
            // update the sample
            short shoOldSample = getSample(byteArrSamples, i);
            short shoNewSample = (short)(shoOldSample + fDecay *
                shoArrDelayBuffer[iDelayBufferPos]);
            setSample(byteArrSamples, i, shoNewSample);

            // update the delay buffer
            shoArrDelayBuffer[iDelayBufferPos] = shoNewSample;
            iDelayBufferPos++;
            if (iDelayBufferPos == shoArrDelayBuffer.length) {
                iDelayBufferPos = 0;
            }
        }
    }

}
