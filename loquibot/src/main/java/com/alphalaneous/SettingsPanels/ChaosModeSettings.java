package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
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

public class ChaosModeSettings {

	public static boolean enableChaos = false;
	public static boolean modOnly = false;
	public static boolean subOnly = false;
	public static boolean disableKillOption = false;
	public static double minX = 0;
	public static double maxX = 0;
	public static double minY = 0;
	public static double maxY = 0;
	public static double minSize = 0;
	public static double maxSize = 0;
	public static double minSpeed = 0;
	public static double maxSpeed = 0;
	public static boolean minXOption = false;
	public static boolean maxXOption = false;
	public static boolean minYOption = false;
	public static boolean maxYOption = false;
	public static boolean minSizeOption = false;
	public static boolean maxSizeOption = false;
	public static boolean minSpeedOption = false;
	public static boolean maxSpeedOption = false;
	private static final ThemedCheckbox enableChaosMode = createButton("$ENABLE_CHAOS_MODE$", 80);
	private static final ThemedCheckbox modOnlyChaos = createButton("$MOD_ONLY_CHAOS$", 110);
	private static final ThemedCheckbox subOnlyChaos = createButton("$SUB_ONLY_CHAOS$", 140);

	private static final ThemedCheckbox disableKill = createButton("$DISABLE_KILL$", 180);
	private static final ThemedCheckbox minimumX = createButton("$MINIMUM_X$", 210);
	private static final ThemedCheckbox maximumX = createButton("$MAXIMUM_X$", 285);
	private static final ThemedCheckbox minimumY = createButton("$MINIMUM_Y$", 360);
	private static final ThemedCheckbox maximumY = createButton("$MAXIMUM_Y$", 435);
	private static final ThemedCheckbox minimumSize = createButton("$MINIMUM_SIZE$", 510);
	private static final ThemedCheckbox maximumSize = createButton("$MAXIMUM_SIZE$", 585);
	private static final ThemedCheckbox minimumSpeed = createButton("$MINIMUM_SPEED$", 660);
	private static final ThemedCheckbox maximumSpeed = createButton("$MAXIMUM_SPEED$", 735);
	private static final FancyTextArea minXInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea maxXInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea minYInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea maxYInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea minSizeInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea maxSizeInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea minSpeedInput = new FancyTextArea(true, true, true);
	private static final FancyTextArea maxSpeedInput = new FancyTextArea(true, true, true);
	private static final JPanel mainPanel = new JPanel(null);
	private static final JPanel panel = new JPanel();
	private static final JScrollPane scrollPane = new SmoothScrollPane(panel);


	public static JPanel createPanel() {

		scrollPane.setBounds(0, 0, 542, 622);
		scrollPane.setPreferredSize(new Dimension(542, 622));

		mainPanel.setBounds(0, 0, 542, 622);
		mainPanel.setBackground(Defaults.SUB_MAIN);


		panel.setLayout(null);
		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 820);
		panel.setPreferredSize(new Dimension(542, 820));
		panel.setBackground(Defaults.SUB_MAIN);

		enableChaosMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				enableChaos = enableChaosMode.getSelectedState();
			}
		});
		modOnlyChaos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				modOnly = modOnlyChaos.getSelectedState();
			}
		});
		subOnlyChaos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				subOnly = subOnlyChaos.getSelectedState();
			}
		});

		disableKill.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				disableKillOption = disableKill.getSelectedState();
			}
		});

		minimumX.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				minXOption = minimumX.getSelectedState();
				minXInput.setEditable(minXOption);
			}
		});
		maximumX.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				maxXOption = maximumX.getSelectedState();
				maxXInput.setEditable(maxXOption);
			}
		});
		minimumY.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				minYOption = minimumY.getSelectedState();
				minYInput.setEditable(minYOption);
			}
		});
		maximumY.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				maxYOption = maximumY.getSelectedState();
				maxYInput.setEditable(maxYOption);
			}
		});
		minimumSize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				minSizeOption = minimumSize.getSelectedState();
				minSizeInput.setEditable(minSizeOption);
			}
		});
		maximumSize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				maxSizeOption = maximumSize.getSelectedState();
				maxSizeInput.setEditable(maxSizeOption);
			}
		});
		minimumSpeed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				minSpeedOption = minimumSpeed.getSelectedState();
				minSpeedInput.setEditable(minSpeedOption);
			}
		});
		maximumSpeed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				maxSpeedOption = maximumSpeed.getSelectedState();
				maxSpeedInput.setEditable(maxSpeedOption);
			}
		});

		minXInput.setEditable(false);
		minXInput.setBounds(25, 243, 490, 32);
		minXInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		minXInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					minX = Integer.parseInt(minXInput.getText());
				} catch (NumberFormatException f) {
					minX = 0;
				}
			}
		});
		maxXInput.setEditable(false);
		maxXInput.setBounds(25, 318, 490, 32);
		maxXInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		maxXInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					maxX = Integer.parseInt(maxXInput.getText());
				} catch (NumberFormatException f) {
					maxX = 0;
				}
			}
		});
		minYInput.setEditable(false);
		minYInput.setBounds(25, 393, 490, 32);
		minYInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		minYInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					minY = Integer.parseInt(minYInput.getText());
				} catch (NumberFormatException f) {
					minY = 0;
				}
			}
		});
		maxYInput.setEditable(false);
		maxYInput.setBounds(25, 468, 490, 32);
		maxYInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		maxYInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					maxY = Integer.parseInt(maxYInput.getText());
				} catch (NumberFormatException f) {
					maxY = 0;
				}
			}
		});
		minSizeInput.setEditable(false);
		minSizeInput.setBounds(25, 543, 490, 32);
		minSizeInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		minSizeInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					minSize = Integer.parseInt(minSizeInput.getText());
				} catch (NumberFormatException f) {
					minSize = 0;
				}
			}
		});
		maxSizeInput.setEditable(false);
		maxSizeInput.setBounds(25, 618, 490, 32);
		maxSizeInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		maxSizeInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					maxSize = Integer.parseInt(maxSizeInput.getText());
				} catch (NumberFormatException f) {
					maxSize = 0;
				}
			}
		});
		minSpeedInput.setEditable(false);
		minSpeedInput.setBounds(25, 693, 490, 32);
		minSpeedInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		minSpeedInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					minSpeed = Integer.parseInt(minSpeedInput.getText());
				} catch (NumberFormatException f) {
					minSpeed = 0;
				}
			}
		});
		maxSpeedInput.setEditable(false);
		maxSpeedInput.setBounds(25, 768, 490, 32);
		maxSpeedInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		maxSpeedInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					maxSpeed = Integer.parseInt(maxSpeedInput.getText());
				} catch (NumberFormatException f) {
					maxSpeed = 0;
				}
			}
		});


		panel.add(new SettingsTitle("$CHAOS_SETTINGS$"));
		panel.add(enableChaosMode);
		panel.add(modOnlyChaos);
		panel.add(subOnlyChaos);
		panel.add(disableKill);
		panel.add(minimumX);
		panel.add(maximumX);
		panel.add(minimumY);
		panel.add(maximumY);
		panel.add(minimumSize);
		panel.add(maximumSize);
		panel.add(minimumSpeed);
		panel.add(maximumSpeed);

		panel.add(minXInput);
		panel.add(maxXInput);
		panel.add(minYInput);
		panel.add(maxYInput);
		panel.add(minSizeInput);
		panel.add(maxSizeInput);
		panel.add(minSpeedInput);
		panel.add(maxSpeedInput);

		mainPanel.add(scrollPane);
		return mainPanel;

	}

	public static void resizeHeight(int height){

		height -= 38;

		mainPanel.setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getWidth(), height);

		scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(), scrollPane.getWidth(), height);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height));
		scrollPane.updateUI();
	}

	public static void loadSettings() {


		if (!Settings.getSettings("isChaos").asString().equalsIgnoreCase("")) {
			enableChaos = Settings.getSettings("isChaos").asBoolean();
			enableChaosMode.setChecked(enableChaos);
		}
		if (!Settings.getSettings("isModChaos").asString().equalsIgnoreCase("")) {
			modOnly = Settings.getSettings("isModChaos").asBoolean();
			modOnlyChaos.setChecked(modOnly);
		}
		if (!Settings.getSettings("isSubChaos").asString().equalsIgnoreCase("")) {
			subOnly = Settings.getSettings("isSubChaos").asBoolean();
			subOnlyChaos.setChecked(subOnly);
		}
		if (!Settings.getSettings("disableKill").asString().equalsIgnoreCase("")) {
			disableKillOption = Settings.getSettings("disableKill").asBoolean();
			disableKill.setChecked(disableKillOption);
		}
		if (!Settings.getSettings("minXOption").asString().equalsIgnoreCase("")) {
			minXOption = Settings.getSettings("minXOption").asBoolean();
			minimumX.setChecked(minXOption);
			minXInput.setEditable(minXOption);
		}
		if (!Settings.getSettings("maxXOption").asString().equalsIgnoreCase("")) {
			maxXOption = Settings.getSettings("maxXOption").asBoolean();
			maximumX.setChecked(maxXOption);
			maxXInput.setEditable(maxXOption);
		}
		if (!Settings.getSettings("minYOption").asString().equalsIgnoreCase("")) {
			minYOption = Settings.getSettings("minYOption").asBoolean();
			minimumY.setChecked(minYOption);
			minYInput.setEditable(minYOption);
		}
		if (!Settings.getSettings("maxYOption").asString().equalsIgnoreCase("")) {
			maxYOption = Settings.getSettings("maxYOption").asBoolean();
			maximumY.setChecked(maxYOption);
			maxYInput.setEditable(maxYOption);
		}
		if (!Settings.getSettings("minSizeOption").asString().equalsIgnoreCase("")) {
			minSizeOption = Settings.getSettings("minSizeOption").asBoolean();
			minimumSize.setChecked(minSizeOption);
			minSizeInput.setEditable(minSizeOption);
		}
		if (!Settings.getSettings("maxSizeOption").asString().equalsIgnoreCase("")) {
			maxSizeOption = Settings.getSettings("maxSizeOption").asBoolean();
			maximumSize.setChecked(maxSizeOption);
			maxSizeInput.setEditable(maxSizeOption);
		}
		if (!Settings.getSettings("minSpeedOption").asString().equalsIgnoreCase("")) {
			minSpeedOption = Settings.getSettings("minSpeedOption").asBoolean();
			minimumSpeed.setChecked(minSpeedOption);
			minSpeedInput.setEditable(minSpeedOption);
		}
		if (!Settings.getSettings("maxSpeedOption").asString().equalsIgnoreCase("")) {
			maxSpeedOption = Settings.getSettings("maxSpeedOption").asBoolean();
			maximumSpeed.setChecked(maxSpeedOption);
			maxSpeedInput.setEditable(maxSpeedOption);
		}
		if (!Settings.getSettings("minX").asString().equalsIgnoreCase("")) {
			minX = Settings.getSettings("minX").asDouble();
			minXInput.setText(String.valueOf(minX));
		}
		if (!Settings.getSettings("maxX").asString().equalsIgnoreCase("")) {
			maxX = Settings.getSettings("maxX").asDouble();
			maxXInput.setText(String.valueOf(maxX));
		}
		if (!Settings.getSettings("minY").asString().equalsIgnoreCase("")) {
			minY = Settings.getSettings("minY").asDouble();
			minYInput.setText(String.valueOf(minY));
		}
		if (!Settings.getSettings("maxY").asString().equalsIgnoreCase("")) {
			maxY = Settings.getSettings("maxY").asDouble();
			maxYInput.setText(String.valueOf(maxY));
		}
		if (!Settings.getSettings("minSize").asString().equalsIgnoreCase("")) {
			minSize = Settings.getSettings("minSize").asDouble();
			minSizeInput.setText(String.valueOf(minSize));
		}
		if (!Settings.getSettings("maxSize").asString().equalsIgnoreCase("")) {
			maxSize = Settings.getSettings("maxSize").asDouble();
			maxSizeInput.setText(String.valueOf(maxSize));
		}
		if (!Settings.getSettings("minSpeed").asString().equalsIgnoreCase("")) {
			minSpeed = Settings.getSettings("minSpeed").asDouble();
			minSpeedInput.setText(String.valueOf(minSpeed));
		}
		if (!Settings.getSettings("maxSpeed").asString().equalsIgnoreCase("")) {
			maxSpeed = Settings.getSettings("maxSpeed").asDouble();
			maxSpeedInput.setText(String.valueOf(maxSpeed));
		}
	}

	public static void setSettings() {

		Settings.writeSettings("isChaos", String.valueOf(enableChaos));
		Settings.writeSettings("isModChaos", String.valueOf(modOnly));
		Settings.writeSettings("isSubChaos", String.valueOf(subOnly));
		Settings.writeSettings("disableKill", String.valueOf(disableKillOption));
		Settings.writeSettings("minXOption", String.valueOf(minXOption));
		Settings.writeSettings("maxXOption", String.valueOf(maxXOption));
		Settings.writeSettings("minYOption", String.valueOf(minYOption));
		Settings.writeSettings("maxYOption", String.valueOf(maxYOption));
		Settings.writeSettings("minSizeOption", String.valueOf(minSizeOption));
		Settings.writeSettings("maxSizeOption", String.valueOf(maxSizeOption));
		Settings.writeSettings("minSpeedOption", String.valueOf(minSpeedOption));
		Settings.writeSettings("maxSpeedOption", String.valueOf(maxSpeedOption));
		Settings.writeSettings("minX", String.valueOf(minX));
		Settings.writeSettings("maxX", String.valueOf(maxX));
		Settings.writeSettings("minY", String.valueOf(minY));
		Settings.writeSettings("maxY", String.valueOf(maxY));
		Settings.writeSettings("minSize", String.valueOf(minSize));
		Settings.writeSettings("maxSize", String.valueOf(maxSize));
		Settings.writeSettings("minSpeed", String.valueOf(minSpeed));
		Settings.writeSettings("maxSpeed", String.valueOf(maxSpeed));
	}

	private static ThemedCheckbox createButton(String text, int width, int y) {

		ThemedCheckbox button = new ThemedCheckbox(text);
		button.setBounds(25, y, width, 30);
		button.setForeground(Defaults.FOREGROUND);
		button.setOpaque(false);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.refresh();
		return button;
	}

	private static ThemedCheckbox createButton(String text, int y) {
		return createButton(text, 490, y);
	}

	public static void refreshUI() {

		panel.setBackground(Defaults.SUB_MAIN);
		mainPanel.setBackground(Defaults.SUB_MAIN);
		scrollPane.setBackground(Defaults.SUB_MAIN);
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
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);

			}
		}
	}
}
