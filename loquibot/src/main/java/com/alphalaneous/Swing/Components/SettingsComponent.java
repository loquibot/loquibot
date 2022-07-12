package com.alphalaneous.Swing.Components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsComponent extends JPanel {

    private static final ArrayList<SettingsComponent> components = new ArrayList<>();

    public SettingsComponent(JComponent panel, Dimension dimension){
        setLayout(null);
        setPreferredSize(new Dimension(dimension.width, dimension.height+6));
        setBackground(new Color(0,0,0,0));
        panel.setBounds(30,0,panel.getPreferredSize().width, panel.getPreferredSize().height);
        add(panel);
        setOpaque(false);
        components.add(this);
    }

    protected void resizeComponent(Dimension dimension){

    }
    protected void refreshUI(){

    }
    public static void refreshAll(){
        for(SettingsComponent component : components){
            component.refreshUI();
            component.setBackground(new Color(0,0,0,0));
        }
    }
}
