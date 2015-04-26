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
    
    /**
     * Weapon
     * 
     * Parameterized Constructor
     * 
     * @param aniLeft is an object of class <code>Animation</code>
     * @param aniRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     */
    public Weapon(Animation aniLeft, Animation aniRight,
        Animation aniDeadLeft, Animation aniDeadRight)
    {
        super(aniLeft, aniRight, aniDeadLeft, aniDeadRight);
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
     * jump
     * 
     * Makes the player jump if the player is on the ground or
     * if forceJump is true.
     * 
     * @param forceJump is an object of class <code>Boolean</code>
     */
//    public void jump(boolean forceJump) {
//        if (bOnGround || forceJump) {
//            bOnGround = false;
//            setVelocityY(fJUMP_SPEED);
//        }
//    }

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
}
