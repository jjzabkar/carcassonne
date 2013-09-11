package model;

// State layout:
//
// START_GAME
// START_TURN
// DRAW_TILE
// PLACE_TILE
// PLACE_MEEPLE
// END_TURN
// END_GAME
//
// Each state advances to the state below it, however there are some exceptions
// to how the actual play advances. These exceptions are outlined below:
//
// * gameplay loops through the inner states, beginning at START_TURN and
//   terminating at END_TURN.
// * during the PLACE_TILE state, the user can rotate the tile before placing it
// * for both the PLACE_TILE and PLACE_MEEPLE states, a score() action is
//   triggered after the user places the object
// * when in the PLACE_MEEPLE state, both actions to place a meeple, and to draw
//   a card are accepted. this allows the player to optionally end their turn
//   after placing their game tile and before placing a meeple on the board
// * in the END_TURN state, if there are no tiles left to place (and after the
//   current player places a meeple), play ends, with the final scoring occuring
//   followed by the end game status screen being displayed
//
// Several of the states may end up being placeholder states, if only to keep
// track of the game state explicitly.

public enum GameState {
	START_GAME, START_TURN, DRAW_TILE, PLACE_TILE, PLACE_MEEPLE, END_TURN, END_GAME
}
