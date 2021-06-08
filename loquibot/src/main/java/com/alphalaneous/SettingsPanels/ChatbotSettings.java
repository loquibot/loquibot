package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Defaults;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.Settings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class ChatbotSettings {
	public static boolean silentOption = false;
	public static boolean multiOption = true;
	public static boolean antiDox = true;
	public static int cooldown = 0;
	private static final JPanel panel = new JPanel(null);
	private static final LangLabel versionLabel = new LangLabel("");

	private static final ThemedCheckbox silentChatMode = createButton("$SILENT_MODE$", 110);
	private static final ThemedCheckbox multiThreadMode = createButton("$MULTI_THREAD$", 140);
	private static final ThemedCheckbox antiDoxMode = createButton("$ANTI_DOX$", 200);
	private static final LangLabel antiDoxInfo = new LangLabel("<html> $ANTI_DOX_INFO$ </html>");
	private static final LangLabel multiThreadInfo = new LangLabel("<html> $MULTI_THREAD_INFO$ </html>");
	private static final LangLabel cooldownLabel = new LangLabel("$GLOBAL_COOLDOWN_LABEL$");

	private static final FancyTextArea globalCooldownInput = new FancyTextArea(true, false);


	public static JPanel createPanel() {

		InputStream is;
		try {
			is = new FileInputStream(Defaults.saveDirectory + "\\GDBoard\\version.txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			versionLabel.setTextLangFormat("$LOQUIBOT_VERSION$", br.readLine().replaceAll("version=", ""));

		} catch (IOException e) {
			versionLabel.setTextLangFormat("$LOQUIBOT_VERSION$", "unknown");
		}

		versionLabel.setForeground(Defaults.FOREGROUND2);
		versionLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		versionLabel.setBounds(25, 70, 490, 40);

		antiDoxInfo.setForeground(Defaults.FOREGROUND2);
		antiDoxInfo.setFont(Defaults.MAIN_FONT.deriveFont(12f));
		antiDoxInfo.setBounds(25, 225, 490, 40);

		multiThreadInfo.setForeground(Defaults.FOREGROUND2);
		multiThreadInfo.setFont(Defaults.MAIN_FONT.deriveFont(12f));
		multiThreadInfo.setBounds(25, 165, 490, 40);

		cooldownLabel.setForeground(Defaults.FOREGROUND);
		cooldownLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		cooldownLabel.setBounds(25, 260, 490, 40);

		silentChatMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				silentOption = silentChatMode.getSelectedState();
				Settings.writeSettings("silentMode", String.valueOf(silentOption));

			}
		});
		multiThreadMode.setChecked(true);
		multiThreadMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				multiOption = multiThreadMode.getSelectedState();
				Settings.writeSettings("multiMode", String.valueOf(multiOption));
			}
		});
		antiDoxMode.setChecked(true);
		antiDoxMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				antiDox = antiDoxMode.getSelectedState();
				Settings.writeSettings("antiDox", String.valueOf(antiDox));
			}
		});
		globalCooldownInput.setBounds(25, 295, 490, 32);
		globalCooldownInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		globalCooldownInput.setText("0");
		globalCooldownInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					cooldown = Integer.parseInt(globalCooldownInput.getText());
				} catch (NumberFormatException f) {
					cooldown = 0;
				}
				Settings.writeSettings("globalCooldown", String.valueOf(cooldown));
			}
		});

		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 622);
		panel.setBackground(Defaults.SUB_MAIN);

		panel.add(new SettingsTitle("$CHATBOT_SETTINGS$"));
		panel.add(versionLabel);
		panel.add(silentChatMode);
		panel.add(multiThreadMode);
		panel.add(antiDoxMode);
		panel.add(antiDoxInfo);
		panel.add(multiThreadInfo);
		panel.add(globalCooldownInput);
		panel.add(cooldownLabel);
		return panel;

	}

	private static ThemedCheckbox createButton(String text, int y) {
		ThemedCheckbox button = new ThemedCheckbox(text);
		button.setBounds(25, y, 490, 30);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.refresh();
		return button;
	}

	public static void loadSettings() {

		if (!Settings.getSettings("silentMode").asString().equalsIgnoreCase("")) {
			silentOption = Settings.getSettings("silentMode").asBoolean();
			silentChatMode.setChecked(silentOption);
		}
		if (!Settings.getSettings("antiDox").asString().equalsIgnoreCase("")) {
			antiDox = Settings.getSettings("antiDox").asBoolean();
			antiDoxMode.setChecked(antiDox);
		}
		if (!Settings.getSettings("multiMode").asString().equalsIgnoreCase("")) {
			multiOption = Settings.getSettings("multiMode").asBoolean();
			multiThreadMode.setChecked(multiOption);
		}
		if (!Settings.getSettings("globalCooldown").asString().equalsIgnoreCase("")) {
			cooldown = Settings.getSettings("globalCooldown").asInteger();
			globalCooldownInput.setText(String.valueOf(cooldown));
		}
	}

	public static void refreshUI() {
		panel.setBackground(Defaults.SUB_MAIN);
		versionLabel.setForeground(Defaults.FOREGROUND2);
		antiDoxInfo.setForeground(Defaults.FOREGROUND2);
		multiThreadInfo.setForeground(Defaults.FOREGROUND2);
		cooldownLabel.setForeground(Defaults.FOREGROUND);
	}
}
