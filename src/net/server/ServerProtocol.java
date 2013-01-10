package net.server;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import model.BoardPosition;
import model.Game;
import model.GameState;
import model.Player;
import model.PlayerStruct;
import model.Tile;

public class ServerProtocol extends SocketServerProtocol {

	// Message format is as follows (client sends followed by server replies):
	//
	// Note: Any messages sent at the wrong time or other errors will cause the
	// server to return SocketClientProtocol.NAK ("NAK").
	//
	// JOINLOBBY
	//
	// LEAVELOBBY;player;<int>
	//
	// ASSIGNPLAYER;player;<int>
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
	// ROTATETILE;currentPlayer;<int>;direction;<string:(clockwise|counterClockwise)>
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
	// ENDTURN;currentPlayer;<int>
	// ENDTURN;currentPlayer;<int>
	//
	// INFO;player;<int>;
	// INFO;player;<int>;currentPlayer;<int:(0|1)>;score;<int>;meeplesPlaced;<int>
	//
	// INFO;game;
	// INFO;game;currentPlayer;<int>;drawPileEmpty;<int:(0|1)>
	//

	private HashMap<Socket, PrintWriter> writers = new HashMap<Socket, PrintWriter>();
	private ArrayList<String> parsedMessage = new ArrayList<String>();

	@Override
	public void addSender(Socket socket) {
		try {
			OutputStream outStream = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(outStream, true);

			writers.put(socket, writer);

		} catch (IOException e) {
			// Getting the output stream has
			// failed.
			// TODO
		}
	}

	@Override
	public void removeSender(Socket socket) {
		writers.remove(socket);
		// TODO: close socket/ writer connections
	}

	@Override
	public int getMaxConnections() {
		return Game.getMaxPlayers();
	}

	@Override
	public int getNumConnections() {
		return writers.size();
	}

	// Pre-game variables (lobby).
	private HashMap<Integer, PlayerStruct> lobbyPlayers = new HashMap<Integer, PlayerStruct>();

	private final Color[] colors = { Color.black, Color.blue, Color.green,
			Color.red, Color.yellow };
	private ArrayList<Color> availablePlayerColors = new ArrayList<Color>(
			Arrays.asList(colors));

	// In-game variables.
	private Game game;
	private GameState gameState = null;
	private int currentPlayer;

	// Pre-game messages.
	private String[] makeAssignPlayerMsg(int numberRep) {

		String message = "ASSIGNPLAYER;player;" + numberRep;
		String[] output = { SocketServerProtocol.replySender, message };

		return output;
	}

	private String[] makeUpdateLobbyMsg() {

		String message = "UPDATELOBBY";

		Iterator<Integer> playersIter = lobbyPlayers.keySet().iterator();

		while (playersIter.hasNext()) {

			Integer numberRep = playersIter.next();
			PlayerStruct player = lobbyPlayers.get(numberRep);

			message += ";player;" + numberRep + ";name;" + player.getName()
					+ ";color;" + player.getColorString();
		}

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	// In-game messages.
	private String[] makeGameInfoMsg() {

		int isDrawPileEmpty = game.isDrawPileEmpty() ? 1 : 0;

		String message = "INFO;game;currentPlayer;" + currentPlayer
				+ ";drawPileEmpty;" + isDrawPileEmpty;

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	private String[] makePlayerInfoMsg(int player) {

		Player playerModel = game.getPlayers().get(player);

		int isCurrentPlayer = (player == currentPlayer) ? 1 : 0;
		int playerScore = playerModel.getScore();
		int numMeeplesPlaced = game.getNumMeeplesPlaced(playerModel);

		String message = "INFO;player;" + player + ";currentPlayer;"
				+ isCurrentPlayer + ";score;" + playerScore + ";meeplesPlaced;"
				+ numMeeplesPlaced;

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	private String[] makeInitMsg(int player) {

		String message = "INIT;currentPlayer;" + player + ";gameBoardWidth;"
				+ game.getBoardWidth() + ";gameBoardHeight;"
				+ game.getBoardHeight();

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	private String[] makeDrawTileMsg(int player, String identifier,
			int orientation) {

		String message = "DRAWTILE;currentPlayer;" + player + ";identifier;"
				+ identifier + ";orientation;" + orientation;

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	// Note that if an error has occurred we will not modify the game at all,
	// and instead we'll just return the message to the sender indicating an
	// error.
	private String[] makePlaceTileMsg(int player, int xBoard, int yBoard,
			int error) {

		String message = "PLACETILE;currentPlayer;" + player + ";xBoard;"
				+ xBoard + ";yBoard;" + yBoard + ";error;" + error;

		String recipient = (error == 0) ? SocketServerProtocol.replyAll
				: SocketServerProtocol.replySender;

		String[] output = { recipient, message };

		return output;
	}

	private String[] makeRotateTileMsg(int player, String direction) {

		String message = "ROTATETILE;currentPlayer;" + player + ";direction;"
				+ direction;

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	private String[] makePlaceMeepleMsg(int player, int xBoard, int yBoard,
			int xTile, int yTile, int error) {

		String message = "PLACEMEEPLE;currentPlayer;" + player + ";xBoard;"
				+ xBoard + ";yBoard;" + yBoard + ";xTile;" + xTile + ";yTile;"
				+ yTile + ";error;" + error;

		String recipient = (error == 0) ? SocketServerProtocol.replyAll
				: SocketServerProtocol.replySender;

		String[] output = { recipient, message };

		return output;
	}

	private String[] makeScoreMsg(ArrayList<BoardPosition> removedMeeples) {

		String message = "SCORE";

		for (int i = 0; i < removedMeeples.size(); i++) {

			BoardPosition meeplePosition = removedMeeples.get(i);

			if (meeplePosition != null) {
				message = message.concat(";meeple;xBoard;"
						+ meeplePosition.xBoard + ";yBoard;"
						+ meeplePosition.yBoard + ";xTile;"
						+ meeplePosition.xTile + ";yTile;"
						+ meeplePosition.yTile);
			}
		}

		String[] output = { SocketServerProtocol.replyAll, message };

		return output;
	}

	private String[] makeErrorMsg() {

		String[] output = { SocketServerProtocol.replySender,
				SocketServerProtocol.NAK };
		return output;
	}

	private String[] makeEndTurnMsg(int currentPlayer) {

		String message = "ENDTURN;currentPlayer;" + currentPlayer;
		String[] output = { SocketServerProtocol.replyAll, message };
		return output;
	}

	private String[] makeEndGameMsg() {

		String[] output = { SocketServerProtocol.replyAll,
				SocketServerProtocol.EXIT };
		return output;
	}

	private String[] makeCloseClientMsg() {

		String[] output = { SocketServerProtocol.replySender,
				SocketServerProtocol.EXIT };
		return output;
	}

	// Utility functions.
	/**
	 * Add a player to the list of players in the lobby.
	 * 
	 * @param numberRep
	 *            The number representation of the player (0-4).
	 */
	private void addPlayer(int numberRep) {

		Color rgb = availablePlayerColors.remove(0);
		String name = "Player " + numberRep;
		lobbyPlayers.put(numberRep, new PlayerStruct(name, rgb));
	}

	/**
	 * Remove a player from the list of the players in the lobby.
	 * 
	 * @param numberRep
	 *            The number representation of the player (0-4).
	 */
	private void removePlayer(int numberRep) {

		PlayerStruct player = lobbyPlayers.get(numberRep);
		availablePlayerColors.add(0, player.getColor());
		lobbyPlayers.remove(numberRep);
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
		ArrayList<Integer> usedPlayerSlots;
		usedPlayerSlots = new ArrayList<Integer>(lobbyPlayers.keySet());
		Collections.sort(usedPlayerSlots);

		int candidateSlot = 0;

		for (int i = 0; i < usedPlayerSlots.size(); i++) {
			if (usedPlayerSlots.get(i).intValue() == candidateSlot) {
				candidateSlot++;
			}
		}

		return candidateSlot;
	}

	// Send the message(s) to relevant client(s).
	// The String arrays contained in processedMessages are two elements each.
	// The first element is the message recipient, and the second element is the
	// message itself.
	// TODO: I want to make this simpler. Perhaps move String[]... to
	// ArrayList<String>... to make it easier to split apart the recipient and
	// message.
	private ArrayList<String> disseminateMessages(Socket sender,
			String[]... processedMessages) {

		ArrayList<String> messages = new ArrayList<String>();

		for (int i = 0; i < processedMessages.length; i++) {

			String recipient = processedMessages[i][0];
			String currentMessage = processedMessages[i][1];
			messages.add(currentMessage);

			if (recipient.equals(SocketServerProtocol.replyAll)) {

				Iterator<Socket> sendersIter = writers.keySet().iterator();

				while (sendersIter.hasNext()) {

					Socket aSender = sendersIter.next();

					if (!aSender.equals(sender)) {
						writers.get(aSender).println(currentMessage);
					}
				}
			}
		}

		return messages;
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
	public ArrayList<String> processInput(Socket sender, String input) {

		// First we have some actions which are able to be called at any point
		// during the game. These are requests for info about the game and any
		// player. Scoring is also allowed at different points throughout the
		// game.

		// This also allows us to manipulate the input info to reduce any
		// duplicated code.
		parsedMessage.clear();
		parsedMessage.addAll(Arrays.asList(input.split(";")));

		// Allow a player to exit the game (lobby).
		if (parsedMessage.get(0).equals(SocketServerProtocol.EXIT)) {

			String[] closeClientMsg = makeCloseClientMsg();
			return disseminateMessages(sender, closeClientMsg);
		}

		if (parsedMessage.get(0).equals("JOINLOBBY")) {

			// Assign a player to the client which has joined the lobby.
			if (lobbyPlayers.size() >= Game.getMaxPlayers()) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			int playerSlot = getFreePlayerSlot();
			addPlayer(playerSlot);

			String[] assignPlayer = makeAssignPlayerMsg(playerSlot);

			// Send all the clients a message to update their lobbies.
			String[] updateLobby = makeUpdateLobbyMsg();

			return disseminateMessages(sender, assignPlayer, updateLobby);
		}

		if (parsedMessage.get(0).equals("UPDATEPLAYER")) {

			int numberRep = Integer.parseInt(parsedMessage.get(2));
			String name = parsedMessage.get(4);
			String color = parsedMessage.get(6);

			// Set the new values.
			PlayerStruct player = lobbyPlayers.get(numberRep);
			player.setName(name);
			player.setColor(color);

			String[] updateLobby = makeUpdateLobbyMsg();
			return disseminateMessages(sender, updateLobby);
		}

		if (parsedMessage.get(0).equals("LEAVELOBBY")) {
			// Free the player which left the lobby.
			int playerSlot = Integer.parseInt(parsedMessage.get(2));
			removePlayer(playerSlot);

			// Send all the clients a message to update their lobbies.
			String[] updateLobbyMsg = makeUpdateLobbyMsg();

			return disseminateMessages(sender, updateLobbyMsg);
		}

		// If the game is just starting then we need to send over initialization
		// info. The gameboard width, height (# of tiles), the player whose turn
		// it is, &c.
		if (parsedMessage.get(0).equals("INIT")) {

			if (!parsedMessage.get(1).equals("numPlayers")) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			gameState = GameState.START_GAME;
			int numPlayers = Integer.parseInt(parsedMessage.get(2));

			// TODO .. what?
			game = new Game(numPlayers);
			gameState = GameState.DRAW_TILE;

			String[] initMsg = makeInitMsg(currentPlayer);
			return disseminateMessages(sender, initMsg);
		}

		if (parsedMessage.get(0).equals("DRAWTILE")) {

			if (GameState.DRAW_TILE != gameState) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			if (!parsedMessage.get(1).equals("currentPlayer")) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
				return disseminateMessages(sender, makeErrorMsg());
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

			String[] drawTileMsg = makeDrawTileMsg(currentPlayer, identifier,
					orientation);
			return disseminateMessages(sender, drawTileMsg);
		}

		if (GameState.PLACE_TILE == gameState) {

			if (!parsedMessage.get(0).equals("PLACETILE")
					&& !parsedMessage.get(0).equals("ROTATETILE")) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			if (!parsedMessage.get(1).equals("currentPlayer")) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
				return disseminateMessages(sender, makeErrorMsg());
			}

			// Check what the client wants us to do.
			if (parsedMessage.get(0).equals("ROTATETILE")) {

				if (!parsedMessage.get(3).equals("direction")) {
					return disseminateMessages(sender, makeErrorMsg());
				}

				String direction = parsedMessage.get(4);
				Player player = game.getPlayers().get(currentPlayer);

				if (direction.equals("clockwise")) {
					player.getCurrentTile().rotateClockwise();
				}

				if (direction.equals("counterClockwise")) {
					player.getCurrentTile().rotateCounterClockwise();
				}

				String[] rotateTileMsg = makeRotateTileMsg(currentPlayer,
						direction);
				return disseminateMessages(sender, rotateTileMsg);
			}

			if (parsedMessage.get(0).equals("PLACETILE")) {

				if (!parsedMessage.get(3).equals("xBoard")
						|| !parsedMessage.get(5).equals("yBoard")) {
					return disseminateMessages(sender, makeErrorMsg());
				}

				int xBoard = Integer.parseInt(parsedMessage.get(4));
				int yBoard = Integer.parseInt(parsedMessage.get(6));

				Player player = game.getPlayers().get(currentPlayer);
				int error = game.placeTile(player, xBoard, yBoard);

				String[][] ret;
				String[] placeTileMsg = makePlaceTileMsg(currentPlayer, xBoard,
						yBoard, error);

				// Advance play if we don't encounter any errors.
				if (error == 0) {

					gameState = GameState.PLACE_MEEPLE;
					ret = addGameUpdateInfo(placeTileMsg);

				} else {

					ret = new String[1][];
					ret[0] = placeTileMsg;
				}

				return disseminateMessages(sender, ret);
			}
		}

		// Receive:
		// PLACEMEEPLE;currentPlayer;<int>;xBoard;<int>;yBoard;<int>;xTile;<int>;yTile;<int>
		if (GameState.PLACE_MEEPLE == gameState) {

			if (parsedMessage.get(0).equals("ENDTURN")) {

				if (!parsedMessage.get(1).equals("currentPlayer")) {
					return disseminateMessages(sender, makeErrorMsg());
				}

				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return disseminateMessages(sender, makeErrorMsg());
				}

				String[] endTurnMsg = makeEndTurnMsg(currentPlayer);

				currentPlayer = (currentPlayer + 1) % game.getNumPlayers();
				gameState = GameState.DRAW_TILE;

				String ret[][] = addGameUpdateInfo(endTurnMsg);

				return disseminateMessages(sender, ret);
			}

			if (parsedMessage.get(0).equals("PLACEMEEPLE")) {

				if (!parsedMessage.get(1).equals("currentPlayer")) {
					return disseminateMessages(sender, makeErrorMsg());
				}

				if (Integer.parseInt(parsedMessage.get(2)) != currentPlayer) {
					return disseminateMessages(sender, makeErrorMsg());
				}

				// If everything is good; we're synchronized, continue.
				int xBoard = Integer.parseInt(parsedMessage.get(4));
				int yBoard = Integer.parseInt(parsedMessage.get(6));
				int xTile = Integer.parseInt(parsedMessage.get(8));
				int yTile = Integer.parseInt(parsedMessage.get(10));

				Player player = game.getPlayers().get(currentPlayer);

				int e = game.placeMeeple(player, xBoard, yBoard, xTile, yTile);

				String ret[][];
				String[] placeMeepleMsg = makePlaceMeepleMsg(currentPlayer,
						xBoard, yBoard, xTile, yTile, e);

				// With no errors, we advance gameplay, along with adding more
				// messages to be returned to the clients to update them.
				if (e == 0) {

					ret = addGameUpdateInfo(placeMeepleMsg);

				} else {

					ret = new String[1][];
					ret[0] = placeMeepleMsg;
				}

				return disseminateMessages(sender, ret);
			}
		}

		// End game state.
		if (GameState.END_GAME == gameState) {
			return disseminateMessages(sender, makeEndGameMsg());
		}

		return disseminateMessages(sender, makeErrorMsg());
	}

	// Add game update info messages to the passed in message.
	// This function will determine if the game is over (needed to determine
	// scoring rules). While doing this it will create a message list which
	// begins with the passed-in message, and is followed by the score message,
	// player information messages, and a game information message.
	private String[][] addGameUpdateInfo(String[] message) {

		// We'll have a player information message for each player, along with
		// a score message, game info message, and the passed-in message.
		int numMessages = game.getNumPlayers() + 3;
		String ret[][] = new String[numMessages][];

		boolean isGameOver = false;

		// This method can be called during many game states; we don't want to
		// end the game unless we are in the last stage of play with no turns
		// left (no tiles left to draw).
		if (game.isDrawPileEmpty() && GameState.PLACE_MEEPLE == gameState) {
			gameState = GameState.END_GAME;
			isGameOver = true;
		}

		ret[0] = message;
		ret[1] = makeScoreMsg(game.score(isGameOver));

		for (int i = 0; i < game.getNumPlayers(); i++) {
			ret[i + 2] = makePlayerInfoMsg(i);
		}

		ret[numMessages - 1] = makeGameInfoMsg();

		return ret;
	}
}
