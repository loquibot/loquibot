package com.alphalaneous.Panels;

import com.alphalaneous.Defaults;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.ThemedColor;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoPanel extends JPanel {

    private final JTextPane description = new JTextPane();

    public InfoPanel() {

        setLayout(null);

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
        setBounds(5, 0, 10, 100);
        setBackground(new ThemedColor("color3", this, ThemedColor.BACKGROUND));
        setOpaque(false);

        add(description);

    }

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

    public void resetBounds(int x, int y, int width, int height) {
        description.setBounds(3, 5, width - 16, height);
        setBounds(x + 5, y, width - 10, height);
    }


    private void appendToPane(JTextPane tp, String msg, Color c) {
        description.setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset;
        if (c != null) aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        else aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Defaults.FOREGROUND_A);

        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_CENTER);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
        description.setEditable(false);

    }

    public JPanel getWindow() {
        return this;
    }

    public void refreshInfo() {
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
        description.setVisible(true);
    }

    public void refreshUI() {
        description.setForeground(Defaults.FOREGROUND_A);
    }

}
