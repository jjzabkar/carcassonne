package ui;

import java.awt.Color;
import java.awt.Graphics;

import model.Tile;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class MeepleUi implements DrawableInterface {

	private Color color;
	private int x = 0;
	private int y = 0;

	public MeepleUi(Color color, int x, int y) {

		// TODO: differentiate meeple and tile color?
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public Color getColor() {
		return this.color;
	}

	public int getx() {
		return x;
	}

	public int gety() {
		return y;
	}

	public void setx(int x) {
		this.x = x;
	}

	public void sety(int y) {
		this.y = y;
	}

	@Override
	public void draw(Graphics g) {

		// Create some sort of a shape to be on the middle of a tile.
		g.setColor(color);
		g.fillRoundRect(x, y, Tile.tileTypeSize, Tile.tileTypeSize,
				Tile.tileTypeSize / 4, Tile.tileTypeSize / 4);
	}
}
