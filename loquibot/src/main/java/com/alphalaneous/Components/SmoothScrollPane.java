package com.alphalaneous.Components;

import com.alphalaneous.Defaults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class SmoothScrollPane extends JScrollPane {

    public SmoothScrollPane(JComponent component){
        super(component);
        setBorder(BorderFactory.createEmptyBorder());
        getViewport().setBackground(Defaults.SUB_MAIN);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        getVerticalScrollBar().setUnitIncrement(30);
        getVerticalScrollBar().setOpaque(false);
        setOpaque(false);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().setUI(new ScrollbarUI());
        getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        setBackground(Defaults.SUB_MAIN);
        setWheelScrollingEnabled(false);
        setDoubleBuffered(true);
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                new Thread(() -> {
                    for(int i = 0; i < 30; i++){
                        int pos = getVerticalScrollBar().getValue() + e.getWheelRotation()*3;
                        getVerticalScrollBar().setValue(pos);
                        try {
                            Thread.sleep(2, 500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    }
}
