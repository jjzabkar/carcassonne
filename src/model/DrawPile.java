package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-07-02
 */
public class DrawPile {

    private ArrayList<Tile> tiles = new ArrayList<Tile>();

    public DrawPile() {
        this.initDrawPile();
    }

    /**
     * Initialize the draw pile for a basic game. The starting tile is already
     * removed from the draw pile upon creation, and is automatically added to
     * the board during the {@link #initGameBoard()} method.
     */
    private void initDrawPile() {

        char[][] tile = new char[7][7];

        // See http://en.wikipedia.org/wiki/File:CarcassonneTiles.svg,
        // the tiles are created beginning at the top left, and going
        // through rows.

        // The tiles are declared in the orientation in which the back-side
        // lettering is upright.

        // Passed-in identifiers are used to tie the model representation to
        // a graphical representation. Along with this there is an orientation.

        // city half (5)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'C', 'C' },
                { 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
                { 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
                { 'F', 'F', 'F', 'C', 'C', 'C', 'C' },
                { 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
                { 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

        for (int i = 0; i < 5; i++) {
            this.tiles.add(new Tile(tile, "CH"));
        }

        // city road 3way (3)
        tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
                { 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'C', 'F', 'F', 'F', 'R', 'R', 'R' },
                { 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "CR3"));
        }

        // city2 opposite (3)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'C', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "C2O"));
        }

        // cloister (4)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
                { 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
                { 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 4; i++) {
            this.tiles.add(new Tile(tile, "K"));
        }

        // cloister road (2)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
                { 'R', 'R', 'K', 'K', 'K', 'F', 'F' },
                { 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 2; i++) {
            this.tiles.add(new Tile(tile, "KR"));
        }

        // road 4way (1)
        tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'R', 'R', 'R', 'x', 'R', 'R', 'R' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        this.tiles.add(new Tile(tile, "R4"));

        // road bend [2] (9)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'R', 'R', 'R', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        for (int i = 0; i < 9; i++) {
            this.tiles.add(new Tile(tile, "R2"));
        }

        // road (8)
        tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        for (int i = 0; i < 8; i++) {
            this.tiles.add(new Tile(tile, "R"));
        }

        // road 3way (4)
        tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'R', 'R', 'R', 'F', 'R', 'R', 'R' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 4; i++) {
            this.tiles.add(new Tile(tile, "R3"));
        }

        // city (1)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

        this.tiles.add(new Tile(tile, "C"));

        // city 3 (4)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 4; i++) {
            this.tiles.add(new Tile(tile, "C3"));
        }

        // city 3 road (3)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'C', 'C', 'C', 'C', 'C', 'C', 'C' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "C3R"));
        }

        // city 2 adjacent (2)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

        for (int i = 0; i < 2; i++) {
            this.tiles.add(new Tile(tile, "C2A"));
        }

        // city side (5)
        tile = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 5; i++) {
            this.tiles.add(new Tile(tile, "CS"));
        }

        // city road (5)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'R', 'R', 'R', 'R', 'R', 'R', 'R' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "CR"));
        }

        // city road bend [2] (3)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'R', 'R', 'R' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'F' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "CR2"));
        }

        // city road bend [2] mirrored (3)
        tile = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
                { 'R', 'R', 'R', 'R', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
                { 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "CR2M"));
        }

        // city 2 (3)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
                { 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
                { 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
                { 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
                { 'F', 'C', 'C', 'C', 'C', 'C', 'F' },
                { 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

        for (int i = 0; i < 3; i++) {
            this.tiles.add(new Tile(tile, "C2"));
        }

        // city half road bend [2] (5)
        tile = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                { 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
                { 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
                { 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
                { 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
                { 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
                { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        for (int i = 0; i < 5; i++) {
            this.tiles.add(new Tile(tile, "CHR2"));
        }

        // Finally shuffle the list.
        Collections.shuffle(this.tiles, new Random(System.nanoTime()));
    }

    /**
     * Allow a player to take a tile from the draw pile.
     * 
     * @param aPlayer The player which receives the tile.
     */
    public void draw(Player aPlayer) {
        aPlayer.setCurrentTile(this.tiles.remove(0));

        // For now, let's just re-shuffle the tiles after every draw.
        Collections.shuffle(this.tiles, new Random(System.nanoTime()));
    }

    /**
     * Return whether there are tiles left in the draw pile.
     * 
     * @return A boolean indicating if the draw pile is empty.
     */
    public boolean isEmpty() {
        return this.tiles.isEmpty();
    }

}