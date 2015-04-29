package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * Player
 *
 * It manages the definition of each object of type <code>Player</code>
 *
 * The Player.
 * 
 * @author Quazar Volume
 *
 */
public class Player extends Creature {

    private static final float fJUMP_SPEED = -.4f;

    private boolean bOnGround;
    private boolean bFacingRight;   // States wether or not the player is facing right.
    private float fInitialJumpY = 0;    // Records from where did the character start to jump
    private float fJumpAccelHeight = 200; // States how far can the player jump from its initial position in Y
    private boolean bJumpAccelHeightReached = false;
    
    /**
     * Player
     * 
     * Parameterized Constructor
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     */
    public Player(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight)
    {
        super(aniWalkLeft, aniWalkRight, aniDeadLeft, aniDeadRight,
                aniIdleLeft, aniIdleRight);
        bFacingRight = true;
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
        // check if collided with ground
        if (getVelocityY() > 0) {
            bOnGround = true;
            fInitialJumpY = 0;
            bJumpAccelHeightReached = false;
        }
    
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
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            bOnGround = false;
        }
        super.setY(y);
    }

    /**
     * wakeUp
     * 
     * do nothing
     */
    public void wakeUp() {
        // do nothing
    }

    /**
     * jump
     * 
     * Makes the player jump if the player is on the ground or
     * if forceJump is true.
     * 
     * @param forceJump is an object of class <code>Boolean</code>
     */
    public void jump(boolean forceJump) {
//        if (bOnGround || forceJump) {
//            bOnGround = false;
//            setVelocityY(fJUMP_SPEED);
//        }
        //Check if the player is on the ground, ready to jump
        if (bOnGround || forceJump) {
            bOnGround = false;
            fInitialJumpY = this.getY();
        }
        
        if (this.getY() > fInitialJumpY - fJumpAccelHeight && !bJumpAccelHeightReached) {
            setVelocityY (fJUMP_SPEED);
        } else {
            bJumpAccelHeightReached = true;
        }
    }
    
    /**
     * Set Jump Accel Height Reached
     * 
     * Set if the player has reached its maximum height to accelerate vertically
     * while jumping
     * 
     * @param bReached is a <code>boolean</code> 
     */
    public void setJumpAccelHeightReached( boolean bReached ) {
        bJumpAccelHeightReached = bReached;
    }
    
    /**
     * setJumpAccelHeight
     * 
     * Changes up to where the player can keep accelerating from the ground
     * 
     * @param fHeight is the distance between the initial height of the jump
     * up to where the player can keep accelerating
     */
    public void setJumpAccelHeight( float fHeight ) {
        fJumpAccelHeight = fHeight;
    }
    
    /**
     * IsGrounded
     * 
     * @return <code>boolean</code> bOnGround as true if the player is touching
     * the ground
     * 
     */
    public boolean isGrounded() {
        return bOnGround;
    }

    /**
     * getMaxSpeed
     * 
     * returns max speed of the player
     * 
     * @return object of class <code>Integer</code>
     */
    public float getMaxSpeed() {
        return 0.5f;
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
}
