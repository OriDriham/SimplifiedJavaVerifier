package root;

import root.exceptions.ExitException;
import root.main.Sjavac;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that represent a type in sjava.
 */
public class Type {
    /*
    Represent the integer 0.
     */
    private static final int ZERO = 0;

    /*
    Represent the integer 1.
     */
    private static final int ONE = 1;

    /*
    Represent the integer 2.
     */
    private static final int TWO = 2;

    /*
    Represent the String "".
     */
    private static final String EMPTY = "";

    /*
    Represent the String "=".
     */
    private static final String EQUALS = "=";

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
    Represent the String "right".
     */
    private static final String RIGHT = "right";

    /*
    Represent the String "left".
     */
    private static final String LEFT = "left";

    /*
    Represent the String "String".
     */
    private static final String STRING = "String";

    /*
    Represent the String "char".
     */
    private static final String CHAR = "char";

    /*
    The index of the first run of the program, one out of two runs.
     */
    private static final int FIRST_RUN_INDEX = 0;

    /*
    Represent a String with an int value inside.
     */
    private static final String INT_SOME_VALUE = "1";

    /*
    Represent a String with a double value inside.
     */
    private static final String DOUBLE_SOME_VALUE = "1.0";

    /*
    Represent a String with a char value inside.
     */
    private static final String CHAR_SOME_VALUE = "'a'";

    /*
    Represent a String with a String value inside.
     */
    private static final String STRING_SOME_VALUE = "\"String\"";

    /*
    Represent a String with a boolean value inside.
     */
    private static final String BOOLEAN_SOME_VALUE = "true";

    /*
    Represent the Char ';'.
     */
    private static final char SEMICOLON = ';';

    /*
    Represent the String ",;".
     */
    private static final String COMMA_SEMICOLON = ",;";


    //======Methods=====//

    /**
     * Check if the given line is a declaration line. This includes both only declaration,
     * and declaration + assignment in the same line.
     *
     * @param line The given line to check.
     * @param currentScope The current Scope of the file.
     * @return Return true if it is a declaration line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean checkLineDeclaration(String line, Scope currentScope, int runNumber)
            throws ExitException {
        Pattern typePattern = Pattern.compile(Regex.VALID_DECLARATION_LINE);
        String originalLine = line;
        line = checkIfEndsWithSemiColon(line);
        Matcher typeMatcher = typePattern.matcher(line);
        if(typeMatcher.matches()) {
            line = originalLine;
            String type = typeMatcher.group(TWO);  // type or final
            boolean hasFinal = false;
            if(typeMatcher.group(ONE) != null) {  // a final and not a type
                hasFinal = true;
            }
            String[] lineParts = line.split(Regex.LINE_TO_PARTS);
            Pattern invalidDeclarationPattern = Pattern.compile(Regex.STARTS_WITH_TYPE);
            for(int i=0; i<lineParts.length; i++) {
                if(i > ZERO) {  // declaration not allowed
                    Matcher invalidDeclarationMatcher = invalidDeclarationPattern.matcher(lineParts[i]);
                    if(invalidDeclarationMatcher.matches()) {
                        throw new ExitException(ExitException.EXIT_CODE_1);
                    }
                }
                lineParts[i] = lineParts[i].replaceAll(Regex.SPACES, EMPTY);
                if(hasFinal && (i == ZERO)) {
                    lineParts[ZERO] = lineParts[ZERO].substring(Sjavac.FINAL_WORD_LENGTH);
                }
                if(i == ZERO) {  // include declaration
                    lineParts[i] = lineParts[i].substring(type.length());
                }
            }
            checkAssignmentOrDeclaration(lineParts, type, currentScope, hasFinal, runNumber);
        }
        else {
            return false;
        }
        return true;
    }

    /*
    Go over lineParts and check if each part is an assignment + declaration or only declaration part.
     */
    private static void checkAssignmentOrDeclaration(String[] lineParts, String type, Scope currentScope,
                                                     boolean hasFinal, int runNumber) throws ExitException {
        for(String part: lineParts) {
            String name;
            boolean isAssigned = false;
            if(part.contains(EQUALS)) {  // declaration + assignment
                name = checkAssignmentValidation(part, type, currentScope);
                isInInnerDict(name, currentScope);
                isAssigned = true;
            }
            else {  // only declaration
                if(hasFinal) {
                    throw new ExitException(ExitException.EXIT_CODE_1);
                }
                checkDeclarationValidity(part, currentScope);
                name = part;
            }
            String assignValue = Boolean.toString(isAssigned);
            String[] assignArray = {type, assignValue};
            if(hasFinal) {
                assignArray[ZERO] = FINAL + " " + type;
            }
            if(runNumber == FIRST_RUN_INDEX) {
                Sjavac.globalVariables.put(name, assignArray);
            }
            else {
                if(Sjavac.SCOPE_COUNTER == Sjavac.METHOD_SCOPE) {
                    currentScope.innerDict.put(name, assignArray);
                }
            }
        }
    }

    /*
    Checks if ends with semicolon, and return a new line with a minor change, that will be used to check
    a regex.
     */
    private static String checkIfEndsWithSemiColon(String line) {
        if(line.matches(Regex.ENDS_WITH_SEMICOLON)) {
            int numberOfSpaces = ZERO;
            for(int i=line.length()-ONE; i>=ZERO; i--) {
                numberOfSpaces++;
                if(line.charAt(i) == SEMICOLON) {
                    break;
                }
            }
            line = line.substring(ZERO, line.length()-numberOfSpaces);
            line = line + COMMA_SEMICOLON;
            return line;
        }
        return line;
    }

    /**
     * Check if the given line is an assignment line. This method check only assignment, without declaration.
     *
     * @param line The given line to check.
     * @param currentScope The current Scope of the file
     * @return Return true if it is an assignment line, false otherwise.
     * @throws ExitException Will be thrown if an error occurred, i.e. - invalid syntax.
     */
    public static boolean checkLineAssignment(String line, Scope currentScope, int runNumber)
            throws ExitException {
        Pattern assignmentOnlyPattern = Pattern.compile(Regex.VALID_ASSIGNMENT_ONLY_LINE);
        Matcher assignmentOnlyMatcher = assignmentOnlyPattern.matcher(line);
        if(assignmentOnlyMatcher.matches()) {
            String[] lineParts = line.split(Regex.LINE_TO_PARTS);
            for(int i=0; i<lineParts.length; i++) {
                lineParts[i] = lineParts[i].replaceAll(Regex.SPACES, EMPTY);
            }
            String type;
            for(String part: lineParts) {
                type = getTypeFromDictionary(part, currentScope);  // also making sure it exists in the dict
                finalTypeFail(type);  // throws an exception if is a final
                String name = checkAssignmentValidation(part, type, currentScope);
                String[] assignmentArray = {type, TRUE};
                if(runNumber == FIRST_RUN_INDEX) {
                    Sjavac.globalVariables.put(name, assignmentArray);
                }
                else {
                    if(Sjavac.SCOPE_COUNTER == Sjavac.METHOD_SCOPE) {
                        currentScope.innerDict.put(name, assignmentArray);  // <type>
                    }
                }
                return true;
            }
        }
        else {
            return false;
        }
        return false;
    }

    /*
    Throws an exception if the given type starts with "final" string.
     */
    private static void finalTypeFail(String type) throws ExitException {
        if(type.startsWith(FINAL)) {
            throw new ExitException(ExitException.EXIT_CODE_1);
        }
    }

    /*
    Given an assignment part of a line, this method return the string representation of the type
    of the variable. If non was found - throws an exception.
     */
    private static String getTypeFromDictionary(String assignmentPart, Scope currentScope) throws ExitException {
        String name = assignmentPart.substring(ZERO, assignmentPart.indexOf(EQUALS));
        String[] arrayAssignment = currentScope.innerDict.get(name);
        String type;
        if(arrayAssignment == null) {
            try {
                arrayAssignment = currentScope.outerDict.get(name);
                if(arrayAssignment == null) {
                    arrayAssignment = Sjavac.globalVariables.get(name);
                    if(arrayAssignment == null) {
                        throw new ExitException(ExitException.EXIT_CODE_1);
                    }
                    type = Sjavac.globalVariables.get(name)[ZERO];  // in global
                }
                else {  // in outer
                    type = currentScope.outerDict.get(name)[ZERO];
                }
            }
            catch(NullPointerException e) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
        }
        else {  // in inner
            type = currentScope.innerDict.get(name)[ZERO];
        }
        return type;
    }

    /*
    Checks if the given name is a valid name.
     */
    private static boolean isValidVariableName(String name) {
        Pattern variablePattern = Pattern.compile(Regex.VALID_VARIABLE_NAME);
        Matcher variableMatcher = variablePattern.matcher(name);
        return variableMatcher.matches();
    }

    /*
    Given a String 'type', this method returns the enum type of this string representation if the
    given value is of the same type, otherwise - return null.
     */
    private static TypeCompare getAndCheckEnumType(String type, String value) {
        String upperType = type.toUpperCase();
        TypeCompare typeEnum = TypeCompare.valueOf(upperType);
        if(typeEnum.isEqual(value)) {
            return typeEnum;
        }
        return null;
    }

    /*
    This method returns the TypeCompare type the given String "type", if the given String "type" is
    not a valid type name, return null.
     */
    private static TypeCompare getEnumType(String type) {
        String upperType = type.toUpperCase();
        try {
            return TypeCompare.valueOf(upperType);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    /*
    Check if the given part is a valid assignment part, if yes - return the variable name
    (left to the equals sign), else - throws an exception.
     */
    private static String checkAssignmentValidation(String part, String type, Scope currentScope)
            throws ExitException {
        String leftToEquals = part.substring(ZERO, part.indexOf(EQUALS));
        String rightToEquals = part.substring(part.indexOf(EQUALS)+ONE);
        boolean checkLeft = checkForDuplicates(leftToEquals, type, LEFT, currentScope);
        boolean checkRight = checkForDuplicates(rightToEquals, type, RIGHT, currentScope);
        if(checkLeft || checkRight) {
            TypeCompare enumType = getEnumType(type);
            if(enumType != null) {
                if(checkRight || enumType.isEqual(rightToEquals)) {
                    rightToEquals = enumType.returnSomeValue();
                }
                else {
                    throw new ExitException(ExitException.EXIT_CODE_1);
                }
            }
        }
        if(isValidVariableName(leftToEquals)) {
            if(getAndCheckEnumType(type, rightToEquals) != null) {
                return leftToEquals;
            }
            else {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
        }
        else {
            throw new ExitException(ExitException.EXIT_CODE_1);
        }
    }

    /*
    Checks both the variable name validity and if that name is already in one of the dictionaries,
    if not - throws an exception.
     */
    private static void checkDeclarationValidity(String name, Scope currentScope)
            throws ExitException {
        if(isValidVariableName(name)) {
            isInInnerDict(name, currentScope);
        }
        else {
            throw new ExitException(ExitException.EXIT_CODE_1);
        }
    }

    /**
     * Checks if the given name with the given type appears in one of the dictionaries, if it appears -
     * return true, else - return false. If in non of them - throws an exception.
     *
     * @param name The name of the variable.
     * @param type The type of the variable.
     * @param side The side of 'name' in relation to an equals sign. "right" if right to an equals sign,
     *             "left" if left to an equals sign.
     * @param currentScope The current scope the cursor is in while going over the program.
     * @return If in one of the dictionaries - return true, else - false.
     * @throws ExitException Will be throws if in non of the dictionaries, i.e. - not declared.
     */
    public static boolean checkForDuplicates(String name, String type, String side, Scope currentScope)
            throws ExitException {
        String[] valueArray = null;
        if(currentScope.innerDict.size() > ZERO) {
            valueArray = currentScope.innerDict.get(name);
        }
        if(valueArray == null) {  // not in inner
            if(currentScope.outerDict.size() >  ZERO) {
                valueArray = (currentScope.outerDict.get(name));
            }
            if(valueArray == null) {  // not in outer
                if(Sjavac.globalVariables.size() >  ZERO) {
                    valueArray = (Sjavac.globalVariables.get(name));
                }
                if (valueArray == null) {  // not in global
                    return false;
                }
                else {  // in global
                    if(valueArray[ZERO].equals(type) || valueArray[ZERO].equals(FINAL + " " + type)) {
                        if(side.equals(RIGHT) && valueArray[ONE].equals(FALSE)) {
                            throw new ExitException(ExitException.EXIT_CODE_1);
                        }
                        return true;
                    }
                }
            }
            else {  // in outer
                return ifMatchesTypeAndNotInitialized(valueArray, type, side);
            }

        }
        else {  // in inner
            if(ifMatchesTypeAndNotInitialized(valueArray, type, side)) {
                return true;
            }
            else {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
        }
        return false;
    }

    /*
    Check if valueArray matches the given type and not initialized.
     */
    private static boolean ifMatchesTypeAndNotInitialized(String[] valueArray, String type, String side)
            throws ExitException{
        if(valueArray[ZERO].equals(type)) {
            if(side.equals(RIGHT) && valueArray[ONE].equals(FALSE)) {
                throw new ExitException(ExitException.EXIT_CODE_1);
            }
            return true;
        }
        return false;
    }

    /**
     * If is in inner dictionary - throws exception, else, does nothing.
     *
     * @param name The name of the variable.
     * @param currentScope The current scope the cursor is in while going over the program.
     * @throws ExitException Will be thrown if name is in inner dictionary.
     */
    public static void isInInnerDict(String name, Scope currentScope)
            throws ExitException {
        String[] valueArray = null;
        if(currentScope.innerDict.size() > ZERO) {
            valueArray = currentScope.innerDict.get(name);
        }
        if(valueArray != null) {  // in inner
            throw new ExitException(ExitException.EXIT_CODE_1);
        }
    }

    /**
     * Checks if the given name is in one of the dictionaries, and if it's there - if initialized.
     *
     * @param name The name of the variable.
     * @param currentScope The current scope the cursor is in while going over the program.
     * @return If both in a dictionary and initialized - return true, else - false.
     */
    public static boolean isInDictionaryAndInitialized(String name, Scope currentScope) {
        String[] valueArray = null;
        if(currentScope.innerDict.size() > ZERO) {
            valueArray = currentScope.innerDict.get(name);
        }
        if(valueArray == null || valueArray[ONE].equals(FALSE)) {  // not in inner
            if (currentScope.outerDict.size() > ZERO) {
                valueArray = (currentScope.outerDict.get(name));
            }
            if (valueArray == null || valueArray[ONE].equals(FALSE)) {  // not in outer
                if (Sjavac.globalVariables.size() > ZERO) {
                    valueArray = (Sjavac.globalVariables.get(name));
                }
                if (valueArray == null) {  // not in global
                    return false;
                }
            }
        }
        boolean validType = !valueArray[ZERO].equals(STRING) && !valueArray[ZERO].equals(CHAR);
        return valueArray[ONE].equals(TRUE) && validType;
    }

    /**
     * An enum for comparing different types.
     */
    public enum TypeCompare {
        INT {
            /**
             * Abstract method that checks if the given String value is equal to a valid value of the same
             * type, will be overridden by each type.
             * @param value A String value to check.
             * @return If equals the type - true, else - false.
             */
            @Override
            public boolean isEqual(String value) {
                return INT.matchesPattern(value, Regex.INT_VALID_VALUE);
            }

            /**
             * Abstract method that return a value associated with the type.
             *
             * @return String representation of a value of the type.
             */
            @Override
            public String returnSomeValue() {
                return Type.INT_SOME_VALUE;
            }
        },
        DOUBLE {
            /**
             * Abstract method that checks if the given String value is equal to a valid value of the same
             * type, will be overridden by each type.
             * @param value A String value to check.
             * @return If equals the type - true, else - false.
             */
            @Override
            public boolean isEqual(String value) {
                return DOUBLE.matchesPattern(value, Regex.DOUBLE_VALID_VALUE);
            }

            /**
             * Abstract method that return a value associated with the type.
             *
             * @return String representation of a value of the type.
             */
            @Override
            public String returnSomeValue() {
                return Type.DOUBLE_SOME_VALUE;
            }
        },
        CHAR {
            /**
             * Abstract method that checks if the given String value is equal to a valid value of the same
             * type, will be overridden by each type.
             * @param value A String value to check.
             * @return If equals the type - true, else - false.
             */
            @Override
            public boolean isEqual(String value) {
                return CHAR.matchesPattern(value, Regex.CHAR_VALID_VALUE);
            }

            /**
             * Abstract method that return a value associated with the type.
             *
             * @return String representation of a value of the type.
             */
            @Override
            public String returnSomeValue() {
                return Type.CHAR_SOME_VALUE;
            }
        },
        STRING {
            /**
             * Abstract method that checks if the given String value is equal to a valid value of the same
             * type, will be overridden by each type.
             * @param value A String value to check.
             * @return If equals the type - true, else - false.
             */
            @Override
            public boolean isEqual(String value) {
                return STRING.matchesPattern(value, Regex.STRING_VALID_VALUE);
            }

            /**
             * Abstract method that return a value associated with the type.
             *
             * @return String representation of a value of the type.
             */
            @Override
            public String returnSomeValue() {
                return Type.STRING_SOME_VALUE;
            }
        },
        BOOLEAN {
            /**
             * Abstract method that checks if the given String value is equal to a valid value of the same
             * type, will be overridden by each type.
             * @param value A String value to check.
             * @return If equals the type - true, else - false.
             */
            @Override
            public boolean isEqual(String value) {
                return BOOLEAN.matchesPattern(value, Regex.BOOLEAN_VALID_VALUE);
            }

            /**
             * Abstract method that return a value associated with the type.
             *
             * @return String representation of a value of the type.
             */
            @Override
            public String returnSomeValue() {
                return Type.BOOLEAN_SOME_VALUE;
            }
        };

        /**
         * Abstract method that checks if the given String value is equal to a valid value of the same type,
         * will be overridden by each type.
         * @param value A String value to check.
         * @return If equals the type - true, else - false.
         */
        public abstract boolean isEqual(String value);

        /**
         * Abstract method that return a value associated with the type.
         *
         * @return String representation of a value of the type.
         */
        public abstract String returnSomeValue();

        /*
        Given a value and a pattern, return true if the value matches the pattern, else - false.
         @param value A String value to check.
         @param pattern A pattern to check on.
         @return True if the value matches the pattern, else - false.
         */
        private boolean matchesPattern(String value, String pattern) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(value);
            return m.matches();
        }
    }
}
