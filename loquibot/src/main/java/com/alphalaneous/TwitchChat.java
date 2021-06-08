package com.alphalaneous;

import javax.swing.*;
import java.awt.*;

public class TwitchChat {
	private static JFrame chatWindow = new JFrame();
	private static JPanel chatPanel = new JPanel();
	private static GridBagConstraints gbc = new GridBagConstraints();


	public static void createPanel() {
		chatWindow.setUndecorated(true);
		chatWindow.setFocusable(false);
		chatWindow.setFocusableWindowState(false);
		chatWindow.setAlwaysOnTop(true);
		chatWindow.setSize(400, 600);
		chatWindow.setLayout(null);
		chatWindow.setBackground(new Color(0, 0, 0, 200));
		chatPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		chatPanel.setBackground(new Color(0, 0, 0, 0));
		chatPanel.setOpaque(false);
		chatPanel.setBounds(0, 30, 400, 570);
		chatWindow.add(chatPanel);

		chatWindow.setVisible(true);

	}

	public static void addMessage(ChatMessage chatMessage) {
		JPanel panel = new JPanel();
		JTextPane textArea = new JTextPane();
		//textArea.setContentType("text/html");
		textArea.setText(chatMessage.getSender() + ": " + chatMessage.getMessage());
		//textArea.setLineWrap(true);
		//textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setBorder(BorderFactory.createEmptyBorder());
		textArea.setOpaque(false);
		textArea.setBackground(new Color(0, 0, 0, 0));
		textArea.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		textArea.setForeground(Defaults.FOREGROUND);
		int height = getJTextPaneHeight(textArea);
		textArea.setPreferredSize(new Dimension(400, height));
		textArea.setMinimumSize(new Dimension(400, height));
		textArea.setMaximumSize(new Dimension(400, height));

		chatPanel.add(textArea);
	}

	private static int getJTextPaneHeight(JTextPane textArea) {
		JFrame tempFrame = new JFrame();
		tempFrame.setSize(400, 0);
		tempFrame.setMinimumSize(new Dimension(400, 0));
		tempFrame.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
		tempFrame.add(textArea);
		tempFrame.revalidate();
		tempFrame.pack();
		System.out.println(tempFrame.getPreferredSize().height - 39);
		return tempFrame.getPreferredSize().height - 39;
	}
}