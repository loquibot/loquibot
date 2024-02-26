package com.alphalaneous;

import com.alphalaneous.Components.AccountPanel;
import com.alphalaneous.Components.DialogBox;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Pages.SettingsSubPages.AccountsPage;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class Onboarding {
	private static final ThemeableJPanel everything = new ThemeableJPanel();
	private static final ThemeableJPanel content = new ThemeableJPanel();

	private static final int width = 500;
	private static final int height = 500;

	static AccountPanel twitchAccount = new AccountPanel("Twitch Account", null);

	static AccountPanel youTubeAccount = new AccountPanel("YouTube Account", null);
	static RoundedButton nextButton = new RoundedButton("Next");
	static ThemeableJLabel authInfo = new ThemeableJLabel();

	public static final AtomicBoolean isCompleted = new AtomicBoolean(false);

	public static void init() {

		AtomicBoolean twitchLoggedIn = new AtomicBoolean(false);
		AtomicBoolean youtubeLoggedIn = new AtomicBoolean(false);

		youTubeAccount.setLoginButton(AccountsPage.createLoginButton("Log in with YouTube", Assets.getImage("youtube-logo"), () -> {
			new Thread(() -> {
				try {
					YouTubeAccount.setCredential(true, false);
					youTubeAccount.login(YouTubeAccount.name, new ImageIcon(Assets.makeRoundedCorner(YouTubeAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					youtubeLoggedIn.set(true);
					setNextButtonVisible();
				}
				catch (Exception e){
					Logging.getLogger().error(e.getMessage(), e);
				}
			}).start();

		}));

		twitchAccount.setLoginButton(AccountsPage.createLoginButton("Log in with Twitch", Assets.getImage("twitch-logo"), () -> {
			new Thread(() -> {
				TwitchAPI.setOauth(false, () -> {
					twitchAccount.login(TwitchAccount.display_name, new ImageIcon(Assets.makeRoundedCorner(TwitchAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					twitchLoggedIn.set(true);
					setNextButtonVisible();
				});
			}).start();
		}));

		ThemeableJPanel infoPanel = new ThemeableJPanel();
		infoPanel.setOpaque(false);

		ThemeableJLabel infoLabel = new ThemeableJLabel("Connect an account to allow Loquibot to read your chat and listen for channel events.");
		infoLabel.setLineWrap(true);
		infoLabel.setForeground("foreground");
		infoLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

		infoPanel.add(infoLabel);

		everything.setBounds(0, 0, width-4, height);
		everything.setBorder(new EmptyBorder(10,10,10,10));
		everything.setOpaque(false);
		everything.setLayout(new BorderLayout());

		content.setOpaque(false);
		content.setBackground(new Color(0,0,0,0));
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));


		ThemeableJPanel titlePanel = new ThemeableJPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new GridBagLayout());
		ThemeableJLabel mainText = new ThemeableJLabel();

		mainText.setText("Connect to Loquibot");
		mainText.setOpaque(false);
		mainText.setForeground("foreground");
		mainText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(24f));

		titlePanel.add(mainText);

		ThemeableJPanel authInfoPanel = new ThemeableJPanel();
		authInfoPanel.setOpaque(false);
		authInfoPanel.setLayout(new GridBagLayout());

		authInfo.setFont(Fonts.getFont("Poppins-Regular").deriveFont(12f));
		authInfo.setForeground("foreground");


		authInfoPanel.add(authInfo);

		nextButton.setBackground("list-background-normal", "list-background-normal");
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
					if(twitchLoggedIn.get() || youtubeLoggedIn.get()){
						SettingsHandler.writeSettings("onboardingCompleted", "true");
						isCompleted.set(true);
						Window.closeDialog();
					}
				} catch (Exception ignored) {
				}
			}
		});

		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(titlePanel);
		content.add(Box.createRigidArea(new Dimension(0, 20)));
		content.add(infoPanel);
		content.add(Box.createRigidArea(new Dimension(0, 20)));
		content.add(twitchAccount);
		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(youTubeAccount);

		ThemeableJPanel nextPanel = new ThemeableJPanel();
		nextPanel.setLayout(new GridLayout(0,1));
		nextPanel.setOpaque(false);
		nextPanel.add(authInfoPanel);
		nextPanel.add(nextButton);

		everything.add(content, BorderLayout.NORTH);
		everything.add(nextPanel, BorderLayout.SOUTH);

		DialogBox.showDialogBox(everything, true);
	}

	public static void setNextButtonVisible(){
		nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		nextButton.setEnabled(true);
		nextButton.setForeground("foreground", "foreground");
		authInfo.setText("Press Next to continue!");

	}

}
