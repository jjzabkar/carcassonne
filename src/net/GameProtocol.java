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
	// SCORE
	// SCORE[;meeple;xBoard;<int>;yBoard;<int>;xTile;<int>;yTile;<int>]*
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
	private int numPlayers;
	private int currentPlayer = 0;

	private String makeGameInfoMsg() {

		int isDrawPileEmpty = game.isDrawPileEmpty() ? 1 : 0;

		String output = "INFO;game" + ";currentPlayer;" + currentPlayer
				+ ";drawPileEmpty;" + isDrawPileEmpty;

		return output;
	}

	private String makePlayerInfoMsg(int player) {

		int isCurrentPlayer = (player == currentPlayer) ? 1 : 0;
		int playerScore = game.getPlayers()[player].getScore();

		Player playerModel = game.getPlayers()[player];
		int numMeeplesPlaced = game.getNumMeeplesPlaced(playerModel);

		String output = "INFO" + ";player;" + player + ";currentPlayer;"
				+ isCurrentPlayer + ";score;" + playerScore + ";meeplesPlaced;"
				+ numMeeplesPlaced;

		return output;
	}

	private String makeInitMsg(int player) {

		String output = "INIT" + ";currentPlayer;" + player
				+ ";gameBoardWidth;" + game.getBoardWidth()
				+ ";gameBoardHeight;" + game.getBoardHeight();

		return output;
	}

	private String makeDrawTileMsg(int player, String identifier,
			int orientation) {

		String output = "DRAWTILE" + ";currentPlayer;" + player
				+ ";identifier;" + identifier + ";orientation;" + orientation;

		return output;
	}

	private String makePlaceTileMsg(int player, int xBoard, int yBoard,
			int error) {

		String output = "PLACETILE" + ";currentPlayer;" + player + ";xBoard;"
				+ xBoard + ";yBoard;" + yBoard + ";error;" + error;

		return output;
	}

	private String makeRotateTileMsg(int player, String direction, int error) {

		String output = "ROTATETILE" + ";currentPlayer;" + player
				+ ";direction;" + direction + ";error;" + error;

		return output;
	}

	private String makePlaceMeepleMsg(int player, int xBoard, int yBoard,
			int xTile, int yTile, int error) {

		String output = "PLACEMEEPLE" + ";currentPlayer;" + player + ";xBoard;"
				+ xBoard + ";yBoard;" + yBoard + ";xTile;" + xTile + ";yTile;"
				+ yTile + ";error;" + error;

		return output;
	}

	private String makeScoreMsg(ArrayList<BoardPosition> removedMeeples) {

		String output = "SCORE";

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

		return output;
	}

	private ArrayList<String> makeArrayMsg(String... messages) {

		ArrayList<String> ret = new ArrayList<String>();

		for (int i = 0; i < messages.length; i++) {
			ret.add(messages[i]);
		}

		return ret;
	}

	public ArrayList<String> processInput(String input) {

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
				return makeArrayMsg(makeGameInfoMsg());
			}

			if (parsedMessage.get(1).equals("player")) {

				// Get which player the client is inquiring about.
				int player = Integer.parseInt(parsedMessage.get(2));

				// Send back player information.
				return makeArrayMsg(makePlayerInfoMsg(player));
			}
		}

		// If the game is just starting then we need to send over initialization
		// info. The gameboard width, height (# of tiles), the player whose turn
		// it is, &c.
		if (GameState.START_GAME == gameState) {

			if (!parsedMessage.get(0).equals("INIT")) {
				return makeArrayMsg(SocketProtocol.NAK);
			}

			if (parsedMessage.get(1).equals("numPlayers")) {
				numPlayers = Integer.parseInt(parsedMessage.get(2));
			}

			game = new Game(numPlayers);
			gameState = GameState.DRAW_TILE;

			return makeArrayMsg(makeInitMsg(currentPlayer));

		}

		if (GameState.DRAW_TILE == gameState) {

			if (!parsedMessage.get(0).equals("DRAWTILE")) {
				return makeArrayMsg(SocketProtocol.NAK);
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// The client is telling us that a different player is taking
				// a tile than we told them. This is incorrect.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArrayMsg(SocketProtocol.NAK);
				}

				// Otherwise continue the game by drawing a tile for the current
				// player and letting the client know what the result was.
				Player player = game.getPlayers()[currentPlayer];
				game.drawTile(player);
				gameState = GameState.PLACE_TILE;

				// Get variables to make the message & return it.
				Tile tile = player.getCurrentTile();
				String identifier = tile.getIdentifier();
				int orientation = tile.getOrientation();

				return makeArrayMsg(makeDrawTileMsg(currentPlayer, identifier,
						orientation));
			}
		}

		if (GameState.PLACE_TILE == gameState) {

			if (!parsedMessage.get(0).equals("PLACETILE")
					&& !parsedMessage.get(0).equals("ROTATETILE")) {
				return makeArrayMsg(SocketProtocol.NAK);
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// Again, check the player the client is telling us that's
				// playing is actually the player whose turn it is.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArrayMsg(SocketProtocol.NAK);
				}

				// Check what the client wants us to do.
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

					return makeArrayMsg(makeRotateTileMsg(currentPlayer,
							direction, err));
				}

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

					if (err == 0) {

						ArrayList<String> ret = new ArrayList<String>();

						ret.add(makePlaceTileMsg(currentPlayer, xBoard, yBoard,
								err));

						ret.add(makeScoreMsg(game.score(false)));

						for (int i = 0; i < game.getNumPlayers(); i++) {
							ret.add(makePlayerInfoMsg(i));
						}

						ret.add(makeGameInfoMsg());

						gameState = GameState.END_TURN;

						return ret;

					} else {
						return makeArrayMsg(SocketProtocol.NAK);
					}
				}
			}
		}

		if (GameState.END_TURN == gameState) {

			// The player decided to end their turn after placing a tile.
			if (parsedMessage.get(0).equals("DRAWTILE")) {

				currentPlayer = (currentPlayer + 1) % numPlayers;
				gameState = GameState.DRAW_TILE;
			}

			// Or they decided to place a meeple.
			if (parsedMessage.get(0).equals("PLACEMEEPLE")) {

				gameState = GameState.PLACE_MEEPLE;
			}

			return processInput(input);
		}

		if (GameState.PLACE_MEEPLE == gameState) {

			if (!parsedMessage.get(0).equals("PLACEMEEPLE")) {
				return makeArrayMsg(SocketProtocol.NAK);
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// Again, check the client is synchronized wrt/ player turn.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArrayMsg(SocketProtocol.NAK);
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

				if (err == 0) {

					boolean isGameOver = game.isDrawPileEmpty();

					if (isGameOver) {
						gameState = GameState.END_GAME;
					}

					ArrayList<String> ret = new ArrayList<String>();

					ret.add(makePlaceMeepleMsg(currentPlayer, xBoard, yBoard,
							xTile, yTile, err));

					ret.add(makeScoreMsg(game.score(isGameOver)));

					for (int i = 0; i < game.getNumPlayers(); i++) {
						ret.add(makePlayerInfoMsg(i));
					}

					ret.add(makeGameInfoMsg());

					gameState = GameState.DRAW_TILE;
					currentPlayer = (currentPlayer + 1) % numPlayers;

					return ret;

				} else {
					return makeArrayMsg(SocketProtocol.NAK);
				}

			}
		}

		// End game state.
		if (GameState.END_GAME == gameState) {

			return makeArrayMsg(SocketProtocol.EXIT);
		}

		return makeArrayMsg(SocketProtocol.NAK);
	}
}