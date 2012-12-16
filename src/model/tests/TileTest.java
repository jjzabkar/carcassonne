package model.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import model.Tile;
import model.TileType;

import org.junit.Test;


public class TileTest {

	@Test
	public void rotateClockwiseTest() {

		// Test object.
		char[][] testTileArr = new char[][] {
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		Tile testTile = new Tile(testTileArr, "CHR2");
		testTile.rotateClockwise();

		// Oracle.
		char[][] oracleTileArr = new char[][] {
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

		Tile oracleTile = new Tile(oracleTileArr, "CHR2");
		int oracleOrientation = 1;

		// Tests.
		boolean theSame = true;

		for (int i = 0; i < Tile.tileSize; i++) {
			for (int j = 0; j < Tile.tileSize; j++) {
				if (testTile.getTileType(i, j) != oracleTile.getTileType(i, j)) {
					theSame = false;
				}
			}
		}

		assertTrue(theSame);
		assertEquals(oracleOrientation, testTile.getOrientation());
	}

	@Test
	public void rotateCounterClockwiseTest() {

		// Test object.
		char[][] testTileArr = new char[][] {
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		Tile testTile = new Tile(testTileArr, "CHR2");
		testTile.rotateCounterClockwise();

		// Oracle.
		char[][] oracleTileArr = new char[][] {
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'C', 'C', 'C', 'C', 'C', 'F', 'F' },
				{ 'C', 'C', 'C', 'F', 'F', 'F', 'F' },
				{ 'C', 'C', 'F', 'F', 'R', 'R', 'R' },
				{ 'C', 'C', 'F', 'R', 'R', 'F', 'F' },
				{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		Tile oracleTile = new Tile(oracleTileArr, "CHR2");
		int oracleOrientation = 3;

		// Tests.
		boolean theSame = true;

		for (int i = 0; i < Tile.tileSize; i++) {
			for (int j = 0; j < Tile.tileSize; j++) {
				if (testTile.getTileType(i, j) != oracleTile.getTileType(i, j)) {
					theSame = false;
				}
			}
		}

		assertTrue(theSame);
		assertEquals(oracleOrientation, testTile.getOrientation());
	}

	@Test
	// Cover the getTop, getBottom, getRight, getLeft methods.
	public void sideAccessorTests() {

		// Test object.
		char[][] testTileArr = new char[][] {
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		Tile testTile = new Tile(testTileArr, "CHR2");

		// Oracle.
		TileType[] oracleTop = new TileType[] { null, TileType.CITY,
				TileType.CITY, TileType.CITY, TileType.CITY, TileType.CITY,
				null };

		TileType[] oracleBottom = new TileType[] { null, TileType.FIELD,
				TileType.FIELD, TileType.ROAD, TileType.FIELD, TileType.FIELD,
				null };

		TileType[] oracleRight = oracleTop;
		TileType[] oracleLeft = oracleBottom;

		// Tests.
		assertArrayEquals(oracleTop, testTile.getTop());
		assertArrayEquals(oracleBottom, testTile.getBottom());
		assertArrayEquals(oracleRight, testTile.getRight());
		assertArrayEquals(oracleLeft, testTile.getLeft());

	}

	@Test
	// Cover the getTileType, getIdentifier, getOrientation methods.
	public void tileAccessorTests() {

		// Test object.
		char[][] testTileArr = new char[][] {
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		Tile testTile = new Tile(testTileArr, "CHR2");

		// Oracle.
		String oracleIdentifier = "CHR2";
		int oracleOrientation = 0;

		// Tests.
		assertEquals(TileType.FIELD, testTile.getTileType(4, 3));
		assertEquals(oracleIdentifier, testTile.getIdentifier());
		assertEquals(oracleOrientation, testTile.getOrientation());

	}

}
