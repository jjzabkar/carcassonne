package model;

import java.awt.Color;

import net.client.SocketClientProtocol;

class PlayerStruct {

	public String name;
	public Color color;
	public int score;

	PlayerStruct(String name, String color) {
		this.name = name;
		this.color = SocketClientProtocol.stringToColor(color);
	}

	PlayerStruct(String name, Color color, int score) {
		this.name = name;
		this.color = color;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}