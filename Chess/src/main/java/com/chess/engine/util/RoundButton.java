package com.chess.engine.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

public class RoundButton extends JButton {

    private final Dimension arcs;

    public RoundButton(String label, int r) {
        super(label);

        setBackground(Color.decode("#7ea651"));
        setForeground(Color.WHITE);
        setFocusable(false);

        arcs = new Dimension(r, r);
        Dimension size = getPreferredSize();
        
        size.width = size.height = Math.max(size.width, size.height);
        
        setPreferredSize(size);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.decode("#90ac44"));
        } else if (getModel().isRollover()) {
            g.setColor(Color.decode("#98bc4c"));
        } else {
            g.setColor(getBackground());
        }

        g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1, arcs.width, arcs.height);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1, arcs.width, arcs.height);
    }

    Shape shape;

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getSize().width - 1, getSize().height - 1, arcs.width, arcs.height);
        }
        
        return shape.contains(x, y);
    }
}
