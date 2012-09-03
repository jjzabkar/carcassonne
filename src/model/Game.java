package model;

import java.util.ArrayList;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-08
 */
public class Game {
	private Board gameBoard = new Board();
	private DrawPile drawPile = new DrawPile();
	private Player[] players;

	public Game(int numPlayers) {
		// TODO input checking on number of players
		// Initialize the players.
		this.initPlayers(numPlayers);
	}

	/**
	 * Initialize the players.
	 * 
	 * @param numPlayers
	 *            The number of players which will be playing the game.
	 */
	private void initPlayers(int numPlayers) {

		this.players = new Player[numPlayers];

		for (int i = 0; i < numPlayers; i++) {
			this.players[i] = new Player();
		}
	}

	/**
	 * Allow a player to take a tile from the draw pile.
	 * 
	 * @param player
	 *            The player which receives the tile.
	 */
	public void drawTile(Player player) {
		drawPile.draw(player);

		// Re-draw if we get a tile which is not able to be placed.
		while (!gameBoard.canPlaceTile(player)) {
			drawPile.draw(player);
		}
	}

	/**
	 * Return whether there are tiles left in the draw pile.
	 * 
	 * @return A boolean indicating if the draw pile is empty.
	 */
	public boolean isDrawPileEmpty() {
		return drawPile.isEmpty();
	}

	/**
	 * Allow a player to place a tile on the game board.
	 * 
	 * @param player
	 *            The player which is placing the tile.
	 * @param xBoard
	 *            The x position on the game board to place the tile.
	 * @param yBoard
	 *            The y position on the game board to place the tile.
	 * 
	 * @return a non-zero integer if the tile could not be placed, zero
	 *         otherwise.
	 */
	public int placeTile(Player player, int xBoard, int yBoard) {
		return gameBoard.placeTile(player, xBoard, yBoard);
	}

	/**
	 * Allow a player to place a meeple on the game board.
	 * 
	 * @param player
	 *            The player which is placing the meeple.
	 * @param xBoard
	 *            The x position the meeple is to be placed on the board.
	 * @param yBoard
	 *            The y position the meeple is to be placed on the board.
	 * @param xTile
	 *            The x position on the tile to place the meeple.
	 * @param yTile
	 *            The y position on the tile to place the meeple.
	 * 
	 * @return a non-zero integer if the meeple could not be placed, zero
	 *         otherwise.
	 */
	public int placeMeeple(Player player, int xBoard, int yBoard, int xTile,
			int yTile) {
		return gameBoard.placeMeeple(player, xBoard, yBoard, xTile, yTile);
	}

	/**
	 * Return the number of meeples a player has placed.
	 * 
	 * @param player
	 *            The player to check for how many meeples they have placed.
	 * 
	 * @return the number of meeples which are in gameplay.
	 */
	public int getNumMeeplesPlaced(Player player) {
		return gameBoard.getNumMeeplesPlaced(player);
	}

	/**
	 * Score each player for all game features.
	 * 
	 * @param hasGameEnded
	 *            True if scoring at the end of the game, false if scoring
	 *            during the game.
	 * 
	 * @return an ArrayList of BoardPosition which represent meeples that have
	 *         been removed from the board.
	 */
	public ArrayList<BoardPosition> score(boolean hasGameEnded) {

		ArrayList<BoardPosition> meeples = new ArrayList<BoardPosition>();

		meeples.addAll(gameBoard.scoreCloisters(this.players, hasGameEnded));
		meeples.addAll(gameBoard.scoreRoads(this.players, hasGameEnded));
		meeples.addAll(gameBoard.scoreCities(this.players, hasGameEnded));

		if (hasGameEnded) {
			meeples.addAll(gameBoard.scoreFields(this.players));
		}

		return meeples;
	}

	public Player[] getPlayers() {
		return this.players;
	}

	public int getNumPlayers() {
		return this.players.length;
	}

	// Pass this off to the gameboard as it keeps track of positioning.
	public Meeple getMeeple(int xBoard, int yBoard, int xTile, int yTile) {
		return this.gameBoard.getMeeple(xBoard, yBoard, xTile, yTile);
	}

	// Pass off info about the board, used for ui to calculate the canvas size.
	public int getBoardWidth() {
		return gameBoard.getWidth();
	}

	public int getBoardHeight() {
		return gameBoard.getHeight();
	}
}
