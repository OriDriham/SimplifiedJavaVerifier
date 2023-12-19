package root.exceptions;

/**
 * A class for an exit exception, i.e. an exception that will lead the program to stop running.
 */
public class ExitException extends Exception {
    /*
    The version number.
     */
    private static final long serialVersionUID = 1L;

    /*
    The exit code that will be printed while exiting the program.
     */
    private final int exitCode;

    /**
     * Exit codes to exit with, and print.
     */
    public static final int
            EXIT_CODE_0 = 0, EXIT_CODE_1 = 1, EXIT_CODE_2 = 2;

    /**
     * Informative messages for different errors.
     */
    public static final String
            INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments",
            FILE_NOT_FOUND = "The sJava file not found",
            IO_MESSAGE = "IO exception - couldn't open/close the file";

    /**
     * Constructor for ExitException in case no error message need to be printed.
     *
     * @param exitCode The exit code to print.
     */
    public ExitException(int exitCode) {
        super();
        this.exitCode = exitCode;
    }

    /**
     * Constructor for ExitException, that gets an exit code for the program and an error message to print.
     *
     * @param exitCode The exit code to print.
     * @param errorMessage An informative error message
     */
    public ExitException(int exitCode, String errorMessage) {
        super(errorMessage);
        this.exitCode = exitCode;
    }


    //======Methods=====//

    /**
     * Prints the exit code into System.out and the given error message into System.err.
     *
     * @param errorMessage The error message related to this exception, to print.
     */
    public void printMessage(String errorMessage) {
        System.out.println(exitCode);
        if(errorMessage != null) {  // if an error message was given
            System.err.println(errorMessage);
        }
    }
}
