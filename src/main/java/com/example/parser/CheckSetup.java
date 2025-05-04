package com.example.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.File;

public class CheckSetup {
public static void main(String[] args) {
    System.out.println("Checking ANTLR Parser Setup...");
    
    // Check if ANTLR classes are available
    try {
        Class<?> lexerClass = Class.forName("org.antlr.v4.runtime.Lexer");
        System.out.println("✓ ANTLR runtime is available: " + lexerClass.getName());
    } catch (ClassNotFoundException e) {
        System.out.println("✗ ANTLR runtime is NOT available");
    }
    
    // Check if generated parser classes are available
    try {
        Class<?> parserClass = Class.forName("com.example.parser.ArithmeticParser");
        System.out.println("✓ Generated parser classes are available: " + parserClass.getName());
    } catch (ClassNotFoundException e) {
        System.out.println("✗ Generated parser classes are NOT available");
    }
    
    // Check if grammar file exists
    File grammarFile = new File("src/main/antlr4/com/example/parser/Arithmetic.g4");
    if (grammarFile.exists()) {
        System.out.println("✓ Grammar file exists");
    } else {
        System.out.println("✗ Grammar file does NOT exist");
    }
    
    // Try parsing a simple expression
    try {
        String input = "a + b * c";
        CharStream charStream = CharStreams.fromString(input);
        ArithmeticLexer lexer = new ArithmeticLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticParser parser = new ArithmeticParser(tokens);
        ParseTree tree = parser.expr();
        
        System.out.println("✓ Successfully parsed: " + input);
        System.out.println("Parse tree: " + tree.toStringTree(parser));
    } catch (Exception e) {
        System.out.println("✗ Failed to parse expression");
        e.printStackTrace();
    }
    
    System.out.println("\nSetup check complete.");
}
}