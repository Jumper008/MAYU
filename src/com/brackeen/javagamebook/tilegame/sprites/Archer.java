package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Archer is a Creature that moves slowly on the ground.
*/
/**
 * Archer
 *
 * It manages the definition of each object of type <code>Archer</code>
 *
 *  A Archer is a Creature that moves slowly on the ground.
 * 
 * @author Quazar Volume
 *
 */
public class Archer extends Creature {
    
    /**
     * Archer
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
    public Archer(Animation aniWalkLeft, Animation aniWalkRight,
        Animation aniDeadLeft, Animation aniDeadRight, Animation aniIdleLeft, 
        Animation aniIdleRight)
    {
        super(aniWalkLeft, aniWalkRight, aniDeadLeft, aniDeadRight, 
                aniIdleLeft, aniIdleRight);
    }
    
    /**
     * Archer
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
    public Archer(Animation aniWalkLeft, Animation aniWalkRight,
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
        return 0.05f;
    }
    

}
