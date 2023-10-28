package com.alphalaneous;

import com.alphalaneous.Components.ListButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.Page;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SidebarSwitcher {

    private static final ThemeableJPanel sideBarSwitcherPanel = new ThemeableJPanel();

    private static final HashMap<ThemeableJButton, Page> pages = new HashMap<>();
    private static final ThemeableJPanel sidePanel = new ThemeableJPanel(){
        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
            super.paintComponent(g);
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


    public static ThemeableJPanel getPanel(){
        return sideBarSwitcherPanel;
    }


    public static void addPage(ImageIcon icon, Page page, Function function){

        ListButton button = new ListButton(null);
        page.setVisible(false);

        if(icon != null){
            button.setIcon(icon);
        }

        button.addActionListener(e -> {

            boolean pageWasVisible = page.isVisible();

            for (Map.Entry<ThemeableJButton, Page> set : pages.entrySet()){

                ThemeableJButton button1 = set.getKey();
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

        pages.put(button, page);
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

        for (Map.Entry<ThemeableJButton, Page> set : pages.entrySet()){
            pages1.add(set.getValue());
        }

        return pages1;
    }

}
