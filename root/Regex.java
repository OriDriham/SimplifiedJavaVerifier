package root;

/**
 * All regular expressions used in the program.
 */
public class Regex {
    /**
     * All regex that will be used in the program
     */
    public static final String
    // Basic:
    SPACES = "[ \\t]*",
    EMPTY_OR_SPACE = "[ \\t]*|" + "",
    ONE_PLUS_SPACES = "[ \\t]+",
    AT_LEAST_ONE_SPACE = "[ \\t]+",
    TYPE = "(int|double|boolean|String|char)",
    LINE_TO_PARTS = "\\s*[,;]\\s*",
    CONDITION_SPACE = "[\\s]+",
    VALID_VARIABLE_NAME = "(([a-zA-Z]\\w*)|([_]\\w+))",
    VALID_METHOD_NAME = "([a-zA-Z]\\w*)",
    INT_VALID_VALUE = "(0|-?[1-9]+[0-9]{0,9})",
    POSITIVE_INT_VALID_VALUE = "(0|[1-9]+[0-9]{0,9})",
    DOUBLE_VALID_VALUE = "(" + INT_VALID_VALUE + "\\.?" + INT_VALID_VALUE + "|\\.?" +
    POSITIVE_INT_VALID_VALUE + ")",
    STRING_VALID_VALUE = "(\".*\")",
    BOOLEAN_VALID_VALUE = "(true|false|" + INT_VALID_VALUE + "|" + DOUBLE_VALID_VALUE + ")",
    CHAR_VALID_VALUE = "'.'",

    // Statements:
    FINAL_OPTIONAL = SPACES + "(final)?" + SPACES,
    CONDITIONS = SPACES + "(if|while)" + SPACES,
    END_OF_LINE = SPACES + ";" + SPACES,
    START_OF_SECTION = SPACES + "\\{" + SPACES,
    END_OF_SECTION = SPACES + "\\}" + SPACES,
    RETURN = SPACES + "return" + END_OF_LINE + SPACES,
    VOID = SPACES + "void" + SPACES,
    METHOD_USE_NAME = "\\(.*\\)\\s*;\\s*",
    START_SECTION = ".*\\s*\\{\\s*",
    ENDS_WITH_SEMICOLON = ".*(" + "\\;" + SPACES + ")",

    // Complex:
    VALID_VALUE = "(" + SPACES + INT_VALID_VALUE + "|" + DOUBLE_VALID_VALUE + "|" + STRING_VALID_VALUE +
            "|" + BOOLEAN_VALID_VALUE + "|" + "('.*')" + ")",
    VALID_DECLARATION_LINE = FINAL_OPTIONAL + TYPE + AT_LEAST_ONE_SPACE + SPACES +
            "(([\\w.-]+)" + "((" + SPACES + "=" + SPACES + "[^" + SPACES + "]*" + SPACES + "," + SPACES +
            ")|(" + SPACES + "," + SPACES + ")))*" + END_OF_LINE,
    VALID_ASSIGNMENT_ONLY_LINE = SPACES + "(([ \\t]*([\\w.-]+)[ \\t]*[=,])+)" + SPACES + "[\\w\"']"
            + END_OF_LINE,
    PARAM_LIST = SPACES + "((" + FINAL_OPTIONAL + TYPE + SPACES + ")" +
            "(" + VALID_VARIABLE_NAME + ")" + SPACES + "[,]?" + ")*" + SPACES,
    ARGUMENTS_TO_METHOD = SPACES + "(((" + SPACES + VALID_VARIABLE_NAME + SPACES + ")" +
            SPACES + "|" + SPACES + "(" + VALID_VALUE + "))" + SPACES + "[,]?" + ")*" + SPACES,
    VALID_METHOD_DECLARATION = VOID + VALID_METHOD_NAME + SPACES + "(\\(" + PARAM_LIST + "\\))" +
            START_OF_SECTION,
    STARTS_WITH_TYPE = SPACES + "^" + FINAL_OPTIONAL + TYPE + ONE_PLUS_SPACES + "\\w+" + SPACES,
    METHOD_CALL = SPACES + VALID_VARIABLE_NAME + SPACES + "\\(" + ARGUMENTS_TO_METHOD + "\\)" + END_OF_LINE,
    AND_OR = "(\\|\\|)|(&&)",
    LOGIC_OPERATORS = SPACES + "((" + BOOLEAN_VALID_VALUE + "|" + VALID_VARIABLE_NAME + ")" + SPACES +
            "((\\|\\|)|(&&))?)*",
    CONDITION_LINE = CONDITIONS + "\\(((" + LOGIC_OPERATORS + ")*)\\)" + START_OF_SECTION
    ;  // end of public static final String
}
