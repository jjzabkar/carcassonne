package client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VolumeControlPanel extends JPanel {

	private static final long serialVersionUID = -2046521548545455432L;
	private JSlider volumeSlider;
	private JLabel volumeLabel;

	public VolumeControlPanel(int initialVolume) {
		super();

		volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, initialVolume);

		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				volumeLabel.setText(String.format("%1$s%%", s.getValue()));
			}
		});

		volumeLabel = new JLabel(String.format("%1$s%%", initialVolume));

		// Value at largest will be '100%', which works out to 30 x 16 size.
		// Do this so moving the slider doesn't change the component width.
		volumeLabel.setPreferredSize(new Dimension(30, 16));
		volumeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		add(volumeSlider, BorderLayout.WEST);
		add(volumeLabel, BorderLayout.EAST);
	}
}
