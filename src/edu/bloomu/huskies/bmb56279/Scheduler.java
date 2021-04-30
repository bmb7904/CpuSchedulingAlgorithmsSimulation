package edu.bloomu.huskies.bmb56279;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A custom class whose objects represent an Operating System's scheduler. A scheduler
 * schedules processes waiting on the ready queue for execution according to a set of
 * rules. This scheduler will take a work load in its constructor, and store the work
 * load in a list. It will then have methods to schedule the processes in the workload
 * and simulate their execution according to three different scheduling algorithms:
 *          1.) First Come, First Serve
 *          2.) Shortest Job First (Non-Preemptive)
 *          3.) Shortest Job First (Preemptive) a.k.a Shortest Remaining Time First
 *
 * After the appropriate instance method is called upon the workload to simulate
 * scheduling and execution, methods from this class to calculate and retrieve the
 * execution schedule and get the average waiting time are available.
 *
 * Pre-conditions: Assume each process only has one CPU burst of a known length. Assume
 * we are using a single core CPU. Assume that no processes will be changed to the
 * waiting state (waiting for I/O as an example). Assume there is no dispatch
 * latency and context switches are instantaneous. Also, assume no processes are sharing
 * any data kernel data, thus we won't have to worry about race conditions in preemptive
 * scheduling algorithms.
 *
 *
 * In this implementation, I decided to create a custom MyLock object that acts as a
 * lock for the CPU. Because we are assuming this Scheduler is running on a single core
 * CPU, only one process can be executed on the CPU at any given time. MyLock is very
 * simplified, and so it will not block any Processes trying to obtain the
 * lock if it is currently held by another process, but through the code you can see
 * that MyLock is mutually exclusive in that no more than one Process can hold it at
 * any one time. Preemptive and Non-preemptive scheduling use the lock in different
 * ways, however, it remains true that only one process can possess the lock at any
 * given time for both.
 *
 * CPU-scheduling decisions that take place under the following circumstances:
 *
 * 1.) When a process switches from the running state to the waiting state (for
 * example, as the result of an I/O request or an invocation of wait() for the
 * termination of a child process)
 *
 * 2.) When a process terminates
 *
 *  are referred to as Non-Preemptive scheduling. Because of the simplified
 *  implementation of our scheduler, there are no processes that will enter the waiting
 *  state. Thus, for Non-Preemptive scheduling, decisions about scheduling are
 *  made when a process terminates.
 *
 *
 *  CPU-scheduling decisions that take place under the following circumstances:
 *
 *  1.) When a process switches from the running state to the ready state (for
 *  example, when an interrupt occurs)
 *
 *  2.) When a process switches from the waiting state to the ready state (for
 *  example, at completion of I/O)
 *
 *  3.) When a new process enters the ready state
 *
 *  are referred to as Preemptive scheduling. In our simplified example of the this
 *  Scheduler class, no processes will enter the waiting state or wait for I/O. Thus,
 *  for preemptive scheduling, scheduling decisions are made only when a new process is
 *  added to the ready queue and changes to the ready state. This means that a process
 *  may be preempted by another process of a higher priority, even if it is not
 *  finished executing and it still has some CPU time remaining.
 *
 * @author Brett Bernardi
 */
public class Scheduler {
    // A priority queue is implemented as a binary heap, and thus, it is sorted in
    // the sense that the "head" is always the least, but the entire list cannot be
    // guaranteed to be "in order" at any given time.
    // The ready queue that processes are put on when they arrive and are in the ready
    // state
    private PriorityQueue<Process> readyQueue;
    // the sum of all processes' CPU burst length
    int totalExecutionTime;
    // number of processes in a work load
    int numProcesses;
    // A list that holds all the processes in this work load
    // Does not change throughout the simulation
    private final ArrayList<Process> jobQueue;
    // The schedule of Processes to be executed.
    private char[] schedule;
    // The type of algorithm to be used to schedule this work load
    private final SchedulingType type;

    /**
     * Constructor for the Scheduler class that takes in a work load of processes
     * represented in a String array of user input. Takes this array and converts to an
     * ArrayList of process objects. Will Throw a custom Exception (ImproperArguments) if
     * there are not an even number of arguments supplied by the user. Each process
     * takes the form: (Arrival Time, CPU Burst Length).
     * @param workLoad - a String[] array of user input
     * @param type - type of scheduling(enum) specified
     * @throws ImproperArguments - thrown if arguments are invalid
     */
    public Scheduler(String[] workLoad, SchedulingType type) throws ImproperArguments {
        this.type = type;
        char tempPID = 'A';
        this.numProcesses = 0;
        this.totalExecutionTime = 0;
        this.jobQueue = new ArrayList<>();
        // convert array of user input into an ArrayList of Process objects
        if (workLoad.length % 2 == 0) {
            for (int i = 0; i < workLoad.length - 1; i += 2) {
                Process process = new Process(tempPID, Integer.parseInt(workLoad[i]),
                        Integer.parseInt(workLoad[i + 1]));
                process.setState(ProcessState.NEW);
                jobQueue.add(process);
                tempPID++;
                this.numProcesses++;
                this.totalExecutionTime += process.getCPUTime();
            }

        }
        else {
            throw new ImproperArguments("Every Process needs an arrival time and a CPU " +
                    "burst length!. Try again!");
        }

        this.schedule = new char[this.totalExecutionTime];
        // enhanced switch statement that will pass the appropriate Comparator to the
        // constructor of the the newly created readyQueue, depending on the specified
        // type.
        switch(this.type) {
            case FCFS -> this.readyQueue = new PriorityQueue<>(new FCFSComparator());
            case SRTF, SJFNP -> this.readyQueue = new PriorityQueue<>(new SJFComparator());
        }
    }

    /**
     * This method will be used for non-preemptive, priority algorithms. Non-preemptive
     * means that once a process begins execution, it cannot be preempted by another
     * process, even if that other process has a higher priority according to the rules.
     * Both FCFS and SJFNP can use this method, as the only distinction between the two
     * is the rules setting the priority queue, and that is handled by which ever
     * comparator is passed into the constructor during the creation of the priority
     * queue representing the ready queue. In either case, Processes are added to the
     * ready queue as their arrival time states, and the process with the highest
     * priority will be executed until it is completed (it has the terminated state.)
     *
     * This method uses a class called MyLock, which contains a reference to a Process
     * that owns the lock on the CPU. Once a process is given the lock to the CPU, it
     * cannot be released until the process has completed execution. In other words,
     * scheduling decisions are made only when a process finishes execution and the
     * lock is released.
     */
    private void nonPreemptiveScheduleAndExecute() {
        // the unit of time. Each loop will take one unit of time
        int time = 0;
        int scheduleIndex = 0;
        // The lock on the CPU
        MyLock lock = new MyLock();

        // loop will break out only when all processes are finished executing (in other
        // words, all processes will be in the terminated state).
        while(!allProcessesTerminated()) {
            // check for any processes whose arrival times are now. Set state to ready,
            // and then add the process to the ready queue
            for(Process p: jobQueue) {
                if(p.getArrivalTime() == time && p.getState() == ProcessState.NEW) {
                    p.setState(ProcessState.READY);
                    readyQueue.add(p);
                }
            }

            // If no process is currently in possession of the lock on the CPU and
            // there is a ready process on the ready queue, make your scheduling
            // decisions by giving the Process on the ready queue with the highest
            // priority the lock.
            // The only time a scheduling decision will be made about which process to
            // give the CPU is when no process currently has the lock, or in other
            // words, after a process terminates and the lock is released. This is the key
            // to Non-preemptive scheduling.
            if (!lock.isLocked() && !readyQueue.isEmpty()) {
                // get the head process according to FCFS
                Process headProcess = readyQueue.poll();
                // set state
                headProcess.setState(ProcessState.RUNNING);
                // obtain the lock on the CPU
                lock.lock(headProcess);
            }

            if(lock.isLocked()) {
                // whichever process has the lock, execute for one unit of time
                lock.getProcess().execute();
                // update schedule and schedule index
                this.schedule[scheduleIndex++] = lock.getProcess().getID();
                // all other processes in the ready queue are waiting
                for (Process p : readyQueue) {
                    p.incrementWaitingTime();
                }

                // check if current process that owns the lock is finished executing.
                if (lock.getProcess().getCPUTime() == 0) {
                    // set state to terminated
                    lock.getProcess().setState(ProcessState.TERMINATED);
                    // release the lock for the next available process
                    lock.unlock();
                }
            }

            // increment time counter for next iteration
            time++;
        }
    }

    /**
     * Preemptively schedules and executes the workload using a priority queue. The
     * type of priority queue is specified in the constructor of this class, by passing
     * a Comparator. In preemptive scheduling, no single process gets the lock on the
     * CPU for any amount of time longer than one unit of time. This means that a
     * currently executing process may be preempted by another process of a higher
     * priority, even before that process is finished executing. In this example,
     * Preeemptive SJF is the algorithm that uses this method, but other
     * preemptive priority based algorithms can also use this in the future (with the
     * appropriate Comparator).
     */
    private void preemptiveScheduleAndExecute() {
        // each cycle of the loop take one unit of time
        int time = 0;
        // index for the schedule array
        int scheduleIndex = 0;
        MyLock lock = new MyLock();

        // loop breaks out when all processes are in the terminated state
        while(!allProcessesTerminated()) {

            // Add process to ready queue at the appropriate time
            for(Process p: jobQueue) {
                if(p.getArrivalTime() == time && p.getState() == ProcessState.NEW) {
                    p.setState(ProcessState.READY);
                    readyQueue.add(p);
                }
            }
            // Preemptive scheduling schedules processes when processes change state
            // from new to ready
            // First check if ready queue is not empty, and try to preempt the process
            // that currently has the lock.
            if(!readyQueue.isEmpty()) {
                lock.lock(readyQueue.poll());
            }
            // A process should have a lock at this point. It only won't if there are
            // no processes ready, which in that case it will
            if(lock.isLocked()) {
                lock.getProcess().execute();
                lock.getProcess().setState(ProcessState.RUNNING);
                this.schedule[scheduleIndex++] = lock.getProcess().getID();

                // all other processes in the ready queue are waiting
                for (Process p : readyQueue) {
                    p.incrementWaitingTime();
                }
                // Put the process back onto the ready queue if it is not finished
                // executing.
                if(lock.getProcess().getCPUTime() != 0) {
                    lock.getProcess().setState(ProcessState.READY);
                    readyQueue.add(lock.getProcess());
                }
                // If process is finished executing, set state to Terminated.
                else {
                    lock.getProcess().setState(ProcessState.TERMINATED);
                }
                // release lock
                lock.unlock();

            }
            // increment time counter
            time++;


        }
    }

    /**
     * A public method that will simulate the scheduling and execution of the workload
     * associated with this object. Will call the appropriate private simulate method,
     * based on the supplied type of algorithm specified in the Constructor for this
     * object.
     */
    public void simulate() {
        // enhanced switch statement
        // no fall-through behavior
        switch(this.type) {
            case FCFS, SJFNP -> this.nonPreemptiveScheduleAndExecute();
            case SRTF -> this.preemptiveScheduleAndExecute();
        }
    }

    /**
     * Will return the schedule of execution of each Process. The size of this char[]
     * will be equal to the totalExecutionTime.
     *
     * @return char[] - The schedule of execution
     */
    public char[] getSchedule() {
        return this.schedule;
    }

    /**
     * Calculates and returns the average waiting time of each process in Work Load
     * that was passed in the constructor, and is now stored in the ArrayList of all
     * processes.
     *
     * @return double - the Avg. Waiting Time.
     */
    public double getAvgWaitTime() {
        double sum = 0;
        for(Process p: jobQueue) {
            sum += p.getWaitingTime();
        }
        return (sum / this.numProcesses);
    }

    /**
     * Private helper method that goes through the job queue (the list of all processes
     * in this work load) and will return true if all processes are finished executing
     * and are in the terminated state. Return false otherwise
     *
     * @return boolean - true if all processes in terminated state. False otherwise.
     */
    private boolean allProcessesTerminated() {
        for(Process p: jobQueue) {
            if(p.getState() != ProcessState.TERMINATED) {
                return false;
            }
        }
        return true;
    }

}

