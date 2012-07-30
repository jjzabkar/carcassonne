package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import model.Board;
import model.Meeple;
import model.Player;
import model.Tile;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {

	private int err = 0;
	private Board board;
	private Player player;
	private char[][] chr2;

	@Before
	public void initialize() {

		// Test object.
		chr2 = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		board = new Board();
		player = new Player(Color.BLACK);
	}

	@Test
	public void placeTileTest01() {
		// Place a tile somewhere on the board as the first tile.
		// We should be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));
		err = board.placeTile(player, 64, 42);

		assertEquals(0, err);
		assertEquals(null, player.getCurrentTile());
		assertEquals(64, player.getLastTilePlacedXPos());
		assertEquals(42, player.getLastTilePlacedYPos());

		// Place another tile somewhere on the board, not adjacent to the last
		// tile.
		// We should not be allowed to do this.
		// The tile should still be with the player as it has yet to be placed.
		// Additionally, the last placed positions should not have been updated.

		Tile aTile = new Tile(chr2, "CHR2");
		player.setCurrentTile(aTile);
		err = board.placeTile(player, 46, 79);

		assertEquals(1, err);
		assertEquals(aTile, player.getCurrentTile());
		assertEquals(64, player.getLastTilePlacedXPos());
		assertEquals(42, player.getLastTilePlacedYPos());
	}

	@Test
	public void placeTileTest02() {
		// Place a tile somewhere on the board as the first tile.
		// Then place a tile next to it with the sides not matching.
		// We should not be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));
		err = board.placeTile(player, 64, 42);

		assertEquals(0, err);

		Tile aTile = new Tile(chr2, "CHR2");
		player.setCurrentTile(aTile);
		err = board.placeTile(player, 64, 43);

		assertEquals(1, err);
		assertEquals(aTile, player.getCurrentTile());
		assertEquals(64, player.getLastTilePlacedXPos());
		assertEquals(42, player.getLastTilePlacedYPos());
	}

	@Test
	public void placeTileTest03() {
		// Place a tile somewhere on the board as the first tile.
		// Then place a tile next to it with the sides matching.
		// We should be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));
		err = board.placeTile(player, 64, 42);

		assertEquals(0, err);

		Tile aTile = new Tile(chr2, "CHR2");
		aTile.rotateClockwise();
		player.setCurrentTile(aTile);
		err = board.placeTile(player, 64, 43);

		assertEquals(0, err);
		assertEquals(null, player.getCurrentTile());
		assertEquals(64, player.getLastTilePlacedXPos());
		assertEquals(43, player.getLastTilePlacedYPos());
	}

	@Test
	public void placeMeepleTest01() {
		// Place a meeple on a tile which was just placed.
		// We should be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));
		err = board.placeTile(player, 64, 42);

		assertEquals(0, err);

		err = board.placeMeeple(player, 64, 42, 2, 3);

		assertEquals(0, err);
		// TODO check meeple is placed. see board placemeeple function
	}

	@Test
	public void placeMeepleTest02() {
		// Place a meeple on a tile which was not just placed.
		// We should not be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));
		err = board.placeTile(player, 64, 42);

		assertEquals(0, err);

		Tile secondTile = new Tile(chr2, "CHR2");
		secondTile.rotateClockwise();
		player.setCurrentTile(secondTile);
		err = board.placeTile(player, 64, 41);

		assertEquals(0, err);

		err = board.placeMeeple(player, 64, 42, 2, 3);

		assertEquals(1, err);
	}

	@Test
	public void placeMeepleTest03() {
		// Place a meeple on a tile which was just placed, on a feature which is
		// already taken by a different player
		// We should not be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));

		err = board.placeTile(player, 64, 42);
		assertEquals(0, err);

		err = board.placeMeeple(player, 64, 42, 0, 4);
		assertEquals(0, err);

		Player secondPlayer = new Player(Color.BLUE);
		Tile secondTile = new Tile(chr2, "CHR2");
		secondTile.rotateClockwise();

		secondPlayer.setCurrentTile(secondTile);

		err = board.placeTile(secondPlayer, 64, 43);
		assertEquals(0, err);

		err = board.placeMeeple(secondPlayer, 64, 43, 1, 0);
		assertEquals(1, err);
	}

	@Test
	public void placeMeepleTest04() {
		// Place a meeple on a tile which was just placed by another player.
		// We should not be allowed to do this.

		player.setCurrentTile(new Tile(chr2, "CHR2"));

		err = board.placeTile(player, 64, 42);
		assertEquals(0, err);

		Player secondPlayer = new Player(Color.BLUE);
		Tile secondTile = new Tile(chr2, "CHR2");
		secondTile.rotateClockwise();

		secondPlayer.setCurrentTile(secondTile);

		err = board.placeTile(secondPlayer, 64, 43);
		assertEquals(0, err);

		err = board.placeMeeple(secondPlayer, 64, 42, 1, 0);
		assertEquals(1, err);
	}

	@Test
	public void getMeepleTest() {
		// After placing a meeple attempt to get it by putting in the same
		// board coordinates.
		// The meeple should be returned.

		player.setCurrentTile(new Tile(chr2, "CHR2"));

		err = board.placeTile(player, 64, 42);
		assertEquals(0, err);

		err = board.placeMeeple(player, 64, 42, 0, 4);
		assertEquals(0, err);

		Meeple m = board.getMeeple(64, 42, 0, 4);

		assertNotNull(m);
		assertEquals(Color.BLACK.brighter().brighter().brighter(), m.getColor());
	}

	@Test
	public void scoreTest01() {
		// TODO
	}

}
