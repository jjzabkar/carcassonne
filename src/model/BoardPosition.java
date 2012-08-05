package model;

/*
 * This class is used to keep track of a board position any time it is
 * needed. It is used in to keep track of meeple placement, and in the
 * scoring functions.
 */
/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class BoardPosition {

	public final int xBoard;
	public final int yBoard;
	public final int xTile;
	public final int yTile;

	public BoardPosition(int xBoard, int yBoard, int xTile, int yTile) {
		this.xBoard = xBoard;
		this.yBoard = yBoard;
		this.xTile = xTile;
		this.yTile = yTile;
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}

		if (other == this) {
			return true;
		}

		if (!(other instanceof BoardPosition)) {
			return false;
		}

		BoardPosition otherBoardPosition = (BoardPosition) other;

		if (this.xBoard == otherBoardPosition.xBoard
				&& this.yBoard == otherBoardPosition.yBoard
				&& this.xTile == otherBoardPosition.xTile
				&& this.yTile == otherBoardPosition.yTile) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		// Whenever we override equals we need to override hashCode.

		// Let's assume (this should always be correct anyway based on the
		// tile and board design) that xBoard & yBoard can be an integer
		// from 0-999, and that xTile & yTile can be an integer from 0-9.

		// So we'll just shift over the digits so the first three represent
		// xBoard, next three yBoard, and the next two xTile and yTile.

		int board = (this.xBoard * 1000) + this.yBoard;
		int tile = (this.xTile * 10) + this.yTile;

		return (board * 100) + tile;
	}
}