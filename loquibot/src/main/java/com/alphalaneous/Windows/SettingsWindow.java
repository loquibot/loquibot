package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Components.*;
import com.alphalaneous.SettingsPanels.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static com.alphalaneous.Defaults.defaultUI;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class SettingsWindow {
	public static JPanel window = new JPanel();
	public static boolean run = true;
	private static final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
	private static final JScrollPane buttonsScroll = new SmoothScrollPane(buttons);
	private static final JPanel content = new JPanel();
	private static final JPanel blankSpace = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
	private static final JButtonUI selectUI = new JButtonUI();
	private static final JPanel generalPage = RequestsSettings.createPanel();
	private static final JPanel generalBotPage = ChatbotSettings.createPanel();
	private static final JPanel overlayPage = OutputSettings.createPanel();
	private static final JPanel accountsPage = AccountSettings.createPanel();
	private static final JPanel commandsPage = CommandSettings.createPanel();
	private static final JPanel pointsPage = ChannelPointSettings.createPanel();
	private static final JPanel requestsPage = FiltersSettings.createPanel();
	private static final JPanel shortcutsPage = ShortcutSettings.createPanel();
	private static final JPanel personalizationPage = PersonalizationSettings.createPanel();
	private static final JPanel blockedPage = BlockedSettings.createPanel();
	private static final JPanel blockedUsersPage = BlockedUserSettings.createPanel();
	private static final JPanel blockedCreatorsPage = BlockedCreatorSettings.createPanel();
	private static final JPanel loggedIDsPage = RequestsLog.createPanel();
	private static final JPanel languagePage = LanguageSettings.createPanel();
	private static final JPanel chaosModePage = ChaosModeSettings.createPanel();
	private static final LangLabel title = new LangLabel("$SETTINGS_TITLE$");
	private static final JPanel settingsPanel = new JPanel(null);

	private static final JPanel mainPanel = new JPanel();
	private static final JPanel titlePanel = new JPanel();
	private static final JPanel settingsButtons = new JPanel();

	private static final JPanel botSection = new TitleSeparator("$BOT_SECTION_TITLE$");
	private static final JPanel GDSection = new TitleSeparator("$GD_SECTION_TITLE$");
	private static final JPanel userSection = new TitleSeparator("$USER_SECTION_TITLE$");

	private static final SettingsButton requests = createButton("$REQUESTS_SETTINGS$", "\uF26F", () -> {
		generalPage.setVisible(true);
		return null;
	});
	private static final SettingsButton generalBot = createButton("$CHATBOT_SETTINGS$", "\uF130", () -> {
		generalBotPage.setVisible(true);
		return null;
	});
	private static final SettingsButton chaosMode = createButton("$CHAOS_SETTINGS$", "\uF6A4", () -> {
		chaosModePage.setVisible(true);
		return null;
	});
	private static final SettingsButton outputs = createButton("$OUTPUTS_SETTINGS$", "\uF68D", () -> {
		overlayPage.setVisible(true);
		return null;
	});
	private static final SettingsButton accounts = createButton("$ACCOUNTS_SETTINGS$", "\uF133", () -> {
		accountsPage.setVisible(true);
		return null;
	});
	private static final SettingsButton commands = createButton("$COMMANDS_SETTINGS$", "\uF0B4", () -> {
		commandsPage.setVisible(true);
		return null;
	});
	private static final SettingsButton points = createButton("$CHANNEL_POINTS_SETTINGS$", "\uF52F", () -> {
		pointsPage.setVisible(true);
		return null;
	});
	private static final SettingsButton cheers = createButton("$CHEERS_SETTINGS$", "\uF157", () -> {
		//cheersPage.setVisible(true);
		return null;
	});
	private static final SettingsButton filters = createButton("$FILTERS_SETTINGS$", "\uF309", () -> {
		requestsPage.setVisible(true);
		return null;
	});
	private static final SettingsButton shortcuts = createButton("$SHORTCUTS_SETTINGS$", "\uF105", () -> {
		shortcutsPage.setVisible(true);
		return null;
	});
	private static final SettingsButton personalization = createButton("$PERSONALIZATION_SETTINGS$", "\uF1B9", () -> {
		personalizationPage.setVisible(true);
		return null;
	});
	private static final SettingsButton blocked = createButton("$BLOCKED_IDS_SETTINGS$", "\uF313", () -> {
		blockedPage.setVisible(true);
		return null;
	});
	private static final SettingsButton blockedUsers = createButton("$BLOCKED_USERS_SETTINGS$", "\uF5D2", () -> {
		blockedUsersPage.setVisible(true);
		return null;
	});
	private static final SettingsButton blockedCreators = createButton("$BLOCKED_CREATORS_SETTINGS$", "\uF5D1", () -> {
		blockedCreatorsPage.setVisible(true);
		return null;
	});
	private static final SettingsButton loggedIDs = createButton("$LOGGED_IDS_SETTINGS$", "\uF0D6", () -> {
		loggedIDsPage.setVisible(true);
		new Thread(() -> {
			File file = new File(Defaults.saveDirectory + "\\GDBoard\\requestsLog.txt");
			if (file.exists()) {
				Scanner sc = null;
				try {
					sc = new Scanner(file);
				} catch (FileNotFoundException f) {
					f.printStackTrace();
				}
				assert sc != null;
				while (sc.hasNextLine()) {
					RequestsLog.addButton(Long.parseLong(sc.nextLine().split(",")[0]));
				}
				sc.close();
			}
		}).start();
		return null;
	});
	private static final SettingsButton language = createButton("$LANGUAGE_SETTINGS$", "\uE12B", () -> {
		languagePage.setVisible(true);
		return null;
	});

	public static void createPanel() {

		int width = 750;
		int height = 662;

		blankSpace.setBounds(0, 0, 208, 60);
		buttonsScroll.setBounds(0, 60, 208, height-98);
		buttonsScroll.getViewport().setBackground(Defaults.MAIN);
		buttonsScroll.setBackground(Defaults.MAIN);


		content.setBounds(208, 0, 542, height);

		blankSpace.setBackground(Defaults.MAIN);


		title.setForeground(Defaults.FOREGROUND);
		title.setFont(Defaults.SEGOE_FONT.deriveFont(24f));

		mainPanel.setBounds(0, 0, width - 20, height);
		//mainPanel.setBackground(Defaults.TOP);
		mainPanel.setBackground(Defaults.ACCENT);

		mainPanel.setLayout(null);

		titlePanel.setBounds(0, 20, width - 20, height);
		titlePanel.setBackground(Defaults.TOP);
		titlePanel.add(title);


		settingsButtons.setBackground(Defaults.TOP);
		settingsButtons.setBounds(50, 80, width - 100, height - 100);

		mainPanel.add(settingsButtons);
		mainPanel.add(titlePanel);

		buttons.setBackground(Defaults.MAIN);


		content.setBackground(Defaults.SUB_MAIN);
		content.setLayout(null);

		content.add(generalPage);
		content.add(chaosModePage);
		content.add(generalBotPage);
		content.add(overlayPage);
		content.add(accountsPage);
		content.add(commandsPage);
		content.add(pointsPage);
		content.add(requestsPage);
		content.add(shortcutsPage);
		content.add(personalizationPage);
		content.add(blockedPage);
		content.add(blockedUsersPage);
		content.add(blockedCreatorsPage);
		content.add(loggedIDsPage);
		content.add(languagePage);

		generalPage.setVisible(true);
		overlayPage.setVisible(false);
		accountsPage.setVisible(false);
		commandsPage.setVisible(false);
		pointsPage.setVisible(false);
		requestsPage.setVisible(false);
		shortcutsPage.setVisible(false);
		personalizationPage.setVisible(false);
		blockedPage.setVisible(false);
		blockedUsersPage.setVisible(false);
		blockedCreatorsPage.setVisible(false);
		loggedIDsPage.setVisible(false);
		languagePage.setVisible(false);
		chaosModePage.setVisible(false);


		JButton home = createButton("$BACK_BUTTON$", "\uF31E", () -> {
			Window.showMainPanel();
			settingsPanel.setVisible(false);
			return null;
		});
		blankSpace.add(home);


		requests.setBackground(Defaults.SELECT);
		requests.setUI(selectUI);

		buttons.add(botSection);
		buttons.add(generalBot);
		buttons.add(commands);
		buttons.add(points);
		//buttons.add(cheers);

		buttons.add(createSeparator());
		buttons.add(GDSection);
		buttons.add(requests);
		//buttons.add(messages);
		buttons.add(filters);
		buttons.add(chaosMode);
		buttons.add(outputs);
		buttons.add(shortcuts);
		buttons.add(blocked);
		buttons.add(blockedUsers);
		buttons.add(blockedCreators);
		buttons.add(loggedIDs);

		buttons.add(createSeparator());
		buttons.add(userSection);
		buttons.add(accounts);
		buttons.add(personalization);
		//buttons.add(language); //

		buttons.setPreferredSize(new Dimension(208, 680));

		settingsPanel.setVisible(false);
		settingsPanel.setBounds(settingsPanel.getWidth()/2-width/2, 0, width, height);
		settingsPanel.setPreferredSize(new Dimension(width, height));
		settingsPanel.add(blankSpace);
		settingsPanel.add(buttonsScroll);
		settingsPanel.add(content);
		Window.windowLayeredPane.add(settingsPanel);

	}

	public static void refreshUI() {
		blankSpace.setBackground(Defaults.MAIN);
		mainPanel.setBackground(Defaults.ACCENT);

		titlePanel.setBackground(Defaults.TOP);
		title.setForeground(Defaults.FOREGROUND);
		settingsButtons.setBackground(Defaults.TOP);
		buttonsScroll.getVerticalScrollBar().setUI(new ScrollbarUI());
		buttonsScroll.getViewport().setBackground(Defaults.MAIN);
		buttonsScroll.setBackground(Defaults.MAIN);
		selectUI.setBackground(Defaults.SELECT);
		selectUI.setHover(Defaults.BUTTON_HOVER);
		selectUI.setSelect(Defaults.SELECT);
		buttons.setBackground(Defaults.MAIN);
		content.setBackground(Defaults.SUB_MAIN);
		for (Component component : buttons.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				if (!((JButton) component).getUI().equals(selectUI)) {
					component.setBackground(Defaults.MAIN);
				} else {
					component.setBackground(Defaults.SELECT);
				}

			}
		}
		for (Component component : blankSpace.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				if (!((JButton) component).getUI().equals(selectUI)) {
					component.setBackground(Defaults.MAIN);
				} else {
					component.setBackground(Defaults.SELECT);
				}

			}
		}
		((TitleSeparator) GDSection).refreshTextColor();
		((TitleSeparator) userSection).refreshTextColor();
		((TitleSeparator) botSection).refreshTextColor();
	}


	public static void resize(int width, int height){

		settingsPanel.setBounds(width/2-settingsPanel.getWidth()/2, 0, settingsPanel.getWidth(), height);
		buttonsScroll.setBounds(0, 60, 208, height-98);

		content.setBounds(208, 0, 542, height);

	}

	public static void showSettings() {
		click(requests);
		settingsPanel.setVisible(true);
	}

	private static JPanel createSeparator(){

		JPanel panel = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Defaults.FOREGROUND2);
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
			label.setForeground(Defaults.FOREGROUND);
			label.setBounds(14, 0, 170, 30);
			setPreferredSize(new Dimension(170, 30));
			setOpaque(false);
			setBackground(new Color(0, 0, 0, 0));
			add(label);
		}

		public void refreshTextColor(){
			label.setForeground(Defaults.FOREGROUND);
		}

	}
	private static class SettingsButton extends CurvedButtonAlt {

		private final Callable<Void> method;
		private final String text;

		SettingsButton(String text, String icon, Callable<Void> method){
			super("");
			this.text = text;
			this.method = method;
			selectUI.setBackground(Defaults.SELECT);
			selectUI.setHover(Defaults.BUTTON_HOVER);

			LangLabel label = new LangLabel(text);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			label.setBounds(40, 7, 208, 20);
			label.setForeground(Defaults.FOREGROUND);

			LangLabel iconLabel = new LangLabel(icon);

			iconLabel.setFont(Defaults.SYMBOLS.deriveFont(14f));
			iconLabel.setBounds(15, 6, 20, 20);
			iconLabel.setForeground(Defaults.FOREGROUND);

			setLayout(null);
			add(label);
			add(iconLabel);
			setBackground(Defaults.MAIN);
			setUI(defaultUI);
			setForeground(Defaults.FOREGROUND);
			setBorder(BorderFactory.createEmptyBorder());
			setPreferredSize(new Dimension(170, 32));

			addActionListener(e -> runMethod());
		}
		public void runMethod(){
			RequestsLog.clear();
			for (Component componentA : content.getComponents()) {
				if (componentA instanceof JPanel) {
					componentA.setVisible(false);
				}
			}
			try {
				method.call();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			for (Component component : buttons.getComponents()) {
				if (component instanceof SettingsButton) {
					((JButton) component).setUI(defaultUI);
					component.setBackground(Defaults.MAIN);
				}
			}
			if (!text.equalsIgnoreCase("$BACK_BUTTON$")) {
				setUI(selectUI);
				setBackground(Defaults.SELECT);
			}
		}

	}

	private static SettingsButton createButton(String text, String icon, Callable<Void> method) {
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
