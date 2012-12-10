package client.ui;

import java.awt.Color;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-09
 */
class PlayerUi {

	private String name;
	private Color color;
	private int score;

	public PlayerUi(String name, Color color, int score) {
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
