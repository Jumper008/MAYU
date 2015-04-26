package com.brackeen.javagamebook.graphics;

import javax.swing.RepaintManager;
import javax.swing.JComponent;

/**
 * NullRepaintManager
 *
 * It manages the definition of each object of type
 * <code>NullRepaintManager</code>
 *
 * The NullRepaintManager is a RepaintManager that doesn't
 * do any repainting. Useful when all the rendering is done
 * manually by the application.
 *
 * @author Quazar Volume
 */
public class NullRepaintManager extends RepaintManager {

    /**
     * install
     *
     * Installs the NullRepaintManager
     */
    public static void install() {
        RepaintManager rmRepaintManager = new NullRepaintManager();
        rmRepaintManager.setDoubleBufferingEnabled(false);
        RepaintManager.setCurrentManager(rmRepaintManager);
    }

    /**
     * addInvalidComponent
     *
     * @param jcCom is an object of class <code>JComponent</code>
     */
    public void addInvalidComponent(JComponent jcCom) {
        // do nothing
    }

    /**
     * addDirtyRegion
     *
     * @param jcCom is an object of class <code>JComponent</code>
     * @param iX is an object of class <code>Integer</code>
     * @param iY is an object of class <code>Integer</code>
     * @param iW is an object of class <code>Integer</code>
     * @param iH is an object of class <code>Integer</code>
     */
    public void addDirtyRegion(JComponent jcCom, int iX, int iY,
        int iW, int iH)
    {
        // do nothing
    }

    /**
     * markCompletelyDirty
     *
     * @param jcCom is an object of class <code>JComponent</code>
     */
    public void markCompletelyDirty(JComponent jcCom) {
        // do nothing
    }

    /**
     * paintDirtyRegions
     */
    public void paintDirtyRegions() {
        // do nothing
    }

}
