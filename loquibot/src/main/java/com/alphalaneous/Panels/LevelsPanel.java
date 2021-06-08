package com.alphalaneous.Panels;

import com.alphalaneous.*;
import com.alphalaneous.Components.ScrollbarUI;
import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;

public class LevelsPanel {

	private static final JPanel buttonPanel = new JPanel();
	private static int buttonWidth = 400;

	private static int prevSelectedID = 0;
	private static final GridBagConstraints gbc = new GridBagConstraints();
	private static final JPanel borderPanel = new JPanel(new BorderLayout());
	private static final JScrollPane scrollPane = new SmoothScrollPane(borderPanel);
	private static final JPanel root = new JPanel(new BorderLayout());


	public static void createPanel(){

		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setOpaque(false);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(8, 8, 0, 2);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		borderPanel.add(buttonPanel, BorderLayout.NORTH);
		borderPanel.setOpaque(false);

		borderPanel.setBackground(Defaults.MAIN);
		buttonPanel.setBackground(Defaults.MAIN);
		root.add(scrollPane);

	}

	public static void refreshButtons(){
		buttonPanel.removeAll();
		//long time = ZonedDateTime.now().toInstant().toEpochMilli();

		for(LevelData data : Requests.levels){
			LevelButton button = data.getLevelButton();
			buttonPanel.add(button, gbc);
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//long timeAfter = ZonedDateTime.now().toInstant().toEpochMilli();
		//System.out.println(timeAfter - time);
		buttonPanel.updateUI();
	}

	public static void resizeButtons(int width){
		buttonWidth = width - 15;
		Component[] comp = buttonPanel.getComponents();
		for (Component component : comp) {
			if (component instanceof LevelButton) {
				((LevelButton) component).resizeButton(width);
			}
		}
	}

	public static void updateUI(long ID, boolean vulgar, boolean image, boolean analyzed) {
		while (true) {
			for (Component component : buttonPanel.getComponents()) {
				if (component instanceof LevelButton) {
					if (((LevelButton) component).ID == ID) {
						((LevelButton) component).setAnalyzed(analyzed, image, vulgar);
						((LevelButton) component).refresh(image, vulgar);
						return;
					}
				}
			}
		}
	}

	public static boolean isScrollbarVisible(){
		return scrollPane.getVerticalScrollBar().isShowing();
	}
	public static JPanel getReqWindow() {
		return root;
	}

	public static void setName(int count) {
		Window.windowFrame.setTitle("loquibot - " + count);
	}

	public static LevelButton getButton(int i) {
		return ((LevelButton) buttonPanel.getComponent(i));
	}

	public static void movePosition(int position, int newPosition) {
		long selectID = -1;
		if (newPosition >= Requests.levels.size()) {
			newPosition = Requests.levels.size() - 1;
		}
		for (int i = 0; i < Requests.levels.size(); i++) {
			if (getButton(i).selected) {
				selectID = Requests.levels.get(i).getLevelData().id();
			}
		}
		System.out.println("Position: " + position + " | newPosition: " + newPosition);
		for (int i = 0; i < Requests.levels.size(); i++) {
			if (selectID == Requests.levels.get(i).getLevelData().id()) {
				LevelsPanel.setSelect(i);
			}
		}
		RequestFunctions.saveFunction();
		refreshButtons();
	}
	public static void setSelect(int position){
		setSelect(position, false);
	}

	public static void setSelect(int position, boolean refresh){
		deselectAll();
		if(buttonPanel.getComponentCount() != 0) {
			LevelButton button = ((LevelButton) buttonPanel.getComponents()[position]);
			button.select(refresh);
			if(position == 0){
				scrollPane.getViewport().setViewPosition(new Point(0,0));
			}
			else {
				scrollPane.getViewport().setViewPosition(new Point(0, button.getY()));
			}
		}
	}

	public static int getSize(){
		return buttonPanel.getComponentCount();
	}

	//public static int getSelectedID(){
		//return selectedID;
	//}

	public static int getPrevSelectedID(){
		return prevSelectedID;
	}

	public static int getButtonWidth(){
		return buttonWidth;
	}

	public static void deselectAll(){
		for(int i = 0; i < buttonPanel.getComponents().length; i++){
			if(buttonPanel.getComponents()[i] instanceof LevelButton){
				((LevelButton)buttonPanel.getComponents()[i]).deselect();
			}
		}
	}

	public static int findButton(JButton button){
		for(int i = 0; i < buttonPanel.getComponents().length; i++){
			if(buttonPanel.getComponents()[i] instanceof JButton){
				if(buttonPanel.getComponents()[i].equals(button)){
					return i;
				}
			}
		}
		return -1;
	}

	public static void refreshUI(){
		root.setBackground(Defaults.MAIN);
		int i = 0;
		for (Component component : buttonPanel.getComponents()) {
			if (component instanceof LevelButton) {
				if (LevelButton.selectedID == i) {
					((LevelButton) component).select();
				} else {
					component.setBackground(Defaults.MAIN);
				}
				((LevelButton) component).refresh();
			}
			i++;
		}
		scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());
		scrollPane.setBackground(Defaults.MAIN);
		scrollPane.getViewport().setBackground(Defaults.MAIN);
		borderPanel.setBackground(Defaults.MAIN);
		buttonPanel.setBackground(Defaults.MAIN);
	}

}
