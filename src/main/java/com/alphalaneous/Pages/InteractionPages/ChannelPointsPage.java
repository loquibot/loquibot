package com.alphalaneous.Pages.InteractionPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints.ChannelPointData;
import com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints.ChannelPointReward;
import com.alphalaneous.Layouts.WrapLayout;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.GraphicsFunctions;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ChannelPointsPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    static ThemeableJLabel notAvailable = new ThemeableJLabel("$CHANNEL_POINTS_UNAVAILABLE$");


    @OnLoad(order = 8)
    public static void init() {

        //gbc.weightx = 1;
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        //gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setLayout(new WrapLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        page.setPreferredSize(new Dimension(500,500));

        JFrame frame = new JFrame();
        //frame.add(page);

        frame.setSize(500,500);
        //frame.setVisible(true);

        StreamInteractionsPage.addPage("$CHANNEL_POINTS_TITLE$", page, () -> {
            StreamInteractionsPage.disableRightButton(false);
            StreamInteractionsPage.setRightButtonIcon("\uF078");
        }, ChannelPointsPage::load);
    }

    public static void load(){


        if(TwitchAccount.broadcaster_type != null) {
            try {
                buttonPanel.removeAll();
                if (TwitchAccount.broadcaster_type.equalsIgnoreCase("affiliate")
                        || TwitchAccount.broadcaster_type.equalsIgnoreCase("partner")) {
                    ArrayList<ChannelPointReward> rewards = TwitchAPI.getChannelPoints();
                    for (ChannelPointReward reward : rewards) {
                        buttonPanel.add(new ChannelPointButton(reward.getId(), reward.getTitle(), reward.getBgColor(), reward.getIcon()), gbc);
                    }
                } else {
                    buttonPanel.add(notAvailable);
                }
            } catch (Exception e) {
                Logging.getLogger().error(e.getMessage(), e);
            }
            buttonPanel.updateUI();
        }
    }

    static class ChannelPointButton extends JButton {

        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
            super.paintComponent(g);
        }

        ChannelPointButton(String id, String name, Color color, Icon icon){

            ThemeableJLabel pointLabel = new ThemeableJLabel(name);
            pointLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel pointIcon = new JLabel(icon);
            pointLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            setBackground(color);
            setLayout(null);

            JButtonUI pointUI = new JButtonUI();

            double brightness = Math.sqrt(color.getRed() * color.getRed() * .241 +
                    color.getGreen() * color.getGreen() * .691 +
                    color.getBlue() * color.getBlue() * .068);

            Color hover;

            if (brightness > 130) {
                hover = getBackground().darker();
            } else {
                hover = getBackground().brighter();

            }

            Color click;

            if (brightness > 130) {
                click = hover.darker();
            } else {
                click = hover.brighter();
            }

            pointUI.setColors(color, hover, click);

            setUI(pointUI);
            pointLabel.setBounds(0, 20, 120, 120);
            pointIcon.setBounds(0, -10, 120, 120);

            add(pointLabel);
            add(pointIcon);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));



            if (brightness > 130) {
                pointLabel.setForeground("black");
            } else {
                pointLabel.setForeground("white");
            }
            setBorder(BorderFactory.createEmptyBorder());
            setPreferredSize(new Dimension(120, 120));
            addActionListener(e -> {

                boolean found = false;

                for (ChannelPointData existingData : ChannelPointData.getRegisteredChannelPoints()) {
                    if (existingData.getId().equalsIgnoreCase(id)) {
                        existingData.setName(name);
                        showEditMenu(existingData);
                        found = true;
                        break;
                    }
                }
                if(!found){

                    ChannelPointData data = new ChannelPointData(id);
                    data.setName(name);
                    data.register();

                    showEditMenu(data);
                }
            });
        }
    }
    public static void showEditMenu(ChannelPointData dataParam){

        String title = "$EDIT_REWARD$";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            Utilities.ifNotNull(kv.get("message"), o -> dataParam.setMessage((String) o));


            dataParam.save(true);
            e.close();

        });
        editCommandPanel.addDisabledNameInput("$REWARD_NAME_INPUT$", "$REWARD_NAME_DESC$");
        editCommandPanel.addMessageInput();
        editCommandPanel.setBounds(0,0,800,380);

        editCommandPanel.showMenu();
    }
}
