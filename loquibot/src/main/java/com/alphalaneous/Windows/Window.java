package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Interactive.MediaShare.MediaShare;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Tabs.ChatbotPages.CustomCommands;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Swing.Components.ContextMenu;
import com.alphalaneous.Tabs.ChatbotTab;
import com.alphalaneous.Tabs.MediaShareTab;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Theming.ThemedColor;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.sun.jna.platform.WindowUtils;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.border.Border;
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
    private static final CurvedButton updateButton = MediaShareTab.createButton("\uF11A", "Update Available");
    private static JPanel controlsPanel;
    private static final int width = 800, height = 660;
    private static final CurvedButton playButton = MediaShareTab.createButton("\uF184","Play/Pause");
    private static final JLabel duration = new JLabel();
    private static final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 300, 0);
    private static final JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 10000, 0);
    private static final JLabel volumeLabel = new JLabel("\uF0E2");


    private static final CurvedButton button = MediaShareTab.createButton("\uF18F", "View Media Controls");

    private static final JPanel blankPanel = new JPanel(){{
        setBackground(new Color(0,0,0,0));
        setPreferredSize(new Dimension(40,40));
        setOpaque(false);
    }};


    public static void initFrame() {

        windowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.close();
            }
        });
        windowFrame.setIconImages(Main.getIconImages());
        windowFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        windowFrame.setMinimumSize(new Dimension(900, 500));

        if(SettingsHandler.getSettings("window").exists()){
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

        updatePanel.setBounds(0, windowFrame.getHeight()-135, 50, 100);
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
        updatePanel.add(blankPanel);


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

        controlsPanel = new JPanel(){
            public final int pixels = 10;
            {
                Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
                setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
                setBackground(Defaults.COLOR6);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                int shade = 0;
                int topOpacity = 80;

                Graphics2D g2d = (Graphics2D) g.create();
                LoadingPane.applyRenderingProperties(g2d);

                for (int i = 0; i < pixels; i++) {
                    g.setColor(new Color(shade, shade, shade, ((topOpacity / pixels) * i)));
                    g.drawRect(i, i, this.getWidth() - ((i * 2)+1), this.getHeight() - ((i * 2) + 1));
                }
                g.setColor(getBackground());
                g.fillRoundRect(pixels-5, pixels-5,this.getWidth()-pixels*2+10, this.getHeight()-pixels*2+10, 10,10);
            }
        };

        controlsPanel.addMouseListener(new MouseAdapter() {});
        controlsPanel.setBounds(65, getWindow().getHeight()-60, getWindow().getWidth()-160, 60);
        layeredContentPanel.add(controlsPanel, 1, -1);
        controlsPanel.setVisible(false);

        controlsPanel.setLayout(null);

        playButton.setBounds(10,10,40,40);
        controlsPanel.add(playButton);

        playButton.addActionListener(e -> MediaShare.togglePause());

        button.setPreferredSize(new Dimension(40,40));
        updatePanel.add(button);

        button.addActionListener(e -> controlsPanel.setVisible(!controlsPanel.isVisible()));

        duration.setBounds(60, 10, 200,40);
        duration.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        duration.setForeground(Defaults.FOREGROUND_B);
        Window.setTime("0:00 / 0:00");

        slider.setBounds(150, 10, 500, 40);
        slider.setUI(new LightSliderUI(slider));
        slider.setOpaque(false);
        slider.setBackground(new Color(0,0,0,0));
        slider.setBorder(BorderFactory.createEmptyBorder());
        slider.setEnabled(false);
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                dragging = false;
            }
        });
        slider.addChangeListener(e -> {
            if(dragging) MediaShare.setTime(slider.getValue()/10d);

        });

        volumeLabel.setFont(Defaults.SYMBOLS.deriveFont(20f));
        volumeLabel.setForeground(Defaults.FOREGROUND_A);
        volumeLabel.setBounds(480, 10, 40, 40);

        volumeSlider.setBounds(510, 10, 100, 40);
        volumeSlider.setUI(new LightSliderUI(slider));
        volumeSlider.setOpaque(false);
        volumeSlider.setBackground(new Color(0,0,0,0));
        volumeSlider.setBorder(BorderFactory.createEmptyBorder());


        float volume = 1;
        if(SettingsHandler.getSettings("mediaVolume").exists()){
            volume = SettingsHandler.getSettings("mediaVolume").asFloat();
        }

        volumeSlider.setValue((int) (volume * 10000));
        volumeSlider.addChangeListener(e -> {
            MediaShare.setVolume(volumeSlider.getValue()/10000f);
            SettingsHandler.writeSettings("mediaVolume", String.valueOf(volumeSlider.getValue()/10000f));
        });

        controlsPanel.add(duration);
        controlsPanel.add(slider);
        controlsPanel.add(volumeSlider);
        controlsPanel.add(volumeLabel);
        controlsPanel.setVisible(false);

    }
    private static boolean dragging = false;

    public static void setSliderValue(int value){
        if(!dragging){
            slider.setValue(value);
        }
    }

    public static void setSliderInfo(boolean playing, int length){
        if(!playing) {
            slider.setValue(0);
            slider.setEnabled(false);
        }
        else{
            slider.setEnabled(true);
            slider.setMaximum(length*10);
        }
    }

    public static void setPlayButtonIcon(boolean playing){
        if(playing) playButton.setText("\uF186");
        else playButton.setText("\uF184");
    }

    public static void setTime(String time){
        duration.setText(time);
    }

    public static void showUpdateButton(){
        blankPanel.setVisible(false);
        updateButton.setVisible(true);
    }

    public static void refreshUI() {
        selectUI.setBackground(Defaults.COLOR);
        selectUI.setHover(Defaults.COLOR5);
        selectUI.setSelect(Defaults.COLOR2);
        buttonUI.setBackground(Defaults.COLOR3);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR2);
        duration.setForeground(Defaults.FOREGROUND_B);
        updateButton.setBackground(Defaults.COLOR3);
        controlsPanel.setBackground(Defaults.COLOR6);
        button.setBackground(Defaults.COLOR);
        playButton.setBackground(Defaults.COLOR);
        button.setForeground(Defaults.FOREGROUND_A);
        playButton.setForeground(Defaults.FOREGROUND_A);
        volumeLabel.setForeground(Defaults.FOREGROUND_A);
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


    public static void showMediaControls() {
        controlsPanel.setVisible(true);
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
                tabPanel.setBounds(0, 0, 50, windowFrame.getHeight() - 135);
                updatePanel.setBounds(0, windowFrame.getHeight()-135, 50, 100);
                layeredContentPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
                mainContent.setBounds(50, 0, windowFrame.getWidth() - 50, windowFrame.getHeight());
                RequestsTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
                MediaShareTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
                SettingsTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
                ChatbotTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
                dialogBackgroundPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
                backgroundColor.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
                componentLayer.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
                fullPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
                if (dialogComponent != null) {
                    dialogComponent.setBounds(windowFrame.getWidth() / 2 - dialogComponent.getWidth() / 2-8, windowFrame.getHeight() / 2 - dialogComponent.getHeight() / 2 - 20, dialogComponent.getWidth(), dialogComponent.getHeight());
                }
                controlsPanel.setBounds(65, getWindow().getHeight()-110, getWindow().getWidth()-160, 60);
                slider.setBounds(150, 10, getWindow().getWidth()-480, 40);
                volumeSlider.setBounds(getWindow().getWidth()-280, 10, 100, 40);
                volumeLabel.setBounds(getWindow().getWidth()-310, 10, 40, 40);

                SettingsPage.resizeAll(windowFrame.getWidth(), windowFrame.getHeight());
                ListView.resizeAll(new Dimension(windowFrame.getWidth(), windowFrame.getHeight()));
                CommandConfigCheckbox.resizeAll(windowFrame.getWidth());
                TimerConfigCheckbox.resizeAll(windowFrame.getWidth());
                KeywordConfigCheckbox.resizeAll(windowFrame.getWidth());
                CustomCommands.LegacyCommandsLabel.resize(windowFrame.getWidth());
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
        if (!SettingsHandler.getSettings("windowState").asString().equalsIgnoreCase("")) {
            int windowState = SettingsHandler.getSettings("windowState").asInteger();
            windowFrame.setExtendedState(windowState);
        }

        if (!SettingsHandler.getSettings("windowSize").asString().equalsIgnoreCase("")) {
            String[] dim = SettingsHandler.getSettings("windowSize").asString().split(",");
            int newW = Integer.parseInt(dim[0]);
            int newH = Integer.parseInt(dim[1]);

            windowFrame.setSize(newW, newH);
            tabPanel.setBounds(0, 0, 50, windowFrame.getHeight() - 135);
            updatePanel.setBounds(0, windowFrame.getHeight()-135, 50, 100);
            layeredContentPanel.setBounds(0, 0, windowFrame.getWidth(), windowFrame.getHeight());
            mainContent.setBounds(50, 0, windowFrame.getWidth() - 50, windowFrame.getHeight());
            RequestsTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
            MediaShareTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
            SettingsTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
            ChatbotTab.resize(windowFrame.getWidth(), windowFrame.getHeight());
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
}
