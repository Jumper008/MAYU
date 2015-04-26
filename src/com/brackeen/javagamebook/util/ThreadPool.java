package com.brackeen.javagamebook.util;
import java.util.LinkedList;

/**
 * ThreadPool
 *
 * It manages the definition of each object of type <code>ThreadPool</code>
 *
 * A thread pool is a group of a limited number of threads that
 * are used to execute tasks.
 *
 * @author Quazar Volume
 *
 */
public class ThreadPool extends ThreadGroup {

    private boolean bIsAlive;
    private LinkedList lklTaskQueue;
    private int iThreadID;
    private static int iThreadPoolID;

    /**
     * ThreadPool
     * 
     * Parameterized Constructor
     * 
     * Creates a new ThreadPool.
     * 
     * @param iNumThreads is an object of class <code>integer</code>
     */
    public ThreadPool(int iNumThreads) {
        super("ThreadPool-" + (iThreadPoolID++));
        setDaemon(true);

        bIsAlive = true;

        lklTaskQueue = new LinkedList();
        for (int i=0; i<iNumThreads; i++) {
            new PooledThread().start();
        }
    }


    /**
     * runTask
     * 
     * Requests a new task to run. This method returns
     * immediately, and the task executes on the next available
     * idle thread in this ThreadPool.
     * 
     * @param runTask is an object of class <code>Runnable</code>
     */
    public synchronized void runTask(Runnable runTask) {
        if (!bIsAlive) {
            throw new IllegalStateException();
        }
        if (runTask != null) {
            lklTaskQueue.add(runTask);
            notify();
        }

    }

    /**
     * getTask
     * 
     * @return object of class <code>Runnable</code>
     * @throws InterruptedException 
     */
    protected synchronized Runnable getTask()
        throws InterruptedException
    {
        while (lklTaskQueue.size() == 0) {
            if (!bIsAlive) {
                return null;
            }
            wait();
        }
        return (Runnable)lklTaskQueue.removeFirst();
    }

    /**
     * close
     * 
     * Closes this ThreadPool and returns immediately. All
     * threads are stopped, and any waiting tasks are not
     * executed. Once a ThreadPool is closed, no more tasks can
     * be run on this ThreadPool.
     * 
     */
    public synchronized void close() {
        if (bIsAlive) {
            bIsAlive = false;
            lklTaskQueue.clear();
            interrupt();
        }
    }

    /**
     * join
     * 
     * Closes this ThreadPool and waits for all running threads
     * to finish. Any waiting tasks are executed.
     * 
     */
    public void join() {
        // notify all waiting threads that this ThreadPool is no
        // longer alive
        synchronized (this) {
            bIsAlive = false;
            notifyAll();
        }

        // wait for all threads to finish
        Thread[] thrArrThreads = new Thread[activeCount()];
        int iCount = enumerate(thrArrThreads);
        for (int iI=0; iI<iCount; iI++) {
            try {
                thrArrThreads[iI].join();
            }
            catch (InterruptedException ex) { }
        }
    }

    /**
     * threadStarted
     * 
     * Signals that a PooledThread has started. This method
     * does nothing by default; subclasses should override to do
     * any thread-specific startup tasks
     */
    protected void threadStarted() {
        // do nothing
    }

    /**
     * threadStopped
     * 
     * Signals that a PooledThread has stopped. This method
     * does nothing by default; subclasses should override to do
     * any thread-specific cleanup tasks.
     */
    protected void threadStopped() {
        // do nothing
    }

    /**
     * PooledThread
     * 
     * It manages the definition of each object
     * of type <code>PooledThread</code>
     * 
     * A PooledThread is a Thread in a ThreadPool group, designed
     * to run tasks (Runnables).
     */
    private class PooledThread extends Thread {

        /**
         * PooledThread
         * 
         * Default Constructor  
         */
        public PooledThread() {
            super(ThreadPool.this,
                "PooledThread-" + (iThreadID++));
        }

        /**
         * run
         * 
         */
        public void run() {
            // signal that this thread has started
            threadStarted();

            while (!isInterrupted()) {

                // get a task to run
                Runnable runTask = null;
                try {
                    runTask = getTask();
                }
                catch (InterruptedException ex) { }

                // if getTask() returned null or was interrupted,
                // close this thread.
                if (runTask == null) {
                    break;
                }

                // run the task, and eat any exceptions it throws
                try {
                    runTask.run();
                }
                catch (Throwable t) {
                    uncaughtException(this, t);
                }
            }
            // signal that this thread has stopped
            threadStopped();
        }
    }
}