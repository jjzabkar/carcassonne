package model;

import java.awt.Color;
import java.awt.Graphics;

import ui.DrawableInterface;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-08
 */
public class Meeple implements DrawableInterface {

	private Color color;

	// UI variables.
	private int x = 0;
	private int y = 0;

	public Meeple(Color color) {
		// TODO: some other means to differentiate meeple and tile color
		// TODO: above; also, in board class, the meeples are related to the
		// player by their color. we need something better
		this.color = color;
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
