package com.brackeen.javagamebook.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;

/**
 * Creature
 *
 * It manages the definition of each object of type <code>Creature</code>
 *
 * A Creature is a Sprite that is affected by gravity and can
 * die. It has four Animations: moving left, moving right,
 * dying on the left, and dying on the right.
 *
 * @author Quazar Volume
 *
 */
public abstract class Creature extends Sprite {

 
    private static final int iDIE_TIME = 1000;
    //Amount of time to go from STATE_DYING to STATE_DEAD

    public static final int iSTATE_NORMAL = 0;
    public static final int iSTATE_DYING = 1;
    public static final int iSTATE_DEAD = 2;

    private Animation aniWalkLeft;
    private Animation aniWalkRight;
    private Animation aniIdleLeft;
    private Animation aniIdleRight;
    private Animation aniDeadLeft;
    private Animation aniDeadRight;
    private int iState;
    private long lStateTime;

    /**
     * Creature
     * 
     * Parameterized Constructor
     * 
     * Creates a new Creature with the specified Animations.
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     */
    public Creature(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight)
    {
        super(aniWalkRight);
        this.aniWalkLeft = aniWalkLeft;
        this.aniWalkRight = aniWalkRight;
        this.aniDeadLeft = aniDeadLeft;
        this.aniDeadRight = aniDeadRight;
        this.aniIdleLeft = aniIdleLeft;
        this.aniIdleRight = aniIdleRight;
        iState = iSTATE_NORMAL;
    }

    /**
     * clone
     * 
     * Clones the object
     * 
     * @return object of class <code>Creature</code>
     */
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constConstructor = getClass().getConstructors()[0];
        try {
            return constConstructor.newInstance(new Object[] {
                (Animation)aniWalkLeft.clone(),
                (Animation)aniWalkRight.clone(),
                (Animation)aniDeadLeft.clone(),
                (Animation)aniDeadRight.clone(),
                (Animation)aniIdleLeft.clone(),
                (Animation)aniIdleRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * getMaxSpeed
     * 
     * Gets the maximum speed of this Creature.
     * 
     * @return object of class <code>integer</code>
     */
    public float getMaxSpeed() {
        return 0;
    }

    /**
     * wakeUp
     * 
     * Wakes up the creature when the Creature first appears
     * on screen. Normally, the creature starts moving left.
     * 
     */
    public void wakeUp() {
        if (getState() == iSTATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }

    /**
     * getState
     * 
     * Gets the state of this Creature. The state is either
     * STATE_NORMAL, STATE_DYING, or STATE_DEAD.
     * 
     * @return object of class <code>integer</code>
     */
    public int getState() {
        return iState;
    }

    /**
     * setState
     * 
     * Sets the state of this Creature to STATE_NORMAL,
     * STATE_DYING, or STATE_DEAD.
     * 
     * @param iState object of class <code>integer</code>
     */
    public void setState(int iState) {
        if (this.iState != iState) {
            this.iState = iState;
            lStateTime = 0;
            if (iState == iSTATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }

    /**
     * isAlive
     * 
     * Checks if this creature is alive.
     * 
     * @return object of class <code>integer</code>
     */
    public boolean isAlive() {
        return (iState == iSTATE_NORMAL);
    }

    /**
     * isFlying
     * 
     * Checks if this creature is flying.
     * 
     * @return object of class <code>Boolean</code>
     */
    public boolean isFlying() {
        return false;
    }

    /**
     * collideHorizontal
     * 
     * Called before update() if the creature collided with a
     * tile horizontally.
     */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }

    /**
     * collideVertical
     * 
     * Called before update() if the creature collided with a
     * tile vertically.
     */
    public void collideVertical() {
        setVelocityY(0);
    }

    /**
     * update
     * 
     * Updates the animaton for this creature.
     * 
     * @param lElapsedTime is an object of class <code>long</code>
     */
    public void update(long lElapsedTime) {
        // select the correct Animation
        Animation aniNewAnim = aniAnim;
        if (getVelocityX() < 0) {
            aniNewAnim = aniWalkLeft;
        }
        else if (getVelocityX() > 0) {
            aniNewAnim = aniWalkRight;
        }
        else if (aniNewAnim == aniWalkLeft) {
            aniNewAnim = aniIdleLeft;
        }
        else if (aniNewAnim == aniWalkRight) {
            aniNewAnim = aniIdleRight;
        }
        if (iState == iSTATE_DYING && aniNewAnim == aniWalkLeft) {
            aniNewAnim = aniDeadLeft;
        }
        else if (iState == iSTATE_DYING && aniNewAnim == aniWalkRight) {
            aniNewAnim = aniDeadRight;
        }

        // update the Animation
        if (aniAnim != aniNewAnim) {
            aniAnim = aniNewAnim;
            aniAnim.start();
        }
        else {
            aniAnim.update(lElapsedTime);
        }

        // update to "dead" state
        lStateTime += lElapsedTime;
        if (iState == iSTATE_DYING && lStateTime >= iDIE_TIME) {
            setState(iSTATE_DEAD);
        }
    }

}
