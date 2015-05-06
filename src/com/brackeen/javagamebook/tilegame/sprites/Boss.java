package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Boss is a Creature that moves slowly on the ground.
*/
/**
 * Boss
 *
 * It manages the definition of each object of type <code>Boss</code>
 *
 *  A Boss is a Creature that's meant to be far stronger than the others.
 * 
 * @author Quazar Volume
 *
 */
public class Boss extends Creature {
    
    /**
     * Boss
     * 
     * Parameterized Constructor that sets health with the default value
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     */
    public Boss(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight)
    {
        super(aniWalkLeft, aniWalkRight, aniDeadLeft, aniDeadRight, 
                aniIdleLeft, aniIdleRight);
    }
    
    /**
     * Boss
     * 
     * Parameterized Constructor.
     * 
     * @param aniWalkLeft is an object of class <code>Animation</code>
     * @param aniWalkRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     * @param aniIdleLeft is an object of class <code>Animation</code>
     * @param aniIdleRight is an object of class <code>Animation</code>
     * @param iHealth is an object of class <code>int</code>
     */
    public Boss(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight, int iHealth)
    {
        super(aniWalkLeft, aniWalkRight, aniDeadLeft, aniDeadRight, 
                aniIdleLeft, aniIdleRight, iHealth);
    }

    /**
     * getMaxSpeed
     * 
     * @return object of class <code>integer</code>
     */
    public float getMaxSpeed() {
        return 0.5f;
    }

}
