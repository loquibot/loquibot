package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Components.ScrollbarUI;
import com.alphalaneous.Components.SmoothScrollPane;
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

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class RequestsSettings {
	public static volatile boolean gdModeOption = true;
	public static volatile boolean lowCPUMode = false;
	public static volatile boolean followersOption = false;
	public static volatile boolean subsOption = false;
	public static volatile boolean repeatedOption = false;
	public static volatile boolean repeatedOptionAll = false;
	public static volatile boolean updatedRepeatedOption = false;
	public static volatile boolean nowPlayingOption = false;
	public static volatile boolean queueFullOption = false;
	public static volatile boolean confirmOption = false;
	public static volatile boolean confirmWhisperOption = false;
	public static volatile boolean autoDownloadOption = false;
	public static volatile boolean queueLimitBoolean = false;
	public static volatile boolean userLimitOption = false;
	public static volatile boolean userLimitStreamOption = false;
	public static volatile boolean streamerBypassOption = false;
	public static volatile boolean modsBypassOption = false;
	public static volatile boolean disableShowPositionOption = true;
	public static int queueLimit = 0;
	public static int userLimit = 0;
	public static int userLimitStream = 0;
	public static int queueLevelLength = 10;
	public static ThemedCheckbox deathMessage = createButton("Death Messages", 826);
	private static final ThemedCheckbox gdMode = createButton("$GD_MODE$", 75);
	private static final ThemedCheckbox followers = createButton("$FOLLOWERS_ONLY$", 105);
	private static final ThemedCheckbox subOnly = createButton("$SUBSCRIBERS_ONLY$", 135);
	private static final ThemedCheckbox nowPlaying = createButton("$DISABLE_NOW_PLAYING$", 165);
	private static final ThemedCheckbox queueFull = createButton("$DISABLE_QUEUE_FULL$", 195);
	private static final ThemedCheckbox confirmWhisper = createButton("$WHISPER_CONFIRMATION$", 225);
	private static final ThemedCheckbox confirm = createButton("$DISABLE_CONFIRMATION$", 255);
	private static final ThemedCheckbox disableShowPosition = createButton("$DISABLE_SHOW_POSITION$", 285);
	private static final ThemedCheckbox repeated = createButton("$DISABLE_REPEATED$", 315);
	private static final ThemedCheckbox repeatedAll = createButton("$DISABLE_REPEATED_ALL$", 345);
	private static final ThemedCheckbox allowUpdatedRepeated = createButton("$ALLOW_UPDATED_REPEATED$", 375);
	private static final ThemedCheckbox autoDownload = createButton("$AUTOMATIC_SONG_DOWNLOADS$", 405);
	private static final ThemedCheckbox lowCPU = createButton("$LOW_CPU_MODE$", 435);
	private static final ThemedCheckbox streamerBypass = createButton("$STREAMER_BYPASS$", 465);
	private static final ThemedCheckbox modsBypass = createButton("$MODS_BYPASS$", 495);
	private static final ThemedCheckbox queueLimitText = createButton("$MAX_QUEUE_SIZE$", 525);
	private static final ThemedCheckbox userLimitText = createButton("$REQUEST_LIMIT_QUEUE$", 600);
	private static final ThemedCheckbox userLimitStreamText = createButton("$STREAM_REQUEST_LIMIT$", 675);
	private static final FancyTextArea queueSizeInput = new FancyTextArea(true, false);
	private static final FancyTextArea userLimitInput = new FancyTextArea(true, false);
	private static final FancyTextArea userLimitStreamInput = new FancyTextArea(true, false);
	private static final LangLabel queueCommandLabel = new LangLabel("$QUEUE_COMMAND_LABEL$");
	private static final FancyTextArea queueCommandLength = new FancyTextArea(true, false);
	private static final JPanel mainPanel = new JPanel(null);
	private static final JPanel panel = new JPanel();
	private static final JScrollPane scrollPane = new SmoothScrollPane(panel);


	public static JPanel createPanel() {


		panel.setLayout(null);
		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 870);
		panel.setPreferredSize(new Dimension(542, 870));
		panel.setBackground(Defaults.SUB_MAIN);

		gdMode.setChecked(true);
		gdMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				gdModeOption = gdMode.getSelectedState();
				CommandSettings.refresh();

			}
		});

		followers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				followersOption = followers.getSelectedState();
			}
		});

		subOnly.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				subsOption = subOnly.getSelectedState();
			}
		});

		nowPlaying.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				nowPlayingOption = nowPlaying.getSelectedState();
			}
		});
		queueFull.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				queueFullOption = queueFull.getSelectedState();
			}
		});
		confirmWhisper.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				confirmWhisperOption = confirmWhisper.getSelectedState();
			}
		});

		confirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				confirmOption = confirm.getSelectedState();
			}
		});
		disableShowPosition.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				disableShowPositionOption = disableShowPosition.getSelectedState();
			}
		});
		repeated.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				repeatedOption = repeated.getSelectedState();
			}
		});

		repeatedAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				repeatedOptionAll = repeatedAll.getSelectedState();
			}
		});
		allowUpdatedRepeated.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				updatedRepeatedOption = allowUpdatedRepeated.getSelectedState();
			}
		});

		autoDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				autoDownloadOption = autoDownload.getSelectedState();
			}
		});
		lowCPU.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				lowCPUMode = lowCPU.getSelectedState();
			}
		});


		streamerBypass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				streamerBypassOption = streamerBypass.getSelectedState();
			}
		});
		modsBypass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				modsBypassOption = modsBypass.getSelectedState();
			}
		});

		deathMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Settings.writeSettings("SendDeathMessages", String.valueOf(deathMessage.getSelectedState()));
			}
		});

		queueLimitText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				queueLimitBoolean = queueLimitText.getSelectedState();
				queueSizeInput.setEditable(queueLimitBoolean);
			}
		});
		queueSizeInput.setEditable(false);
		queueSizeInput.setBounds(25, 559, 490, 32);
		queueSizeInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		queueSizeInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					queueLimit = Integer.parseInt(queueSizeInput.getText());
				} catch (NumberFormatException f) {
					queueLimit = 0;
				}
			}
		});

		userLimitText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				userLimitOption = userLimitText.getSelectedState();
				userLimitInput.setEditable(userLimitOption);
			}
		});

		userLimitInput.setEditable(false);
		userLimitInput.setBounds(25, 634, 490, 32);
		userLimitInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		userLimitInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					userLimit = Integer.parseInt(userLimitInput.getText());
				} catch (NumberFormatException f) {
					userLimit = 0;
				}
			}
		});

		userLimitStreamText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				userLimitStreamOption = userLimitStreamText.getSelectedState();
				userLimitStreamInput.setEditable(userLimitStreamOption);
			}
		});

		userLimitStreamInput.setEditable(false);
		userLimitStreamInput.setBounds(25, 709, 490, 32);
		userLimitStreamInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		userLimitStreamInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					userLimitStream = Integer.parseInt(userLimitStreamInput.getText());
				} catch (NumberFormatException f) {
					userLimitStream = 0;
				}
			}
		});

		queueCommandLabel.setForeground(Defaults.FOREGROUND);
		queueCommandLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		queueCommandLabel.setBounds(25, 750, 490, queueCommandLabel.getPreferredSize().height + 5);

		queueCommandLength.setText("10");
		queueCommandLength.setBounds(25, 784, 490, 32);
		queueCommandLength.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		queueCommandLength.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					queueLevelLength = Integer.parseInt(queueCommandLength.getText());
				} catch (NumberFormatException f) {
					queueLevelLength = 10;
				}
			}
		});

		panel.add(new SettingsTitle("$REQUESTS_SETTINGS$"));
		panel.add(gdMode);
		panel.add(followers);
		panel.add(subOnly);
		panel.add(nowPlaying);
		panel.add(queueFull);
		panel.add(confirmWhisper);
		panel.add(confirm);
		panel.add(disableShowPosition);
		panel.add(repeated);
		panel.add(repeatedAll);
		panel.add(allowUpdatedRepeated);
		panel.add(autoDownload);
		panel.add(lowCPU);
		panel.add(streamerBypass);
		panel.add(modsBypass);
		panel.add(queueLimitText);
		panel.add(queueSizeInput);
		panel.add(userLimitText);
		panel.add(userLimitInput);
		panel.add(userLimitStreamText);
		panel.add(userLimitStreamInput);
		panel.add(queueCommandLabel);
		panel.add(queueCommandLength);
		panel.add(deathMessage);

		scrollPane.setBounds(0, 0, 542, 622);
		scrollPane.setPreferredSize(new Dimension(542, 622));

		mainPanel.setBounds(0, 0, 542, 622);
		mainPanel.add(scrollPane);
		return mainPanel;
	}

	public static void loadSettings() {
		if (!Settings.getSettings("gdMode").asString().equalsIgnoreCase("")) {
			gdModeOption = Settings.getSettings("gdMode").asBoolean();
		} else {
			gdModeOption = true;
		}
		followersOption = Settings.getSettings("followers").asBoolean();
		subsOption = Settings.getSettings("subscribers").asBoolean();
		//silentOption = Boolean.parseBoolean(Settings.getSettings("silentMode"));
		nowPlayingOption = Settings.getSettings("disableNP").asBoolean();
		queueFullOption = Settings.getSettings("disableQF").asBoolean();
		confirmWhisperOption = Settings.getSettings("whisperConfirm").asBoolean();
		confirmOption = Settings.getSettings("disableConfirm").asBoolean();
		disableShowPositionOption = Settings.getSettings("disableShowPosition").asBoolean();
		repeatedOption = Settings.getSettings("repeatedRequests").asBoolean();
		repeatedOptionAll = Settings.getSettings("repeatedRequestsAll").asBoolean();
		updatedRepeatedOption = Settings.getSettings("updatedRepeated").asBoolean();
		lowCPUMode = Settings.getSettings("lowCPUMode").asBoolean();
		streamerBypassOption = Settings.getSettings("streamerBypass").asBoolean();
		modsBypassOption = Settings.getSettings("modsBypass").asBoolean();


		autoDownloadOption = Settings.getSettings("autoDL").asBoolean();
		queueLimitBoolean = Settings.getSettings("queueLimitEnabled").asBoolean();
		if (!Settings.getSettings("queueLimit").asString().equalsIgnoreCase("")) {
			queueLimit = Settings.getSettings("queueLimit").asInteger();
			queueSizeInput.setText(String.valueOf(queueLimit));
		}
		userLimitOption = Settings.getSettings("userLimitEnabled").asBoolean();
		if (!Settings.getSettings("userLimit").asString().equalsIgnoreCase("")) {
			userLimit = Settings.getSettings("userLimit").asInteger();
			userLimitInput.setText(String.valueOf(userLimit));
		}
		userLimitStreamOption = Settings.getSettings("userLimitStreamEnabled").asBoolean();
		if (!Settings.getSettings("userLimitStream").asString().equalsIgnoreCase("")) {
			userLimitStream = Settings.getSettings("userLimitStream").asInteger();
			userLimitStreamInput.setText(String.valueOf(userLimitStream));
		}
		if (!Settings.getSettings("queueLevelLength").asString().equalsIgnoreCase("")) {
			queueLevelLength = Settings.getSettings("queueLevelLength").asInteger();
			queueCommandLength.setText(String.valueOf(queueLevelLength));
		}
		gdMode.setChecked(gdModeOption);
		followers.setChecked(followersOption);
		nowPlaying.setChecked(nowPlayingOption);
		disableShowPosition.setChecked(disableShowPositionOption);
		queueFull.setChecked(queueFullOption);
		confirm.setChecked(confirmOption);
		confirmWhisper.setChecked(confirmWhisperOption);
		subOnly.setChecked(subsOption);
		repeated.setChecked(repeatedOption);
		repeatedAll.setChecked(repeatedOptionAll);
		allowUpdatedRepeated.setChecked(updatedRepeatedOption);
		autoDownload.setChecked(autoDownloadOption);
		queueLimitText.setChecked(queueLimitBoolean);
		userLimitText.setChecked(userLimitOption);
		userLimitStreamText.setChecked(userLimitStreamOption);
		lowCPU.setChecked(lowCPUMode);
		streamerBypass.setChecked(streamerBypassOption);
		modsBypass.setChecked(modsBypassOption);

		deathMessage.setChecked(Settings.getSettings("SendDeathMessages").asBoolean());


		queueSizeInput.setEditable(queueLimitBoolean);
		userLimitInput.setEditable(userLimitOption);
		userLimitStreamInput.setEditable(userLimitStreamOption);
	}

	public static void setSettings() {

		Settings.writeSettings("gdMode", String.valueOf(gdModeOption));
		Settings.writeSettings("followers", String.valueOf(followersOption));
		Settings.writeSettings("subscribers", String.valueOf(subsOption));
		Settings.writeSettings("disableNP", String.valueOf(nowPlayingOption));
		Settings.writeSettings("disableQF", String.valueOf(queueFullOption));
		Settings.writeSettings("whisperConfirm", String.valueOf(confirmWhisperOption));
		Settings.writeSettings("disableConfirm", String.valueOf(confirmOption));
		Settings.writeSettings("disableShowPosition", String.valueOf(disableShowPositionOption));
		Settings.writeSettings("autoDL", String.valueOf(autoDownloadOption));
		Settings.writeSettings("queueLimitEnabled", String.valueOf(queueLimitBoolean));
		Settings.writeSettings("queueLimit", String.valueOf(queueLimit));
		Settings.writeSettings("userLimitEnabled", String.valueOf(userLimitOption));
		Settings.writeSettings("userLimit", String.valueOf(userLimit));
		Settings.writeSettings("userLimitStreamEnabled", String.valueOf(userLimitStreamOption));
		Settings.writeSettings("userLimitStream", String.valueOf(userLimitStream));
		Settings.writeSettings("repeatedRequests", String.valueOf(repeatedOption));
		Settings.writeSettings("repeatedRequestsAll", String.valueOf(repeatedOptionAll));
		Settings.writeSettings("updatedRepeated", String.valueOf(updatedRepeatedOption));
		Settings.writeSettings("lowCPUMode", String.valueOf(lowCPUMode));
		Settings.writeSettings("streamerBypass", String.valueOf(streamerBypassOption));
		Settings.writeSettings("modsBypass", String.valueOf(modsBypassOption));
		Settings.writeSettings("queueLevelLength", String.valueOf(queueLevelLength));


	}

	@SuppressWarnings("unused")
	private static JLabel createLabel(String text, int y) {
		JLabel label = new JLabel(text);
		label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		label.setBounds(25, y, label.getPreferredSize().width + 5, 30);
		label.setForeground(Defaults.FOREGROUND);

		return label;
	}

	public static void resizeHeight(int height){

		height -= 38;

		mainPanel.setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getWidth(), height);

		scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(), scrollPane.getWidth(), height);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height));
		scrollPane.updateUI();
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

	public static void refreshUI() {
		panel.setBackground(Defaults.SUB_MAIN);
		scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());

		scrollPane.setBackground(Defaults.SUB_MAIN);

		for (Component component : panel.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				component.setBackground(Defaults.BUTTON);
			}
			if (component instanceof JLabel && !(((LangLabel) component).getIdentifier().equals("GDBOARD_VERSION"))) {
				component.setForeground(Defaults.FOREGROUND);

			}
		}
	}
}
