package com.alphalaneous;

import com.alphalaneous.Components.CurvedButton;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Panels.Comment;
import com.alphalaneous.Panels.LevelButton;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.Window;
import jdash.client.exception.GDClientException;
import jdash.common.IconType;
import jdash.common.entity.GDComment;
import org.imgscalr.Scalr;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alphalaneous.Windows.Window.ListButton.invertImage;

public class LevelDetails extends JPanel {

    private YouTubeVideo videoUsed = null;
    private BufferedImage image = null;
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JLabel usernameLabel;
    private JLabel requesterLabel;
    private JLabel songTitleLabel;
    private JLabel songArtistLabel;
    private JLabel songIDLabel;
    private JLabel YTTitleLabel;
    private JLabel YTUploader;
    private JLabel YTViews;
    private JLabel commentsLabel = new JLabel("Comments");
    private JButtonUI clearUI = new JButtonUI();
    private JTextPane description = new JTextPane();
    private JLabel songIcon = new JLabel();
    private CurvedButton youTubeButton = new CurvedButton("");
    private JPanel commentsPanel = new JPanel();
    private SmoothScrollPane commentScrollPane = new SmoothScrollPane(commentsPanel);
    private int page = 0;
    private GridBagConstraints gbc = new GridBagConstraints();
    private JButton prev = createButton("\uF305", 0, "$PREV_PAGE$");
    private JButton next = createButton("\uF304", 35, "$NEXT_PAGE$");
    private JButton topComments = createButton("\uF138", 90, "$TOP_COMMENTS$");
    private JButton newest = createButton("\uF22B", 125, "$LATEST_COMMENTS$");
    private LoadingPane loadingPane = new LoadingPane();

    public LevelDetails(LevelData data){
        setLayout(null);
        setBounds(0,0,520, Window.getWindow().getHeight());
        titleLabel = new JLabel(data.getGDLevel().name());

        String starCount = "";
        if(data.getGDLevel().stars() > 0) starCount = data.getGDLevel().stars() + " stars • ";

        infoLabel = new JLabel(starCount + data.getGDLevel().length() + " • " + data.getGDLevel().downloads() + " downloads • " + data.getGDLevel().likes() + " likes • (" + data.getGDLevel().id() + ")");


        titleLabel.setFont(Defaults.MAIN_FONT.deriveFont(24f));
        titleLabel.setBounds(20, 20, (int) titleLabel.getPreferredSize().getWidth() + 20, 30);

        infoLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        infoLabel.setBounds(20, 50, (int) infoLabel.getPreferredSize().getWidth() + 20, 30);

        description.setText("N/A");
        StyledDocument doc = description.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        description.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        description.setOpaque(false);
        description.setEditable(false);
        description.setForeground(Defaults.FOREGROUND_A);
        description.setBackground(new Color(0, 0, 0, 0));
        description.setSelectionColor(Defaults.ACCENT);
        refreshInfo(description);

        description.setSize(460, Short.MAX_VALUE);
        description.setBounds(20, 75, 460, description.getPreferredSize().height);

        JLabel playerIcon = new JLabel();

        usernameLabel = new JLabel("By Unknown");

        if(data.getGDLevel().creatorName().isPresent()){
            usernameLabel = new JLabel("By " + data.getGDLevel().creatorName().get());
        }

        requesterLabel = new JLabel("Sent by " + data.getRequester());


        songTitleLabel = new JLabel("N/A");
        songArtistLabel = new JLabel("By N/A");
        songIDLabel = new JLabel("(N/A)");

        if(data.getGDLevel().song().isPresent()){
            songTitleLabel = new JLabel(data.getGDLevel().song().get().title());
            songArtistLabel = new JLabel("By " + data.getGDLevel().song().get().artist());
            songIDLabel = new JLabel("(" + data.getGDLevel().song().get().id() + ")");
        }


        usernameLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        usernameLabel.setBounds(80, description.getPreferredSize().height + 85, 170, 30);

        requesterLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        requesterLabel.setBounds(80, description.getPreferredSize().height + 105, 170, 30);

        if(data.getPlayerIcon() == null) playerIcon.setIcon(GDAPI.getIcon(IconType.CUBE, 1, 1, 3, false, 100));
        else playerIcon.setIcon(data.getPlayerIcon());

        playerIcon.setBounds(-5, description.getPreferredSize().height + 60, 170, 100);

        songTitleLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        songTitleLabel.setBounds(315, description.getPreferredSize().height + 80, 170, 30);

        songArtistLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        songArtistLabel.setBounds(315, description.getPreferredSize().height + 95, 170, 30);

        songIDLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        songIDLabel.setBounds(315, description.getPreferredSize().height + 110, 170, 30);

        songIcon.setIcon(Assets.music);

        if(!Defaults.isLight) songIcon.setIcon(Assets.music);
        else songIcon.setIcon(invertImage(Assets.music));


        songIcon.setBounds(260, description.getPreferredSize().height + 60, 100, 100);


        String[] difficulties = {"NA", "easy", "normal", "hard", "harder", "insane"};
        String[] demonDifficulties = {"easy", "medium", "hard", "insane", "extreme"};

        JLabel reqDifficulty = new JLabel();
        reqDifficulty.setBounds(450, 10, 50,50);

        if(data.getGDLevel().isAuto()){
            if (data.getGDLevel().isEpic()) {
                reqDifficulty.setIcon(Assets.difficultyIconsEpicLarge.get("auto"));
            } else if (data.getGDLevel().featuredScore() > 0) {
                reqDifficulty.setIcon(Assets.difficultyIconsFeatureLarge.get("auto"));
            } else if (data.getGDLevel().stars() != 0) {
                reqDifficulty.setIcon(Assets.difficultyIconsNormalLarge.get("auto"));
            } else {
                reqDifficulty.setIcon(new ImageIcon(Assets.difficultyIconsNormalLarge.get("auto").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        }
        else if(data.getGDLevel().isDemon()){
            for (String difficultyA : demonDifficulties) {
                if (data.getGDLevel().demonDifficulty().name().equalsIgnoreCase(difficultyA)) {
                    difficultyA = difficultyA + " demon";
                    if (data.getGDLevel().isEpic()) {
                        reqDifficulty.setIcon(Assets.difficultyIconsEpicLarge.get(difficultyA));
                    } else if (data.getGDLevel().featuredScore() > 0) {
                        reqDifficulty.setIcon(Assets.difficultyIconsFeatureLarge.get(difficultyA));
                    } else if (data.getGDLevel().stars() != 0) {
                        reqDifficulty.setIcon(Assets.difficultyIconsNormalLarge.get(difficultyA));
                    } else {
                        reqDifficulty.setIcon(new ImageIcon(Assets.difficultyIconsNormalLarge.get(difficultyA).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                    }
                }
            }
        }
        else {
            for (String difficultyA : difficulties) {
                if (data.getGDLevel().difficulty().toString().equalsIgnoreCase(difficultyA)) {
                    if (data.getGDLevel().isEpic()) {
                        reqDifficulty.setIcon(Assets.difficultyIconsEpicLarge.get(difficultyA));
                    } else if (data.getGDLevel().featuredScore() > 0) {
                        reqDifficulty.setIcon(Assets.difficultyIconsFeatureLarge.get(difficultyA));
                    } else if (data.getGDLevel().stars() != 0) {
                        reqDifficulty.setIcon(Assets.difficultyIconsNormalLarge.get(difficultyA));
                    } else {
                        reqDifficulty.setIcon(new ImageIcon(Assets.difficultyIconsNormalLarge.get(difficultyA).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                    }

                }
            }
        }

        int coins = data.getGDLevel().coinCount();
        int coinPos = 0;

        for (int i = 0; i < coins; i++) {
            JLabel coin;
            if (data.getGDLevel().hasCoinsVerified()) {
                coin = new JLabel(Assets.verifiedCoin);
            } else {
                coin = new JLabel(Assets.unverifiedCoin);
            }
            coin.setBounds((int) titleLabel.getPreferredSize().getWidth() + titleLabel.getX() + 10 + coinPos, 27, 15, 15);
            coinPos = coinPos + 10;
            add(coin);
        }



        clearUI.setBackground(new Color(255,255,255, 30));
        clearUI.setHover(new Color(255,255,255, 20));
        clearUI.setSelect(new Color(255,255,255, 10));

        youTubeButton.setUI(clearUI);
        youTubeButton.setBackground(new Color(255,255,255, 30));
        youTubeButton.setForeground(Defaults.FOREGROUND_A);
        youTubeButton.setBorder(BorderFactory.createEmptyBorder());
        youTubeButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        youTubeButton.setLayout(null);
        youTubeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Utilities.openURL(new URI("https://www.youtube.com/watch?v=" + videoUsed.getVideoID()));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        youTubeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        youTubeButton.refresh();

        youTubeButton.setBounds(20, description.getPreferredSize().height + 150, 470, 100);

        ThumbnailPanel imagePanel = new ThumbnailPanel();
        imagePanel.setOpaque(false);
        imagePanel.setBounds(5,5,154, 90);
        youTubeButton.add(imagePanel);

        YTTitleLabel = new JLabel();
        YTTitleLabel.setBounds(170, 10, 270, 30);
        YTTitleLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        youTubeButton.add(YTTitleLabel);

        YTUploader = new JLabel();
        YTUploader.setBounds(170, 30, 270, 30);
        YTUploader.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        youTubeButton.add(YTUploader);

        YTViews = new JLabel();
        YTViews.setBounds(170, 50, 270, 30);
        YTViews.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        youTubeButton.add(YTViews);

        commentScrollPane.setBounds(0,description.getPreferredSize().height + 200,510, Window.getWindow().getHeight()-240 - description.getPreferredSize().height);
        commentScrollPane.setBackground(new Color(0,0,0,0));
        commentScrollPane.setOpaque(false);
        commentScrollPane.getViewport().setOpaque(false);
        commentsPanel.setBackground(new Color(0,0,0,0));
        commentsPanel.setOpaque(false);
        commentsPanel.setLayout(new FlowLayout());
        commentsPanel.setLayout(new GridBagLayout());

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(8, 9, 0, 2);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        commentsPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        loadingPane.setBounds(230,50,62,62);
        commentScrollPane.add(loadingPane);

        new Thread(() -> refreshComments(0, false, data.getGDLevel().id())).start();

        commentsLabel.setFont(Defaults.MAIN_FONT.deriveFont(18f));
        commentsLabel.setBounds(18, commentScrollPane.getY()-45, 200, 40);


        prev.addActionListener(e -> {
            if(page > 0){
                page--;
                refreshComments(page, false, data.getGDLevel().id());
            }
        });

        prev.setBounds(435, commentScrollPane.getY()-40, 30, 30);


        next.addActionListener(e -> {
            page++;
            boolean success = refreshComments(page, false, data.getGDLevel().id());
            if(!success) {
                page--;
                refreshComments(page, false, data.getGDLevel().id());
            }
        });

        next.setBounds(470, commentScrollPane.getY()-40, 30, 30);


        topComments.addActionListener(e -> {
            page = 0;
            refreshComments(page, true, data.getGDLevel().id());
        });

        topComments.setBounds(135, commentScrollPane.getY()-40, 30, 30);


        newest.addActionListener(e -> {
            page = 0;
            refreshComments(page, false, data.getGDLevel().id());
        });

        newest.setBounds(170, commentScrollPane.getY()-40, 30, 30);


        add(prev);
        add(next);
        add(topComments);
        add(newest);


        add(titleLabel);
        add(infoLabel);
        add(description);
        add(playerIcon);
        add(usernameLabel);
        add(requesterLabel);
        add(songIcon);
        add(songTitleLabel);
        add(songArtistLabel);
        add(songIDLabel);
        add(reqDifficulty);
        add(commentScrollPane);
        add(commentsLabel);
        refreshUI();

        youTubeButton.setVisible(false);

        new SwingWorker<YouTubeVideo, Object>(){
            @Override
            public YouTubeVideo doInBackground() {
                ArrayList<YouTubeVideo> youTubeVideos;
                try {
                    youTubeVideos = YTScrape.searchYouTube(String.valueOf(data.getGDLevel().id()));
                    while(true) {
                        int pos = maxValue(youTubeVideos);
                        if (youTubeVideos.get(pos).getTitle().contains(data.getGDLevel().name())) {
                            return youTubeVideos.get(pos);
                        }
                        else youTubeVideos.remove(pos);
                        if(youTubeVideos.size() == 0) return null;
                    }

                } catch (IOException f) {
                    f.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    if(get() != null) {
                        image = imageDownloader(get().getThumbnailURL());
                        imagePanel.setImage(Scalr.resize(image, 160));
                        YTTitleLabel.setText(get().getTitle());
                        YTUploader.setText(get().getUsername());
                        YTViews.setText(get().getViewCount() + " views");
                        add(youTubeButton);
                        commentScrollPane.setBounds(0,description.getPreferredSize().height + 310,510, Window.getWindow().getHeight()-350 - description.getPreferredSize().height);
                        commentsLabel.setBounds(18, commentScrollPane.getY()-45, 200, 40);
                        videoUsed = get();
                        youTubeButton.setVisible(true);
                        data.setYoutubeURL("https://www.youtube.com/watch?v=" + videoUsed.getVideoID());

                        JSONObject object = new JSONObject();
                        object.put("type", "youtube");
                        Main.sendMessageToStreamDeck(object.toString());
                        next.setBounds(470, commentScrollPane.getY()-40, 30, 30);
                        prev.setBounds(435, commentScrollPane.getY()-40, 30, 30);
                        topComments.setBounds(135, commentScrollPane.getY()-40, 30, 30);
                        newest.setBounds(170, commentScrollPane.getY()-40, 30, 30);


                        repaint();
                        updateUI();
                    }
                } catch (Exception ignore) {
                }
            }
        }.execute();
    }

    int tries = 0;
    public boolean refreshComments(int page, boolean top, long id) throws GDClientException {
        tries++;
        commentsPanel.setVisible(false);
        commentsPanel.removeAll();
        List<GDComment> comments;
        try {
            comments = GDAPI.getGDComments(id, top, page);
        }
        catch (Exception e){
            if(tries <= 15){
                return refreshComments(page, top, id);
            }
            else tries = 0;
            e.printStackTrace();
            return false;
        }
        for (GDComment com : comments) {
            Comment comment = new Comment(com, 480);
            commentsPanel.add(comment, gbc);
        }
        commentScrollPane.remove(loadingPane);
        commentsPanel.setVisible(true);
        tries = 0;
        return true;
    }

    public void resizeAll(int height){
        if(youTubeButton.isVisible()) commentScrollPane.setBounds(0,description.getPreferredSize().height + 310,510, height-350 - description.getPreferredSize().height);
        else commentScrollPane.setBounds(0,description.getPreferredSize().height + 200,510, height-240 - description.getPreferredSize().height);
    }

    private JButton createButton(String icon, int x, String tooltip) {
        JButton button = new RoundedJButton(icon, tooltip);
        button.setFont(Defaults.SYMBOLS.deriveFont(16f));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setForeground(Defaults.FOREGROUND_A);
        button.setBackground(new Color(255,255,255, 30));
        button.setUI(clearUI);
        return button;
    }

    public void refreshUI(){
        setBackground(Defaults.COLOR3);
        titleLabel.setForeground(Defaults.FOREGROUND_A);
        infoLabel.setForeground(Defaults.FOREGROUND_C);
        usernameLabel.setForeground(Defaults.FOREGROUND_A);
        requesterLabel.setForeground(Defaults.FOREGROUND_C);
        songTitleLabel.setForeground(Defaults.FOREGROUND_A);
        songArtistLabel.setForeground(Defaults.FOREGROUND_C);
        songIDLabel.setForeground(Defaults.FOREGROUND_C);
        YTTitleLabel.setForeground(Defaults.FOREGROUND_A);
        YTUploader.setForeground(Defaults.FOREGROUND_C);
        YTViews.setForeground(Defaults.FOREGROUND_C);
        commentsLabel.setForeground(Defaults.FOREGROUND_A);
        prev.setForeground(Defaults.FOREGROUND_A);
        next.setForeground(Defaults.FOREGROUND_A);
        topComments.setForeground(Defaults.FOREGROUND_A);
        newest.setForeground(Defaults.FOREGROUND_A);
        if(!Defaults.isLight) songIcon.setIcon(Assets.music);
        else songIcon.setIcon(invertImage(Assets.music));
        refreshInfo(description);
        for(Component component : commentsPanel.getComponents()){
            if(component instanceof Comment){
                ((Comment) component).refresh();
            }
        }
        repaint();
    }


    public int maxValue(ArrayList<YouTubeVideo> videos) {
        long max = 0;
        int pos = 0;

        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getViewCount() > max) {
                max = videos.get(i).getViewCount();
                pos = i;
            }
        }
        return pos;
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        tp.setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset;
        if (c != null) aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        else aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Defaults.FOREGROUND_A);

        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_CENTER);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
        tp.setEditable(false);

    }
    public void refreshInfo(JTextPane description) {
        description.setVisible(false);
        appendToPane(description, "", Defaults.FOREGROUND_A);
        if (RequestsTab.getQueueSize() == 0) {
            description.setText("NA");
        } else {
            description.setText("");

            ArrayList<String> colored = new ArrayList<>();
            boolean hasColored = false;


            String desc = RequestsTab.getRequest(LevelButton.selectedID).getLevelData().getGDLevel().description();
            Matcher matcher = Pattern.compile("<(c[a-zA-Z])>(.+?)</c>").matcher(desc);
            while (matcher.find()) {
                hasColored = true;
                colored.add(matcher.group(1).substring(1) + ">" + matcher.group(2));
            }
            if (hasColored) {
                String[] descSplit = desc.split("</c>");
                char colorVal = 0;
                String colorSectionA = "";
                boolean colorGotten = false;
                for (String descSection : descSplit) {
                    String[] descSectionSplit = descSection.split("<c");
                    all:
                    for (String descSectionSplitSection : descSectionSplit) {
                        if (descSectionSplitSection.length() > 0 && descSectionSplitSection.substring(1).startsWith(">")) {
                            for (String colorSection : colored) {
                                if (descSectionSplitSection.equals(colorSection)) {
                                    colorVal = colorSection.charAt(0);
                                    colorGotten = true;
                                    colorSectionA = colorSection;
                                    break all;
                                }
                            }
                        } else {
                            appendToPane(description, descSection.split("<c")[0], Defaults.FOREGROUND_A);
                        }
                    }
                    if (colorGotten) {
                        Color color = Defaults.FOREGROUND_A;
                        switch (colorVal) {
                            case 'o':
                                color = new Color(255, 165, 75);
                                break;
                            case 'y':
                                color = new Color(255, 255, 72);
                                break;
                            case 'g':
                                color = new Color(64, 227, 72);
                                break;
                            case 'j':
                                color = new Color(50, 200, 255);
                                break;
                            case 'b':
                                color = new Color(74, 82, 225);
                                break;
                            case 'p':
                                color = new Color(255, 0, 255);
                                break;
                            case 'l':
                                color = new Color(96, 171, 239);
                                break;
                            case 'r':
                                color = new Color(255, 74, 74);
                                break;
                            case 'x':
                                color = new Color(255, 0, 0);
                                break;
                            default:
                                break;
                        }
                        appendToPane(description, colorSectionA.substring(2), color);
                    }

                }
            } else {
                description.setText(desc);
            }
        }
        if(description.getText().trim().equalsIgnoreCase("")){
            description.setText("(No description provided)");
        }
        description.setVisible(true);
    }
    public static BufferedImage imageDownloader(String urlString){
        BufferedImage image = null;
        try {
            String cleanUrl = urlString.replace(" ","%20");
            URL url = new URL(cleanUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private BufferedImage blurredImage;

    public BufferedImage blur(BufferedImage input) {

        if(blurredImage != null) return blurredImage;


        BufferedImage newImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);

        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(500);
        boxBlurFilter.filter(input, newImage);

        Graphics2D gbi = newImage.createGraphics();

        gbi.drawImage(newImage, 0, 0, null);
        gbi.setColor(Defaults.OVERLAY);
        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
        gbi.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        newImage.getGraphics().drawImage(newImage, 0, 0, null);
        blurredImage = newImage;
        return newImage;
    }

    private BufferedImage scaled;

    @Override
    public Dimension getPreferredSize() {
        return image == null ? new Dimension(200, 200) : new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    public void invalidate() {
        super.invalidate();
        generateScaledInstance();
    }

    protected void generateScaledInstance() {
        if (image != null) {
            scaled = getScaledInstanceToFill(blur(image), getSize());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (scaled != null) {
            int x = (getWidth() - scaled.getWidth()) / 2;
            int y = (getHeight() - scaled.getHeight()) / 2;
            g2d.drawImage(scaled, x, y, this);
        }
        else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getSize().width, getSize().height);
        }
        g2d.dispose();
    }

    public BufferedImage getScaledInstanceToFill(BufferedImage img, Dimension size) {
        float scaleFactor = getScaleFactorToFill(img, size);
        return getScaledInstance(img, scaleFactor);
    }

    public float getScaleFactorToFill(BufferedImage img, Dimension size) {
        float scale = 1f;
        if (img != null) {
            int imageWidth = img.getWidth();
            int imageHeight = img.getHeight();
            scale = getScaleFactorToFill(new Dimension(imageWidth, imageHeight), size);
        }
        return scale;
    }

    public float getScaleFactorToFill(Dimension original, Dimension toFit) {
        float scale = 1f;
        if (original != null && toFit != null) {
            float dScaleWidth = getScaleFactor(original.width, toFit.width);
            float dScaleHeight = getScaleFactor(original.height, toFit.height);
            scale = Math.max(dScaleHeight, dScaleWidth);
        }
        return scale;
    }

    public float getScaleFactor(int iMasterSize, int iTargetSize) {
        float scale = 1;
        if (iMasterSize > iTargetSize) {
            scale = (float) iTargetSize / (float) iMasterSize;
        } else {
            scale = (float) iTargetSize / (float) iMasterSize;
        }
        return scale;
    }

    public BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {
        BufferedImage imgBuffer = null;
        imgBuffer = getScaledInstance(img, dScaleFactor, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
        return imgBuffer;
    }

    protected BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor, Object hint, boolean higherQuality) {

        BufferedImage scaled = img;
        if (dScaleFactor != 1.0) {
            if (dScaleFactor > 1.0) {
                scaled = getScaledUpInstance(img, dScaleFactor, hint, higherQuality);
            } else if (dScaleFactor > 0.0) {
                scaled = getScaledDownInstance(img, dScaleFactor, hint, higherQuality);
            }
        }

        return scaled;

    }

    protected BufferedImage getScaledDownInstance(BufferedImage img, double dScaleFactor, Object hint, boolean higherQuality) {

        int targetWidth = (int) Math.round(img.getWidth() * dScaleFactor);
        int targetHeight = (int) Math.round(img.getHeight() * dScaleFactor);

        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        BufferedImage ret = (BufferedImage) img;

        if (targetHeight > 0 || targetWidth > 0) {
            int w, h;
            if (higherQuality) {
                w = img.getWidth();
                h = img.getHeight();
            } else {
                w = targetWidth;
                h = targetHeight;
            }

            do {
                if (higherQuality && w > targetWidth) {
                    w /= 2;
                    if (w < targetWidth) {
                        w = targetWidth;
                    }
                }

                if (higherQuality && h > targetHeight) {
                    h /= 2;
                    if (h < targetHeight) {
                        h = targetHeight;
                    }
                }

                BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
                g2.drawImage(ret, 0, 0, w, h, null);
                g2.dispose();

                ret = tmp;
            } while (w != targetWidth || h != targetHeight);
        } else {
            ret = new BufferedImage(1, 1, type);
        }
        return ret;
    }

    protected BufferedImage getScaledUpInstance(BufferedImage img,
                                                double dScaleFactor,
                                                Object hint,
                                                boolean higherQuality) {

        int targetWidth = (int) Math.round(img.getWidth() * dScaleFactor);
        int targetHeight = (int) Math.round(img.getHeight() * dScaleFactor);

        int type = BufferedImage.TYPE_INT_ARGB;

        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {

            w = img.getWidth();
            h = img.getHeight();

        } else {

            w = targetWidth;
            h = targetHeight;

        }

        do {

            if (higherQuality && w < targetWidth) {

                w *= 2;
                if (w > targetWidth) {

                    w = targetWidth;

                }

            }

            if (higherQuality && h < targetHeight) {

                h *= 2;
                if (h > targetHeight) {

                    h = targetHeight;

                }

            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
            tmp = null;

        } while (w != targetWidth || h != targetHeight);

        return ret;

    }

}
