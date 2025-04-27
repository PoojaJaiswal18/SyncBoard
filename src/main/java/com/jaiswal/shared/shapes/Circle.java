package com.jaiswal.shared.shapes;
import java.awt.*;

public class Circle extends Shape {
    private static final long serialVersionUID = 1L;

    public Circle(Color color, int strokeWidth, Point startPoint, Point endPoint) {
        super(color, strokeWidth, startPoint, endPoint);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth));

        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(endPoint.x - startPoint.x);
        int height = Math.abs(endPoint.y - startPoint.y);

        g.drawOval(x, y, width, height);
    }
}