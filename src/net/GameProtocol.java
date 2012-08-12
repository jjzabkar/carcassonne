package net;

import java.util.ArrayList;
import java.util.Arrays;

import model.BoardPosition;
import model.Game;
import model.GameState;
import model.Player;
import model.Tile;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class GameProtocol implements SocketProtocol {

	// Message format is as follows (client sends followed by server replies):
	//
	// Note: Any messages sent at the wrong time or other errors will cause the
	// server to return SocketProtocol.NAK ("NAK").
	//
	//
	// INIT;numPlayers;<int>
	// INIT;currentPlayer;<int>;gameBoardWidth;<int>;gameBoardHeight;<int>
	//
	// DRAWTILE;currentPlayer;<int>
	// DRAWTILE;currentPlayer;<int>;identifier;<string>;orientation;<int:[0-3]>
	//
	// ROTATETILE;currentPlayer;<int>;direction;<string:(clockwise|counterClockwise)>
	// ROTATETILE;currentPlayer;<int>;direction;<string:(clockwise|counterClockwise)>;error;<int:(0|1)>
	//
	// PLACETILE;currentPlayer;<int>;xBoard;<int>;yBoard;<int>
	// PLACETILE;currentPlayer;<int>;xBoard;<int>;yBoard;<int>;error;<int:(0|1)>
	//
	// PLACEMEEPLE;currentPlayer;<int>;xBoard;<int>;yBoard;<int>;xTile;<int>;yTile;<int>
	// PLACEMEEPLE;currentPlayer;<int>;xBoard;<int>;yBoard;<int>;xTile;<int>;yTile;<int>;error;<int:(0|1)>
	//
	// SCORE;over;<int:(0|1)>
	// SCORE;over;<int:(0|1)>[;meeple;xBoard;<int>;yBoard;<int>;xTile;<int>;yTile;<int>]*
	//
	//
	// INFO;player;<int>;
	// INFO;player;<int>;currentPlayer;<int:(0|1)>;score;<int>;meeplesPlaced;<int>
	//
	// INFO;game;
	// INFO;game;currentPlayer;<int>;drawPileEmpty;<int:(0|1)>
	//

	// State layout:
	//
	// START_GAME
	// DRAW_TILE
	// PLACE_TILE
	// SCORE_PLAYERS --> END_TURN
	// PLACE_MEEPLE
	// SCORE_PLAYERS --> DRAW_TILE
	// END_GAME
	//
	// Each state advances to the state below it, or to the state pointed to on
	// the right. End turn state is just a placeholder state which changes the
	// game state to a proper state based on a few properties of our game. This
	// allows us to not over-complicate the message passing protocol.

	private ArrayList<String> parsedMessage = new ArrayList<String>();

	private Game game;
	private GameState gameState = GameState.START_GAME;
	private GameState lastMove;
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
		if (parsedMessage.get(0).equals("INFO")) {

			if (parsedMessage.get(1).equals("game")) {

				// Send back game information.
				int isDrawPileEmpty = game.isDrawPileEmpty() ? 1 : 0;

				output = "INFO;game" + ";currentPlayer;" + currentPlayer
						+ ";drawPileEmpty;" + isDrawPileEmpty;

				return output;
			}

			if (parsedMessage.get(1).equals("player")) {

				// Get which player the client is inquiring about.
				int playerInt = Integer.parseInt(parsedMessage.get(2));

				// Send back player information.
				int isCurrentPlayer = (playerInt == currentPlayer) ? 1 : 0;
				int playerScore = game.getPlayers()[playerInt].getScore();

				Player playerPlayer = game.getPlayers()[playerInt];
				int numMeeplesPlaced = game.getNumMeeplesPlaced(playerPlayer);

				output = "INFO" + ";player;" + playerInt + ";currentPlayer;"
						+ isCurrentPlayer + ";score;" + playerScore
						+ ";meeplesPlaced;" + numMeeplesPlaced;

				return output;
			}
		}

		// If the game is just starting then we need to first ack that we got
		// the number of players sent. Then send over initialization info. The
		// gameboard width, height (# of tiles), the player whose turn it is,
		if (GameState.START_GAME == gameState) {

			if (!parsedMessage.get(0).equals("INIT")) {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1).equals("numPlayers")) {
				numPlayers = Integer.parseInt(parsedMessage.get(2));
			}

			game = new Game(numPlayers);

			output = "INIT" + ";currentPlayer;" + currentPlayer
					+ ";gameBoardWidth;" + game.getBoardWidth()
					+ ";gameBoardHeight;" + game.getBoardHeight();

			gameState = GameState.DRAW_TILE;

			return output;

		}

		if (GameState.DRAW_TILE == gameState) {

			if (!parsedMessage.get(0).equals("DRAWTILE")) {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

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

				output = "DRAWTILE" + ";currentPlayer;" + currentPlayer
						+ ";identifier;" + tile.getIdentifier()
						+ ";orientation;" + tile.getOrientation();

				gameState = GameState.PLACE_TILE;

				return output;
			}
		}

		if (GameState.PLACE_TILE == gameState) {

			if (!parsedMessage.get(0).equals("PLACETILE")
					&& !parsedMessage.get(0).equals("ROTATETILE")) {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// Again, check the player the client is telling us that's
				// playing is actually the player whose turn it is.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return SocketProtocol.NAK;
				}

				// Check what the client wants us to do.
				if (parsedMessage.get(0).equals("PLACETILE")) {

					// If not we continue on with the game; place the tile and
					// advance to the next game state.
					int xBoard = 0;
					int yBoard = 0;

					if (parsedMessage.get(3).equals("xBoard")) {
						xBoard = Integer.parseInt(parsedMessage.get(4));
					}
					if (parsedMessage.get(5).equals("yBoard")) {
						yBoard = Integer.parseInt(parsedMessage.get(6));
					}

					Player player = game.getPlayers()[currentPlayer];
					int err = game.placeTile(player, xBoard, yBoard);

					output = "PLACETILE" + ";currentPlayer;" + currentPlayer
							+ ";xBoard;" + xBoard + ";yBoard;" + yBoard
							+ ";error;" + err;

					if (err == 0) {
						lastMove = gameState;
						gameState = GameState.SCORE_PLAYERS;
					}
				}

				if (parsedMessage.get(0).equals("ROTATETILE")) {

					int err = 0;
					String direction = "clockwise";

					if (parsedMessage.get(3).equals("direction")) {
						direction = parsedMessage.get(4);
					}

					Player player = game.getPlayers()[currentPlayer];

					if (direction.equals("clockwise")) {
						player.getCurrentTile().rotateClockwise();
					} else if (direction.equals("counterClockwise")) {
						player.getCurrentTile().rotateCounterClockwise();
					} else {
						err = 1;
					}

					output = "ROTATETILE" + ";currentPlayer;" + currentPlayer
							+ ";direction;" + direction + ";error;" + err;
				}

				return output;
			}
		}

		if (GameState.PLACE_MEEPLE == gameState) {

			if (!parsedMessage.get(0).equals("PLACEMEEPLE")) {
				return SocketProtocol.NAK;
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

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

				if (parsedMessage.get(3).equals("xBoard")) {
					xBoard = Integer.parseInt(parsedMessage.get(4));
				}
				if (parsedMessage.get(5).equals("yBoard")) {
					yBoard = Integer.parseInt(parsedMessage.get(6));
				}
				if (parsedMessage.get(7).equals("xTile")) {
					xTile = Integer.parseInt(parsedMessage.get(8));
				}
				if (parsedMessage.get(9).equals("yTile")) {
					yTile = Integer.parseInt(parsedMessage.get(10));
				}

				Player player = game.getPlayers()[currentPlayer];

				int err;
				err = game.placeMeeple(player, xBoard, yBoard, xTile, yTile);

				output = "PLACEMEEPLE" + ";currentPlayer;" + currentPlayer
						+ ";xBoard;" + xBoard + ";yBoard;" + yBoard + ";xTile;"
						+ xTile + ";yTile;" + yTile + ";error;" + err;

				if (err == 0) {
					lastMove = gameState;
					gameState = GameState.SCORE_PLAYERS;
				} else {
					gameState = lastMove;
				}

				return output;
			}
		}

		// The client wants the server to recalculate scoring information.
		// Note that to get the updated scoring info the client must send us the
		// INFO message to get the score of each player.
		if (GameState.SCORE_PLAYERS == gameState) {

			if (parsedMessage.get(0).equals("SCORE")) {

				if (parsedMessage.get(1).equals("over")) {

					int isGameOver = Integer.parseInt(parsedMessage.get(2));
					boolean gameOver = (isGameOver == 0) ? false : true;

					// If there are still tiles or the game isn't in an end
					// state then the game isn't over.. even if the client says
					// it is.
					if ((!game.isDrawPileEmpty() || gameState != GameState.END_GAME)
							&& gameOver) {
						return SocketProtocol.NAK;
					}

					ArrayList<BoardPosition> removedMeeples;
					removedMeeples = game.score(gameOver);

					output = "SCORE;over;" + isGameOver;

					for (int i = 0; i < removedMeeples.size(); i++) {

						BoardPosition meeplePosition = removedMeeples.get(i);

						if (meeplePosition != null) {
							output = output.concat(";meeple;xBoard;"
									+ meeplePosition.xBoard + ";yBoard;"
									+ meeplePosition.yBoard + ";xTile;"
									+ meeplePosition.xTile + ";yTile;"
									+ meeplePosition.yTile);
						}
					}

					// From here we can either advance to the end turn state, or
					// the place meeple state.
					// Assume an end turn state, but allow to change to meeple
					// placement if need be.
					gameState = GameState.END_TURN;

					return output;
				}
			}
		}

		// End turn game state. We arrive here after scoring takes place. So,
		// we've either just scored tile placement, or meeple placement. If
		// we've just scored tile placement, then allow the player to place a
		// meeple if they wish. Otherwise the game is either over, or the turn
		// is over (gameplay resumes with the next player in the draw tile
		// state).
		if (GameState.END_TURN == gameState) {

			if (parsedMessage.get(0).equals("PLACEMEEPLE")
					&& lastMove != GameState.PLACE_MEEPLE) {
				// If the player actually wants to place a meeple, let them.
				// Allow for a rollback of state if the meeple placement fails.
				lastMove = gameState;
				gameState = GameState.PLACE_MEEPLE;

			} else if (game.isDrawPileEmpty()) {

				gameState = GameState.END_GAME;
			} else {

				currentPlayer = (currentPlayer + 1) % numPlayers;
				gameState = GameState.DRAW_TILE;
			}

			return processInput(input);
		}

		// End game state.
		if (GameState.END_GAME == gameState) {
			return SocketProtocol.EXIT;
		}

		return SocketProtocol.NAK;
	}
}