package com.alphalaneous;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ContextMenu;
import com.alphalaneous.Components.SidebarSwitcher;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Components.ChatPanel;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Pages.ChatPage;
import com.alphalaneous.Pages.Page;
import com.alphalaneous.Pages.SettingsSubPages.ChatSettingsPage;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Utilities.SettingsHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Window {

    private static final ChatPanel chatPanel = new ChatPanel();
    private static final ThemeableJPanel contentPane = new ThemeableJPanel();
    private static final JFrame frame = new JFrame("Loquibot");
    private static final JPanel componentLayer = new JPanel();
    private static final JPanel backgroundColor = new JPanel(){
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(0,0,0,125));
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    };
    private static JComponent dialogComponent;
    private static final JPanel dialogBackgroundPanel = new JPanel();

    private static boolean disableClickThrough = false;
    public static final JLayeredPane layeredContentPanel = new JLayeredPane() {
        //allows multiple components to overlap without drawing over the other
        @Override
        public boolean isOptimizedDrawingEnabled() {
            return false;
        }
    };

    @OnLoad(order = -1)
    public static void init(){

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize(frame.getWidth(), frame.getHeight());
                super.componentResized(e);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                onMove(frame.getX(), frame.getY());
                super.componentMoved(e);
            }
        });
        ArrayList<Image> iconImages = new ArrayList<>();
        iconImages.add(Assets.getImage("loquibot-small-icon").getImage());
        iconImages.add(Assets.getImage("loquibot-medium-icon").getImage());
        iconImages.add(Assets.getImage("loquibot-large-icon").getImage());

        frame.setIconImages(iconImages);

        frame.addWindowStateListener(e -> {
            SettingsHandler.writeSettings("windowState", String.valueOf(e.getNewState()));
        });
        startEventListener();

        contentPane.setLayout(new BorderLayout(5,5));
        contentPane.add(SidebarSwitcher.getPanel());
        //contentPane.add(chatPanel, BorderLayout.EAST);
        contentPane.setBackground("background-lighter");

        initDialogComponents();

        int winSizeWidth = 1000;
        int winSizeHeight = 600;

        if(SettingsHandler.getSettings("windowSizeWidth").exists()){
            winSizeWidth = SettingsHandler.getSettings("windowSizeWidth").asInteger();
        }
        if(SettingsHandler.getSettings("windowSizeHeight").exists()){
            winSizeHeight = SettingsHandler.getSettings("windowSizeHeight").asInteger();
        }

        frame.setSize(winSizeWidth, winSizeHeight);

        int winPosX;
        int winPosY;

        if(SettingsHandler.getSettings("windowPosX").exists() && SettingsHandler.getSettings("windowPosY").exists()){

            winPosX = SettingsHandler.getSettings("windowPosX").asInteger();
            winPosY = SettingsHandler.getSettings("windowPosY").asInteger();
            frame.setLocation(winPosX, winPosY);
        }
        else{
            frame.setLocationRelativeTo(null);
        }

        if(SettingsHandler.getSettings("windowState").exists()){
            frame.setExtendedState(SettingsHandler.getSettings("windowState").asInteger());
            frame.setState(JFrame.NORMAL);

        }

        layeredContentPanel.setBounds(0, 0, winSizeWidth, winSizeHeight);
        layeredContentPanel.setBackground(new Color(0,0,0,0));
        layeredContentPanel.setOpaque(false);

        layeredContentPanel.setLayer(contentPane, 0);
        layeredContentPanel.setLayer(dialogBackgroundPanel, 1);

        layeredContentPanel.add(contentPane, 0, -1);
        layeredContentPanel.add(dialogBackgroundPanel, 1, -1);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                Main.onExit();
            }
        });

        frame.add(layeredContentPanel);
        frame.setMinimumSize(new Dimension(900,700));


    }

    public static void setVisible(boolean visible){
        frame.setVisible(visible);
    }

    public static void loadTwitchChat(String username){

        //chatPanel.loadTwitchChat(username);

    }
    public static void loadYouTubeChat(String streamID){

        //chatPanel.loadYouTubeChat(streamID);

    }
    public static void onResize(int width, int height){
        setCorrectedBounds(contentPane, width, height);
        setCorrectedBounds(componentLayer, width, height);
        setCorrectedBounds(dialogBackgroundPanel, width, height);
        setCorrectedBounds(backgroundColor, width, height);
        ChatSettingsPage.getScrollPane().setPreferredSize(new Dimension(width - 492, height-380));
        setCorrectedBounds(ChatSettingsPage.getCommandsPanel(), width - 492, height-380);

        if(dialogComponent != null) {
            dialogComponent.setBounds(frame.getWidth() / 2 - dialogComponent.getWidth() / 2 - 8, frame.getHeight() / 2 - dialogComponent.getHeight() / 2 - 20, dialogComponent.getWidth(), dialogComponent.getHeight());
        }
        for(Page page : SidebarSwitcher.getPages()){
            setCorrectedBounds(page, 10, 0,width - 92, height);
        }

        for(ChatPageComponent page : ChatPage.getPages()){
            setCorrectedBounds(page, 0, 0,width - 92, height-71);
        }

        for(ChatPageComponent page : StreamInteractionsPage.getPages()){
            setCorrectedBounds(page, 0, 0,width - 92, height-71);
        }

        if(frame.getExtendedState() != Frame.MAXIMIZED_BOTH && frame.getState() != Frame.MAXIMIZED_VERT && frame.getState() != Frame.MAXIMIZED_HORIZ) {
            SettingsHandler.writeSettings("windowSizeWidth", String.valueOf(width));
            SettingsHandler.writeSettings("windowSizeHeight", String.valueOf(height));
        }
        frame.revalidate();
    }

    public static void onMove(int x, int y){
        if(frame.getExtendedState() != Frame.MAXIMIZED_BOTH && frame.getState() != Frame.MAXIMIZED_VERT && frame.getState() != Frame.MAXIMIZED_HORIZ) {
            SettingsHandler.writeSettings("windowPosX", String.valueOf(x));
            SettingsHandler.writeSettings("windowPosY", String.valueOf(y));
        }
    }

    public static boolean isVisible(){
        return frame.isVisible();
    }

    public static void setCorrectedBounds(JComponent component, int width, int height){
        setCorrectedBounds(component, 0,0, width, height);
    }

    public static void setCorrectedBounds(JComponent component, int x, int y, int width, int height){
        component.setBounds(x,y,width-15, height-38);
    }

    public static void initDialogComponents(){

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
        dialogBackgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        backgroundColor.setLayout(null);
        backgroundColor.setOpaque(false);

        componentLayer.setOpaque(false);
        componentLayer.setLayout(null);
        componentLayer.setBounds(0, 0, frame.getWidth(), frame.getHeight());


        dialogBackgroundPanel.add(backgroundColor);
        backgroundColor.add(componentLayer);
    }

    private static int posX, posY;
    private static boolean inside = false;

    private static void startEventListener() {
        long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e.getID() == 503) {
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;
                    posX = me.getLocationOnScreen().x - frame.getX() - 8;
                    posY = me.getLocationOnScreen().y - frame.getY() - 31;
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

    public static void setChatVisible(boolean visible){

        //if(visible){
        //    chatPanel.setVisible(true);
        //    chatPanel.setPreferredSize(new Dimension(400,600));
        //}
        //else {
            chatPanel.setVisible(false);
            chatPanel.setPreferredSize(new Dimension(0,0));
        //}
    }



    public static void showDialog(JComponent component) {
        showDialog(component, false);
    }


    public static void showDialog(JComponent component, boolean disableClickThrough) {

        closeDialog();
        Window.disableClickThrough = disableClickThrough;
        dialogComponent = component;
        component.setBounds(frame.getWidth() / 2 - component.getWidth() / 2-8, frame.getHeight() / 2 - component.getHeight() / 2 - 20, component.getWidth(), component.getHeight());
        componentLayer.add(component);

        dialogBackgroundPanel.setVisible(true);
    }

    public static void closeDialog() {
        if (dialogComponent != null) {
            componentLayer.remove(dialogComponent);
        }
        dialogBackgroundPanel.setVisible(false);
    }

    public static JFrame getFrame(){
        return frame;
    }

    public static void revalidate(){
        frame.revalidate();
        frame.repaint();
    }

    private static ContextMenu contextMenu;
    public static void addContextMenu(ContextMenu panel, Point pos) {
        contextMenu = panel;

        panel.setBounds(pos.x, pos.y, panel.getWidth(), panel.getHeight());
        layeredContentPanel.add(contextMenu, 2, -1);
    }

    public static void addContextMenu(ContextMenu panel) {
        contextMenu = panel;

        int posXnew = posX;
        int posYnew = posY;

        if (posY + panel.getHeight() >= frame.getHeight()) {
            posYnew = frame.getHeight() - panel.getHeight() - 25;
        }
        if (posX + panel.getWidth() + 45 >= frame.getWidth()) {
            posXnew = frame.getWidth() - panel.getWidth() - 45;
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

}
