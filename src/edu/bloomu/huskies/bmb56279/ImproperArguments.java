package edu.bloomu.huskies.bmb56279;

/**
 * A custom class that extends the Exception class. Instances of this class will be
 * thrown when user supplied input does not meet the criteria for valid arguments.
 *
 * @author Brett Bernardi
 */
public class ImproperArguments extends Exception {
    public ImproperArguments(String message) {
        super(message);
    }
}
