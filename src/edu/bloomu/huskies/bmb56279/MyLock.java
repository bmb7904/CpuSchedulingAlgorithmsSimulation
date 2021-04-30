package edu.bloomu.huskies.bmb56279;

/**
 * A custom class that represents a lock, which a Process object can own and release.
 * Obviously, there is very simplified, but it works for my very simplified simulation
 * of scheduling algorithms.
 *
 * @author Brett Bernardi
 */

public class MyLock {
    // The process that owns the lock
    private Process process;
    // boolean that is true of a Process currently has the lock. False otherwise.
    private boolean isLocked;

    /**
     * Initialize constructor with default false for the boolean field.
     */
    public MyLock() {
        this.isLocked = false;
    }

    /**
     * Will give the lock to the Process specified in the parameter.
     * @param p - The process obtaining the lock
     */
    public void lock(Process p) {
        this.process = p;
        this.isLocked = true;
    }

    /**
     * Releases the lock and the Process associated with the lock.
     */
    public void unlock() {
        this.process = null;
        this.isLocked = false;
    }

    /**
     * Returns true if a process currently has the CPU lock.
     * @return true if a process has the lock, false otherwise.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Return the process that holds the lock.
     * @return - The process that holds the lock
     */
    public Process getProcess() {
        return this.process;
    }
}
