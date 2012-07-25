package tests;

import static org.junit.Assert.assertTrue;
import model.Tile;

import org.junit.Test;

public class TileTest {

    @Test
    public void rotateClockwiseTest() {

        char[][] testTileArr =
            new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                    { 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
                    { 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
                    { 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
                    { 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
                    { 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
                    { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        char[][] oracleTileArr =
            new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
                    { 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
                    { 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
                    { 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
                    { 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
                    { 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
                    { 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

        Tile testTile = new Tile(testTileArr, "CHR2");
        Tile oracleTile = new Tile(oracleTileArr, "CHR2");
        testTile.rotateClockwise();

        boolean theSame = true;

        for (int i = 0; i < Tile.tileSize; i++) {
            for (int j = 0; j < Tile.tileSize; j++) {
                if (testTile.getTileType(i, j) != oracleTile.getTileType(i, j)) {
                    theSame = false;
                }
            }
        }

        assertTrue(theSame);
    }

    @Test
    public void rotateCounterClockwiseTest() {

        char[][] testTileArr =
            new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                    { 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
                    { 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
                    { 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
                    { 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
                    { 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
                    { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        char[][] oracleTileArr =
            new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
                    { 'C', 'C', 'C', 'C', 'C', 'F', 'F' },
                    { 'C', 'C', 'C', 'F', 'F', 'F', 'F' },
                    { 'C', 'C', 'F', 'F', 'R', 'R', 'R' },
                    { 'C', 'C', 'F', 'R', 'R', 'F', 'F' },
                    { 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
                    { 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

        Tile testTile = new Tile(testTileArr, "CHR2");
        Tile oracleTile = new Tile(oracleTileArr, "CHR2");
        testTile.rotateCounterClockwise();

        boolean theSame = true;

        for (int i = 0; i < Tile.tileSize; i++) {
            for (int j = 0; j < Tile.tileSize; j++) {
                if (testTile.getTileType(i, j) != oracleTile.getTileType(i, j)) {
                    theSame = false;
                }
            }
        }

        assertTrue(theSame);
    }
}
