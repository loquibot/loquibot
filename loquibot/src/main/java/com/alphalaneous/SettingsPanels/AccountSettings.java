package com.alphalaneous.SettingsPanels;

import com.alphalaneous.*;
import com.alphalaneous.Components.*;
import com.alphalaneous.Panels.ContextButton;
import com.alphalaneous.Panels.ContextMenu;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Base64;

import static com.alphalaneous.Defaults.settingsButtonUI;

public class AccountSettings {


	private static final JPanel settingsPanel = new JPanel();
	private static final JFrame logonFrame = new JFrame();

	private static final LangLabel usernameLabel = new LangLabel("$USERNAME$");
	private static final LangLabel passwordLabel = new LangLabel("$PASSWORD$");
	private static final LangLabel disclaimerLabel = new LangLabel("$DISCLAIMER$");
	private static final LangLabel settingsTitleLabel = new LangLabel("$ACCOUNTS_SETTINGS$");

	private static final FancyTextArea usernameTextArea = new FancyTextArea(false, false);
	private static final FancyPasswordField passwordTextArea = new FancyPasswordField();

	private static final CurvedButton loginButton = new CurvedButton("$LOGIN$");
	private static final CurvedButton cancelButton = new CurvedButton("$CANCEL$");

	private static final Color red = new Color(255, 0, 0);

	private static AccountPanel geometryDashPanel = null;
	private static AccountPanel twitchPanel = null;

	public static JPanel createPanel() {

		settingsPanel.setDoubleBuffered(true);
		settingsPanel.setBounds(0, 0, 542, 622);
		settingsPanel.setBackground(Defaults.SUB_MAIN);
		settingsPanel.setLayout(null);


		settingsTitleLabel.setForeground(Defaults.FOREGROUND);
		settingsTitleLabel.setBounds(25,20,500,50);
		settingsTitleLabel.setFont(Defaults.MAIN_FONT.deriveFont(24f));

		logonFrame.setSize(500, 270);
		logonFrame.setResizable(false);
		logonFrame.setTitle("Log into GD");
		logonFrame.setIconImage(Assets.loquibot.getImage());
		logonFrame.getContentPane().setBackground(Defaults.MAIN);
		logonFrame.setLayout(null);


		disclaimerLabel.setBounds(10, 140, 464, 30);
		disclaimerLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		disclaimerLabel.setForeground(Defaults.FOREGROUND2);
		logonFrame.add(disclaimerLabel);

		usernameLabel.setBounds(10, 10, 464, 30);
		usernameLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		usernameLabel.setForeground(Defaults.FOREGROUND);
		logonFrame.add(usernameLabel);

		passwordLabel.setBounds(10, 70, 464, 30);
		passwordLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		passwordLabel.setForeground(Defaults.FOREGROUND);
		logonFrame.add(passwordLabel);

		usernameTextArea.setBounds(10, 40, 464, 30);
		logonFrame.add(usernameTextArea);

		passwordTextArea.setBounds(10, 100, 464, 30);
		logonFrame.add(passwordTextArea);

		loginButton.setBounds(10, 180, 230, 40);
		loginButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		loginButton.setUI(settingsButtonUI);
		loginButton.setBackground(Defaults.BUTTON);
		loginButton.setForeground(Defaults.FOREGROUND);
		loginButton.setPreferredSize(new Dimension(232, 40));
		loginButton.refresh();
		loginButton.addActionListener(e -> {
			try {
				boolean successfulLogin = GDAPI.login(usernameTextArea.getText(), String.valueOf(passwordTextArea.getPassword()));
				if(successfulLogin){
					LoadGD.isAuth = true;
					refreshGD(usernameTextArea.getText());
					Settings.writeSettings("p", new String(Base64.getEncoder().encode(xor(new String(passwordTextArea.getPassword())).getBytes())));
					Settings.writeSettings("GDUsername", usernameTextArea.getText());
					Settings.writeSettings("GDLogon", "true");
					logonFrame.setVisible(false);
				}
				else {
					usernameLabel.setForeground(red);
					passwordLabel.setForeground(red);
					Settings.writeSettings("GDLogon", "false");
					LoadGD.isAuth = false;
				}
			} catch (Exception f) {
				f.printStackTrace();
				usernameLabel.setForeground(red);
				usernameLabel.setTextLang("Failed");
			}
		});
		logonFrame.add(loginButton);

		cancelButton.setBounds(244, 180, 230, 40);
		cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		cancelButton.setUI(settingsButtonUI);
		cancelButton.setBackground(Defaults.BUTTON);
		cancelButton.setForeground(Defaults.FOREGROUND);
		cancelButton.setPreferredSize(new Dimension(230, 40));
		cancelButton.refresh();
		cancelButton.addActionListener(e -> logonFrame.setVisible(false));

		logonFrame.add(cancelButton);

		geometryDashPanel = new AccountPanel(
				() -> Window.addContextMenu(createGDContextMenu()), 80);
		twitchPanel = new AccountPanel(
				() -> Window.addContextMenu(createTwitchContextMenu()), 190);
		refreshTwitch(TwitchAccount.display_name);

		//todo Login button when not logged into GD

		settingsPanel.add(new SettingsTitle("$ACCOUNTS_SETTINGS$"));
		settingsPanel.add(geometryDashPanel);
		settingsPanel.add(twitchPanel);

		return settingsPanel;
	}

	public static ContextMenu createGDContextMenu(){
		ContextMenu geometryDashContextMenu = new ContextMenu();
		geometryDashContextMenu.addButton(new ContextButton("Refresh Login", () -> logonFrame.setVisible(true)));
		geometryDashContextMenu.addButton(new ContextButton("Logout", () -> logonFrame.setVisible(true)));
		return geometryDashContextMenu;
	}
	public static ContextMenu createTwitchContextMenu(){
		ContextMenu twitchContextMenu = new ContextMenu();
		twitchContextMenu.addButton(new ContextButton("Refresh Login", () -> new Thread(APIs::setOauth).start()));
		return twitchContextMenu;
	}

	public static void refreshTwitch(String channel) {
		twitchPanel.refreshInfo(channel, "Twitch", new ImageIcon(makeRoundedCorner(TwitchAccount.profileImage).getScaledInstance(60,60, Image.SCALE_SMOOTH)));
	}

	public static void refreshGD(String username) {
		if (LoadGD.isAuth) {
			ImageIcon icon = GDAPI.getIcon(GDAPI.getGDUserProfile(username), 120);
			geometryDashPanel.refreshInfo(username, "Geometry Dash", icon, -30, 0);
		}
	}
	public static BufferedImage makeRoundedCorner(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();

		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new Ellipse2D.Double(0, 0, w, h));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}

	public static void refreshUI() {

		settingsPanel.setBackground(Defaults.SUB_MAIN);
		settingsTitleLabel.setForeground(Defaults.FOREGROUND);

		geometryDashPanel.refresh();
		twitchPanel.refresh();

		logonFrame.getContentPane().setBackground(Defaults.MAIN);
		disclaimerLabel.setForeground(Defaults.FOREGROUND2);

		if (usernameLabel.getForeground() != red) {
			usernameLabel.setForeground(Defaults.FOREGROUND);
			passwordLabel.setForeground(Defaults.FOREGROUND);
		}
		usernameTextArea.refresh_();
		passwordTextArea.refresh_();

		loginButton.setBackground(Defaults.BUTTON);
		loginButton.setForeground(Defaults.FOREGROUND);
		loginButton.refresh();

		cancelButton.setBackground(Defaults.BUTTON);
		cancelButton.setForeground(Defaults.FOREGROUND);
		cancelButton.refresh();

		for (Component component : settingsPanel.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				component.setBackground(Defaults.BUTTON);
			}
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);
			}
		}
	}

	private static String xor(String inputString) {

		StringBuilder outputString = new StringBuilder();

		int len = inputString.length();

		for (int i = 0; i < len; i++) {
			outputString.append((char) (inputString.charAt(i) ^ 15));
		}
		return outputString.toString();
	}



	private static class AccountPanel extends JPanel {

		JLabel accountNameLabel;
		JLabel accountTypeLabel;
		JLabel accountImageLabel;
		CurvedButton dropDownButton;


		AccountPanel(Function dropDownFunction, int y){

			setLayout(null);
			accountNameLabel = new JLabel();
			accountNameLabel.setBounds(100,25,500,30);
			accountNameLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
			accountTypeLabel = new JLabel();
			accountTypeLabel.setBounds(100,50,500,30);
			accountTypeLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			accountImageLabel = new JLabel();
			accountImageLabel.setBounds(20,20,60,60);

			dropDownButton = new CurvedButton("\uF666 \uF666 \uF666");

			dropDownButton.setUI(Defaults.defaultUI);
			dropDownButton.setOpaque(false);
			dropDownButton.setFont(Defaults.SYMBOLS.deriveFont(6f));
			dropDownButton.setBounds(440,35,30,30);
			dropDownButton.setPreferredSize(new Dimension(30,30));
			dropDownButton.refresh();
			dropDownButton.addActionListener(e -> dropDownFunction.run());
			setBounds(25,y,490,100);
			setPreferredSize(new Dimension(490,100));
			setOpaque(false);

		}
		public void refreshInfo(String accountName, String accountType, ImageIcon accountImage, int xShift, int yShift){
			accountNameLabel.setText(accountName);
			accountTypeLabel.setText(accountType);
			accountImageLabel.setIcon(accountImage);
			accountImageLabel.setBounds(20+xShift,20+yShift,60+-xShift,60+-yShift);
			add(accountImageLabel);
			add(accountNameLabel);
			add(accountTypeLabel);
			add(dropDownButton);
			updateUI();
		}

		public void logout(){
			remove(accountImageLabel);
			remove(accountNameLabel);
			remove(accountTypeLabel);
		}

		public void refreshInfo(String accountName, String accountType, ImageIcon accountImage){
			refreshInfo(accountName, accountType, accountImage, 0,0);
		}

		public void refresh(){
			setBackground(Defaults.MAIN);
			accountNameLabel.setForeground(Defaults.FOREGROUND);
			accountTypeLabel.setForeground(Defaults.FOREGROUND2);
			dropDownButton.setBackground(Defaults.MAIN);
			dropDownButton.setForeground(Defaults.FOREGROUND);
			dropDownButton.refresh();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			g.setColor(getBackground());

			RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHints(qualityHints);
			g2.fillRoundRect(0, 0, getSize().width, getSize().height, 30, 30);

			super.paintComponent(g);
		}
	}

}
