package com.alphalaneous.Pages;

import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Page extends ThemeableJPanel {

    boolean showChat = true;

    ThemeableJPanel contentPane = new ThemeableJPanel(){{
        setOpaque(false);
        setLayout(new BorderLayout());
    }};

    SmoothScrollPane scrollPane = new SmoothScrollPane(contentPane){{
        setOpaque(false);
        getViewport().setOpaque(false);
    }};

    ThemeableJPanel titleCard = new ThemeableJPanel(){
        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
            super.paintComponent(g);
        }
    };

    public Page(){
        this(null, false);
    }
    public Page(String title, boolean scrollable){

        setLayout(new BorderLayout());


        titleCard.setOpaque(false);
        titleCard.setPreferredSize(new Dimension(Integer.MAX_VALUE, 56));
        titleCard.setBackground("background");
        titleCard.setLayout(new BorderLayout());

        setOpaque(false);

        if(title != null) {
            ThemeableJLabel titleLabel = new ThemeableJLabel();
            titleLabel.setText(title);
            titleLabel.setForeground("foreground");
            titleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(24f));
            titleCard.setBorder(new EmptyBorder(0,20,0,0));
            titleCard.add(titleLabel, BorderLayout.WEST);
        }

        add(titleCard, BorderLayout.NORTH);

        if(!scrollable){
            scrollPane.remove(contentPane);
            add(contentPane);
        }
        else{
            contentPane.setBorder(new EmptyBorder(0,5,0,5));
            add(scrollPane);
        }
        scrollPane.setBorder(new EmptyBorder(0,0,15,0));
    }

    public ThemeableJPanel getTitleCard(){
        return titleCard;
    }

    public ThemeableJPanel getContentPane(){
        return contentPane;
    }

    public void setShowChat(boolean showChat){
        this.showChat = showChat;
    }

    public boolean showsChat(){
        return this.showChat;
    }

}
