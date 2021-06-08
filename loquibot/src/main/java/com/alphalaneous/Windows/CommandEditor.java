package com.alphalaneous.Windows;

import com.alphalaneous.Components.*;
import com.alphalaneous.Defaults;
import com.alphalaneous.Main;
import com.alphalaneous.SettingsPanels.CommandSettings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;

import static com.alphalaneous.Defaults.defaultUI;

public class CommandEditor {

	private static final JFrame editor = new JFrame();
	private static String command;
	private static boolean active = false;
	private static final RadioPanel editorChoices = new RadioPanel(new String[]{"$BASIC_EDITOR$", "$ADVANCED_EDITOR$"});
	private static final RadioPanel basicChoices = new RadioPanel(new String[]{"$SEND_MESSAGE$", "$PLAY_SOUND$"});

	private static final UndoManager undoManager = new UndoManager();

	private static String choice;
	private static final LangLabel commandName = new LangLabel("$COMMAND_NAME$");
	private static final LangLabel messageLabel = new LangLabel("$MESSAGE_LABEL$");

	private static final FancyTextArea commandNameText = new FancyTextArea(false, false);
	private static final FancyTextArea commandResponse = new FancyTextArea(false, false);
	private static final FancyTextArea soundFileLocation = new FancyTextArea(false, false);
	private static final RoundedJButton fileExplorerButton = new RoundedJButton("\uF12B", "$OPEN_EXPLORER$");
	private static final RoundedJButton deleteButton = new RoundedJButton("\uF0CE", "$DELETE_COMMAND$");

	private static final ThemedCheckbox modOnly = createButton("$MOD_ONLY$", 310);
	private static final ThemedCheckbox whisper = createButton("$SEND_AS_WHISPER$", 340);
	private static final ThemedCheckbox disable = createButton("$DISABLE_COMMAND$", 370);

	private static final RSyntaxTextArea codeInput = new RSyntaxTextArea();
	private static final JScrollPane codePanel = new JScrollPane(codeInput);

	private static final JPanel basicPanel = new JPanel(null);
	private static final JPanel advancedPanel = new JPanel(null);

	private static boolean isDisabled = false;
	private static boolean isModOnly = false;
	private static boolean isWhisper = false;
	private static String type = "commands";
	private static String optionType = "SEND_MESSAGE";

	private static final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 1200, 0);
	private static final LangLabel sliderValue = new LangLabel("");

	public static void createPanel() {
		URL iconURL = Window.class.getResource("/Icons/windowIcon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		Image newIcon = icon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
		editor.setTitle("GDBoard - Editor");
		editor.setSize(new Dimension(650, 530));
		editor.setLocation(Defaults.screenSize.x + Defaults.screenSize.width / 2 - editor.getWidth() / 2, Defaults.screenSize.y + Defaults.screenSize.height / 2 - editor.getHeight() / 2);
		editor.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		editor.setResizable(false);
		editor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new Thread(() -> {
					String option;
					try {


						Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + type + "\\" + commandNameText.getText() + ".js");
						if (Files.exists(file)) {
							option = DialogBox.showDialogBox("$OVERRIDE_TITLE$", "$SAVE_INFO$", "", new String[]{"$YES$", "$NO$", "$CANCEL$"}, new Object[]{commandNameText.getText()});
						} else {
							option = DialogBox.showDialogBox("$SAVE_TITLE$", "$SAVE_INFO$", "", new String[]{"$YES$", "$NO$", "$CANCEL$"}, new Object[]{commandNameText.getText()});
						}
						if (option.equals("YES")) {
							if (commandNameText.getText().trim().equalsIgnoreCase("")) {
								new Thread(() -> DialogBox.showDialogBox("$INVALID_NAME$", "$INVALID_NAME_INFO$", "", new String[]{"$OKAY$"})).start();
							} else {
								save();
								if (type.equalsIgnoreCase("commands")) {
									CommandSettings.refresh();
								}
								active = false;
								editor.setVisible(false);
							}
						}
						if (option.equals("NO")) {
							//don't save
							active = false;
							editor.setVisible(false);
						}
					} catch (Exception f) {
						active = false;
						editor.setVisible(false);
					}
				}).start();
			}
		});
		editor.getContentPane().setBackground(Defaults.TOP);
		editor.setIconImage(newIcon);
		editor.setLayout(null);
		codeInput.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);

		codeInput.setCurrentLineHighlightColor(Defaults.BUTTON);

		codePanel.getVerticalScrollBar().setUI(new RectangleScrollbarUI());
		codePanel.getHorizontalScrollBar().setUI(new RectangleScrollbarUI());
		codePanel.getVerticalScrollBar().setOpaque(false);
		codePanel.getHorizontalScrollBar().setOpaque(false);
		codeInput.setTabSize(4);
		codeInput.setBackground(Defaults.MAIN);
		codeInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
		codeInput.setForeground(Defaults.FOREGROUND);
		codePanel.setBorder(BorderFactory.createEmptyBorder());
		codeInput.setText("function command(){\n\n}");
		codePanel.setBounds(10, 10, 615, 180);
		editorChoices.setBounds(320, 25, 300, 65);
		for (RadioButton button : editorChoices.buttons) {
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					choice = editorChoices.getSelectedButton();
					if (choice.equalsIgnoreCase("BASIC_EDITOR")) {
						optionType = "BASIC_EDITOR";
						basicPanel.setVisible(true);
						advancedPanel.setVisible(false);

					} else if (choice.equalsIgnoreCase("ADVANCED_EDITOR")) {
						optionType = "ADVANCED_EDITOR";
						advancedPanel.setVisible(true);
						basicPanel.setVisible(false);
					}
				}
			});
		}


		basicPanel.setBackground(Defaults.SUB_MAIN);
		basicPanel.setBounds(0, 100, 650, 200);

		advancedPanel.setBackground(Defaults.SUB_MAIN);
		advancedPanel.setBounds(0, 100, 650, 200);

		soundFileLocation.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					java.util.List<File> droppedFiles = (java.util.List<File>)
							evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					if (!(droppedFiles.size() > 1)) {
						System.out.println(droppedFiles.get(0).getPath());
						soundFileLocation.setText(droppedFiles.get(0).getPath());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		basicChoices.setBounds(320, 10, 300, 65);
		for (RadioButton button : basicChoices.buttons) {
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					choice = basicChoices.getSelectedButton();
					if (choice.equalsIgnoreCase("SEND_MESSAGE")) {
						optionType = "SEND_MESSAGE";
						commandResponse.setVisible(true);
						soundFileLocation.setVisible(false);
						fileExplorerButton.setVisible(false);
						messageLabel.setTextLang("$MESSAGE_LABEL$");
					} else if (choice.equalsIgnoreCase("PLAY_SOUND")) {
						optionType = "PLAY_SOUND";
						commandResponse.setVisible(false);
						soundFileLocation.setVisible(true);
						fileExplorerButton.setVisible(true);

						messageLabel.setTextLang("$SOUND_LABEL$");

					}
				}
			});
		}


		Document doc = codeInput.getDocument();
		doc.addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

		InputMap im = codeInput.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = codeInput.getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");
		//noinspection MagicConstant
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK), "Redo");

		am.put("Undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
		am.put("Redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
		fileExplorerButton.setFont(Defaults.SYMBOLS.deriveFont(14f));
		fileExplorerButton.setUI(defaultUI);
		fileExplorerButton.setForeground(Defaults.FOREGROUND);
		fileExplorerButton.setBackground(Defaults.MAIN);
		fileExplorerButton.setBounds(585, 80, 32, 32);
		/*fileExplorerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);

				new JFXPanel();
				PlatformImpl.startup(() -> {
					FileChooser d = new FileChooser();
					d.setTitle("Open Sound File");
					d.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp3 files (*.mp3)"));
					File file = d.showOpenDialog(null);
					if (file != null) {
						soundFileLocation.setText(file.getPath().replace("\\", "/"));
					}
				});
			}
		});*/

		deleteButton.setFont(Defaults.SYMBOLS.deriveFont(14f));
		deleteButton.setUI(defaultUI);
		deleteButton.setForeground(Defaults.FOREGROUND);
		deleteButton.setBackground(Defaults.MAIN);
		deleteButton.setBounds(270, 50, 32, 32);
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				new Thread(() -> {
					String option = DialogBox.showDialogBox("$DELETE_COMMAND_TITLE$", "$DELETE_COMMAND_INFO$", "", new String[]{"$YES$", "$NO$"}, new Object[]{command});
					if (option.equalsIgnoreCase("YES")) {
						deleteCommand();
						if (type.equalsIgnoreCase("commands")) {
							CommandSettings.refresh();
						}
					}
				}).start();
			}
		});

		fileExplorerButton.setVisible(false);
		//basicPanel.add(fileExplorerButton);

		editorChoices.setChecked("BASIC_EDITOR");
		editorChoices.setWidth(300);

		basicChoices.setChecked("SEND_MESSAGE");
		basicChoices.setWidth(300);

		commandName.setBounds(20, 20, 300, 30);
		commandName.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		commandName.setForeground(Defaults.FOREGROUND);

		messageLabel.setBounds(20, 45, 300, 30);
		messageLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		messageLabel.setForeground(Defaults.FOREGROUND2);

		commandNameText.setBounds(20, 50, 240, 32);
		commandNameText.getDocument().putProperty("filterNewlines", Boolean.TRUE);

		commandResponse.setBounds(20, 80, 595, 96);
		commandResponse.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		commandResponse.setLineWrap(true);
		commandResponse.setWrapStyleWord(true);

		soundFileLocation.setBounds(20, 80, 595, 32);
		soundFileLocation.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		soundFileLocation.setVisible(false);

		disable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				isDisabled = disable.getSelectedState();
			}
		});

		modOnly.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				isModOnly = modOnly.getSelectedState();
			}
		});
		whisper.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				isWhisper = whisper.getSelectedState();
			}
		});

		sliderValue.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		sliderValue.setTextLangFormat("$COOLDOWN$", 0);
		sliderValue.setForeground(Defaults.FOREGROUND);
		sliderValue.setBounds(25, 410, 585, sliderValue.getPreferredSize().height + 5);

		UIDefaults sliderDefaults = new UIDefaults();

		sliderDefaults.put("Slider.thumbWidth", 20);
		sliderDefaults.put("Slider.thumbHeight", 20);
		sliderDefaults.put("Slider:SliderThumb.backgroundPainter", (Painter<JComponent>) (g, c, w, h) -> {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(2f));
			g.setColor(Defaults.ACCENT);
			g.fillOval(1, 1, w - 3, h - 3);
			g.setColor(Defaults.MAIN);
			g.drawOval(1, 1, w - 4, h - 4);
		});
		sliderDefaults.put("Slider:SliderTrack.backgroundPainter", (Painter<JComponent>) (g, c, w, h) -> {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(2f));
			g.setColor(Defaults.BUTTON);
			g.fillRoundRect(0, 6, w - 1, 8, 8, 8);
		});
		sliderDefaults.put("Slider:SliderThumb[MouseOver].backgroundPainter", (Painter<JComponent>) (g, c, w, h) -> {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(2f));
			g.setColor(Defaults.ACCENT);
			g.fillOval(1, 1, w - 3, h - 3);
			g.setColor(Defaults.TOP);
			g.drawOval(1, 1, w - 4, h - 4);
		});

		slider.setMinorTickSpacing(5);
		slider.setMinorTickSpacing(100);
		slider.putClientProperty("Nimbus.Overrides", sliderDefaults);
		slider.putClientProperty("Nimbus.Overrides.InheritDefaults", false);
		slider.setBounds(25, 440, 585, 30);
		slider.setBackground(Defaults.SUB_MAIN);
		slider.setBorder(BorderFactory.createEmptyBorder());
		slider.addChangeListener(e -> {
			if (slider.getValue() == 10) {
				sliderValue.setTextLangFormat("$COOLDOWN_SINGULAR$", (double) slider.getValue() / 10);

			} else {
				sliderValue.setTextLangFormat("$COOLDOWN$", (double) slider.getValue() / 10);
			}
		});

		advancedPanel.setVisible(false);
		basicPanel.setVisible(true);
		basicPanel.add(soundFileLocation);
		basicPanel.add(messageLabel);
		basicPanel.add(basicChoices);
		basicPanel.add(commandResponse);
		editor.add(commandName);
		editor.add(commandNameText);
		editor.add(editorChoices);

		editor.add(disable);
		editor.add(modOnly);
		editor.add(whisper);
		editor.add(deleteButton);
		editor.add(slider);
		editor.add(sliderValue);
		advancedPanel.add(codePanel);
		editor.add(basicPanel);
		editor.add(advancedPanel);
	}

	private static void save() {
		if (!Files.exists(Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + type + "\\"))) {
			try {
				Files.createDirectory(Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + type + "\\"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String function;
		if (editorChoices.currentSelect.equals("ADVANCED_EDITOR")) {
			function = codeInput.getText();
		} else {
			if (basicChoices.currentSelect.equals("SEND_MESSAGE")) {
				function = "function command() { return \"" + commandResponse.getText() + "\";}";
			} else {
				function = "function command() { Board.playSound(\"" + soundFileLocation.getText().replace("\\", "\\\\") + "\");}";
			}
		}
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + type + "\\" + commandNameText.getText().replace("\\\\", "").replace("/", "") + ".js");
		if (!Files.exists(file)) {
			try {
				Files.createFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			Files.write(file, function.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (type.equalsIgnoreCase("commands")) {
			saveStuff("mod", isModOnly);
			saveStuff("whisper", isWhisper);
			saveStuff("disable", isDisabled);
			saveCooldown();
		}
		saveOption();
	}

	public static void showEditor(String type, String command, boolean isDefault) {
		if (!active) {
			CommandEditor.type = type;
			active = true;
			CommandEditor.command = command;
			deleteButton.setVisible(false);
			whisper.setChecked(false);
			disable.setChecked(false);
			modOnly.setChecked(false);
			commandNameText.setText("");
			commandResponse.setText("");
			soundFileLocation.setText("");
			codeInput.setText("function command(){\n\n}");
			editorChoices.setChecked("BASIC_EDITOR");
			basicChoices.setChecked("SEND_MESSAGE");
			basicPanel.setVisible(true);
			advancedPanel.setVisible(false);
			commandResponse.setVisible(true);
			soundFileLocation.setVisible(false);
			messageLabel.setTextLang("$MESSAGE_LABEL$");
			fileExplorerButton.setVisible(false);
			editor.setSize(new Dimension(650, 530));
			if (!command.equals("")) {
				if (!isDefault) {
					deleteButton.setVisible(true);
				}
				commandNameText.setText(command);
				commandNameText.setEditable(false);
				if (type.equalsIgnoreCase("commands")) {
					whisper.setChecked(getStuff("whisper"));
					disable.setChecked(getStuff("disable"));
					modOnly.setChecked(getStuff("mod"));
					slider.setValue(getCooldown());
				} else {
					editor.setSize(new Dimension(editor.getWidth(), 340));
				}
				if (!isDefault) {
					if (getOption().equalsIgnoreCase("SEND_MESSAGE")) {
						commandResponse.setText(replaceLast(getCommand().replaceFirst("function command\\(\\) \\{ return \"", ""), "\";}", ""));
						editorChoices.setChecked("BASIC_EDITOR");
						optionType = "SEND_MESSAGE";
						basicChoices.setChecked("SEND_MESSAGE");
					} else if (getOption().equalsIgnoreCase("PLAY_SOUND")) {
						soundFileLocation.setText(replaceLast(getCommand().replaceFirst("function command\\(\\) \\{ Board.playSound\\(\"", ""), "\");}", ""));
						editorChoices.setChecked("BASIC_EDITOR");
						basicChoices.setChecked("PLAY_SOUND");
						optionType = "PLAY_SOUND";
						commandResponse.setVisible(false);
						soundFileLocation.setVisible(true);
						fileExplorerButton.setVisible(true);
						messageLabel.setTextLang("$SOUND_LABEL$");
					} else if (getOption().equalsIgnoreCase("ADVANCED_EDITOR")) {
						codeInput.setText(getCommand());
						basicChoices.setChecked("SEND_MESSAGE");
						editorChoices.setChecked("ADVANCED_EDITOR");
						optionType = "ADVANCED_EDITOR";
						basicPanel.setVisible(false);
						advancedPanel.setVisible(true);
					}
				} else {
					codeInput.setText(getCommand());
					optionType = "ADVANCED_EDITOR";
					basicChoices.setChecked("SEND_MESSAGE");
					editorChoices.setChecked("ADVANCED_EDITOR");
					basicPanel.setVisible(false);
					advancedPanel.setVisible(true);
				}
			} else {
				commandNameText.setEditable(true);
			}
			if (!type.equalsIgnoreCase("commands")) {
				editor.setSize(new Dimension(editor.getWidth(), 340));
			}
			CommandEditor.type = type;
			boolean commands = type.equalsIgnoreCase("commands");

			disable.setVisible(commands);
			modOnly.setVisible(commands);
			whisper.setVisible(commands);
			slider.setVisible(commands);
			sliderValue.setVisible(commands);

			editor.setLocation(Defaults.screenSize.x + Defaults.screenSize.width / 2 - editor.getWidth() / 2, Defaults.screenSize.y + Defaults.screenSize.height / 2 - editor.getHeight() / 2);
		}
		commandNameText.clearUndo();
		commandResponse.clearUndo();
		undoManager.discardAllEdits();
		editor.setVisible(true);
	}

	private static ThemedCheckbox createButton(String text, int y) {
		ThemedCheckbox button = new ThemedCheckbox(text);
		button.setBounds(25, y, 585, 30);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.refresh();
		return button;
	}

	private static void saveStuff(String identifier, boolean state) {
		if (state) {
			Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + identifier + ".txt");
			try {
				if (!Files.exists(file)) {
					Files.createFile(file);
				}
				Files.write(
						file,
						(commandNameText.getText() + "\n").getBytes(),
						StandardOpenOption.APPEND);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			boolean exists = false;
			Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + identifier + ".txt");
			try {
				if (Files.exists(file)) {
					Scanner sc = new Scanner(file);
					while (sc.hasNextLine()) {
						if (String.valueOf(commandNameText.getText()).equals(sc.nextLine())) {
							exists = true;
							break;
						}
					}
					sc.close();
					if (exists) {
						Path temp = Paths.get(Defaults.saveDirectory + "\\GDBoard\\_temp" + identifier + "_");
						PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
						Files.lines(file)
								.filter(line -> !line.contains(commandNameText.getText()))
								.forEach(out::println);
						out.flush();
						out.close();
						Files.delete(file);
						Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\GDBoard\\" + identifier + ".txt"), StandardCopyOption.REPLACE_EXISTING);
					}
				}
			} catch (Exception f) {
				f.printStackTrace();
			}
		}
	}

	private static boolean getStuff(String identifier) {
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + identifier + ".txt");
		if (Files.exists(file)) {
			Scanner sc = null;
			try {
				sc = new Scanner(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			assert sc != null;
			while (sc.hasNextLine()) {
				if (String.valueOf(commandNameText.getText()).equals(sc.nextLine())) {
					sc.close();
					return true;
				}
			}
		}
		return false;
	}

	public static String getCommand() {
		try {

			if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/" + commandNameText.getText() + ".js"))) {
				return String.valueOf(Files.readString(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/" + commandNameText.getText() + ".js"), StandardCharsets.UTF_8));
			}

			if (type.equalsIgnoreCase("commands")) {
				StringBuilder function = new StringBuilder();
				InputStream is = Main.class
						.getClassLoader().getResourceAsStream("Commands/" + commandNameText.getText() + ".js");
				assert is != null;
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				String line;
				while ((line = br.readLine()) != null) {
					function.append(line).append("\n");
				}
				is.close();
				isr.close();
				br.close();
				return function.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static int getCooldown() {
		int cooldown = 0;
		if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt"))) {
			Scanner sc3 = null;
			try {
				sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt").toFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			assert sc3 != null;
			while (sc3.hasNextLine()) {
				String line = sc3.nextLine();
				if (line.split("=")[0].trim().equalsIgnoreCase(commandNameText.getText())) {
					cooldown = Integer.parseInt(line.split("=")[1].trim());
					break;
				}
			}
			sc3.close();
		}
		return cooldown;
	}

	public static String getOption() {
		String option = "SEND_MESSAGE";
		if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt"))) {
			Scanner sc3 = null;
			try {
				sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt").toFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			assert sc3 != null;
			while (sc3.hasNextLine()) {
				String line = sc3.nextLine();
				if (line.split("=")[0].trim().equalsIgnoreCase(commandNameText.getText())) {
					option = line.split("=")[1].trim();
					break;
				}
			}
			sc3.close();
		}
		return option;
	}

	private static void saveCooldown() {
		try {
			int cooldown = -1;
			if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt"))) {
				Scanner sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt").toFile());
				while (sc3.hasNextLine()) {
					String line = sc3.nextLine();
					if (line.split("=").length > 1) {
						if (line.split("=")[0].trim().equalsIgnoreCase(commandNameText.getText())) {
							cooldown = Integer.parseInt(line.split("=")[1].trim());
							break;
						}
					}
				}
				sc3.close();
			} else {
				Files.createFile(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt"));
			}
			if (cooldown != -1) {
				BufferedReader file = new BufferedReader(new FileReader(Defaults.saveDirectory + "/GDBoard/cooldown.txt"));
				StringBuilder inputBuffer = new StringBuilder();
				String line;
				while ((line = file.readLine()) != null) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
				file.close();

				FileOutputStream fileOut = new FileOutputStream(Defaults.saveDirectory + "/GDBoard/cooldown.txt");
				fileOut.write(inputBuffer.toString().replace(commandNameText.getText() + " = " + cooldown, commandNameText.getText() + " = " + slider.getValue()).getBytes());
				fileOut.close();
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt").toFile(), true));
				writer.newLine();
				writer.write(commandNameText.getText() + " = " + slider.getValue());
				writer.close();

			}
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	private static void saveOption() {
		try {

			String typeA = null;
			if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt"))) {
				Scanner sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt").toFile());
				while (sc3.hasNextLine()) {
					String line = sc3.nextLine();
					if (line.split("=").length > 1) {
						if (line.split("=")[0].trim().equalsIgnoreCase(commandNameText.getText())) {
							typeA = line.split("=")[1].trim();
							break;
						}
					}
				}
				sc3.close();
			} else {
				Files.createFile(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt"));
			}
			if (typeA != null) {
				BufferedReader file = new BufferedReader(new FileReader(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt"));
				StringBuilder inputBuffer = new StringBuilder();
				String line;
				while ((line = file.readLine()) != null) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
				file.close();

				FileOutputStream fileOut = new FileOutputStream(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt");
				fileOut.write(inputBuffer.toString().replace(commandNameText.getText() + " = " + typeA, commandNameText.getText() + " = " + optionType).getBytes());
				fileOut.close();
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/options.txt").toFile(), true));
				writer.newLine();
				writer.write(commandNameText.getText() + " = " + optionType);
				writer.close();
			}
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	private static void deleteCommand() {
		isModOnly = false;
		isWhisper = false;
		isDisabled = false;
		slider.setValue(0);
		optionType = "SEND_MESSAGE";
		save();
		try {
			Files.delete(Paths.get(Defaults.saveDirectory + "/GDBoard/" + type + "/" + commandNameText.getText() + ".js"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		editor.setVisible(false);
		active = false;
	}

	private static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length());
		} else {
			return string;
		}
	}

	public static void refreshUI() {
		editor.getContentPane().setBackground(Defaults.TOP);
		codeInput.setCurrentLineHighlightColor(Defaults.BUTTON);
		codeInput.setBackground(Defaults.MAIN);
		codeInput.setForeground(Defaults.FOREGROUND);
		editorChoices.refreshUI();
		basicChoices.refreshUI();
		basicPanel.setBackground(Defaults.SUB_MAIN);
		advancedPanel.setBackground(Defaults.SUB_MAIN);
		fileExplorerButton.setUI(defaultUI);
		fileExplorerButton.setForeground(Defaults.FOREGROUND);
		fileExplorerButton.setBackground(Defaults.MAIN);
		deleteButton.setUI(defaultUI);
		deleteButton.setForeground(Defaults.FOREGROUND);
		deleteButton.setBackground(Defaults.MAIN);
		commandName.setForeground(Defaults.FOREGROUND);
		messageLabel.setForeground(Defaults.FOREGROUND2);
		sliderValue.setForeground(Defaults.FOREGROUND);
		slider.setBackground(Defaults.SUB_MAIN);
	}
}
