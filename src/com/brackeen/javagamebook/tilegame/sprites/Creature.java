package com.brackeen.javagamebook.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;
import java.util.Calendar;

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
    private int iHealth;
    private Calendar calShootTime;
    private boolean bFacingRight;   // States wether or not the creature is facing to the right.
    
    private Sprite sprStickySprite; // Variables used to make the creature stick to another creature
    private float fStickOffsetX;
    private float fStickOffsetY;
    
    /**
     * Creature
     * 
     * Parameterized Constructor
     * 
     * Creates a new Creature with the specified Animations. With iHealth set 
     * as 2, ShootTime with the time of its creation, and bFacingRight set
     * as true.
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
        iHealth = 2;
        calShootTime = Calendar.getInstance();
        bFacingRight = true;
        sprStickySprite = null;
    }
    
    /**
     * Creature
     * 
     * Parameterized Constructor 2
     * 
     * Creates a new Creature with the specified Animations and Health and 
     * ShootTime as the time of its creation. bFacingRight is set
     * as true
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     * @param iHealth is an object of class <code>int</code>
     */
    public Creature(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight, int iHealth)
    {
        super(aniWalkRight);
        this.aniWalkLeft = aniWalkLeft;
        this.aniWalkRight = aniWalkRight;
        this.aniDeadLeft = aniDeadLeft;
        this.aniDeadRight = aniDeadRight;
        this.aniIdleLeft = aniIdleLeft;
        this.aniIdleRight = aniIdleRight;
        iState = iSTATE_NORMAL;
        this.iHealth = iHealth;
        calShootTime = Calendar.getInstance();
        bFacingRight = true;
        sprStickySprite = null;
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
     * setHealth
     * 
     * Sets the amount of health for the creature
     * 
     * @param iHealth 
     */
    public void setHealth(int iHealth) {
        this.iHealth = iHealth;
    }
    
    /**
     * getHealth
     * 
     * Returns the amount of health points the creature has left
     * 
     * @return object of class <code>int</code>
     */
    public int getHealth() {
        return iHealth;
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
    
    /**
     * getShootTime
     * 
     * Returns calShootTime (this represents the time at which the last attack was issued)
     * 
     * @return object of class <code>Calendar</code>
     */
    public Calendar getShootTime() {
        return calShootTime;
    }
    
    /**
     * updateShootTime
     * 
     * Updates calShootTime to the current time.
     * 
     * calShootTime represents the time at which the last attack was issued by
     * the creature. (In the case of an archer, at which time he shot its
     * last arrow.)
     */
    public void updateShootTime() {
        calShootTime = Calendar.getInstance();
    }
    
    /**
     * setFacingRight
     * 
     * Activates or deactivates the way the player is facing.
     * 
     * @param bRight is an object of class <code>boolean</code> that states 
     * wether the player is, or not, facing right.
     * 
     */
    public void setFacingRight( boolean bRight ) {
        bFacingRight = bRight;
    }
    
    /**
     * getFacingRight
     * 
     * Returns wether the player is, or not, facing right.
     * 
     * @return object of class <code>boolean</code>
     */
    public boolean getFacingRight() {
        return bFacingRight;
    }

    /**
     * setSticky
     * 
     * Makes the creature sticky. This implies that it will store a reference to
     * the sprite it receives. This, used alongside with getStickyX and 
     * getStickyY, can be used to obtain relative positions to where this
     * creature should be positioned in order make it look like its stuck to
     * sprStickySprite.
     * 
     * @param sprStickySprite is an object of class <code>Sprite</code>
     */
    public void setSticky( Sprite sprStickySprite ) {
        this.sprStickySprite = sprStickySprite;
        
        this.fStickOffsetX = sprStickySprite.getX() - this.getX();
        this.fStickOffsetY = sprStickySprite.getY() - this.getY();
    }
    
    /**
     * isSticky
     * 
     * States whether the creature is currently "stuck" to another creature.
     * 
     * @return <code>true</code> if sprStickySprite != null
     */
    public boolean isSticky() {
        return sprStickySprite != null;
    }
    
    /**
     * getStickyX
     * 
     * States the position in X where this creature should be placed in order for
     * it to look to be stuck to sprStickySprite
     * 
     * @return object of class <code>float</code>
     */
    public float getStickyX() {
        if ( this.isSticky() ) {
            return sprStickySprite.getX() - fStickOffsetX;
        }
        else {
            return this.getX();
        }
    }
    
    /**
     * getStickyY
     * 
     * States the position in Y where this creature should be placed in order for
     * it to look to be stuck to sprStickySprite
     * 
     * @return object of class <code>float</code>
     */
    public float getStickyY() {
        if ( this.isSticky() ) {
            return sprStickySprite.getY() - fStickOffsetY;
        }
        else {
            return this.getY();
        }
    }
}
