package model;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-08
 */
public class Tile {
    // A tile is stored as a 7x7 matrix, where the corner cells are null.
    // (Would be 5x5 but some tiles need more space.)
    // ie.
    //    x _ _ _ _ _ x    Stored: [ [ _ _ _ _ _ _ _ ], ] X: 3, 2
    //    _ _ _ _ _ _ _            | [ _ _ _ _ _ _ _ ], | p: y, x
    //    _ _ _ _ _ _ _            | [ _ _ _ _ _ _ _ ], |
    //    _ _ _ _ _ _ _            | [ _ _ X _ _ _ _ ], |
    //    _ _ _ _ _ _ _            | [ _ _ _ _ _ _ _ ], |
    //    _ _ _ _ _ _ _            | [ _ _ _ _ _ _ _ ], |
    //    x _ _ _ _ _ x            [ [ _ _ _ _ _ _ _ ]  ]

    // The top, right, bottom, and left three cells are set as either city,
    // road, or field. The center is then set as a proper representation.
    // ie.
    //    x F F R F F x      x F F F F F x      x C C C C C x
    //    C F F R F F F      F F F F F F F      C C C C C F F
    //    C F F R F F F      F F K K K F F      C C C F F F F
    //    C F F R F F F      F F K K K F F      C C F F R R R
    //    C F F R F F F      F F K K K F F      C C F R R F F
    //    C F F R F F F      F F F R F F F      C F F R F F F
    //    x F F R F F x      x F F R F F x      x F F R F F x

    // Shown above are the starting tile, a cloister attached to a road,
    // and a 2-sided city/castle with an L-bend road (respectively).
    // Representation in this way allows for easier scoring and tests for
    // connectedness.

    protected TileType[][] tile;
    private String identifier = "";
    private int orientation = 0;

    private Tile() {
        // Create the tile array.
        this.tile = new TileType[7][7];

        // Set the corners.
        this.tile[0][0] = null;
        this.tile[0][6] = null;
        this.tile[6][0] = null;
        this.tile[6][6] = null;
    }

    /**
     * Constructor
     * 
     * @param theTile
     * @param identifier
     */
    public Tile(char[][] theTile, String identifier) {
        this();

        if (theTile.length != 7) {
            throw new IllegalArgumentException("Tile size must be 7x7.");
        }

        for (int i = 0; i < theTile.length; i++) {
            if (theTile[i].length != 7) {
                throw new IllegalArgumentException("Tile size must be 7x7.");
            }

            for (int j = 0; j < theTile[i].length; j++) {
                this.tile[i][j] = charToTileType(theTile[i][j]);
            }
        }

        this.identifier = identifier;
    }

    /**
     * Constructor
     * 
     * @param theTile
     * @param identifier
     * @param orientation
     */
    public Tile(char[][] theTile, String identifier, int orientation) {
        this(theTile, identifier);
        this.orientation = orientation;
    }

    /**
     * Based on an input character, return the corresponding TileType.
     * 
     * @param letter Any of the letters k, c, r, i, f, x. (case insensitive)
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
     * Rotate the tile counter-clockwise by 90 degrees.
     */
    public void rotateCounterClockwise() {
        TileType[][] ret = new TileType[7][7];

        for (int i = 0; i < this.tile.length; i++) {
            for (int j = 0; j < this.tile[i].length; j++) {
                ret[i][j] = this.tile[j][7 - i - 1];
            }
        }

        this.tile = ret;
        this.orientation = (this.orientation - 1) % 4;
    }

    /**
     * Rotate the tile clockwise by 90 degrees.
     */
    public void rotateClockwise() {
        TileType[][] ret = new TileType[7][7];

        for (int i = 0; i < this.tile.length; i++) {
            for (int j = 0; j < this.tile[i].length; j++) {
                ret[i][j] = this.tile[7 - j - 1][i];
            }
        }

        this.tile = ret;
        this.orientation = (this.orientation + 1) % 4;
    }

    // Accessor Methods
    public TileType[] getTop() {
        return this.tile[0];
    }

    public TileType[] getRight() {
        TileType[] right = new TileType[7];

        for (int i = 0; i < 7; i++) {
            right[i] = this.tile[i][6];
        }

        return right;
    }

    public TileType[] getBottom() {
        return this.tile[6];
    }

    public TileType[] getLeft() {
        TileType[] left = new TileType[7];

        for (int i = 0; i < 7; i++) {
            left[i] = this.tile[i][0];
        }

        return left;
    }

    public TileType getTileType(int xPos, int yPos) {
        return this.tile[yPos][xPos];
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getOrientation() {
        return this.orientation;
    }

}
