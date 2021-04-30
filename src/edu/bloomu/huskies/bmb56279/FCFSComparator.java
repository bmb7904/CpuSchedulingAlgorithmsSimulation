package edu.bloomu.huskies.bmb56279;

import java.util.Comparator;

/**
 * A class that implements the comparator interface to sort Process objects in a
 * priority queue (representing a ready queue) according to FCFS rules. This means the
 * process with the earliest arrival time in the priority queue will always be the
 * head of the list.
 *
 * @author Brett Bernardi
 */
class FCFSComparator implements Comparator<Process> {
    @Override
    public int compare(Process p1, Process p2) {
        return p1.getArrivalTime() - p2.getArrivalTime();
    }
}