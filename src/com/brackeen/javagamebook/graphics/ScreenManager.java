package com.brackeen.javagamebook.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

/**
 * ScreenManager
 *
 * It manages the definition of each object of type <code>ScreenManager</code>
 *
 * The ScreenManager class manages initializing and displaying
 * full screen graphics modes.
 *
 * @author Quazar Volume
 */
public class ScreenManager {

    private GraphicsDevice gdDevice; // Device object for screen management

    /**
     * ScreenManager
     *
     * Default constructor
     *
     * Creates a new ScreenManager object
     */
    public ScreenManager() {
        GraphicsEnvironment geEnvironment =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        gdDevice = geEnvironment.getDefaultScreenDevice();
    }
    
    /**
     * getCompatibleDisplayModes
     *
     * Returns a list of compatible display modes for the
     * default device on the system.
     *
     * @return object of class <code>DisplayMode</code>
     */
    public DisplayMode[] getCompatibleDisplayModes() {
        return gdDevice.getDisplayModes();
    }
    
    /**
     * findFirstCompatibleMode
     *
     * Returns the first compatible mode in a list of modes.
     * Returns null if no modes are compatible.
     *
     * @param dmModes is an object of class <code>DisplayMode</code>
     * @return object of class <code>DisplayMode</code>
     */
    public DisplayMode findFirstCompatibleMode(
        DisplayMode dmModes[])
    {
        // Get compatible modes
        DisplayMode dmGoodModes[] = gdDevice.getDisplayModes();
        for (int i = 0; i < dmModes.length; i++) {
            for (int j = 0; j < dmGoodModes.length; j++) {
                if (displayModesMatch(dmModes[i], dmGoodModes[j])) {
                    return dmModes[i]; // Returns first compatible mode
                }
            }

        }

        return null;
    }

    /**
     * getCurrentDisplayMode
     *
     * Returns the current display mode
     *
     * @return object of class <code>DisplayMode</code>
     */
    public DisplayMode getCurrentDisplayMode() {
        return gdDevice.getDisplayMode();
    }

    /**
     * displayModesMatch
     *
     * Determines if two display modes "match". Two display
     * modes match if they have the same resolution, bit depth,
     * and refresh rate. The bit depth is ignored if one of the
     * modes has a bit depth of DisplayMode.BIT_DEPTH_MULTI.
     * Likewise, the refresh rate is ignored if one of the
     * modes has a refresh rate of
     * DisplayMode.REFRESH_RATE_UNKNOWN.
     *
     * @param dmMode1 is an object of class <code>DisplayMode</code>
     * @param dmMode2 is an object of class <code>DisplayMode</code>
     * @return object of class <code>Boolean</code>
     */
    public boolean displayModesMatch(DisplayMode dmMode1,
        DisplayMode dmMode2)

    {
        // Compares display's width
        if (dmMode1.getWidth() != dmMode2.getWidth() ||
            dmMode1.getHeight() != dmMode2.getHeight())
        {
            return false;
        }

        // Compares display's bit depth
        if (dmMode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
            dmMode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
            dmMode1.getBitDepth() != dmMode2.getBitDepth())
        {
            return false;
        }

        // Compares display's refresh rate
        if (dmMode1.getRefreshRate() !=
            DisplayMode.REFRESH_RATE_UNKNOWN &&
            dmMode2.getRefreshRate() !=
            DisplayMode.REFRESH_RATE_UNKNOWN &&
            dmMode1.getRefreshRate() != dmMode2.getRefreshRate())
         {
             return false;
         }

         return true;
    }
    
    /**
     * setFullScreen
     *
     * Enters full screen mode and changes the display mode.
     * If the specified display mode is null or not compatible
     * with this device, or if the display mode cannot be
     * changed on this system, the current display mode is used.
     * <p>
     * The display uses a BufferStrategy with 2 buffers.
     * </p>
     *
     * @param dmDisplayMode is an object of class <code>DisplayMode</code>
     */
    public void setFullScreen(DisplayMode dmDisplayMode) {
        final JFrame jfFrame = new JFrame();
        jfFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfFrame.setUndecorated(true);
        jfFrame.setIgnoreRepaint(true);
        jfFrame.setResizable(false);

        gdDevice.setFullScreenWindow(jfFrame);

        if (dmDisplayMode != null &&
            gdDevice.isDisplayChangeSupported())
        {
            try {
                gdDevice.setDisplayMode(dmDisplayMode);
            }
            catch (IllegalArgumentException ex) { }
            // fix for mac os x
            jfFrame.setSize(dmDisplayMode.getWidth(),
                dmDisplayMode.getHeight());
        }
        // avoid potential deadlock in 1.4.1_02
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    jfFrame.createBufferStrategy(2);
                }
            });
        }
        catch (InterruptedException ex) {
            // ignore
        }
        catch (InvocationTargetException  ex) {
            // ignore
        }


    }
    
    /**
     * getGraphics
     *
     * Gets the graphics context for the display. The
     * ScreenManager uses double buffering, so applications must
     * call update() to show any graphics drawn.
     * <p>
     * The application must dispose of the graphics object.
     * </p>
     *
     * @return object of class <code>Graphics2D</code>
     */
    public Graphics2D getGraphics() {
        Window winWindow = gdDevice.getFullScreenWindow();
        if (winWindow != null) {
            BufferStrategy strategy = winWindow.getBufferStrategy();
            return (Graphics2D)strategy.getDrawGraphics();
        }
        else {
            return null;
        }
    }
    
    /**
     * update
     *
     * Updates the display
     */
    public void update() {
        Window winWindow = gdDevice.getFullScreenWindow();
        if (winWindow != null) {
            BufferStrategy strategy = winWindow.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
    }
    
    /**
     * getFullScreenWindow
     *
     * Returns the window currently used in full screen mode.
     * Returns null if the device is not in full screen mode.
     *
     * @return object of class <code>JFrame</code>
     */
    public JFrame getFullScreenWindow() {
        return (JFrame)gdDevice.getFullScreenWindow();
    }
    
    /**
     * getWidth
     *
     * Returns the width of the window currently used in full
     * screen mode. Returns 0 if the device is not in full screen mode.
     *
     * @return object of class <code>Integer</code>
     */
    public int getWidth() {
        Window winWindow = gdDevice.getFullScreenWindow();
        // Checks if device is in full screen mode
        if (winWindow != null) {
            return winWindow.getWidth();
        }
        else {
            return 0;
        }
    }
    
    /**
     * getHeight
     *
     * Returns the height of the window currently used in full
     * screen mode. Returns 0 if the device is not in full screen mode.
     *
     * @return object of class <code>Integer</code>
     */
    public int getHeight() {
        Window winWindow = gdDevice.getFullScreenWindow();
        // Checks if device is in full screen mode
        if (winWindow != null) {
            return winWindow.getHeight();
        }
        else {
            return 0;
        }
    }
    
    /**
     * restoreScreen
     *
     * Restores the screen's display mode.
     */
    public void restoreScreen() {
        Window winWindow = gdDevice.getFullScreenWindow();
        // Checks if device is in full screen mode
        if (winWindow != null) {
            winWindow.dispose();
        }
        gdDevice.setFullScreenWindow(null);
    }
    
    /**
     * createCompatibleImage
     *
     * Creates an image compatible with the current display.
     *
     * @param iW is an object of class <code>Integer</code>
     * @param iH is an object of class <code>Integer</code>
     * @param iTransparancy is an object of class <code>Integer</code>
     * @return object of class <code>BufferedImage</code>
     */
    public BufferedImage createCompatibleImage(int iW, int iH,
        int iTransparancy)
    {
        Window window = gdDevice.getFullScreenWindow();
        // Checks if device is in full screen mode
        if (window != null) {
            GraphicsConfiguration gc =
                window.getGraphicsConfiguration();
            return gc.createCompatibleImage(iW, iH, iTransparancy);
        }
        return null;
    }
}
