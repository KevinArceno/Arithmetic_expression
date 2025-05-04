# Arithmetic Expression Parser with ANTLR and Java
A visual parser application that demonstrates parsing arithmetic expressions using ANTLR4 and Java. This tool provides a graphical interface for entering expressions, visualizing parse trees, and evaluating expressions.

![image alt](https://github.com/KevinArceno/Arithmetic_expression/blob/4a9ece5f18a81859fd91b6e016f1310d87f7da70/1.png)



# 🚀 Features
  🎯 Real-time expression parsing with ANTLR

  🌳 Parse tree visualization with color-coded nodes

  🧮 Expression evaluation with support for variables

  🧾 Detailed parsing log: tokens, tree, and results

  📚 Built-in examples for quick testing


# 📦 Prerequisites
Java 11 or higher

Maven 3.6+

Graphviz (optional, for exporting parse trees)


# 📁 Project Structure
        src/
        ├── main/
        │   ├── antlr4/
        │   │   └── com/example/parser/
        │   │       └── Arithmetic.g4           # ANTLR grammar
        │   ├── java/com/example/parser/
        │   │   ├── Main.java                   # Entry point
        │   │   ├── SimpleParserApp.java       # Basic parser UI
        │   │   ├── CompleteParserApp.java     # Full application
        │   │   ├── ParseTreeVisualizer.java   # Custom visualization
        │   │   └── ArithmeticEvaluator.java   # Evaluator logic
        │   └── resources/
        │       └── ...
        ├── test/
        │   └── ...
        └── pom.xml                             # Maven config


#  🛠️ Build the Project
Clone and compile:

      git clone https://github.com/KevinArceno/Arithmetic_expression.git
      cd Arithmetic_expression
      mvn clean compile


This will:

1.  Download dependencies

2.   Generate ANTLR parser/lexer from the .g4 grammar

3.   Compile the Java source code

# ▶️ Run the Application
Choose any method below:
# Option 1: Using Maven
    mvn exec:java -Dexec.mainClass="com.example.parser.Main"
# Option 2: Run compiled classes
    java -cp target/classes com.example.parser.Main
# Option 3: Use JAR (after packaging)
    mvn clean package
    java -jar target/arithmetic-parser-1.0-SNAPSHOT.jar
# Option 4: From an IDE
1.  Import into IntelliJ IDEA or Eclipse
2.  Locate Main.java
3.  Right-click → Run

# 🧠 Grammar
ANTLR grammar used for parsing:

    grammar Arithmetic;
    
    expr
        : expr '+' term    # Addition
        | expr '-' term    # Subtraction
        | term             # TermOnly
        ;
    
    term
        : term '*' factor  # Multiplication
        | term '/' factor  # Division
        | factor           # FactorOnly
        ;
    
    factor
        : '(' expr ')'     # Parentheses
        | ID               # Variable
        | NUM              # Number
        ;
    
    ID  : [a-z] ;          // Variable: single lowercase letter
    NUM : [0-9]+ ;         // Numbers
    WS  : [ \t\r\n]+ -> skip ;
Precedence is handled via grammar rule order:
multiplication/division > addition/subtraction, with support for parentheses.

# 📊 Parse Tree Visualization
Color-coded nodes:

    - 🔴 Expressions (`expr`)
    - 🟢 Terms (`term`)
    - 🔵 Factors (`factor`)
    - 🟡 Operators (`+`, `-`, `*`, `/`)
    - ⚪ Terminals (IDs, NUMs)

# 🧪 Using the Application
1.  Type an arithmetic expression (e.g. (a + 3) * b)
2.  Click “Parse” to generate the tree
3.  Optionally assign variable values
4.  Click “Evaluate” to compute the result
5.  Explore the visual tree

# 🔧 Extending the Project
🧩 Add operators like exponentiation:

    - Update Arithmetic.g4
    
    - Run mvn generate-sources
    
    - Update ArithmeticEvaluator.java
🎛️ Add function support (e.g. sin, log):

    - Add grammar rules and tokens

    -  evaluation logic

🖼️ Enhance visuals:

    - Improve ParseTreeVisualizer

    - Add animation or export as image

# 📚 Comparison: ANTLR vs JFlex
    | Feature          | ANTLR                       | JFlex + CUP               |
    | ---------------- | --------------------------- | ------------------------- |
    | Lexing & Parsing | Combined                    | Separate tools            |
    | Tooling          | Modern, well-documented     | More manual setup         |
    | Visualization    | Better ANTLR plugin support | Requires additional work  |
    | Learning Curve   | Easier with examples        | More verbose, lower-level |

# 📄 License
This project is licensed under the [![MIT: License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/KevinArceno/Arithmetic_expression/blob/620d9f91507e4a07090b1e93878f7facf982f6bb/LICENSE) file. See the LICENSE file.


# 🙏 Acknowledgments
    Terence Parr and the ANTLR project
    
    Java Swing for GUI
    
    Graphviz (for potential tree exports)

