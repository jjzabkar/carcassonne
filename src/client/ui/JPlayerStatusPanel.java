package client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-10
 */
public class JPlayerStatusPanel extends JPanel {

	private static final long serialVersionUID = -8476267311237741566L;

	private JLabel nameLabel = new JLabel();
	private JLabel scoreLabel = new JLabel();
	private JColorPickerButton colorLabel;

	/**
	 * Create a new player status panel.
	 * 
	 * This panel is shown in the in-game screen & contains relevant information
	 * for the player that it is created for.
	 * 
	 * @param name
	 *            The player's name.
	 * @param score
	 *            The player's score.
	 * @param color
	 *            The player's color.
	 */
	public JPlayerStatusPanel(String name, int score, Color color) {
		super(new GridBagLayout());

		nameLabel.setText(name);
		scoreLabel.setText(Integer.toString(score));
		// TODO, new component to just display a color, we don't need a button
		colorLabel = new JColorPickerButton(new Color[] { color }, 20);

		layoutPanel();
	}

	/**
	 * Layout the panel. We have the name, score, and color which will be
	 * displayed in that order from left to right.
	 */
	private void layoutPanel() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2, 2, 2, 2);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 1;

		gc.weightx = 0.5;
		gc.anchor = GridBagConstraints.LINE_START;
		add(nameLabel, gc);

		gc.gridx++;
		gc.weightx = 0.25;
		gc.anchor = GridBagConstraints.CENTER;
		add(scoreLabel, gc);

		gc.gridx++;
		gc.weightx = 0.25;
		gc.anchor = GridBagConstraints.LINE_END;
		add(colorLabel, gc);
	}

	/**
	 * Set the score information held by this panel.
	 * 
	 * @param score
	 *            The new score to be displayed.
	 */
	public void setScore(int score) {
		scoreLabel.setText(Integer.toString(score));
	}

	/**
	 * Set whether this panel represents the current player.
	 * 
	 * @param currentPlayer
	 *            Whether this panel represents the current player.
	 */
	public void setCurrentPlayer(boolean currentPlayer) {

		if (currentPlayer) {
			nameLabel.setFont(getFont().deriveFont(Font.BOLD));
		} else {
			nameLabel.setFont(getFont().deriveFont(Font.PLAIN));
		}
	}
}
