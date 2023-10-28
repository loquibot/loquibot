package com.alphalaneous.Pages.CommandPages;

import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;

import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChatPageComponent extends ThemeableJPanel {

    ThemeableJPanel contentPane = new ThemeableJPanel(){{
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0,5,0,5));
    }};

    SmoothScrollPane scrollPane = new SmoothScrollPane(contentPane){{
        setOpaque(false);
        getViewport().setOpaque(false);
    }};

    public ChatPageComponent(){
        setLayout(new BorderLayout());
        setOpaque(false);
        add(scrollPane);

    }

    public ThemeableJPanel getContentPane(){
        return contentPane;
    }

}
