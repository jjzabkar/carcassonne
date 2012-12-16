package model;

import ui.GameUi;

public class Main {

	public static void main(String[] args) {

		// Create our game client & start it.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GameUi();
			}
		});
	}
}
