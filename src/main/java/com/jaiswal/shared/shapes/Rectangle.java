package com.jaiswal.shared.shapes;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a rectangle shape in the whiteboard application.
 */
public class Rectangle extends Shape implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    /**
     * Constructs a new rectangle with specified coordinates and dimensions.
     *
     * @param x The x-coordinate of the top-left corner
     * @param y The y-coordinate of the top-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     */
    public Rectangle(int x, int y, int width, int height) {
        super(Color.BLACK, 2, null, null);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a new rectangle with specified coordinates, dimensions, and appearance.
     *
     * @param x The x-coordinate of the top-left corner
     * @param y The y-coordinate of the top-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the rectangle
     * @param strokeWidth The stroke width for drawing
     */
    public Rectangle(int x, int y, int width, int height, Color color, float strokeWidth) {
        super(color, (int)strokeWidth, null, null);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g) {
        // Save the original stroke and color
        Stroke originalStroke = g.getStroke();
        Color originalColor = g.getColor();

        // Set drawing properties
        g.setColor(getColor());
        g.setStroke(new BasicStroke(getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw the rectangle
        g.drawRect(x, y, width, height);

        // Restore original graphics settings
        g.setStroke(originalStroke);
        g.setColor(originalColor);
    }
}