package com.jaiswal.shared.shapes;

import java.awt.*;

public class Line extends Shape {
    private static final long serialVersionUID = 1L;

    public Line(Color color, int strokeWidth, Point startPoint, Point endPoint) {
        super(color, strokeWidth, startPoint, endPoint);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth));
        g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    }
}