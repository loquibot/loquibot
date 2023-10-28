package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;

import javax.swing.*;
import java.awt.*;

public class OptionComponent extends ThemeableJPanel {

    public OptionComponent(JComponent panel, Dimension dimension){
        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(dimension.width, dimension.height+6));
        panel.setBounds(30,0,panel.getPreferredSize().width, panel.getPreferredSize().height);
        add(panel);
    }

    protected void resizeComponent(Dimension dimension){
    }
}
