package com.jaiswal.shared.shapes;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a circle shape in the whiteboard application.
 */
public class Circle extends Shape implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int centerX;
    private final int centerY;
    private final int radius;

    /**
     * Constructs a new circle with specified center point and radius.
     *
     * @param centerX The x-coordinate of the center point
     * @param centerY The y-coordinate of the center point
     * @param radius The radius of the circle
     */
    public Circle(int centerX, int centerY, int radius) {
        super(Color.BLACK, 2, null, null);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    /**
     * Constructs a new circle with specified center, radius, and appearance.
     *
     * @param centerX The x-coordinate of the center point
     * @param centerY The y-coordinate of the center point
     * @param radius The radius of the circle
     * @param color The color of the circle
     * @param strokeWidth The stroke width for drawing
     */
    public Circle(int centerX, int centerY, int radius, Color color, float strokeWidth) {
        super(color, (int)strokeWidth, null, null);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g) {
        // Save the original stroke and color
        Stroke originalStroke = g.getStroke();
        Color originalColor = g.getColor();

        // Set drawing properties
        g.setColor(getColor());
        g.setStroke(new BasicStroke(getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw the circle
        int diameter = radius * 2;
        g.drawOval(centerX - radius, centerY - radius, diameter, diameter);

        // Restore original graphics settings
        g.setStroke(originalStroke);
        g.setColor(originalColor);
    }
}