package net;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
	// JOINLOBBY
	//
	// LEAVELOBBY;player;<int>
	//
	// ASSIGNPLAYER;player;<int>
	//
	// UPDATELOBBY[;player;<int>;name;<string>;color;<string:(RGB)>]+
	//
	// UPDATEPLAYER;player;<int>;name;<string>;color;<string:(RGB)>
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

	// Pre-game variables (lobby).
	private ArrayList<PlayerStruct> lobbyPlayers = new ArrayList<PlayerStruct>();
	private ArrayList<Color> availablePlayerColors = new ArrayList<Color>() {

		private static final long serialVersionUID = -5941480684523828148L;

		{
			add(Color.black);
			add(Color.blue);
			add(Color.yellow);
			add(Color.red);
			add(Color.green);
		}
	};

	class PlayerStruct {

		PlayerStruct(int numberRep, String name, String color) {
			this.numberRep = numberRep;
			this.name = name;
			this.color = color;
		}

		private int numberRep;
		private String name;
		private String color;
	}

	// In-game variables.
	private Game game;
	private GameState gameState = GameState.START_GAME;
	private int currentPlayer = 0;

	// Pre-game messages.
	private String makeAssignPlayerMsg(int numberRep) {

		String output = SocketProtocol.replySender + ";ASSIGNPLAYER"
				+ ";player;" + numberRep;

		return output;
	}

	private String makeUpdateLobbyMsg() {

		String output = SocketProtocol.replyAll + ";UPDATELOBBY";

		for (int i = 0; i < lobbyPlayers.size(); i++) {

			int numberRep = lobbyPlayers.get(i).numberRep;
			String name = lobbyPlayers.get(i).name;
			String color = lobbyPlayers.get(i).color;

			output = output + ";player;" + numberRep + ";name;" + name
					+ ";color;" + color;
		}

		return output;
	}

	// In-game messages.
	private String makeGameInfoMsg() {

		int isDrawPileEmpty = game.isDrawPileEmpty() ? 1 : 0;

		String output = SocketProtocol.replyAll + ";INFO;game"
				+ ";currentPlayer;" + currentPlayer + ";drawPileEmpty;"
				+ isDrawPileEmpty;

		return output;
	}

	private String makePlayerInfoMsg(int player) {

		int isCurrentPlayer = (player == currentPlayer) ? 1 : 0;
		int playerScore = game.getPlayers().get(currentPlayer).getScore();

		Player playerModel = game.getPlayers().get(currentPlayer);
		int numMeeplesPlaced = game.getNumMeeplesPlaced(playerModel);

		String output = SocketProtocol.replyAll + ";INFO" + ";player;" + player
				+ ";currentPlayer;" + isCurrentPlayer + ";score;" + playerScore
				+ ";meeplesPlaced;" + numMeeplesPlaced;

		return output;
	}

	private String makeInitMsg(int player) {

		String output = SocketProtocol.replyAll + ";INIT" + ";currentPlayer;"
				+ player + ";gameBoardWidth;" + game.getBoardWidth()
				+ ";gameBoardHeight;" + game.getBoardHeight();

		return output;
	}

	private String makeDrawTileMsg(int player, String identifier,
			int orientation) {

		String output = SocketProtocol.replyAll + ";DRAWTILE"
				+ ";currentPlayer;" + player + ";identifier;" + identifier
				+ ";orientation;" + orientation;

		return output;
	}

	private String makePlaceTileMsg(int player, int xBoard, int yBoard,
			int error) {

		String output = SocketProtocol.replyAll + ";PLACETILE"
				+ ";currentPlayer;" + player + ";xBoard;" + xBoard + ";yBoard;"
				+ yBoard + ";error;" + error;

		return output;
	}

	private String makeRotateTileMsg(int player, String direction, int error) {

		String output = SocketProtocol.replyAll + ";ROTATETILE"
				+ ";currentPlayer;" + player + ";direction;" + direction
				+ ";error;" + error;

		return output;
	}

	private String makePlaceMeepleMsg(int player, int xBoard, int yBoard,
			int xTile, int yTile, int error) {

		String output = SocketProtocol.replyAll + ";PLACEMEEPLE"
				+ ";currentPlayer;" + player + ";xBoard;" + xBoard + ";yBoard;"
				+ yBoard + ";xTile;" + xTile + ";yTile;" + yTile + ";error;"
				+ error;

		return output;
	}

	private String makeScoreMsg(ArrayList<BoardPosition> removedMeeples) {

		String output = SocketProtocol.replyAll + ";SCORE";

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

	// Other messages.
	private final String errorMsg = SocketProtocol.replySender + ";"
			+ SocketProtocol.NAK;

	// Utility functions.
	/**
	 * Add a player to the list of players in the lobby.
	 * 
	 * @param numberRep
	 *            The number representation of the player (0-4).
	 */
	private void addPlayer(int numberRep) {

		Color color = availablePlayerColors.remove(0);
		String rgb = colorToString(color);

		PlayerStruct p;
		p = new PlayerStruct(numberRep, "Player " + numberRep, rgb);
		lobbyPlayers.add(p);
	}

	/**
	 * Remove a player from the list of the players in the lobby.
	 * 
	 * @param numberRep
	 *            The number representation of the player (0-4).
	 */
	private void removePlayer(int numberRep) {

		for (int i = 0; i < lobbyPlayers.size(); i++) {
			if (lobbyPlayers.get(i).numberRep == numberRep) {

				// Retrieve used color.
				String colorString = lobbyPlayers.get(i).color;
				Color color = stringToColor(colorString);
				availablePlayerColors.add(0, color);

				lobbyPlayers.remove(i);
			}
		}
	}

	/**
	 * Convert a Color to a String of length nine consisting of an RGB value.
	 * Each individual color value (R, G, B) is a string of length three,
	 * containing a value from "000" to "255".
	 * 
	 * @param color
	 *            A Color to be converted to a String representation.
	 * 
	 * @return A String representing the input Color.
	 */
	private String colorToString(Color color) {

		DecimalFormat df = new DecimalFormat("000");

		String r = df.format(color.getRed());
		String g = df.format(color.getGreen());
		String b = df.format(color.getBlue());
		String rgb = r + g + b;

		return rgb;
	}

	/**
	 * Convert a String of length nine to a Color. The string consists of an RGB
	 * value; with each being 3 characters each containing a value from "000" to
	 * "255".
	 * 
	 * @param string
	 *            The String to be converted to a Color.
	 * 
	 * @return A Color representing the input String.
	 */
	private Color stringToColor(String string) {

		int r = Integer.parseInt(string.substring(0, 3));
		int g = Integer.parseInt(string.substring(3, 6));
		int b = Integer.parseInt(string.substring(6, 9));

		return new Color(r, g, b);
	}

	/**
	 * Return the next free number representation for a player.
	 * 
	 * @return An integer representing the number (Id) for a new player.
	 */
	private int getFreePlayerSlot() {

		// Find which player slot is not used. To do this record all used slots,
		// sort them, and then run through until we find a slot (number) which
		// is not used.
		ArrayList<Integer> usedPlayerSlots = new ArrayList<Integer>();
		int candidateSlot = 0;

		for (int i = 0; i < lobbyPlayers.size(); i++) {
			usedPlayerSlots.add(lobbyPlayers.get(i).numberRep);
		}

		Collections.sort(usedPlayerSlots);

		for (int i = 0; i < usedPlayerSlots.size(); i++) {
			if (usedPlayerSlots.get(i).intValue() == candidateSlot) {
				candidateSlot++;
			}
		}

		return candidateSlot;
	}

	/**
	 * Given an array of messages, create an ArrayList of them.
	 * 
	 * @param messages
	 *            An array of messages (strings).
	 * 
	 * @return An ArrayList containing the input array in the same order.
	 */
	private ArrayList<String> makeArray(String... messages) {

		return new ArrayList<String>(Arrays.asList(messages));
	}

	/**
	 * Process input received from a game client/user. Depending on the game
	 * state, carry out an appropriate action, and return any relevant updates
	 * to the client(s)/user(s).
	 * 
	 * @param input
	 *            A string message which represents a game action (see message
	 *            format at the top of this file).
	 * 
	 * @return An ArrayList of string messages to return to the
	 *         client(s)/user(s).
	 */
	@Override
	public ArrayList<String> processInput(String input) {

		// First we have some actions which are able to be called at any point
		// during the game. These are requests for info about the game and any
		// player. Scoring is also allowed at different points throughout the
		// game.

		// This also allows us to manipulate the input info to reduce any
		// duplicated code.
		parsedMessage.clear();
		parsedMessage.addAll(Arrays.asList(input.split(";")));

		// Allow a player to exit the game (lobby).
		if (parsedMessage.get(0).equals(SocketProtocol.EXIT)) {

			String closeClient = SocketProtocol.replySender + ";"
					+ SocketProtocol.EXIT;

			return makeArray(closeClient);
		}

		if (parsedMessage.get(0).equals("JOINLOBBY")) {

			// Assign a player to the client which has joined the lobby.
			if (lobbyPlayers.size() >= 5) {
				return makeArray(errorMsg);
			}

			int playerSlot = getFreePlayerSlot();
			addPlayer(playerSlot);

			String assignPlayer = makeAssignPlayerMsg(playerSlot);

			// Send all the clients a message to update their lobbies.
			String updateLobby = makeUpdateLobbyMsg();

			return makeArray(assignPlayer, updateLobby);
		}

		if (parsedMessage.get(0).equals("UPDATEPLAYER")) {

			int numberRep = Integer.parseInt(parsedMessage.get(2));
			String name = parsedMessage.get(4);
			String color = parsedMessage.get(6);

			for (int i = 0; i < lobbyPlayers.size(); i++) {

				PlayerStruct lobbyPlayer = lobbyPlayers.get(i);

				if (lobbyPlayer.numberRep == numberRep) {
					lobbyPlayer.name = name;
					lobbyPlayer.color = color;
				}
			}

			String updateLobby = makeUpdateLobbyMsg();
			return makeArray(updateLobby);
		}

		if (parsedMessage.get(0).equals("LEAVELOBBY")) {
			// Free the player which left the lobby.
			int playerSlot = Integer.parseInt(parsedMessage.get(2));
			removePlayer(playerSlot);

			// Send all the clients a message to update their lobbies.
			String updateLobby = makeUpdateLobbyMsg();

			return makeArray(updateLobby);
		}

		// If the game is just starting then we need to send over initialization
		// info. The gameboard width, height (# of tiles), the player whose turn
		// it is, &c.
		if (GameState.START_GAME == gameState) {

			int numPlayers = 0;

			if (!parsedMessage.get(0).equals("INIT")) {
				return makeArray(errorMsg);
			}

			if (parsedMessage.get(1).equals("numPlayers")) {
				numPlayers = Integer.parseInt(parsedMessage.get(2));
			}

			game = new Game(numPlayers);
			gameState = GameState.DRAW_TILE;

			return makeArray(makeInitMsg(currentPlayer));
		}

		if (GameState.DRAW_TILE == gameState) {

			if (!parsedMessage.get(0).equals("DRAWTILE")) {
				return makeArray(errorMsg);
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// The client is telling us that a different player is taking
				// a tile than we told them. This is incorrect.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArray(errorMsg);
				}

				// Otherwise continue the game by drawing a tile for the current
				// player and letting the client know what the result was.
				Player player = game.getPlayers().get(currentPlayer);
				game.drawTile(player);
				gameState = GameState.PLACE_TILE;

				// Get variables to make the message & return it.
				Tile tile = player.getCurrentTile();
				String identifier = tile.getIdentifier();
				int orientation = tile.getOrientation();

				return makeArray(makeDrawTileMsg(currentPlayer, identifier,
						orientation));
			}
		}

		if (GameState.PLACE_TILE == gameState) {

			if (!parsedMessage.get(0).equals("PLACETILE")
					&& !parsedMessage.get(0).equals("ROTATETILE")) {
				return makeArray(errorMsg);
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// Again, check the player the client is telling us that's
				// playing is actually the player whose turn it is.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArray(errorMsg);
				}

				// Check what the client wants us to do.
				if (parsedMessage.get(0).equals("ROTATETILE")) {

					int err = 0;
					String direction = "clockwise";

					if (parsedMessage.get(3).equals("direction")) {
						direction = parsedMessage.get(4);
					}

					Player player = game.getPlayers().get(currentPlayer);

					if (direction.equals("clockwise")) {
						player.getCurrentTile().rotateClockwise();
					} else if (direction.equals("counterClockwise")) {
						player.getCurrentTile().rotateCounterClockwise();
					} else {
						err = 1;
					}

					return makeArray(makeRotateTileMsg(currentPlayer,
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

					Player player = game.getPlayers().get(currentPlayer);
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
						return makeArray(errorMsg);
					}
				}
			}
		}

		if (GameState.END_TURN == gameState) {

			if (parsedMessage.get(0).equals("ENDTURN")) {

				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArray(errorMsg);
				}

				return makeArray(SocketProtocol.replyAll
						+ ";ENDTURN;currentPlayer;" + parsedMessage.get(2));
			}

			// The player decided to end their turn after placing a tile.
			if (parsedMessage.get(0).equals("DRAWTILE")) {

				currentPlayer = (currentPlayer + 1) % game.getNumPlayers();
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
				return makeArray(errorMsg);
			}

			if (parsedMessage.get(1).equals("currentPlayer")) {

				// Again, check the client is synchronized wrt/ player turn.
				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return makeArray(errorMsg);
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

				Player player = game.getPlayers().get(currentPlayer);

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
					currentPlayer = (currentPlayer + 1) % game.getNumPlayers();

					return ret;

				} else {
					return makeArray(errorMsg);
				}
			}
		}

		// End game state.
		if (GameState.END_GAME == gameState) {
			String endGameMsg = SocketProtocol.replyAll + ";"
					+ SocketProtocol.EXIT;
			return makeArray(endGameMsg);
		}

		return makeArray(errorMsg);
	}
}