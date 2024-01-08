package com.alphalaneous.Components;

import com.alphalaneous.Components.ListButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.Page;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Utilities.GraphicsFunctions;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SidebarSwitcher {

    private static final ThemeableJPanel sideBarSwitcherPanel = new ThemeableJPanel();

    private static final HashMap<IconButton, Page> pages = new HashMap<>();
    private static final ThemeableJPanel sidePanel = new ThemeableJPanel(){
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        }
    };

    public static final ThemeableJPanel pagePanel = new ThemeableJPanel();

    static {

        sideBarSwitcherPanel.setLayout(new BorderLayout());
        sideBarSwitcherPanel.setOpaque(false);

        sidePanel.setOpaque(false);

        sidePanel.setPreferredSize(new Dimension(60, 600));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        sidePanel.add(Box.createRigidArea(new Dimension(6, 4)));

        sidePanel.setBackground("background");

        sideBarSwitcherPanel.add(Box.createRigidArea(new Dimension(2, 4)));
        sideBarSwitcherPanel.add(sidePanel, BorderLayout.WEST);

        pagePanel.setLayout(null);
        pagePanel.setOpaque(false);
        pagePanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        sideBarSwitcherPanel.add(pagePanel);
        sideBarSwitcherPanel.setBorder(new EmptyBorder(10, 10, 10, 0));

    }

    static class IconButton {

        public ThemeableJButton button;
        public ImageIcon icon;

        IconButton(ThemeableJButton button, ImageIcon icon){
            this.button = button;
            this.icon = icon;
        }

    }


    public static ThemeableJPanel getPanel(){
        return sideBarSwitcherPanel;
    }

    public static void togglePage(Page page, boolean toggle){

        pages.forEach((k, v) -> {
            if(v.equals(page)) {
                k.button.setVisible(toggle);
                Component[] components = k.button.getParent().getComponents();
                for(int i = 0; i < components.length; i++){
                    if(components[i].equals(k)){
                        components[i+1].setVisible(toggle);
                    }
                }
            }
        });
    }

    public static void setTheme(boolean light){
        pages.forEach((k, v) -> {
            if(light){
                k.button.setIcon(Assets.invertImage(k.icon));
            }
            else {
                k.button.setIcon(k.icon);
            }
        });
    }


    public static void addPage(ImageIcon icon, Page page, Function function){


        ListButton button = new ListButton(null);

        button.setArc(17);

        IconButton iconButton = new IconButton(button, icon);

        page.setVisible(false);

        if(icon != null){
            button.setIcon(icon);
        }

        button.addActionListener(e -> {

            boolean pageWasVisible = page.isVisible();

            for (Map.Entry<IconButton, Page> set : pages.entrySet()){

                ThemeableJButton button1 = set.getKey().button;
                button1.setSelected(false);

                Page page1 = set.getValue();
                page1.setVisible(false);
            }

            button.setSelected(true);
            page.setVisible(true);

            Window.setChatVisible(page.showsChat());

            if(!pageWasVisible) {
                if (function != null) function.run();
            }
        });

        pages.put(iconButton, page);
        sidePanel.add(button);

        if(pages.size() == 1){
            button.setSelected(true);
            page.setVisible(true);
            if (function != null) function.run();
        }

        pagePanel.add(page);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    public static ArrayList<Page> getPages(){

        ArrayList<Page> pages1 = new ArrayList<>();

        for (Map.Entry<IconButton, Page> set : pages.entrySet()){
            pages1.add(set.getValue());
        }

        return pages1;
    }

}
