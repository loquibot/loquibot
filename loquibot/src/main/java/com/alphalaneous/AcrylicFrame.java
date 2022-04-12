package com.alphalaneous;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class AcrylicFrame extends JFrame {


    private final ArrayList<Background> backgrounds = new ArrayList<>();
    private final ArrayList<BackgroundColor> backgroundColors = new ArrayList<>();

    private final JLayeredPane layeredContentPanel = new JLayeredPane() {
        @Override
        public boolean isOptimizedDrawingEnabled() { return false;
        }
    };
    private final JPanel backgroundPane = new JPanel();
    private final JPanel contentPane = new JPanel();

    public AcrylicFrame(){
        this(null);
    }

    public AcrylicFrame(String name) {
        setTitle(name);
        String wallpaperLocationString = RegQuery.getWallpaperLocation();
        BufferedImage input = null;
        assert wallpaperLocationString != null;
        boolean validWallpaper = true;

        try {
            File wallpaperLocation = new File(wallpaperLocationString);
            input = ImageIO.read(wallpaperLocation);
        } catch (Exception e) {
            validWallpaper = false;
            //e.printStackTrace();
        }

        setLayout(null);
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = g.getScreenDevices();
        for(GraphicsDevice device : devices){
            if(validWallpaper) {
                BufferedImage newImage = copyImage(Objects.requireNonNull(input));
                Scalr.resize(Objects.requireNonNull(newImage), device.getDisplayMode().getWidth());
                JLabel label = new JLabel(blur((Objects.requireNonNull(newImage))));
                label.setBounds(device.getDefaultConfiguration().getBounds().x, device.getDefaultConfiguration().getBounds().y, device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
                Background background = new Background(label, device.getDefaultConfiguration().getBounds().x, device.getDefaultConfiguration().getBounds().y, device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
                backgrounds.add(background);
                backgroundPane.add(label);
            }
            else{
                JPanel panel = new JPanel();
                panel.setBounds(device.getDefaultConfiguration().getBounds().x, device.getDefaultConfiguration().getBounds().y, device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
                BackgroundColor backgroundColor = new BackgroundColor(panel, device.getDefaultConfiguration().getBounds().x, device.getDefaultConfiguration().getBounds().y, device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
                panel.setBackground(Objects.requireNonNull(RegQuery.getWallpaperColor()).darker().darker().darker());
                backgroundColors.add(backgroundColor);
                backgroundPane.add(panel);
            }
        }
        boolean finalValidWallpaper = validWallpaper;
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                if(finalValidWallpaper) {
                    for (Background background : backgrounds) {
                        background.setNewLocation(getLocation());
                    }
                }
                else {
                    for (BackgroundColor background : backgroundColors) {
                        background.setNewLocation(getLocation());
                    }
                }
            }

            @Override
            public void componentResized(ComponentEvent e) {
                layeredContentPanel.setBounds(0,0, getWidth(), getHeight());
                contentPane.setBounds(0,0, getWidth(), getHeight());
                contentPane.updateUI();
                backgroundPane.setBounds(0,0, getWidth(), getHeight());
                super.componentResized(e);
            }
        });

        backgroundPane.setLayout(null);
        contentPane.setOpaque(false);
        contentPane.setBackground(new Color(0,0,0,0));
        layeredContentPanel.add(backgroundPane, 0, -1);
        layeredContentPanel.add(contentPane, 1, -1);

        super.add(layeredContentPanel);
    }


    @Override
    public Component add(Component comp) {
        return contentPane.add(comp);
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        contentPane.add(comp, constraints, index);
    }

    public void setContentLayout(LayoutManager manager) {
        contentPane.setLayout(manager);
    }

    public Icon blur(BufferedImage input) {

        //input = Scalr.resize(input, input.getWidth() / 10);

        BufferedImage newImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);

        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(30);
        boxBlurFilter.filter(input, newImage);

        //setSaturation(1f, newImage);


        Graphics2D gbi = newImage.createGraphics();

        gbi.drawImage(newImage, 0, 0, null);
        gbi.setColor(new Color(30, 30, 30));
        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
        gbi.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        newImage.getGraphics().drawImage(newImage, 0, 0, null);

        //newImage = Scalr.resize(newImage, Scalr.Method.BALANCED, newImage.getWidth() * 10);

        return new ImageIcon(newImage);
    }

    private void setSaturation(float newSaturation, BufferedImage image){

        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){

                int RGB = image.getRGB(i, j);

                Color pixelColor = Color.decode(String.valueOf(RGB));

                int red = pixelColor.getRed();
                int green = pixelColor.getGreen();
                int blue = pixelColor.getBlue();

                float[] hsb = Color.RGBtoHSB(red, green, blue, null);

                float hue = hsb[0];
                float saturation = hsb[1];
                float brightness = hsb[2];

                saturation = newSaturation;

                int rgb = Color.HSBtoRGB(hue, saturation, brightness);

                red = (rgb>>16)&0xFF;
                green = (rgb>>8)&0xFF;
                blue = rgb&0xFF;

                image.setRGB(i, j, rgb);
            }
        }
    }
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    private static class Background {

        private final JLabel label;
        private final int origX;
        private final int origY;
        private final int origW;
        private final int origH;

        Background(JLabel label, int origX, int origY, int origW, int origH){
            this.label = label;
            this.origX = origX;
            this.origY = origY;
            this.origW = origW;
            this.origH = origH;
        }

        public void setNewLocation(Point p){
            label.setBounds(-p.x + origX-60, -p.y-60 + origY, origW+100, origH+100);

        }
    }
    private static class BackgroundColor {

        private final JPanel panel;
        private final int origX;
        private final int origY;
        private final int origW;
        private final int origH;

        BackgroundColor(JPanel panel, int origX, int origY, int origW, int origH){
            this.panel = panel;
            this.origX = origX;
            this.origY = origY;
            this.origW = origW;
            this.origH = origH;
        }

        public void setNewLocation(Point p){
            panel.setBounds(-p.x + origX-60, -p.y-60 + origY, origW+100, origH+100);
        }
    }
}
