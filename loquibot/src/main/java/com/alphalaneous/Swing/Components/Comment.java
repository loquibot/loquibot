package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Services.GeometryDash.GDAPI;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Utilities;
import jdash.common.IconType;
import jdash.common.entity.GDComment;
import jdash.common.entity.GDUser;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Comment extends JPanel {

    private final JLabel content;
    private final JLabel commentAuthorLabel;
    private final JLabel likesLabel;
    private final JLabel likeIcon;
    private final JLabel playerIcon;
    private final JLabel percentLabel;
    private String creatorName;
    private String commentAuthor;
    private int percent = 0;
    public Comment(GDComment comment, int width){

        setLayout(null);
        setOpaque(false);
        int likes = comment.likes();
        commentAuthor = "";
        creatorName = "";

        String commentContent = String.format("<html><div WIDTH=%d>%s</div></html>", width - 15, StringEscapeUtils.unescapeHtml4(comment.content()));

        GDUser commentAuthorUser = null;

        Optional<Integer> percentOptional = comment.percentage();
        Optional<GDUser> commentAuthorOptional = comment.author();
        Optional<String> creatorNameOptional = Optional.empty();

        if(RequestsTab.getRequest(LevelButton.selectedID) != null){
            creatorNameOptional = RequestsTab.getRequest(LevelButton.selectedID).getLevelData().getGDLevel().creatorName();
        }


        if (commentAuthorOptional.isPresent()){
            commentAuthorUser = commentAuthorOptional.get();
            commentAuthor = commentAuthorUser.name();
        }

        percentOptional.ifPresent(integer -> percent = integer);
        creatorNameOptional.ifPresent(s -> creatorName = s);

        content = new JLabel(commentContent);
        commentAuthorLabel = new JLabel(commentAuthor);
        likesLabel = new JLabel(String.valueOf(likes));
        likeIcon = new JLabel();
        playerIcon = new JLabel();
        percentLabel = new JLabel();
        if(percent != 0) percentLabel.setText(percent + "%");

        commentAuthorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        commentAuthorLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
        commentAuthorLabel.setForeground(Defaults.FOREGROUND_A);
        commentAuthorLabel.setBounds(30, 2, commentAuthorLabel.getPreferredSize().width, 18);

        if (commentAuthor.equalsIgnoreCase("Alphalaneous")){

            new Thread(() -> {
                int tries = 0;
                while(tries < 10) {
                    float hue = 0;
                    do {
                        content.setForeground(Color.getHSBColor(hue, 1, 1));
                        content.invalidate();
                        content.revalidate();
                        content.repaint();
                        hue += 0.01;
                        if (hue > 1) hue = 0;
                        Utilities.sleep(30);
                    } while (content.isShowing());
                    tries++;
                }
            }).start();

        }
        else if (commentAuthor.equalsIgnoreCase(creatorName)) content.setForeground(new Color(246, 255, 0));
        else content.setForeground(Defaults.FOREGROUND_C);
        String finalCommentAuthor = commentAuthor;
        commentAuthorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Utilities.openURL(new URI("https://www.gdbrowser.com/profile/" + finalCommentAuthor));
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                int center = (commentAuthorLabel.getPreferredSize().width) / 2;
                commentAuthorLabel.setFont(Defaults.MAIN_FONT.deriveFont(13f));
                commentAuthorLabel.setBounds(30 + center - (commentAuthorLabel.getPreferredSize().width) / 2, 2,
                        commentAuthorLabel.getPreferredSize().width + 5, 18);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                commentAuthorLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
                commentAuthorLabel.setBounds(30, 2, commentAuthorLabel.getPreferredSize().width, 18);
            }
        });

        percentLabel.setFont(Defaults.MAIN_FONT.deriveFont(11f));
        percentLabel.setForeground(Defaults.FOREGROUND_C);
        percentLabel.setBounds(commentAuthorLabel.getPreferredSize().width + 42, 1,
                percentLabel.getPreferredSize().width + 5, 18);

        likeIcon.setText("\uF138");
        likeIcon.setBounds(width - 16, 2, 20, 20);
        likeIcon.setFont(Defaults.SYMBOLS.deriveFont(14f));
        likeIcon.setForeground(Defaults.FOREGROUND_A);

        if (likes < 0) {
            likeIcon.setText("\uF139");
            likeIcon.setBounds(width - 16, 7, 20, 20);
        }

        likesLabel.setFont(Defaults.MAIN_FONT.deriveFont(10f));
        likesLabel.setForeground(Defaults.FOREGROUND_A);
        likesLabel.setBounds(width - likesLabel.getPreferredSize().width - 22, 6, likesLabel.getPreferredSize().width + 5, 18);

        playerIcon.setIcon(GDAPI.getIcon(IconType.CUBE, 1, 1, 1, false, 25));
        playerIcon.setBounds(3, -5, 30 + 2, 30 + 2);
        if(commentAuthorUser != null) {
            Optional<IconType> mainIconTypeOptional = commentAuthorUser.mainIconType();
            Optional<Integer> mainIconIDOptional = commentAuthorUser.mainIconId();

            if (mainIconTypeOptional.isPresent() && mainIconIDOptional.isPresent()) {
                GDUser finalCommentAuthorUser = commentAuthorUser;
                new Thread(() -> playerIcon.setIcon(GDAPI.getIcon(mainIconTypeOptional.get(),
                        mainIconIDOptional.get(),
                        finalCommentAuthorUser.color1Id(),
                        finalCommentAuthorUser.color2Id(),
                        finalCommentAuthorUser.hasGlowOutline(), 25))).start();
            }
        }


        content.setFont(Defaults.MAIN_FONT.deriveFont(11.5f));

        content.setBounds(9, 22, width - 15, content.getPreferredSize().height);
        content.setOpaque(false);

        add(commentAuthorLabel);
        add(content);
        add(percentLabel);
        add(likesLabel);
        add(likeIcon);
        add(playerIcon);

        setBackground(new Color(0,0,0,0));
        setPreferredSize(new Dimension(width, 28 + content.getPreferredSize().height));
    }

    public void refresh(){
        setBackground(Defaults.COLOR3);
        content.setForeground(Defaults.FOREGROUND_C);
        commentAuthorLabel.setForeground(Defaults.FOREGROUND_A);
        if (commentAuthor.equalsIgnoreCase(creatorName)) commentAuthorLabel.setForeground(new Color(47, 62, 195));
        likeIcon.setForeground(Defaults.FOREGROUND_A);
        likesLabel.setForeground(Defaults.FOREGROUND_A);
        percentLabel.setForeground(Defaults.FOREGROUND_C);
    }
}
