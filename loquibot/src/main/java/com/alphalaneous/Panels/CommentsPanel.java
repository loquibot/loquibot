package com.alphalaneous.Panels;

import com.alphalaneous.Components.*;
import com.alphalaneous.Defaults;
import com.alphalaneous.GDAPI;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.ThemedColor;
import jdash.client.exception.GDClientException;
import jdash.common.entity.GDComment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CommentsPanel extends JPanel {

    private final JPanel panel = new JPanel();
    private final JButtonUI buttonUI = new JButtonUI();
    private final int width = 285;
    private final JButtonUI newUI = new JButtonUI();
    private final JScrollPane scrollPane = new SmoothScrollPane(panel){
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g.setColor(getBackground());

            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRect(0,20, getSize().width, getSize().height);
            g2.fillRoundRect(0, 0, getSize().width, getSize().height, Defaults.globalArc, Defaults.globalArc);


            super.paintComponent(g);
        }

    };
    private final JPanel buttons = new JPanel(){
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g.setColor(getBackground());

            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRect(0,0, getSize().width, getSize().height-20);
            g2.fillRoundRect(0, 0, getSize().width, getSize().height, Defaults.globalArc, Defaults.globalArc);


            super.paintComponent(g);
        }

    };
    private boolean top = false;
    private int page = 0;

    public CommentsPanel(){

        setLayout(null);
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        buttonUI.setBackground(Defaults.COLOR6);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);

        newUI.setBackground(Defaults.COLOR);
        newUI.setHover(Defaults.COLOR3);
        newUI.setSelect(Defaults.COLOR4);

        buttons.setLayout(null);


        JButton prev = createButton("\uF305", 0, "$PREV_PAGE$");
        prev.addActionListener(e -> {
            if (page != 0) {
                page = page - 2;
                try {
                    loadComments(page, top);
                } catch (Exception ignored) { }
            }

        });

        JButton next = createButton("\uF304", 35, "$NEXT_PAGE$");
        next.addActionListener(e -> {
            page = page + 2;
            if (!loadComments(page, top)) {
                page = page - 2;
                try {
                    loadComments(page, top);
                } catch (Exception ignored) { }
            }
        });

        JButton topComments = createButton("\uF138", 90, "$TOP_COMMENTS$");
        topComments.addActionListener(e -> {
            page = 0;
            try {
                loadComments(0, true);
            } catch (Exception ignored) { }

        });

        JButton newest = createButton("\uF22B", 125, "$LATEST_COMMENTS$");
        newest.addActionListener(e -> {
            top = false;
            page = 0;
            try {
                loadComments(0, false);
            } catch (Exception ignored) { }
        });

        buttons.add(prev);
        buttons.add(next);
        buttons.add(topComments);
        buttons.add(newest);

        scrollPane.setBounds(0, 0, width, 0);

        buttons.setBounds(0, 0, width, 40);


        add(scrollPane);
        add(buttons);

        scrollPane.getVerticalScrollBar().setBackground(new ThemedColor("color3", scrollPane.getVerticalScrollBar(), ThemedColor.BACKGROUND));
        scrollPane.setBackground(new ThemedColor("color3", scrollPane, ThemedColor.BACKGROUND));
        scrollPane.getViewport().setBackground(new ThemedColor("color3", scrollPane.getViewport(), ThemedColor.BACKGROUND));

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);


        buttons.setBackground(new ThemedColor("color6", buttons, ThemedColor.BACKGROUND));
        panel.setBackground(new ThemedColor("color3", panel, ThemedColor.BACKGROUND));

        buttons.setOpaque(false);
        panel.setOpaque(false);

        panel.setPreferredSize(new Dimension(width, 0));
        panel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 4));
        panel.setVisible(false);

    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(getBackground());

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.fillRoundRect(0, 0, getSize().width, getSize().height, 20, 20);

        super.paintComponent(g);
    }

    public void unloadComments(boolean reset) {
        panel.setVisible(false);
        if (reset) {
            top = false;
            page = 0;
        }
        panel.removeAll();
        panel.setPreferredSize(new Dimension(width, 0));
    }

    public boolean loadComments(int page, boolean top) {
        //todo create a new panel each time to prevent overlap
        this.top = top;
        if (RequestsTab.getQueueSize() == 0) {
            return false;
        }
        try {
            int panelHeight = 0;
            panel.removeAll();
            panel.setVisible(false);

            List<GDComment> commentsList = GDAPI.getGDComments(RequestsTab.getRequest(LevelButton.selectedID).getLevelData().getGDLevel().id(), top, page);

            for (GDComment com : commentsList) {
                Comment comment = new Comment(com, width - 15);
                panelHeight += 32 + comment.getContentHeight();

                panel.setPreferredSize(new Dimension(width, panelHeight));
                panel.add(comment);
                Thread.sleep(0);
            }
            //scrollPane.getViewport().setViewPosition(new Point(0, 0));
            panel.setVisible(true);
            return true;

        } catch (GDClientException | InterruptedException e) {
            return false;
        }
    }

    public void resizeHeight(int x, int height) {
        scrollPane.setBounds(0, 0, width, height - 88);
        buttons.setBounds(0, height - 88, width, 40);
        setBounds(x+5, 5, width, height-10);
    }

    private JButton createButton(String icon, int x, String tooltip) {
        JButton button = new RoundedJButton(icon, tooltip);
        button.setFont(Defaults.SYMBOLS.deriveFont(16f));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setForeground(Defaults.FOREGROUND_A);
        button.setBackground(Defaults.COLOR6);
        button.setUI(buttonUI);
        button.setBounds(x + 5, 5, 30, 30);
        return button;
    }
    public void refreshUI(){
        buttonUI.setBackground(Defaults.COLOR6);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);
        newUI.setBackground(Defaults.COLOR);
        newUI.setHover(Defaults.COLOR3);
        newUI.setSelect(Defaults.COLOR4);
        scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());


        for (Component component : panel.getComponents()) {
            if (component instanceof Comment) {
                ((Comment) component).refresh();
            }
        }

        for (Component component : buttons.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(Defaults.COLOR6);
                component.setForeground(Defaults.FOREGROUND_A);
            }
        }
    }
}
