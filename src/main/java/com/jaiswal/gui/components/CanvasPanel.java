package com.jaiswal.gui.components;

import com.jaiswal.client.WhiteboardClient;
import com.jaiswal.gui.dialogs.TextInputDialog;
import com.jaiswal.gui.utils.UIConstants;
import com.jaiswal.shared.IDrawable;
import com.jaiswal.shared.TextElement;
import com.jaiswal.shared.shapes.Shape;
import com.jaiswal.shared.shapes.Line;
import com.jaiswal.shared.shapes.Circle;
import com.jaiswal.shared.shapes.Rectangle;
import com.jaiswal.shared.shapes.FreehandShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Canvas panel for the whiteboard application.
 * Handles drawing operations, zoom, and user interactions with the canvas.
 */
public class CanvasPanel extends JPanel {

    // Drawing and canvas state
    private final WhiteboardClient client;
    private Map<Integer, IDrawable> elements = new HashMap<>();
    private Point startPoint;
    private Point currentPoint;
    private String selectedTool = "PENCIL";
    private Color currentColor = Color.BLACK;
    private float currentStrokeWidth = 2.0f;
    private Font currentFont = new Font(UIConstants.FONT_FAMILY, Font.PLAIN, 16);
    private boolean isDragging = false;
    private boolean fillShape = false;
    private boolean showGrid = true;
    private int gridSize = 20;

    // For freehand drawing
    private List<Point> freehandPoints = new ArrayList<>();

    // Zoom controls
    private double zoomFactor = 1.0;
    private static final double ZOOM_STEP = 0.1;
    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 3.0;

    // UI feedback variables
    private final Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Cursor crosshairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    private final Cursor textCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

    /**
     * Constructs a new CanvasPanel
     *
     * @param client The whiteboard client to communicate with
     */
    public CanvasPanel(WhiteboardClient client) {
        this.client = client;
        initializePanel();
        setupEventListeners();
    }

    /**
     * Initializes panel properties and appearance
     */
    private void initializePanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UIConstants.BORDER_COLOR));
        setPreferredSize(new Dimension(2000, 1500)); // Default canvas size
        setDoubleBuffered(true); // Reduces flicker during painting
        setCursor(defaultCursor);
    }

    /**
     * Sets up mouse and keyboard event listeners for the canvas
     */
    private void setupEventListeners() {
        // Mouse listeners for drawing operations
        CanvasMouseHandler mouseHandler = new CanvasMouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        // Mouse wheel listener for zoom
        addMouseWheelListener(e -> {
            // Zoom in/out with Ctrl+Mouse Wheel
            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                double zoomStep = e.getWheelRotation() < 0 ? ZOOM_STEP : -ZOOM_STEP;
                updateZoom(zoomFactor + zoomStep);
                e.consume();
            }
        });

        // Key bindings for common operations
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // Delete operation
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedElements();
            }
        });

        // Zoom operations
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK), "zoomIn");
        actionMap.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK), "zoomOut");
        actionMap.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK), "resetZoom");
        actionMap.put("resetZoom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetZoom();
            }
        });
    }

    /**
     * Inner class to handle mouse events on the canvas
     */
    private class CanvasMouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            requestFocusInWindow(); // Ensure canvas has focus for keyboard events

            // Capture start point adjusted for zoom
            Point2D.Double zoomedPoint = toModelCoordinates(e.getPoint());
            startPoint = new Point((int) zoomedPoint.x, (int) zoomedPoint.y);
            currentPoint = startPoint;
            isDragging = true;

            // For freehand drawing, start collecting points
            if ("FREEHAND".equals(selectedTool)) {
                freehandPoints.clear();
                freehandPoints.add(new Point(startPoint));
            }

            // Handle specific tool behaviors
            switch (selectedTool) {
                case "TEXT":
                    showTextInputDialog(startPoint);
                    break;
                case "SELECT":
                    // Implement selection logic
                    break;
                case "ERASER":
                    // Implement eraser logic
                    break;
                default:
                    // For drawing tools, just wait for the drag operation
                    break;
            }

            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isDragging) return;

            // Update current point adjusted for zoom
            Point2D.Double zoomedPoint = toModelCoordinates(e.getPoint());
            currentPoint = new Point((int) zoomedPoint.x, (int) zoomedPoint.y);

            // For freehand, collect points as we drag
            if ("FREEHAND".equals(selectedTool)) {
                freehandPoints.add(new Point(currentPoint));
            }

            // Handle drawing based on tool
            if ("PENCIL".equals(selectedTool) || "LINE".equals(selectedTool) ||
                    "RECTANGLE".equals(selectedTool) || "CIRCLE".equals(selectedTool) ||
                    "FREEHAND".equals(selectedTool)) {
                // For drawing preview, just repaint
                repaint();
            } else if ("ERASER".equals(selectedTool)) {
                // Implement eraser logic
                // client.eraseAt(currentPoint.x, currentPoint.y);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!isDragging) return;

            // Final coordinates adjusted for zoom
            Point2D.Double zoomedPoint = toModelCoordinates(e.getPoint());
            Point endPoint = new Point((int) zoomedPoint.x, (int) zoomedPoint.y);

            // Only create elements if points are different (to avoid accidental clicks)
            if (!startPoint.equals(endPoint) || "TEXT".equals(selectedTool)) {
                try {
                    switch (selectedTool) {
                        case "PENCIL":
                            client.drawShape(createPencilShape(startPoint, endPoint));
                            break;
                        case "FREEHAND":
                            if (freehandPoints.size() > 1) {
                                // Use our new FreehandShape class
                                client.drawShape(createFreehandShape(freehandPoints));
                            }
                            break;
                        case "LINE":
                            client.drawShape(createLineShape(startPoint, endPoint));
                            break;
                        case "RECTANGLE":
                            client.drawShape(createRectangleShape(startPoint, endPoint));
                            break;
                        case "CIRCLE":
                            client.drawShape(createCircleShape(startPoint, endPoint));
                            break;
                        case "TEXT":
                            // Text is handled in mousePressed
                            break;
                        case "SELECT":
                            // Selection logic
                            break;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CanvasPanel.this,
                            "Error drawing shape: " + ex.getMessage(),
                            "Drawing Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            isDragging = false;
            freehandPoints.clear();
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // Update cursor based on the current tool
            updateCursorForTool();
        }
    }

    /**
     * Creates a pencil shape drawable
     */
    private Shape createPencilShape(Point start, Point end) {
        // Using Line with just coordinates instead of points
        return new Line(start.x, start.y, end.x, end.y, currentColor, currentStrokeWidth);
    }

    /**
     * Creates a freehand shape from a collection of points
     */
    private Shape createFreehandShape(List<Point> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Freehand shape needs at least 2 points");
        }

        // Create a deep copy of the points to avoid modification after drawing
        List<Point> pointsCopy = new ArrayList<>(points.size());
        for (Point p : points) {
            pointsCopy.add(new Point(p));
        }

        // Use our dedicated FreehandShape class with color and stroke width
        return new FreehandShape(pointsCopy, currentColor, currentStrokeWidth);
    }

    /**
     * Creates a line shape drawable
     */
    private Line createLineShape(Point start, Point end) {
        return new Line(start.x, start.y, end.x, end.y, currentColor, currentStrokeWidth);
    }

    /**
     * Creates a rectangle shape drawable
     */
    private Rectangle createRectangleShape(Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(end.x - start.x);
        int height = Math.abs(end.y - start.y);

        return new Rectangle(x, y, width, height, currentColor, currentStrokeWidth);
    }

    /**
     * Creates a circle shape drawable
     */
    private Circle createCircleShape(Point start, Point end) {
        // Calculate center point
        int centerX = (start.x + end.x) / 2;
        int centerY = (start.y + end.y) / 2;

        // Calculate radius based on distance from center to end point
        double dx = end.x - centerX;
        double dy = end.y - centerY;
        int radius = (int) Math.sqrt(dx * dx + dy * dy);

        return new Circle(centerX, centerY, radius, currentColor, currentStrokeWidth);
    }

    /**
     * Updates mouse cursor based on the currently selected tool
     */
    private void updateCursorForTool() {
        switch (selectedTool) {
            case "TEXT":
                setCursor(textCursor);
                break;
            case "SELECT":
                setCursor(defaultCursor);
                break;
            case "HAND":
                setCursor(handCursor);
                break;
            default:
                setCursor(crosshairCursor);
                break;
        }
    }

    /**
     * Deletes selected elements from the canvas
     */
    private void deleteSelectedElements() {
        // Implement deletion logic here
        // client.deleteSelected();
    }

    /**
     * Displays the text input dialog at the specified point
     *
     * @param point The position for the text
     */
    private void showTextInputDialog(Point point) {
        try {
            // Create the dialog with the current font and color settings
            TextInputDialog dialog = new TextInputDialog(
                    SwingUtilities.getWindowAncestor(this),
                    currentFont,
                    currentColor);

            // Show the dialog and wait for user interaction
            dialog.setVisible(true);

            // Check if the user confirmed the dialog
            if (dialog.isConfirmed()) {
                // Get the entered text
                String text = dialog.getText();

                // Only create a new text element if text was entered
                if (text != null && !text.trim().isEmpty()) {
                    // Create a new text element with the user's selections - corrected argument order
                    TextElement textElement = new TextElement(
                            text,                       // String text
                            point,                      // Point position
                            dialog.getSelectedColor(),  // Color color
                            dialog.getSelectedFont()    // Font font
                    );

                    // Send the text element to the client for drawing
                    client.drawText(textElement);
                }
            }
        } catch (Exception ex) {
            // Handle any errors that might occur
            JOptionPane.showMessageDialog(
                    this,
                    "Error showing text dialog: " + ex.getMessage(),
                    "Text Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Converts screen coordinates to model coordinates based on zoom level
     *
     * @param screenPoint The point in screen coordinates
     * @return The point in model coordinates
     */
    private Point2D.Double toModelCoordinates(Point screenPoint) {
        double modelX = screenPoint.x / zoomFactor;
        double modelY = screenPoint.y / zoomFactor;
        return new Point2D.Double(modelX, modelY);
    }

    /**
     * Converts model coordinates to screen coordinates based on zoom level
     *
     * @param modelPoint The point in model coordinates
     * @return The point in screen coordinates
     */
    private Point2D.Double toScreenCoordinates(Point2D.Double modelPoint) {
        double screenX = modelPoint.x * zoomFactor;
        double screenY = modelPoint.y * zoomFactor;
        return new Point2D.Double(screenX, screenY);
    }

    /**
     * Updates the zoom factor and repaints the canvas
     *
     * @param newZoom The new zoom factor
     */
    private void updateZoom(double newZoom) {
        // Limit zoom to acceptable range
        zoomFactor = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));

        // Update panel size
        Dimension originalSize = new Dimension(2000, 1500);
        int newWidth = (int) (originalSize.width * zoomFactor);
        int newHeight = (int) (originalSize.height * zoomFactor);
        setPreferredSize(new Dimension(newWidth, newHeight));

        // Trigger layout update
        revalidate();
        repaint();
    }

    /**
     * Increases the zoom level by one step
     */
    public void zoomIn() {
        updateZoom(zoomFactor + ZOOM_STEP);
    }

    /**
     * Decreases the zoom level by one step
     */
    public void zoomOut() {
        updateZoom(zoomFactor - ZOOM_STEP);
    }

    /**
     * Resets zoom to 100%
     */
    public void resetZoom() {
        updateZoom(1.0);
    }

    /**
     * Sets the drawing elements to be displayed
     *
     * @param elements Map of drawable elements
     */
    public void setElements(Map<Integer, IDrawable> elements) {
        this.elements = new HashMap<>(elements);
        repaint();
    }

    /**
     * Sets the selected drawing tool
     *
     * @param tool The tool name
     */
    public void setSelectedTool(String tool) {
        this.selectedTool = tool;
        updateCursorForTool();
    }

    /**
     * Sets the current drawing color
     *
     * @param color The color for drawing
     */
    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    /**
     * Sets the current stroke width
     *
     * @param width The stroke width
     */
    public void setCurrentStrokeWidth(float width) {
        this.currentStrokeWidth = width;
    }

    /**
     * Sets the current font for text operations
     *
     * @param font The font for text
     */
    public void setCurrentFont(Font font) {
        this.currentFont = font;
    }

    /**
     * Sets whether to fill shapes with color
     *
     * @param fillShape True to fill shapes, false for outlines only
     */
    public void setFillShape(boolean fillShape) {
        this.fillShape = fillShape;
        repaint();
    }

    /**
     * Sets whether to show the grid
     *
     * @param showGrid True to show grid, false to hide
     */
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    /**
     * Sets the grid size
     *
     * @param gridSize Grid size in pixels
     */
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply antialiasing for smoother drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Apply zoom transformation
        AffineTransform originalTransform = g2d.getTransform();
        g2d.scale(zoomFactor, zoomFactor);

        // Draw grid background for better spatial awareness
        drawGrid(g2d);

        // Draw all elements from the map
        for (IDrawable drawable : elements.values()) {
            drawable.draw(g2d);
        }

        // Draw preview of the current drawing operation
        if (isDragging && startPoint != null && currentPoint != null) {
            drawPreview(g2d);
        }

        // Restore original transform
        g2d.setTransform(originalTransform);

        // Display zoom level indicator
        drawZoomIndicator(g2d);

        g2d.dispose();
    }

    /**
     * Draws a grid pattern on the canvas background
     *
     * @param g2d The graphics context
     */
    private void drawGrid(Graphics2D g2d) {
        // Skip grid if zoomed out too far or if grid is disabled
        if (zoomFactor < 0.7 || !showGrid) return;

        g2d.setColor(new Color(240, 240, 240));
        g2d.setStroke(new BasicStroke(0.5f));

        int width = (int) (getWidth() / zoomFactor);
        int height = (int) (getHeight() / zoomFactor);

        // Draw vertical lines
        for (int x = 0; x <= width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }

        // Draw horizontal lines
        for (int y = 0; y <= height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }
    }

    /**
     * Draws a preview of the current drawing operation
     *
     * @param g2d The graphics context
     */
    private void drawPreview(Graphics2D g2d) {
        g2d.setColor(currentColor);
        g2d.setStroke(new BasicStroke(currentStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int x1 = startPoint.x;
        int y1 = startPoint.y;
        int x2 = currentPoint.x;
        int y2 = currentPoint.y;

        switch (selectedTool) {
            case "PENCIL":
                g2d.drawLine(x1, y1, x2, y2);
                break;
            case "FREEHAND":
                // Draw all the collected points for freehand drawing
                if (freehandPoints.size() > 1) {
                    Point prevPoint = freehandPoints.get(0);
                    for (int i = 1; i < freehandPoints.size(); i++) {
                        Point p = freehandPoints.get(i);
                        g2d.drawLine(prevPoint.x, prevPoint.y, p.x, p.y);
                        prevPoint = p;
                    }
                }
                break;
            case "LINE":
                g2d.drawLine(x1, y1, x2, y2);
                break;
            case "RECTANGLE":
                int x = Math.min(x1, x2);
                int y = Math.min(y1, y2);
                int width = Math.abs(x2 - x1);
                int height = Math.abs(y2 - y1);

                if (fillShape) {
                    g2d.fillRect(x, y, width, height);
                } else {
                    g2d.drawRect(x, y, width, height);
                }
                break;
            case "CIRCLE":
                // Calculate center point
                int centerX = (x1 + x2) / 2;
                int centerY = (y1 + y2) / 2;

                // Calculate radius based on distance from center to current point
                double dx = x2 - centerX;
                double dy = y2 - centerY;
                int radius = (int) Math.sqrt(dx * dx + dy * dy);

                // Draw circle with center and radius
                if (fillShape) {
                    g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                } else {
                    g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                }
                break;
        }
    }

    /**
     * Draws zoom level indicator in the corner of the canvas
     *
     * @param g2d The graphics context
     */
    private void drawZoomIndicator(Graphics2D g2d) {
        String zoomText = String.format("%.0f%%", zoomFactor * 100);
        Font smallFont = new Font(UIConstants.FONT_FAMILY, Font.PLAIN, 10);
        FontMetrics metrics = g2d.getFontMetrics(smallFont);
        int textWidth = metrics.stringWidth(zoomText);

        // Create semi-transparent background
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(getWidth() - textWidth - 20, getHeight() - 25, textWidth + 10, 20, 10, 10);

        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.setFont(smallFont);
        g2d.drawString(zoomText, getWidth() - textWidth - 15, getHeight() - 10);
    }
}