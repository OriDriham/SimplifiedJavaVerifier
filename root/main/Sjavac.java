package root.main;

import root.Method;
import root.Scope;
import root.Type;
import root.Regex;
import root.exceptions.ExitException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A main class for the whole project.
 */
public class Sjavac {
    /*
    Represent the integer 1.
     */
    private static final int ONE = 1;

    /*
    Represent a single command line argument to the program.
     */
    private static final int SINGLE_ARGUMENT = 1;

    /*
    The index of the first command line argument
     */
    private static final int FIRST_ARGUMENT_INDEX = 0;

    /*
    The amount of times the program will go over the text file. first time - detect globals,
    second time - a regular run.
     */
    private static final int TWO_RUNS = 2;

    /**
     * The length of the String "final".
     */
    public static final int FINAL_WORD_LENGTH = 5;

    /**
     * The index of the first run, out of two runs.
     */
    public static final int FIRST_RUN_INDEX = 0;

    /**
     * A counter for the amount of scopes opened in the program, a scope starts with { and end with }.
     */
    public static int SCOPE_COUNTER = 0;

    /**
     * A constant represents the global scope.
     */
    public static final int GLOBAL_SCOPE = 0;

    /**
     * A constant represents a method scope.
     */
    public static final int METHOD_SCOPE = 1;

    /**
     * A constant represent a condition scope, such as if/while.
     */
    public static final int CONDITION_SCOPE = 2;

    /**
     * Array of all scopes in the given file.
     */
    public static ArrayList<Scope> scopesArray = new ArrayList<>();

    /**
     * A dictionary (HashMap) that stores all the global variables in the given file.
     */
    public static HashMap<String, String[]> globalVariables = new HashMap<>();


    //======Methods=====//

    /**
     * Main method.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            isValidArgAndFile(args);
        } catch (ExitException e) {
            e.printMessage(e.getMessage());
            return;
        }
        String fileName = args[FIRST_ARGUMENT_INDEX];
        Scope currentScope = new Scope();
        scopesArray.add(currentScope);
        for(int i=0; i<TWO_RUNS; i++) {
            try (BufferedReader fileToRead = new BufferedReader(new FileReader(fileName))) {
                String line = fileToRead.readLine();
                while (line != null) {  // iterate line by line until exception or end of file
                    checkLine(line, currentScope, i);  // will throw an exception if failed
                    currentScope = scopesArray.get(scopesArray.size()-ONE);
                    line = fileToRead.readLine();
                }
                if(i == FIRST_RUN_INDEX) {
                    Sjavac.SCOPE_COUNTER = GLOBAL_SCOPE;
                    continue;
                }
                if(Sjavac.SCOPE_COUNTER == GLOBAL_SCOPE) {
                    throw new ExitException(ExitException.EXIT_CODE_0);
                }
                else {
                    throw new ExitException(ExitException.EXIT_CODE_1);
                }
            }
            catch (ExitException e) {
                if(i == FIRST_RUN_INDEX) {
                    continue;
                }
                e.printMessage(e.getMessage());
            }
            catch (IOException e) {
                System.out.println(ExitException.EXIT_CODE_2);
                System.err.println(ExitException.IO_MESSAGE);
            }
        }
        globalVariables = new HashMap<>();  // reset
        Sjavac.SCOPE_COUNTER = GLOBAL_SCOPE;  // reset
    }

    /*
    Checks of the given argument is valid and an existing file, else - throws an exception.
     */
    private static void isValidArgAndFile(String[] args) throws ExitException {
        if(args.length != SINGLE_ARGUMENT) {
            throw new ExitException(ExitException.EXIT_CODE_2, ExitException.INVALID_NUMBER_OF_ARGUMENTS);
        }
        else {
            File file = new File(args[FIRST_ARGUMENT_INDEX]);
            if(!file.exists() || !file.isFile()) {
                throw new ExitException(ExitException.EXIT_CODE_2, ExitException.FILE_NOT_FOUND);
            }
        }
    }

    /*
    Check all possibilities for a valid line.
     */
    private static void checkLine(String line, Scope currentScope, int runNumber) throws ExitException {
        Pattern emptyPattern = Pattern.compile(Regex.EMPTY_OR_SPACE);
        Matcher emptyMatcher = emptyPattern.matcher(line);
        if(emptyMatcher.matches()) {
            return;
        }
        if(runNumber == FIRST_RUN_INDEX) {  // first run - find all global variables
            if(Method.isAMethod(line, Regex.VALID_METHOD_DECLARATION)) {  // check if it is a method line
                Method.checkMethodDeclaration(line, currentScope, runNumber);
                return;
            }
            else if(SCOPE_COUNTER >= METHOD_SCOPE) {  // check if already in a method body
                if(Method.isOpenASectionLine(line)) {
                    SCOPE_COUNTER++;
                }
                else if(Method.isEndOfSectionLine(line)) {
                    SCOPE_COUNTER--;
                }
                return;
            }
            else {  // in global scope, check if a valid declaration and add to globalVariables
                if(Type.checkLineDeclaration(line, currentScope, runNumber) ||
                        Type.checkLineAssignment(line, currentScope, runNumber) || Scope.isAComment(line)
                        || Method.isAMethodCall(line, Regex.METHOD_CALL, currentScope, runNumber)) {
                    return;
                }
            }
        }
        else {  // second run
            if(Scope.isAComment(line) || Type.checkLineDeclaration(line, currentScope, runNumber) ||
                    Type.checkLineAssignment(line, currentScope, runNumber)
                    || Method.checkMethodDeclaration(line, currentScope, runNumber) ||
                    Method.isAReturnLine(line, currentScope) ||
                    Method.isEndOfSection(line, currentScope) ||
                    Method.isAMethodCall(line, Regex.METHOD_CALL, currentScope, runNumber) ||
                    Method.isAConditionDeclaration(line, currentScope)){
                return;
            }
        }
        throw new ExitException(ExitException.EXIT_CODE_1);
    }
}
