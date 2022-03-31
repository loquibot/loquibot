package com.alphalaneous;

import com.alphalaneous.Components.CurvedButton;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.SettingsPanels.ShortcutSettings;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.DialogBox;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class Onboarding {
	public static int openKeybind = 36;
	static boolean isLoading = false;
	private static final JPanel content = new JPanel(null);
	private static final JButtonUI defaultUI = new JButtonUI();

	static void createPanel() {

		int width = 465;
		int height = 512;

		content.setOpaque(false);
		content.setBounds(0, 0, width - 2, height);
		content.setBackground(new Color(0,0,0,0));
		content.setLayout(null);

		JTextPane mainText = new JTextPane();
		StyledDocument doc = mainText.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		mainText.setText("Thank You for using loquibot! Here are a few \nthings to get you started!");
		mainText.setBounds(20, 20, width - 50, mainText.getPreferredSize().height + 25);
		mainText.setOpaque(false);
		mainText.setEditable(false);
		mainText.setForeground(Defaults.FOREGROUND_A);
		mainText.setBackground(new Color(0,0,0,0));
		mainText.setFont(Defaults.MAIN_FONT.deriveFont(18f));

		JTextPane infoText = new JTextPane();
		StyledDocument doc2 = infoText.getStyledDocument();
		SimpleAttributeSet center2 = new SimpleAttributeSet();
		StyleConstants.setAlignment(center2, StyleConstants.ALIGN_CENTER);
		doc2.setParagraphAttributes(0, doc2.getLength(), center2, false);
		infoText.setText("Before we begin, make sure loquibot is VIP or Mod in your chat! This will prevent it from getting caught up in Twitch's default chat limits.\n\nloquibot has tons of settings to tailor requests just for you, but can also work with defaults, just press next, log in, and boom, it's ready to go!");
		infoText.setBounds(20, 100, width - 50, 300);
		infoText.setOpaque(false);
		infoText.setEditable(false);
		infoText.setForeground(Defaults.FOREGROUND_A);
		infoText.setBackground(new Color(0,0,0,0));
		infoText.setFont(Defaults.MAIN_FONT.deriveFont(13f));

		defaultUI.setBackground(Defaults.COLOR2);
		defaultUI.setHover(Defaults.COLOR5);
		defaultUI.setSelect(Defaults.COLOR4);

		JLabel authInfo = new JLabel("Press Next to Log In with Twitch and start loquibot!");
		authInfo.setFont(Defaults.MAIN_FONT.deriveFont(12f));
		authInfo.setBounds(20, height - 80, width - 50, authInfo.getPreferredSize().height + 5);
		authInfo.setForeground(Defaults.FOREGROUND_A);
		CurvedButton moveOn = new CurvedButton("Click here if Success and loquibot hasn't moved on");
		moveOn.setBackground(Defaults.COLOR2);
		moveOn.setBounds(20, height - 140, width - 55, 30);
		moveOn.setPreferredSize(new Dimension(width - 55, 30));
		moveOn.setUI(defaultUI);
		moveOn.setForeground(Defaults.FOREGROUND_A);
		moveOn.setBorder(BorderFactory.createEmptyBorder());
		moveOn.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		moveOn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Settings.writeSettings("openKeybind", String.valueOf(openKeybind));
				Settings.writeSettings("onboarding", "false");
			}
		});
		moveOn.refresh();
		moveOn.setVisible(false);
		CurvedButton button = new CurvedButton("Next");

		button.setBackground(Defaults.COLOR2);
		button.setBounds(20, height - 45, width - 50, 30);
		button.setPreferredSize(new Dimension(width - 50, 30));
		button.setUI(defaultUI);
		button.setForeground(Defaults.FOREGROUND_A);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					new Thread(() -> {
						APIs.setOauth();
						while (!APIs.success.get()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						System.out.println("here");
						Settings.writeSettings("onboarding", "false");
						Onboarding.isLoading = false;
						com.alphalaneous.Windows.Window.closeDialog();
					}).start();
				} catch (Exception ignored) {
				}
			}
		});
		button.refresh();

		content.add(mainText);
		content.add(authInfo);
		content.add(button);
		content.add(moveOn);
		content.add(infoText);
		DialogBox.showDialogBox(content, true);
	}

	static void refreshUI() {
		defaultUI.setBackground(Defaults.COLOR2);
		defaultUI.setHover(Defaults.COLOR5);
		defaultUI.setSelect(Defaults.COLOR4);
		content.setBackground(new Color(0,0,0,0));
	}


	@SuppressWarnings("unused")
	private static JButton createButton(String icon, String tooltip) {
		JButton button = new RoundedJButton(icon, tooltip);
		button.setPreferredSize(new Dimension(50, 50));
		button.setUI(defaultUI);
		button.setBackground(Defaults.COLOR);
		button.setForeground(Defaults.FOREGROUND_A);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(20f));
		return button;
	}

}
