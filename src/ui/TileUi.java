package ui;

import java.awt.Color;
import java.awt.Graphics;

import model.Tile;
import model.TileType;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class TileUi implements DrawableInterface {

	private TileType[][] tile;
	private String identifier = "";
	private int orientation = 0;

	private int x = 0;
	private int y = 0;

	/** Denotes the width & height of a tileType element. **/
	public static final int tileTypeSize = 10;

	private TileUi() {
		// Create the tile array.
		tile = new TileType[Tile.tileSize][Tile.tileSize];

		// Set the corners.
		tile[0][0] = null;
		tile[0][Tile.tileSize - 1] = null;
		tile[Tile.tileSize - 1][0] = null;
		tile[Tile.tileSize - 1][Tile.tileSize - 1] = null;
	}

	public TileUi(TileType[][] tile, String identifier) {
		this();

		for (int i = 0; i < tile.length; i++) {

			for (int j = 0; j < tile[i].length; j++) {
				this.tile[i][j] = tile[i][j];
			}
		}

		this.identifier = identifier;
	}

	/**
	 * Rotate the tile clockwise by 90 degrees.
	 */
	public void rotateClockwise() {
		TileType[][] ret = new TileType[Tile.tileSize][Tile.tileSize];

		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[i].length; j++) {
				ret[i][j] = tile[Tile.tileSize - j - 1][i];
			}
		}

		tile = ret;
		orientation = (orientation + 1) % 4;
	}

	/**
	 * Rotate the tile counter-clockwise by 90 degrees.
	 */
	public void rotateCounterClockwise() {
		TileType[][] ret = new TileType[Tile.tileSize][Tile.tileSize];

		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[i].length; j++) {
				ret[i][j] = tile[j][Tile.tileSize - i - 1];
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
