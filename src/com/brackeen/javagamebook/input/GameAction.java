package com.brackeen.javagamebook.input;

/**
 * GameAction
 * 
 * It manages the definition of each object of type <code>GameAction</code>
 * 
 * The GameAction class is an abstract to a user-initiated 
 * action, like jumping or moving. GameActions can be mapped 
 * to keys or the mouse with the InputManager.
 * 
 * @author Quazar Volume
 */
public class GameAction {

    /**
        Normal behavior. The isPressed() method returns true
        as long as the key is held down.
    */
    public static final int iNORMAL = 0;

    /**
        Initial press behavior. The isPressed() method returns
        true only after the key is first pressed, and not again
        until the key is released and pressed again.
    */
    public static final int iDETECT_RELEASE_ONLY = 0;
    public static final int iDETECT_INITAL_PRESS_ONLY = 1;
    public static final int iDETECT_WAITING_FOR_RELEASE_ONLY = 2;

    private static final int iSTATE_RELEASED = 0;
    private static final int iSTATE_PRESSED = 1;
    private static final int iSTATE_WAITING_FOR_RELEASE = 2;

    // Control variables
    private String sName;
    private int iBehavior;
    private int iAmount;
    private int iState;
    
    /**
     * GameAction
     * 
     * Parameterized constructor (One parameter)
     * 
     * Create a new GameAction with the NORMAL behavior.
     * 
     * @param sName is an object of class <code>String</code>
     */
    public GameAction(String sName) {
        this(sName, iNORMAL);
    }
    
    /**
     * GameAction
     * 
     * Parameterized constructor (Two parameters)
     * 
     * Create a new GameAction with the specified behavior.
     * 
     * @param sName is an object of class <code>String</code>
     * @param iBehavior is an object of class <code>Integer</code>
     */
    public GameAction(String sName, int iBehavior) {
        this.sName = sName;
        this.iBehavior = iBehavior;
        reset();
    }
    
    /**
     * getName
     * 
     * Gets the name of this GameAction.
     * 
     * @return object of class <code>String</code>
     */
    public String getName() {
        return sName;
    }
    
    /**
     * reset
     * 
     * Resets this GameAction so that it appears like it hasn't been pressed.
     */
    public void reset() {
        iState = iSTATE_RELEASED;
        iAmount = 0;
    }
    
    /**
     * tap
     * 
     * Taps this GameAction. Same as calling press() followed by release().
     */
    public synchronized void tap() {
        press();
        release();
    }
    
    /**
     * press
     * 
     * Signals that the key was pressed.
     */
    public synchronized void press() {
        press(1);
    }
    
    /**
     * press
     * 
     * Signals that the key was pressed a specified number of 
     * times, or that the mouse move a specified distance.
     * 
     * @param iAmount is an object of class <code>Integer</code>
     */
    public synchronized void press(int iAmount) {
        if (iState != iSTATE_WAITING_FOR_RELEASE) {
            this.iAmount+=iAmount;
            iState = iSTATE_PRESSED;
        }

    }
    
    /**
     * release
     * 
     * Signals that the key was released
     */
    public synchronized void release() {
        iState = iSTATE_RELEASED;
    }
    
    /**
     * isPressed
     * 
     * Returns whether the key was pressed or not since last checked.
     * 
     * @return object of class <code>Boolean</code>
     */
    public synchronized boolean isPressed() {
        return (getAmount() != 0);
    }
    
    /**
     * getAmount
     * 
     * For keys, this is the number of times the key was 
     * pressed since it was last checked. 
     * For mouse movement, this is the distance moved.
     * 
     * @return object of class <code>Integer</code>
     */
    public synchronized int getAmount() {
        int retVal = iAmount;
        if (retVal != 0) {
            if (iState == iSTATE_RELEASED) {
                iAmount = 0;
            }
            else if (iBehavior == iDETECT_INITAL_PRESS_ONLY) {
                iState = iSTATE_WAITING_FOR_RELEASE;
                iAmount = 0;
            }
        }
        return retVal;
    }
}
