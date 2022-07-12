package com.alphalaneous.Windows;

import javax.swing.*;
import java.io.InvalidObjectException;

public class CommandCreatorTool extends JFrame {

    private JSplitPane jsp;

    CommandCreatorTool(String name) throws InvalidObjectException {

        JPanel objectsPane = new JPanel();
        JPanel resultsPane = new JPanel();


        jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, objectsPane, resultsPane);
        setContentPane(jsp);
        setSize(300, 300);

        jsp.setDividerLocation(getWidth() / 2);


        setLocationRelativeTo(null);



        setVisible(true);
    }
}
