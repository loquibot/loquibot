package com.alphalaneous.Swing.Components;

import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;

public class LevelsPanel extends JPanel {

    private final JPanel buttonPanel = new JPanel();
    private int buttonWidth = 400;

    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JPanel borderPanel = new JPanel(new BorderLayout());
    private final JScrollPane scrollPane = new SmoothScrollPane(borderPanel);

    public LevelsPanel(){
        setOpaque(false);
        setLayout(new BorderLayout());
        setBackground(new Color(0,0,0,0));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setOpaque(false);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(8, 9, 0, 2);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        borderPanel.add(buttonPanel, BorderLayout.NORTH);
        borderPanel.setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(new Color(0,0,0,0));
        scrollPane.getViewport().setBackground(new Color(0,0,0,0));

        scrollPane.getViewport().setOpaque(false);
        borderPanel.setBackground(new Color(0,0,0,0));
        buttonPanel.setBackground(new Color(0,0,0,0));
        add(scrollPane);
    }

    public void addButton(LevelButton button){
        buttonPanel.add(button, gbc);
    }
    public void addButton(LevelButton button, int pos){
        buttonPanel.add(button, gbc, pos);
    }
    public void clearRequests(){
        buttonPanel.removeAll();
    }
    public int getQueueSize(){
        return buttonPanel.getComponentCount();
    }

    public void resizePanel(int width, int height){
        buttonWidth = width - 18;
        Component[] comp = buttonPanel.getComponents();
        for (Component component : comp) {
            if (component instanceof LevelButton) {
                ((LevelButton) component).resizeButton();
            }
        }
        setBounds(0, 0, width, height+8);
    }

    public void updateUI(long ID) {
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof LevelButton) {
                if (((LevelButton) component).ID == ID) {
                    ((LevelButton) component).refresh();
                    return;
                }
            }
        }
    }

    public void setWindowName(int count) {
        Window.setTitle("loquibot - " + count);
    }

    public LevelButton getButton(int i) {
        if(buttonPanel.getComponents().length > 0) {
            return ((LevelButton) buttonPanel.getComponent(i));
        }
        return null;
    }

    public void removeRequest(int pos){
        buttonPanel.remove(pos);
    }

    public void movePosition(int position, int newPosition) {
        long selectID = -1;
        int selectPos = -1;
        if (newPosition >= RequestsTab.getQueueSize()) {
            newPosition = RequestsTab.getQueueSize() - 1;
        }

        buttonPanel.add(buttonPanel.getComponents()[position], gbc, newPosition);

        for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
            if (getButton(i).selected) {
                selectID = RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().id();
                selectPos = i;
                break;
            }
        }

        if(newPosition < selectID){
            setSelect(selectPos, false, false);
        }

        RequestFunctions.saveFunction();
    }
    public void setSelect(int position){
        setSelect(position, false, true);
    }
    public void setSelect(int position, boolean refresh){
        setSelect(position, refresh, true);
    }
    public void setSelect(int position, boolean refresh, boolean resetScroll){
        deselectAll();
        if(buttonPanel.getComponentCount() != 0) {
            LevelButton button;
            if (buttonPanel.getComponentCount() == 1) {
                button = ((LevelButton) buttonPanel.getComponents()[0]);
            } else {
                button = ((LevelButton) buttonPanel.getComponents()[position]);
            }
            button.select(refresh);
            if (resetScroll) {
                if (position == 0) {
                    scrollPane.getViewport().setViewPosition(new Point(0, 0));
                } else {
                    scrollPane.getViewport().setViewPosition(new Point(0, button.getY()));
                }
            }
        }
    }

    public int getButtonWidth(){
        return buttonWidth;
    }

    public void deselectAll(){
        for(int i = 0; i < buttonPanel.getComponents().length; i++){
            if(buttonPanel.getComponents()[i] instanceof LevelButton){
                ((LevelButton)buttonPanel.getComponents()[i]).deselect();
            }
        }
    }

    public void refreshUI(){
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof LevelButton) {
                ((LevelButton) component).refresh();
            }
        }
        scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());
    }
}
