package com.alphalaneous;

import com.alphalaneous.Components.CurvedButton;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.MultiLineLabel;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.Panels.LevelButton;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.Window;
import org.imgscalr.Scalr;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoDetails extends JPanel {

    private YouTubeVideo videoUsed = null;
    private BufferedImage image = null;
    private final MultiLineLabel titleLabel;
    private JLabel usernameLabel;
    //private final JLabel requesterLabel;
    private final JLabel YTTitleLabel;
    private final JLabel YTUploader;
    private final JLabel YTViews;
    private final JButtonUI clearUI = new JButtonUI();
    private final JTextPane description = new JTextPane();
    private final CurvedButton youTubeButton = new CurvedButton("");
    private final GridBagConstraints gbc = new GridBagConstraints();
    private int descHeight = 30;


    public VideoDetails(YouTubeVideo data){
        setLayout(null);
        setBounds(0,0,520, Window.getWindow().getHeight());
        titleLabel = new MultiLineLabel(data.getTitle(), 340, Defaults.MAIN_FONT.deriveFont(20f));

        titleLabel.setBounds(20, 220, (int) titleLabel.getPreferredSize().width + 20, titleLabel.getPreferredSize().height);



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
        
        try {
            descHeight = description.getPreferredSize().height;
        }
        catch (Exception ignored){}
        
        description.setBounds(20, 75, 460, descHeight);

        usernameLabel = new JLabel(data.getUsername());


        //if(data.isYouTube()) requesterLabel = new JLabel("Sent by " + data.getDisplayName());
        //else requesterLabel = new JLabel("Sent by " + data.getRequester());

        usernameLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        usernameLabel.setBounds(20, titleLabel.getPreferredSize().height + 220, 170, 30);

        //requesterLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        //requesterLabel.setBounds(80, descHeight + 105, 170, 30);


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

        youTubeButton.setBounds(20, descHeight + 150, 470, 100);

        ThumbnailPanel imagePanel = new ThumbnailPanel();
        imagePanel.setOpaque(false);
        imagePanel.setBounds(10,10,340, 191);

        add(imagePanel);

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

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(8, 9, 0, 2);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        image = imageDownloader(data.getThumbnailURL()); //todo save thumbnail from button rather than redownloading
        imagePanel.setImage(Scalr.resize(image, 360));
        updateUI();

        add(titleLabel);
        add(description);
        add(usernameLabel);
        refreshUI();

        youTubeButton.setVisible(false);
    }

    int tries = 0;

    public void resizeAll(int height){
        //if(youTubeButton.isVisible()) commentScrollPane.setBounds(0,descHeight + 310,510, height-350 - descHeight);
        //else commentScrollPane.setBounds(0,descHeight + 200,510, height-240 - descHeight);
    }

    private JButton createButton(String icon, String tooltip) {
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
        blurredImage = null;
        generateScaledInstance();
        titleLabel.setForeground(Defaults.FOREGROUND_A);
        usernameLabel.setForeground(Defaults.FOREGROUND_B);
        YTTitleLabel.setForeground(Defaults.FOREGROUND_A);
        YTUploader.setForeground(Defaults.FOREGROUND_C);
        YTViews.setForeground(Defaults.FOREGROUND_C);
        refreshInfo(description);
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
        float scale;
        scale = (float) iTargetSize / (float) iMasterSize;
        return scale;
    }

    public BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {
        BufferedImage imgBuffer;
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

        BufferedImage ret = img;

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

        BufferedImage ret = img;
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

        } while (w != targetWidth || h != targetHeight);

        return ret;

    }

}
