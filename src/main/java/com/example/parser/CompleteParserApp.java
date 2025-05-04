package com.example.parser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public class CompleteParserApp {
    private static ParseTreeVisualizer treeVisualizer;
    private static Map<String, JTextField> variableFields = new HashMap<>();
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("ANTLR Arithmetic Expression Parser & Evaluator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // Create the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel inputLabel = new JLabel("Enter Expression:");
        JTextField inputField = new JTextField("a + b * c");
        JButton parseButton = new JButton("Parse");
        JButton evaluateButton = new JButton("Evaluate");
        
        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(parseButton);
        buttonPanel.add(evaluateButton);
        
        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        // Create examples panel
        JPanel examplesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        examplesPanel.add(new JLabel("Examples:"));
        
        String[] examples = {"a + b * c", "(a + b) * c", "a * (b + c)", "a + b + c * d"};
        for (String example : examples) {
            JButton exampleButton = new JButton(example);
            exampleButton.addActionListener(e -> inputField.setText(example));
            examplesPanel.add(exampleButton);
        }
        
        // Create variables panel
        JPanel variablesPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        variablesPanel.setBorder(BorderFactory.createTitledBorder("Variable Values"));
        
        String[] varNames = {"a", "b", "c", "d"};
        for (String varName : varNames) {
            JPanel varPanel = new JPanel(new BorderLayout(5, 0));
            varPanel.add(new JLabel(varName + " = "), BorderLayout.WEST);
            JTextField varField = new JTextField("5");
            varPanel.add(varField, BorderLayout.CENTER);
            variablesPanel.add(varPanel);
            variableFields.put(varName, varField);
        }
        
        // Create top panel to hold input and variables
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(variablesPanel, BorderLayout.CENTER);
        topPanel.add(examplesPanel, BorderLayout.SOUTH);

        // Create output panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);

        // Tree panel for parse tree visualization
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createTitledBorder("Parse Tree Visualization"));
        
        treeVisualizer = new ParseTreeVisualizer(null, null);
        JScrollPane treeScrollPane = new JScrollPane(treeVisualizer);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        // Log panel for parsing details
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Parsing Log"));
        
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        // Add panels to split pane
        splitPane.setLeftComponent(treePanel);
        splitPane.setRightComponent(logPanel);

        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Add action listener to parse button
        parseButton.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                logArea.setText("Please enter an expression");
                return;
            }
            
            try {
                // Parse the input and update the UI
                parseExpression(input, logArea, false);
            } catch (Exception ex) {
                logArea.setText("Error parsing expression: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Add action listener to evaluate button
        evaluateButton.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                logArea.setText("Please enter an expression");
                return;
            }
            
            try {
                // Parse and evaluate the expression
                parseExpression(input, logArea, true);
            } catch (Exception ex) {
                logArea.setText("Error evaluating expression: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Add main panel to frame and show
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private static void parseExpression(String input, JTextArea logArea, boolean evaluate) {
        StringBuilder log = new StringBuilder();
        log.append("Parsing expression: ").append(input).append("\n\n");

        // Create the lexer and parser
        CharStream charStream = CharStreams.fromString(input);
        ArithmeticLexer lexer = new ArithmeticLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        log.append("Tokens:\n");
        lexer.reset();
        while (true) {
            Token token = lexer.nextToken();
            if (token.getType() == Token.EOF) break;
            log.append(String.format("%-10s %-20s\n", 
                       token.getText(), 
                       lexer.getVocabulary().getDisplayName(token.getType())));
        }
        log.append("\n");

        // Reset lexer for parsing
        lexer.reset();
        tokens = new CommonTokenStream(lexer);
        
        ArithmeticParser parser = new ArithmeticParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                   int line, int charPositionInLine, String msg, RecognitionException e) {
                log.append("Syntax Error: ").append(msg).append("\n");
            }
        });

        // Parse the expression
        ParseTree parseTree = parser.expr();
        log.append("Parsing completed successfully\n\n");

        // Update the tree visualizer
        treeVisualizer.setParseTree(parseTree, parser.getRuleNames());
        
        // Add parse tree structure to log
        log.append("Parse Tree Structure:\n");
        log.append(parseTree.toStringTree(parser)).append("\n\n");
        
        // Evaluate the expression if requested
        if (evaluate) {
            try {
                ArithmeticEvaluator evaluator = new ArithmeticEvaluator();
                
                // Set variable values from UI
                for (Map.Entry<String, JTextField> entry : variableFields.entrySet()) {
                    String varName = entry.getKey();
                    JTextField field = entry.getValue();
                    try {
                        double value = Double.parseDouble(field.getText());
                        evaluator.setVariable(varName, value);
                    } catch (NumberFormatException ex) {
                        log.append("Warning: Invalid value for variable ").append(varName)
                           .append(". Using default value.\n");
                    }
                }
                
                double result = evaluator.evaluate(parseTree);
                log.append("\nEvaluation Result: ").append(result).append("\n");
            } catch (Exception e) {
                log.append("\nEvaluation Error: ").append(e.getMessage()).append("\n");
            }
        }

        // Update the log area
        logArea.setText(log.toString());
        logArea.setCaretPosition(0);
    }
}