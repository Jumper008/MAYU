package com.brackeen.javagamebook.test;

import java.awt.*;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.ScreenManager;

/**
 * GameCore
 * 
 * It manages the definition of each object of type <code>GameCore</code>
 * 
 * Simple abstract class used for testing. Subclasses should 
 * implement the draw() method.
 * 
 * @author Quazar Volume
 */
public abstract class GameCore {

    // Font size
    protected static final int iFONT_SIZE = 24;

    // Array of possible display modes
    private static final DisplayMode dmArrPOSSIBLE_MODES[] = {
        new DisplayMode(800, 600, 16, 0),
        new DisplayMode(800, 600, 32, 0),
        new DisplayMode(800, 600, 24, 0),
        new DisplayMode(640, 480, 16, 0),
        new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 24, 0),
        new DisplayMode(1024, 768, 16, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(1024, 768, 24, 0),
    };

    private boolean bIsRunning;
    protected ScreenManager smScreen;
    
    /**
     * stop
     * 
     * Signals the game loop that it's time to quit
     */
    public void stop() {
        bIsRunning = false;
    }
    
    /**
     * run
     * 
     * Calls init() and gameLoop()
     */
    public void run() {
        try {
            init();
            gameLoop();
        }
        finally {
            smScreen.restoreScreen();
            lazilyExit();
        }
    }
    
    /**
     * lazilyExit
     * 
     * Exits the VM from a daemon thread. The daemon thread waits 
     * 2 seconds then calls System.exit(0). Since the VM should 
     * exit when only daemon threads are running, this makes sure 
     * System.exit(0) is only called if necessary. It's necessary 
     * if the Java Sound system is running.
     */
    public void lazilyExit() {
        Thread thrThread = new Thread() {
            public void run() {
                // first, wait for the VM exit on its own.
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException ex) { }
                // system is still running, so force an exit
                System.exit(0);
            }
        };
        thrThread.setDaemon(true);
        thrThread.start();
    }
    
    /**
     * init
     * 
     * Sets full screen mode and initiates and objects.
     */
    public void init() {
        // Manages screen
        smScreen = new ScreenManager();
        DisplayMode dmDisplayMode =
            smScreen.findFirstCompatibleMode(dmArrPOSSIBLE_MODES);
        smScreen.setFullScreen(dmDisplayMode);

        // Manages windows
        Window winWindow = smScreen.getFullScreenWindow();
        winWindow.setFont(new Font("Dialog", Font.PLAIN, iFONT_SIZE));
        winWindow.setBackground(Color.blue);
        winWindow.setForeground(Color.white);

        bIsRunning = true;
    }

    /**
     * loadImage
     * 
     * Loads specified image.
     * 
     * @param sFileName is an object of class <code>String</code>
     * @return object of class <code>Image</code>
     */
    public Image loadImage(String sFileName) {
        return new ImageIcon(sFileName).getImage();
    }
    
    /**
     * gameLoop
     * 
     * Runs through the game loop until stop() is called.
     */
    public void gameLoop() {
        long lStartTime = System.currentTimeMillis();
        long lCurrTime = lStartTime;

        while (bIsRunning) {
            long lElapsedTime =
                System.currentTimeMillis() - lCurrTime;
            lCurrTime += lElapsedTime;

            // update
            update(lElapsedTime);

            // draw the screen
            Graphics2D gra2D_G = smScreen.getGraphics();
            draw(gra2D_G);
            gra2D_G.dispose();
            smScreen.update();

            // don't take a nap! run as fast as possible
            /*try {
                Thread.sleep(20);
            }
            catch (InterruptedException ex) { }*/
        }
    }
    
    /**
     * update
     * 
     * Updates the state of the game/animation based on the 
     * amount of elapsed time that has passed.
     * 
     * @param lElapsedTime is an object of class <code>Long</code>
     */
    public void update(long lElapsedTime) {
        // do nothing
    }
    
    /**
     * draw
     * 
     * Draws to the screen. Subclasses must override this method.
     * 
     * @param gra2D_G is an object of class <code>Graphics2D</code>
     */
    public abstract void draw(Graphics2D gra2D_G);
}
