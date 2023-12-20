# Simplified Java Verifier

The Simplified Java Verifier is a Java program designed to validate and verify the syntax of Simplified Java (sJava) code files.\
It includes components for checking method declarations, global variables, method calls, conditions (if/while), and other structural elements in sJava code.


# File Descriptions

### ExitException.java
###### Package: root.exceptions
A class for an exit exception, representing an exception that will lead the program to stop running.\
It includes exit codes and informative error messages for different scenarios.\
i.e. an exception that will lead the program to stop running.

### Sjavac.java
###### Package: root.main
The main class for the project, responsible for handling the overall flow of the program.\
It performs two runs over the sJava file, first to detect global variables and methods, and second for regular processing.\
It uses other components like Scope, Type, and Method to validate and process the code.

### Method.java
###### Package: root
A class that handles method lines, including if/while statements that occur inside methods.\
It checks method declarations, return lines, end of section lines, method calls, and condition (if/while) declaration lines.\
It works in conjunction with other classes like Scope and Type.

### Type.java
###### Package: root
This class represents a type in the Sjava programming language.\
It includes constants for various strings such as numeric values, boolean values, and special characters.\
The class provides methods for checking declaration lines, assignment lines, and validating variable names.\
It also includes an enumeration (TypeCompare) for comparing different types and their associated values.

### Regex.java
###### Package: root
This class defines various regular expressions (regex) as static final strings.\
These regex patterns are used in the program to match and validate different constructs such as spaces, variable names, method names, declarations, assignments, and more.\
It provides a centralized place for managing all the regex used throughout the program.

### Scope.java
###### Package: root
This class represents a scope in the program, where a scope is defined by a pair of curly braces ('{' and '}').\
It includes fields for dictionaries representing the outer scope, inner scope, and methods list.\
The class has constructors for creating instances with or without initial dictionaries. Additionally, it includes a method (isAComment) to check if a given line is a comment line.


# Usage

Compile the project using a Java compiler:

```sh
javac root/*.java
```

Run the Sjavac class, providing the sJava file as a command line argument:

```sh
java root.main.Sjavac your_file.sjava
```

### Exit Codes:
```
0 - Successful execution.
1 - Error in code structure or syntax.
2 - Invalid number of command line arguments or file not found.
```


# Design

The ExitException.java class is designed to extend the Exception class to handle exceptions effectively.

Adhering to the Single Choice Principle, the code utilizes an Enum class named TypeCompare.\
This enum encompasses types for variables (int, char, String, double, boolean), each with its distinct values and functions for validating with the appropriate regex.

Following the modularity principle, my code is organized into levels during the analysis of Sjava code.\
Initially, a main function is responsible for checking the file path and reading it line by line.\
Subsequently, a class is implemented to validate each line's correctness.


# Implementation details

The project employs three dictionaries, namely methodDict, innerDict, and outerDict, to manage method names, their argument lists, declared variables, and their scopes efficiently.\
This choice was driven by the ease of checking for key existence (O(1)), prevention of duplicate values, and its overall contribution to the implementation.

To track the current scope and determine actions like adding a "return" to conclude a scope, the main class utilizes global variables.\
The depth of the current scope is represented by these global variables, starting from 0 for the global scope, 1 for the method scope, and 2 or higher for if/while scopes.

For enhanced code readability, a dedicated class containing regex string patterns is employed.\
This approach facilitates easy debugging by addressing issues in sub-patterns, automatically resolving them in more complex patterns utilizing these sub-patterns.\
Magic numbers are used judiciously to prevent redundant writing of sub-patterns.

The text analysis process is a combination of regex patterns, along with the usage of "split" and "replace all" operations to facilitate efficient parsing and manipulation of the code.\
This approach enhances the clarity and maintainability of the codebase.


# Error Handling

The error handling mechanism is facilitated by the "ExitException.java" class, which inherits from the Exception class.\
This class is designed to hold and throw exit numbers, with the following mapping: 2 for IO exceptions, 1 for issues in the sjava file, and 0 for valid syntax.

For IO exceptions, the program utilizes a try-catch block while attempting to open the given file path.\
In the event of an error during this operation, an ExitException with a type 2 error is thrown.

My custom exception class is also employed to handle errors in sjava files during the checking run.\
Despite the use of boolean methods to identify various line types (such as method declarations or method calls), 
there are scenarios where it is essential to halt the check upon encountering an illegal expression in the sjava file.\
In such cases, an exception is thrown, signaling the main method to terminate the run and return an error type 1.

This approach proves advantageous as it provides a consistent means to exit the program upon discovering errors in the sjava files, seamlessly integrating within the program's structure.


# Object Oriented Design

The Object-Oriented Design of the code revolves around breaking it into small, independent units.\
I employ classes for various types of lines in the sjava program, such as "Method.java" and "Type.java".\
Additionally, personalized methods handle specific line types or checks, like "checkLineAssignment" or "checkLineDeclaration", each self-contained for efficient management.

To maintain modularity and clarity, a class is utilized for regex patterns, and another holds scope information.\
This structured approach contributes to code organization and readability.

Consideration was given to alternative design options, such as separating the regex and pattern-checking into one class and managing method and variable information in another.\
However, the chosen approach of reading the sjava file line by line using distinct classes for different line types was deemed the most effective.

Addressing the addition of new variable types, like "float", is straightforward within the implementation.
It involves updating the Enum class to include the string "float" for the TYPE regex and adding a corresponding regex pattern for valid float values.

For introducing new features, such as different method types (e.g., "int foo()"), modifications are made to the "MethodDict".\
A variable for method type is added, ensuring it aligns with the expected type within the scope.

To incorporate standard Java methods (e.g., "System.out.println"), a comprehensive list is passed through, adding them to the global method dictionary.\
This dictionary serves as a repository for information on all Java standard methods, mirroring the organization for other methods in the sjava program.
