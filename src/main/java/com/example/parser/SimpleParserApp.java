package com.example.parser;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class SimpleParserApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create a simple frame
        JFrame frame = new JFrame("Simple ANTLR Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create a panel with a text field and button
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField inputField = new JTextField("a + b * c");
        JButton parseButton = new JButton("Parse");
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);

        panel.add(inputField, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        panel.add(parseButton, BorderLayout.SOUTH);

        // Add action listener to the button
        parseButton.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                outputArea.setText("Please enter an expression");
                return;
            }

            try {
                // Parse the input
                CharStream charStream = CharStreams.fromString(input);
                ArithmeticLexer lexer = new ArithmeticLexer(charStream);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                ArithmeticParser parser = new ArithmeticParser(tokens);

                // Get the parse tree
                ParseTree tree = parser.expr();

                // Display the result
                outputArea.setText("Parsing successful!\n\n");
                outputArea.append("Parse Tree:\n");
                outputArea.append(tree.toStringTree(parser));
            } catch (Exception ex) {
                outputArea.setText("Error parsing expression: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Add the panel to the frame and show
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}