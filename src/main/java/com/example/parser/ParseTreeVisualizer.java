package com.example.parser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseTreeVisualizer extends JPanel {
    private ParseTree parseTree;
    private String[] ruleNames;
    private Map<ParseTree, Rectangle2D.Double> nodePositions = new HashMap<>();
    private static final int NODE_WIDTH = 80;
    private static final int NODE_HEIGHT = 30;
    private static final int VERTICAL_GAP = 50;
    private static final int HORIZONTAL_GAP = 10;

    public ParseTreeVisualizer(ParseTree parseTree, String[] ruleNames) {
        this.parseTree = parseTree;
        this.ruleNames = ruleNames;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (parseTree == null) return;
        
        // Calculate node positions
        calculateNodePositions();
        
        // Draw connections first (so they're behind nodes)
        drawConnections(g2d);
        
        // Draw nodes
        drawNodes(g2d);
    }

    private void calculateNodePositions() {
        nodePositions.clear();
        
        // Calculate width needed for each subtree
        Map<ParseTree, Integer> subtreeWidths = new HashMap<>();
        calculateSubtreeWidths(parseTree, subtreeWidths);
        
        // Calculate positions
        int totalWidth = subtreeWidths.get(parseTree);
        int startX = (getWidth() - totalWidth) / 2;
        
        calculateNodePositionsRecursive(parseTree, startX, 20, subtreeWidths);
    }

    private int calculateSubtreeWidths(ParseTree tree, Map<ParseTree, Integer> subtreeWidths) {
        if (tree.getChildCount() == 0) {
            // Leaf node
            String text = tree.getText();
            int width = Math.max(NODE_WIDTH, text.length() * 10);
            subtreeWidths.put(tree, width);
            return width;
        } else {
            // Calculate width for each child
            int totalWidth = 0;
            for (int i = 0; i < tree.getChildCount(); i++) {
                ParseTree child = tree.getChild(i);
                totalWidth += calculateSubtreeWidths(child, subtreeWidths) + HORIZONTAL_GAP;
            }
            
            // Remove extra gap after last child
            if (totalWidth > HORIZONTAL_GAP) {
                totalWidth -= HORIZONTAL_GAP;
            }
            
            // Ensure minimum width for the node
            totalWidth = Math.max(NODE_WIDTH, totalWidth);
            subtreeWidths.put(tree, totalWidth);
            return totalWidth;
        }
    }

    private void calculateNodePositionsRecursive(ParseTree tree, int x, int y, Map<ParseTree, Integer> subtreeWidths) {
        int width = subtreeWidths.get(tree);
        int nodeWidth = Math.min(NODE_WIDTH, width);
        
        // Center the node in its allocated space
        int nodeX = x + (width - nodeWidth) / 2;
        nodePositions.put(tree, new Rectangle2D.Double(nodeX, y, nodeWidth, NODE_HEIGHT));
        
        if (tree.getChildCount() > 0) {
            int childX = x;
            for (int i = 0; i < tree.getChildCount(); i++) {
                ParseTree child = tree.getChild(i);
                int childWidth = subtreeWidths.get(child);
                calculateNodePositionsRecursive(child, childX, y + VERTICAL_GAP, subtreeWidths);
                childX += childWidth + HORIZONTAL_GAP;
            }
        }
    }

    private void drawConnections(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        
        for (Map.Entry<ParseTree, Rectangle2D.Double> entry : nodePositions.entrySet()) {
            ParseTree parent = entry.getKey();
            Rectangle2D.Double parentRect = entry.getValue();
            
            for (int i = 0; i < parent.getChildCount(); i++) {
                ParseTree child = parent.getChild(i);
                Rectangle2D.Double childRect = nodePositions.get(child);
                
                if (childRect != null) {
                    // Draw line from bottom of parent to top of child
                    g2d.draw(new Line2D.Double(
                        parentRect.getCenterX(), parentRect.getMaxY(),
                        childRect.getCenterX(), childRect.getMinY()
                    ));
                }
            }
        }
    }

    private void drawNodes(Graphics2D g2d) {
        for (Map.Entry<ParseTree, Rectangle2D.Double> entry : nodePositions.entrySet()) {
            ParseTree node = entry.getKey();
            Rectangle2D.Double rect = entry.getValue();
            
            // Determine node type and color
            Color fillColor;
            String label;
            
            if (node instanceof RuleContext) {
                RuleContext context = (RuleContext) node;
                int ruleIndex = context.getRuleIndex();
                label = ruleNames[ruleIndex];
                
                // Color based on rule type
                if (label.equals("expr")) {
                    fillColor = new Color(255, 200, 200); // Light red
                } else if (label.equals("term")) {
                    fillColor = new Color(200, 255, 200); // Light green
                } else if (label.equals("factor")) {
                    fillColor = new Color(200, 200, 255); // Light blue
                } else {
                    fillColor = new Color(240, 240, 240); // Light gray
                }
            } else {
                // Terminal node
                label = node.getText();
                
                // Color based on token type
                if (label.equals("+") || label.equals("-") || label.equals("*") || label.equals("/")) {
                    fillColor = new Color(255, 255, 200); // Light yellow for operators
                } else if (label.equals("(") || label.equals(")")) {
                    fillColor = new Color(255, 230, 210); // Light orange for parentheses
                } else {
                    fillColor = new Color(240, 240, 240); // Light gray for other terminals
                }
            }
            
            // Draw rounded rectangle
            g2d.setColor(fillColor);
            g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 10, 10));
            
            g2d.setColor(Color.BLACK);
            g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 10, 10));
            
            // Draw text
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getHeight();
            
            g2d.drawString(label, 
                (float)(rect.x + (rect.width - textWidth) / 2), 
                (float)(rect.y + (rect.height + textHeight / 2) / 2));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void setParseTree(ParseTree parseTree, String[] ruleNames) {
        this.parseTree = parseTree;
        this.ruleNames = ruleNames;
        repaint();
    }
}