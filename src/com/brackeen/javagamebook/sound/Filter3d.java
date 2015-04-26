package com.brackeen.javagamebook.sound;

import com.brackeen.javagamebook.graphics.Sprite;


/**
 * Filter3d
 * 
 * It manages the definition of each object of type <code>Filter3d</code>
 * The Filter3d class is a SoundFilter that creates a 3d sound
 * effect. The sound is filtered so that it is quiter the farther
 * away the sound source is from the listener.
 * 
 * @author Quazar Volume
 */
public class Filter3d extends SoundFilter {

    // number of samples to shift when changing the volume.
    private static final int iNUM_SHIFTING_SAMPLES = 500;

    private Sprite sprSource;
    private Sprite sprListener;
    private int iMaxDistance;
    private float fLastVolume;

  
    /**
     * Filter3d
     * 
     * Parameterized Constructor
     * 
     * Creates a new Filter3d object with the specified source
     * and listener Sprites. The Sprite's position can be
     * 
     * @param sprSource is an array of class <code>Sprite</code>
     * @param sprListener is an object of class <code>Sprite</code>
     * @param iMaxDistance is an object of class <code>Integer</code>
     */
    public Filter3d(Sprite sprSource, Sprite sprListener,
        int iMaxDistance)
    {
        this.sprSource = sprSource;
        this.sprListener = sprListener;
        this.iMaxDistance = iMaxDistance;
        this.fLastVolume = 0.0f;
    }


    /**
     * filter
     * 
     * Filters the sound so that it gets more quiet with
     * distance.
     * 
     * @param byteArrSamples is an array of class <code>byte</code>
     * @param iOffset is an object of class <code>Integer</code>
     * @param iLength is an object of class <code>Integer</code>
     */
    public void filter(byte[] byteArrSamples, int iOffset, int iLength) {

        if (sprSource == null || sprListener == null) {
            // nothing to filter - return
            return;
        }

        // calculate the listener's distance from the sound source
        float fDx = (sprSource.getX() - sprListener.getX());
        float fDy = (sprSource.getY() - sprListener.getY());
        float fDistance = (float)Math.sqrt(fDx * fDx + fDy * fDy);

        // set volume from 0 (no sound) to 1
        float fNewVolume = (iMaxDistance - fDistance) / iMaxDistance;
        if (fNewVolume <= 0) {
            fNewVolume = 0;
        }

        // set the volume of the sample
        int iShift = 0;
        for (int iI=iOffset; iI<iOffset+iLength; iI+=2) {

            float fVolume = fNewVolume;

            // shift from the last volume to the new volume
            if (iShift < iNUM_SHIFTING_SAMPLES) {
                fVolume = fLastVolume + (fNewVolume - fLastVolume) *
                    iShift / iNUM_SHIFTING_SAMPLES;
                iShift++;
            }

            // change the volume of the sample
            short shoOldSample = getSample(byteArrSamples, iI);
            short shoNewSample = (short)(shoOldSample * fVolume);
            setSample(byteArrSamples, iI, shoNewSample);
        }

        fLastVolume = fNewVolume;
    }

}
