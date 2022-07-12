package com.alphalaneous.Settings;

import com.alphalaneous.Images.Assets;
import com.alphalaneous.Swing.Components.SettingsComponent;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Swing.Components.SmoothScrollPane;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Theming.ThemedColor;
import com.alphalaneous.Swing.ThemedComponents.ThemedIconCheckbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Filters {

    public static ArrayList<String> excludedRequestedDifficulties = new ArrayList<>();
    public static ArrayList<String> excludedDifficulties = new ArrayList<>();
    public static ArrayList<String> excludedLengths = new ArrayList<>();

    private static final JPanel requestedDifficultyPanel = new JPanel();
    private static final JPanel difficultyPanel = new JPanel();
    private static final JPanel lengthPanel = new JPanel();

    private static final JPanel difficultyPanelWithScroll = new JPanel(new GridLayout(1,1,0,0)){
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g.setColor(getBackground());

            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRoundRect(0, 0, getSize().width, getSize().height, Defaults.globalArc, Defaults.globalArc);


            super.paintComponent(g);
        }
    };

    private static final JPanel requestedDifficultyPanelWithScroll = new JPanel(new GridLayout(1,1,0,0)){
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g.setColor(getBackground());

            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRoundRect(0, 0, getSize().width, getSize().height, Defaults.globalArc, Defaults.globalArc);


            super.paintComponent(g);
        }
    };


    private static final JPanel lengthPanelWithScroll = new JPanel(){
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g.setColor(getBackground());
            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRoundRect(0, 0, getSize().width, getSize().height, 10, 10);
            super.paintComponent(g);
        }
    };


    private static final SmoothScrollPane difficultyPanelScroll = new SmoothScrollPane(difficultyPanel);
    private static final SmoothScrollPane requestedDifficultyPanelScroll = new SmoothScrollPane(requestedDifficultyPanel);
    private static final SmoothScrollPane lengthPanelScroll = new SmoothScrollPane(lengthPanel);


    private static final ThemedIconCheckbox naIcon = createDifficultyButton("NA");
    private static final ThemedIconCheckbox autoIcon = createDifficultyButton("auto");
    private static final ThemedIconCheckbox easyIcon = createDifficultyButton("easy");
    private static final ThemedIconCheckbox normalIcon = createDifficultyButton("normal");
    private static final ThemedIconCheckbox hardIcon = createDifficultyButton("hard");
    private static final ThemedIconCheckbox harderIcon = createDifficultyButton("harder");
    private static final ThemedIconCheckbox insaneIcon = createDifficultyButton("insane");
    private static final ThemedIconCheckbox easyDemonIcon = createDifficultyButton("easy demon");
    private static final ThemedIconCheckbox mediumDemonIcon = createDifficultyButton("medium demon");
    private static final ThemedIconCheckbox hardDemonIcon = createDifficultyButton("hard demon");
    private static final ThemedIconCheckbox insaneDemonIcon = createDifficultyButton("insane demon");
    private static final ThemedIconCheckbox extremeDemonIcon = createDifficultyButton("extreme demon");

    private static final ThemedIconCheckbox naIconReq = createReqDifficultyButton("NA");
    private static final ThemedIconCheckbox autoIconReq = createReqDifficultyButton("auto");
    private static final ThemedIconCheckbox easyIconReq = createReqDifficultyButton("easy");
    private static final ThemedIconCheckbox normalIconReq = createReqDifficultyButton("normal");
    private static final ThemedIconCheckbox hardIconReq = createReqDifficultyButton("hard");
    private static final ThemedIconCheckbox harderIconReq = createReqDifficultyButton("harder");
    private static final ThemedIconCheckbox insaneIconReq = createReqDifficultyButton("insane");
    private static final ThemedIconCheckbox easyDemonIconReq = createReqDifficultyButton("easy demon");
    private static final ThemedIconCheckbox mediumDemonIconReq = createReqDifficultyButton("medium demon");
    private static final ThemedIconCheckbox hardDemonIconReq = createReqDifficultyButton("hard demon");
    private static final ThemedIconCheckbox insaneDemonIconReq = createReqDifficultyButton("insane demon");
    private static final ThemedIconCheckbox extremeDemonIconReq = createReqDifficultyButton("extreme demon");

    private static final ThemedIconCheckbox tinyIcon = createLengthButton("Tiny");
    private static final ThemedIconCheckbox shortIcon = createLengthButton("Short");
    private static final ThemedIconCheckbox mediumIcon = createLengthButton("Medium");
    private static final ThemedIconCheckbox longIcon = createLengthButton("Long");
    private static final ThemedIconCheckbox XLIcon = createLengthButton("XL");


    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$FILTERS_SETTINGS$");
        settingsPage.addCheckbox("$RATED_LEVELS_ONLY$", "", "rated");
        settingsPage.addCheckbox("$UNRATED_LEVELS_ONLY$", "", "unrated");
        settingsPage.addCheckbox("$DISABLE_SELECTED_DIFFICULTIES$", "", "disableDifficulties", true, null);

        difficultyPanel.setBackground(new Color(0,0,0,0));

        difficultyPanel.add(naIcon);
        difficultyPanel.add(autoIcon);
        difficultyPanel.add(easyIcon);
        difficultyPanel.add(normalIcon);
        difficultyPanel.add(hardIcon);
        difficultyPanel.add(harderIcon);
        difficultyPanel.add(insaneIcon);
        difficultyPanel.add(easyDemonIcon);
        difficultyPanel.add(mediumDemonIcon);
        difficultyPanel.add(hardDemonIcon);
        difficultyPanel.add(insaneDemonIcon);
        difficultyPanel.add(extremeDemonIcon);

        difficultyPanelScroll.setVerticalScrollEnabled(false);
        difficultyPanelScroll.setHorizontalScrollEnabled(true);


        difficultyPanelWithScroll.setPreferredSize(new Dimension(500,80));
        difficultyPanelWithScroll.setBackground(new Color(255,255,255,20));
        difficultyPanelWithScroll.add(difficultyPanelScroll);


        SettingsComponent difficultyComponent = new SettingsComponent(difficultyPanelWithScroll, new Dimension(700,80)){
            @Override
            protected void resizeComponent(Dimension dimension){
                setPreferredSize(new Dimension(dimension.width-300,getPreferredSize().height));
                difficultyPanelScroll.setPreferredSize(new Dimension(dimension.width-340,difficultyPanelWithScroll.getPreferredSize().height));
                difficultyPanelWithScroll.setPreferredSize(new Dimension(dimension.width-340,difficultyPanelWithScroll.getPreferredSize().height));
                difficultyPanelWithScroll.setBounds(30,0,difficultyPanelWithScroll.getPreferredSize().width, difficultyPanelWithScroll.getPreferredSize().height);
            }
            @Override
            protected void refreshUI(){
                difficultyPanelWithScroll.setBackground(new ThemedColor("color6", difficultyPanelScroll, ThemedColor.BACKGROUND));
                difficultyPanelWithScroll.setOpaque(false);
            }
        };
        difficultyPanelScroll.setOpaque(false);
        difficultyPanel.setOpaque(false);
        difficultyPanelWithScroll.setOpaque(false);
        difficultyPanelScroll.getViewport().setOpaque(false);
        difficultyComponent.setOpaque(false);
        settingsPage.addComponent(difficultyComponent);
        settingsPage.addCheckbox("$DISABLE_SELECTED_REQUESTED_DIFFICULTIES$", "", "disableReqDifficulties", true, null);

        requestedDifficultyPanel.setBackground(new Color(0,0,0,0));

        requestedDifficultyPanel.add(autoIconReq);
        requestedDifficultyPanel.add(easyIconReq);
        requestedDifficultyPanel.add(normalIconReq);
        requestedDifficultyPanel.add(hardIconReq);
        requestedDifficultyPanel.add(harderIconReq);
        requestedDifficultyPanel.add(insaneIconReq);
        requestedDifficultyPanel.add(hardDemonIconReq);

        requestedDifficultyPanelScroll.setVerticalScrollEnabled(false);
        requestedDifficultyPanelScroll.setHorizontalScrollEnabled(true);


        requestedDifficultyPanelWithScroll.setPreferredSize(new Dimension(500,80));
        requestedDifficultyPanelWithScroll.setBackground(new Color(255,255,255,20));
        requestedDifficultyPanelWithScroll.add(requestedDifficultyPanelScroll);


        SettingsComponent requestedDifficultyComponent = new SettingsComponent(requestedDifficultyPanelWithScroll, new Dimension(700,80)){
            @Override
            protected void resizeComponent(Dimension dimension){
                setPreferredSize(new Dimension(dimension.width-300,getPreferredSize().height));
                requestedDifficultyPanelScroll.setPreferredSize(new Dimension(dimension.width-340,requestedDifficultyPanelWithScroll.getPreferredSize().height));
                requestedDifficultyPanelWithScroll.setPreferredSize(new Dimension(dimension.width-340,requestedDifficultyPanelWithScroll.getPreferredSize().height));
                requestedDifficultyPanelWithScroll.setBounds(30,0,requestedDifficultyPanelWithScroll.getPreferredSize().width, requestedDifficultyPanelWithScroll.getPreferredSize().height);
            }
            @Override
            protected void refreshUI(){
                requestedDifficultyPanelWithScroll.setBackground(new ThemedColor("color6", requestedDifficultyPanelScroll, ThemedColor.BACKGROUND));
                requestedDifficultyPanelWithScroll.setOpaque(false);
            }
        };
        requestedDifficultyPanelScroll.setOpaque(false);
        requestedDifficultyPanel.setOpaque(false);
        requestedDifficultyPanelWithScroll.setOpaque(false);
        requestedDifficultyPanelScroll.getViewport().setOpaque(false);
        requestedDifficultyComponent.setOpaque(false);
        settingsPage.addComponent(requestedDifficultyComponent);




        lengthPanel.setBackground(new Color(0,0,0,0));

        lengthPanel.add(tinyIcon);
        lengthPanel.add(shortIcon);
        lengthPanel.add(mediumIcon);
        lengthPanel.add(longIcon);
        lengthPanel.add(XLIcon);


        lengthPanelWithScroll.setPreferredSize(new Dimension(500,80));
        lengthPanelWithScroll.setBackground(new Color(255,255,255,20));
        lengthPanelWithScroll.add(lengthPanelScroll);
        lengthPanelScroll.setVerticalScrollEnabled(false);
        lengthPanelScroll.setHorizontalScrollEnabled(true);

        SettingsComponent lengthComponent = new SettingsComponent(lengthPanelWithScroll, new Dimension(700,80)){
            @Override
            protected void resizeComponent(Dimension dimension){
                setPreferredSize(new Dimension(dimension.width-300,getPreferredSize().height));
                lengthPanelScroll.setPreferredSize(new Dimension(dimension.width-340,lengthPanelScroll.getPreferredSize().height));
                lengthPanelWithScroll.setPreferredSize(new Dimension(dimension.width-340,lengthPanelScroll.getPreferredSize().height));
                lengthPanelWithScroll.setBounds(30,0,lengthPanelScroll.getPreferredSize().width, lengthPanelScroll.getPreferredSize().height);
            }
            @Override
            protected void refreshUI(){
                lengthPanelWithScroll.setBackground(new ThemedColor("color6", lengthPanelScroll, ThemedColor.BACKGROUND));
                lengthPanelScroll.setOpaque(false);
            }
        };

        lengthPanelScroll.setOpaque(false);
        lengthPanel.setOpaque(false);
        lengthComponent.setOpaque(false);
        lengthPanelWithScroll.setOpaque(false);
        lengthPanelScroll.getViewport().setOpaque(false);

        settingsPage.addCheckbox("$DISABLE_SELECTED_LENGTHS$", "", "disableLengths", true, null);

        settingsPage.addComponent(lengthComponent);

        settingsPage.addCheckedInput("$MINIMUM_LIKES$", "", 1, true, true, false, "minLikesOption", "minLikes");
        settingsPage.addCheckedInput("$MAXIMUM_LIKES$", "", 1, true, true, false, "maxLikesOption", "maxLikes");
        settingsPage.addCheckedInput("$MINIMUM_OBJECTS$", "", 1, true, false, false, "minObjectsOption", "minObjects");
        settingsPage.addCheckedInput("$MAXIMUM_OBJECTS$", "", 1, true, false, false, "maxObjectsOption", "maxObjects");
        settingsPage.addCheckedInput("$MINIMUM_ID$", "", 1, true, false, false, "minIDOption", "minID");
        settingsPage.addCheckedInput("$MAXIMUM_ID$", "", 1, true, false, false, "maxIDOption", "maxID");

        loadDifficultiesAndLengths();
        return settingsPage;
    }
    private static ThemedIconCheckbox createDifficultyButton(String difficulty) {
        ImageIcon imageIcon = Assets.difficultyIconsNormal.get(difficulty);

        ThemedIconCheckbox checkbox = createIconCheckbox(imageIcon,30);
        checkbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkbox.getSelectedState()) excludedDifficulties.add(difficulty.toLowerCase());
                else excludedDifficulties.remove(difficulty.toLowerCase());
                SettingsHandler.writeSettings("difficultyFilter", excludedDifficulties.toString());
            }
        });
        return checkbox;
    }
    private static ThemedIconCheckbox createReqDifficultyButton(String difficulty) {
        ImageIcon imageIcon = Assets.difficultyIconsNormal.get(difficulty);

        ThemedIconCheckbox checkbox = createIconCheckbox(imageIcon,30);
        checkbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkbox.getSelectedState()) excludedRequestedDifficulties.add(difficulty.toLowerCase());
                else excludedRequestedDifficulties.remove(difficulty.toLowerCase());
                SettingsHandler.writeSettings("reqDifficultyFilter", excludedRequestedDifficulties.toString());
            }
        });
        return checkbox;
    }

    private static ThemedIconCheckbox createLengthButton(String length) {
        ImageIcon imageIcon = Assets.lengthIcons.get(length);

        ThemedIconCheckbox checkbox = createIconCheckbox(imageIcon,16);
        checkbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkbox.getSelectedState()) excludedLengths.add(length.toLowerCase());
                else excludedLengths.remove(length.toLowerCase());
                SettingsHandler.writeSettings("lengthFilter", excludedLengths.toString());
            }
        });
        return checkbox;
    }

    private static ThemedIconCheckbox createIconCheckbox(ImageIcon imageIcon, int height) {
        if(imageIcon == null){
            return null;
        }
        double ratio = imageIcon.getIconWidth()/(double)imageIcon.getIconHeight();
        if(ratio < 0){
            ratio = imageIcon.getIconHeight()/(double)imageIcon.getIconWidth();
        }
        int newWidth = (int) (ratio * height);

        Icon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(newWidth, height, Image.SCALE_SMOOTH));
        //Icon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        ThemedIconCheckbox button = new ThemedIconCheckbox(icon);
        button.setForeground(Defaults.FOREGROUND_A);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setPreferredSize(new Dimension(icon.getIconWidth()+6,height+50));
        button.refresh();
        return button;
    }
    public static void loadDifficultiesAndLengths() {
        String excludedReqDifficultiesString = SettingsHandler.getSettings("reqDifficultyFilter").asString();
        String excludedDifficultiesString = SettingsHandler.getSettings("difficultyFilter").asString();
        String excludedLengthsString = SettingsHandler.getSettings("lengthFilter").asString();
        if(excludedDifficultiesString.length() != 0) {
            excludedDifficulties = new ArrayList<>(Arrays.asList(excludedDifficultiesString.substring(1, excludedDifficultiesString.length() - 1).split(", ")));
        }
        if(excludedReqDifficultiesString.length() != 0) {
            excludedRequestedDifficulties = new ArrayList<>(Arrays.asList(excludedReqDifficultiesString.substring(1, excludedReqDifficultiesString.length() - 1).split(", ")));
        }
        if(excludedLengthsString.length() != 0) {
            excludedLengths = new ArrayList<>(Arrays.asList(excludedLengthsString.substring(1, excludedLengthsString.length() - 1).split(", ")));
        }
        if (excludedLengths.contains("tiny")) {
            tinyIcon.setChecked(true);
        }
        if (excludedLengths.contains("short")) {
            shortIcon.setChecked(true);
        }
        if (excludedLengths.contains("medium")) {
            mediumIcon.setChecked(true);
        }
        if (excludedLengths.contains("long")) {
            longIcon.setChecked(true);
        }
        if (excludedLengths.contains("xl")) {
            XLIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("na")) {
            naIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("auto")) {
            autoIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("easy")) {
            easyIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("normal")) {
            normalIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("hard")) {
            hardIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("harder")) {
            harderIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("insane")) {
            insaneIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("easy demon")) {
            easyDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("medium demon")) {
            mediumDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("hard demon")) {
            hardDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("insane demon")) {
            insaneDemonIcon.setChecked(true);
        }
        if (excludedDifficulties.contains("extreme demon")) {
            extremeDemonIcon.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("na")) {
            naIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("auto")) {
            autoIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("easy")) {
            easyIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("normal")) {
            normalIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("hard")) {
            hardIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("harder")) {
            harderIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("insane")) {
            insaneIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("easy demon")) {
            easyDemonIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("medium demon")) {
            mediumDemonIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("hard demon")) {
            hardDemonIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("insane demon")) {
            insaneDemonIconReq.setChecked(true);
        }
        if (excludedRequestedDifficulties.contains("extreme demon")) {
            extremeDemonIconReq.setChecked(true);
        }
    }

}
