package com.alphalaneous.Tabs;

import com.alphalaneous.Images.Assets;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Settings.ChannelPoints;
import com.alphalaneous.Settings.Chatbot;
import com.alphalaneous.Settings.Logs.RequestsLog;
import com.alphalaneous.Settings.SpamProtection;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Tabs.ChatbotPages.*;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ChatbotTab {
	public static JPanel window = new JPanel();
	private static final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
	private static final JScrollPane buttonsScroll = new SmoothScrollPane(buttons);
	private static final JPanel content = new JPanel();
	private static final JPanel generalBotPage = Chatbot.createPanel();
	private static final JPanel customCommandsPage = CustomCommands.createPanel();
	private static final JPanel defaultCommandsPage = DefaultCommands.createPanel();
	private static final JPanel timersPage = TimerSettings.createPanel();

	private static final JPanel pointsPage = ChannelPoints.createPanel();
	private static final JPanel spamProtectionPage = SpamProtection.createPanel();
	private static final JPanel customKeywordsPage = CustomKeywords.createPanel();
	private static final JPanel customCheerActionsPage = CustomCheerActions.createPanel();

	private static final JPanel blockedKeywordsPage = BlockedKeywords.createPanel();

	private static final JPanel chatbotPanel = new JPanel(null);

	private static final JPanel commandsSection = new TitleSeparator("$COMMANDS_SECTION_TITLE$");
	private static final JPanel chatSection = new TitleSeparator("$CHAT_SECTION_TITLE$");


	private static final FunctionButton generalBotButton = createButton("$CHATBOT_SETTINGS$", "\uF130", () -> {
		generalBotPage.setVisible(true);
	});
	private static final FunctionButton customCommandsButton = createButton("$CUSTOM_COMMANDS_SETTINGS$", "\uF03C", () -> {
		CustomCommands.loadCommands();
		customCommandsPage.setVisible(true);
	});
	private static final FunctionButton defaultCommandsButton = createButton("$DEFAULT_COMMANDS_SETTINGS$", "\uF4EA", () -> {
		com.alphalaneous.Tabs.ChatbotPages.DefaultCommands.loadCommands();
		defaultCommandsPage.setVisible(true);
	});
	private static final FunctionButton channelPointsButton = createButton("$CHANNEL_POINTS_SETTINGS$", "\uF52B", () -> {
		pointsPage.setVisible(true);
	});
	private static final FunctionButton timersButton = createButton("$TIMERS_SETTINGS$", "\uF210", () -> {
		TimerSettings.loadTimers();
		timersPage.setVisible(true);
	});
	private static final FunctionButton spamProtectionButton = createButton("$SPAM_PROTECTION_SETTINGS$", "\uF02D", () -> {
		spamProtectionPage.setVisible(true);
	});
	private static final FunctionButton customKeywordsButton = createButton("$CUSTOM_KEYWORDS_SETTINGS$", "\uF0B3", () -> {
		CustomKeywords.loadKeywords();
		customKeywordsPage.setVisible(true);
	});
	private static final FunctionButton customCheerActionsButton = createButton("$CUSTOM_CHEER_ACTIONS_SETTINGS$", "\uF259", () -> {
		CustomCheerActions.loadCheerActions();
		customCheerActionsPage.setVisible(true);
	});
	private static final FunctionButton blockedKeywordsButton = createButton("$BLOCKED_KEYWORDS_SETTINGS$", "\uF0AB", () -> {
		blockedKeywordsPage.setVisible(true);
	});

	public static final JButtonUI selectUI = new JButtonUI(){{
		setBackground(Defaults.COLOR4);
		setHover(Defaults.COLOR1);
		setSelect(Defaults.COLOR4);
	}};


	public static void createPanel() {

		int width = 750;
		int height = 662;

		buttonsScroll.setBounds(0, 60, 198, height-98);
		buttonsScroll.getViewport().setBackground(new Color(0,0,0,0));
		buttonsScroll.setBackground(new Color(0,0,0,0));
		buttonsScroll.setOpaque(false);
		buttonsScroll.getViewport().setOpaque(false);

		content.setBounds(208, 0, 524, height);

		channelPointsButton.setIcon(Assets.channelPoints);

		buttons.setOpaque(false);
		buttons.setBackground(new Color(0,0,0,0));

		content.setOpaque(false);
		content.setBackground(new Color(0,0,0,0));
		content.setLayout(null);

		buttons.add(new JPanel(){{
			setOpaque(false);
			setBackground(new Color(0,0,0,0));
			setPreferredSize(new Dimension(100, 10));
		}});

		buttons.add(generalBotButton);
		buttons.add(createSeparator());
		buttons.add(commandsSection);
		buttons.add(defaultCommandsButton);
		buttons.add(customCommandsButton);
		buttons.add(customKeywordsButton);

		buttons.add(createSeparator());
		buttons.add(chatSection);
		buttons.add(timersButton);
		buttons.add(customCheerActionsButton);
		buttons.add(channelPointsButton);
		buttons.add(spamProtectionButton);
		//buttons.add(blockedKeywordsButton);

		content.add(generalBotPage);
		content.add(customCommandsPage);
		content.add(defaultCommandsPage);
		content.add(timersPage);
		content.add(pointsPage);
		content.add(spamProtectionPage);
		content.add(blockedKeywordsPage);
		content.add(customKeywordsPage);
		content.add(customCheerActionsPage);

		buttons.setPreferredSize(new Dimension(208, 400));

		chatbotPanel.setOpaque(false);
		chatbotPanel.setVisible(false);
		chatbotPanel.setBounds(16, 0, width, height);
		chatbotPanel.setPreferredSize(new Dimension(width, height));
		chatbotPanel.add(buttonsScroll);
		chatbotPanel.add(content);

		Window.add(chatbotPanel, Assets.commands, () -> click(generalBotButton));
		refreshSettingsButtons();
	}

	public static void refreshUI() {
		buttonsScroll.getVerticalScrollBar().setUI(new ScrollbarUI());
		selectUI.setBackground(Defaults.COLOR4);
		selectUI.setHover(Defaults.COLOR1);
		selectUI.setSelect(Defaults.COLOR4);

		for (Component component : buttons.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND_A);
					}
				}
				if(((JButton) component).getUI() != null) {
					if (!((JButton) component).getUI().equals(selectUI)) {
						component.setBackground(new Color(0, 0, 0, 0));
					} else {
						component.setBackground(new Color(255, 255, 255, 20));
					}
				}

			}
		}
		FunctionButton.refreshAll();
		((TitleSeparator) commandsSection).refreshTextColor();
		((TitleSeparator) chatSection).refreshTextColor();
	}


	public static void resize(int width, int height){

		chatbotPanel.setBounds(16, 0, width, height);
		buttonsScroll.setBounds(0, 0, 198, height-38);

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

		public void refreshTextColor(){
			label.setForeground(Defaults.FOREGROUND_A);
		}
		public void resizeText(float size){
			label.setFont(Defaults.MAIN_FONT.deriveFont(size));
		}
	}


	public static void refreshSettingsButtons(){
		String language = SettingsHandler.getSettings("language").asString();
		for(Component component : buttons.getComponents()){
			if(component instanceof FunctionButton){
				switch (language){
					case "fr_fr":
					case "pt_br":
					case "es_es":
						((FunctionButton) component).resizeText(12f);
						break;
					default:
						((FunctionButton) component).resizeText(14f);
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

	private static class FunctionButton extends CurvedButton {

		private final Function function;
		private ImageIcon icon;
		private final LangLabel iconLabel;
		private static final ArrayList<FunctionButton> functionButtons = new ArrayList<>();
		private final LangLabel label;

		FunctionButton(String text, String icon, Function function){
			super("");
			this.function = function;

			label = new LangLabel(text);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			label.setBounds(40, 7, 208, 20);
			label.setForeground(Defaults.FOREGROUND_A);

			iconLabel = new LangLabel(icon);

			iconLabel.setFont(Defaults.SYMBOLS.deriveFont(14f));
			iconLabel.setBounds(15, 6, 20, 20);
			iconLabel.setForeground(Defaults.FOREGROUND_A);

			setLayout(null);
			add(label);
			add(iconLabel);
			setBackground(new Color(255,255,255,20));
			setUI(selectUI);
			setForeground(Defaults.FOREGROUND_A);
			setBorder(BorderFactory.createEmptyBorder());
			setPreferredSize(new Dimension(180, 32));
			addActionListener(e -> runMethod());
			functionButtons.add(this);
		}
		public void setIcon(ImageIcon icon){
			this.icon = new ImageIcon(icon.getImage().getScaledInstance(25,25, Image.SCALE_SMOOTH));
			iconLabel.setBounds(10, 6, 25, 25);
			if(!Defaults.isLight) iconLabel.setIcon(this.icon);
			else iconLabel.setIcon(invertImage(this.icon));

		}

		public void resizeText(float size){
			label.setFont(Defaults.MAIN_FONT.deriveFont(size));
		}

		public void refreshUI(){
			if(icon != null){
				if(!Defaults.isLight) iconLabel.setIcon(icon);
				else iconLabel.setIcon(invertImage(icon));
			}
		}
		public static void refreshAll(){
			for(FunctionButton button : functionButtons){
				button.refreshUI();
			}
		}
		public void runMethod(){
			for (Component componentA : content.getComponents()) {
				if (componentA instanceof JPanel) {
					componentA.setVisible(false);
				}
			}
			try {
				function.run();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			for (CurvedButton component : buttons) {
				if (component instanceof FunctionButton) {
					component.setUI(SettingsTab.settingsUI);
					component.setBackground(new Color(0,0,0,0));
				}
			}

			setUI(SettingsTab.selectUI);
			setBackground(Defaults.COLOR4);
			setOpaque(false);

		}
		private static ImageIcon invertImage(ImageIcon buttonIcon) {
			BufferedImage img = new BufferedImage(
					buttonIcon.getIconWidth(),
					buttonIcon.getIconHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.createGraphics();
			buttonIcon.paintIcon(null, g, 0,0);
			g.dispose();

			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					int rgba = img.getRGB(x, y);
					Color col = new Color(rgba, true);
					col = new Color(255 - col.getRed(),
							255 - col.getGreen(),
							255 - col.getBlue(), col.getAlpha());
					img.setRGB(x, y, col.getRGB());
				}
			}
			return new ImageIcon(img);
		}
	}
	private static FunctionButton createButton(String text, String icon, Function function) {
		return new FunctionButton(text, icon, function);
	}


	static void click(FunctionButton button) {
		for (Component componentA : content.getComponents()) {
			if (componentA instanceof JPanel) {
				componentA.setVisible(false);
			}
		}
		RequestsLog.clear();
		button.runMethod();
	}
}
