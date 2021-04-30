package edu.bloomu.huskies.bmb56279;

import java.util.Comparator;

/**
 * A class that implements the Comparator interface that will then be used by a
 * priority queue to simulate the ready queue using the Shortest Job First cpu
 * scheduling algorithm. A priority list that is passed an instance of this class will be
 * sorted according to the cpu remaining time. The process with the smallest CPU
 * remaining time will always be the head of a priority list using this Comparator.
 *
 * @author Brett Bernardi
 */
class SJFComparator implements Comparator<Process> {
    @Override
    public int compare(Process p1, Process p2) {
        return p1.getCPUTime() - p2.getCPUTime();
    }
}