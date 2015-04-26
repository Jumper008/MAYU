package com.brackeen.javagamebook.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
 * Animation
 *
 * It manages the definition of each object of type <code>Animation</code>
 *
 * The Animation class manages a series of images (frames) and
 * the amount of time to display each frame.
 *
 * @author Quazar Volume
 */
public class Animation {

    private ArrayList Arrframes; // Array of frames
    private int iCurrFrameIndex; // Index of current frame
    private long lAnimTime; // Animation time
    private long lTotalDuration; // Total duration of animation


    /**
     * Animation
     *
     * Default constructor
     *
     * Creates a new, empty Animation
     */
    public Animation() {
        this(new ArrayList(), 0); // Invokes parameterized constructor
    }

    /**
     * Animation
     *
     * Parameterized constructor
     *
     * Creates a new, custom Animation
     *
     * @param frames is an object of class <code>ArrayList</code>
     * @param lTotalDuration is an object of class <code>Long</code>
     */
    private Animation(ArrayList frames, long lTotalDuration) {
        this.Arrframes = frames;
        this.lTotalDuration = lTotalDuration;
        start(); // Invokes start function
    }

    /**
     * clone
     *
     * Creates a duplicate of this animation. The list of frames
     * are shared between the two Animations, but each Animation
     * can be animated independently.
     *
     * @return object of class <code>Object</code>
     */
    public Object clone() {
        return new Animation(Arrframes, lTotalDuration);
    }

    /**
     * addFrame
     *
     * Adds an image to the animation with the specified
     * duration (time to display the image).
     *
     * @param imaImage is an object of class <code>Image</code>
     * @param lDuration is an object of class <code>Long</code>
     */
    public synchronized void addFrame(Image imaImage,
        long lDuration)
    {
        lTotalDuration += lDuration; // Updates lTotalDuration
        Arrframes.add(new AnimFrame(imaImage, lTotalDuration)); // Adds frame
    }

    /**
     * start
     *
     * Starts this animation over from the beginning.
     */
    public synchronized void start() {
        lAnimTime = 0; // Resets animation time
        iCurrFrameIndex = 0; // Resets the current frame
    }

    /**
     * update
     *
     * Updates this animation's current image (frame), if necessary.
     *
     * @param lElapsedTime is an object of class <code>Long</code>
     */
    public synchronized void update(long lElapsedTime) {
        // Checks if frame array list has more than one frame to update
        if (Arrframes.size() > 1) {
            lAnimTime += lElapsedTime; // Updates animation time

            // Resets animation time if necessary
            if (lAnimTime >= lTotalDuration) {
                lAnimTime = lAnimTime % lTotalDuration;
                iCurrFrameIndex = 0;
            }

            // Checks if it needs to update animation or not
            while (lAnimTime > getFrame(iCurrFrameIndex).lEndTime) {
                iCurrFrameIndex++;
            }
        }
    }

    /**
     * getImage
     *
     * Gets this Animation's current image. Returns null if this
     * animation has no images.
     *
     * @return object of class <code>Image</code> or null
     */
    public synchronized Image getImage() {
        // Checks if frame array list is empty
        if (Arrframes.size() == 0) {
            return null;
        }
        else {
            return getFrame(iCurrFrameIndex).imaImage; // Returns image if not
        }
    }

    /**
     * getFrame
     *
     * Gets this Animation's specified frame.
     *
     * @param iFrame is an object of class <code>Integer</code>
     * @return object of class <code>AnimFrame</code>
     */
    private AnimFrame getFrame(int iFrame) {
        return (AnimFrame)Arrframes.get(iFrame);
    }
    
    /**
     * AnimFrame
     *
     * It manages the definition of each object of type <code>AnimFrame</code>
     *
     * The AnimFrame class manages an image and an end time, used to know the
     * duration of that specific frame in an animation.
     *
     * @author Quazar Volume
     */
    private class AnimFrame {

        Image imaImage; // Image of the frame
        long lEndTime; // Ending time of the frame

        /**
         * AnimFrame
         *
         * Default constructor
         *
         * Creates a new, parameterized AnimFrame
         *
         * @param imaImage is an object of class <code>Image</code>
         * @param lEndTime is an object of class <code>Long</code>
         */
        public AnimFrame(Image imaImage, long lEndTime) {
            this.imaImage = imaImage;
            this.lEndTime = lEndTime;
        }
    }
}
