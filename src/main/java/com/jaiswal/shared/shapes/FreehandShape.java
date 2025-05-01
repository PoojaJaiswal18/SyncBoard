package com.jaiswal.shared.shapes;

import com.jaiswal.shared.IDrawable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a freehand drawing shape consisting of multiple connected points.
 * This class provides a more sophisticated implementation for freehand drawing
 * compared to using simple line segments.
 */
public class FreehandShape extends Shape implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    // Collection of points that make up the freehand drawing path
    private final List<Point> points;

    /**
     * Constructs a new freehand shape with the specified properties.
     *
     * @param points The list of points forming the path
     * @param color The color of the shape
     * @param strokeWidth The stroke width for drawing
     */
    public FreehandShape(List<Point> points, Color color, float strokeWidth) {
        super(
                // Calculate bounding box parameters
                getMinX(points),
                getMinY(points),
                getWidth(points),
                getHeight(points),
                color,
                strokeWidth
        );

        // Create a deep copy of the points to avoid external modifications
        this.points = new ArrayList<>(points.size());
        for (Point p : points) {
            this.points.add(new Point(p));
        }
    }

    /**
     * Simpler constructor for compatibility with CanvasPanel
     *
     * @param points The list of points forming the path
     */
    public FreehandShape(List<Point> points) {
        this(points, Color.BLACK, 2.0f);
    }

    /**
     * Finds the minimum x coordinate in a list of points
     */
    private static int getMinX(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return 0;
        }

        int minX = Integer.MAX_VALUE;
        for (Point p : points) {
            minX = Math.min(minX, p.x);
        }
        return minX;
    }

    /**
     * Finds the minimum y coordinate in a list of points
     */
    private static int getMinY(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return 0;
        }

        int minY = Integer.MAX_VALUE;
        for (Point p : points) {
            minY = Math.min(minY, p.y);
        }
        return minY;
    }

    /**
     * Calculates the width of the bounding box containing all points
     */
    private static int getWidth(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return 0;
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (Point p : points) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
        }
        return maxX - minX;
    }

    /**
     * Calculates the height of the bounding box containing all points
     */
    private static int getHeight(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return 0;
        }

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point p : points) {
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        return maxY - minY;
    }

    /**
     * Gets the list of points in this freehand shape
     *
     * @return The list of points
     */
    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    /**
     * Draws the freehand shape on the graphics context
     *
     * @param g The graphics context to draw on
     */
    @Override
    public void draw(Graphics2D g) {
        if (points.size() < 2) {
            return; // Nothing to draw
        }

        // Save the original stroke and color
        Stroke originalStroke = g.getStroke();
        Color originalColor = g.getColor();

        // Set drawing properties
        g.setColor(getColor());
        g.setStroke(new BasicStroke(getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw connected line segments between consecutive points
        Point prevPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            g.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
            prevPoint = currentPoint;
        }

        // Restore original graphics settings
        g.setStroke(originalStroke);
        g.setColor(originalColor);
    }

    /**
     * Creates a string representation of the freehand shape
     *
     * @return The string representation
     */
    @Override
    public String toString() {
        return "FreehandShape[points=" + points.size() + ", color=" + getColor() +
                ", strokeWidth=" + getStrokeWidth() + "]";
    }
}