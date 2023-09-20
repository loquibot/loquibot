package com.alphalaneous.Tabs;

import com.alphalaneous.ChatBot.ServerBot;
import com.alphalaneous.Images.Assets;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.*;
import com.alphalaneous.Settings.Logs.RequestsLog;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Theming.ThemedColor;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

public class SettingsTab {
	public static JPanel window = new JPanel();
	private static final JPanel buttonsParent = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private static final JPanel buttons = new JPanel(new GridBagLayout());
	private static final JScrollPane buttonsScroll = new SmoothScrollPane(buttonsParent);
	private static final JPanel content = new JPanel();
	public static final JButtonUI selectUI = new JButtonUI();
	private static final JPanel generalPage = Requests.createPanel();

	private static final JPanel modsPage = Modifications.createPanel();
	private static final JPanel messagePage = Messages.createPanel();
	private static final JPanel overlayPage = Outputs.createPanel();
	private static final JPanel accountsPage = Account.createPanel();
	//private static final JPanel commandsPage = CommandSettings.createPanel();
	private static final JPanel requestsPage = Filters.createPanel();
	private static final JPanel shortcutsPage = Keybinds.createPanel();
	private static final JPanel personalizationPage = Personalization.createPanel();
	private static final JPanel blockedPage = BlockedIDs.createPanel();
	private static final JPanel blockedSongIDsPage = BlockedSongIDs.createPanel();

	private static final JPanel blockedUsersPage = BlockedUsers.createPanel();
	private static final JPanel blockedCreatorsPage = BlockedCreators.createPanel();
	private static final JPanel loggedIDsPage = RequestsLog.createPanel();
	private static final JPanel devPage = Developer.createPanel();

	private static final JPanel reportedIDsPage = ReportedIDs.createPanel();

	//private static final JPanel legalPage = Legal.createPanel();
	private static final JPanel privacyPage = Privacy.createPanel();
	private static final JPanel warrantyPage = Warranty.createPanel();
	private static final JPanel termsPage = Terms.createPanel();

	private static final JPanel languagePage = Languages.createPanel();
	private static final LangLabel title = new LangLabel("$SETTINGS_TITLE$");
	private static final JPanel settingsPanel = new JPanel(null);

	private static final JPanel mainPanel = new JPanel();
	private static final JPanel titlePanel = new JPanel();

	private static final JPanel botSection = new TitleSeparator("$BOT_SECTION_TITLE$");
	private static final JPanel GDSection = new TitleSeparator("$GD_SECTION_TITLE$");
	private static final JPanel noticesSection = new TitleSeparator("$NOTICES_SECTION_TITLE$");
	private static final JPanel mediaShareSection = new TitleSeparator("$MEDIA_SHARE_SECTION_TITLE$ (Beta)");
	private static final JPanel userSection = new TitleSeparator("$USER_SECTION_TITLE$");

	public static JButtonUI settingsUI = new JButtonUI(){{
		setBackground(new Color(0,0,0,0));
		setHover(Defaults.COLOR1);
		setSelect(Defaults.COLOR4);
	}};


	private static final SettingsButton requests = createButton("$REQUESTS_SETTINGS$", "\uF26F", () -> {
		generalPage.setVisible(true);
	});
	private static final SettingsButton mods = createButton("$MODS_SETTINGS$", "\uF1B2", () -> {
		modsPage.setVisible(true);
	});
	private static final SettingsButton outputs = createButton("$OUTPUTS_SETTINGS$", "\uF68D", () -> {
		overlayPage.setVisible(true);
	});
	private static final SettingsButton accounts = createButton("$ACCOUNTS_SETTINGS$", "\uF133", () -> {
		accountsPage.setVisible(true);
	});

	private static final SettingsButton filters = createButton("$FILTERS_SETTINGS$", "\uF309", () -> {
		requestsPage.setVisible(true);
	});
	private static final SettingsButton shortcuts = createButton("$SHORTCUTS_SETTINGS$", "\uF105", () -> {
		shortcutsPage.setVisible(true);
	});
	private static final SettingsButton personalization = createButton("$PERSONALIZATION_SETTINGS$", "\uF1B9", () -> {
		personalizationPage.setVisible(true);
	});
	private static final SettingsButton blocked = createButton("$BLOCKED_IDS_SETTINGS$", "\uF313", () -> {
		blockedPage.setVisible(true);
	});
	private static final SettingsButton blockedSongIDs = createButton("$BLOCKED_SONG_IDS_SETTINGS$", "\uF181", () -> {
		blockedSongIDsPage.setVisible(true);
	});
	private static final SettingsButton blockedUsers = createButton("$BLOCKED_USERS_SETTINGS$", "\uF5D2", () -> {
		blockedUsersPage.setVisible(true);
	});
	private static final SettingsButton blockedCreators = createButton("$BLOCKED_CREATORS_SETTINGS$", "\uF5D1", () -> {
		blockedCreatorsPage.setVisible(true);
	});
	private static final SettingsButton loggedIDs = createButton("$LOGGED_IDS_SETTINGS$", "\uF0D6", () -> {
		loggedIDsPage.setVisible(true);
		RequestsLog.loadIDs();
	});
	private static final SettingsButton developer = createButton("$DEVELOPER_SETTINGS$", "\uF114", () -> {
		devPage.setVisible(true);
	});
	private static final SettingsButton reportedIDs = createButton("$REPORTED_ID_SETTINGS$", "\uF04A", () -> {
		reportedIDsPage.setVisible(true);
		ReportedIDs.loadIDs();
	});
	//private static final SettingsButton legal = createButton("$LEGAL_SETTINGS$", null, () -> {
	//	legalPage.setVisible(true);
	//	return null;
	//});
	private static final SettingsButton privacy = createButton("$PRIVACY_SETTINGS$", null, () -> {
		privacyPage.setVisible(true);
	});
	private static final SettingsButton warranty = createButton("$WARRANTY_SETTINGS$", null, () -> {
		warrantyPage.setVisible(true);
	});
	private static final SettingsButton terms = createButton("$TERMS_SETTINGS$", null, () -> {
		termsPage.setVisible(true);
	});
	private static final SettingsButton language = createButton("$LANGUAGE_SETTINGS$", "\uF4F3", () -> {
		languagePage.setVisible(true);
	});
	private static final GridBagConstraints gbc = new GridBagConstraints();

	public static void createPanel() {

		int width = 750;
		int height = 662;

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0, 0, 8, 0);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;

		buttonsScroll.setBounds(0, 60, 198, height);

		buttonsScroll.getViewport().setOpaque(false);
		buttonsScroll.setOpaque(false);

		buttonsScroll.getViewport().setBackground(new Color(0,0,0,0));
		buttonsScroll.setBackground(new Color(0,0,0,0));


		content.setBounds(208, 0, 524, height);

		title.setForeground(Defaults.FOREGROUND_A);
		title.setFont(Defaults.SEGOE_FONT.deriveFont(24f));

		mainPanel.setBounds(0, 0, width - 20, height);
		//mainPanel.setBackground(Defaults.TOP);
		mainPanel.setBackground(Defaults.ACCENT);

		mainPanel.setLayout(null);

		titlePanel.setBounds(0, 20, width - 20, height);
		titlePanel.setBackground(Defaults.COLOR6);
		titlePanel.add(title);

		mainPanel.add(titlePanel);

		buttons.setOpaque(false);
		buttonsParent.setOpaque(false);
		buttons.setBackground(new Color(0,0,0,0));
		buttonsParent.setBackground(new Color(0,0,0,0));

		content.setOpaque(false);
		content.setBackground(new Color(0,0,0,0));
		content.setLayout(null);

		content.add(generalPage);
		if(!Defaults.isMac()) content.add(modsPage);
		content.add(messagePage);
		content.add(overlayPage);
		content.add(accountsPage);
		//content.add(commandsPage);
		content.add(requestsPage);
		content.add(shortcutsPage);
		content.add(personalizationPage);
		content.add(blockedPage);
		content.add(blockedSongIDsPage);
		content.add(blockedUsersPage);
		content.add(blockedCreatorsPage);
		content.add(loggedIDsPage);
		content.add(devPage);
		content.add(reportedIDsPage);
		content.add(languagePage);
		//content.add(legalPage);
		content.add(privacyPage);
		content.add(warrantyPage);
		content.add(termsPage);

		generalPage.setVisible(true);
		modsPage.setVisible(false);
		overlayPage.setVisible(false);
		messagePage.setVisible(false);
		accountsPage.setVisible(false);
		//commandsPage.setVisible(false);
		requestsPage.setVisible(false);
		shortcutsPage.setVisible(false);
		personalizationPage.setVisible(false);
		blockedPage.setVisible(false);
		blockedSongIDsPage.setVisible(false);
		blockedUsersPage.setVisible(false);
		blockedCreatorsPage.setVisible(false);
		loggedIDsPage.setVisible(false);
		devPage.setVisible(false);
		reportedIDsPage.setVisible(false);
		//legalPage.setVisible(false);
		languagePage.setVisible(false);
		privacyPage.setVisible(false);
		warrantyPage.setVisible(false);
		termsPage.setVisible(false);

		requests.setBackground(Defaults.COLOR4);
		requests.setUI(selectUI);
		buttons.add(new JPanel(){{
			setOpaque(false);
			setBackground(new Color(0,0,0,0));
			setPreferredSize(new Dimension(100, 10));
		}}, gbc);

		buttons.add(userSection, gbc);
		buttons.add(accounts, gbc);
		buttons.add(personalization, gbc);
		buttons.add(language, gbc);
		buttons.add(createSeparator(), gbc);
		buttons.add(GDSection, gbc);
		buttons.add(requests, gbc);
		if(!Defaults.isMac()) buttons.add(mods, gbc);
		//buttons.add(messages, gbc);
		buttons.add(filters, gbc);
		buttons.add(outputs, gbc);
		buttons.add(shortcuts, gbc);
		buttons.add(blocked, gbc);
		buttons.add(blockedSongIDs, gbc);
		buttons.add(blockedUsers, gbc);
		buttons.add(blockedCreators, gbc);
		buttons.add(loggedIDs, gbc);
		if(SettingsHandler.getSettings("isDev").asBoolean()) {
			buttons.add(developer, gbc);
		}
		buttons.add(reportedIDs, gbc);
		reportedIDs.setVisible(false);
		//buttons.add(createSeparator(), gbc);
		//buttons.add(mediaShareSection, gbc);
		//buttons.add(mediaShare, gbc);
		//buttons.add(mediaShareKeybinds, gbc);
		buttons.add(createSeparator(), gbc);
		buttons.add(noticesSection, gbc);
		//buttons.add(legal, gbc);
		buttons.add(privacy, gbc);
		buttons.add(warranty, gbc);
		buttons.add(terms, gbc);

		buttonsParent.add(buttons);
		//width 208
		settingsPanel.setOpaque(false);
		settingsPanel.setVisible(false);
		settingsPanel.setBounds(16, 0, width, height);
		settingsPanel.setPreferredSize(new Dimension(width, height));
		settingsPanel.add(buttonsScroll);
		settingsPanel.add(content);
		Window.add(settingsPanel, Assets.settings, () -> click(accounts));
		refreshSettingsButtons();

	}
	public static void showReportedIDsTab(){
		reportedIDs.setVisible(true);
	}

	public static void refreshUI() {
		mainPanel.setBackground(Defaults.ACCENT);

		titlePanel.setBackground(new Color(0,0,0,0));
		title.setForeground(Defaults.FOREGROUND_A);
		buttonsScroll.getVerticalScrollBar().setUI(new ScrollbarUI());
		buttonsScroll.getViewport().setBackground(new Color(0,0,0,0));
		buttonsScroll.setBackground(new Color(0,0,0,0));
		settingsPanel.setBackground(new Color(0,0,0,0));
		selectUI.setBackground(Defaults.COLOR4);
		selectUI.setHover(Defaults.COLOR1);
		selectUI.setSelect(Defaults.COLOR4);

		settingsUI.setBackground(new Color(0,0,0,0));
		settingsUI.setHover(Defaults.COLOR1);
		settingsUI.setSelect(Defaults.COLOR4);

		buttons.setBackground(new Color(0,0,0,0));
		buttonsParent.setBackground(new Color(0,0,0,0));
		content.setBackground(new Color(0,0,0,0));
		for (Component component : buttons.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND_A);
					}
				}
				if (!((JButton) component).getUI().equals(selectUI)) {
					component.setBackground(new Color(0,0,0,0));
					((JButton) component).setOpaque(false);
				}
			}
		}
		((TitleSeparator) GDSection).refreshTextColor();
		((TitleSeparator) userSection).refreshTextColor();
		((TitleSeparator) botSection).refreshTextColor();
		((TitleSeparator) noticesSection).refreshTextColor();
		((TitleSeparator) mediaShareSection).refreshTextColor();
	}


	public static void resize(int width, int height){
		settingsPanel.setBounds(16, 0, width, height);
		buttonsScroll.setBounds(0, 0, 198, height-40);

		content.setBounds(198, 0, width, height);
	}

	private static JPanel createSeparator(){

		JPanel panel = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Defaults.FOREGROUND_B);
				g2.drawLine(14, 10, 170,10);
				super.paintComponent(g);
			}
		};
		panel.setOpaque(false);
		panel.setBackground(new Color(0,0,0,0));
		panel.setPreferredSize(new Dimension(170,16));

		return panel;
	}

	private static class TitleSeparator extends JPanel {

		private final LangLabel label = new LangLabel("");

		public TitleSeparator(String title) {
			setLayout(null);
			label.setTextLang(title);
			label.setFont(Defaults.MAIN_FONT.deriveFont(12f));
			label.setForeground(Defaults.FOREGROUND_A);
			label.setBounds(14, 0, 170, 30);
			setPreferredSize(new Dimension(170, 30));
			setOpaque(false);
			setBackground(new Color(0, 0, 0, 0));
			add(label);
		}
		public void resizeText(float size){
			label.setFont(Defaults.MAIN_FONT.deriveFont(size));
		}

		public void refreshTextColor(){
			label.setForeground(Defaults.FOREGROUND_A);
		}

	}

	public static void refreshSettingsButtons(){
		String language = SettingsHandler.getSettings("language").asString();
		for(Component component : buttons.getComponents()){
			if(component instanceof SettingsButton){
				switch (language){
					case "fr_fr":
					case "pt_br":
					case "es_es":
						((SettingsButton) component).resizeText(12f);
						break;
					default:
						((SettingsButton) component).resizeText(14f);
						break;
				}
			}
			if(component instanceof TitleSeparator){
				switch (language){
					case "fr_fr":
					case "pt_br":
						((TitleSeparator) component).resizeText(10f);
						break;
					default:
						((TitleSeparator) component).resizeText(12f);
						break;
				}
			}
		}
	}

	private static class SettingsButton extends CurvedButton {

		private final Function method;
		private final LangLabel label;


		SettingsButton(String text, String icon, Function method){
			super("");
			this.method = method;

			label = new LangLabel(text);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			if(icon == null) label.setBounds(15, 7, 208, 20);
			else label.setBounds(40, 7, 208, 20);
			label.setForeground(Defaults.FOREGROUND_A);
			label.setFont(Defaults.MAIN_FONT.deriveFont(14f));

			LangLabel iconLabel = new LangLabel(icon);

			iconLabel.setFont(Defaults.SYMBOLS.deriveFont(14f));
			iconLabel.setBounds(15, 6, 20, 20);
			iconLabel.setForeground(Defaults.FOREGROUND_A);

			setLayout(null);
			add(label);
			if(icon != null) add(iconLabel);
			setBackground(new Color(0,0,0,0));
			setUI(selectUI);
			setForeground(Defaults.FOREGROUND_A);
			setBorder(BorderFactory.createEmptyBorder());
			setPreferredSize(new Dimension(180, 32));
			setOpaque(false);
			addActionListener(e -> runMethod());
		}

		public void resizeText(float size){
			label.setFont(Defaults.MAIN_FONT.deriveFont(size));
		}

		public void runMethod(){
			RequestsLog.clear();
			for (Component componentA : content.getComponents()) {
				if (componentA instanceof JPanel) {
					componentA.setVisible(false);
				}
			}
			try {
				method.run();
			} catch (Exception e) {
				Main.logger.error(e.getLocalizedMessage(), e);
			}
			for (CurvedButton component : buttons) {
				if (component instanceof SettingsButton) {
					component.setUI(settingsUI);
					component.setBackground(new Color(0,0,0,0));
				}
			}
			setUI(selectUI);
			setBackground(new ThemedColor("color1", this, ThemedColor.BACKGROUND));
			setOpaque(false);
		}

	}

	private static SettingsButton createButton(String text, String icon, Function method) {
		return new SettingsButton(text, icon, method);
	}


	static void click(SettingsButton button) {
		for (Component componentA : content.getComponents()) {
			if (componentA instanceof JPanel) {
				componentA.setVisible(false);
			}
		}
		RequestsLog.clear();
		button.runMethod();
	}
}
