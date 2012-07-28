package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Game;
import model.Meeple;
import model.Player;
import model.Tile;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-28
 */
public class GameUI extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private Game game = null;
	private Boolean gameStarted = false;

	// Each menu screen (and the game screen) is contained within their own
	// JPanel's, which are set as the frame content pane when appropriate.

	// UI settings.
	private String title = "Carcassonne";
	private Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");

	private String[] windowedResolutions = { "800 x 600", "1024 x 768",
			"1280 x 720", "1280 x 960", "1280 x 1024", "1360 x 768",
			"1600 x 900", "1680 x 1050", "1920 x 1080" };

	private String[] windowedSettings = { "Fullscreen", "Windowed",
			"Borderless Window" };

	private String currentWindowedMode = "Windowed";
	private Dimension currentWindowedResolution = new Dimension(800, 600);
	private int currentVolume = 25;
	private boolean currentSoundEnabled = false;

	// Main JPanels which are to be swapped as the frame content.
	private JPanel titleScreenContentPane;
	private JPanel optionsScreenContentPane;
	private JPanel gameContentPane;

	// And other ui elements which need to be declared globally.
	private JComboBox windowedResolutionDropDown;
	private JLabel volumeSliderLabel;
	private JCanvas gameBoardWindow;
	private JCanvas currentTilePanel;

	public GameUI() {
		// Initialize game menus.
		this.initTitleScreen();
		this.initOptionsScreen();
		this.initGameScreen();

		// Set the title bar and icon.
		this.setTitle(title);
		this.setIconImage(icon);

		// Show the title screen to begin with.
		this.setContentPane(this.titleScreenContentPane);

		// Other frame/program settings.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setPreferredSize(this.currentWindowedResolution);
		this.pack();
		this.setVisible(true);

		// TODO: begin to play game music if enabled.

		// Set the frame to center on-screen.
		this.setLocationRelativeTo(null);
	}

	// State machine for menus
	//
	// title -> quit <------------------|
	// |<---> Single Player --------->|
	// |<---> Multiplayer ----------->| | video (resolution, windowed)
	// |<---> Multiplayer (Online) -->| | sound (%)
	// |<---> options <---------------->| music (on/off)

	private void initTitleScreen() {
		this.titleScreenContentPane = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Carcassonne");
		titleLabel.setVerticalTextPosition(JLabel.BOTTOM);
		titleLabel.setHorizontalTextPosition(JLabel.CENTER);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);

		JButton singleplayerButton = new JButton("Singleplayer");
		singleplayerButton.setVerticalTextPosition(AbstractButton.CENTER);
		singleplayerButton.setHorizontalTextPosition(AbstractButton.CENTER);
		singleplayerButton.setMnemonic(KeyEvent.VK_S);
		singleplayerButton.setActionCommand("startSingleplayerGame");
		singleplayerButton.addActionListener(this);

		JButton multiplayerButton = new JButton("Multiplayer");
		multiplayerButton.setVerticalTextPosition(AbstractButton.CENTER);
		multiplayerButton.setHorizontalTextPosition(AbstractButton.CENTER);
		multiplayerButton.setMnemonic(KeyEvent.VK_M);
		multiplayerButton.setActionCommand("startMultiplayerGame");
		multiplayerButton.addActionListener(this);

		// TODO
		multiplayerButton.setEnabled(false);

		JButton optionsButton = new JButton("Options");
		optionsButton.setVerticalTextPosition(AbstractButton.CENTER);
		optionsButton.setHorizontalTextPosition(AbstractButton.CENTER);
		optionsButton.setMnemonic(KeyEvent.VK_O);
		optionsButton.setActionCommand("showOptionsScreen");
		optionsButton.addActionListener(this);

		JButton exitButton = new JButton("Quit Game");
		exitButton.setVerticalTextPosition(AbstractButton.CENTER);
		exitButton.setHorizontalTextPosition(AbstractButton.CENTER);
		exitButton.setMnemonic(KeyEvent.VK_Q);
		exitButton.setActionCommand("exitGame");
		exitButton.addActionListener(this);

		// After creating the components, place them on the title screen.
		JPanel titleContainer = new JPanel(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2, 2, 2, 2);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;

		titleContainer.add(singleplayerButton, gc);

		gc.gridy = 1;

		titleContainer.add(multiplayerButton, gc);

		gc.gridy = 2;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(2, 2, 20, 2);

		titleContainer.add(optionsButton, gc);

		gc.gridx = 1;

		titleContainer.add(exitButton, gc);

		this.titleScreenContentPane.add(titleLabel, BorderLayout.PAGE_START);
		this.titleScreenContentPane.add(titleContainer, BorderLayout.PAGE_END);
	}

	private void initOptionsScreen() {
		this.optionsScreenContentPane = new JPanel(new BorderLayout());

		JLabel optionsLabel = new JLabel("Options");
		optionsLabel.setVerticalTextPosition(JLabel.BOTTOM);
		optionsLabel.setHorizontalTextPosition(JLabel.CENTER);
		optionsLabel.setHorizontalAlignment(JLabel.CENTER);

		JLabel videoSettingsLabel = new JLabel("Video");
		videoSettingsLabel.setVerticalTextPosition(JLabel.BOTTOM);
		videoSettingsLabel.setHorizontalTextPosition(JLabel.CENTER);
		videoSettingsLabel.setHorizontalAlignment(JLabel.CENTER);

		JLabel audioSettingsLabel = new JLabel("Sound");
		audioSettingsLabel.setVerticalTextPosition(JLabel.BOTTOM);
		audioSettingsLabel.setHorizontalTextPosition(JLabel.CENTER);
		audioSettingsLabel.setHorizontalAlignment(JLabel.CENTER);

		windowedResolutionDropDown = new JComboBox(this.windowedResolutions);

		windowedResolutionDropDown.setSelectedItem("800 x 600");
		windowedResolutionDropDown.setActionCommand("changeResolution");
		windowedResolutionDropDown.addActionListener(this);

		JComboBox windowedModeDropDown = new JComboBox(this.windowedSettings);

		windowedModeDropDown.setSelectedItem("Windowed");
		windowedModeDropDown.setActionCommand("changeWindowedMode");
		windowedModeDropDown.addActionListener(this);

		// TODO MUSIC
		JPanel volumeContainer = new JPanel(new BorderLayout());
		volumeContainer.setPreferredSize(new Dimension(240, 20));

		volumeSliderLabel = new JLabel(this.currentVolume + "%");
		volumeSliderLabel.setVerticalTextPosition(JLabel.BOTTOM);
		volumeSliderLabel.setHorizontalTextPosition(JLabel.CENTER);
		volumeSliderLabel.setHorizontalAlignment(JLabel.RIGHT);

		JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100,
				this.currentVolume);

		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				volumeSliderLabel.setText(s.getValue() + "%");
			}
		});

		// Sub-placement of volume slider and label in the volume container.
		volumeContainer.add(volumeSlider, BorderLayout.WEST);
		volumeContainer.add(volumeSliderLabel, BorderLayout.EAST);

		JButton soundToggleButton = new JButton("Music: "
				+ (this.currentSoundEnabled ? "ON" : "OFF"));

		soundToggleButton.setVerticalTextPosition(AbstractButton.CENTER);
		soundToggleButton.setHorizontalTextPosition(AbstractButton.CENTER);
		soundToggleButton.setMnemonic(KeyEvent.VK_M);
		soundToggleButton.setActionCommand("toggleSound");
		soundToggleButton.addActionListener(this);

		JButton backButton = new JButton("Back");
		backButton.setVerticalTextPosition(AbstractButton.CENTER);
		backButton.setHorizontalTextPosition(AbstractButton.CENTER);
		backButton.setMnemonic(KeyEvent.VK_B);
		backButton.setActionCommand("showTitleScreen");
		backButton.addActionListener(this);

		// Now, place the created components in the container.
		JPanel optionsContainer = new JPanel(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2, 2, 2, 2);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;

		optionsContainer.add(videoSettingsLabel);

		gc.gridx = 1;

		optionsContainer.add(audioSettingsLabel);

		gc.gridx = 0;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;

		optionsContainer.add(windowedResolutionDropDown, gc);

		gc.gridy = 2;

		optionsContainer.add(windowedModeDropDown, gc);

		gc.gridx = 1;
		gc.gridy = 1;

		optionsContainer.add(volumeContainer, gc);

		gc.gridy = 2;

		optionsContainer.add(soundToggleButton, gc);

		gc.gridy = 3;
		gc.insets = new Insets(2, 2, 20, 2);

		optionsContainer.add(backButton, gc);

		this.optionsScreenContentPane
				.add(optionsLabel, BorderLayout.PAGE_START);
		this.optionsScreenContentPane.add(optionsContainer,
				BorderLayout.PAGE_END);
	}

	private void initGameScreen() {
		this.gameContentPane = new JPanel(new BorderLayout());

		this.gameBoardWindow = new JCanvas();
		this.gameBoardWindow.setBorder(BorderFactory
				.createLineBorder(Color.BLACK));

		// Add mouse listener for game window.
		this.gameBoardWindow.addMouseListener(this);

		JPanel infoContainer = new JPanel(new BorderLayout());
		infoContainer.setPreferredSize(new Dimension(200, 600));
		infoContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Top part of the info window has the score information.
		JPanel scoreInfoWindow = new JPanel(new GridBagLayout());
		scoreInfoWindow.setPreferredSize(new Dimension((int) infoContainer
				.getPreferredSize().getWidth(), (int) infoContainer
				.getPreferredSize().getHeight() * 1 / 3));
		scoreInfoWindow.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Bottom part of the info window has the controls.
		JPanel controlsWindow = new JPanel(new GridBagLayout());
		controlsWindow.setPreferredSize(new Dimension((int) infoContainer
				.getPreferredSize().getWidth(), (int) infoContainer
				.getPreferredSize().getHeight() * 2 / 3));
		controlsWindow.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Zooming.
		JButton zoomInButton = new JButton("+");
		zoomInButton.setActionCommand("zoomIn");
		zoomInButton.addActionListener(this);

		JButton zoomOutButton = new JButton("-");
		zoomOutButton.setActionCommand("zoomOut");
		zoomOutButton.addActionListener(this);

		// Panning.
		JButton panRightButton = new JButton(">");
		panRightButton.setActionCommand("panRight");
		panRightButton.addActionListener(this);

		JButton panLeftButton = new JButton("<");
		panLeftButton.setActionCommand("panLeft");
		panLeftButton.addActionListener(this);

		JButton panUpButton = new JButton("^");
		panUpButton.setActionCommand("panUp");
		panUpButton.addActionListener(this);

		JButton panDownButton = new JButton("v");
		panDownButton.setActionCommand("panDown");
		panDownButton.addActionListener(this);

		// Tile Rotation.
		JButton rotateCWButton = new JButton("--\\");
		rotateCWButton.setActionCommand("rotateCW");
		rotateCWButton.addActionListener(this);

		JButton rotateCCWButton = new JButton("/--");
		rotateCCWButton.setActionCommand("rotateCCW");
		rotateCCWButton.addActionListener(this);

		// Draw Pile.
		// We make the draw icon the same size as a tile (currently).
		// We set the height of the draw icon container according to how much
		// space it should take up in the layout.
		ImageIcon drawTileImageIcon = new ImageIcon("tile-back.png");
		Image drawTileImage = drawTileImageIcon.getImage();
		BufferedImage drawTileBI = new BufferedImage(70, 70,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = drawTileBI.createGraphics();
		g.drawImage(drawTileImage, 0, 0, 70, 70, null);
		ImageIcon drawTileButtonImage = new ImageIcon(drawTileBI);

		JButton drawTileButton = new JButton(drawTileButtonImage);
		drawTileButton.setPreferredSize(new Dimension(controlsWindow
				.getPreferredSize().width * 2 / 3, controlsWindow
				.getPreferredSize().height * 2 / 8));
		drawTileButton.setActionCommand("drawTile");
		drawTileButton.addActionListener(this);

		// Current Tile.
		currentTilePanel = new JCanvas();
		currentTilePanel.setPreferredSize(new Dimension(Tile.tileSize
				* Tile.tileTypeSize, Tile.tileSize * Tile.tileTypeSize));

		// End turn button.
		// Action for this button should only be allowed to let the player
		// skip placing their meeple.
		JButton endTurnButton = new JButton("End my turn");
		endTurnButton.setActionCommand("endTurn");
		endTurnButton.addActionListener(this);

		// Fill in the info window controls.
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2, 2, 2, 2);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		gc.gridheight = 2;
		gc.fill = GridBagConstraints.NONE;
		controlsWindow.add(drawTileButton, gc);
		gc.gridx = 2;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		controlsWindow.add(zoomInButton, gc);
		gc.gridy = 1;
		controlsWindow.add(zoomOutButton, gc);
		gc.gridx = 0;
		gc.gridy = 2;
		controlsWindow.add(panLeftButton, gc);
		gc.gridx = 1;
		controlsWindow.add(panUpButton, gc);
		gc.gridx = 2;
		controlsWindow.add(panRightButton, gc);
		gc.gridx = 0;
		gc.gridy = 3;
		controlsWindow.add(rotateCCWButton, gc);
		gc.gridx = 1;
		controlsWindow.add(panDownButton, gc);
		gc.gridx = 2;
		controlsWindow.add(rotateCWButton, gc);
		gc.gridx = 0;
		gc.gridy = 4;
		gc.gridwidth = 3;
		gc.gridheight = 3;
		controlsWindow.add(currentTilePanel, gc);
		gc.gridx = 0;
		gc.gridy = 7;
		gc.gridwidth = 3;
		gc.gridheight = 1;
		controlsWindow.add(endTurnButton, gc);

		// Add everything to everything in the info container.
		this.gameContentPane.add(this.gameBoardWindow, BorderLayout.CENTER);
		this.gameContentPane.add(infoContainer, BorderLayout.EAST);
		infoContainer.add(scoreInfoWindow, BorderLayout.NORTH);
		infoContainer.add(controlsWindow, BorderLayout.SOUTH);
	}

	private int gameState = 0;
	private Player currentPlayer;

	@Override
	public void actionPerformed(ActionEvent e) {

		// Actions for gameplay.
		if (gameStarted) {

			// Handle some of the commands to do with ui things.

			// Event handlers for tile rotation.
			if ("rotateCW".equals(e.getActionCommand())
					&& currentPlayer.getCurrentTile() != null) {

				currentPlayer.getCurrentTile().rotateClockwise();
				this.currentTilePanel.repaint();
			}

			if ("rotateCCW".equals(e.getActionCommand())
					&& currentPlayer.getCurrentTile() != null) {

				currentPlayer.getCurrentTile().rotateCounterClockwise();
				this.currentTilePanel.repaint();
			}

			// int err = 0;

			// Each turn begins with a player drawing a tile from the draw
			// pile. Here we allow a player to draw the tile, and after they
			// have we draw it to the screen on the current tile panel.
			if ("drawTile".equals(e.getActionCommand()) && gameState == 0) {

				// Get the next player. TODO
				currentPlayer = game.getPlayers()[0];

				game.drawTile(currentPlayer);
				Tile tileToPlace = currentPlayer.getCurrentTile();

				tileToPlace.setBoardx(0);
				tileToPlace.setBoardy(0);
				this.currentTilePanel.add(tileToPlace);
				this.currentTilePanel.repaint();

				gameState++;

			}

			// Play a game!
			/*
			 * game = new Game(4);
			 * 
			 * while (!isDrawPileEmpty()) { for (int i = 0; i > 0; i = (i + 1) %
			 * game.getNumPlayers()) { Player p = game.getPlayers()[i];
			 * game.drawTile(p); game.placeTile(p, xPos, yPos);
			 * game.placeMeeple(p, xBoard, yBoard, xTile, yTile);
			 * 
			 * game.scoreCities(false); game.scoreCloisters(false);
			 * game.scoreRoads(false); } }
			 * 
			 * game.scoreCities(true); game.scoreCloisters(true);
			 * game.scoreRoads(true); game.scoreFields();
			 */

		}

		// Main screen.
		if ("showOptionsScreen".equals(e.getActionCommand())) {
			this.setContentPane(this.optionsScreenContentPane);
			this.validate();
			this.repaint();
		} else if ("exitGame".equals(e.getActionCommand())) {
			System.exit(0);
		} else if ("startSingleplayerGame".equals(e.getActionCommand())) {
			this.setContentPane(this.gameContentPane);
			this.validate();
			this.repaint();
			this.gameStarted = true;
			this.game = new Game(4);
		} else if ("startMultiplayerGame".equals(e.getActionCommand())) {
			// TODO
		}

		// Options screen.
		if ("showTitleScreen".equals(e.getActionCommand())) {
			this.setContentPane(this.titleScreenContentPane);
			this.validate();
			this.repaint();
		} else if ("changeWindowedMode".equals(e.getActionCommand())) {
			// Get the users selection.
			JComboBox cb = (JComboBox) e.getSource();
			String mode = (String) cb.getSelectedItem();

			if ("Fullscreen".equals(mode)
					&& !"Fullscreen".equals(this.currentWindowedMode)) {
				// Fullscreen the window by removing the title bar, resizing
				// to the screen size, and moving to fill the screen.
				this.dispose();
				this.setUndecorated(true);
				this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
				this.setLocation(0, 0);
				this.setVisible(true);

				// Disable the resolution setting.
				this.windowedResolutionDropDown.setEnabled(false);
			} else if (!"Fullscreen".equals(mode)) {
				this.dispose();
				this.setUndecorated("Borderless Window".equals(mode));
				this.setVisible(true);

				// Change the resolution to match the current selection.
				this.setSize(this.currentWindowedResolution);

				if ("Fullscreen".equals(this.currentWindowedMode)) {
					this.setLocationRelativeTo(null);

					// Re-enable the resolution setting.
					this.windowedResolutionDropDown.setEnabled(true);
				}
			}

			this.currentWindowedMode = mode;

		} else if ("changeResolution".equals(e.getActionCommand())
				&& this.currentWindowedMode != "Fullscreen") {
			JComboBox cb = (JComboBox) e.getSource();
			String res = (String) cb.getSelectedItem();

			String[] resArray = res.split(" ");

			int width = Integer.parseInt(resArray[0]);
			int height = Integer.parseInt(resArray[2]);

			this.setSize(width, height);
			this.currentWindowedResolution = new Dimension(width, height);
		} else if ("toggleSound".equals(e.getActionCommand())) {
			JButton b = (JButton) e.getSource();

			if (this.currentSoundEnabled) {
				b.setText("Music: OFF");
				this.currentSoundEnabled = false;
			} else {
				b.setText("Music: ON");
				this.currentSoundEnabled = true;
				// Code to play song file; looped.
			}

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// Detect if there has been a mouse click on the board canvas object.
		if (e.getComponent() == this.gameBoardWindow) {

			int err = 0;

			// We'll do some click calculations outside of the state-specific
			// checks to prevent code duplication. First get the clicked
			// position, and then convert it to a tile location.
			int xPos = e.getX();
			int yPos = e.getY();

			// Example:
			// x click is 52, with each tile having 5 TileType's at size
			// 10 each. Then the player has clicked 52 / 5*10 = 1st tile.
			// Our array is zero-indexed, so this is correct. Otherwise we'd
			// get the mathematical ceiling.
			int xBoard = xPos / (Tile.tileTypeSize * Tile.tileSize);
			int yBoard = yPos / (Tile.tileTypeSize * Tile.tileSize);

			int xTile = (xPos % (Tile.tileTypeSize * Tile.tileSize))
					/ Tile.tileTypeSize;
			int yTile = (yPos % (Tile.tileTypeSize * Tile.tileSize))
					/ Tile.tileTypeSize;

			// Check that the proper game state is selected. Here we are
			// looking for the tile placement state.
			if (gameState == 1) {

				// Place the tile.
				Tile tileToPlace = currentPlayer.getCurrentTile();
				err = game.placeTile(currentPlayer, xBoard, yBoard);

				// If no error draw the tile on the gameboard and remove it
				// from the currentTile area.
				if (err == 0) {
					this.gameBoardWindow.add(tileToPlace);
					this.currentTilePanel.clear();
					this.gameBoardWindow.repaint();
					this.currentTilePanel.repaint();

					gameState++;
				} else {
					// TODO: better error handling
					JOptionPane.showMessageDialog(this,
							"Can't place tile there.");
				}

				return;
			}

			// Here we are looking for the meeple placement state.
			if (gameState == 2) {

				// Place the meeple.
				err = game.placeMeeple(currentPlayer, xBoard, yBoard, xTile,
						yTile);

				if (err == 0) {
					Meeple m = this.game
							.getMeeple(xBoard, yBoard, xTile, yTile);
					this.gameBoardWindow.add(m);
					this.gameBoardWindow.repaint();

					gameState = 0;

					this.game.scoreCloisters(false);
					this.game.scoreRoads(false);
					this.game.scoreCities(false);
				} else {
					// TODO: better error handling
					JOptionPane.showMessageDialog(this,
							"Can't place meeple there");
				}

				return;
			}

		}

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

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GameUI game = new GameUI();
			}
		});
	}

}