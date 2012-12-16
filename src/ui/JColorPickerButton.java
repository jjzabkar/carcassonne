package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;


class JColorPickerButton extends JComponent implements MouseListener,
		MessageSender {

	private static final long serialVersionUID = 5967617777706358673L;
	private ArrayList<Color> colors = new ArrayList<Color>();
	private int currentColor = 0;
	private Dimension dimension;
	private int displaySize;

	/**
	 * Creates a new JColorPickerButton, which is a rounded rectangle of width &
	 * height displaySize (square). The button displays the first color in the
	 * colors array when initialized. When clicked the button will cycle through
	 * the colors in the colors array one at a time.
	 * 
	 * @param colors
	 *            An array of colors to be displayed, and allow the user to
	 *            choose from.
	 * @param displaySize
	 *            The width & height of the button (it's a square!).
	 */
	public JColorPickerButton(Color[] colors, int displaySize) {
		super();

		enableInputMethods(true);
		addMouseListener(this);

		dimension = new Dimension(displaySize, displaySize);

		this.colors.addAll(Arrays.asList(colors));
		this.displaySize = displaySize;
	}

	/*
	 * This component is currently used inside a JPlayerSettingsPanel. When the
	 * color is changed, it triggers a player update message to be sent to the
	 * server via the JPlayerSettingsPanel.
	 */
	@Override
	public void sendMessage(String message) {

		if (getParent() == null) {
			return;
		}

		if (getParent() instanceof MessageSender) {
			((MessageSender) getParent()).sendMessage(message);
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		g.setColor(colors.get(currentColor));
		g.fillRoundRect(0, 0, displaySize, displaySize, displaySize / 4,
				displaySize / 4);

		g.setColor(Color.gray);
		g.drawRoundRect(0, 0, displaySize, displaySize, displaySize / 4,
				displaySize / 4);
	}

	@Override
	public Dimension getPreferredSize() {
		return dimension;
	}

	@Override
	public Dimension getMinimumSize() {
		return dimension;
	}

	@Override
	public Dimension getMaximumSize() {
		return dimension;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		currentColor = (currentColor + 1) % colors.size();
		sendMessage("UPDATEPLAYER");
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Gets the currently displayed color.
	 * 
	 * @return The currently displayed color.
	 */
	public Color getCurrentColor() {
		return colors.get(currentColor);
	}

	/**
	 * Sets the currently displayed color.
	 * 
	 * @param currentColor
	 *            The index of the color to set. If the index is larger than the
	 *            color array it will be wrapped (modulus operation) to a valid
	 *            index.
	 */
	public void setCurrentColor(int currentColor) {
		this.currentColor = (currentColor % colors.size());
		repaint();
	}
}
