package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;
import com.alphalaneous.Interfaces.CheckListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Checkbox extends ThemeableJPanel {

    private final ThemeableJLabel check = new ThemeableJLabel("\uE922");
    private final ThemeableJLabel checkSymbol = new ThemeableJLabel("\uE73E");
    private final ThemeableJLabel hover = new ThemeableJLabel("\uE922");
    private boolean isChecked = false;

    private CheckListener checkListener = null;

    public Checkbox(){
        setLayout(null);
        setOpaque(false);


        check.setFont(Fonts.getFont("SegoeFluent").deriveFont(16f));
        check.setForeground("foreground-darker");
        check.setBounds(0, 1, 30, 30);
        checkSymbol.setBounds(0, 1, 30, 30);
        hover.setBounds(0, 1, 30, 30);

        checkSymbol.setForeground("foreground");
        checkSymbol.setFont(Fonts.getFont("SegoeFluent").deriveFont(16f));
        hover.setForeground("foreground");
        hover.setFont(Fonts.getFont("SegoeFluent").deriveFont(16f));
        checkSymbol.setVisible(false);
        hover.setVisible(false);
        add(hover);
        add(checkSymbol);
        add(check);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    check.setText("\uE73B");
                    check.setBounds(0, 1, 30, 30);
                    check.setFont(check.getFont().deriveFont(16f));
                    check.setForeground("foreground-darker");
                    hover.setVisible(false);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isChecked) {
                        check.setText("\uE922");
                        check.setForeground("foreground");
                        checkSymbol.setVisible(false);
                        isChecked = false;
                    } else {
                        check.setText("\uE73B");
                        check.setFont(check.getFont().deriveFont(18f));
                        check.setForeground("accent");
                        checkSymbol.setVisible(true);
                        isChecked = true;
                    }
                    if(checkListener != null) checkListener.run(isChecked);
                }
                hover.setVisible(true);
            }

            public void mouseEntered(MouseEvent e) {
                hover.setVisible(true);
            }

            public void mouseExited(MouseEvent e) {
                if (!isChecked) {
                    check.setText("\uE922");
                    check.setFont(check.getFont().deriveFont(16f));
                    check.setForeground("foreground-darker");
                    checkSymbol.setVisible(false);
                } else {
                    check.setText("\uE73B");
                    check.setFont(check.getFont().deriveFont(18f));
                    check.setForeground("accent");
                    checkSymbol.setVisible(true);
                }
                hover.setVisible(false);
            }
        });

        setPreferredSize(new Dimension(30,30));

    }

    public void addCheckListener(CheckListener checkListener){
        this.checkListener = checkListener;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
        if (!isChecked) {
            check.setText("\uE922");
            check.setBounds(0, 1, 30, 30);
            check.setFont(check.getFont().deriveFont(16f));
            check.setForeground("foreground-darker");
            checkSymbol.setVisible(false);
        } else {
            check.setText("\uE73B");
            check.setBounds(-1, 1, 30, 30);
            check.setFont(check.getFont().deriveFont(18f));
            check.setForeground("accent");
            checkSymbol.setVisible(true);
        }
        if(checkListener != null) checkListener.run(checked);
    }

    public boolean getSelectedState() {
        return isChecked;
    }

}
