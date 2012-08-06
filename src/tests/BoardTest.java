package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
	private Player player2;

	// Tiles
	private char[][] chr2;
	private char[][] k;
	private char[][] cr3;
	private char[][] c2a;

	@Before
	public void initialize() {

		// Test objects.
		chr2 = new char[][] { { 'x', 'C', 'C', 'C', 'C', 'C', 'x' },
				{ 'F', 'F', 'C', 'C', 'C', 'C', 'C' },
				{ 'F', 'F', 'F', 'F', 'C', 'C', 'C' },
				{ 'R', 'R', 'R', 'F', 'F', 'C', 'C' },
				{ 'F', 'F', 'R', 'R', 'F', 'C', 'C' },
				{ 'F', 'F', 'F', 'R', 'F', 'F', 'C' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		k = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
				{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
				{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
				{ 'F', 'F', 'K', 'K', 'K', 'F', 'F' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
				{ 'x', 'F', 'F', 'F', 'F', 'F', 'x' } };

		cr3 = new char[][] { { 'x', 'F', 'F', 'R', 'F', 'F', 'x' },
				{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
				{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
				{ 'C', 'F', 'F', 'F', 'R', 'R', 'R' },
				{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
				{ 'C', 'F', 'F', 'R', 'F', 'F', 'F' },
				{ 'x', 'F', 'F', 'R', 'F', 'F', 'x' } };

		c2a = new char[][] { { 'x', 'F', 'F', 'F', 'F', 'F', 'x' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
				{ 'F', 'F', 'F', 'F', 'F', 'F', 'C' },
				{ 'x', 'C', 'C', 'C', 'C', 'C', 'x' } };

		board = new Board();
		player = new Player();
		player2 = new Player();
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

		Player secondPlayer = new Player();
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

		Player secondPlayer = new Player();
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
	}

	@Test
	public void scoreRoadTest01() {
		// Test scoring of a simple road (during game).
		// Place a tile with a meeple on it, then place another tile completing
		// the road. Then call score().

		player.setCurrentTile(new Tile(cr3, "CR3"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 4, 4);
		assertEquals(0, err);

		err = board.placeMeeple(player, 4, 4, 5, 3);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(cr3, "CR3"));
		player.getCurrentTile().rotateClockwise();

		err = board.placeTile(player, 5, 4);
		assertEquals(0, err);

		board.scoreRoads(new Player[] { player }, false);
		assertEquals(2, player.getScore());
	}

	@Test
	public void scoreRoadTest02() {
		// Test scoring of a simple road (not finished) (end game).

		player.setCurrentTile(new Tile(cr3, "CR3"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 4, 4);
		assertEquals(0, err);

		err = board.placeMeeple(player, 4, 4, 5, 3);
		assertEquals(0, err);

		board.scoreRoads(new Player[] { player }, true);
		assertEquals(1, player.getScore());
	}

	@Test
	public void scoreCastleTest01() {
		// Test scoring of a simple castle (during game).
		// Place a tile with a meeple on it, then place another tile completing
		// the road. Then call score().

		player.setCurrentTile(new Tile(cr3, "CR3"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 4, 4);
		assertEquals(0, err);

		err = board.placeMeeple(player, 4, 4, 0, 1);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(cr3, "CR3"));
		player.getCurrentTile().rotateClockwise();
		player.getCurrentTile().rotateClockwise();

		err = board.placeTile(player, 3, 4);
		assertEquals(0, err);

		board.scoreCities(new Player[] { player }, false);
		assertEquals(4, player.getScore());
	}

	@Test
	public void scoreCastleTest02() {
		// Test scoring of a simple castle (not finished) (end game).

		player.setCurrentTile(new Tile(cr3, "CR3"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 4, 4);
		assertEquals(0, err);

		err = board.placeMeeple(player, 4, 4, 0, 1);
		assertEquals(0, err);

		board.scoreCities(new Player[] { player }, true);
		assertEquals(1, player.getScore());
	}

	@Test
	public void scoreCastleTest03() {
		// Test scoring of a simple castle.
		// One player starts the castle, another one finishes it as a
		// side-effect of tile placement.

		assertEquals(0, player.getScore());
		assertEquals(0, player2.getScore());

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 4, 4);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(chr2, "CHR2"));
		player.getCurrentTile().rotateClockwise();
		err = board.placeTile(player, 4, 3);
		assertEquals(0, err);

		err = board.placeMeeple(player, 4, 3, 4, 5);
		assertEquals(0, err);

		player2.setCurrentTile(new Tile(c2a, "C2A"));
		player2.getCurrentTile().rotateClockwise();
		err = board.placeTile(player2, 5, 3);
		assertEquals(0, err);

		err = board.placeMeeple(player2, 5, 3, 4, 6);
		assertEquals(0, err);

		board.scoreCities(new Player[] { player, player2 }, false);
		assertEquals(6, player.getScore());
		assertEquals(0, player2.getScore());
	}

	@Test
	public void scoreCloisterTest01() {
		// Test scoring of a finished cloister (during game).

		player.setCurrentTile(new Tile(k, "K"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 6, 6);
		assertEquals(0, err);

		err = board.placeMeeple(player, 6, 6, 3, 3);
		assertEquals(0, err);

		// Starting from north position, placing clockwise manner.
		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateClockwise();
		player.getCurrentTile().rotateClockwise();
		err = board.placeTile(player, 6, 5);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 7, 5);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		err = board.placeTile(player, 7, 6);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 7, 7);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateClockwise();
		err = board.placeTile(player, 6, 7);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 5, 7);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateClockwise();
		err = board.placeTile(player, 5, 6);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 5, 5);
		assertEquals(0, err);

		board.scoreCloisters(new Player[] { player }, false);
		assertEquals(9, player.getScore());
		// TODO meeple should also have been removed from the tile.
	}

	@Test
	public void scoreCloisterTest02() {
		// Test scoring of a cloister (not finished) (end game).

		player.setCurrentTile(new Tile(k, "K"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 6, 6);
		assertEquals(0, err);

		err = board.placeMeeple(player, 6, 6, 3, 3);
		assertEquals(0, err);

		// Starting from north position, placing clockwise manner.
		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateClockwise();
		player.getCurrentTile().rotateClockwise();
		err = board.placeTile(player, 6, 5);
		assertEquals(0, err);

		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 7, 5);
		assertEquals(0, err);

		board.scoreCloisters(new Player[] { player }, true);
		assertEquals(3, player.getScore());
	}

	@Test
	public void scoreFieldTest01() {
		// Test scoring of a field (not finished) (end game).

		player.setCurrentTile(new Tile(c2a, "C2A"));
		assertEquals(0, player.getScore());

		err = board.placeTile(player, 6, 6);
		assertEquals(0, err);

		err = board.placeMeeple(player, 6, 6, 3, 3);
		assertEquals(0, err);

		// Place a tile at north position.
		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateCounterClockwise();
		err = board.placeTile(player, 6, 5);
		assertEquals(0, err);

		// Place a tile at east position, finishing a castle.
		player.setCurrentTile(new Tile(c2a, "C2A"));
		player.getCurrentTile().rotateClockwise();
		err = board.placeTile(player, 7, 6);
		assertEquals(0, err);

		board.scoreFields(new Player[] { player });
		assertEquals(3, player.getScore());
	}

}
