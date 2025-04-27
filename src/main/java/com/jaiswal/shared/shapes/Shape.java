package com.jaiswal.shared.shapes;

import com.jaiswal.shared.IDrawable;

import java.awt.*;
import java.io.Serializable;

public abstract class Shape implements IDrawable, Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected Color color;
    protected int strokeWidth;
    protected Point startPoint;
    protected Point endPoint;

    public Shape(Color color, int strokeWidth, Point startPoint, Point endPoint) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    // Getters and setters
    public Color getColor() {
        return color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }
}
