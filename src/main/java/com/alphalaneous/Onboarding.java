package com.alphalaneous;

import com.alphalaneous.Components.DialogBox;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.SettingsHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class Onboarding {
	private static final ThemeableJPanel everything = new ThemeableJPanel();
	private static final ThemeableJPanel content = new ThemeableJPanel();

	private static final int width = 465;
	private static final int height = 300;

	public static final AtomicBoolean isCompleted = new AtomicBoolean(false);

	public static void init() {

		everything.setBounds(0, 0, width-4, height);
		everything.setLayout(null);
		everything.setOpaque(false);

		content.setOpaque(false);
		content.setBounds(0, 0, width, height-50);
		content.setBackground(new Color(0,0,0,0));
		content.setLayout(null);

		ThemeableJLabel mainText = new ThemeableJLabel();

		mainText.setText("Log In");
		mainText.setBounds(20, 20, width - 50, mainText.getPreferredSize().height + 25);
		mainText.setOpaque(false);
		mainText.setForeground("foreground");
		mainText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(18f));

		ThemeableJLabel authInfo = new ThemeableJLabel();
		authInfo.setText("Press Next to continue!");
		authInfo.setFont(Fonts.getFont("Poppins-Regular").deriveFont(12f));
		authInfo.setBounds(20, height - 80, width - 50, authInfo.getPreferredSize().height + 5);
		authInfo.setForeground("foreground");
		authInfo.setVisible(false);
		RoundedButton nextButton = new RoundedButton("Next");

		AtomicBoolean twitchLoggedIn = new AtomicBoolean(false);

		AccountButton twitchButton = new AccountButton(Assets.getImage("twitch-logo"), "Twitch",height - 180);
		twitchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					nextButton.setForeground("foreground", "foreground");
					new Thread(() -> {
						TwitchAPI.setOauth(false);
						twitchLoggedIn.set(true);
						nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						nextButton.setEnabled(true);
						nextButton.setForeground("foreground", "foreground");
						twitchButton.setUsername(TwitchAccount.display_name);
						authInfo.setVisible(true);

					}).start();
				} catch (Exception f) {
					f.printStackTrace();
				}
			}
		});


		nextButton.setBackground("list-background-normal", "list-background-normal");
		nextButton.setBounds(20, height - 45, width - 50, 30);
		nextButton.setPreferredSize(new Dimension(width - 50, 30));
		nextButton.setForeground("foreground-disabled", "foreground-disabled");
		nextButton.setBorder(BorderFactory.createEmptyBorder());
		nextButton.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
		nextButton.setEnabled(false);
		nextButton.setCursor(Cursor.getDefaultCursor());
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					if(twitchLoggedIn.get()){
						SettingsHandler.writeSettings("onboardingCompleted", "true");
						isCompleted.set(true);
						com.alphalaneous.Window.closeDialog();

					}
				} catch (Exception ignored) {
				}
			}
		});

		content.add(mainText);
		content.add(authInfo);
		content.add(twitchButton);

		everything.add(content);
		everything.add(nextButton);

		DialogBox.showDialogBox(everything, true);
	}


	private static class AccountButton extends RoundedButton {

		private final ThemeableJLabel username = new ThemeableJLabel();
		private final ThemeableJLabel service = new ThemeableJLabel();

		AccountButton(ImageIcon icon, String text, int y){
			super("");

			setBounds(20, y, width - 50, 60);
			setPreferredSize(new Dimension(width - 50, 30));

			setBackground("list-background-normal", "list-background-select");
			setForeground("foreground", "foreground");
			setBorder(BorderFactory.createEmptyBorder());
			setFont(Fonts.getFont("Poppins-Regular").deriveFont(20f));
			setLayout(null);

			ThemeableJLabel serviceIcon = new ThemeableJLabel();
			if(icon != null) serviceIcon.setIcon(icon);
			serviceIcon.setBounds(10,5, 50,50);
			add(serviceIcon);

			service.setText(text);
			service.setBounds(70, 15, width, 30);
			service.setFont(Fonts.getFont("Poppins-Regular").deriveFont(22f));
			service.setForeground("foreground");
			add(service);

			username.setBounds(70,30, width, 30);
			username.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
			username.setForeground("foreground");

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
