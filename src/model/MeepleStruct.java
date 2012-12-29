package model;

public class MeepleStruct {

	private int xBoard;
	private int yBoard;
	private int xTile;
	private int yTile;

	public MeepleStruct(int xBoard, int yBoard, int xTile, int yTile) {

		this.xBoard = xBoard;
		this.yBoard = yBoard;
		this.xTile = xTile;
		this.yTile = yTile;
	}

	public int getxBoard() {
		return xBoard;
	}

	public void setxBoard(int xBoard) {
		this.xBoard = xBoard;
	}

	public int getyBoard() {
		return yBoard;
	}

	public void setyBoard(int yBoard) {
		this.yBoard = yBoard;
	}

	public int getxTile() {
		return xTile;
	}

	public void setxTile(int xTile) {
		this.xTile = xTile;
	}

	public int getyTile() {
		return yTile;
	}

	public void setyTile(int yTile) {
		this.yTile = yTile;
	}

}
