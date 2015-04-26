package com.brackeen.javagamebook.sound;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;
import com.brackeen.javagamebook.util.ThreadPool;
import com.brackeen.javagamebook.util.LoopingByteInputStream;


/**
 * SoundManager
 *
 * It manages the definition of each object of type <code>SoundManager</code>
 *
 * The SoundManager class manages sound playback. The
 * SoundManager is a ThreadPool, with each thread playing back
 * one sound at a time. This allows the SoundManager to
 * easily limit the number of simultaneous sounds being played.
 *
 * @author Quazar Volume
 *
 */
public class SoundManager extends ThreadPool {

    private AudioFormat afPlaybackFormat;
    private ThreadLocal tlLocalLine;
    private ThreadLocal tlLocalBuffer;
    private Object objPausedLock;
    private boolean bPaused;

    /**
     * SoundManager
     * 
     * Parameterized Constructor
     * 
     * Creates a new SoundManager using the maximum number of
     * simultaneous sounds.
     * 
     * @param afPlaybackFormat is an object of class <code>AudioFormat</code>
     */
    public SoundManager(AudioFormat afPlaybackFormat) {
        this(afPlaybackFormat,
            getMaxSimultaneousSounds(afPlaybackFormat));
    }
    
    /**
     * SoundManager
     * 
     * Parameterized Constructor
     * 
     * Creates a new SoundManager with the specified maximum
     * number of simultaneous sounds.
     * 
     * @param afPlaybackFormat is an object of class <code>AudioFormat</code>
     * @param iMaxSimultaneousSounds is an object of class <code>Integer</code>
     */
    public SoundManager(AudioFormat afPlaybackFormat,
        int iMaxSimultaneousSounds)
    {
        super(Math.min(iMaxSimultaneousSounds,
            getMaxSimultaneousSounds(afPlaybackFormat)));
        this.afPlaybackFormat = afPlaybackFormat;
        tlLocalLine = new ThreadLocal();
        tlLocalBuffer = new ThreadLocal();
        objPausedLock = new Object();
        // notify threads in pool it's ok to start
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * getMaxSimultaneousSounds
     * 
     * Gets the maximum number of simultaneous sounds with the
     * specified AudioFormat that the default mixer can play.
     * 
     * @param afPlaybackFormat is an object of class <code>AudioFormat</code>
     * @return object of class <code>Mixeer</code>
     */
    public static int getMaxSimultaneousSounds(
        AudioFormat afPlaybackFormat)
    {
        DataLine.Info dlLineInfo = new DataLine.Info(
            SourceDataLine.class, afPlaybackFormat);
        Mixer mixMixer = AudioSystem.getMixer(null);
        return mixMixer.getMaxLines(dlLineInfo);
    }

    /**
     * cleanUp
     * 
     * Does any clean up before closing.
     * 
     */
    protected void cleanUp() {
        // signal to unpause
        setPaused(false);

        // close the mixer (stops any running sounds)
        Mixer mixMixer = AudioSystem.getMixer(null);
        if (mixMixer.isOpen()) {
            mixMixer.close();
        }
    }

    /**
     * close
     * 
     * 
     */
    public void close() {
        cleanUp();
        super.close();
    }

    /**
     * join
     * 
     */
    public void join() {
        cleanUp();
        super.join();
    }

    /**
     * setPaused
     * 
     * Sets the paused state. Sounds may not pause immediately.
     * 
     * @param bPaused is an object of class <code>boolean</code>
     */
    public void setPaused(boolean bPaused) {
        if (this.bPaused != bPaused) {
            synchronized (objPausedLock) {
                this.bPaused = bPaused;
                if (!bPaused) {
                    // restart sounds
                    objPausedLock.notifyAll();
                }
            }
        }
    }

    /**
     * isPaused
     * 
     * Returns the paused state.
     * 
     * @return object of class <code>boolean</code>
     */
    public boolean isPaused() {
        return bPaused;
    }

    /**
     * getSound
     * 
     *  Loads a Sound from the file system. Returns null if an
     *  error occurs.
     * 
     * @param sFilename is an object of class <code>String</code>
     * @return object of class <code>Sound</code>
     */
    public Sound getSound(String sFilename) {
        return getSound(getAudioInputStream(sFilename));
    }

    /**
     * getSound
     * 
     * Loads a Sound from an input stream. Returns null if an
     * error occurs.
     * 
     * @param isIs is an object of class <code>InputStream</code>
     * @return object of class <code>Sound</code>
     */
    public Sound getSound(InputStream isIs) {
        return getSound(getAudioInputStream(isIs));
    }
    
    /**
     * getSound
     * 
     * Loads a Sound from an AudioInputStream.
     * 
     * @param aisAudioStream is an object of class <code>AudioInputStream</code>
     * @return object of class <code>Sound</code>
     */
    public Sound getSound(AudioInputStream aisAudioStream) {
        if (aisAudioStream == null) {
            return null;
        }

        // get the number of bytes to read
        int iLength = (int)(aisAudioStream.getFrameLength() *
            aisAudioStream.getFormat().getFrameSize());

        // read the entire stream
        byte[] byteArrSamples = new byte[iLength];
        DataInputStream disIs = new DataInputStream(aisAudioStream);
        try {
            disIs.readFully(byteArrSamples);
            disIs.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        // return the samples
        return new Sound(byteArrSamples);
    }

    /**
     * getAudioInputStream
     * 
     * Creates an AudioInputStream from a sound from the file
     * system.
     * 
     * @param sFilename is an object of class <code>string</code>
     * @return object of class <code>AudioInputStream</code>
     */
    public AudioInputStream getAudioInputStream(String sFilename) {
        try {
            return getAudioInputStream(new FileInputStream(sFilename));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * getAudioInputStream
     * 
     * Creates an AudioInputStream from a sound from an input
     * stream
     * 
     * @param isIs is an object of class <code>Inputstream</code>
     * @return object of class <code>AudioInputStream</code>
     */
    public AudioInputStream getAudioInputStream(InputStream isIs) {

        try {
            if (!isIs.markSupported()) {
                isIs = new BufferedInputStream(isIs);
            }
            // open the source stream
            AudioInputStream aisSource =
                AudioSystem.getAudioInputStream(isIs);

            // convert to playback format
            return AudioSystem.getAudioInputStream(afPlaybackFormat, aisSource);
        }
        catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * play
     * 
     * Plays a sound. This method returns immediately.
     * 
     * @param souSound is an object of class <code>Sound</code>
     * @return object of class <code>Sound</code>
     */
    public InputStream play(Sound souSound) {
        return play(souSound, null, false);
    }

    /**
     * play
     * 
     *  Plays a sound with an optional SoundFilter, and optionally
     *  looping. This method returns immediately.
     * 
     * @param souSound is an object of class <code>Sound</code>
     * @param sfFilter is an object of class <code>SoundFilter</code>
     * @param bLoop is an object of class <code>Boolean</code>
     * @return object of class <code>Sound</code>
     */
    public InputStream play(Sound souSound, SoundFilter sfFilter,
        boolean bLoop)
    {
        InputStream isIs;
        if (souSound != null) {
            if (bLoop) {
                isIs = new LoopingByteInputStream(
                    souSound.getSamples());
            }
            else {
                isIs = new ByteArrayInputStream(souSound.getSamples());
            }

            return play(isIs, sfFilter);
        }
        return null;
    }

    /**
     * play
     * 
     * Plays a sound from an InputStream. This method
     * returns immediately.
     * 
     * @param isIs is an object of class <code>InputStream</code>
     * @return object of class <code>Sound</code>
     */
    public InputStream play(InputStream isIs) {
        return play(isIs, null);
    }

    /**
     * play
     * 
     * Plays a sound from an InputStream with an optional
     * sound filter. This method returns immediately.
     * 
     * @param isIs is an object of class <code>InputStream</code>
     * @param sfFilter is an object of class <code>SoundFilter</code>
     * @return object of class <code>InputStream</code>
     */
    public InputStream play(InputStream isIs, SoundFilter sfFilter) {
        if (isIs != null) {
            if (sfFilter != null) {
                isIs = new FilteredSoundStream(isIs, sfFilter);
            }
            runTask(new SoundPlayer(isIs));
        }
        return isIs;
    }

    /**
     * threadStarted
     * 
     * Signals that a PooledThread has started. Creates the
     * Thread's line and buffer.
     * 
     */
    protected void threadStarted() {
        // wait for the SoundManager constructor to finish
        synchronized (this) {
            try {
                wait();
            }
            catch (InterruptedException ex) { }
        }

        // use a short, 100ms (1/10th sec) buffer for filters that
        // change in real-time
        int iBufferSize = afPlaybackFormat.getFrameSize() *
            Math.round(afPlaybackFormat.getSampleRate() / 10);

        // create, open, and start the line
        SourceDataLine sdlLine;
        DataLine.Info dlLineInfo = new DataLine.Info(
            SourceDataLine.class, afPlaybackFormat);
        try {
            sdlLine = (SourceDataLine)AudioSystem.getLine(dlLineInfo);
            sdlLine.open(afPlaybackFormat, iBufferSize);
        }
        catch (LineUnavailableException ex) {
            // the line is unavailable - signal to end this thread
            Thread.currentThread().interrupt();
            return;
        }

        sdlLine.start();

        // create the buffer
        byte[] byteArrBuffer = new byte[iBufferSize];

        // set this thread's locals
        tlLocalLine.set(sdlLine);
        tlLocalBuffer.set(byteArrBuffer);
    }

    /**
     * threadStopped
     * 
     * Signals that a PooledThread has stopped. Drains and
     * closes the Thread's Line.
     */
    protected void threadStopped() {
        SourceDataLine sdlLine = (SourceDataLine)tlLocalLine.get();
        if (sdlLine != null) {
            sdlLine.drain();
            sdlLine.close();
        }
    }
    
    /**
     * SoundPlayer
     * 
     * It manages the definition of each object of type <code>SoundPlayer</code>
     * 
     * The SoundPlayer class is a task for the PooledThreads to
     * run. It receives the threads's Line and byte buffer from
     * the ThreadLocal variables and plays a sound from an
     * InputStream.
     * 
     */
    protected class SoundPlayer implements Runnable {

        private InputStream isSource;
        
        /**
         * SoundPlayer
         * 
         * Default Constructor 
         * 
         * @param isSource is an object of class <code>InputStream</code>
         */
        public SoundPlayer(InputStream isSource) {
            this.isSource = isSource;
        }
        
        /**
         * run
         * 
         */
        public void run() {
            // get line and buffer from ThreadLocals
            SourceDataLine sdlLine = (SourceDataLine)tlLocalLine.get();
            byte[] byteArrBuffer = (byte[])tlLocalBuffer.get();
            if (sdlLine == null || byteArrBuffer == null) {
                // the line is unavailable
                return;
            }

            // copy data to the line
            try {
                int iNumBytesRead = 0;
                while (iNumBytesRead != -1) {
                    // if paused, wait until unpaused
                    synchronized (objPausedLock) {
                        if (bPaused) {
                            try {
                                objPausedLock.wait();
                            }
                            catch (InterruptedException ex) {
                                return;
                            }
                        }
                    }
                    // copy data
                    iNumBytesRead =
                        isSource.read(byteArrBuffer, 0, byteArrBuffer.length);
                    if (iNumBytesRead != -1) {
                        sdlLine.write(byteArrBuffer, 0, iNumBytesRead);
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}
