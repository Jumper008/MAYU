package com.brackeen.javagamebook.input;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 * InputManager
 * 
 * It manages the definition of each object of type <code>InputManager</code>
 * 
 * The InputManager manages input of key and mouse events. 
 * Events are mapped to GameActions.
 * 
 * This class implements multiple interfaces
 * 
 * @author Quazar Volume
 */
public class InputManager implements KeyListener, MouseListener,
    MouseMotionListener, MouseWheelListener
{
    /**
        An invisible cursor.
    */
    public static final Cursor curINVISIBLE_CURSOR =
        Toolkit.getDefaultToolkit().createCustomCursor(
            Toolkit.getDefaultToolkit().getImage(""),
            new Point(0,0),
            "invisible");

    // mouse codes
    public static final int iMOUSE_MOVE_LEFT = 0;
    public static final int iMOUSE_MOVE_RIGHT = 1;
    public static final int iMOUSE_MOVE_UP = 2;
    public static final int iMOUSE_MOVE_DOWN = 3;
    public static final int iMOUSE_WHEEL_UP = 4;
    public static final int iMOUSE_WHEEL_DOWN = 5;
    public static final int iMOUSE_BUTTON_1 = 6;
    public static final int iMOUSE_BUTTON_2 = 7;
    public static final int iMOUSE_BUTTON_3 = 8;

    // number of mouse codes
    private static final int iNUM_MOUSE_CODES = 9;

    // key codes are defined in java.awt.KeyEvent.
    // most of the codes (except for some rare ones like
    // "alt graph") are less than 600.
    private static final int iNUM_KEY_CODES = 600;

    private GameAction[] gaArrKeyActions = new GameAction[iNUM_KEY_CODES];
    private GameAction[] gaArrMouseActions = new GameAction[iNUM_MOUSE_CODES];

    private Point pMouseLocation;
    private Point pCenterLocation;
    private Component comComp;
    private Robot robRobot;
    private boolean bIsRecentering;
    
    /**
     * InputManager
     * 
     * Creates a new InputManager that listens to input from the 
     * specified component.
     * 
     * @param comComp is an object of class <code>Component</code>
     */
    public InputManager(Component comComp) {
        this.comComp = comComp;
        pMouseLocation = new Point();
        pCenterLocation = new Point();

        // register key and mouse listeners
        comComp.addKeyListener(this);
        comComp.addMouseListener(this);
        comComp.addMouseMotionListener(this);
        comComp.addMouseWheelListener(this);

        // allow input of the TAB key and other keys normally
        // used for focus traversal
        comComp.setFocusTraversalKeysEnabled(false);
    }
    
    /**
     * setCursor
     * 
     * Sets the cursor on this InputManager's input component.
     * 
     * @param curCursor is object of class <code>Cursor</code>
     */
    public void setCursor(Cursor curCursor) {
        comComp.setCursor(curCursor);
    }
    
    /**
     * setRelativeMouseMode
     * 
     * Sets whether relative mouse mode is on or not. For 
     * relative mouse mode, the mouse is "locked" in the center 
     * of the screen, and only the changed in mouse movement 
     * is measured. In normal mode, the mouse is free to move 
     * about the screen.
     * 
     * @param bMode is object of class <code>Boolean</code>
     */
    public void setRelativeMouseMode(boolean bMode) {
        if (bMode == isRelativeMouseMode()) {
            return;
        }

        // Checks if bMode is true
        if (bMode) {
            try {
                robRobot = new Robot();
                recenterMouse();
            }
            catch (AWTException ex) {
                // couldn't create robot!
                robRobot = null;
            }
        }
        else {
            robRobot = null;
        }
    }
    
    /**
     * isRelativeMouseMode
     * 
     * Returns whether or not relative mouse mode is on.
     * 
     * @return object of class <code>Boolean</code>
     */
    public boolean isRelativeMouseMode() {
        return (robRobot != null);
    }
    
    /**
     * mapToKey
     * 
     * Maps a GameAction to a specific key. The key codes are 
     * defined in java.awt.KeyEvent. If the key already has 
     * a GameAction mapped to it, the new GameAction overwrites it.
     * 
     * @param gaGameAction is an object of class <code>GameAction</code>
     * @param iKeyCode is an object of class <code>Integer</code>
     */
    public void mapToKey(GameAction gaGameAction, int iKeyCode) {
        gaArrKeyActions[iKeyCode] = gaGameAction;
    }
    
    /**
     * mapToMouse
     * 
     * Maps a GameAction to a specific mouse action. The mouse 
     * codes are defined here in InputManager (MOUSE_MOVE_LEFT, 
     * MOUSE_BUTTON_1, etc). If the mouse action already has 
     * a GameAction mapped to it, the new GameAction overwrites it.
     * 
     * @param gaGameAction is an object of class <code>GameAction</code>
     * @param iMouseCode is an object of class <code>Integer</code>
     */
    public void mapToMouse(GameAction gaGameAction,
        int iMouseCode) {
        gaArrMouseActions[iMouseCode] = gaGameAction;
    }
    
    /**
     * clearMap
     * 
     * Clears all mapped keys and mouse actions to this GameAction.
     * 
     * @param gaGameAction is an object of class <code>GameAction</code>
     */
    public void clearMap(GameAction gaGameAction) {
        // Swipes all game action key array
        for (int i=0; i<gaArrKeyActions.length; i++) {
            if (gaArrKeyActions[i] == gaGameAction) {
                gaArrKeyActions[i] = null;
            }
        }

        // Swipes all game action mouse array
        for (int i=0; i<gaArrMouseActions.length; i++) {
            if (gaArrMouseActions[i] == gaGameAction) {
                gaArrMouseActions[i] = null;
            }
        }

        gaGameAction.reset();
    }
    
    /**
     * getMaps
     * 
     * Gets a List of names of the keys and mouse actions mapped 
     * to this GameAction. Each entry in the List is a String.
     * 
     * @param gaGameCode is an object of class <code>GameAction</code>
     * @return object of class <code>List</code>
     */
    public List getMaps(GameAction gaGameCode) {
        ArrayList ArrList = new ArrayList();

        // Swipes all game action key array
        for (int i=0; i<gaArrKeyActions.length; i++) {
            if (gaArrKeyActions[i] == gaGameCode) {
                ArrList.add(getKeyName(i));
            }
        }

        // Swipes all game action mouse array
        for (int i=0; i<gaArrMouseActions.length; i++) {
            if (gaArrMouseActions[i] == gaGameCode) {
                ArrList.add(getMouseName(i));
            }
        }
        return ArrList;
    }
    
    /**
     * resetAllGameActions
     * 
     * Resets all GameActions so they appear like they haven't been pressed.
     */
    public void resetAllGameActions() {
        // Swipes all game action key array
        for (int i=0; i<gaArrKeyActions.length; i++) {
            if (gaArrKeyActions[i] != null) {
                gaArrKeyActions[i].reset();
            }
        }

        // Swipes all game action mouse array
        for (int i=0; i<gaArrMouseActions.length; i++) {
            if (gaArrMouseActions[i] != null) {
                gaArrMouseActions[i].reset();
            }
        }
    }
    
    /**
     * getKeyName
     * 
     * Gets the name of a key code.
     * 
     * @param iKeyCode is an object of class <code>Integer</code>
     * @return object of class <code>String</code>
     */
    public static String getKeyName(int iKeyCode) {
        return KeyEvent.getKeyText(iKeyCode);
    }
    
    /**
     * getMouseName
     * 
     * Gets the name of a mouse code.
     * 
     * @param iMouseCode is an object of class <code>Integer</code>
     * @return object of class <code>String</code>
     */
    public static String getMouseName(int iMouseCode) {
        switch (iMouseCode) {
            case iMOUSE_MOVE_LEFT: return "Mouse Left";
            case iMOUSE_MOVE_RIGHT: return "Mouse Right";
            case iMOUSE_MOVE_UP: return "Mouse Up";
            case iMOUSE_MOVE_DOWN: return "Mouse Down";
            case iMOUSE_WHEEL_UP: return "Mouse Wheel Up";
            case iMOUSE_WHEEL_DOWN: return "Mouse Wheel Down";
            case iMOUSE_BUTTON_1: return "Mouse Button 1";
            case iMOUSE_BUTTON_2: return "Mouse Button 2";
            case iMOUSE_BUTTON_3: return "Mouse Button 3";
            default: return "Unknown mouse code " + iMouseCode;
        }
    }
    
    /**
     * getMouseX
     * 
     * Gets the x position of the mouse.
     * 
     * @return object of class <code>Integer</code>
     */
    public int getMouseX() {
        return pMouseLocation.x;
    }
    
    /**
     * getMouseY
     * 
     * Gets the y position of the mouse.
     * 
     * @return object of class <code>Integer</code>
     */
    public int getMouseY() {
        return pMouseLocation.y;
    }
    
    /**
     * recenterMouse
     * 
     * Uses the Robot class to try to position the mouse in the 
     * center of the screen. 
     * <p>
     * Note that use of the Robot class may not be available 
     * on all platforms.
     * </p>
     */
    private synchronized void recenterMouse() {
        if (robRobot != null && comComp.isShowing()) {
            pCenterLocation.x = comComp.getWidth() / 2;
            pCenterLocation.y = comComp.getHeight() / 2;
            SwingUtilities.convertPointToScreen(pCenterLocation,
                comComp);
            bIsRecentering = true;
            robRobot.mouseMove(pCenterLocation.x, pCenterLocation.y);
        }
    }

    /**
     * getKeyAction
     * 
     * Gets the game key action of a specific key code if the key code resides
     * within the key action array.
     * 
     * @param keE is an object of class <code>KeyEvent</code>
     * @return object of class <code>GameAction</code>
     */
    private GameAction getKeyAction(KeyEvent keE) {
        int iKeyCode = keE.getKeyCode();
        
        // Verifies if iKeyCode resides inside array
        if (iKeyCode < gaArrKeyActions.length) {
            return gaArrKeyActions[iKeyCode];
        }
        else {
            return null;
        }
    }
    
    /**
     * getMouseButtonCode
     * 
     * Gets the mouse code for the button specified in this MouseEvent.
     * 
     * @param keE is an object of class <code>MouseEvent</code>
     * @return object of class <code>Static Integer</code>
     */
    public static int getMouseButtonCode(MouseEvent keE) {
         switch (keE.getButton()) {
            case MouseEvent.BUTTON1:
                return iMOUSE_BUTTON_1;
            case MouseEvent.BUTTON2:
                return iMOUSE_BUTTON_2;
            case MouseEvent.BUTTON3:
                return iMOUSE_BUTTON_3;
            default:
                return -1;
        }
    }

    /**
     * getMouseButtonAction
     * 
     * Gets the mouse action for the button specified in this MouseEvent.
     * 
     * @param keE is an object of class <code>MouseEvent</code>
     * @return object of class <code>GameAction</code>
     */
    private GameAction getMouseButtonAction(MouseEvent keE) {
        int iMouseCode = getMouseButtonCode(keE);
        // Checks if iMousecode is valid
        if (iMouseCode != -1) {
             return gaArrMouseActions[iMouseCode];
        }
        else {
             return null;
        }
    }
    
    /**
     * keyPressed
     * 
     * Invokes game action of pressed key if it exists.
     * 
     * @param keE is an object of class <code>KeyEvent</code>
     */
    @Override
    public void keyPressed(KeyEvent keE) {
        GameAction gaGameAction = getKeyAction(keE);
        if (gaGameAction != null) {
            gaGameAction.press();
        }
        // make sure the key isn't processed for anything else
        keE.consume();
    }

    /**
     * keyReleased
     * 
     * Signals that the key was released to stop the game action of that
     * specific key.
     * 
     * @param keE is an object of class <code>KeyEvent</code>
     */
    @Override
    public void keyReleased(KeyEvent keE) {
        GameAction gaGameAction = getKeyAction(keE);
        if (gaGameAction != null) {
            gaGameAction.release();
        }
        // make sure the key isn't processed for anything else
        keE.consume();
    }

    /**
     * keyTyped
     * 
     * Does nothing.
     * 
     * @param keE is an object of class <code>KeyEvent</code>
     */
    @Override
    public void keyTyped(KeyEvent keE) {
        // make sure the key isn't processed for anything else
        keE.consume();
    }

    /**
     * mousePressed
     * 
     * Invokes game action of pressed mouse button if it exists.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mousePressed(MouseEvent meE) {
        GameAction gaGameAction = getMouseButtonAction(meE);
        if (gaGameAction != null) {
            gaGameAction.press();
        }
    }

    /**
     * mouseReleased
     * 
     * Signals that the mouse button was released to stop the game action of 
     * that specific mouse button.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mouseReleased(MouseEvent meE) {
        GameAction gaGameAction = getMouseButtonAction(meE);
        if (gaGameAction != null) {
            gaGameAction.release();
        }
    }

    /**
     * mouseClicked
     * 
     * Does nothing.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mouseClicked(MouseEvent meE) {
        // do nothing
    }

    /**
     * mouseEntered
     * 
     * Tracks when the mouse enters the screen.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mouseEntered(MouseEvent meE) {
        mouseMoved(meE);
    }

    /**
     * mouseExited
     * 
     * Tracks when the mouse exits the screen.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mouseExited(MouseEvent meE) {
        mouseMoved(meE);
    }

    /**
     * mouseDragged
     * 
     * Tracks when the mouse is dragged.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mouseDragged(MouseEvent meE) {
        mouseMoved(meE);
    }

    /**
     * mouseMoved
     * 
     * Tracks when the mouse is moved.
     * 
     * @param meE is an object of class <code>MouseEvent</code>
     */
    @Override
    public synchronized void mouseMoved(MouseEvent meE) {
        // this event is from re-centering the mouse - ignore it
        if (bIsRecentering &&
            pCenterLocation.x == meE.getX() &&
            pCenterLocation.y == meE.getY())
        {
            bIsRecentering = false;
        }
        else {
            int dx = meE.getX() - pMouseLocation.x;
            int dy = meE.getY() - pMouseLocation.y;
            mouseHelper(iMOUSE_MOVE_LEFT, iMOUSE_MOVE_RIGHT, dx);
            mouseHelper(iMOUSE_MOVE_UP, iMOUSE_MOVE_DOWN, dy);

            if (isRelativeMouseMode()) {
                recenterMouse();
            }
        }

        pMouseLocation.x = meE.getX();
        pMouseLocation.y = meE.getY();

    }

    /**
     * mouseWheelMoved
     * 
     * Tracks the movement of the mouse wheel and invokes game action
     * 
     * @param mweE is an object of class <code>MouseEvent</code>
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mweE) {
        mouseHelper(iMOUSE_WHEEL_UP, iMOUSE_WHEEL_DOWN,
            mweE.getWheelRotation());
    }

    private void mouseHelper(int iCodeNeg, int iCodePos,
        int iAmount)
    {
        GameAction gaGameAction;
        if (iAmount < 0) {
            gaGameAction = gaArrMouseActions[iCodeNeg];
        }
        else {
            gaGameAction = gaArrMouseActions[iCodePos];
        }
        if (gaGameAction != null) {
            gaGameAction.press(Math.abs(iAmount));
            gaGameAction.release();
        }
    }

}