package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
/**
 * Grub
 *
 * It manages the definition of each object of type <code>Grub</code>
 *
 *  A Grub is a Creature that moves slowly on the ground.
 * 
 * @author Quazar Volume
 *
 */
public class Grub extends Creature {
    
    /**
     * Grub
     * 
     * Parameterized Constructor
     * 
     * @param aniLeft is an object of class <code>Animation</code>
     * @param aniRight is an object of class <code>Animation</code>
     * @param aniDeadLeft is an object of class <code>Animation</code>
     * @param aniDeadRight is an object of class <code>Animation</code>
     */
    public Grub(Animation aniLeft, Animation aniRight,
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
        return 0.05f;
    }

}
