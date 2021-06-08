package com.alphalaneous.Panels;

import com.alphalaneous.Defaults;

import javax.swing.*;
import java.awt.*;

//JButton because for some reason you can click through the JPanel
public class ContextMenu extends JButton {

    private final GridBagConstraints gbc = new GridBagConstraints();

    private int height = 0;

    public ContextMenu() {
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5,5,0,5));

        setOpaque(false);
        setBackground(Defaults.SUB_MAIN.darker());
        setEnabled(false);

    }
    public void addButton(ContextButton button){

        height += 37; //Brute forced the right value, no idea why it's 37
        setBounds(0,0,175,height+5);
        add(button, gbc);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(20,20);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width, height, arcs.width, arcs.height);
    }
}
