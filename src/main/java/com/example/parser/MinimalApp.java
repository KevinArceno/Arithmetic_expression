package com.example.parser;

import javax.swing.*;
import java.awt.*;

public class MinimalApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a simple frame
            JFrame frame = new JFrame("Minimal ANTLR Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            
            // Add a label
            JLabel label = new JLabel("Hello, ANTLR Parser!");
            label.setFont(new Font("Arial", Font.BOLD, 18));
            label.setHorizontalAlignment(JLabel.CENTER);
            
            frame.getContentPane().add(label);
            frame.setVisible(true);
        });
    }
}