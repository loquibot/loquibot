package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Images.BoxBlurFilter;
import com.alphalaneous.Interactive.MediaShare.MediaShare;
import com.alphalaneous.Services.YouTube.YouTubeVideo;
import com.alphalaneous.Windows.Window;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoDetails extends JPanel {

    private final BufferedImage image;
    private final MultiLineLabel titleLabel;
    private final JLabel usernameLabel;
    private final JLabel requesterLabel;
    private final JButtonUI clearUI = new JButtonUI();
    private final CurvedButton playButton = createButton("\uF184", "play");

    public VideoDetails(YouTubeVideo data){

        ThumbnailPanel imagePanel = new ThumbnailPanel();

        image = (BufferedImage) data.getImage().getImage();
        imagePanel.setImage(Scalr.resize(image, 360));

        setOpaque(false);
        setLayout(null);
        setBounds(0,0,360, Window.getWindow().getHeight());

        titleLabel = new MultiLineLabel(data.getTitle(), 340, Defaults.MAIN_FONT.deriveFont(20f));
        titleLabel.setBounds(20, 220, 340, titleLabel.getPreferredSize().height);

        usernameLabel = new JLabel(data.getUsername());

        usernameLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        usernameLabel.setBounds(20, titleLabel.getPreferredSize().height + 220, 170, 30);

        requesterLabel = new JLabel("Sent by " + data.getRequester());
        requesterLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        requesterLabel.setBounds(20, titleLabel.getPreferredSize().height + 240, 170, 30);


        clearUI.setBackground(new Color(255,255,255, 30));
        clearUI.setHover(new Color(255,255,255, 20));
        clearUI.setSelect(new Color(255,255,255, 10));

        imagePanel.setOpaque(false);
        imagePanel.setBounds(10,10,340, 191);

        playButton.setBounds(160, titleLabel.getPreferredSize().height + 280, 40,40);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MediaShare.showMediaShare(data);
            }
        });

        add(titleLabel);
        add(imagePanel);
        add(playButton);
        add(usernameLabel);
        add(requesterLabel);

        refreshUI();
    }

    private CurvedButton createButton(String icon, String tooltip) {
        CurvedButton button = new CurvedButton(icon, tooltip);
        button.setFont(Defaults.SYMBOLS.deriveFont(18f));
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
        titleLabel.setForeground(Defaults.FOREGROUND_A);
        usernameLabel.setForeground(Defaults.FOREGROUND_B);
        requesterLabel.setForeground(Defaults.FOREGROUND_B);
        playButton.setForeground(Defaults.FOREGROUND_A);
        generateScaledInstance();
        repaint();
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
