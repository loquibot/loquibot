package com.alphalaneous.Swing.ThemedComponents;

import com.alphalaneous.*;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Swing.Components.CurvedButtonAlt;
import com.alphalaneous.Swing.Components.JButtonUI;
import com.alphalaneous.Swing.Components.LangLabel;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Theming.ThemedColor;
import com.alphalaneous.Utils.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ThemedConfigCheckbox extends JPanel {

    private static final ArrayList<ThemedConfigCheckbox> buttons = new ArrayList<>();

    private final JPanel infoPanel = new JPanel();
    private final JPanel titlePanel = new JPanel();
    private final JLabel descriptionText = new JLabel();
    private final CurvedButtonAlt configButton = new CurvedButtonAlt("\uF00F");
    private final LangLabel text = new LangLabel("");
    private final JLabel check = new JLabel("\uE922");
    private final JLabel checkSymbol = new JLabel("\uE73E");
    private final JLabel hover = new JLabel("\uE922");
    private boolean isChecked = false;
    private final boolean isCommand;
    private final String description;
    private final CommandData commandData;
    private final JPanel colorPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g.setColor(getBackground());

            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRoundRect(0, 0, 50, getSize().height, Defaults.globalArc, Defaults.globalArc);
            g2.fillRect(10, 0, getSize().width, getSize().height);

            super.paintComponent(g);
        }
    };

    private final JButtonUI emptyUI = new JButtonUI() {{
        setBackground(new Color(0, 0, 0, 0));
        setHover(Defaults.COLOR2);
        setSelect(new Color(0, 0, 0, 0));

    }};

    private static final Color broadcasterColor = new Color(231, 25, 23);
    private static final Color modColor = new Color(8, 175, 12);
    private static final Color vipColor = new Color(223, 1, 186);
    private static final Color subColor = new Color(129, 5, 180);
    private static final Color everyoneColor = Defaults.COLOR2;
    private final KeywordData keywordData;


    public ThemedConfigCheckbox(String label, String description, Function function, boolean isCommand) {
        this(label, description, function, isCommand, null);
    }

    public ThemedConfigCheckbox(String label, String description, Function function, boolean isCommand, CommandData commandData){
        this(label, description, function, isCommand, commandData, null);
    }
    public ThemedConfigCheckbox(String label, String description, Function function, boolean isCommand, CommandData commandData, KeywordData keywordData) {
        setLayout(null);
        this.description = description;
        this.isCommand = isCommand;
        this.commandData = commandData;
        this.keywordData = keywordData;
        text.setTextLang(label);
        text.setForeground(Defaults.FOREGROUND_A);
        check.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
        checkSymbol.setForeground(Color.WHITE);
        checkSymbol.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
        hover.setForeground(Defaults.FOREGROUND_A);
        hover.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
        checkSymbol.setVisible(false);
        hover.setVisible(false);

        colorPanel.setOpaque(false);
        if(isCommand && commandData != null) {
            colorPanel.setBounds(0, 0, 6, 70);

            switch (commandData.getUserLevel()) {
                case "owner": {
                    colorPanel.setBackground(broadcasterColor);
                    break;
                }
                case "moderator": {
                    colorPanel.setBackground(modColor);
                    break;
                }
                case "twitch_vip": {
                    colorPanel.setBackground(vipColor);
                    break;
                }
                case "subscriber": {
                    colorPanel.setBackground(subColor);
                    break;
                }
                default:
                    colorPanel.setBackground(Defaults.COLOR5);
            }
        }
        if(keywordData != null){
            colorPanel.setBounds(0, 0, 6, 70);
            switch (keywordData.getUserLevel()) {
                case "owner": {
                    colorPanel.setBackground(broadcasterColor);
                    break;
                }
                case "moderator": {
                    colorPanel.setBackground(modColor);
                    break;
                }
                case "twitch_vip": {
                    colorPanel.setBackground(vipColor);
                    break;
                }
                case "subscriber": {
                    colorPanel.setBackground(subColor);
                    break;
                }
                default:
                    colorPanel.setBackground(Defaults.COLOR5);
            }
        }

        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBounds(150, 0, getWidth()-150, 66);
        infoPanel.setOpaque(false);
        infoPanel.setBackground(new Color(0,0,0,0));
        titlePanel.setLayout(new BorderLayout());
        if(!isCommand || keywordData == null){
            text.setPreferredSize(new Dimension(40, 30));
            titlePanel.setBounds(50,0, 200, 70);
        }
        else {
            text.setPreferredSize(new Dimension(40, 60));
            titlePanel.setBounds(50,0, 90, 70);
        }
        titlePanel.setOpaque(false);
        titlePanel.setBackground(new Color(0,0,0,0));

        descriptionText.setText("<html><div WIDTH=450>" + Language.setLocale(description) + "</div></html>");
        descriptionText.setFont(Defaults.MAIN_FONT.deriveFont(13f));
        descriptionText.setOpaque(false);

        configButton.setFont(Defaults.SYMBOLS.deriveFont(18f));
        //configButton.setUI(Defaults.settingsButtonUI);
        configButton.setContentAreaFilled(false);
        configButton.setUI(emptyUI);
        configButton.setBackground(new Color(0,0,0,0));
        configButton.setOpaque(false);
        configButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        configButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                configButton.setFont(Defaults.SYMBOLS.deriveFont(22f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                configButton.setFont(Defaults.SYMBOLS.deriveFont(18f));
            }
        });
        configButton.setForeground(Defaults.FOREGROUND_A);
        configButton.addActionListener(e -> {
            if(function != null) function.run();
        });
        if(isCommand || keywordData != null) {
            add(colorPanel);
        }
        add(configButton);

        add(hover);
        add(checkSymbol);
        add(check);
        if(!isCommand || keywordData == null){
            titlePanel.setLayout(null);
            text.setBounds(0,10,500,30);
            descriptionText.setBounds(0,30,500,30);

            titlePanel.add(text);
            titlePanel.add(descriptionText);
        }
        else {
            titlePanel.add(text);
            infoPanel.add(descriptionText);
            add(infoPanel);
        }
        add(titlePanel);

        setBackground(new ThemedColor("color", this, ThemedColor.BACKGROUND));

        check.setForeground(Defaults.FOREGROUND_B);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    check.setText("\uE73B");
                    check.setForeground(Defaults.FOREGROUND_B);
                    hover.setVisible(false);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isChecked) {
                        check.setText("\uE922");
                        check.setForeground(Defaults.FOREGROUND_B);
                        checkSymbol.setVisible(false);
                        isChecked = false;
                    } else {
                        check.setText("\uE73B");
                        check.setForeground(Defaults.ACCENT);
                        checkSymbol.setVisible(true);
                        isChecked = true;
                    }
                }
                hover.setVisible(true);
            }

            public void mouseEntered(MouseEvent e) {
                hover.setVisible(true);
            }

            public void mouseExited(MouseEvent e) {
                if (!isChecked) {
                    check.setText("\uE922");
                    check.setForeground(Defaults.FOREGROUND_B);
                    checkSymbol.setVisible(false);
                } else {
                    check.setText("\uE73B");
                    check.setForeground(Defaults.ACCENT);
                    checkSymbol.setVisible(true);
                }
                hover.setVisible(false);
            }
        });
        buttons.add(this);
    }

    public static void refreshAll() {
        for (ThemedConfigCheckbox button : buttons) {
            button.refresh();
        }
    }

    public void setText(String textA) {
        text.setTextLang(textA);
    }

    public boolean getSelectedState() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
        if (!isChecked) {
            check.setText("\uE922");
            check.setForeground(Defaults.FOREGROUND_B);
            checkSymbol.setVisible(false);
        } else {
            check.setText("\uE73B");
            check.setForeground(Defaults.ACCENT);
            checkSymbol.setVisible(true);
        }
    }

    public void resize(int width) {

        infoPanel.setBounds(150, 0, width-150, 66);
        titlePanel.setBounds(50,0, width-100, 70);
        if(!isCommand || keywordData == null){
            descriptionText.setText("<html><div WIDTH=" + (width - 100) + ">" + Language.setLocale(description) + "</div></html>");
            text.setBounds(0,10,width-100,30);
            descriptionText.setBounds(0,30,width-100,30);
        }
        else{
            descriptionText.setText("<html><div WIDTH=" + (width - 220) + ">" + Language.setLocale(description) + "</div></html>");
            descriptionText.setBounds(0,30,width-220,30);
        }
        check.setBounds(20, 20, 30, 30);
        checkSymbol.setBounds(20, 20, 30, 30);
        hover.setBounds(20, 20, 30, 30);
        configButton.setBounds(width-50, 20, 30, 30);

    }

    public void refresh() {
        if (!isChecked) {
            check.setForeground(Defaults.FOREGROUND_B);
        } else {
            check.setForeground(Defaults.ACCENT);
        }
        descriptionText.setForeground(Defaults.FOREGROUND_B);
        text.setForeground(Defaults.FOREGROUND_A);
        text.setFont(getFont());
        //text.setBounds(50, 13, getWidth(), text.getPreferredSize().height + 5);
        //descriptionText.setBounds(50, 33, getWidth(), descriptionText.getPreferredSize().height + 5);
        configButton.setBounds(getWidth()-50, 20, 30, 30);

        if(isCommand && commandData!= null){
            if(commandData.getUserLevel().equalsIgnoreCase("everyone")) colorPanel.setBackground(Defaults.COLOR5);
        }
        if(keywordData != null){
            if(keywordData.getUserLevel().equalsIgnoreCase("everyone")) colorPanel.setBackground(Defaults.COLOR5);
        }

        emptyUI.setBackground(new Color(0,0, 0,0));
        emptyUI.setHover(Defaults.COLOR2);
        emptyUI.setSelect(new Color(0,0, 0,0));

        configButton.setUI(emptyUI);
        configButton.setBackground(new Color(0,0,0,0));
        configButton.setForeground(Defaults.FOREGROUND_A);
        check.setBounds(20, 20, 30, 30);
        checkSymbol.setBounds(20, 20, 30, 30);
        hover.setForeground(Defaults.FOREGROUND_A);
        hover.setBounds(20, 20, 30, 30);
    }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(getBackground());

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.fillRoundRect(0, 0, getSize().width, getSize().height, Defaults.globalArc, Defaults.globalArc);


        super.paintComponent(g);
    }

}
