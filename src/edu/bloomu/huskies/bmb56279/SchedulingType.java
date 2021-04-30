package edu.bloomu.huskies.bmb56279;

/**
 * Enum that lists the three different types of scheduling algorithms to be used by the
 * Scheduler class.
 *
 * @author Brett Bernardi
 */
public enum SchedulingType {
    // First Come, First Serve
    FCFS,
    // Shortest Job First, Non-Preemptive
    SJFNP,
    // Shortest Job First (Shortest Remaining Time First)
    SRTF
}
