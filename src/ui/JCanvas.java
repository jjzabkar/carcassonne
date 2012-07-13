package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.1
 * @since 2011-11-21
 */
class JCanvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private ArrayList<DrawableInterface> components;

    public JCanvas() {
        this.setDoubleBuffered(true);
        this.components = new ArrayList<DrawableInterface>();

        this.setPreferredSize(new Dimension(600, 600));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < components.size(); i++) {
            components.get(i).draw(g);
        }

    }

    public void add(DrawableInterface o) {
        components.add(o);
    }

    public void remove(DrawableInterface o) {
        components.remove(o);
    }

    public void clear() {
        components.clear();
    }
}