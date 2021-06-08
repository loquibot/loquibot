package com.alphalaneous.SettingsPanels;

import com.alphalaneous.*;
import com.alphalaneous.Components.*;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Windows.CommandEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.alphalaneous.Defaults.settingsButtonUI;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ChannelPointSettings {
	private static int i = 0;
	private static double height = 0;
	private static final JLabel commandLabel = new JLabel();
	private static final JPanel commandsPanel = new JPanel();
	private static final JScrollPane scrollPane = new SmoothScrollPane(commandsPanel);
	private static final JPanel panel = new JPanel();
	private static final JPanel titlePanel = new JPanel();
	private static final RoundedJButton refreshPoints = new RoundedJButton("\uF078", "$REFRESH_CHANNEL_POINTS_TOOLTIP$");
	private static final LangLabel notAvailable = new LangLabel("$CHANNEL_POINTS_UNAVAILABLE$");

	public static JPanel createPanel() {

		LangLabel label = new LangLabel("$POINTS_LIST$");
		label.setForeground(Defaults.FOREGROUND);
		label.setFont(Defaults.MAIN_FONT.deriveFont(24f));
		label.setBounds(25, 25, label.getPreferredSize().width + 5, label.getPreferredSize().height + 5);

		panel.add(label);

		refreshPoints.setBackground(Defaults.BUTTON);
		refreshPoints.setBounds(490, 31, 30, 30);
		refreshPoints.setFont(Defaults.SYMBOLS.deriveFont(14f));
		refreshPoints.setForeground(Defaults.FOREGROUND);
		refreshPoints.setUI(settingsButtonUI);
		refreshPoints.addActionListener(e -> refresh());

		panel.add(refreshPoints);


		notAvailable.setForeground(Defaults.FOREGROUND);
		notAvailable.setFont(Defaults.MAIN_FONT.deriveFont(16f));

		titlePanel.setBounds(0, 0, 542, 50);
		titlePanel.setLayout(null);
		titlePanel.setDoubleBuffered(true);
		titlePanel.setBackground(Defaults.TOP);

		commandLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		commandLabel.setForeground(Defaults.FOREGROUND);
		commandLabel.setBounds(50, 17, commandLabel.getPreferredSize().width + 5, commandLabel.getPreferredSize().height + 5);
		titlePanel.add(commandLabel);


		panel.setLayout(null);
		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 622);
		panel.setBackground(Defaults.SUB_MAIN);
		commandsPanel.setDoubleBuffered(true);
		commandsPanel.setBounds(0, 0, 542, 0);
		commandsPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

		commandsPanel.setPreferredSize(new Dimension(542, 0));
		commandsPanel.setBackground(Defaults.SUB_MAIN);
		commandsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
		scrollPane.setBounds(0, 80, 542, 542);
		scrollPane.setPreferredSize(new Dimension(542, 542));

		panel.setBounds(0, 0, 542, 622);
		panel.add(scrollPane);
		return panel;
	}
	public static void resizeHeight(int height){

		height -= 38;

		panel.setBounds(panel.getX(), panel.getY(), panel.getWidth(), height);

		scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(), scrollPane.getWidth(), height-80);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height-80));
		scrollPane.updateUI();

	}
	public static void refresh() {

		commandsPanel.removeAll();
		height = 0;
		commandsPanel.setBounds(0, 0, 542, (int) (height + 14));
		commandsPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));

		if (TwitchAccount.broadcaster_type.equalsIgnoreCase("affiliate")
				|| TwitchAccount.broadcaster_type.equalsIgnoreCase("partner")) {
			commandsPanel.remove(notAvailable);
			ArrayList<ChannelPointReward> rewards = APIs.getChannelPoints();
			for (ChannelPointReward reward : rewards) {
				addButton(reward.getTitle(), reward.getBgColor(), reward.getIcon(), reward.isDefaultIcon());
			}
		} else {
			commandsPanel.add(notAvailable);
		}

	}

	public static void addButton(String command, Color color, Icon icon, boolean defaultIcon) {
		i++;
		if ((i - 1) % 4 == 0) {
			height = height + 124;

			commandsPanel.setBounds(0, 0, 542, (int) (height + 14));
			commandsPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));
			if (i > 0) {
				scrollPane.updateUI();
			}
		}
		JButtonUI colorUI = new JButtonUI();
		colorUI.setBackground(color);
		colorUI.setHover(color);
		colorUI.setSelect(color.darker());
		CurvedButton button = new CurvedButton("");
		JLabel pointLabel = new JLabel(command);
		JLabel pointIcon = new JLabel(icon);
		pointLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.setBackground(color);
		button.setLayout(null);
		pointLabel.setBounds(60 - pointLabel.getPreferredSize().width / 2, 20, 120, 120);
		pointIcon.setBounds(0, -10, 120, 120);

		button.add(pointLabel);
		button.add(pointIcon);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setUI(colorUI);
		double brightness = Math.sqrt(color.getRed() * color.getRed() * .241 +
				color.getGreen() * color.getGreen() * .691 +
				color.getBlue() * color.getBlue() * .068);

		if (brightness > 130) {
			pointLabel.setForeground(Color.BLACK);
			if (defaultIcon) {
				pointIcon.setIcon(new ImageIcon(HighlightButton.colorImage(HighlightButton.convertToBufferedImage(icon), Color.BLACK)));
			}
		} else {
			pointLabel.setForeground(Color.WHITE);
		}
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setPreferredSize(new Dimension(120, 120));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (brightness > 130) {
					button.setBackground(button.getBackground().darker());
				} else {
					button.setBackground(button.getBackground().brighter());

				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(color);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					Path comPath = Paths.get(Defaults.saveDirectory + "/GDBoard/points/" + command + ".js");

					new Thread(() -> {
						try {
							if(Files.exists(comPath)) {
								Command.run(TwitchAccount.display_name, true, true, new String[]{"dummy"}, Files.readString(comPath, StandardCharsets.UTF_8), 0, null);
							}
							} catch (IOException e1) {
							e1.printStackTrace();
						}
					}).start();

				}
			}
		});
		button.addActionListener(e -> {
			CommandEditor.showEditor("points", command, false);
		});
		button.refresh();
		commandsPanel.add(button);
	}

	public static void refreshUI() {
		panel.setBackground(Defaults.TOP);
		titlePanel.setBackground(Defaults.TOP);
		commandLabel.setForeground(Defaults.FOREGROUND);
		commandsPanel.setBackground(Defaults.SUB_MAIN);
		notAvailable.setForeground(Defaults.FOREGROUND);
		scrollPane.setBackground(Defaults.SUB_MAIN);
		scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());


		for (Component component : commandsPanel.getComponents()) {
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);

			}
		}

		for (Component component : panel.getComponents()) {
			if (component instanceof JButton) {
				component.setForeground(Defaults.FOREGROUND);
				component.setBackground(Defaults.BUTTON);
			}
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);
			}
		}
	}

	public static class ButtonInfo {

		public Path path;

		ButtonInfo(Path path) {
			this.path = path;
		}

	}
}
