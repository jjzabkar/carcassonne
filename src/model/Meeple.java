package model;

import java.awt.Color;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-08
 */
public class Meeple {
    private Color color;

    public Meeple(Color aColor) {
        this.color = aColor;
    }

    public Color getColor() {
        return this.color;
    }

    // TODO method to draw meeple

}
