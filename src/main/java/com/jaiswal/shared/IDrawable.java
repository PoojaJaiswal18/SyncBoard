package com.jaiswal.shared;

import java.awt.Graphics2D;
import java.io.Serializable;

public interface IDrawable extends Serializable {
    void draw(Graphics2D g);
    int getId();
    void setId(int id);
}