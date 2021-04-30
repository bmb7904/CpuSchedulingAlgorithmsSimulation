package edu.bloomu.huskies.bmb56279;

import java.util.Scanner;

/**
 * The class containing the main method that retrieves user input, creates the
 * appropriate Scheduler objects, calls the simulate method to schedule and execute
 * processes, and then displays the results. User can exit outof loop (and program) by
 * typing in the string "q" or "Q".
 *
 * @author Brett Bernardi
 */
public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);


        while (true) {
            System.out.print("\nEnter process arrival times and burst lengths: ");
            String userLine = input.nextLine();
            if (userLine.equalsIgnoreCase("q")) {
                break;
            }
            String[] userInput = userLine.split(" ");
            Scheduler fcfs;
            Scheduler sjfnp;
            Scheduler srtf;
            // The constructor for the Scheduler class will throw an ImproperArguments
            // Exception if user input is not in the correct format.
            try {
                fcfs = new Scheduler(userInput, SchedulingType.FCFS);
                sjfnp = new Scheduler(userInput, SchedulingType.SJFNP);
                srtf = new Scheduler(userInput, SchedulingType.SRTF);

                // At this point, arguments are valid. No Exception caught.
                System.out.println();

                // Simulate and retrieve results for SRTF
                srtf.simulate();
                System.out.print("SRTF: ");
                convertAndPrintSchedule(srtf.getSchedule());
                System.out.println();
                System.out.print("Average Waiting Time: ");
                System.out.printf("%.2f\n", srtf.getAvgWaitTime());
                System.out.println();

                // Simulate and retrieve results for SJF
                sjfnp.simulate();
                System.out.print("SJF: ");
                convertAndPrintSchedule(sjfnp.getSchedule());
                System.out.println();
                System.out.print("Average Waiting Time: ");
                System.out.printf("%.2f\n", sjfnp.getAvgWaitTime());
                System.out.println();

                // Simulate and retrieve results for FCFS
                fcfs.simulate();
                System.out.print("FCFS: ");
                convertAndPrintSchedule(fcfs.getSchedule());
                System.out.println();
                System.out.print("Average Waiting Time: ");
                System.out.printf("%.2f\n", fcfs.getAvgWaitTime());
                System.out.println();


            } catch (ImproperArguments e) {
                System.out.println("ERROR! " + e.getMessage());
            }
            System.out.println(
                    "---------------------------------------------------------------------");
        }
        // close the Scanner and print departing message
        input.close();
        System.out.println("Good-bye!");

    }

    /**
     * Private static helper method to print the Process schedule (char[] array) into
     * a readable form.
     *
     * Pre-Conditions: char[] array in parameter must have a size greater than 1. It's
     * unlikely to ever encounter that in this simulation. That would require a
     * workload of one process consisting of a CPU burst of length one unit of time.
     * Unlikely.
     *
     * @param schedule - the Schedule of processes as a char[] array
     */
    private static void convertAndPrintSchedule(char[] schedule) {
        int counter = 1;
        for (int i = 0; i < schedule.length; i++) {
            if (i == schedule.length - 1) {
                if (schedule[i] == schedule[i - 1]) {
                    System.out.printf("%c%d ", schedule[i], counter);
                } else {
                    System.out.printf("%c%d ", schedule[i], 1);
                }
            } else {
                if (schedule[i] == schedule[i + 1]) {
                    counter++;
                } else {
                    System.out.printf("%c%d ", schedule[i], counter);
                    counter = 1;
                }
            }
        }
    }
}




