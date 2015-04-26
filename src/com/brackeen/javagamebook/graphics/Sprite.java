package com.brackeen.javagamebook.graphics;

import java.awt.Image;

/**
 * Sprite
 *
 * It manages the definition of each object of type <code>Sprite</code>
 *
 * The Sprite class manages an animation which has a specific position and a
 * specific velocity.
 *
 * @author Quazar Volume
 */
public class Sprite {

    protected Animation aniAnim; // Animation of sprite
    // position (pixels)
    private float fX;
    private float fY;
    // velocity (pixels per millisecond)
    private float fDx;
    private float fDy;

    /**
     * Sprite
     *
     * Default constructor
     *
     * Creates a new Sprite object with the specified Animation.
     *
     * @param aniAnim is an object of class <code>Animation</code>
     */
    public Sprite(Animation aniAnim) {
        this.aniAnim = aniAnim;
    }
    
    /**
     * update
     *
     * Updates this Sprite's Animation and its position based
     * on the velocity.
     *
     * @param lElapsedTime is an object of class <code>Long</code>
     */
    public void update(long lElapsedTime) {
        fX += fDx * lElapsedTime;
        fY += fDy * lElapsedTime;
        aniAnim.update(lElapsedTime);
    }
    
    /**
     * getX
     * 
     * Gets this Sprite's current x position.
     * 
     * @return object of class <code>Float</code>
     */
    public float getX() {
        return fX;
    }
    
    /**
     * getY
     * 
     * Gets this Sprite's current y position.
     * 
     * @return object of class <code>Float</code>
     */
    public float getY() {
        return fY;
    }
    
    /**
     * setX
     * 
     * Sets this Sprite's current x position.
     * 
     * @param fX is an object of class <code>Float</code>
     */
    public void setX(float fX) {
        this.fX = fX;
    }
    
    /**
     * setY
     * 
     * Sets this Sprite's current y position.
     * 
     * @param fY is an object of class <code>Float</code>
     */
    public void setY(float fY) {
        this.fY = fY;
    }
    
    /**
     * getWidth
     * 
     * Gets this Sprite's width, based on the size of the current image.
     * 
     * @return object of class <code>Integer</code>
     */
    public int getWidth() {
        return aniAnim.getImage().getWidth(null);
    }
    
    /**
     * getHeight
     * 
     * Gets this Sprite's height, based on the size of the current image.
     * 
     * @return object of class <code>Integer</code>
     */
    public int getHeight() {
        return aniAnim.getImage().getHeight(null);
    }
    
    /**
     * getVelocityX
     * 
     * Gets the horizontal velocity of this Sprite in pixels per millisecond.
     * 
     * @return object of class <code>Float</code>
     */
    public float getVelocityX() {
        return fDx;
    }
    
    /**
     * getVelocityY
     * 
     * Gets the vertical velocity of this Sprite in pixels per millisecond.
     * 
     * @return object of class <code>Float</code>
     */
    public float getVelocityY() {
        return fDy;
    }
    
    /**
     * setVelocityX
     * 
     * Sets the horizontal velocity of this Sprite in pixels per millisecond.
     * 
     * @param fDx is an object of class <code>Float</code>
     */
    public void setVelocityX(float fDx) {
        this.fDx = fDx;
    }
    
    /**
     * setVelocityY
     * 
     * Sets the vertical velocity of this Sprite in pixels per millisecond.
     * 
     * @param fDy is an object of class <code>Float</code>
     */
    public void setVelocityY(float fDy) {
        this.fDy = fDy;
    }
    
    /**
     * getImage
     * 
     * Gets this Sprite's current image.
     * 
     * @return object of class <code>Image</code>
     */
    public Image getImage() {
        return aniAnim.getImage();
    }
    
    /**
     * clone
     * 
     * Clones this Sprite. Does not clone position or velocity info.
     * 
     * @return object of class <code>Object</code>
     */
    public Object clone() {
        return new Sprite(aniAnim);
    }
}