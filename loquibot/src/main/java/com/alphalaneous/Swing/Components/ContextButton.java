package com.alphalaneous.Swing.Components;

import com.alphalaneous.Swing.Components.CurvedButtonAlt;
import com.alphalaneous.Swing.Components.JButtonUI;
import com.alphalaneous.Defaults;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;

public class ContextButton extends CurvedButtonAlt {

    public ContextButton(String label, Function function) {
        super(label);
        addActionListener(e -> {
           function.run();
           Window.destroyContextMenu();
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBackground(new Color(0,0,0,0));

        JButtonUI buttonUI = new JButtonUI();
        buttonUI.setBackground(new Color(0,0,0,0));
        buttonUI.setHover(Defaults.COLOR7);
        buttonUI.setSelect(Defaults.COLOR7);

        setUI(buttonUI);
        setForeground(Defaults.FOREGROUND_A);
        setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        setFont(Defaults.MAIN_FONT.deriveFont(14f));
        setPreferredSize(new Dimension(172, 30));
        setHorizontalAlignment(SwingConstants.LEFT);
    }
}
