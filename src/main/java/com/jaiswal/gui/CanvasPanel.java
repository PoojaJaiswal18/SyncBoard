package com.jaiswal.gui;
import com.jaiswal.shared.IDrawable;
import com.jaiswal.shared.TextElement;
import com.jaiswal.shared.shapes.Circle;
import com.jaiswal.shared.shapes.Line;
import com.jaiswal.shared.shapes.Rectangle;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CanvasPanel extends JPanel {
    private Map<Integer, IDrawable> elements = new ConcurrentHashMap<>();
    private Point startPoint;
    private Point currentPoint;
    private String currentTool = "Line";
    private Color currentColor = Color.BLACK;
    private int currentStrokeWidth = 2;
    private Font currentFont = new Font("Arial", Font.PLAIN, 12);
    private DrawListener drawListener;
    private TextInputListener textInputListener;

    public interface DrawListener {
        void onShapeDrawn(IDrawable shape);
        void onTextAdded(TextElement text);
    }

    public interface TextInputListener {
        void requestTextInput(Point location);
    }

    public CanvasPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                currentPoint = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint != null) {
                    if (currentTool.equals("Text")) {
                        if (textInputListener != null) {
                            textInputListener.requestTextInput(e.getPoint());
                        }
                    } else {
                        IDrawable shape = createShape(currentTool, startPoint, e.getPoint());
                        if (shape != null && drawListener != null) {
                            drawListener.onShapeDrawn(shape);
                        }
                    }
                    startPoint = null;
                    currentPoint = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = e.getPoint();
                repaint();
            }
        });
    }

    public void setElements(Map<Integer, IDrawable> elements) {
        this.elements = elements;
        repaint();
    }

    public void setCurrentTool(String tool) {
        this.currentTool = tool;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setCurrentStrokeWidth(int width) {
        this.currentStrokeWidth = width;
    }

    public void setCurrentFont(Font font) {
        this.currentFont = font;
    }

    public void setDrawListener(DrawListener listener) {
        this.drawListener = listener;
    }

    public void setTextInputListener(TextInputListener listener) {
        this.textInputListener = listener;
    }

    public void addText(String text, Point location) {
        TextElement textElement = new TextElement(text, location, currentColor, currentFont);
        if (drawListener != null) {
            drawListener.onTextAdded(textElement);
        }
    }

    private IDrawable createShape(String tool, Point start, Point end) {
        // Using enhanced switch statement as suggested by IDE warning
        return switch (tool) {
            case "Line" -> new Line(currentColor, currentStrokeWidth, new Point(start), new Point(end));
            case "Rectangle" -> new Rectangle(currentColor, currentStrokeWidth, new Point(start), new Point(end));
            case "Circle" -> new Circle(currentColor, currentStrokeWidth, new Point(start), new Point(end));
            default -> null;
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all elements
        for (IDrawable element : elements.values()) {
            element.draw(g2d);
        }

        // Draw current shape being drawn
        if (startPoint != null && currentPoint != null && !currentTool.equals("Text")) {
            IDrawable previewShape = createShape(currentTool, startPoint, currentPoint);
            if (previewShape != null) {
                previewShape.draw(g2d);
            }
        }
    }
}