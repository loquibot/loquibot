package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Assets;
import com.alphalaneous.Components.*;
import com.alphalaneous.Defaults;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.Settings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.ThemedComponents.ThemedIconCheckbox;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.SettingsWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.alphalaneous.Defaults.settingsButtonUI;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class FiltersSettings {

    public static volatile int minLikes = 0;
    public static volatile int maxLikes = 0;
    public static volatile int minObjects = 0;
    public static volatile int maxObjects = 0;
    public static volatile int minID = 0;
    public static volatile int maxID = 0;
    public static volatile boolean minLikesOption = false;
    public static volatile boolean maxLikesOption = false;
    public static volatile boolean minObjectsOption = false;
    public static volatile boolean maxObjectsOption = false;
    public static volatile boolean minIDOption = false;
    public static volatile boolean maxIDOption = false;
    public static volatile boolean ratedOption = false;
    public static volatile boolean unratedOption = false;
    public static volatile boolean disableOption = true;
    public static volatile boolean disableLengthOption = true;
    public static volatile boolean disallowOption = false;
    public static volatile boolean allowOption = false;
    public static volatile ArrayList<String> excludedDifficulties = new ArrayList<>();
    public static volatile ArrayList<String> excludedLengths = new ArrayList<>();

    private static final ThemedIconCheckbox naIcon = createDifficultyButton("NA");
    private static final ThemedIconCheckbox autoIcon = createDifficultyButton("auto");
    private static final ThemedIconCheckbox easyIcon = createDifficultyButton("easy");
    private static final ThemedIconCheckbox normalIcon = createDifficultyButton("normal");
    private static final ThemedIconCheckbox hardIcon = createDifficultyButton("hard");
    private static final ThemedIconCheckbox harderIcon = createDifficultyButton("harder");
    private static final ThemedIconCheckbox insaneIcon = createDifficultyButton("insane");
    private static final ThemedIconCheckbox easyDemonIcon = createDifficultyButton("easy demon");
    private static final ThemedIconCheckbox mediumDemonIcon = createDifficultyButton("medium demon");
    private static final ThemedIconCheckbox hardDemonIcon = createDifficultyButton("hard demon");
    private static final ThemedIconCheckbox insaneDemonIcon = createDifficultyButton("insane demon");
    private static final ThemedIconCheckbox extremeDemonIcon = createDifficultyButton("extreme demon");

    private static final ThemedCheckbox tiny = createButton("$GD_TINY$", 5);
    private static final ThemedCheckbox shortL = createButton("$GD_SHORT$", 35);
    private static final ThemedCheckbox medium = createButton("$GD_MEDIUM$", 65);
    private static final ThemedCheckbox longL = createButton("$GD_LONG$", 95);
    private static final ThemedCheckbox XL = createButton("$GD_XL$", 125);
    private static final ThemedCheckbox minimumLikes = createButton("$MINIMUM_LIKES$", 490);
    private static final ThemedCheckbox maximumLikes = createButton("$MAXIMUM_LIKES$", 565);
    private static final ThemedCheckbox minimumObjects = createButton("$MINIMUM_OBJECTS$", 640);
    private static final ThemedCheckbox maximumObjects = createButton("$MAXIMUM_OBJECTS$", 715);
    private static final ThemedCheckbox minimumID = createButton("$MINIMUM_ID$", 790);
    private static final ThemedCheckbox maximumID = createButton("$MAXIMUM_ID$", 865);
    private static final FancyTextArea minLikesInput = new FancyTextArea(true, true);
    private static final FancyTextArea maxLikesInput = new FancyTextArea(true, true);
    private static final FancyTextArea minObjectsInput = new FancyTextArea(true, false);
    private static final FancyTextArea maxObjectsInput = new FancyTextArea(true, false);
    private static final FancyTextArea minIDInput = new FancyTextArea(true, false);
    private static final FancyTextArea maxIDInput = new FancyTextArea(true, false);
    private static final CurvedButton allowedStrings = new CurvedButton("$ALLOWED_WORDS$");
    private static final CurvedButton disallowedStrings = new CurvedButton("$DISALLOWED_WORDS$");
    private static final ThemedCheckbox rated = createButton("$RATED_LEVELS_ONLY$", 75);
    private static final ThemedCheckbox unrated = createButton("$UNRATED_LEVELS_ONLY$", 105);
    private static final ThemedCheckbox disableDifficulties = createButton("$DISABLE_SELECTED_DIFFICULTIES$", 135);
    private static final ThemedCheckbox disableLengths = createButton("$DISABLE_SELECTED_LENGTHS$", 280);
    private static final JPanel difficultyPanel = new JPanel();
    private static final JPanel lengthPanel = new JPanel(null);
    private static final JPanel mainPanel = new JPanel(null);
    private static final JPanel panel = new JPanel();
    private static final JScrollPane scrollPane = new SmoothScrollPane(panel);
    private static final JPanel listPanel = new JPanel();
    private static final JScrollPane listScrollPane = new SmoothScrollPane(listPanel);
    private static final JPanel topPanel = new JPanel();
    private static final JLabel label = new JLabel();
    private static final FancyTextArea input = new FancyTextArea(false, false);
    private static final RoundedJButton addID = new RoundedJButton("\uF078", "Add Word");
    private static final RoundedJButton backButton = new RoundedJButton("\uF31E", "Back");
    private static final ThemedCheckbox enableWordSetting = createButton("",55);
    private static int i = 0;
    private static double height = 0;
    private static boolean allowedStringsBool = false;

    public static JPanel createPanel() {

        scrollPane.setBounds(0, 0, 542, 622);
        scrollPane.setPreferredSize(new Dimension(542, 622));

        listPanel.setDoubleBuffered(true);
        listPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
        listPanel.setPreferredSize(new Dimension(542, 0));
        listPanel.setBackground(Defaults.SUB_MAIN);

        topPanel.setBackground(Defaults.TOP);
        topPanel.setLayout(null);

        label.setForeground(Defaults.FOREGROUND);
        label.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        input.setBounds(280, 15, 200, 32);
        input.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        addID.setBackground(Defaults.BUTTON);
        addID.setBounds(490, 16, 30, 30);
        addID.setFont(Defaults.SYMBOLS.deriveFont(22f));
        addID.setForeground(Defaults.FOREGROUND);
        addID.setUI(settingsButtonUI);

        backButton.setBackground(Defaults.BUTTON);
        backButton.setBounds(15, 16, 30, 30);
        backButton.setFont(Defaults.SYMBOLS.deriveFont(14f));

        backButton.setForeground(Defaults.FOREGROUND);
        backButton.setUI(settingsButtonUI);

        backButton.addActionListener(e -> {
            topPanel.setVisible(false);
            listScrollPane.setVisible(false);
            scrollPane.setVisible(true);
        });

        addID.addActionListener(e -> {
            try {
                Path file;
                if (allowedStringsBool) {
                    file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\allowedStrings.txt");
                } else {
                    file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\disallowedStrings.txt");
                }
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }
                boolean goThrough = true;
                Scanner sc = new Scanner(file.toFile());
                while (sc.hasNextLine()) {
                    if (String.valueOf(input.getText()).equals(sc.nextLine())) {
                        goThrough = false;
                        break;
                    }
                }
                sc.close();
                if (goThrough) {
                    if (!input.getText().equalsIgnoreCase("")) {

                        Files.write(file, (input.getText() + "\n").getBytes(), StandardOpenOption.APPEND);
                        addButton(input.getText());
                        input.setText("");
                        listPanel.updateUI();
                    }
                }
            } catch (IOException e1) {
                DialogBox.showDialogBox("Error!", e1.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
            }
        });


        topPanel.add(backButton);
        topPanel.add(addID);
        topPanel.add(input);
        topPanel.add(label);
        topPanel.add(enableWordSetting);
        topPanel.setBounds(0, 0, 542, 90);
        topPanel.setVisible(false);


        listScrollPane.setBounds(0, 90, 542, 532);
        listScrollPane.setPreferredSize(new Dimension(542, 532));
        listScrollPane.setVisible(false);

        enableWordSetting.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (allowedStringsBool) {
                    allowOption = enableWordSetting.getSelectedState();
                    System.out.println(allowOption);

                } else {
                    disallowOption = enableWordSetting.getSelectedState();
                }
            }
        });

        mainPanel.setBounds(0, 0, 542, 622);
        mainPanel.setBackground(Defaults.SUB_MAIN);


        panel.setLayout(null);
        panel.setDoubleBuffered(true);
        panel.setBounds(0, 0, 542, 1040);
        panel.setPreferredSize(new Dimension(542, 1040));
        panel.setBackground(Defaults.SUB_MAIN);

        rated.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                ratedOption = rated.getSelectedState();
            }
        });
        unrated.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                unratedOption = unrated.getSelectedState();
            }
        });
        disableDifficulties.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                disableOption = disableDifficulties.getSelectedState();
            }
        });
        disableLengths.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                disableLengthOption = disableLengths.getSelectedState();
            }
        });
        disableDifficulties.setChecked(true);
        disableLengths.setChecked(true);

        naIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (naIcon.getSelectedState()) {
                    excludedDifficulties.add("na");
                } else {
                    excludedDifficulties.remove("na");
                }
            }
        });


        autoIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (autoIcon.getSelectedState()) {
                    excludedDifficulties.add("auto");
                } else {
                    excludedDifficulties.remove("auto");
                }
            }
        });
        easyIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (easyIcon.getSelectedState()) {
                    excludedDifficulties.add("easy");
                } else {
                    excludedDifficulties.remove("easy");
                }
            }
        });
        normalIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (normalIcon.getSelectedState()) {
                    excludedDifficulties.add("normal");
                } else {
                    excludedDifficulties.remove("normal");
                }
            }
        });
        hardIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (hardIcon.getSelectedState()) {
                    excludedDifficulties.add("hard");
                } else {
                    excludedDifficulties.remove("hard");
                }
            }
        });
        harderIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (harderIcon.getSelectedState()) {
                    excludedDifficulties.add("harder");
                } else {
                    excludedDifficulties.remove("harder");
                }
            }
        });
        insaneIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (insaneIcon.getSelectedState()) {
                    excludedDifficulties.add("insane");
                } else {
                    excludedDifficulties.remove("insane");
                }
            }
        });
        easyDemonIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (easyDemonIcon.getSelectedState()) {
                    excludedDifficulties.add("easy demon");
                } else {
                    excludedDifficulties.remove("easy demon");
                }
            }
        });
        mediumDemonIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (mediumDemonIcon.getSelectedState()) {
                    excludedDifficulties.add("medium demon");
                } else {
                    excludedDifficulties.remove("medium demon");
                }
            }
        });
        hardDemonIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (hardDemonIcon.getSelectedState()) {
                    excludedDifficulties.add("hard demon");
                } else {
                    excludedDifficulties.remove("hard demon");
                }
            }
        });
        insaneDemonIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (insaneDemonIcon.getSelectedState()) {
                    excludedDifficulties.add("insane demon");
                } else {
                    excludedDifficulties.remove("insane demon");
                }
            }
        });
        extremeDemonIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (extremeDemonIcon.getSelectedState()) {
                    excludedDifficulties.add("extreme demon");
                } else {
                    excludedDifficulties.remove("extreme demon");
                }
            }
        });


        tiny.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tiny.getSelectedState()) {
                    excludedLengths.add("tiny");
                } else {
                    excludedLengths.remove("tiny");
                }
            }
        });
        shortL.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (shortL.getSelectedState()) {
                    excludedLengths.add("short");
                } else {
                    excludedLengths.remove("short");
                }
            }
        });
        medium.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (medium.getSelectedState()) {
                    excludedLengths.add("medium");
                } else {
                    excludedLengths.remove("medium");
                }
            }
        });
        longL.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (longL.getSelectedState()) {
                    excludedLengths.add("long");
                } else {
                    excludedLengths.remove("long");
                }
            }
        });
        XL.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (XL.getSelectedState()) {
                    excludedLengths.add("xl");
                } else {
                    excludedLengths.remove("xl");
                }
            }
        });

        minimumLikes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                minLikesOption = minimumLikes.getSelectedState();
                minLikesInput.setEditable(minLikesOption);
            }
        });
        maximumLikes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                maxLikesOption = maximumLikes.getSelectedState();
                maxLikesInput.setEditable(maxLikesOption);
            }
        });
        minimumObjects.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                minObjectsOption = minimumObjects.getSelectedState();
                minObjectsInput.setEditable(minObjectsOption);
            }
        });
        maximumObjects.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                maxObjectsOption = maximumObjects.getSelectedState();
                maxObjectsInput.setEditable(maxObjectsOption);
            }
        });

        minimumID.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                minIDOption = minimumID.getSelectedState();
                minIDInput.setEditable(minIDOption);
            }
        });
        maximumID.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                maxIDOption = maximumID.getSelectedState();
                maxIDInput.setEditable(maxIDOption);
            }
        });

        minLikesInput.setEditable(false);
        minLikesInput.setBounds(25, 523, 490, 32);
        minLikesInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        minLikesInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    minLikes = Integer.parseInt(minLikesInput.getText());
                } catch (NumberFormatException f) {
                    minLikes = 0;
                }
            }
        });
        maxLikesInput.setEditable(false);
        maxLikesInput.setBounds(25, 598, 490, 32);
        maxLikesInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        maxLikesInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    maxLikes = Integer.parseInt(maxLikesInput.getText());
                } catch (NumberFormatException f) {
                    maxLikes = 0;
                }
            }
        });
        minObjectsInput.setEditable(false);
        minObjectsInput.setBounds(25, 673, 490, 32);
        minObjectsInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        minObjectsInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    minObjects = Integer.parseInt(minObjectsInput.getText());
                } catch (NumberFormatException f) {
                    minObjects = 0;
                }
            }
        });
        maxObjectsInput.setEditable(false);
        maxObjectsInput.setBounds(25, 748, 490, 32);
        maxObjectsInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        maxObjectsInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    maxObjects = Integer.parseInt(maxObjectsInput.getText());
                } catch (NumberFormatException f) {
                    maxObjects = 0;
                }
            }
        });
        minIDInput.setEditable(false);
        minIDInput.setBounds(25, 823, 490, 32);
        minIDInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        minIDInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    minID = Integer.parseInt(minIDInput.getText());
                } catch (NumberFormatException f) {
                    minID = 0;
                }
            }
        });
        maxIDInput.setEditable(false);
        maxIDInput.setBounds(25, 898, 490, 32);
        maxIDInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        maxIDInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    maxID = Integer.parseInt(maxIDInput.getText());
                } catch (NumberFormatException f) {
                    maxID = 0;
                }
            }
        });

        allowedStrings.setBounds(25, 955, 490, 30);
        allowedStrings.setPreferredSize(new Dimension(490, 30));
        allowedStrings.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        allowedStrings.setUI(settingsButtonUI);
        allowedStrings.setForeground(Defaults.FOREGROUND);
        allowedStrings.setBackground(Defaults.BUTTON);
        allowedStrings.refresh();
        allowedStrings.setOpaque(false);
        allowedStrings.setBorder(BorderFactory.createEmptyBorder());
        allowedStrings.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        allowedStrings.addActionListener(e -> {
            i = 0;
            height = 0;
            allowedStringsBool = true;
            scrollPane.setVisible(false);
            listPanel.removeAll();
            enableWordSetting.setText("Only allow level titles that contain these");
            enableWordSetting.setChecked(allowOption);
            enableWordSetting.refresh();
            label.setText("Allowed: ");
            label.setBounds(60, 22, label.getPreferredSize().width + 5, label.getPreferredSize().height + 5);
            topPanel.setVisible(true);
            listScrollPane.setVisible(true);
            File file = new File(Defaults.saveDirectory + "\\GDBoard\\allowedStrings.txt");
            if (file.exists()) {
                Scanner sc = null;
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                }
                assert sc != null;
                while (sc.hasNextLine()) {
                    addButton(sc.nextLine());
                }
                sc.close();
            }

        });

        disallowedStrings.setBounds(25, 995, 490, 30);
        disallowedStrings.setPreferredSize(new Dimension(490, 30));
        disallowedStrings.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        disallowedStrings.setUI(settingsButtonUI);
        disallowedStrings.setForeground(Defaults.FOREGROUND);
        disallowedStrings.setBackground(Defaults.BUTTON);
        disallowedStrings.refresh();
        disallowedStrings.setOpaque(false);
        disallowedStrings.setBorder(BorderFactory.createEmptyBorder());
        disallowedStrings.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        disallowedStrings.addActionListener(e -> {
            i = 0;
            height = 0;
            allowedStringsBool = false;
            scrollPane.setVisible(false);
            listPanel.removeAll();
            enableWordSetting.setText("Don't allow level titles that contain these");
            enableWordSetting.setChecked(disallowOption);
            enableWordSetting.refresh();
            label.setText("Disallowed: ");
            label.setBounds(60, 22, label.getPreferredSize().width + 5, label.getPreferredSize().height + 5);
            topPanel.setVisible(true);
            listScrollPane.setVisible(true);
            File file = new File(Defaults.saveDirectory + "\\GDBoard\\disallowedStrings.txt");
            if (file.exists()) {
                Scanner sc = null;
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                }
                assert sc != null;
                while (sc.hasNextLine()) {
                    addButton(sc.nextLine());
                }
                sc.close();
            }

        });


        difficultyPanel.setBounds(-4, 170, 538, 100);
        difficultyPanel.setBackground(Defaults.TOP);


        lengthPanel.setBounds(0, 320, 534, 160);
        lengthPanel.setBackground(Defaults.TOP);

        lengthPanel.add(tiny);
        lengthPanel.add(shortL);
        lengthPanel.add(medium);
        lengthPanel.add(longL);
        lengthPanel.add(XL);

        panel.add(new SettingsTitle("$FILTERS_SETTINGS$"));
        panel.add(rated);
        panel.add(unrated);
        panel.add(disableDifficulties);
        panel.add(disableLengths);
        difficultyPanel.add(naIcon);
        difficultyPanel.add(autoIcon);
        difficultyPanel.add(easyIcon);
        difficultyPanel.add(normalIcon);
        difficultyPanel.add(hardIcon);
        difficultyPanel.add(harderIcon);
        difficultyPanel.add(insaneIcon);
        difficultyPanel.add(easyDemonIcon);
        difficultyPanel.add(mediumDemonIcon);
        difficultyPanel.add(hardDemonIcon);
        difficultyPanel.add(insaneDemonIcon);
        difficultyPanel.add(extremeDemonIcon);

        panel.add(difficultyPanel);
        panel.add(lengthPanel);

        panel.add(minimumLikes);
        panel.add(maximumLikes);
        panel.add(minimumObjects);
        panel.add(maximumObjects);
        panel.add(minimumID);
        panel.add(maximumID);
        panel.add(minLikesInput);
        panel.add(maxLikesInput);
        panel.add(minObjectsInput);
        panel.add(maxObjectsInput);
        panel.add(minIDInput);
        panel.add(maxIDInput);
        panel.add(allowedStrings);
        panel.add(disallowedStrings);

        mainPanel.add(scrollPane);
        mainPanel.add(listScrollPane);
        mainPanel.add(topPanel);
        return mainPanel;


    }

    public static void loadSettings() {
        ratedOption = Settings.getSettings("rated").asBoolean();
        rated.setChecked(ratedOption);
        unratedOption = Settings.getSettings("unrated").asBoolean();
        unrated.setChecked(unratedOption);
        disableOption = Settings.getSettings("disableDifficulties").asBoolean();
        disableLengthOption = Settings.getSettings("disableLengths").asBoolean();
        disableDifficulties.setChecked(disableOption);
        String excludedDifficultiesString = Settings.getSettings("difficultyFilter").asString();
        String excludedLengthsString = Settings.getSettings("lengthFilter").asString();
        if(excludedDifficultiesString.length() != 0) {
            excludedDifficulties = new ArrayList<>(Arrays.asList(excludedDifficultiesString.substring(1, excludedDifficultiesString.length() - 1).split(", ")));
        }
        if(excludedLengthsString.length() != 0) {
            excludedLengths = new ArrayList<>(Arrays.asList(excludedLengthsString.substring(1, excludedLengthsString.length() - 1).split(", ")));
        }
        minLikesOption = Settings.getSettings("minLikesOption").asBoolean();
        minimumLikes.setChecked(minLikesOption);
        minLikesInput.setEditable(minLikesOption);
        maxLikesOption = Settings.getSettings("maxLikesOption").asBoolean();
        maximumLikes.setChecked(maxLikesOption);
        maxLikesInput.setEditable(maxLikesOption);
        minObjectsOption = Settings.getSettings("minObjectsOption").asBoolean();
        minimumObjects.setChecked(minObjectsOption);
        minObjectsInput.setEditable(minObjectsOption);
        maxObjectsOption = Settings.getSettings("maxObjectsOption").asBoolean();
        maximumObjects.setChecked(maxObjectsOption);
        maxObjectsInput.setEditable(maxObjectsOption);
        minIDOption = Settings.getSettings("minIDOption").asBoolean();
        minimumID.setChecked(minIDOption);
        minIDInput.setEditable(minIDOption);
        maxIDOption = Settings.getSettings("maxIDOption").asBoolean();
        maximumID.setChecked(maxIDOption);
        maxIDInput.setEditable(maxIDOption);
        allowOption = Settings.getSettings("allowStrings").asBoolean();
        disallowOption = Settings.getSettings("disallowStrings").asBoolean();
        minLikes = Settings.getSettings("minLikes").asInteger();
        minLikesInput.setText(String.valueOf(minLikes));
        maxLikes = Settings.getSettings("maxLikes").asInteger();
        maxLikesInput.setText(String.valueOf(maxLikes));
        minObjects = Settings.getSettings("minObjects").asInteger();
        minObjectsInput.setText(String.valueOf(minObjects));
        maxObjects = Settings.getSettings("maxObjects").asInteger();
        maxObjectsInput.setText(String.valueOf(maxObjects));
        minID = Settings.getSettings("minID").asInteger();
        minIDInput.setText(String.valueOf(minID));
        maxID = Settings.getSettings("maxID").asInteger();
        maxIDInput.setText(String.valueOf(maxID));

        if (excludedLengths.contains("tiny")) {
            tiny.setChecked(true);
        }
        if (excludedLengths.contains("short")) {
            shortL.setChecked(true);
        }
        if (excludedLengths.contains("medium")) {
            medium.setChecked(true);
        }
        if (excludedLengths.contains("long")) {
            longL.setChecked(true);
        }
        if (excludedLengths.contains("xl")) {
            XL.setChecked(true);
        }
        if (excludedDifficulties.contains("na")) {
            naIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("auto")) {
            autoIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("easy")) {
            easyIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("normal")) {
            normalIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("hard")) {
            hardIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("harder")) {
            harderIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("insane")) {
            insaneIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("easy demon")) {
            easyDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("medium demon")) {
            mediumDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("hard demon")) {
            hardDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("insane demon")) {
            insaneDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("extreme demon")) {
            extremeDemonIcon.setChecked(true);
        }
    }

    public static void setSettings() {

        Settings.writeSettings("rated", String.valueOf(ratedOption));
        Settings.writeSettings("unrated", String.valueOf(unratedOption));
        Settings.writeSettings("disallowStrings", String.valueOf(disallowOption));
        Settings.writeSettings("allowStrings", String.valueOf(allowOption));
        Settings.writeSettings("disableDifficulties", String.valueOf(disableOption));
        Settings.writeSettings("minLikesOption", String.valueOf(minLikesOption));
        Settings.writeSettings("maxLikesOption", String.valueOf(maxLikesOption));
        Settings.writeSettings("minObjectsOption", String.valueOf(minObjectsOption));
        Settings.writeSettings("maxObjectsOption", String.valueOf(maxObjectsOption));
        Settings.writeSettings("minIDOption", String.valueOf(minIDOption));
        Settings.writeSettings("maxIDOption", String.valueOf(maxIDOption));
        Settings.writeSettings("disableLengths", String.valueOf(disableOption));
        Settings.writeSettings("minLikes", String.valueOf(minLikes));
        Settings.writeSettings("maxLikes", String.valueOf(maxLikes));
        Settings.writeSettings("minObjects", String.valueOf(minObjects));
        Settings.writeSettings("maxObjects", String.valueOf(maxObjects));
        Settings.writeSettings("minID", String.valueOf(minID));
        Settings.writeSettings("maxID", String.valueOf(maxID));
        Settings.writeSettings("difficultyFilter", excludedDifficulties.toString());
        Settings.writeSettings("lengthFilter", excludedLengths.toString());

    }

    public static void resizeHeight(int height) {

        height -= 38;

        mainPanel.setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getWidth(), height);

        scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(), scrollPane.getWidth(), height);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height));
        scrollPane.updateUI();
        listScrollPane.setBounds(listScrollPane.getX(), listScrollPane.getY(), listScrollPane.getWidth(), height - 90);
        listScrollPane.setPreferredSize(new Dimension(listScrollPane.getWidth(), height - 90));
        listScrollPane.updateUI();

    }

    private static void removeString(String string) {
        i--;
        if (i % 3 == 0) {
            height = height - 39;
            listPanel.setBounds(0, 0, 542, (int) (height + 4));
            listPanel.setPreferredSize(new Dimension(542, (int) (height + 4)));
            scrollPane.updateUI();
        }
        for (Component component : listPanel.getComponents()) {
            if (component instanceof CurvedButton) {
                if (((CurvedButton) component).getLText().equalsIgnoreCase(string)) {
                    listPanel.remove(component);
                    listPanel.updateUI();
                }
            }
        }
    }

    public static void addButton(String string) {
        i++;
        if ((i - 1) % 3 == 0) {
            height = height + 39;

            listPanel.setBounds(0, 0, 542, (int) (height + 4));
            listPanel.setPreferredSize(new Dimension(542, (int) (height + 4)));
            if (i > 0) {
                scrollPane.updateUI();
            }
        }
        Path file;
        if (allowedStringsBool) {
            file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\allowedStrings.txt");
        } else {
            file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\disallowedStrings.txt");
        }
        CurvedButton button = new CurvedButton(string);

        button.setBackground(Defaults.BUTTON);
        button.setUI(settingsButtonUI);
        button.setForeground(Defaults.FOREGROUND);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setPreferredSize(new Dimension(170, 35));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                SettingsWindow.run = false;
                new Thread(() -> {
                    String option;
                    if (allowedStringsBool) {
                        option = DialogBox.showDialogBox("Remove " + button.getLText() + "?", "<html>This will remove the ability to send levels containing this word.<html>", "", new String[]{"Yes", "No"});
                    } else {
                        option = DialogBox.showDialogBox("Remove " + button.getLText() + "?", "<html>This will allow levels containing this word.<html>", "", new String[]{"Yes", "No"});

                    }
                    if (option.equalsIgnoreCase("yes")) {
                        if (Files.exists(file)) {
                            try {
                                Path temp = Paths.get(Defaults.saveDirectory + "\\GDBoard\\_temp_");
                                PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
                                Files.lines(file)
                                        .filter(line -> !line.contains(button.getLText()))
                                        .forEach(out::println);
                                out.flush();
                                out.close();
                                Files.delete(file);
                                if (allowedStringsBool) {
                                    Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\GDBoard\\allowedStrings.txt"), StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\GDBoard\\disallowedStrings.txt"), StandardCopyOption.REPLACE_EXISTING);

                                }

                            } catch (IOException ex) {
                                ex.printStackTrace();
                                DialogBox.showDialogBox("Error!", ex.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
                            }
                        }
                        removeString(button.getLText());
                    }
                    SettingsWindow.run = true;
                }).start();
            }
        });
        button.refresh();
        listPanel.add(button);
        listPanel.updateUI();

    }
    private static ThemedCheckbox createButton(String text, int y) {

        ThemedCheckbox button = new ThemedCheckbox(text);
        button.setBounds(25, y, 490, 30);
        button.setForeground(Defaults.FOREGROUND);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.refresh();
        return button;
    }
    private static ThemedIconCheckbox createDifficultyButton(String difficulty) {

        Icon icon = new ImageIcon(Assets.difficultyIconsNormal.get(difficulty).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        ThemedIconCheckbox button = new ThemedIconCheckbox(icon);
        //button.setBounds(25, y, 490, 30);
        button.setForeground(Defaults.FOREGROUND);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setPreferredSize(new Dimension(36,80));
        button.refresh();
        return button;
    }

    public static void refreshUI() {
        listScrollPane.setBackground(Defaults.SUB_MAIN);
        difficultyPanel.setBackground(Defaults.TOP);
        lengthPanel.setBackground(Defaults.TOP);
        panel.setBackground(Defaults.SUB_MAIN);
        mainPanel.setBackground(Defaults.SUB_MAIN);
        scrollPane.setBackground(Defaults.SUB_MAIN);
        scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());

        addID.setForeground(Defaults.FOREGROUND);
        addID.setBackground(Defaults.BUTTON);
        label.setForeground(Defaults.FOREGROUND);
        listPanel.setBackground(Defaults.SUB_MAIN);
        listScrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());
        backButton.setForeground(Defaults.FOREGROUND);
        backButton.setBackground(Defaults.BUTTON);
        topPanel.setBackground(Defaults.TOP);
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

        for (Component component : listPanel.getComponents()) {
            if (component instanceof JButton) {
                for (Component component2 : ((JButton) component).getComponents()) {
                    if (component2 instanceof JLabel) {
                        component2.setForeground(Defaults.FOREGROUND);
                    }
                }
                component.setBackground(Defaults.MAIN);
            }
        }
        for (Component component : lengthPanel.getComponents()) {
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
    private static void setExcludedLength(String length, boolean state){
        if(state) excludedLengths.add(length);
        else excludedLengths.remove(length);
    }
    private static void setExcludedDifficulty(String difficulty, boolean state){
        if(state) excludedDifficulties.add(difficulty);
        else excludedDifficulties.remove(difficulty);
    }
}
