package client.ui;

import java.awt.Color;
import java.awt.Graphics;

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

		this.color = color;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}

		if (other == this) {
			return true;
		}

		if (!(other instanceof MeepleUi)) {
			return false;
		}

		MeepleUi otherMeepleUi = (MeepleUi) other;

		// We won't worry about color since the game rules don't allow multiple
		// meeples on the same tile anyway (x & y will make them unique).
		if (this.x == otherMeepleUi.getx() && this.y == otherMeepleUi.gety()) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		// Whenever we override equals we need to override hashCode.

		// Let x & y position define the hash value. Max Java int is ten digits
		// long, about 20 m. Let x be the first 5 digits, and y be the latter 5.
		// Our x & y should be small enough to make this work. (gives us about
		// 130 max tile width at game board size of 150 tiles; we currently
		// have 70 width with 144 board size)
		return (x * 100000) + y;
	}

	public Color getColor() {
		return color;
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
		g.fillRoundRect(x, y, TileUi.tileTypeSize, TileUi.tileTypeSize,
				TileUi.tileTypeSize / 4, TileUi.tileTypeSize / 4);
	}
}
