package com.alphalaneous.Tabs;

import com.alphalaneous.*;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Images.Assets;
import com.alphalaneous.Interactive.MediaShare.MediaShare;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.FancyTextArea;
import com.alphalaneous.Swing.Components.JButtonUI;
import com.alphalaneous.Swing.Components.RoundedJButton;
import com.alphalaneous.Swing.Components.VideoButton;
import com.alphalaneous.Swing.Components.VideoDetailsPanel;
import com.alphalaneous.Swing.Components.VideosPanel;
import com.alphalaneous.Services.YouTube.YouTubeVideo;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.alphalaneous.Utils.Defaults.defaultUI;

public class MediaShareTab {

    private static final JPanel attributionsFrame = new JPanel();

    private static final JButtonUI selectUI = new JButtonUI();
    private static final JButtonUI buttonUI = new JButtonUI();

    private static final JPanel contentPanel = new JPanel(null);
    private static final JPanel windowPanel = new JPanel(null);
    private static final JPanel buttonPanel = new JPanel();
    private static final JPanel sideButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
    private static final JPanel iconPanel = new JPanel(null);
    private static final JPanel infoPanel = new JPanel(null);

    private static final JLabel usernameLabel = new JLabel();
    private static final JLabel levelNameLabel = new JLabel();
    private static final JLabel levelIDLabel = new JLabel();
    private static final JLabel AlphalaneousLabel = new JLabel("Alphalaneous");

    private static final FancyTextArea messageTextArea = new FancyTextArea(false, false);

    private static final JButton undo = createButton("\uF32A", "$UNDO_LEVEL_TOOLTIP$");

    private static final JButton toggleMedia = createButton("\uF186", "$TOGGLE_MEDIA_TOOLTIP$");

    private static final VideosPanel videosPanel = new VideosPanel();


    public static void createPanel() {

        buttonUI.setBackground(Defaults.COLOR6);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);

        JButton skip = createButton("\uF31B", "$SKIP_MEDIA_TOOLTIP$");
        skip.addActionListener(e ->MediaShare.removeMedia(VideoButton.selectedID));

        undo.addActionListener(e -> RequestFunctions.undoFunction());

        JButton randNext = createButton("\uF2D8", "$NEXT_RANDOM_TOOLTIP$");
        randNext.addActionListener(e -> RequestFunctions.randomFunction());

        JButton clear = createButton("\uF0CE", "$CLEAR_MEDIA_TOOLTIP$");
        clear.addActionListener(e -> MediaShare.clearMedia(false));

        toggleMedia.addActionListener(e -> toggle());


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
        //buttonPanel.add(undo);
        //buttonPanel.add(randNext);
        buttonPanel.add(clear);
        buttonPanel.add(toggleMedia);

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

        contentPanel.add(videosPanel);
        contentPanel.add(VideoDetailsPanel.getPanel());
        contentPanel.add(iconPanel);
        contentPanel.add(buttonPanel);
        windowPanel.add(contentPanel);
        windowPanel.setBounds(0,0, 100, 100);
        Window.add(windowPanel, "\uF03D");
    }



    public static void toggle(){
        if (Main.programLoaded) {
            boolean doAnnounce = false;
            if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                doAnnounce = SettingsHandler.getSettings("isMod").asBoolean();
            }
            if (MediaShare.sharingEnabled) {
                MediaShare.sharingEnabled = false;
                Main.sendMessage(Utilities.format("🟥 | $MEDIA_SHARE_OFF_TOGGLE_MESSAGE$"), doAnnounce);
                Main.sendYTMessage(Utilities.format("🟥 | $MEDIA_SHARE_OFF_TOGGLE_MESSAGE$"));

            } else {
                MediaShare.sharingEnabled = true;
                Main.sendMessage(Utilities.format("🟩 | $MEDIA_SHARE_ON_TOGGLE_MESSAGE$"), doAnnounce);
                Main.sendYTMessage(Utilities.format("🟥 | $MEDIA_SHARE_ON_TOGGLE_MESSAGE$"));
            }
        }
        if (MediaShare.sharingEnabled) {
            toggleMedia.setText("\uF186");
        } else {
            toggleMedia.setText("\uF184");
        }
    }

    public static void refreshUI() {
        AlphalaneousLabel.setForeground(Defaults.FOREGROUND_A);
        attributionsFrame.setBackground(Defaults.COLOR3);
        selectUI.setBackground(Defaults.COLOR4);
        selectUI.setHover(Defaults.COLOR5);
        selectUI.setSelect(Defaults.COLOR4);
        buttonUI.setBackground(Defaults.COLOR6);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);
        sideButtons.setBackground(Defaults.COLOR6);
        buttonPanel.setBackground(Defaults.COLOR3);
        infoPanel.setBackground(Defaults.COLOR3);
        messageTextArea.refresh_();
        videosPanel.refreshUI();

        for (Component component : buttonPanel.getComponents()) {
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
    }

    public static void resize(int width, int height){
        windowPanel.setBounds(0,0, width, height);
        contentPanel.setBounds(0,0,width,height);
        buttonPanel.setBounds(width-130, 0, buttonPanel.getWidth(), height);
        iconPanel.setBounds(width - 125, height - 95, 80, 50);
        videosPanel.resizePanel(width - 500, height-47);
        VideoDetailsPanel.setPositionAndHeight(width-500, height);

    }

    public static RoundedJButton createButton(String icon, String tooltip) {
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

    public static void addVideo(VideoButton button){
        videosPanel.addButton(button);
    }
    public static void addVideo(VideoButton button, int pos){
        videosPanel.addButton(button, pos);
    }

    public static void clearVideos(){
        videosPanel.clearVideos();
        videosPanel.updateUI();
    }
    public static int getQueueSize(){
        return videosPanel.getQueueSize();
    }

    public static void movePosition(int pos, int newPos){
        videosPanel.movePosition(pos, newPos);
    }

    public static VideoButton getVideo(int pos){
        return videosPanel.getButton(pos);
    }

    public static int getVideoPosition(YouTubeVideo video){
        int pos = 0;
        for(Component component : videosPanel.getButtonPanel().getComponents()){
            if (((VideoButton) component).getVideoData().equals(video)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public static boolean exists(YouTubeVideo video){
        for(Component component : videosPanel.getButtonPanel().getComponents()){
            if (((VideoButton) component).getVideoData().getVideoID().equals(video.getVideoID())) {
                return true;
            }
        }
        return false;
    }

    public static int getVideoPosition(VideoButton button){
        int pos = 0;
        for(Component component : videosPanel.getButtonPanel().getComponents()){
            if(component.equals(button)){
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public static void updateVideosPanel(){
        videosPanel.updateUI();
    }

    public static void removeVideo(int pos){
        videosPanel.removeVideo(pos);
        videosPanel.updateUI();
    }

    public static void setVideoSelect(int pos){
        videosPanel.setSelect(pos);
    }

    public static VideosPanel getVideosPanel(){
        return videosPanel;
    }

}
