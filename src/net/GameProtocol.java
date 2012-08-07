package net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import model.BoardPosition;
import model.Game;
import model.GameState;
import model.Meeple;
import model.Player;
import model.Tile;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class GameProtocol implements SocketProtocol {

	private ArrayList<String> parsedMessage = new ArrayList<String>();

	private Game game;
	private GameState gameState = GameState.GAME_START;
	private int numPlayers;
	private int currentPlayer = 0;

	public String processInput(String input) {

		String output = null;

		// First we have some actions which are able to be called at any point
		// during the game. These are requests for info about the game and any
		// player. Scoring is also allowed at different points throughout the
		// game.

		// This also allows us to manipulate the input info to reduce any
		// duplicated code.
		parsedMessage.clear();
		parsedMessage.addAll(Arrays.asList(input.split(";")));

		// Request for info; could be for game or player info.
		if (parsedMessage.get(0) == "INFO") {

			if (parsedMessage.get(1) == "game") {

				// Send back game information.
				int isDrawPileEmpty = game.isDrawPileEmpty() ? 1 : 0;
				output = "INFO;game;" + isDrawPileEmpty;

				return output;
			}

			if (parsedMessage.get(1) == "player") {

				// Get which player the client is inquiring about.
				int playerInt = Integer.parseInt(parsedMessage.get(2));

				// Send back player information.
				int isCurrentPlayer = (playerInt == currentPlayer) ? 1 : 0;
				int playerScore = game.getPlayers()[playerInt].getScore();

				Player playerPlayer = game.getPlayers()[playerInt];
				int numMeeplesPlaced = game.getNumMeeplesPlaced(playerPlayer);

				output = "INFO;player;" + playerInt + ";score;" + playerScore
						+ ";meeplesPlaced;" + numMeeplesPlaced
						+ ";currentPlayer;" + isCurrentPlayer;

				return output;
			}
		}

		// The client wants the server to recalculate scoring information.
		// Note that to get the updated scoring info the client must send us the
		// INFO message to get the score of each player.
		if (parsedMessage.get(0) == "SCORE") {

			if (parsedMessage.get(1) == "over") {

				int isGameOver = Integer.parseInt(parsedMessage.get(2));
				boolean gameOver = (isGameOver == 0) ? false : true;

				// If there are still tiles or the game isn't in an end state
				// then the game isn't over.. even if the client says it is.
				if ((!game.isDrawPileEmpty() || gameState != GameState.GAME_END)
						&& gameOver) {
					return SocketProtocol.NAK;
				}

				ArrayList<Meeple> removedMeeples = game.score(gameOver);

				HashMap<Meeple, BoardPosition> meeplePlacement;
				meeplePlacement = game.getMeeplePlacement();

				output = "SCORE;over;" + isGameOver;

				for (int i = 0; i < removedMeeples.size(); i++) {

					BoardPosition meeplePosition;
					meeplePosition = meeplePlacement.get(removedMeeples.get(i));

					if (meeplePosition != null) {
						output = output + ";meeple;xBoard;"
								+ meeplePosition.xBoard + ";yBoard;"
								+ meeplePosition.yBoard + ";xTile;"
								+ meeplePosition.xTile + ";yTile;"
								+ meeplePosition.yTile;
					}
				}

				return output;
			}
		}

		// If the game is just starting then we need to first ack that we got
		// the number of players sent. Then send over initialization info. The
		// gameboard width, height (# of tiles), the player whose turn it is,
		if (GameState.GAME_START == gameState) {

			if (parsedMessage.get(0) != "INIT") {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1) == "numPlayers") {
				numPlayers = Integer.parseInt(parsedMessage.get(2));
			}

			game = new Game(numPlayers);

			output = "INIT;gameBoardWidth;" + game.getBoardWidth()
					+ ";gameBoardHeight;" + game.getBoardHeight()
					+ ";currentPlayer;" + currentPlayer;

			gameState = GameState.DRAW_TILE;

			return output;

		}

		if (GameState.DRAW_TILE == gameState) {

			if (parsedMessage.get(0) != "DRAWTILE") {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1) == "currentPlayer") {

				// The client is telling us that a different player is taking
				// a tile than we told them. This is incorrect.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return SocketProtocol.NAK;
				}

				// Otherwise continue the game by drawing a tile for the current
				// player and letting the client know what the result was.
				Player player = game.getPlayers()[currentPlayer];
				game.drawTile(player);

				Tile tile = player.getCurrentTile();

				output = "DRAWTILE;id;" + tile.getIdentifier()
						+ ";orientation;" + tile.getOrientation();

				gameState = GameState.PLACE_TILE;

				return output;
			}
		}

		if (GameState.PLACE_TILE == gameState) {

			if (parsedMessage.get(0) != "PLACETILE") {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1) == "currentPlayer") {

				// Again, check the player the client is telling us that's
				// playing is actually the player whose turn it is.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return SocketProtocol.NAK;
				}

				// If not we continue on with the game; place the tile and
				// advance to the next game state.
				int xBoard = 0;
				int yBoard = 0;

				if (parsedMessage.get(3) == "xBoard") {
					xBoard = Integer.parseInt(parsedMessage.get(4));
				}
				if (parsedMessage.get(5) == "yBoard") {
					yBoard = Integer.parseInt(parsedMessage.get(6));
				}

				Player player = game.getPlayers()[currentPlayer];
				int err = game.placeTile(player, xBoard, yBoard);

				output = "PLACETILE;error;" + err;

				if (err == 0) {
					// TODO player has option of placing meeple
					gameState = GameState.PLACE_MEEPLE;
				}

				return output;
			}
		}

		if (GameState.PLACE_MEEPLE == gameState) {

			if (parsedMessage.get(0) != "PLACEMEEPLE") {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1) == "currentPlayer") {

				// Again, check the client is synchronized wrt/ player turn.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					output = SocketProtocol.NAK;
					return output;
				}

				// If everything is good; we're synchronized, continue.
				int xBoard = 0;
				int yBoard = 0;
				int xTile = 0;
				int yTile = 0;

				if (parsedMessage.get(3) == "xBoard") {
					xBoard = Integer.parseInt(parsedMessage.get(4));
				}
				if (parsedMessage.get(5) == "yBoard") {
					yBoard = Integer.parseInt(parsedMessage.get(6));
				}
				if (parsedMessage.get(7) == "xTile") {
					xTile = Integer.parseInt(parsedMessage.get(8));
				}
				if (parsedMessage.get(9) == "yTile") {
					yTile = Integer.parseInt(parsedMessage.get(10));
				}

				Player player = game.getPlayers()[currentPlayer];

				int err;
				err = game.placeMeeple(player, xBoard, yBoard, xTile, yTile);

				output = "PLACEMEEPLE;error;" + err;

				// Check when ending a turn if there are more tiles to be drawn
				// from the pile. If not, then the game is over.
				// TODO: player has option of placing meeple.
				if (err == 0) {
					if (game.isDrawPileEmpty()) {
						gameState = GameState.GAME_END;
						output = SocketProtocol.EXIT;
					} else {
						currentPlayer = (currentPlayer + 1) & numPlayers;
						gameState = GameState.DRAW_TILE;
					}
				}

				return output;
			}
		}

		return SocketProtocol.NAK;
	}
}