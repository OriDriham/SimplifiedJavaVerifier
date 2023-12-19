package root;

import root.exceptions.ExitException;
import root.main.Sjavac;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that handles method lines, including if/while statements that occur inside methods.
 */
public class Method {
    /*
    Represent the int 0.
     */
    private static final int ZERO = 0;

    /*
    Represent the int 1.
     */
    private static final int ONE = 1;

    /*
    Represent the int 2.
     */
    private static final int TWO = 2;

    /*
    Represent the int 3.
     */
    private static final int THREE = 3;

    /*
    Represent the String " ".
     */
    private static final String SPACE = " ";

    /*
    Represent the String "".
     */
    private static final String EMPTY = "";

    /*
    Represent the String ",){".
     */
    private static final String PARAM_LIST_COMMA = ",){";

    /*
    Represent the String "final".
     */
    private static final String FINAL = "final";

    /*
    Represent the String "true".
     */
    private static final String TRUE = "true";

    /*
    Represent the String "false".
     */
    private static final String FALSE = "false";

    /*
    Represent the String ",".
     */
    private static final String COMMA = ",";

    /*
    Represent the String "right".
     */
    private static final String RIGHT = "right";

    /*
    Represent the String "&&".
     */
    private static final String AND = "&&";

    /*
    Represent the String "||".
     */
    private static final String OR = "||";

    /*
    Represent the String "&".
     */
    private static final char SINGLE_AND_SIGN = '&';

    /*
    Represent the String "|".
     */
    private static final char SINGLE_OR_SIGN = '|';

    /*
    Represent a group number in a specific regex.
     */
    private static final int GROUP_METHOD_NAME = 1;

    /*
    Represent a group number in a specific regex, that holds a parameter list.
     */
    private static final int GROUP_PARAM_LIST = 2;

    /*
    Represent a group number in a specific regex, that holds a condition (if/while).
     */
    private static final int GROUP_CONDITION_ARGUMENTS = 2;


    //======Methods=====//

    /**
     * Checks if the given line is a method declaration line, and return true/false accordingly.
     *
     * @param line         The given line to check.
     * @param currentScope The current Scope of the file.
     * @param runNumber    An integer represents which run number the program is in, first time - the program
     *                     check for global variables and methods declaration, second - regular run.
     * @return Return true if it is a method declaration, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean checkMethodDeclaration(String line, Scope currentScope, int runNumber)
            throws ExitException {
        Pattern methodPattern = Pattern.compile(Regex.VALID_METHOD_DECLARATION);
        Matcher methodMatcher = methodPattern.matcher(line);
        if (methodMatcher.matches()) {
            Sjavac.SCOPE_COUNTER = Sjavac.METHOD_SCOPE;
            String lineWithoutSpaces = line.replaceAll(Regex.SPACES, EMPTY);
            if (lineWithoutSpaces.endsWith(PARAM_LIST_COMMA)) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            String name = methodMatcher.group(GROUP_METHOD_NAME);
            String params = methodMatcher.group(GROUP_PARAM_LIST);
            if (params == null) {
                changeScopeInSecondRun(currentScope, runNumber);
                return true;  // valid argument line - empty
            }
            params = params.substring(ONE, params.length() - ONE);
            if (params.equals(EMPTY)) {
                changeScopeInSecondRun(currentScope, runNumber);
                return true;  // valid argument line - empty
            }
            String[] lineParts = params.split(Regex.LINE_TO_PARTS);
            int LinePartsSize = lineParts.length;
            boolean[] hasFinalArray = new boolean[LinePartsSize];
            String[] typeArray = new String[LinePartsSize];
            checkLineParts(lineParts, hasFinalArray, typeArray);
            String[] allTypes = new String[LinePartsSize];
            HashMap<String, String[]> valueArray = new HashMap<>();
            fillAllTypes(lineParts, hasFinalArray,valueArray, allTypes,typeArray);
            if (valueArray.size() != lineParts.length) {  // there are duplicate arguments
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            currentScope.methodDict.put(name, allTypes);
            if (changeScopeInSecondRun(currentScope, runNumber)) {
                Sjavac.scopesArray.get(Sjavac.scopesArray.size() - ONE).innerDict.putAll(valueArray);
            }
        } else {
            return false;
        }
        return true;
    }

    /*
    Helper method for checkMethodDeclaration that changes the current scope and also the scope counter.
     */
    private static boolean changeScopeInSecondRun(Scope currentScope, int runNumber) {
        if (Sjavac.FIRST_RUN_INDEX != runNumber) {
            Sjavac.scopesArray.add(new Scope(currentScope.innerDict, currentScope.methodDict));
            Sjavac.SCOPE_COUNTER = Sjavac.METHOD_SCOPE;
            return true;
        }
        return false;
    }

    /*
    Helper method for checkMethodDeclaration that use regex pattern and check all of lineParts elements.
    Also fill the typeArray and hasFinalArray from line parts.
     */
    private static void checkLineParts(String[] lineParts, boolean[] hasFinalArray, String[] typeArray) {
        Pattern startTypePattern = Pattern.compile(Regex.STARTS_WITH_TYPE);
        for (int i = 0; i < lineParts.length; i++) {
            Matcher startTypeMatcher = startTypePattern.matcher(lineParts[i]);
            if (startTypeMatcher.matches()) {
                String[] parts = lineParts[i].split(SPACE, THREE);
                lineParts[i] = lineParts[i].replaceAll(Regex.SPACES, EMPTY);
                if (lineParts[i].startsWith(FINAL)) {
                    typeArray[i] = parts[ONE];
                    hasFinalArray[i] = true;
                    lineParts[i] = lineParts[i].substring(Sjavac.FINAL_WORD_LENGTH);
                } else {
                    typeArray[i] = parts[ZERO];
                    hasFinalArray[i] = false;
                }
                lineParts[i] = lineParts[i].substring(typeArray[i].length());
            }
        }
    }

    /*
    Helper method for checkMethodDeclaration that fill the array of "allTypes" from the given arrays -
    lineParts and hasFinalArray.
     */
    private static void fillAllTypes(String[] lineParts, boolean[] hasFinalArray
            , HashMap<String, String[]> valueArray, String[] allTypes,String[] typeArray) {
        for (int i = 0; i < lineParts.length; i++) {
            String[] assignArray = {typeArray[i], TRUE};
            if (hasFinalArray[i]) {  // if is a final argument
                assignArray[ZERO] = FINAL + " " + typeArray[i];
            }
            valueArray.put(lineParts[i], assignArray);
            allTypes[i] = assignArray[ZERO];
        }
    }

    /**
     * Checks if the given line is a valid return line, and return true/false accordingly.
     *
     * @param line The given line to check.
     * @param currentScope The current Scope of the file.
     * @return Return true if it is a valid return line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean isAReturnLine(String line, Scope currentScope) throws ExitException {
        Pattern returnPattern = Pattern.compile(Regex.RETURN);
        Matcher returnMatcher = returnPattern.matcher(line);
        if(returnMatcher.matches()) {
            if(Sjavac.SCOPE_COUNTER == Sjavac.GLOBAL_SCOPE) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            if(Sjavac.SCOPE_COUNTER >= Sjavac.CONDITION_SCOPE) {
                return true;
            }
            currentScope.isReturnUsed = true;
            return true;
        }
        return false;
    }

    /**
     * Checks if the given line is a end of section ('}') line, and return true/false accordingly.
     *
     * @param line The given line to check.
     * @param currentScope The current Scope of the file.
     * @return Return true if it is a end of section line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean isEndOfSection(String line, Scope currentScope) throws ExitException {
        Pattern returnPattern = Pattern.compile(Regex.END_OF_SECTION);
        Matcher returnMatcher = returnPattern.matcher(line);
        if(returnMatcher.matches()) {
            if(Sjavac.SCOPE_COUNTER == Sjavac.GLOBAL_SCOPE) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            if(Sjavac.SCOPE_COUNTER >= Sjavac.CONDITION_SCOPE) {
                Sjavac.SCOPE_COUNTER --;
                return true;
            }
            if(currentScope.isReturnUsed) {
                Sjavac.SCOPE_COUNTER = Sjavac.GLOBAL_SCOPE;
                Sjavac.scopesArray.remove(Sjavac.scopesArray.size()-ONE);
                return true;
            }
            else {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
        }
        return false;
    }

    /**
     * Checks if the given line is a method call line, and return true/false accordingly.
     *
     * @param line The given line to check.
     * @param regex A regular expression to use inside the method.
     * @param currentScope The current Scope of the file.
     * @param runNumber An integer represents which run number the program is in, first time - the program
     *                  check for global variables and methods declaration, second - regular run.
     * @return Return true if it is a method call line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean isAMethodCall(String line, String regex, Scope currentScope, int runNumber)
            throws ExitException {
        if(isAMethod(line, regex)) {
            if(Sjavac.SCOPE_COUNTER == Sjavac.GLOBAL_SCOPE) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            String name = line.split(Regex.METHOD_USE_NAME)[ZERO];
            name = name.replaceAll(Regex.SPACES, EMPTY);
            line = line.replaceAll(Regex.SPACES, EMPTY);
            String valueList = line.substring(name.length()+ONE, line.length()-TWO);
            valueList = valueList.replaceAll(Regex.SPACES, EMPTY);
            String[] linePart = valueList.split(COMMA);
            String[] values = currentScope.methodDict.get(name);
            if(values != null) {  // in method dictionary
                if(linePart.length != values.length) {
                    throw new ExitException(ExitException.EXIT_CODE_1);
                }
                String[] methodArguments = currentScope.methodDict.get(name);
                if(methodArguments != null) {
                    if(methodArguments.length != values.length) {
                        throw new ExitException(ExitException.EXIT_CODE_1);
                    }
                    for(int i=0; i<values.length; i++) {  // same length
                        Type.checkForDuplicates(linePart[i], methodArguments[i], RIGHT, currentScope);
                    }
                }
            }
            else {
                return Sjavac.FIRST_RUN_INDEX == runNumber || valueList.equals(EMPTY);
            }
            return true;
        }
        else {  // not a method
            return false;
        }
    }

    /**
     * Checks if the given line is a condition (if/while) declaration line, and return true/false accordingly.
     *
     * @param line The given line to check.
     * @param currentScope The current Scope of the file.
     * @return Return true if it is a condition declaration line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean isAConditionDeclaration(String line, Scope currentScope) throws ExitException {
        Pattern conditionPattern = Pattern.compile(Regex.CONDITION_LINE);
        Matcher conditionMatcher = conditionPattern.matcher(line);
        if(conditionMatcher.matches()) {
            if(Sjavac.SCOPE_COUNTER == Sjavac.GLOBAL_SCOPE) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            String conditionArgument = conditionMatcher.group(GROUP_CONDITION_ARGUMENTS);
            String[] checkSpaces = conditionArgument.split(Regex.CONDITION_SPACE);
            String conditions = conditionArgument.replaceAll(Regex.SPACES, EMPTY);
            if(conditions.length() == ZERO) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            char lastChar = conditions.charAt(conditions.length()-ONE);
            if(lastChar == SINGLE_AND_SIGN || lastChar == SINGLE_OR_SIGN) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            String[] conditionsArray = conditions.split(Regex.AND_OR);
            if(conditionsArray.length != getCheckSpaceLength(checkSpaces)) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            Pattern namePattern = Pattern.compile(Regex.VALID_VARIABLE_NAME);
            Matcher nameMatcher;
            for(String part: conditionsArray) {
                if(part.equals(TRUE) || part.equals(FALSE)) {
                    continue;
                }
                nameMatcher = namePattern.matcher(part);
                if(nameMatcher.matches()) {
                    if(!Type.isInDictionaryAndInitialized(part, currentScope)) {
                        throw new ExitException(ExitException.EXIT_CODE_1);
                    }
                }
            }
        }
        else {
            return false;
        }
        Sjavac.SCOPE_COUNTER ++;
        return true;
    }

    /**
     * Return the amount of spaces in the given array.
     *
     * @param checkSpaces An array to check the spaces in.
     * @return An int - the amount of spaces in the array.
     */
    public static int getCheckSpaceLength(String[] checkSpaces) {
        int counter = ZERO;
        for (String checkSpace : checkSpaces) {
            if (!checkSpace.equals(AND) && !checkSpace.equals(OR) && !checkSpace.equals(EMPTY)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * This method only checks if the given line matches the given regex and therefore - is a method line.
     *
     * @param line The given line to check.
     * @param regex A regular expression to use inside the method.
     * @return Return true if it is a method line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean isAMethod(String line, String regex) throws ExitException {
        Pattern methodPattern = Pattern.compile(regex);
        Matcher methodMatcher = methodPattern.matcher(line);
        if (methodMatcher.matches()) {
            String lineWithoutSpaces = line.replaceAll(Regex.SPACES, EMPTY);
            if (lineWithoutSpaces.endsWith(PARAM_LIST_COMMA)) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            return true;
        }
        return false;
    }

    /**
     * This method only checks if the given line is a end of section line.
     *
     * @param line The given line to check.
     * @return Return true if it is a end of section line, false otherwise.
     */
    public static boolean isEndOfSectionLine(String line) {
        Pattern returnPattern = Pattern.compile(Regex.END_OF_SECTION);
        Matcher returnMatcher = returnPattern.matcher(line);
        return returnMatcher.matches();
    }

    /**
     * This method only checks if the given line is a open section line.
     *
     * @param line The given line to check.
     * @return Return true if it is a open section line, false otherwise.
     */
    public static boolean isOpenASectionLine(String line) {
        Pattern returnPattern = Pattern.compile(Regex.START_SECTION);
        Matcher returnMatcher = returnPattern.matcher(line);
        return returnMatcher.matches();
    }
}
