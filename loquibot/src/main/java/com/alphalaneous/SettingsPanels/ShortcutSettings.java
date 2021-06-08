package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Defaults;
import com.alphalaneous.Onboarding;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.Settings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import org.jnativehook.keyboard.SwingKeyAdapter;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;


public class ShortcutSettings {
	public static boolean focused = false;
	public static int openKeybind = 0;
	public static int skipKeybind = 0;
	public static int undoKeybind = 0;
	public static int randKeybind = 0;
	public static int copyKeybind = 0;
	public static int blockKeybind = 0;
	public static int clearKeybind = 0;
	private static final JPanel panel = new JPanel();
	private static final JPanel openPanel = createKeybindButton(85, "$OPEN_SHORTCUT$", "openKeybind");
	private static final JPanel skipPanel = createKeybindButton(135, "$SKIP_SHORTCUT$", "skipKeybind");
	private static final JPanel undoPanel = createKeybindButton(185, "$UNDO_SHORTCUT$", "undoKeybind");
	private static final JPanel randPanel = createKeybindButton(235, "$RANDOM_SHORTCUT$", "randomKeybind");
	private static final JPanel copyPanel = createKeybindButton(285, "$COPY_SHORTCUT$", "copyKeybind");
	private static final JPanel blockPanel = createKeybindButton(335, "$BLOCK_SHORTCUT$", "blockKeybind");
	private static final JPanel clearPanel = createKeybindButton(385, "$CLEAR_SHORTCUT$", "clearKeybind");

	public static JPanel createPanel() {

		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 622);
		panel.setBackground(Defaults.SUB_MAIN);
		panel.setLayout(null);

		panel.add(new SettingsTitle("$SHORTCUTS_SETTINGS$"));
		panel.add(openPanel);
		panel.add(skipPanel);
		panel.add(undoPanel);
		panel.add(randPanel);
		panel.add(copyPanel);
		panel.add(blockPanel);
		panel.add(clearPanel);

		return panel;

	}

	private static JPanel createKeybindButton(int y, String text, String setting) {
		JPanel panel = new JPanel(null);
		panel.setBounds(0, y, 542, 36);
		panel.setBackground(Defaults.SUB_MAIN);
		FancyTextArea input = new FancyTextArea(false, false);
		DefaultStyledDocument doc = new DefaultStyledDocument();
		input.setEditable(false);
		input.addKeyListener(new SwingKeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 8 || e.getKeyCode() == 16 || e.getKeyCode() == 17 || e.getKeyCode() == 18 || e.getKeyCode() == 10) {
					input.setText("");

					Settings.writeSettings(setting, "-1");
					loadKeybind(text, -1);
				} else {
					input.setText(KeyEvent.getKeyText(e.getKeyCode()));
					Settings.writeSettings(setting, String.valueOf(e.getKeyCode()));
					loadKeybind(text, e.getKeyCode());
				}
				panel.requestFocusInWindow();
			}
		});
        /*input.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isFocused[0]){
                    if (!(e.getButton() < 4)) {
                        input.setText("Mouse " + e.getButton());
                    }
                }
            }
        });*/
		input.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				focused = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				focused = false;
			}
		});
		input.setBounds(410, 1, 100, 32);

		input.setDocument(doc);

		LangLabel keybindButton = new LangLabel(text);
		keybindButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		keybindButton.setBounds(25, 3, keybindButton.getPreferredSize().width + 5, keybindButton.getPreferredSize().height + 5);
		keybindButton.setForeground(Defaults.FOREGROUND);

		panel.add(keybindButton);
		panel.add(input);
		return panel;
	}

	@SuppressWarnings("unused")
	private static ThemedCheckbox createButton(String text, int x, int y, int width) {

		ThemedCheckbox button = new ThemedCheckbox(text);
		button.setBounds(25, y, width, 30);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.refresh();
		return button;
	}

	public static void loadSettings() {


		if (Settings.getSettings("onboarding").asBoolean()) {
			openKeybind = Onboarding.openKeybind;
		} else if (!Settings.getSettings("openKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("openKeybind").asString().equalsIgnoreCase("-1")) {
			openKeybind = Settings.getSettings("openKeybind").asInteger();
		}
		if (!Settings.getSettings("skipKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("skipKeybind").asString().equalsIgnoreCase("-1")) {
			skipKeybind = Settings.getSettings("skipKeybind").asInteger();
		}
		if (!Settings.getSettings("undoKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("undoKeybind").asString().equalsIgnoreCase("-1")) {
			undoKeybind = Settings.getSettings("undoKeybind").asInteger();
		}
		if (!Settings.getSettings("randomKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("randomKeybind").asString().equalsIgnoreCase("-1")) {
			randKeybind = Settings.getSettings("randomKeybind").asInteger();
		}
		if (!Settings.getSettings("copyKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("copyKeybind").asString().equalsIgnoreCase("-1")) {
			copyKeybind = Settings.getSettings("copyKeybind").asInteger();
		}
		if (!Settings.getSettings("blockKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("blockKeybind").asString().equalsIgnoreCase("-1")) {
			blockKeybind = Settings.getSettings("blockKeybind").asInteger();
		}
		if (!Settings.getSettings("clearKeybind").asString().equalsIgnoreCase("") && !Settings.getSettings("clearKeybind").asString().equalsIgnoreCase("-1")) {
			clearKeybind = Settings.getSettings("clearKeybind").asInteger();
		}

		for (Component component : panel.getComponents()) {
			if (component instanceof JPanel) {
				for (Component component1 : ((JPanel) component).getComponents()) {
					if (component1 instanceof LangLabel) {
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("OPEN_SHORTCUT")) {
							if (!KeyEvent.getKeyText(openKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(openKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}
						}
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("SKIP_SHORTCUT")) {
							if (!KeyEvent.getKeyText(skipKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(skipKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}
						}
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("UNDO_SHORTCUT")) {
							if (!KeyEvent.getKeyText(undoKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(undoKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}
						}
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("RANDOM_SHORTCUT")) {
							if (!KeyEvent.getKeyText(randKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(randKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}
						}
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("COPY_SHORTCUT")) {
							if (!KeyEvent.getKeyText(copyKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(copyKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}
						}
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("BLOCK_SHORTCUT")) {
							if (!KeyEvent.getKeyText(blockKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(blockKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}
						}
						if (((LangLabel) component1).getIdentifier().equalsIgnoreCase("CLEAR_SHORTCUT")) {
							if (!KeyEvent.getKeyText(clearKeybind).equalsIgnoreCase("Unknown keyCode: 0x0")) {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText(KeyEvent.getKeyText(clearKeybind));
							} else {
								((FancyTextArea) ((JPanel) component).getComponent(1)).setText("");
							}

						}
					}
				}
			}


		}
	}

	public static void loadKeybind(String setting, int keybind) {
		if (setting.equalsIgnoreCase("$OPEN_SHORTCUT$")) {
			openKeybind = keybind;
		}
		if (setting.equalsIgnoreCase("$SKIP_SHORTCUT$")) {
			skipKeybind = keybind;
		}
		if (setting.equalsIgnoreCase("$UNDO_SHORTCUT$")) {
			undoKeybind = keybind;
		}
		if (setting.equalsIgnoreCase("$RANDOM_SHORTCUT$")) {
			randKeybind = keybind;
		}
		if (setting.equalsIgnoreCase("$COPY_SHORTCUT$")) {
			copyKeybind = keybind;
		}
		if (setting.equalsIgnoreCase("$BLOCK_SHORTCUT$")) {
			blockKeybind = keybind;
		}
		if (setting.equalsIgnoreCase("$CLEAR_SHORTCUT$")) {
			clearKeybind = keybind;
		}
	}

	public static void refreshUI() {

		panel.setBackground(Defaults.SUB_MAIN);
		for (Component component : panel.getComponents()) {
			if (component instanceof JPanel) {
				for (Component component2 : ((JPanel) component).getComponents()) {
					if (component2 instanceof JButton) {
						for (Component component3 : ((JButton) component2).getComponents()) {
							if (component3 instanceof JLabel) {
								component3.setForeground(Defaults.FOREGROUND);
							}
						}
						component2.setBackground(Defaults.MAIN);
					}
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);

					}

				}
				component.setBackground(Defaults.SUB_MAIN);
			}
		}
	}
}