package com.alphalaneous.Components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MultiLineLabel extends JPanel {

    private final ArrayList<JLabel> labels = new ArrayList<>();

    public MultiLineLabel(String text, int maxWidth, Font font){

        ArrayList<String> textSpaced = new ArrayList<>(java.util.List.of(text.split(" ")));

        do {
            JLabel test = new JLabel();
            test.setFont(font);

            ArrayList<String> toRemove = new ArrayList<>();
            for (String s : textSpaced) {
                test.setText(test.getText() + " " + s);
                if (test.getPreferredSize().width >= maxWidth) {
                    test.setText((test.getText().substring(0, test.getText().length() - s.length() - 1)).trim());
                    labels.add(test);
                    break;
                }
                toRemove.add(s);
            }
            textSpaced.removeAll(toRemove);
            if(textSpaced.size() == 0){
                JLabel finalPiece = new JLabel();
                finalPiece.setFont(font);
                for(String a : toRemove){
                    finalPiece.setText((finalPiece.getText() + " " + a).trim());
                }
                labels.add(finalPiece);
            }
        } while (textSpaced.size() != 0);

        setOpaque(false);
        setBackground(new Color(0,0,0,0));

        setLayout(new FlowLayout(FlowLayout.LEADING, 0,0) {
                      public Dimension preferredLayoutSize(Container target) {
                          Dimension sd = super.preferredLayoutSize(target);
                          sd.width = Math.min(maxWidth, sd.width);
                          return sd;
                      }
                  });

        int height = 0;
        for(JLabel label : labels){
            height += label.getPreferredSize().height;
            add(label);
        }
        setPreferredSize(new Dimension(maxWidth, height));

    }
    @Override
    public void setForeground(Color fg){
        if(labels != null) {
            for (JLabel label : labels) {
                label.setForeground(fg);
            }
        }
    }
}
