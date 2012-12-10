package client.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.JTextField;

import client.model.MessageSender;

public class JPlayerSettingsPanel extends JPanel implements MessageSender {

	private static final long serialVersionUID = 6618698655712246824L;

	private Color[] playerColors = { Color.black, Color.blue, Color.yellow,
			Color.red, Color.green };

	private int numberRep = 0;
	private JTextField playerName = new JTextField(20);

	private JColorPickerButton colorPickerButton = new JColorPickerButton(
			playerColors, 20);

	// TODO remove numberrep?
	public JPlayerSettingsPanel(int numberRep, String playerName) {
		super(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2, 2, 2, 2);
		gc.gridx = 0;
		gc.gridy = 0;
		add(this.playerName, gc);
		gc.gridx = 1;
		add(colorPickerButton, gc);

		this.numberRep = numberRep;
		this.playerName.setText(playerName);

		// Allow the user to update the player name when they press the enter
		// key on the player name text field.
		this.playerName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage("UPDATEPLAYER");
			}
		});
	}

	public JPlayerSettingsPanel(int numberRep, String playerName, Color color) {
		this(numberRep, playerName);

		for (int i = 0; i < playerColors.length; i++) {
			if (color.equals(playerColors[i])) {
				colorPickerButton.setCurrentColor(i);
			}
		}
	}

	/*
	 * This method is called either by the player name text field, or by the
	 * embedded color picker. Either way, they both pass in a message of
	 * "UPDATEPLAYER". When this occurs, the player's information is gathered
	 * and a real UPDATEPLAYER message is sent via the JFrame's sendMessage
	 * method - which forwards the message to the server for processing.
	 */
	@Override
	public void sendMessage(String message) {

		if (getParent() == null) {
			return;
		}

		if (message.equals("UPDATEPLAYER")) {

			Color color = getPlayerColor();
			String rgb = colorToString(color);

			message = "UPDATEPLAYER;player;" + numberRep + ";name;"
					+ getPlayerName() + ";color;" + rgb;
		}

		if (getTopLevelAncestor() instanceof MessageSender) {
			((MessageSender) getTopLevelAncestor()).sendMessage(message);
		}
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}

		if (other == this) {
			return true;
		}

		if (!(other instanceof JPlayerSettingsPanel)) {
			return false;
		}

		JPlayerSettingsPanel otherPSP = (JPlayerSettingsPanel) other;

		if (getPlayerName().equals(otherPSP.getPlayerName())
				&& getPlayerColor().equals(otherPSP.getPlayerColor())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {

		// TODO improve this hashcode?!
		int nameHash = getPlayerName().hashCode();
		int colorHash = getPlayerColor().hashCode();

		return nameHash ^ colorHash;
	}

	/**
	 * Convert a Color to a String of length nine consisting of an RGB value.
	 * Each individual color value (R, G, B) is a string of length three,
	 * containing a value from "000" to "255".
	 * 
	 * @param color
	 *            A Color to be converted to a String representation.
	 * 
	 * @return A String representing the input Color.
	 */
	private String colorToString(Color color) {

		DecimalFormat df = new DecimalFormat("000");

		String r = df.format(color.getRed());
		String g = df.format(color.getGreen());
		String b = df.format(color.getBlue());
		String rgb = r + g + b;

		return rgb;
	}

	public String getPlayerName() {
		return playerName.getText();
	}

	public Color getPlayerColor() {
		return colorPickerButton.getCurrentColor();
	}
}
