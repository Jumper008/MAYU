package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * Fly
 *
 * It manages the definition of each object of type <code>Fly</code>
 *
 * A Fly is a Creature that fly slowly in the air.
 *
 * @author Quazar Volume
 *
 */
public class Fly extends Creature {
    
    /**
     * Fly
     * 
     * Parameterized Constructor
     * 
     * @param aniLeft is an object of class <code>Animation</code>
     * @param aniRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code> 
     */
    public Fly(Animation aniLeft, Animation aniRight,
        Animation aniDeadLeft, Animation aniDeadRight)
    {
        super(aniLeft, aniRight, aniDeadLeft, aniDeadRight);
    }

    /**
     * getMaxSpeed
     * 
     * @return object of class <code>integer</code>
     */
    public float getMaxSpeed() {
        return 0.2f;
    }

    /**
     * isFlying
     * 
     * @return object of class <code>Boolean</code>
     */
    public boolean isFlying() {
        return isAlive();
    }

}
