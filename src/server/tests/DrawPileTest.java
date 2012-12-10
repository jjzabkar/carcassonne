package server.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import server.model.DrawPile;
import server.model.Player;
import server.model.Tile;

public class DrawPileTest {

	@Test
	public void drawPileInitTest() {

		// Test object.
		DrawPile drawPile = new DrawPile();

		// Tests.
		Player player = new Player();
		drawPile.draw(player);
		int numTiles = 1;

		Tile tile = player.getCurrentTile();

		assertEquals("CR", tile.getIdentifier());
		assertEquals(false, drawPile.isEmpty());

		while (!drawPile.isEmpty()) {
			drawPile.draw(player);
			numTiles++;
		}

		assertEquals(72, numTiles);
	}

}
