package edu.bloomu.huskies.bmb56279;

/**
 * A custom class that represents a process in an Operating System. The life of a
 * process can be defined by the five different states: new, ready, waiting, running,
 * terminating. After a process is created and ready to be run, it is placed onto the
 * ready queue, where it will wait until the OS determines it is time for it to get the
 * CPU for execution. The process may be preempted and taken off the CPU before it is
 * finished to wait for I/O, and then once it's ready to be run it is placed back onto
 * the ready queue. This cycle may happen multiple times: the process
 * vacillates between the ready state, the running state, and the waiting state before
 * it is terminated.
 *
 * Each process has a unique identification string and this, as well as the time the
 * process first enters the ready queue, are
 * stored as fields of the class. Each instance of this class ( a process) also has a
 * known CPU burst length, which is the amount of time
 * spent being executed by the CPU. We assume that each process will only have one single
 * CPU burst. The waiting time of a process is defined as the amount of time spent on
 * the ready queue. The waiting time and the remaining CPU will be calculated
 * dynamically through methods of this class.
 *
 * @author Brett Bernardi
 */
public class Process {

    // the exact time the process entered the ready queue
    private final int arrivalTime;
    // the amount of time the process spent on the ready queue
    private int waitingTime;
    // process ID
    private final char PID;
    // the Process's burst length
    private final int burstLength;
    // The amount of time remaining in this process's sole CPU burst. A process that
    // has yet to be put on the CPU will have a cpuTimeRemaining = to CPU burst length.
    private int cpuTimeRemaining;
    // process state
    private ProcessState state;

    /**
     * Constructor that initializes a Process instance and initializes all fields that
     * are appropriate. The CPU time remaining is equal to the burst length before the
     * Process ever gets to the CPU.
     * @param n - char the ID of the process
     * @param arrivalTime - The time the process gets added to the ready queue
     * @param burstLength - The length of a processes' sole cpu burst.
     */
    public Process(char n, int arrivalTime, int burstLength) {
        this.PID = n;
        this.arrivalTime = arrivalTime;
        this.burstLength = burstLength;
        // these are equal initially
        this.cpuTimeRemaining = burstLength;
    }

    /**
     * Getter for the Process ID
     * @return String - the Process ID
     */
    public char getID() {
        return PID;
    }

    /**
     * Have this process execute on the CPU for one unit of time. If the execution was
     * successful, return true. If unsuccessful (possible if there is no remaining
     * cpuTime) return false.
     * @return - true if successful execution, false otherwise
     */
    public boolean execute() {
        if(this.cpuTimeRemaining > 0) {
            // decrement one unit of time from the cpuTimeRemaining
            this.cpuTimeRemaining --;
            return true;
        }
        return false;

    }

    /**
     * Getter for the remaining CPU time
     * @return int - CPUTime
     */
    public int getCPUTime() {
        return this.cpuTimeRemaining;
    }

    /**
     * Getter for Process Arrival Time
     * @return int - Arrival Time
     */
    public int getArrivalTime() {
        return this.arrivalTime;
    }

    /**
     * Overrides toString(). Used for testing purposes.
     * @return String - representation of the Process
     */
    @Override
    public String toString() {
        return "Process: " + this.PID + " Arrival Time: " + this.arrivalTime;
    }

    /**
     * This will increment the waiting time by one unit of time for every method call.
     */
    public void incrementWaitingTime() {
        this.waitingTime++;
    }

    /**
     * Getter for waiting time field.
     * @return int - the waiting time
     */
    public int getWaitingTime() {
        return this.waitingTime;
    }

    /**
     * Setter for the state of the process
     * @param s - ProcessType
     */
    public void setState(ProcessState s) {
        this.state = s;
    }

    /**
     * Getting for the state of the process
     * @return ProcessType - The process type
     */
    public ProcessState getState() {
        return this.state;
    }
}



