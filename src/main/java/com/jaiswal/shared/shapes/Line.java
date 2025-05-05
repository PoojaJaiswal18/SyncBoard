package com.jaiswal.shared.shapes;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a line shape in the whiteboard application.
 */
public class Line extends Shape implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    /**
     * Constructs a new line with specified start and end points.
     *
     * @param x1 The x-coordinate of the start point
     * @param y1 The y-coordinate of the start point
     * @param x2 The x-coordinate of the end point
     * @param y2 The y-coordinate of the end point
     */
    public Line(int x1, int y1, int x2, int y2) {
        super(Color.BLACK, 2, null, null);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Constructs a new line with specified start and end points and appearance.
     *
     * @param x1 The x-coordinate of the start point
     * @param y1 The y-coordinate of the start point
     * @param x2 The x-coordinate of the end point
     * @param y2 The y-coordinate of the end point
     * @param color The color of the line
     * @param strokeWidth The stroke width of the line
     */
    public Line(int x1, int y1, int x2, int y2, Color color, float strokeWidth) {
        super(color, (int)strokeWidth, null, null);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics2D g) {
        // Save the original stroke and color
        Stroke originalStroke = g.getStroke();
        Color originalColor = g.getColor();

        // Set drawing properties
        g.setColor(getColor());
        g.setStroke(new BasicStroke(getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw the line
        g.drawLine(x1, y1, x2, y2);

        // Restore original graphics settings
        g.setStroke(originalStroke);
        g.setColor(originalColor);
    }
}