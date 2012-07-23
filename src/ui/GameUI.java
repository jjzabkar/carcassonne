package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Game;
import model.Player;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-06-28
 */
public class GameUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Game game;

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

    // Title screen variables.
    private JPanel titleScreenContentPane;

    private JLabel titleLabel;

    private JPanel titleContainer;
    private JButton singleplayerButton;
    private JButton multiplayerButton;
    private JButton optionsButton;
    private JButton exitButton;

    // Options screen variables.
    private JPanel optionsScreenContentPane;

    private JLabel optionsLabel;

    private JPanel optionsContainer;
    private JLabel videoSettingsLabel;
    private JLabel audioSettingsLabel;
    private JComboBox<String> windowedResolutionDropDown;
    private JComboBox<String> windowedModeDropDown;

    private JPanel volumeContainer;
    private JSlider volumeSlider;
    private JLabel volumeSliderLabel;

    private JButton soundToggleButton;
    private JButton backButton;

    // Game screen variables.
    private JPanel gameContentPane;

    private JCanvas gameBoardWindow;
    private JPanel infoWindow;

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
    //   title -> quit <------------------|
    //     |<---> Single Player --------->|
    //     |<---> Multiplayer ----------->| | video (resolution, windowed)
    //     |<---> Multiplayer (Online) -->| | sound (%)
    //     |<---> options <---------------->| music (on/off)

    private void initTitleScreen() {
        this.titleScreenContentPane = new JPanel(new BorderLayout());

        this.titleLabel = new JLabel("Carcassonne");
        this.titleLabel.setVerticalTextPosition(JLabel.BOTTOM);
        this.titleLabel.setHorizontalTextPosition(JLabel.CENTER);
        this.titleLabel.setHorizontalAlignment(JLabel.CENTER);

        this.singleplayerButton = new JButton("Singleplayer");
        this.singleplayerButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.singleplayerButton
            .setHorizontalTextPosition(AbstractButton.CENTER);
        this.singleplayerButton.setMnemonic(KeyEvent.VK_S);
        this.singleplayerButton.setActionCommand("startSingleplayerGame");
        this.singleplayerButton.addActionListener(this);

        this.multiplayerButton = new JButton("Multiplayer");
        this.multiplayerButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.multiplayerButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.multiplayerButton.setMnemonic(KeyEvent.VK_M);
        this.multiplayerButton.setActionCommand("startMultiplayerGame");
        this.multiplayerButton.addActionListener(this);
        //TODO
        this.multiplayerButton.setEnabled(false);

        this.optionsButton = new JButton("Options");
        this.optionsButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.optionsButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.optionsButton.setMnemonic(KeyEvent.VK_O);
        this.optionsButton.setActionCommand("showOptionsScreen");
        this.optionsButton.addActionListener(this);

        this.exitButton = new JButton("Quit Game");
        this.exitButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.exitButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.exitButton.setMnemonic(KeyEvent.VK_Q);
        this.exitButton.setActionCommand("exitGame");
        this.exitButton.addActionListener(this);

        // After creating the components, place them on the title screen.
        this.titleContainer = new JPanel(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        this.titleContainer.add(this.singleplayerButton, gc);

        gc.gridy = 1;

        this.titleContainer.add(this.multiplayerButton, gc);

        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(2, 2, 20, 2);

        this.titleContainer.add(this.optionsButton, gc);

        gc.gridx = 1;

        this.titleContainer.add(this.exitButton, gc);

        this.titleScreenContentPane.add(this.titleLabel,
            BorderLayout.PAGE_START);
        this.titleScreenContentPane.add(this.titleContainer,
            BorderLayout.PAGE_END);
    }

    private void initOptionsScreen() {
        this.optionsScreenContentPane = new JPanel(new BorderLayout());

        this.optionsLabel = new JLabel("Options");
        this.optionsLabel.setVerticalTextPosition(JLabel.BOTTOM);
        this.optionsLabel.setHorizontalTextPosition(JLabel.CENTER);
        this.optionsLabel.setHorizontalAlignment(JLabel.CENTER);

        this.videoSettingsLabel = new JLabel("Video");
        this.videoSettingsLabel.setVerticalTextPosition(JLabel.BOTTOM);
        this.videoSettingsLabel.setHorizontalTextPosition(JLabel.CENTER);
        this.videoSettingsLabel.setHorizontalAlignment(JLabel.CENTER);

        this.audioSettingsLabel = new JLabel("Sound");
        this.audioSettingsLabel.setVerticalTextPosition(JLabel.BOTTOM);
        this.audioSettingsLabel.setHorizontalTextPosition(JLabel.CENTER);
        this.audioSettingsLabel.setHorizontalAlignment(JLabel.CENTER);

        this.windowedResolutionDropDown =
            new JComboBox<String>(this.windowedResolutions);

        this.windowedResolutionDropDown.setSelectedItem("800 x 600");
        this.windowedResolutionDropDown.setActionCommand("changeResolution");
        this.windowedResolutionDropDown.addActionListener(this);

        this.windowedModeDropDown =
            new JComboBox<String>(this.windowedSettings);

        this.windowedModeDropDown.setSelectedItem("Windowed");
        this.windowedModeDropDown.setActionCommand("changeWindowedMode");
        this.windowedModeDropDown.addActionListener(this);

        //TODO MUSIC
        this.volumeContainer = new JPanel(new BorderLayout());
        this.volumeContainer.setPreferredSize(new Dimension(240, 20));

        this.volumeSliderLabel = new JLabel(this.currentVolume + "%");
        this.volumeSliderLabel.setVerticalTextPosition(JLabel.BOTTOM);
        this.volumeSliderLabel.setHorizontalTextPosition(JLabel.CENTER);
        this.volumeSliderLabel.setHorizontalAlignment(JLabel.RIGHT);

        this.volumeSlider =
            new JSlider(JSlider.HORIZONTAL, 0, 100, this.currentVolume);

        this.volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider s = (JSlider) e.getSource();
                volumeSliderLabel.setText(s.getValue() + "%");
            }
        });

        // Sub-placement of volume slider and label in the volume container.
        this.volumeContainer.add(this.volumeSlider, BorderLayout.WEST);
        this.volumeContainer.add(this.volumeSliderLabel, BorderLayout.EAST);

        this.soundToggleButton =
            new JButton("Music: " + (this.currentSoundEnabled ? "ON" : "OFF"));

        this.soundToggleButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.soundToggleButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.soundToggleButton.setMnemonic(KeyEvent.VK_M);
        this.soundToggleButton.setActionCommand("toggleSound");
        this.soundToggleButton.addActionListener(this);

        this.backButton = new JButton("Back");
        this.backButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.backButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.backButton.setMnemonic(KeyEvent.VK_B);
        this.backButton.setActionCommand("showTitleScreen");
        this.backButton.addActionListener(this);

        // Now, place the created components in the container. 
        this.optionsContainer = new JPanel(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;

        this.optionsContainer.add(this.videoSettingsLabel);

        gc.gridx = 1;

        this.optionsContainer.add(this.audioSettingsLabel);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        this.optionsContainer.add(this.windowedResolutionDropDown, gc);

        gc.gridy = 2;

        this.optionsContainer.add(this.windowedModeDropDown, gc);

        gc.gridx = 1;
        gc.gridy = 1;

        this.optionsContainer.add(this.volumeContainer, gc);

        gc.gridy = 2;

        this.optionsContainer.add(this.soundToggleButton, gc);

        gc.gridy = 3;
        gc.insets = new Insets(2, 2, 20, 2);

        this.optionsContainer.add(this.backButton, gc);

        this.optionsScreenContentPane.add(this.optionsLabel,
            BorderLayout.PAGE_START);
        this.optionsScreenContentPane.add(this.optionsContainer,
            BorderLayout.PAGE_END);
    }

    private void initGameScreen() {
        this.gameContentPane = new JPanel(new BorderLayout());

        this.gameBoardWindow = new JCanvas();
        this.gameBoardWindow.setBorder(BorderFactory
            .createLineBorder(Color.BLACK));

        this.infoWindow = new JPanel();
        this.infoWindow.setPreferredSize(new Dimension(200, 600));
        this.infoWindow.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.gameContentPane.add(this.gameBoardWindow, BorderLayout.CENTER);
        this.gameContentPane.add(this.infoWindow, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.getContentPane() == this.titleScreenContentPane) {
            titleScreenActionPerformed(e);
        } else if (this.getContentPane() == this.optionsScreenContentPane) {
            optionsScreenActionPerformed(e);
        }

    }

    private void titleScreenActionPerformed(ActionEvent e) {
        if ("showOptionsScreen".equals(e.getActionCommand())) {
            this.setContentPane(this.optionsScreenContentPane);
            this.revalidate();
            this.repaint();
        } else if ("exitGame".equals(e.getActionCommand())) {
            System.exit(0);
        } else if ("startSingleplayerGame".equals(e.getActionCommand())) {
            this.setContentPane(this.gameContentPane);
            this.revalidate();
            this.repaint();

            game = new Game(4);

            Player p = game.getPlayers()[0];
            game.drawTile(p);

            this.gameBoardWindow.add(p.getCurrentTile());

            game.placeTile(p, 2, 2);

            // Play a game!
            /*
            game = new Game(4);
            
            while (!isDrawPileEmpty())
            {
            	for (int i = 0; i > 0; i = (i + 1) % game.getNumPlayers())
            	{
            		Player p = game.getPlayers()[i];
            		game.drawTile(p);
            		game.placeTile(p, xPos, yPos);
            		game.placeMeeple(p, xBoard, yBoard, xTile, yTile);
            	
            		game.scoreCities(false);
            		game.scoreCloisters(false);
            		game.scoreRoads(false);
            	}
            }
            
            game.scoreCities(true);
            game.scoreCloisters(true);
            game.scoreRoads(true);
            game.scoreFields();
            */
        } else if ("startMultiplayerGame".equals(e.getActionCommand())) {
            //TODO
        }
    }

    private void optionsScreenActionPerformed(ActionEvent e) {
        if ("showTitleScreen".equals(e.getActionCommand())) {
            this.setContentPane(this.titleScreenContentPane);
            this.revalidate();
            this.repaint();
        } else if ("changeWindowedMode".equals(e.getActionCommand())) {
            // Get the users selection.
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
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
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
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

    // need to transpose mouse click on canvas to xBoard, yBoard, xTile, yTile
    // function to draw gameboard wrt/ panning & zooming
    // function to draw the gameboard

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GameUI game = new GameUI();
            }
        });
    }

}