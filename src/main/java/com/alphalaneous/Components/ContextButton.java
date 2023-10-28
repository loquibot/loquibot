package com.alphalaneous.Components;

import com.alphalaneous.Interfaces.Function;

import javax.swing.*;
import java.awt.*;

public class ContextButton extends RoundedButton {

    public ContextButton(String label, Function function) {
        super(label);
        addActionListener(e -> {
           function.run();
           //Window.destroyContextMenu();
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBackground(new Color(0,0,0,0));

        //setForeground(Defaults.FOREGROUND_A);
        setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        setPreferredSize(new Dimension(172, 30));
        setHorizontalAlignment(SwingConstants.LEFT);
    }
}
