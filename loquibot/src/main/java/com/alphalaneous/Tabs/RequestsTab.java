package com.alphalaneous.Tabs;

import com.alphalaneous.*;
import com.alphalaneous.Components.*;
import com.alphalaneous.Panels.*;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.OfficerWindow;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;

import static com.alphalaneous.Defaults.defaultUI;

public class RequestsTab {

    private static int selectedID;

    private static String selectedUsername;

    private static final JPanel attributionsFrame = new JPanel();
    private static final JPanel moderationFrame = new JPanel();

    private static final JButtonUI selectUI = new JButtonUI();
    private static final JButtonUI buttonUI = new JButtonUI();


    private static final JPanel contentPanel = new JPanel(null);
    private static final JPanel windowPanel = new JPanel(null);
    private static final JPanel buttonPanel = new JPanel();
    private static final JPanel sideButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
    private static final JPanel iconPanel = new JPanel(null);
    private static final JPanel infoPanel = new JPanel(null);
    private static final JPanel modButtonsPanel = new JPanel();
    private static final JPanel idPanel = new JPanel(null);
    private static final JPanel idButtonsPanel = new JPanel();
    private static final JPanel idInputPanel = new JPanel();

    private static final JLabel usernameLabel = new JLabel();
    private static final JLabel levelNameLabel = new JLabel();
    private static final JLabel levelIDLabel = new JLabel();
    private static final JLabel idLabel = new JLabel("Enter ID:");
    private static final JLabel AlphalaneousLabel = new JLabel("Alphalaneous");

    private static final FancyTextArea messageTextArea = new FancyTextArea(false, false);
    private static final FancyTextArea idTextArea = new FancyTextArea(true, false);

    private static final CurvedButtonAlt addIDButton = createCurvedButton("Add ID");

    //private static final CommentsPanel commentsPanel = new CommentsPanel();
    private static final LevelsPanel levelsPanel = new LevelsPanel();
    //private static final InfoPanel levelInfoPanel = new InfoPanel();

    private static final JButton officerMenuButton = createButton("\uF4F3", "$officer_TOOLTIP$");

    private static final JButton undo = createButton("\uF32A", "$UNDO_LEVEL_TOOLTIP$");

    private static final JButton toggleRequests = createButton("\uF186", "$TOGGLE_REQUESTS_TOOLTIP$");

    public static void createPanel() {

        buttonUI.setBackground(Defaults.COLOR6);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);

        JButton skip = createButton("\uF31B", "$SKIP_LEVEL_TOOLTIP$");
        skip.addActionListener(e -> RequestFunctions.skipFunction());

        skip.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    idTextArea.setText("");
                    idTextArea.requestFocusInWindow();
                    DialogBox.showDialogBox(idInputPanel);
                }
            }
        });

        undo.addActionListener(e -> RequestFunctions.undoFunction());

        JButton randNext = createButton("\uF2D8", "$NEXT_RANDOM_TOOLTIP$");
        randNext.addActionListener(e -> RequestFunctions.randomFunction());

        JButton clear = createButton("\uF0CE", "$CLEAR_TOOLTIP$");
        clear.addActionListener(e -> RequestFunctions.clearFunction());

        toggleRequests.addActionListener(e -> toggle());
        officerMenuButton.addActionListener(e -> OfficerWindow.showWindow());
        officerMenuButton.setVisible(false);
        officerMenuButton.setFont(Defaults.SYMBOLS.deriveFont(24f));

        CurvedButtonAlt delete = createCurvedButton("$DELETE$");
        delete.addActionListener(e -> Main.sendMessage("/delete " + RequestsTab.getRequest(selectedID).getLevelData().getMessageID()));

        CurvedButtonAlt timeout = createCurvedButton("$TIMEOUT$");
        timeout.addActionListener(e -> Main.sendMessage("/timeout " + selectedUsername + " 600"));

        CurvedButtonAlt ban = createCurvedButton("$BAN$");
        ban.addActionListener(e -> Main.sendMessage("/ban " + selectedUsername));

        CurvedButtonAlt purge = createCurvedButton("$PURGE$");
        purge.addActionListener(e -> Main.sendMessage("/timeout " + selectedUsername + " 1"));

        CurvedButtonAlt cancel = createCurvedButton("$CANCEL$");
        cancel.addActionListener(e -> DialogBox.closeDialogBox());

        int width = 465;
        int height = 630;
        contentPanel.setBounds(0, 0, width - 2, height);
        contentPanel.setBackground(new Color(0,0,0,0));
        contentPanel.setLayout(null);
        contentPanel.setDoubleBuffered(true);
        contentPanel.setOpaque(false);

        sideButtons.setBounds(0, -5, 40, 100 - 15);
        sideButtons.setBackground(Defaults.COLOR6);
        sideButtons.setDoubleBuffered(true);

        buttonPanel.setBounds(width - 70, 0, 60, 100);
        buttonPanel.setBackground(Defaults.COLOR3);
        buttonPanel.setDoubleBuffered(true);
        buttonPanel.add(skip);
        buttonPanel.add(undo);
        buttonPanel.add(randNext);
        buttonPanel.add(clear);
        buttonPanel.add(toggleRequests);
        buttonPanel.add(officerMenuButton);

        iconPanel.setBounds(width - 65, 100 - 95, 80, 50);
        iconPanel.setBackground(new Color(0, 0, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.setDoubleBuffered(true);

        infoPanel.setBounds(0, 0, 486, 195);
        infoPanel.setBackground(Defaults.COLOR3);
        infoPanel.add(levelNameLabel);
        infoPanel.add(levelIDLabel);
        infoPanel.add(usernameLabel);
        infoPanel.add(messageTextArea);
        infoPanel.setDoubleBuffered(true);

        moderationFrame.setLayout(null);
        moderationFrame.setBounds(0,0,485,255);
        moderationFrame.add(infoPanel);
        moderationFrame.add(modButtonsPanel);

        messageTextArea.setEditable(false);
        messageTextArea.setBounds(6, 55, 471, 130);
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);

        usernameLabel.setForeground(Defaults.FOREGROUND_A);
        usernameLabel.setBounds(7, 5, 473, 40);
        usernameLabel.setFont(Defaults.MAIN_FONT.deriveFont(24f));
        levelNameLabel.setForeground(Defaults.FOREGROUND_A);
        levelNameLabel.setBounds(473, 0, 473, 40);
        levelNameLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        levelIDLabel.setForeground(Defaults.FOREGROUND_A);
        levelIDLabel.setBounds(473, 16, 473, 40);
        levelIDLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        modButtonsPanel.setBackground(Defaults.COLOR3);
        modButtonsPanel.setBounds(0, 195, 486, 105);
        modButtonsPanel.add(delete);
        modButtonsPanel.add(purge);
        modButtonsPanel.add(timeout);
        modButtonsPanel.add(ban);
        modButtonsPanel.add(cancel);

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
                        Requests.addRequest(Long.parseLong(idTextArea.getText()), TwitchAccount.display_name, true, true, idTextArea.getText(), null, -1,true);
                        textArea.setText("");
                    }).start();
                }
            }
        });

        idLabel.setForeground(Defaults.FOREGROUND_A);
        idLabel.setBounds(7, 5, 172, 30);
        idLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        idPanel.setBackground(Defaults.COLOR3);
        idPanel.add(idLabel);
        idPanel.add(idTextArea);

        addIDButton.addActionListener(e -> {
            if (!idTextArea.getText().equalsIgnoreCase("")) {
                new Thread(() -> {
                    Requests.addRequest(Long.parseLong(idTextArea.getText()), TwitchAccount.display_name, true, true, idTextArea.getText(), null, -1,true);
                    idTextArea.setText("");
                }).start();
            }
        });

        idButtonsPanel.setBackground(Defaults.COLOR3);
        idButtonsPanel.add(addIDButton);

        idPanel.setBounds(0, 0, 188, 70);
        idButtonsPanel.setBounds(0, 70, 188, 70);

        idInputPanel.setLayout(null);
        idInputPanel.setBounds(0,0,185,130);
        idInputPanel.add(idPanel);
        idInputPanel.add(idButtonsPanel);

        attributionsFrame.setBounds(0,0, 400, 120);
        attributionsFrame.setBackground(Defaults.COLOR3);
        attributionsFrame.setLayout(null);

        JLabel loquibotIcon = new JLabel(Assets.loquibot);
        loquibotIcon.setBounds(0, 0, 50, 50);
        loquibotIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel AlphalaneousIcon = new JLabel(new ImageIcon(makeRoundedCorner(convertToBufferedImage(Assets.Alphalaneous.getImage()))));
        AlphalaneousIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        AlphalaneousIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Utilities.openURL(new URI("https://twitter.com/alphalaneous"));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        AlphalaneousLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        AlphalaneousLabel.setForeground(Defaults.FOREGROUND_A);
        AlphalaneousLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));
        AlphalaneousLabel.setBounds(130, 27, 300, 40);
        AlphalaneousLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Utilities.openURL(new URI("https://twitter.com/alphalaneous"));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
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

        JLabel AlphalaneousSubtext = new JLabel("Creator and Developer");
        AlphalaneousSubtext.setForeground(Defaults.FOREGROUND_B);
        AlphalaneousSubtext.setFont(Defaults.MAIN_FONT.deriveFont(15f));
        AlphalaneousSubtext.setBounds(130, 62, 300, 40);

        AlphalaneousIcon.setBounds(30, 20, 80, 80);

        attributionsFrame.add(AlphalaneousIcon);
        attributionsFrame.add(AlphalaneousLabel);
        attributionsFrame.add(AlphalaneousSubtext);

        loquibotIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                DialogBox.showDialogBox(attributionsFrame);
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

        windowPanel.setOpaque(false);
        windowPanel.setBackground(new Color(0,0,0,0));

        contentPanel.add(levelsPanel);
        contentPanel.add(LevelDetailsPanel.getPanel());
        //contentPanel.add(commentsPanel);
        //contentPanel.add(levelInfoPanel);
        contentPanel.add(iconPanel);
        contentPanel.add(buttonPanel);
        //windowPanel.add(sideButtons);
        windowPanel.add(contentPanel);
        windowPanel.setBounds(0,0, 100, 100);
        Window.add(windowPanel, Assets.requests);
        //windowFrame.getContentPane().add(windowLayeredPane);
    }

    public static void toggle(){
        if (Main.programLoaded) {

            boolean doAnnounce = Settings.getSettings("isMod").asBoolean();

            if (Requests.requestsEnabled) {
                Requests.requestsEnabled = false;
                Main.sendMessage(Utilities.format("🟥 | $REQUESTS_OFF_TOGGLE_MESSAGE$"), doAnnounce);

            } else {
                Requests.requestsEnabled = true;
                Main.sendMessage(Utilities.format("🟩 | $REQUESTS_ON_TOGGLE_MESSAGE$"), doAnnounce);
            }
        }
        if (Requests.requestsEnabled) {
            toggleRequests.setText("\uF186");
        } else {
            toggleRequests.setText("\uF184");
        }
    }

    public static void addRequest(LevelButton button){
        levelsPanel.addButton(button);
    }
    public static void addRequest(BasicLevelButton button){
        levelsPanel.addButton(button);
    }
    public static void addRequest(LevelButton button, int pos){
        levelsPanel.addButton(button, pos);
    }
    public static void clearRequests(){
        levelsPanel.clearRequests();
        levelsPanel.updateUI();

    }
    public static int getQueueSize(){
        return levelsPanel.getQueueSize();
    }
    public static void movePosition(int pos, int newPos){
        levelsPanel.movePosition(pos, newPos);
    }
    public static LevelButton getRequest(int pos){
        return levelsPanel.getButton(pos);
    }
    public static BasicLevelButton getRequestBasic(int pos){
        return levelsPanel.getButtonBasic(pos);
    }
    public static int getLevelPosition(LevelButton button){
        int pos = 0;
        for(Component component : button.getParent().getComponents()){
            if(component.equals(button)){
                return pos;
            }
            pos++;
        }
        return -1;
    }
    public static void removeRequest(int pos){
        levelsPanel.removeRequest(pos);
        levelsPanel.updateUI();
    }
    public static void setRequestSelect(int pos){
        levelsPanel.setSelect(pos);
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
        if (getQueueSize() != 0) {
            if(!Settings.getSettings("basicMode").asBoolean()) {
                selectedUsername = String.valueOf(getRequest(pos).getLevelData().getRequester());
                selectedID = pos;
                usernameLabel.setText(String.valueOf(getRequest(pos).getLevelData().getRequester()));
                levelNameLabel.setText(String.valueOf(getRequest(pos).getLevelData().getGDLevel().name()));
                levelNameLabel.setBounds(473 - levelNameLabel.getPreferredSize().width, 0, 473, 40);
                levelIDLabel.setText("(" + getRequest(pos).getLevelData().getGDLevel().id() + ")");
                levelIDLabel.setBounds(473 - levelIDLabel.getPreferredSize().width, 16, 473, 40);
                messageTextArea.setText(getRequest(pos).getLevelData().getMessage());
                messageTextArea.clearUndo();
                DialogBox.showDialogBox(moderationFrame);
            }
            else {
                selectedUsername = String.valueOf(getRequestBasic(pos).getRequester());
                selectedID = pos;
                usernameLabel.setText(String.valueOf(getRequestBasic(pos).getRequester()));
                levelNameLabel.setText("Unknown");
                //levelNameLabel.setBounds(473 - levelNameLabel.getPreferredSize().width, 0, 473, 40);
                levelIDLabel.setText("(" + getRequestBasic(pos).getID() + ")");
                levelIDLabel.setBounds(473 - levelIDLabel.getPreferredSize().width, 16, 473, 40);
                messageTextArea.setText("Unknown");
                messageTextArea.clearUndo();
                DialogBox.showDialogBox(moderationFrame);
            }
        }
    }

    public static void refreshUI() {
        //commentsPanel.refreshUI();
        levelsPanel.refreshUI();
        //levelInfoPanel.refreshUI();
        AlphalaneousLabel.setForeground(Defaults.FOREGROUND_A);
        attributionsFrame.setBackground(Defaults.COLOR3);
        selectUI.setBackground(Defaults.COLOR4);
        selectUI.setHover(Defaults.COLOR5);
        selectUI.setSelect(Defaults.COLOR4);
        buttonUI.setBackground(Defaults.COLOR6);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);
        sideButtons.setBackground(Defaults.COLOR6);
        //contentPanel.setBackground(Defaults.COLOR);
        buttonPanel.setBackground(Defaults.COLOR3);
        infoPanel.setBackground(Defaults.COLOR3);
        messageTextArea.refresh_();
        modButtonsPanel.setBackground(Defaults.COLOR3);

        idPanel.setBackground(Defaults.COLOR3);
        idLabel.setForeground(Defaults.FOREGROUND_A);
        idButtonsPanel.setBackground(Defaults.COLOR3);
        addIDButton.setBackground(Defaults.COLOR);
        addIDButton.setForeground(Defaults.FOREGROUND_A);
        idTextArea.refresh_();
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(Defaults.COLOR);
                component.setForeground(Defaults.FOREGROUND_A);
            }
        }
        for (Component component : sideButtons.getComponents()) {
            if (component instanceof HighlightButton) {
                ((HighlightButton) component).refresh();
            }
        }
        for (Component component : modButtonsPanel.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(Defaults.COLOR);
                component.setForeground(Defaults.FOREGROUND_A);
            }
        }
        for (Component component : infoPanel.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(Defaults.FOREGROUND_A);
            }
        }
        officerMenuButton.setForeground(Color.BLUE);
    }

    public static void resize(int width, int height){
        windowPanel.setBounds(0,0, width, height);
        contentPanel.setBounds(0,0,width,height);
        //commentsPanel.resizeHeight(width - 425, height);
        buttonPanel.setBounds(width-130, 0, buttonPanel.getWidth(), height);
        iconPanel.setBounds(width - 125, height - 95, 80, 50);
        levelsPanel.resizePanel(width - 650, height-47);
        LevelDetailsPanel.setPositionAndHeight(width-650, height);
        //levelInfoPanel.resetBounds(0, levelsPanel.getHeight(), levelsPanel.getWidth(), levelInfoPanel.getWindow().getHeight());

    }

    public static void setOfficerVisible(){
        officerMenuButton.setVisible(true);
    }

    public static LevelsPanel getLevelsPanel(){
        return levelsPanel;
    }


    private static CurvedButtonAlt createCurvedButton(String text) {
        CurvedButtonAlt button = new CurvedButtonAlt(text);
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(Defaults.COLOR);
        button.setUI(defaultUI);
        button.setForeground(Defaults.FOREGROUND_A);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 40, 50));
        return button;
    }

    private static RoundedJButton createButton(String icon, String tooltip) {
        RoundedJButton button = new RoundedJButton(icon, tooltip);
        button.setPreferredSize(new Dimension(50, 50));
        button.setUI(defaultUI);
        button.setBackground(Defaults.COLOR);
        button.setColorB("main");
        button.setColorF("foreground");
        button.setOpaque(false);
        button.setForeground(Defaults.FOREGROUND_A);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.SYMBOLS.deriveFont(20f));
        return button;
    }

    public static void showAttributions() {
        DialogBox.showDialogBox(attributionsFrame);
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
