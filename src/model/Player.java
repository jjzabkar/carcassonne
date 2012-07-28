package model;

import java.awt.Color;
import java.awt.Point;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-08
 */
public class Player {

	private Color color;
	private Meeple[] meeples = new Meeple[7];
	private int score = 0;

	private Tile currentTile = null;
	private Point lastTilePlacedPos = new Point(-1, -1);

	public Player(Color aColor) {
		this.color = aColor;

		for (int i = 0; i < this.meeples.length; i++) {
			this.meeples[i] = new Meeple(this.color);
		}
	}

	// Accessor Methods
	public int getScore() {
		return this.score;
	}

	public Tile getCurrentTile() {
		return this.currentTile;
	}

	public Meeple[] getMeeples() {
		return this.meeples;
	}

	public int getLastTilePlacedXPos() {
		return (int) this.lastTilePlacedPos.getX();
	}

	public int getLastTilePlacedYPos() {
		return (int) this.lastTilePlacedPos.getY();
	}

	public Color getColor() {
		return this.color;
	}

	// Mutator Methods
	public void setScore(int aScore) {
		this.score = aScore;
	}

	public void setCurrentTile(Tile aTile) {
		this.currentTile = aTile;
	}

	public void setLastTilePlacedPos(int xPos, int yPos) {
		this.lastTilePlacedPos = new Point(xPos, yPos);
	}

}
