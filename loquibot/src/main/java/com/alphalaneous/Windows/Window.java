package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Components.*;
import com.alphalaneous.Panels.*;
import com.alphalaneous.SettingsPanels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.alphalaneous.Defaults.defaultUI;

public class Window {

    private static int width = 465;
    private static int height = 630;
    private static int selectedID;
    private static int posX, posY;

    private static boolean inside = false;

    private static String selectedUsername;

    public static JFrame windowFrame = new JFrame();
    private static final JFrame attributionsFrame = new JFrame();
    private static final JFrame moderationFrame = new JFrame();
    private static final JFrame enterIDFrame = new JFrame();

    private static final JButtonUI selectUI = new JButtonUI();
    private static final JButtonUI buttonUI = new JButtonUI();

    public static final JLayeredPane windowLayeredPane = new JLayeredPane();

    private static final JPanel contentPanel = new JPanel(null);
    private static final JPanel windowPanel = new JPanel(null);
    private static final JPanel buttonPanel = new JPanel();
    private static final JPanel sideButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
    private static final JPanel iconPanel = new JPanel(null);
    private static final JPanel infoPanel = new JPanel(null);
    private static final JPanel modButtonsPanel = new JPanel();
    private static final JPanel idPanel = new JPanel(null);
    private static final JPanel idButtonsPanel = new JPanel();

    private static final JLabel usernameLabel = new JLabel();
    private static final JLabel levelNameLabel = new JLabel();
    private static final JLabel levelIDLabel = new JLabel();
    private static final JLabel idLabel = new JLabel("Enter ID:");
    private static final JLabel AlphalaneousLabel = new JLabel("Alphalaneous");
    private static final JLabel EncodedLuaLabel = new JLabel("EncodedLua");

    private static final FancyTextArea messageTextArea = new FancyTextArea(false, false);
    private static final FancyTextArea idTextArea = new FancyTextArea(true, false);

    private static final CurvedButtonAlt addIDButton = createCurvedButton("Add ID");

    private static ContextMenu contextMenu;

    public static void createPanel() {

        buttonUI.setBackground(Defaults.TOP);
        buttonUI.setHover(Defaults.BUTTON_HOVER);
        buttonUI.setSelect(Defaults.SELECT);

        JButton skip = createButton("\uF31B", "$SKIP_LEVEL_TOOLTIP$");
        skip.addActionListener(e -> RequestFunctions.skipFunction());

        JButton undo = createButton("\uF32A", "$UNDO_LEVEL_TOOLTIP$");
        undo.addActionListener(e -> RequestFunctions.undoFunction());

        JButton randNext = createButton("\uF2D8", "$NEXT_RANDOM_TOOLTIP$");
        randNext.addActionListener(e -> RequestFunctions.randomFunction());

        JButton clear = createButton("\uF0CE", "$CLEAR_TOOLTIP$");
        clear.addActionListener(e -> {
            SettingsWindow.run = false;
            RequestFunctions.clearFunction();
        });

        JButton toggleRequests = createButton("\uF186", "$TOGGLE_REQUESTS_TOOLTIP$");
        toggleRequests.addActionListener(e -> {
            SettingsWindow.run = false;
            RequestFunctions.requestsToggleFunction();
            if (Requests.requestsEnabled) {
                toggleRequests.setText("\uF186");
            } else {
                toggleRequests.setText("\uF184");
            }
        });

        CurvedButtonAlt delete = createCurvedButton("$DELETE$");
        delete.addActionListener(e -> Main.sendMessage("/delete " + Requests.levels.get(selectedID).getMessageID()));

        CurvedButtonAlt timeout = createCurvedButton("$TIMEOUT$");
        timeout.addActionListener(e -> Main.sendMessage("/timeout " + selectedUsername + " 600"));

        CurvedButtonAlt ban = createCurvedButton("$BAN$");
        ban.addActionListener(e -> Main.sendMessage("/ban " + selectedUsername));

        CurvedButtonAlt purge = createCurvedButton("$PURGE$");
        purge.addActionListener(e -> Main.sendMessage("/timeout " + selectedUsername + " 1"));

        windowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.close();
            }
        });
        windowFrame.setIconImages(Main.getIconImages());
        windowFrame.setTitle("loquibot - 0");
        windowFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        windowFrame.setSize(width + 250, height + 32 + 230);
        windowFrame.setMinimumSize(new Dimension(800, 660));
        windowFrame.setLayout(null);
        windowFrame.getContentPane().setBackground(Defaults.MAIN);
        windowFrame.getRootPane().setBackground(Defaults.MAIN);
        windowFrame.setBackground(Defaults.MAIN);
        windowFrame.setLocationRelativeTo(null);

        contentPanel.setBounds(40, 0, width - 2, height);
        contentPanel.setBackground(Defaults.SUB_MAIN);
        contentPanel.setLayout(null);

        startComponentListener();
        startEventListener();

        sideButtons.setBounds(0, -5, 40, windowFrame.getHeight() - 15);
        sideButtons.setBackground(Defaults.TOP);

        buttonPanel.setBounds(width - 60, 0, 50, windowFrame.getHeight() - 70);
        buttonPanel.setBackground(Defaults.SUB_MAIN);
        buttonPanel.add(skip);
        buttonPanel.add(undo);
        buttonPanel.add(randNext);
        buttonPanel.add(clear);
        buttonPanel.add(toggleRequests);

        iconPanel.setBounds(width - 60, windowFrame.getHeight() - 95, 50, 50);
        iconPanel.setBackground(new Color(0, 0, 0, 0));
        iconPanel.setOpaque(false);

        infoPanel.setBounds(0, 0, 486, 195);
        infoPanel.setBackground(Defaults.MAIN);
        infoPanel.add(levelNameLabel);
        infoPanel.add(levelIDLabel);
        infoPanel.add(usernameLabel);
        infoPanel.add(messageTextArea);

        moderationFrame.setLayout(null);
        moderationFrame.setTitle("loquibot - Moderation");
        moderationFrame.setIconImages(Main.getIconImages());
        moderationFrame.setSize(500, 300);
        moderationFrame.setResizable(false);
        moderationFrame.add(infoPanel);
        moderationFrame.add(modButtonsPanel);

        messageTextArea.setEditable(false);
        messageTextArea.setBounds(6, 55, 471, 130);
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);

        usernameLabel.setForeground(Defaults.FOREGROUND);
        usernameLabel.setBounds(7, 5, 473, 40);
        usernameLabel.setFont(Defaults.MAIN_FONT.deriveFont(24f));
        levelNameLabel.setForeground(Defaults.FOREGROUND);
        levelNameLabel.setBounds(473, 0, 473, 40);
        levelNameLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        levelIDLabel.setForeground(Defaults.FOREGROUND);
        levelIDLabel.setBounds(473, 16, 473, 40);
        levelIDLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        modButtonsPanel.setBackground(Defaults.TOP);
        modButtonsPanel.setBounds(0, 195, 486, 105);
        modButtonsPanel.add(delete);
        modButtonsPanel.add(purge);
        modButtonsPanel.add(timeout);
        modButtonsPanel.add(ban);

        enterIDFrame.setLayout(null);
        enterIDFrame.setTitle("loquibot - Add ID");
        enterIDFrame.setIconImages(Main.getIconImages());
        enterIDFrame.setSize(200, 170);
        enterIDFrame.setResizable(false);

        idTextArea.setBounds(6, 35, 172, 30);

        int condition = JComponent.WHEN_FOCUSED;
        InputMap inputMap = idTextArea.getInputMap(condition);
        ActionMap actionMap = idTextArea.getActionMap();

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        inputMap.put(enterKey, enterKey.toString());
        actionMap.put(enterKey.toString(), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea textArea = (JTextArea) e.getSource();
                if (!idTextArea.getText().equalsIgnoreCase("")) {
                    new Thread(() -> {
                        Requests.addRequest(Long.parseLong(idTextArea.getText()), TwitchAccount.display_name, true, true, idTextArea.getText(), null, true);
                        textArea.setText("");
                    }).start();
                }
            }
        });

        idLabel.setForeground(Defaults.FOREGROUND);
        idLabel.setBounds(7, 5, 172, 30);
        idLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        idPanel.setBackground(Defaults.MAIN);
        idPanel.add(idLabel);
        idPanel.add(idTextArea);

        addIDButton.addActionListener(e -> {
            if (!idTextArea.getText().equalsIgnoreCase("")) {
                new Thread(() -> {
                    Requests.addRequest(Long.parseLong(idTextArea.getText()), TwitchAccount.display_name, true, true, idTextArea.getText(), null, true);
                    idTextArea.setText("");
                }).start();
            }
        });

        idButtonsPanel.setBackground(Defaults.TOP);
        idButtonsPanel.add(addIDButton);

        idPanel.setBounds(0, 0, 188, 70);
        idButtonsPanel.setBounds(0, 70, 188, 70);

        enterIDFrame.add(idPanel);
        enterIDFrame.add(idButtonsPanel);

        HighlightButton settingsA = new HighlightButton(Assets.settings.getImage());
        HighlightButton discordA = new HighlightButton(Assets.discord.getImage());
        HighlightButton donateA = new HighlightButton(Assets.donate.getImage());

        settingsA.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    enterIDFrame.setLocationRelativeTo(windowFrame);
                    enterIDFrame.setVisible(true);
                }
            }
        });

        settingsA.addActionListener(e -> {
            SettingsWindow.run = false;
            SettingsWindow.showSettings();
            windowPanel.setVisible(false);
        });

        donateA.addActionListener(e -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + "https://www.paypal.me/xAlphalaneous");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        discordA.addActionListener(e -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + "https://discord.gg/x2awccH");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        sideButtons.add(settingsA);
        sideButtons.add(donateA);
        sideButtons.add(discordA);

        attributionsFrame.setTitle("loquibot - Attributions");
        attributionsFrame.setResizable(false);
        attributionsFrame.setSize(new Dimension(400, 280));
        attributionsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        attributionsFrame.getContentPane().setBackground(Defaults.MAIN);
        attributionsFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                attributionsFrame.setVisible(false);
            }
        });
        attributionsFrame.setLayout(null);


        JLabel loquibotIcon = new JLabel(Assets.loquibot);
        loquibotIcon.setBounds(0, 0, 50, 50);
        loquibotIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel AlphalaneousIcon = new JLabel(new ImageIcon(makeRoundedCorner(convertToBufferedImage(Assets.Alphalaneous.getImage()))));
        AlphalaneousIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        AlphalaneousIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Utilities.openLink("https://twitter.com/alphalaneous");
            }
        });

        AlphalaneousLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        AlphalaneousLabel.setForeground(Defaults.FOREGROUND);
        AlphalaneousLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));
        AlphalaneousLabel.setBounds(130, 32, 300, 40);
        AlphalaneousLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Utilities.openLink("https://twitter.com/alphalaneous");
            }

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public void mouseEntered(MouseEvent e) {
                Font originalFont = AlphalaneousLabel.getFont();
                Map attributes = originalFont.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                AlphalaneousLabel.setFont(originalFont.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                AlphalaneousLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));
            }
        });

        JLabel AlphalaneousSubtext = new JLabel("Client Development");
        AlphalaneousSubtext.setForeground(Defaults.FOREGROUND2);
        AlphalaneousSubtext.setFont(Defaults.MAIN_FONT.deriveFont(15f));
        AlphalaneousSubtext.setBounds(130, 72, 300, 40);

        JLabel EncodedLuaIcon = new JLabel(new ImageIcon(makeRoundedCorner(convertToBufferedImage(Assets.EncodedLua.getImage()))));
        EncodedLuaIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        EncodedLuaIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Utilities.openLink("https://twitter.com/EncodedLua");
            }
        });

        EncodedLuaLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        EncodedLuaLabel.setForeground(Defaults.FOREGROUND);
        EncodedLuaLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));
        EncodedLuaLabel.setBounds(130, 122, 300, 40);
        EncodedLuaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Utilities.openLink("https://twitter.com/EncodedLua");
            }

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public void mouseEntered(MouseEvent e) {
                Font originalFont = EncodedLuaLabel.getFont();
                Map attributes = originalFont.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                EncodedLuaLabel.setFont(originalFont.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                EncodedLuaLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));
            }
        });

        JLabel EncodedLuaSubtext = new JLabel("Server Development");
        EncodedLuaSubtext.setForeground(Defaults.FOREGROUND2);
        EncodedLuaSubtext.setFont(Defaults.MAIN_FONT.deriveFont(15f));
        EncodedLuaSubtext.setBounds(130, 162, 300, 40);

        AlphalaneousIcon.setBounds(30, 30, 80, 80);
        EncodedLuaIcon.setBounds(30, 120, 80, 80);

        attributionsFrame.setIconImages(Main.getIconImages());
        attributionsFrame.add(AlphalaneousIcon);
        attributionsFrame.add(AlphalaneousLabel);
        attributionsFrame.add(AlphalaneousSubtext);
        attributionsFrame.add(EncodedLuaIcon);
        attributionsFrame.add(EncodedLuaLabel);
        attributionsFrame.add(EncodedLuaSubtext);

        loquibotIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                attributionsFrame.setLocation(Defaults.screenSize.x + Defaults.screenSize.width / 2 - attributionsFrame.getWidth() / 2, Defaults.screenSize.y + Defaults.screenSize.height / 2 - attributionsFrame.getHeight() / 2);

                attributionsFrame.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loquibotIcon.setIcon(new ImageIcon(Assets.loquibot.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loquibotIcon.setIcon(Assets.loquibot);
            }
        });
        iconPanel.add(loquibotIcon);

        contentPanel.add(LevelsPanel.getReqWindow());
        contentPanel.add(CommentsPanel.getComWindow());
        contentPanel.add(InfoPanel.getInfoWindow());
        contentPanel.add(iconPanel);
        contentPanel.add(buttonPanel);
        windowPanel.add(sideButtons);
        windowPanel.add(contentPanel);
        windowPanel.setBounds(0, 0, 5000, 5000);
        windowLayeredPane.setLayout(null);
        windowLayeredPane.add(windowPanel, 0, 0);
        windowLayeredPane.setBounds(0, 0, 5000, 5000);

        windowFrame.getContentPane().add(windowLayeredPane);
    }

    public static void refresh() {
        if(windowFrame != null) {
            windowFrame.invalidate();
            windowFrame.validate();
        }
    }

    public static void setOnTop(boolean onTop) {
        windowFrame.setFocusableWindowState(!onTop);
        windowFrame.setAlwaysOnTop(onTop);
    }

    public static void sendCommandResponse(Path path) {
        new Thread(() -> {

            try {
                String response = Command.run(Files.readString(path, StandardCharsets.UTF_8));
                if (!response.equalsIgnoreCase("")) {
                    Main.sendMessage(response);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }).start();
    }

    public static void showMainPanel() {
        windowPanel.setVisible(true);
    }

    public static void showModPane(int pos) {
        if (Requests.levels.size() != 0) {
            selectedUsername = String.valueOf(Requests.levels.get(pos).getRequester());
            selectedID = pos;
            usernameLabel.setText(String.valueOf(Requests.levels.get(pos).getRequester()));
            levelNameLabel.setText(String.valueOf(Requests.levels.get(pos).getLevelData().name()));
            levelNameLabel.setBounds(473 - levelNameLabel.getPreferredSize().width, 0, 473, 40);
            levelIDLabel.setText("(" + Requests.levels.get(pos).getLevelData().id() + ")");
            levelIDLabel.setBounds(473 - levelIDLabel.getPreferredSize().width, 16, 473, 40);
            messageTextArea.setText(Requests.levels.get(pos).getMessage());
            messageTextArea.clearUndo();
            moderationFrame.setVisible(true);
            moderationFrame.setLocation(Defaults.screenSize.x + Defaults.screenSize.width / 2 - moderationFrame.getWidth() / 2, Defaults.screenSize.y + Defaults.screenSize.height / 2 - moderationFrame.getHeight() / 2);
        }
    }

    public static void refreshUI() {
        windowFrame.setBackground(Defaults.MAIN);
        windowFrame.getContentPane().setBackground(Defaults.MAIN);

        AlphalaneousLabel.setForeground(Defaults.FOREGROUND);
        EncodedLuaLabel.setForeground(Defaults.FOREGROUND);
        attributionsFrame.getContentPane().setBackground(Defaults.MAIN);
        selectUI.setBackground(Defaults.SELECT);
        selectUI.setHover(Defaults.BUTTON_HOVER);
        selectUI.setSelect(Defaults.SELECT);
        buttonUI.setBackground(Defaults.TOP);
        buttonUI.setHover(Defaults.BUTTON_HOVER);
        buttonUI.setSelect(Defaults.SELECT);
        sideButtons.setBackground(Defaults.TOP);
        contentPanel.setBackground(Defaults.SUB_MAIN);
        buttonPanel.setBackground(Defaults.SUB_MAIN);
        infoPanel.setBackground(Defaults.MAIN);
        messageTextArea.refresh_();
        modButtonsPanel.setBackground(Defaults.TOP);

        idPanel.setBackground(Defaults.MAIN);
        idLabel.setForeground(Defaults.FOREGROUND);
        idButtonsPanel.setBackground(Defaults.TOP);
        addIDButton.setBackground(Defaults.MAIN);
        addIDButton.setForeground(Defaults.FOREGROUND);
        idTextArea.refresh_();
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(Defaults.MAIN);
                component.setForeground(Defaults.FOREGROUND);
            }
        }
        for (Component component : sideButtons.getComponents()) {
            if (component instanceof HighlightButton) {
                ((HighlightButton) component).refresh();
            }
        }
        for (Component component : modButtonsPanel.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(Defaults.MAIN);
                component.setForeground(Defaults.FOREGROUND);
            }
        }
        for (Component component : infoPanel.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(Defaults.FOREGROUND);
            }
        }
    }

    public static void focus() {
        windowFrame.setAlwaysOnTop(true);
        windowFrame.setAlwaysOnTop(PersonalizationSettings.onTopOption);
    }

    public static void resetCommentSize() {
        CommentsPanel.getComWindow().setBounds(400, 0, CommentsPanel.getComWindow().getWidth(), 600);

    }

    private static void startEventListener(){
        long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e.getID() == 503) {
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;
                    posX = me.getLocationOnScreen().x - windowFrame.getX() - 8;
                    posY = me.getLocationOnScreen().y - windowFrame.getY() - 30;
                    if (contextMenu != null) {
                        int panelX = contextMenu.getX();
                        int panelY = contextMenu.getY();

                        Rectangle panelRect = new Rectangle(panelX, panelY, contextMenu.getWidth(), contextMenu.getHeight());
                        inside = panelRect.getBounds().contains(posX, posY);
                    }
                }

            }
            if (e.getID() == 501) {
                if (!inside) {
                    destroyContextMenu();
                }
            }
        }, eventMask);
    }

    private static void startComponentListener(){
        windowFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                windowPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
                contentPanel.setBounds(40, 0, windowFrame.getWidth() - 10, windowFrame.getHeight() - 38);
                buttonPanel.setBounds(windowFrame.getWidth() - 110, 0, 50, windowFrame.getHeight() - 70);
                sideButtons.setBounds(0, -5, 40, windowFrame.getHeight() - 15);
                iconPanel.setBounds(windowFrame.getWidth() - 110, windowFrame.getHeight() - 95, 50, 50);
                SettingsWindow.resize(windowFrame.getWidth(), windowFrame.getHeight());
                RequestsSettings.resizeHeight(windowFrame.getHeight());
                ChaosModeSettings.resizeHeight(windowFrame.getHeight());
                CommandSettings.resizeHeight(windowFrame.getHeight());
                ChannelPointSettings.resizeHeight(windowFrame.getHeight());
                FiltersSettings.resizeHeight(windowFrame.getHeight());
                BlockedSettings.resizeHeight(windowFrame.getHeight());
                BlockedUserSettings.resizeHeight(windowFrame.getHeight());
                BlockedCreatorSettings.resizeHeight(windowFrame.getHeight());
                RequestsLog.resizeHeight(windowFrame.getHeight());

                LevelsPanel.resizeButtons(windowFrame.getWidth() - 415);
                LevelsPanel.getReqWindow().setBounds(0, 0, windowFrame.getWidth() - 415, windowFrame.getHeight() - 152);
                CommentsPanel.getComWindow().setBounds(windowFrame.getWidth() - 415, 0, CommentsPanel.getComWindow().getWidth(), windowFrame.getHeight() + 2);
                CommentsPanel.resetDimensions(CommentsPanel.getComWindow().getWidth(), windowFrame.getHeight() + 2);
                InfoPanel.resetDimensions(LevelsPanel.getReqWindow().getWidth(), InfoPanel.getInfoWindow().getHeight());
                InfoPanel.getInfoWindow().setBounds(0, LevelsPanel.getReqWindow().getHeight() + 1, LevelsPanel.getReqWindow().getWidth(), InfoPanel.getInfoWindow().getHeight());
            }

            public void componentMoved(ComponentEvent evt) {
                GraphicsDevice[] screens = GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getScreenDevices();
                int frameX = windowFrame.getX();
                int frameY = windowFrame.getY();
                Point mouse = new Point(frameX, frameY);
                for (GraphicsDevice screen : screens) {
                    if (screen.getDefaultConfiguration().getBounds().contains(mouse)) {
                        Defaults.screenNum = Integer.parseInt(screen.getIDstring().replaceAll("Display", "").replace("\\", ""));
                    }
                }
            }
        });
    }

    public static void addContextMenu(ContextMenu panel) {
        contextMenu = panel;

        int posXnew = posX;
        int posYnew = posY;

        if (posY + panel.getHeight() + 45 >= Window.height) {
            posYnew = Window.height - panel.getHeight() - 45;
        }
        if (posX + panel.getWidth() + 45 >= Window.width) {
            posXnew = Window.width - panel.getWidth() - 45;
        }
        panel.setBounds(posXnew, posYnew, panel.getWidth(), panel.getHeight());

        windowLayeredPane.add(contextMenu, 1, 0);

    }

    public static void destroyContextMenu() {
        if (contextMenu != null) {
            contextMenu.setVisible(false);
            for (Component c : windowLayeredPane.getComponentsInLayer(1)) {
                windowLayeredPane.remove(c);
            }
            contextMenu = null;
        }
    }

    private static CurvedButtonAlt createCurvedButton(String text) {
        CurvedButtonAlt button = new CurvedButtonAlt(text);
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(Defaults.MAIN);
        button.setUI(defaultUI);
        button.setForeground(Defaults.FOREGROUND);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 30, 50));
        return button;
    }

    private static RoundedJButton createButton(String icon, String tooltip) {
        RoundedJButton button = new RoundedJButton(icon, tooltip);
        button.setPreferredSize(new Dimension(50, 50));
        button.setUI(defaultUI);
        button.setBackground(Defaults.MAIN);
        button.setColorB("main");
        button.setColorF("foreground");
        button.setOpaque(false);
        button.setForeground(Defaults.FOREGROUND);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.SYMBOLS.deriveFont(20f));
        return button;
    }

    public static void setSettings() {
        Settings.setWindowSettings("Window", windowFrame.getX() + "," + windowFrame.getY() + "," + false + "," + true);
        Settings.writeSettings("windowState", String.valueOf(windowFrame.getExtendedState()));
        Settings.writeSettings("windowSize", windowFrame.getWidth() + "," + windowFrame.getHeight());
    }

    public static void showAttributions() {
        attributionsFrame.setLocation(Defaults.screenSize.x + Defaults.screenSize.width / 2 - attributionsFrame.getWidth() / 2, Defaults.screenSize.y + Defaults.screenSize.height / 2 - attributionsFrame.getHeight() / 2);
        attributionsFrame.setVisible(true);
    }

    public static void loadSettings() {
        if (!Settings.getSettings("windowState").asString().equalsIgnoreCase("")) {
            int windowState = Settings.getSettings("windowState").asInteger();
            windowFrame.setExtendedState(windowState);
        }

        String windowLocation = Settings.getSettings("window").asString();
        if(!windowLocation.equalsIgnoreCase("")){
            windowFrame.setLocation(Integer.parseInt(windowLocation.split(",")[0]), Integer.parseInt(windowLocation.split(",")[1]));
        }

        if (!Settings.getSettings("windowSize").asString().equalsIgnoreCase("")) {
            String[] dim = Settings.getSettings("windowSize").asString().split(",");
            int newW = Integer.parseInt(dim[0]);
            int newH = Integer.parseInt(dim[1]);
            width = newW;
            height = newH;
            windowFrame.setSize(newW, newH);
            contentPanel.setBounds(40, 0, newW - 10, newH - 8);
            buttonPanel.setBounds(newW - 60, 0, 50, windowFrame.getHeight() - 70);
            sideButtons.setBounds(0, -5, 40, windowFrame.getHeight() - 15);
            iconPanel.setBounds(newW - 60, windowFrame.getHeight() - 95, 50, 50);
        } else {
            int newW = 465;
            int newH = 630;
            width = newW;
            height = newH;
            windowFrame.setSize(newW, newH);
            contentPanel.setBounds(40, 0, newW - 10, newH - 8);
            buttonPanel.setBounds(newW - 110, 0, 50, windowFrame.getHeight() - 70);
            sideButtons.setBounds(0, -5, 40, windowFrame.getHeight() - 15);
            iconPanel.setBounds(newW - 110, windowFrame.getHeight() - 95, 50, 50);

        }
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(0, 0, 80.0, 80.0));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
