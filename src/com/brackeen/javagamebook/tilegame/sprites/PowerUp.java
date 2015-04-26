package com.brackeen.javagamebook.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;

/**
 * PowerUp
 *
 * It manages the definition of each object of type <code>PowerUp</code>
 *
 * A PowerUp class is a Sprite that the player can pick up.
 * 
 * @author Quazar Volume
 *
 */
public abstract class PowerUp extends Sprite {

    /**
     * PowerUp
     * 
     * Parameterized Constructor
     * 
     * @param aniAnim is an object of class <code>Animation</code>
     */
    public PowerUp(Animation aniAnim) {
        super(aniAnim);
    }

    /**
     * clone
     * 
     * clones object
     * 
     * @return object of class <code>Animation</code>
     */
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constConstructor = getClass().getConstructors()[0];
        try {
            return constConstructor.newInstance(
                new Object[] {(Animation)aniAnim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Star
     * 
     * It manages the definition of each object of type <code>Star</code>
     * 
     * A Star PowerUp. Gives the player points.
     * 
     */
    public static class Star extends PowerUp {
        public Star(Animation aniAnim) {
            super(aniAnim);
        }
    }

    /**
     * Music 
     * 
     * It manages the definition of each object of type <code>Music</code>
     * 
     * A Music PowerUp. Changes the game music.
     * 
     */
    public static class Music extends PowerUp {
        public Music(Animation aniAnim) {
            super(aniAnim);
        }
    }

    /**
     * Goal
     * 
     * It manages the definition of each object of type <code>Goal</code>
     * 
     * A Goal PowerUp. Advances to the next map.
     */
    public static class Goal extends PowerUp {
        public Goal(Animation aniAnim) {
            super(aniAnim);
        }
    }

}
