package client.model;

import java.util.ArrayList;
import java.util.Arrays;

import client.net.SocketProtocol;
import client.ui.GameState;
import client.ui.GameUi;

// Adapter class which receives the returned messages from the server.
// The received messages are processed, followed by the client being told to
// update itself.
public class ClientProtocol implements SocketProtocol {

	private GameUi gameUi = null;

	public ClientProtocol(GameUi gameUi) {
		this.gameUi = gameUi;
	}

	// Variables to keep track of scoring process; after all the player's scores
	// are updated then we can end the current players turn if they don't have
	// any meeples left.
	private int numPlayerScoresUpdated = 0;
	private boolean currentPlayerHasMeeplesLeft = true;

	// TODO; current player checking, error checking.
	// Handle messages;
	@Override
	public String processInput(String input) {

		// Do some coercion on the data.
		ArrayList<String> message;
		message = new ArrayList<String>(Arrays.asList(input.split(";")));

		// PRE-GAME

		if (message.get(0).equals(SocketProtocol.EXIT)) {
			gameUi.exit();
		}

		if (message.get(0).equals("ASSIGNPLAYER")) {

			if (message.get(1).equals("player")) {
				gameUi.setPlayer(Integer.parseInt(message.get(2)));
			}
		}

		if (message.get(0).equals("UPDATELOBBY")) {
			gameUi.updateLobby(message);
		}

		// In-game messages.

		// INIT;currentPlayer;<int>;gameBoardWidth;<int>;gameBoardHeight;<int>
		if (message.get(0).equals("INIT")) {

			int currentPlayer = Integer.parseInt(message.get(2));
			int width = Integer.parseInt(message.get(4));
			int height = Integer.parseInt(message.get(6));

			gameUi.startGame(currentPlayer, width, height);
		}

		// DRAWTILE;currentPlayer;<int>;identifier;<string>;orientation;<int:[0-3]>
		if (message.get(0).equals("DRAWTILE")) {

			int currentPlayer = Integer.parseInt(message.get(2));
			String identifier = message.get(4);
			int orientation = Integer.parseInt(message.get(6));

			gameUi.drawTile(currentPlayer, identifier, orientation);
		}

		// ROTATETILE;currentPlayer;<int>;direction;<string:(clockwise|counterClockwise)>
		if (message.get(0).equals("ROTATETILE")) {

			int currentPlayer = Integer.parseInt(message.get(2));
			String direction = message.get(4);

			gameUi.rotateTile(currentPlayer, direction);
		}

		// Place tile
		if (message.get(0).equals("PLACETILE")
				&& gameUi.getGameState() == GameState.PLACE_TILE) {

			int err = 0;
			int xBoard = 0;
			int yBoard = 0;

			if (message.get(3).equals("xBoard")) {
				xBoard = Integer.parseInt(message.get(4));
			}
			if (message.get(5).equals("yBoard")) {
				yBoard = Integer.parseInt(message.get(6));
			}
			if (message.get(7).equals("error")) {
				err = Integer.parseInt(message.get(8));
			}

			// If no error draw the tile on the gameboard and remove it
			// from the currentTile area.
			if (err == 0) {

				gameUi.placeTile(xBoard, yBoard);
				gameUi.updateGameState(GameState.SCORE_PLACE_TILE);

			} else {
				// TODO: better error handling
				gameUi.showMessageDialog("Can't place tile there.");
			}
		}

		// Place meeple
		if (message.get(0).equals("PLACEMEEPLE")) {

			int err = 0;
			int xBoard = 0;
			int yBoard = 0;
			int xTile = 0;
			int yTile = 0;

			if (message.get(3).equals("xBoard")) {
				xBoard = Integer.parseInt(message.get(4));
			}
			if (message.get(5).equals("yBoard")) {
				yBoard = Integer.parseInt(message.get(6));
			}
			if (message.get(7).equals("xTile")) {
				xTile = Integer.parseInt(message.get(8));
			}
			if (message.get(9).equals("yTile")) {
				yTile = Integer.parseInt(message.get(10));
			}
			if (message.get(11).equals("error")) {
				err = Integer.parseInt(message.get(12));
			}

			if (err == 0) {

				gameUi.placeMeeple(xBoard, yBoard, xTile, yTile);
				gameUi.updateGameState(GameState.SCORE_PLACE_MEEPLE);

			} else {
				// TODO: better error handling
				gameUi.showMessageDialog("Can't place meeple there");
			}
		}

		if (gameUi.getGameState() == GameState.SCORE_PLACE_TILE
				|| gameUi.getGameState() == GameState.SCORE_PLACE_MEEPLE) {

			// Remove meeples.
			if (message.get(0).equals("SCORE")) {
				gameUi.scoreRemoveMeeples(message);
			}

			// Update player info.
			if (message.get(0).equals("INFO")
					&& message.get(1).equals("player")) {

				int player = 0;
				int playerScore = 0;
				int meeplesPlaced = 0;

				if (message.get(1).equals("player")) {
					player = Integer.parseInt(message.get(2));
				}
				if (message.get(5).equals("score")) {
					playerScore = Integer.parseInt(message.get(6));
				}
				if (message.get(7).equals("meeplesPlaced")) {
					meeplesPlaced = Integer.parseInt(message.get(8));
				}

				gameUi.getPlayerStatusPanels().get(player)
						.setScore(playerScore);

				// Each players info is sent after a scoring action;
				// after scoring all players, if the current player has no
				// meeples to place then end their turn.
				numPlayerScoresUpdated++;

				if (meeplesPlaced == 7 && gameUi.getPlayer() == player) {
					currentPlayerHasMeeplesLeft = false;
				}

				if (numPlayerScoresUpdated == gameUi.getNumPlayers()) {

					numPlayerScoresUpdated = 0;

					// Also end the player's turn if they are at the meeple
					// scoring state.
					if (gameUi.getGameState() == GameState.SCORE_PLACE_MEEPLE
							|| !currentPlayerHasMeeplesLeft) {

						currentPlayerHasMeeplesLeft = true;

						String msg = "ENDTURN;currentPlayer;" + player;
						gameUi.sendMessage(msg);

						gameUi.updateGameState(GameState.DRAW_TILE);
						gameUi.endTurn(gameUi.getCurrentPlayer());

					} else {

						gameUi.updateGameState(GameState.PLACE_MEEPLE);
						gameUi.getEndTurnButton().setEnabled(true);
					}
				}
			}
		}

		if (message.get(0).equals("ENDTURN")) {

			gameUi.updateGameState(GameState.DRAW_TILE);
			gameUi.endTurn(Integer.parseInt(message.get(2)));
		}

		// Info
		if (message.get(0).equals("INFO") && message.get(1).equals("game")) {

			boolean drawPileEmpty = false;

			if (message.get(4).equals("drawPileEmpty")) {

				int isDrawPileEmpty = Integer.parseInt(message.get(5));
				drawPileEmpty = (isDrawPileEmpty == 0) ? false : true;
			}

			if (drawPileEmpty) {

				gameUi.updateGameState(GameState.GAME_END);
			}
		}

		return null;
	}
}
