package server.model;

import java.awt.Point;
import java.util.ArrayList;

public class Player {

	public final static int NUM_MEEPLES = 7;
	private ArrayList<Meeple> meeples = new ArrayList<Meeple>();
	private int score = 0;

	private Tile currentTile = null;
	private Point lastTilePlacedPos = new Point(-1, -1);

	public Player() {
		for (int i = 0; i < NUM_MEEPLES; i++) {
			this.meeples.add(new Meeple());
		}
	}

	// Accessor Methods
	public int getScore() {
		return this.score;
	}

	public Tile getCurrentTile() {
		return this.currentTile;
	}

	public ArrayList<Meeple> getMeeples() {
		return this.meeples;
	}

	public int getLastTilePlacedXPos() {
		return lastTilePlacedPos.x;
	}

	public int getLastTilePlacedYPos() {
		return lastTilePlacedPos.y;
	}

	// Mutator Methods
	public void setScore(int aScore) {
		this.score = aScore;
	}

	public void setCurrentTile(Tile aTile) {
		this.currentTile = aTile;
	}

	public void setLastTilePlacedPosition(int xPos, int yPos) {
		this.lastTilePlacedPos = new Point(xPos, yPos);
	}

}
