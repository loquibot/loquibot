package com.alphalaneous.Swing.Components;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class VideoProgressBar extends JProgressBar {

    public VideoProgressBar(int duration) {
        setMaximum(duration*100);
        setStringPainted(true);
        setString("");
        setBorder(BorderFactory.createEmptyBorder());
        setDoubleBuffered(true);
        setOpaque(false);
        setUI((ProgressBarUI) BasicProgressBarUI.createUI(this));
        setBackground(new Color(0,0,0,0));
        setForeground(new Color(255,255,255,50));

    }
}
