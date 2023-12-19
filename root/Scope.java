package root;

import java.util.HashMap;

/**
 * A class that represent a scope. scope start with '{' and ends with '}'.
 */
public class Scope {
    /*
    Represent the String "//".
     */
    private static final String DOUBLE_SLASH = "//";

    /**
     * Signs if a return is used.
     */
    public boolean isReturnUsed = false;

    /**
     * Dictionary for the outer scope dictionary
     */
    public HashMap<String, String[]> outerDict;
    /**
     * Dictionary for the inner scope dictionary
     */
    public HashMap<String, String[]> innerDict;
    /**
     * Dictionary for the methods list
     */
    public HashMap<String,String[]> methodDict;


    //======Constructors=====//

    /**
     * Default constructor for Scope
     *
     */
    public Scope() {
        this.outerDict = new HashMap<>();
        this.innerDict = new HashMap<>();
        this.methodDict = new HashMap<>();
    }

    /**
     * Constructor for Scope
     *
     * @param outerDict The dictionary of the outer scope
     */
    public Scope(HashMap<String, String[]> outerDict, HashMap<String,String[]> methodDict) {
        this.outerDict = outerDict;
        this.innerDict = new HashMap<>();
        this.methodDict = methodDict;
    }


    //======Methods=====//

    /**
     * Checks if the given line is a comment line.
     *
     * @param line The given line to check.
     * @return if it is a comment line - return true, else - false.
     */
    public static boolean isAComment(String line) {
        return line.startsWith(DOUBLE_SLASH);
    }
}
