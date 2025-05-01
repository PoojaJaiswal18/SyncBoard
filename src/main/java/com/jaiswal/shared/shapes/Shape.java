package com.jaiswal.shared.shapes;

import com.jaiswal.shared.IDrawable;

import java.awt.*;
import java.io.Serializable;

/**
 * Base class for all drawable shapes in the whiteboard application.
 */
public abstract class Shape implements IDrawable, Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    // Basic properties common to all shapes
    protected int id;
    protected Color color;
    protected float strokeWidth;

    // Constructor for basic shapes like lines, rectangles, and circles
    public Shape(Color color, int strokeWidth, Point startPoint, Point endPoint) {
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    // Constructor for complex shapes like freehand that use bounding box parameters
    public Shape(int x, int y, int width, int height, Color color, float strokeWidth) {
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }
}