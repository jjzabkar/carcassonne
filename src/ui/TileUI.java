package ui;

import java.awt.Graphics;
import java.awt.Image;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-28
 */
public class TileUI implements DrawableInterface {

    private int x;
    private int y;
    private Image tile;

    public TileUI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rotateCounterClockwise() {
    }

    public void rotateClockwise() {
    }

    /**
     * Draw a simple representation of the tile using the string shorthand.
     * 
     * @param g The graphics object to draw the tile on.
     */
    public void draw(Graphics g) {
    }

    // Accessor/Mutators
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = x;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

}
