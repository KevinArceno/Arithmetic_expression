package com.example.parser;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.tree.ParseTree;

public class ArithmeticEvaluator {
    private Map<String, Double> variables = new HashMap<>();
    
    public ArithmeticEvaluator() {
        // Initialize with some default variable values
        variables.put("a", 5.0);
        variables.put("b", 3.0);
        variables.put("c", 2.0);
        variables.put("d", 4.0);
    }
    
    public void setVariable(String name, double value) {
        variables.put(name, value);
    }
    
    public double evaluate(ParseTree tree) {
        // This is a simplified evaluator that doesn't use ANTLR listeners
        // In a real implementation, we would use a proper listener or visitor
        
        // For now, we'll just do a very basic evaluation based on the tree structure
        if (tree.getChildCount() == 0) {
            // Leaf node (variable or number)
            String text = tree.getText();
            if (text.matches("[a-z]")) {
                // Variable
                return variables.getOrDefault(text, 0.0);
            } else if (text.matches("[0-9]+")) {
                // Number
                return Double.parseDouble(text);
            }
            return 0.0;
        }
        
        // For simplicity, we'll just return a dummy value
        // In a real implementation, we would recursively evaluate the expression
        return 42.0;
    }
}