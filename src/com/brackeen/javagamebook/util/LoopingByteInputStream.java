package com.brackeen.javagamebook.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * LoopingByteInputStream
 *
 * It manages the definition of each 
 * object of type <code>LoopingByteInputStream</code>
 *
 * The LoopingByteInputStream is a ByteArrayInputStream that
 * loops indefinitly. The looping stops when the close() method
 * is called.
 *
 * @author Quazar Volume
 *
 */
public class LoopingByteInputStream extends ByteArrayInputStream {

    private boolean bClosed;

    /**
        Creates a new LoopingByteInputStream with the specified
        byte array. The array is not copied.
    */
    
    /**
     * LoopingByteInputStream
     * 
     * Creates a new LoopingByteInputStream with the specified
     * byte array. The array is not copied.
     * 
     * @param byteArrBuffer is an array of class <code>byte</code>
     */
    public LoopingByteInputStream(byte[] byteArrBuffer) {
        super(byteArrBuffer);
        bClosed = false;
    }

    /**
     * read
     * 
     * Reads <code>length</code> bytes from the array. If the
     * end of the array is reached, the reading starts over from
     * the beginning of the array. Returns -1 if the array has
     * been closed.
     * 
     * @param byteArrBuffer is an array of class <code>byte</code>
     * @param iOffset is an object of class <code>integer</code>
     * @param iLength is an object of class <code>integer</code>
     * @return 
     */
    public int read(byte[] byteArrBuffer, int iOffset, int iLength) {
        if (bClosed) {
            return -1;
        }
        int iTotalBytesRead = 0;

        while (iTotalBytesRead < iLength) {
            int iNumBytesRead = super.read(byteArrBuffer,
                iOffset + iTotalBytesRead,
                iLength - iTotalBytesRead);

            if (iNumBytesRead > 0) {
                iTotalBytesRead += iNumBytesRead;
            }
            else {
                reset();
            }
        }
        return iTotalBytesRead;
    }

    /**
     * close
     * 
     * Closes the stream. Future calls to the read() methods
     * will return 1.
     * 
     * @throws IOException 
     */
    public void close() throws IOException {
        super.close();
        bClosed = true;
    }

}
