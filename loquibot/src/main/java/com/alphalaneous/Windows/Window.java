package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Settings.SettingData;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Tabs.ChatbotPages.CustomCommands;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Swing.Components.ContextMenu;
import com.alphalaneous.Tabs.ChatbotTab;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Theming.ThemedColor;
import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window {

    private static final JFrame windowFrame = new JFrame("loquibot");
    public static final JLayeredPane layeredContentPanel = new JLayeredPane() {
        //allows multiple components to overlap without drawing over the other
        @Override
        public boolean isOptimizedDrawingEnabled() {
            return false;
        }
    };
    private static final JPanel fullPanel = new JPanel();
    private static final JPanel tabPanel = new JPanel();
    private static final JPanel updatePanel = new JPanel();
    private static final JPanel mainContent = new JPanel();
    private static final JButtonUI selectUI = new JButtonUI();
    private static final JButtonUI buttonUI = new JButtonUI();
    private static final JPanel dialogBackgroundPanel = new JPanel();
    private static final JPanel backgroundColor = new JPanel();
    private static final JPanel componentLayer = new JPanel();
    private static final CurvedButton updateButton = createButton("\uF11A", "Update Available");
    private static final int width = 800, height = 660;


    public static void initFrame() {

        windowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("> Closing");
                Main.close();
            }
        });
        windowFrame.setIconImages(Main.getIconImages());
        windowFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        windowFrame.setMinimumSize(new Dimension(900, 500));

        if(SettingsHandler.getSettings("window").exists() && !SettingsHandler.getSettings("alwaysCenter").asBoolean()){

            String position = SettingsHandler.getSettings("window").asString();
            int winX = Integer.parseInt(position.split(",")[0]);
            int winY = Integer.parseInt(position.split(",")[1]);
            windowFrame.setLocation(winX, winY);

        }
        else{
            windowFrame.setLocationRelativeTo(null);
        }
        new Thread(Window::startComponentListener).start();
        startEventListener();

        layeredContentPanel.setBounds(0, 0, width, height);

        tabPanel.setBounds(0, 0, 50, height-60);
        tabPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        tabPanel.setBackground(new ThemedColor("color6", tabPanel, ThemedColor.BACKGROUND));

        updatePanel.setBounds(0, windowFrame.getHeight()-50, 50, 100);
        updatePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        updatePanel.setBackground(new ThemedColor("color6", updatePanel, ThemedColor.BACKGROUND));

        updateButton.setForeground(Color.GREEN);
        updateButton.setPreferredSize(new Dimension(40, 40));
        updateButton.setVisible(false);


        updateButton.addActionListener(e -> {
            try {
                SettingsHandler.writeSettings("hasUpdated", "true");
                Main.restart();
            }
            catch (Exception f){
                f.printStackTrace();
            }
        });

        updatePanel.add(updateButton);


        mainContent.setLayout(null);
        mainContent.setBounds(50, 0, width - 50, height);
        mainContent.setBackground(new ThemedColor("color", mainContent, ThemedColor.BACKGROUND));
        selectUI.setBackground(Defaults.COLOR);
        selectUI.setHover(Defaults.COLOR5);

        buttonUI.setBackground(Defaults.COLOR3);
        buttonUI.setHover(Defaults.COLOR5);

        dialogBackgroundPanel.setVisible(false);
        dialogBackgroundPanel.setOpaque(false);
        dialogBackgroundPanel.setLayout(null);
        dialogBackgroundPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!disableClickThrough) closeDialog();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!disableClickThrough) closeDialog();
            }
        });
        dialogBackgroundPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());

        backgroundColor.setBackground(new Color(0, 0, 0, 125));
        backgroundColor.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
        backgroundColor.setLayout(null);
        componentLayer.setOpaque(false);
        componentLayer.setLayout(null);
        componentLayer.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());

        dialogBackgroundPanel.add(backgroundColor);
        backgroundColor.add(componentLayer);


        fullPanel.setLayout(null);
        fullPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
        fullPanel.setOpaque(false);
        fullPanel.setBackground(new Color(0,0,0,0));

        layeredContentPanel.setOpaque(false);
        layeredContentPanel.setBackground(new Color(0,0,0,0));

        fullPanel.add(tabPanel);
        fullPanel.add(updatePanel);
        fullPanel.add(mainContent);

        layeredContentPanel.setLayer(fullPanel, 0);
        layeredContentPanel.setLayer(dialogBackgroundPanel, 1);

        layeredContentPanel.add(fullPanel, 0, -1);
        layeredContentPanel.add(dialogBackgroundPanel, 1, -1);
        windowFrame.add(layeredContentPanel);




    }
    private static final boolean dragging = false;

    public static void showUpdateButton(){
        updateButton.setVisible(true);
    }
    public static void refreshUI() {
        selectUI.setBackground(Defaults.COLOR);
        selectUI.setHover(Defaults.COLOR5);
        selectUI.setSelect(Defaults.COLOR2);
        buttonUI.setBackground(Defaults.COLOR3);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR2);

        updateButton.setBackground(Defaults.COLOR3);
        for(ListButton button : buttons){
            button.refreshUI();
        }
    }


    public static void loadTopComponent() {
        mainContent.getComponent(0).setVisible(true);
        for(Component component : tabPanel.getComponents()){
            if(component instanceof ListButton){
                ((ListButton) component).runMethod();
                break;
            }
        }
    }

    private static final ArrayList<ListButton> buttons = new ArrayList<>();

    private static ListButton createButton(ImageIcon icon) {
        ListButton button = new ListButton();
        button.setButtonIcon(icon);
        button.setIcon(icon);
        buttons.add(button);
        return button;
    }


    private static ListButton createButton(String text) {
        ListButton button = new ListButton();
        button.setText(text);
        button.setFont(Defaults.SYMBOLS.deriveFont(20f));
        buttons.add(button);
        return button;
    }

    public static JFrame getWindow() {
        return windowFrame;
    }

    private static void unloadAllComponents() {
        for (Component component : mainContent.getComponents()) {
            component.setVisible(false);
        }
    }

    public static void add(JComponent component, ImageIcon icon) {
        add(component, icon, null);
    }

    public static void add(JComponent component, String symbol) {
        add(component, symbol, null);
    }

    public static void add(JComponent component, String symbol, Function function) {
        add(component, function, createButton(symbol));
    }

    public static void add(JComponent component, ImageIcon icon, Function function) {
        add(component, function, createButton(icon));
    }


    private static void add(JComponent component, Function function, JButton button) {
        mainContent.add(component);
        button.addActionListener(e -> {
            unloadAllComponents();
            component.setVisible(true);
            if (function != null) {
                function.run();
            }
        });

        tabPanel.add(button);
        component.setVisible(false);
    }

    private static JComponent dialogComponent;
    private static boolean disableClickThrough = false;


    public static void showDialog(JComponent component) {
        showDialog(component, false, false);
    }

    public static void showDialog(JComponent component, boolean disableClickThrough){
        showDialog(component, disableClickThrough, false);
    }

    public static void showDialog(JComponent component, boolean disableClickThrough, boolean isSolid) {
        closeDialog();
        backgroundColor.setBackground(new Color(0, 0, 0, 125));
        Window.disableClickThrough = disableClickThrough;
        dialogComponent = component;
        component.setBounds(windowFrame.getWidth() / 2 - component.getWidth() / 2-8, windowFrame.getHeight() / 2 - component.getHeight() / 2 - 20, component.getWidth(), component.getHeight());
        componentLayer.add(component);
        if(isSolid) backgroundColor.setBackground(Defaults.COLOR6);

        dialogBackgroundPanel.setVisible(true);


    }

    public static void closeDialog() {
        if (dialogComponent != null) {
            componentLayer.remove(dialogComponent);
        }
        dialogBackgroundPanel.setVisible(false);
    }

    public static void setTitle(String title) {
        windowFrame.setTitle(title);
    }

    public static void addContextMenu(ContextMenu panel) {
        contextMenu = panel;

        int posXnew = posX;
        int posYnew = posY;

        if (posY + panel.getHeight() >= windowFrame.getHeight()) {
            posYnew = windowFrame.getHeight() - panel.getHeight() - 25;
        }
        if (posX + panel.getWidth() + 45 >= windowFrame.getWidth()) {
            posXnew = windowFrame.getWidth() - panel.getWidth() - 45;
        }
        panel.setBounds(posXnew, posYnew, panel.getWidth(), panel.getHeight());
        layeredContentPanel.add(contextMenu, 2, -1);
    }

    public static void destroyContextMenu() {
        if (contextMenu != null) {
            contextMenu.setVisible(false);
            layeredContentPanel.remove(contextMenu);
            contextMenu = null;
        }
    }

    private static int posX, posY;
    private static boolean inside = false;
    private static ContextMenu contextMenu;

    private static void startEventListener() {
        long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e.getID() == 503) {
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;
                    posX = me.getLocationOnScreen().x - windowFrame.getX() - 8;
                    posY = me.getLocationOnScreen().y - windowFrame.getY() - 31;
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

    private static void startComponentListener() {
        windowFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                //closeDialog();
                //resizing = true;

                int changeA = -88;
                int changeB = -50;
                int changeC = 0;
                int changeD = 0;

                if(Defaults.isMac()) {
                    changeA = 0;
                    changeB = 0;
                    changeC = 10;
                    changeD = 16;

                }

                tabPanel.setBounds(0, 0, 50, windowFrame.getHeight() + changeA);
                updatePanel.setBounds(0, windowFrame.getHeight() + changeA, 50, 50);
                layeredContentPanel.setBounds(0, 0, windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                mainContent.setBounds(50, 0, windowFrame.getWidth() + changeB + changeD, windowFrame.getHeight() + changeC);
                RequestsTab.resize(windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                SettingsTab.resize(windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                ChatbotTab.resize(windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                dialogBackgroundPanel.setBounds(0, 0, windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                backgroundColor.setBounds(0, 0, windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                componentLayer.setBounds(0, 0, windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                fullPanel.setBounds(0, 0, windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                if (dialogComponent != null) {
                    dialogComponent.setBounds(windowFrame.getWidth() / 2 - dialogComponent.getWidth() / 2-8, windowFrame.getHeight() / 2 - dialogComponent.getHeight() / 2 - 20, dialogComponent.getWidth(), dialogComponent.getHeight());
                }

                SettingsPage.resizeAll(windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC);
                ListView.resizeAll(new Dimension(windowFrame.getWidth() + changeD, windowFrame.getHeight() + changeC));
                CommandConfigCheckbox.resizeAll(windowFrame.getWidth() + changeD);
                TimerConfigCheckbox.resizeAll(windowFrame.getWidth() + changeD);
                KeywordConfigCheckbox.resizeAll(windowFrame.getWidth() + changeD);
                CustomCommands.LegacyCommandsLabel.resize(windowFrame.getWidth() + changeD);
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
                        Defaults.screenNum = Integer.parseInt(screen.getIDstring().replaceAll("Display", "").replace("\\", "").trim());
                    }
                }
            }
        });
    }

    public static void setOnTop(boolean onTop) {
        windowFrame.setAlwaysOnTop(onTop);
    }
    public static void setFocusable(boolean focusable){
        windowFrame.setFocusableWindowState(focusable);
    }

    public static void setVisible(boolean visible) {
        try {
            windowFrame.setVisible(visible);
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error opening loquibot: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            Main.close(true, false);
        }
    }

    public static void focus() {
        windowFrame.setAlwaysOnTop(true);
        windowFrame.setAlwaysOnTop(SettingsHandler.getSettings("onTop").asBoolean());
    }

    public static void setSettings() {
        SettingsHandler.writeSettings("window", windowFrame.getX() + "," + windowFrame.getY());
        SettingsHandler.writeSettings("windowState", String.valueOf(windowFrame.getExtendedState()));
        SettingsHandler.writeSettings("windowSize", windowFrame.getWidth() + "," + windowFrame.getHeight());
    }

    public static void loadSettings() {

        SettingData state = SettingsHandler.getSettings("windowState");

        if (!state.asString().equalsIgnoreCase("")) {
            int windowState = state.asInteger();
            windowFrame.setExtendedState(windowState);
        }

        SettingData size = SettingsHandler.getSettings("windowSize");
        if (!size.asString().equalsIgnoreCase("")) {
            String[] dim = size.asString().split(",");

            int newW = Integer.parseInt(dim[0]);
            int newH = Integer.parseInt(dim[1]);

            int winWidth = windowFrame.getWidth();
            int winHeight = windowFrame.getHeight();

            windowFrame.setSize(newW, newH);
            tabPanel.setBounds(0, 0, 50,  winHeight- 135);
            updatePanel.setBounds(0, winHeight-135, 50, 100);
            layeredContentPanel.setBounds(0, 0, winWidth, winHeight);
            mainContent.setBounds(50, 0, winWidth - 50, winHeight);


            new Thread(() -> {
                RequestsTab.resize(winWidth, winHeight);
                SettingsTab.resize(winWidth, winHeight);
                ChatbotTab.resize(winWidth, winHeight);
            }).start();



        }
        else {
            windowFrame.setSize(width, height + 30);
        }

    }

    public static class ListButton extends JButton {

        ImageIcon icon;

        ListButton() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBackground(Defaults.COLOR3);
            setUI(buttonUI);
            setForeground(Defaults.FOREGROUND_A);
            setBorder(BorderFactory.createEmptyBorder());
            setPreferredSize(new Dimension(50, 50));
            addActionListener(e -> runMethod());
        }

        public void setButtonIcon(ImageIcon icon){
            this.icon = icon;
            setIcon(icon);
        }

        public void runMethod() {

            for (Component component : tabPanel.getComponents()) {
                if (component instanceof ListButton) {
                    ((JButton) component).setUI(buttonUI);
                    component.setBackground(Defaults.COLOR3);
                }
            }
            setUI(selectUI);
            setBackground(Defaults.COLOR);
        }

        public void refreshUI(){
            if(!getUI().equals(buttonUI)) setBackground(Defaults.COLOR);
            else setBackground(Defaults.COLOR3);
            setForeground(Defaults.FOREGROUND_A);
            if(icon != null){
                if(!Defaults.isLight) setIcon(icon);
                else setIcon(invertImage(icon));
            }
        }
        public static ImageIcon invertImage(ImageIcon buttonIcon) {
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
    public static CurvedButton createButton(String icon, String tooltip) {
        CurvedButton button = new CurvedButton(icon, tooltip);
        button.setPreferredSize(new Dimension(50, 50));
        button.setUI(Defaults.defaultUI);
        button.setBackground(Defaults.COLOR);
        //button.setColorB("main");
        //button.setColorF("foreground");
        button.setOpaque(false);
        button.setForeground(Defaults.FOREGROUND_A);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.SYMBOLS.deriveFont(20f));
        return button;
    }
}
