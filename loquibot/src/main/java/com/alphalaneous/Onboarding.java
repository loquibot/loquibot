package com.alphalaneous;

import com.alphalaneous.Components.CurvedButton;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.SettingsPanels.ShortcutSettings;
import com.alphalaneous.Windows.Window;

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
	static JFrame frame = new JFrame();
	private static final JPanel content = new JPanel(null);
	private static final JButtonUI defaultUI = new JButtonUI();

	static void createPanel() {
		URL iconURL = Window.class.getResource("/Resources/Icons/windowIcon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		Image newIcon = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		frame.setIconImage(newIcon);
		frame.setTitle("GDBoard Startup");
		int width = 465;
		int height = 512;
		frame.setLocation(new Point(Defaults.screenSize.width / 2 - width / 2, Defaults.screenSize.height / 2 - height / 2));
		frame.setSize(width + 5, height + 38);
		frame.setPreferredSize(new Dimension(width + 5, height + 38));
		frame.setLayout(null);
		frame.setResizable(false);
		frame.pack();

		content.setBounds(0, 0, width - 2, height);
		content.setBackground(Defaults.SUB_MAIN);
		content.setLayout(null);

		JTextPane mainText = new JTextPane();
		StyledDocument doc = mainText.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		mainText.setText("Thank You for using GDBoard! Here are a few \nthings to get you started!");
		mainText.setBounds(25, 20, width - 50, mainText.getPreferredSize().height + 15);
		mainText.setOpaque(false);
		mainText.setEditable(false);
		mainText.setForeground(Defaults.FOREGROUND);
		mainText.setBackground(new Color(0, 0, 0, 0));
		mainText.setFont(Defaults.MAIN_FONT.deriveFont(18f));

		JTextPane infoText = new JTextPane();
		StyledDocument doc2 = infoText.getStyledDocument();
		SimpleAttributeSet center2 = new SimpleAttributeSet();
		StyleConstants.setAlignment(center2, StyleConstants.ALIGN_CENTER);
		doc2.setParagraphAttributes(0, doc2.getLength(), center2, false);
		infoText.setText("Before we begin, make sure GDBoard is VIP or Mod in your chat! This will prevent it from getting caught up in Twitch's default chat limits.\n\nGDBoard has tons of settings to tailor requests just for you, but can also work with defaults, just press next, log in, and boom, it's ready to go!");
		infoText.setBounds(25, 100, width - 50, 300);
		infoText.setOpaque(false);
		infoText.setEditable(false);
		infoText.setForeground(Defaults.FOREGROUND);
		infoText.setBackground(new Color(0, 0, 0, 0));
		infoText.setFont(Defaults.MAIN_FONT.deriveFont(13f));

		defaultUI.setBackground(Defaults.BUTTON);
		defaultUI.setHover(Defaults.BUTTON_HOVER);
		defaultUI.setSelect(Defaults.SELECT);

		JLabel authInfo = new JLabel("Press Next to Log In with Twitch and start GDBoard!");
		authInfo.setFont(Defaults.MAIN_FONT.deriveFont(12f));
		authInfo.setBounds(25, height - 80, width - 50, authInfo.getPreferredSize().height + 5);
		authInfo.setForeground(Defaults.FOREGROUND);
		CurvedButton moveOn = new CurvedButton("Click here if Success and GDBoard hasn't moved on");
		moveOn.setBackground(Defaults.BUTTON);
		moveOn.setBounds(25, height - 140, width - 55, 30);
		moveOn.setPreferredSize(new Dimension(width - 55, 30));
		moveOn.setUI(defaultUI);
		moveOn.setForeground(Defaults.FOREGROUND);
		moveOn.setBorder(BorderFactory.createEmptyBorder());
		moveOn.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		moveOn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ShortcutSettings.loadKeybind("Open", openKeybind);
				Settings.writeSettings("openKeybind", String.valueOf(openKeybind));
				Settings.writeSettings("onboarding", "false");
			}
		});
		moveOn.refresh();
		moveOn.setVisible(false);
		CurvedButton button = new CurvedButton("Next");

		button.setBackground(Defaults.BUTTON);
		button.setBounds(25, height - 45, width - 55, 30);
		button.setPreferredSize(new Dimension(width - 55, 30));
		button.setUI(defaultUI);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					//moveOn.setVisible(true);

					Thread thread = new Thread(() -> {
						APIs.setOauth();
						while (!APIs.success.get()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						Settings.writeSettings("onboarding", "false");
						Onboarding.isLoading = false;
						frame.setVisible(false);

					});
					thread.start();
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
		frame.add(content);
	}

	static void refreshUI() {
		defaultUI.setBackground(Defaults.BUTTON);
		defaultUI.setHover(Defaults.BUTTON_HOVER);
		defaultUI.setSelect(Defaults.SELECT);
		content.setBackground(Defaults.SUB_MAIN);
	}


	@SuppressWarnings("unused")
	private static JButton createButton(String icon, String tooltip) {
		JButton button = new RoundedJButton(icon, tooltip);
		button.setPreferredSize(new Dimension(50, 50));
		button.setUI(defaultUI);
		button.setBackground(Defaults.MAIN);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(20f));
		return button;
	}

}
