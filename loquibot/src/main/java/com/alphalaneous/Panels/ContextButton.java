package com.alphalaneous.Panels;

import com.alphalaneous.Components.CurvedButtonAlt;
import com.alphalaneous.Defaults;
import com.alphalaneous.Function;
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
        setBackground(Defaults.MAIN);
        setUI(Defaults.defaultUI);
        setForeground(Defaults.FOREGROUND);
        setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        setFont(Defaults.MAIN_FONT.deriveFont(14f));
        setPreferredSize(new Dimension(172, 30));
        setHorizontalAlignment(SwingConstants.LEFT);
    }
}
