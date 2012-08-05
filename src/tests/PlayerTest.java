package tests;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import model.Player;
import model.Tile;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void playerInitTest() {

		// Test object.
		Player player = new Player(Color.BLACK);

		// Oracle.
		Color oracleColor = Color.BLACK;
		int oracleScore = 0;
		Tile oracleTile = null;
		int oracleLastPlacedPos = -1;

		// Tests.
		assertEquals(oracleColor, player.getColor());
		assertEquals(oracleScore, player.getScore());
		assertEquals(oracleTile, player.getCurrentTile());
		assertEquals(oracleLastPlacedPos, player.getLastTilePlacedXPos());
		assertEquals(oracleLastPlacedPos, player.getLastTilePlacedYPos());
	}

	@Test
	public void playerTileTest() {

		// Test object.
		Player player = new Player(Color.BLACK);

		char[][] testTileArr = new char[][] {
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		Tile testTile = new Tile(testTileArr, "CHR2");

		player.setCurrentTile(testTile);
		player.setLastTilePlacedPosition(46, 35);

		// Oracle.
		Tile oracleTile = testTile;
		int oracleTileXPos = 46;
		int oracleTileYPos = 35;

		// Tests.
		assertEquals(oracleTile, player.getCurrentTile());
		assertEquals(oracleTileXPos, player.getLastTilePlacedXPos());
		assertEquals(oracleTileYPos, player.getLastTilePlacedYPos());
	}

}
