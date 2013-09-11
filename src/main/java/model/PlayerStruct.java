package model;

import java.awt.Color;

import net.client.SocketClientProtocol;

public class PlayerStruct {

	private String name;
	private Color color;
	private int score = 0;

	public PlayerStruct(String name, String color) {
		this.name = name;
		this.color = SocketClientProtocol.stringToColor(color);
	}

	public PlayerStruct(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public String getColorString() {
		return SocketClientProtocol.colorToString(color);
	}

	public void setColor(String color) {
		this.color = SocketClientProtocol.stringToColor(color);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}