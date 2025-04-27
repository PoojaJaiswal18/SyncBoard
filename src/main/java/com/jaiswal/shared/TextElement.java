package com.jaiswal.shared;
import java.awt.*;

public class TextElement implements IDrawable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String text;
    private Point position;
    private Color color;
    private Font font;

    public TextElement(String text, Point position, Color color, Font font) {
        this.text = text;
        this.position = position;
        this.color = color;
        this.font = font;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, position.x, position.y);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    // Getters
    public String getText() {
        return text;
    }

    public Point getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public Font getFont() {
        return font;
    }
}