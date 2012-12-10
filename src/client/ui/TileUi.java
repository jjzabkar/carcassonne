package client.ui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class TileUi implements DrawableInterface {

	private enum TileType {
		ROAD, FIELD, CITY, CLOISTER, RIVER
	}

	private TileType[][] tile = new TileType[tileSize][tileSize];
	private String identifier;
	private int orientation;

	private int x = 0;
	private int y = 0;

	/** Denotes the width & height of a tile (number of tileType's). **/
	// Keep in sync with Tile.java.
	public static final int tileSize = 7;

	/** Denotes the width & height of a tileType element. **/
	public static final int tileTypeSize = 10;

	public TileUi(String identifier, int orientation) {

		this.identifier = identifier;
		this.orientation = orientation;
		createTile();
	}

	/**
	 * Based on an input character, return the corresponding TileType.
	 * 
	 * @param letter
	 *            Any of the letters k, c, r, i, f, x. (case insensitive)
	 * @return A TileType of either Cloister, City, Road, River, Field, Null.
	 */
	private TileType charToTileType(char letter) {
		if ('k' == letter || 'K' == letter) {
			return TileType.CLOISTER;
		} else if ('c' == letter || 'C' == letter) {
			return TileType.CITY;
		} else if ('r' == letter || 'R' == letter) {
			return TileType.ROAD;
		} else if ('i' == letter || 'I' == letter) {
			return TileType.RIVER;
		} else if ('f' == letter || 'F' == letter) {
			return TileType.FIELD;
		} else if ('x' == letter || 'X' == letter) {
			return null;
		} else {
			return null;
		}
	}

	private void createTile() {

		// The orientation is matched to the server representation.
		char[][] tile = new char[][] { {} };

		if (identifier.equals("CH")) {

			// city half
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'C', 'C' },
					{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
					{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
					{ 'F', 'F', 'F', 'C', 'C', 'C', 'C' },
					{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
					{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

		} else if (identifier.equals("CR3")) {

			// city road 3way
			tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
					{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'C', 'F', 'F', 'F', 'R', 'R', 'R' },
					{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		} else if (identifier.equals("C2O")) {

			// city2 opposite
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("K")) {

			// cloister
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
					{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
					{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("KR")) {

			// cloister road
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
					{ 'R', 'R', 'K', 'K', 'K', 'F', 'F' },
					{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("R4")) {

			// road 4way
			tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'R', 'R', 'R', 'x', 'R', 'R', 'R' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		} else if (identifier.equals("R2")) {

			// road bend [2]
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'R', 'R', 'R', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		} else if (identifier.equals("R")) {

			// road
			tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		} else if (identifier.equals("R3")) {

			// road 3way
			tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'R', 'R', 'R', 'F', 'R', 'R', 'R' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("C")) {

			// city
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

		} else if (identifier.equals("C3")) {

			// city 3
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("C3R")) {

			// city 3 road
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		} else if (identifier.equals("C2A")) {

			// city 2 adjacent
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

		} else if (identifier.equals("CS")) {

			// city side
			tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("CR")) {

			// city road
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'R', 'R', 'R', 'R', 'R', 'R', 'R' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("CR2")) {

			// city road bend [2]
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'R', 'R', 'R' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		} else if (identifier.equals("CR2M")) {

			// city road bend [2] mirrored
			tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
					{ 'R', 'R', 'R', 'R', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
					{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		} else if (identifier.equals("C2")) {
			// city 2
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
					{ 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
					{ 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
					{ 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
					{ 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
					{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

		} else if (identifier.equals("CHR2")) {

			// city half road bend [2]
			tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
					{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
					{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
					{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
					{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
					{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
					{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };
		}

		for (int i = 0; i < this.tile.length; i++) {
			for (int j = 0; j < this.tile[i].length; j++) {
				this.tile[j][i] = charToTileType(tile[j][i]);
			}
		}

		for (int i = 0; i < orientation; i++) {
			rotateClockwise();
		}
	}

	/**
	 * Rotate the tile clockwise by 90 degrees.
	 */
	public void rotateClockwise() {
		TileType[][] ret = new TileType[tileSize][tileSize];

		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[i].length; j++) {
				ret[i][j] = tile[tileSize - j - 1][i];
			}
		}

		tile = ret;
		orientation = (orientation + 1) % 4;
	}

	/**
	 * Rotate the tile counter-clockwise by 90 degrees.
	 */
	public void rotateCounterClockwise() {
		TileType[][] ret = new TileType[tileSize][tileSize];

		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[i].length; j++) {
				ret[i][j] = tile[j][tileSize - i - 1];
			}
		}

		tile = ret;
		// Modulus of a negative number doesn't work, so add 4.
		orientation = (orientation + 3) % 4;
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getOrientation() {
		return orientation;
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

	private final Color lightbrown = new Color(185, 156, 107);
	private final Color lightgreen = new Color(169, 208, 79);
	private final Color red = new Color(126, 46, 31);
	private final Color darkblue = new Color(24, 61, 97);

	@Override
	public void draw(Graphics g) {

		Color tileTypeColor = Color.black;

		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[i].length; j++) {

				if (tile[i][j] != null) {

					switch (tile[i][j]) {

					case CLOISTER:
						tileTypeColor = red;
						break;
					case CITY:
						tileTypeColor = lightbrown;
						break;
					case ROAD:
						tileTypeColor = Color.gray;
						break;
					case RIVER:
						tileTypeColor = darkblue;
						break;
					case FIELD:
						tileTypeColor = lightgreen;
						break;
					}

				} else {

					tileTypeColor = Color.white;
				}

				g.setColor(tileTypeColor);
				g.fillRect(x + (j * TileUi.tileTypeSize), y
						+ (i * TileUi.tileTypeSize), TileUi.tileTypeSize,
						TileUi.tileTypeSize);
			}
		}
	}
}
