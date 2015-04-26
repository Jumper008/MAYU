package com.brackeen.javagamebook.sound;

import java.io.*;
import javax.sound.midi.*;

/**
 * MidiPlayer
 *
 * It manages the definition of each object of type <code>MidiPlayer</code>

 * @author Quazar Volume
 *
 */

public class MidiPlayer implements MetaEventListener {

    // Midi meta event
    public static final int iEND_OF_TRACK_MESSAGE = 47;

    private Sequencer seqSequencer;
    private boolean bLoop;
    private boolean bPaused;
    
    /**
     * MidiPlayer
     * 
     * Default Constructor
     * 
     * Creates a new MidiPlayer object.
     * 
     */
    public MidiPlayer() {
        try {
            seqSequencer = MidiSystem.getSequencer();
            seqSequencer.open();
            seqSequencer.addMetaEventListener(this);
        }
        catch ( MidiUnavailableException ex) {
            seqSequencer = null;
        }
    }

    /**
     * getSequence
     * 
     * Loads a sequence from the file system. Returns null if
     * an error occurs.
     * 
     * @param sFilename is an object of class <code>String</code>
     * @return an object of class <code>sequence</code>
     */
    public Sequence getSequence(String sFilename) {
        try {
            return getSequence(new FileInputStream(sFilename));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * getSequence
     * 
     * Loads a sequence from an input stream. Returns null if
     * an error occurs.
     *
     * @param isIs is an object of class <code>InputStream</code>
     * @return an object of class <code>sequence</code>
     */
    public Sequence getSequence(InputStream isIs) {
        try {
            if (!isIs.markSupported()) {
                isIs = new BufferedInputStream(isIs);
            }
            Sequence seqS = MidiSystem.getSequence(isIs);
            isIs.close();
            return seqS;
        }
        catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * play
     * 
     * Plays a sequence, optionally looping. This method returns
     * immediately. The sequence is not played if it is invalid.
     * 
     * @param seqSequence is an object of class <code>Sequence</code>
     * @param bLoop is an object of class <code>Boolean</code>
     */
    public void play(Sequence seqSequence, boolean bLoop) {
        if (seqSequencer != null && seqSequence != null && seqSequencer.isOpen()) {
            try {
                seqSequencer.setSequence(seqSequence);
                seqSequencer.start();
                this.bLoop = bLoop;
            }
            catch (InvalidMidiDataException ex) {
                ex.printStackTrace();
            }
        }
    }

  
    /**
     * meta
     * 
     * This method is called by the sound system when a meta
     * event occurs. In this case, when the end-of-track meta
     * event is received, the sequence is restarted if
     * looping is on.
     * 
     * @param mmEvent is an object of class <code>MetaMessage</code>
     */
    public void meta(MetaMessage mmEvent) {
        if (mmEvent.getType() == iEND_OF_TRACK_MESSAGE) {
            if (seqSequencer != null && seqSequencer.isOpen() && bLoop) {
                seqSequencer.start();
            }
        }
    }


    /**
     * stop
     * 
     * Stops the sequencer and resets its position to 0.
     * 
     */
    public void stop() {
         if (seqSequencer != null && seqSequencer.isOpen()) {
             seqSequencer.stop();
             seqSequencer.setMicrosecondPosition(0);
         }
    }

    /**
     * close
     * 
     * Closes the sequencer.
     * 
     */
    public void close() {
         if (seqSequencer != null && seqSequencer.isOpen()) {
             seqSequencer.close();
         }
    }

    /**
     * Sequencer
     * 
     * Gets the sequencer.
     * 
     * @return an object of class <code>Sequencer</code>
     */
    public Sequencer getSequencer() {
        return seqSequencer;
    }


    /**
     * setPaused
     * 
     * Sets the paused state. Music may not immediately pause.
     * 
     * @param bPaused is an object of class <code>Boolean</code>
     */
    public void setPaused(boolean bPaused) {
        if (this.bPaused != bPaused && seqSequencer != null && seqSequencer.isOpen()) {
            this.bPaused = bPaused;
            if (bPaused) {
                seqSequencer.stop();
            }
            else {
                seqSequencer.start();
            }
        }
    }


    /**
     * isPaused
     * 
     * Returns the paused state.
     * 
     * @return an object of class <code>Boolean</code>
     */
    public boolean isPaused() {
        return bPaused;
    }

}
