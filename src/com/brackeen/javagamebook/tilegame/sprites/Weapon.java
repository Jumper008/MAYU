package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * Weapon
 * 
 * It manages the definition of each object of type <code>Weapon</code>
 * 
 * A weapon is an object that the player can use to kill enemies
 * 
 * @author Quazar Volume
 */
public class Weapon extends Creature {
    private static final float fTHROW_SPEED = .95f;
    private boolean bDownwardArrow;
    
    /**
     * Weapon
     * 
     * Parameterized Constructor that sets health with de default value
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     */
    public Weapon(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight)
    {
        super(aniWalkLeft, aniWalkRight, aniDeadLeft, aniDeadRight, 
                aniIdleLeft, aniIdleRight);
        bDownwardArrow = false;
    }
    
    /**
     * Weapon
     * 
     * Parameterized Constructor
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     * @param iHealth is an object of class <code>int</code>
     */
    public Weapon(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight, int iHealth)
    {
        super(aniWalkLeft, aniWalkRight, aniDeadLeft, aniDeadRight, 
                aniIdleLeft, aniIdleRight, iHealth);
        bDownwardArrow = false;
    }

    /**
     * collideHorizontal
     * 
     * set velocity of X
     */
    public void collideHorizontal() {
        setVelocityX(0);
    }

    /**
     * collideVertical
     * 
     * set velocity of y
     */
    public void collideVertical() {
        setVelocityY(0);
    }

    /**
     * setY
     * 
     * set y 
     * 
     * @param y is an object of class <code>float</code>
     */
    public void setY(float y) {
        super.setY(y);
    }
    
    /**
     * setX
     * 
     * set x
     * 
     * @param x is an object of class <code>float</code>
     */
    public void setX(float x) {
        super.setX(x);
    }

    /**
     * getThrowSpeed
     * 
     * returns throw speed of the weapon
     * 
     * @return object of class <code>Float</code>
     */
    public float getThrowSpeed() {
        return 0.95f;
    }
    
    /**
     * isFlying
     * 
     * @return object of class <code>Boolean</code>
     */
    public boolean isFlying() {
        return !isAlive();
    }
    
    /**
     * isDownwardArrow
     * 
     * Indicates whether or not the arrow is facing downwards
     * 
     * @return <code>true</code> if the arrow is facing downwards
     */
    public boolean isDownwardArrow() {
        return bDownwardArrow;
    }
    
    /**
     * setDownwardArrow
     * 
     * Sets the boolean bDownwardArrow
     * 
     * @param bDownwardArrow is an object of class <code>boolean</code>
     */
    public void setDownwardArrow( boolean bDownwardArrow ) {
        this.bDownwardArrow = bDownwardArrow;
    }
}
