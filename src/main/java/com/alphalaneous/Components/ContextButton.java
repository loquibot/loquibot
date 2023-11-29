package com.alphalaneous.Components;

import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utilities.GraphicsFunctions;
import com.alphalaneous.Window;

import javax.swing.*;
import java.awt.*;

public class ContextButton extends RoundedButton {

    public ContextButton(String label, Function function) {
        super(label);
        addActionListener(e -> {
           function.run();
           Window.destroyContextMenu();
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBackground("background-intense", "background-intense");

        //setForeground(Defaults.FOREGROUND_A);
        setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        setPreferredSize(new Dimension(172, 30));
        setHorizontalAlignment(SwingConstants.LEFT);

    }

    @Override
    public void paintComponent(Graphics g) {
        GraphicsFunctions.roundCorners(g, getBackground(), getSize(), 8);
        super.paintComponent(g);
    }

}
