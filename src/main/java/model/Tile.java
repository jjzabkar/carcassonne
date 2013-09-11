package model;

public class Tile {
	// A tile is stored as a 7x7 matrix, where the corner cells are null.
	//
	// Stored:
	// [ [ _ _ _ _ _ _ _ ],] X: 3, 2
	// | [ _ _ _ _ _ _ _ ],| p: y, x
	// | [ _ _ _ _ _ _ _ ],|
	// | [ _ _ X _ _ _ _ ],|
	// | [ _ _ _ _ _ _ _ ],|
	// | [ _ _ _ _ _ _ _ ],|
	// [ [ _ _ _ _ _ _ _ ] ]

	// The top, right, bottom, and left five cells are set as either city,
	// road, or field. The center is then set as a proper representation.
	// ie.
	//
	// x C C C C C x
	// C C C C C F F
	// C C C F F F F
	// C C F F R R R
	// C C F R R F F
	// C F F R F F F
	// x F F R F F x

	// Shown above are the starting tile, a cloister attached to a road,
	// and a 2-sided city/castle with an L-bend road (respectively).
	// Representation in this way allows for easier scoring and tests for
	// connectedness.

	private TileType[][] tile;
	private String identifier = "";
	private int orientation = 0;

	/** Denotes the width & height of a tile (number of tileType's). **/
	public static final int tileSize = 7;

	private Tile() {
		// Create the tile array.
		tile = new TileType[tileSize][tileSize];

		// Set the corners.
		tile[0][0] = null;
		tile[0][tileSize - 1] = null;
		tile[tileSize - 1][0] = null;
		tile[tileSize - 1][tileSize - 1] = null;
	}

	/**
	 * Constructor
	 * 
	 * @param tile a character array describing the layout of land types.
	 * @param identifier an identifier to associate with the tile.
	 */
	public Tile(char[][] tile, String identifier) {
		this();

		if (tile.length != tileSize) {
			throw new IllegalArgumentException("Tile size must be 7x7.");
		}

		for (int i = 0; i < tile.length; i++) {
			if (tile[i].length != tileSize) {
				throw new IllegalArgumentException("Tile size must be 7x7.");
			}

			for (int j = 0; j < tile[i].length; j++) {
				this.tile[i][j] = charToTileType(tile[i][j]);
			}
		}

		this.identifier = identifier;
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

	// Accessor Methods
	public TileType[] getTop() {
		return tile[0];
	}

	public TileType[] getRight() {
		TileType[] right = new TileType[tileSize];

		for (int i = 0; i < tileSize; i++) {
			right[i] = tile[i][tileSize - 1];
		}

		return right;
	}

	public TileType[] getBottom() {
		return tile[tileSize - 1];
	}

	public TileType[] getLeft() {
		TileType[] left = new TileType[tileSize];

		for (int i = 0; i < tileSize; i++) {
			left[i] = tile[i][0];
		}

		return left;
	}

	public TileType getTileType(int x, int y) {
		return tile[y][x];
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getOrientation() {
		return orientation;
	}
}
