package model;

import java.awt.Color;

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
		Color[] colors = { Color.BLACK, Color.BLUE, Color.YELLOW, Color.RED,
				Color.GREEN };

		for (int i = 0; i < numPlayers; i++) {
			this.players[i] = new Player(colors[i]);
		}
	}

	/**
	 * Allow a player to take a tile from the draw pile.
	 * 
	 * @param aPlayer
	 *            The player which receives the tile.
	 */
	public void drawTile(Player aPlayer) {
		this.drawPile.draw(aPlayer);
	}

	/**
	 * Return whether there are tiles left in the draw pile.
	 * 
	 * @return A boolean indicating if the draw pile is empty.
	 */
	public boolean isDrawPileEmpty() {
		return this.drawPile.isEmpty();
	}

	/**
	 * Allow a player to place a tile on the game board.
	 * 
	 * @param aPlayer
	 *            The player which is placing the tile.
	 * @param xPos
	 *            The x position on the game board to place the tile.
	 * @param yPos
	 *            The y position on the game board to place the tile.
	 * @return a non-zero integer if the tile could not be placed, zero
	 *         otherwise.
	 */
	public int placeTile(Player aPlayer, int xPos, int yPos) {
		return this.gameBoard.placeTile(aPlayer, xPos, yPos);
	}

	/**
	 * Allow a player to place a meeple on the game board.
	 * 
	 * @param aPlayer
	 *            The player which is placing the meeple.
	 * @param xBoard
	 *            The x position the meeple is to be placed on the board.
	 * @param yBoard
	 *            The y position the meeple is to be placed on the board.
	 * @param xTile
	 *            The x position on the tile to place the meeple.
	 * @param yTile
	 *            The y position on the tile to place the meeple.
	 * @return a non-zero integer if the meeple could not be placed, zero
	 *         otherwise.
	 */
	public int placeMeeple(Player aPlayer, int xBoard, int yBoard, int xTile,
			int yTile) {
		return this.gameBoard
				.placeMeeple(aPlayer, xBoard, yBoard, xTile, yTile);
	}

	/**
	 * Score each player for all game features.
	 * 
	 * @param hasGameEnded
	 *            True if scoring at the end of the game, false if scoring
	 *            during the game.
	 */
	public void score(boolean hasGameEnded) {

		this.gameBoard.scoreCloisters(this.players, hasGameEnded);
		this.gameBoard.scoreRoads(this.players, hasGameEnded);
		this.gameBoard.scoreCities(this.players, hasGameEnded);

		if (hasGameEnded) {
			this.gameBoard.scoreFields(this.players);
		}

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

}
