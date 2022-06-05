package com.alphalaneous;

import com.alphalaneous.Components.CurvedButton;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.Windows.DialogBox;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class Onboarding {
	public static int openKeybind = 36;
	static boolean isLoading = false;
	private static final JPanel everything = new JPanel(null);
	private static final JPanel content = new JPanel(null);
	private static final JButtonUI defaultUI = new JButtonUI();
	private static JLabel tutorialImage = new JLabel();

	private static int width = 465;
	private static int height = 512;

	static void createPanel() {

		everything.setBounds(0, 0, width-4, height);
		everything.setLayout(null);
		everything.setOpaque(false);

		tutorialImage.setBounds(0,0,width,height-60);
		tutorialImage.setIcon(Assets.tutorial);
		everything.add(tutorialImage);

		tutorialImage.setVisible(false);

		content.setOpaque(false);
		content.setBounds(0, 0, width, height-100);
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

		infoText.setText("Before we begin, make sure loquibot is VIP or Mod in your chat! This will prevent it from getting caught up in Twitch and YouTube's default chat limits.\n\nloquibot has tons of settings to tailor requests just for you, but can also work with defaults, just log in with Twitch or YouTube, press next, and boom, it's ready to go!");
		infoText.setBounds(20, 100, width - 50, 300);
		infoText.setOpaque(false);
		infoText.setEditable(false);
		infoText.setForeground(Defaults.FOREGROUND_A);
		infoText.setBackground(new Color(0,0,0,0));
		infoText.setFont(Defaults.MAIN_FONT.deriveFont(13f));

		defaultUI.setBackground(Defaults.COLOR2);
		defaultUI.setHover(Defaults.COLOR5);
		defaultUI.setSelect(Defaults.COLOR4);

		JLabel authInfo = new JLabel("Press Next to continue into loquibot!");
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

		CurvedButton nextButton = new CurvedButton("Next");

		AtomicBoolean twitchLoggedIn = new AtomicBoolean(false);
		AtomicBoolean youtubeLoggedIn = new AtomicBoolean(false);

		AccountButton twitchButton = new AccountButton(Assets.TwitchLarge, "Twitch",height - 245);
		twitchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					nextButton.setLForeground(Defaults.FOREGROUND_A);
					new Thread(() -> {
						Settings.writeSettings("twitchEnabled", "true");
						APIs.setOauth();
						while (!APIs.success.get()) {
							Utilities.sleep(100);
						}
						twitchLoggedIn.set(true);
						twitchButton.setUsername(TwitchAccount.login);
					}).start();
				} catch (Exception ignored) {
				}
			}
		});

		AccountButton youtubeButton = new AccountButton(Assets.YouTubeLarge, "YouTube", height - 175);
		youtubeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					nextButton.setLForeground(Defaults.FOREGROUND_A);
					new Thread(() -> {
						Settings.writeSettings("youtubeEnabled", "true");
						YouTubeAccount.setCredential(true);
						youtubeLoggedIn.set(true);
						youtubeButton.setUsername(YouTubeAccount.name);

					}).start();
				} catch (Exception ignored) {
				}
			}
		});


		final int[] page = {0};

		nextButton.setBackground(Defaults.COLOR2);
		nextButton.setBounds(20, height - 45, width - 50, 30);
		nextButton.setPreferredSize(new Dimension(width - 50, 30));
		nextButton.setUI(defaultUI);
		nextButton.setForeground(Defaults.FOREGROUND_B);
		nextButton.setBorder(BorderFactory.createEmptyBorder());
		nextButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					if(youtubeLoggedIn.get() || twitchLoggedIn.get()){
						Settings.writeSettings("onboarding", "false");
						Onboarding.isLoading = false;
						if(!Settings.getSettings("seenTutorial").exists()){
							switch (page[0]){
								case 0:
									content.setVisible(false);
									tutorialImage.setVisible(true);
									break;
								case 1:
								default:
									com.alphalaneous.Windows.Window.closeDialog();
									break;
							}
							page[0]++;
						}
						else {
							com.alphalaneous.Windows.Window.closeDialog();
						}
					}
				} catch (Exception ignored) {
				}
			}
		});
		nextButton.refresh();


		//Conner is cute

		content.add(mainText);
		content.add(authInfo);
		content.add(twitchButton);
		content.add(youtubeButton);
		content.add(moveOn);
		content.add(infoText);

		everything.add(content);
		everything.add(nextButton);

		DialogBox.showDialogBox(everything, true);
	}

	static void refreshUI() {
		defaultUI.setBackground(Defaults.COLOR2);
		defaultUI.setHover(Defaults.COLOR5);
		defaultUI.setSelect(Defaults.COLOR4);
		content.setBackground(new Color(0,0,0,0));
		everything.setBackground(new Color(0,0,0,0));
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

	private static class AccountButton extends CurvedButton {

		private final JLabel username = new JLabel();
		private final JLabel service = new JLabel();

		AccountButton(ImageIcon icon, String text, int y){
			super("");
			setBounds(20, y, width - 50, 60);
			setPreferredSize(new Dimension(width - 50, 30));

			setUI(defaultUI);
			setBackground(Defaults.COLOR2);
			setForeground(Defaults.FOREGROUND_A);
			setBorder(BorderFactory.createEmptyBorder());
			setFont(Defaults.MAIN_FONT.deriveFont(20f));
			setLayout(null);

			JLabel serviceIcon = new JLabel(icon);
			serviceIcon.setBounds(10,5, 50,50);
			add(serviceIcon);

			service.setText(text);
			service.setBounds(70, 15, width, 30);
			service.setFont(Defaults.MAIN_FONT.deriveFont(22f));
			service.setForeground(Defaults.FOREGROUND_A);
			add(service);

			username.setBounds(70,30, width, 30);
			username.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			username.setForeground(Defaults.FOREGROUND_B);

			add(username);

		}

		public void setUsername(String text){
			service.setBounds(70, 5, width, 30);
			username.setText(text);
			username.updateUI();
			content.updateUI();
		}

	}
}
