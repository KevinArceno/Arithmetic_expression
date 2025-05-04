package com.example.parser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

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

public class EnhancedParserApp {
    private static ParseTreeVisualizer treeVisualizer;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("ANTLR Arithmetic Expression Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        // Create the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel inputLabel = new JLabel("Enter Expression:");
        JTextField inputField = new JTextField("a + b * c");
        JButton parseButton = new JButton("Parse");
        
        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(parseButton, BorderLayout.EAST);

        // Create examples panel
        JPanel examplesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        examplesPanel.add(new JLabel("Examples:"));
        
        String[] examples = {"a + b * c", "(a + b) * c", "a * (b + c)", "a + b + c * d"};
        for (String example : examples) {
            JButton exampleButton = new JButton(example);
            exampleButton.addActionListener(e -> inputField.setText(example));
            examplesPanel.add(exampleButton);
        }

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
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(examplesPanel, BorderLayout.SOUTH);
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
                parseExpression(input, logArea);
            } catch (Exception ex) {
                logArea.setText("Error parsing expression: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Add main panel to frame and show
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private static void parseExpression(String input, JTextArea logArea) {
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
        
        // Add rule names to log
        log.append("Grammar Rules:\n");
        String[] ruleNames = parser.getRuleNames();
        for (int i = 0; i < ruleNames.length; i++) {
            log.append(String.format("%d: %s\n", i, ruleNames[i]));
        }

        // Update the log area
        logArea.setText(log.toString());
        logArea.setCaretPosition(0);
    }
}