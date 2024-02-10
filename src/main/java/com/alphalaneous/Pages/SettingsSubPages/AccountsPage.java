package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.DialogBox;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.InteractionPages.ChannelPointsPage;
import com.alphalaneous.Pages.SettingsPage;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Servers;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.AccountPanel;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.Twitch.TwitchBotAccount;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AccountsPage {

    static SettingsSubPage page = new SettingsSubPage("$ACCOUNTS_TITLE$");

    static AccountPanel twitchAccount;

    static AccountPanel youTubeAccount;

    static AccountPanel botAccount = new AccountPanel("Twitch Bot Account", () -> {
        System.out.println("clicked bot");
    });

    @OnLoad(order = 10004)
    public static void init(){

        youTubeAccount = new AccountPanel("YouTube Account", () -> showAccountManagement(youTubeAccount, () -> {
            YouTubeAccount.setCredential(true, false);
            setYouTubeAccountInfo();
            DialogBox.closeDialogBox();
        }, YouTubeAccount::logout, YouTubeAccount.name, YouTubeAccount.profileImage, "YouTube"));

        twitchAccount = new AccountPanel("Twitch Account", () -> showAccountManagement(twitchAccount, () -> {
            TwitchAPI.setOauth(false);
            setTwitchAccountInfo();
            ChannelPointsPage.load();
            Window.loadTwitchChat(TwitchAccount.login);
            TwitchChatListener.getCurrentListener().reconnect(TwitchAccount.login, SettingsHandler.getSettings("oauth").asString());
            DialogBox.closeDialogBox();
            Servers.connectTwitch();
            StreamInteractionsPage.setEnabled(true);
        }, TwitchAccount::logout, TwitchAccount.display_name, TwitchAccount.profileImage, "Twitch"));

        youTubeAccount.setLoginButton(createLoginButton("Log in with YouTube", Assets.getImage("youtube-logo"), () -> new Thread(() -> {
            try {
                YouTubeAccount.setCredential(true, false);
                setYouTubeAccountInfo();
            }
            catch (Exception e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }).start()));

        twitchAccount.setLoginButton(createLoginButton("Log in with Twitch", Assets.getImage("twitch-logo"), () -> new Thread(() -> {
            TwitchAPI.setOauth(false);
            setTwitchAccountInfo();
            ChannelPointsPage.load();
            TwitchChatListener chatListener = new TwitchChatListener(TwitchAccount.login);
            chatListener.connect(SettingsHandler.getSettings("oauth").asString());
            StreamInteractionsPage.setEnabled(true);
            Servers.connectTwitch();
            Window.loadTwitchChat(TwitchAccount.login);
        }).start()));

        page.addComponent(twitchAccount);
        page.addComponent(youTubeAccount);
        page.addComponent(botAccount);

        if(!SettingsHandler.getSettings("differentBotAccount.use").asBoolean()){
            botAccount.login("Loquibot", Assets.getImage("loquibot-account-icon"));
        }

        SettingsPage.addPage("$ACCOUNTS_TITLE$", "\uF161", page, null);
    }

    public static RoundedButton createLoginButton(String text, ImageIcon icon, Function f){
        RoundedButton loginButton = new RoundedButton("");

        loginButton.addActionListener(e -> {
            if(f != null) f.run();
        });

        ThemeableJLabel label = new ThemeableJLabel(text);
        label.setForeground("foreground");
        label.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
        label.setBorder(new EmptyBorder(4,4,4,4));

        loginButton.setArc(10);
        loginButton.setBorder(new EmptyBorder(4,4,4,4));

        ThemeableJLabel iconLabel = new ThemeableJLabel();

        iconLabel.setIcon(icon);
        iconLabel.setBorder(new EmptyBorder(4,4,4,4));

        loginButton.setLayout(new GridBagLayout());

        loginButton.add(iconLabel);
        loginButton.add(label);

        return loginButton;
    }

    public static void setTwitchAccountInfo(){
        twitchAccount.login(TwitchAccount.display_name, new ImageIcon(Assets.makeRoundedCorner(TwitchAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    }

    public static void setYouTubeAccountInfo(){
        youTubeAccount.login(YouTubeAccount.name, new ImageIcon(Assets.makeRoundedCorner(YouTubeAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    }

    public static void setBotAccountInfo(){
        botAccount.login(TwitchAccount.display_name, new ImageIcon(Assets.makeRoundedCorner(TwitchBotAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    }

    public static void showAccountManagement(AccountPanel panel, Function loginFunction, Function logoutFunction, String username, BufferedImage profilePicture, String platform){

        ThemeableJPanel parent = new ThemeableJPanel();
        parent.setLayout(new BorderLayout());
        parent.setSize(500,280);
        parent.setBackground("background");
        parent.setBorder(new EmptyBorder(10,10,10,10));
        ThemeableJPanel panel1 = new ThemeableJPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setBackground("background");


        ImageIcon icon = new ImageIcon(Assets.makeRoundedCorner(profilePicture).getScaledInstance(60, 60, Image.SCALE_SMOOTH));

        ThemeableJPanel titlePanel = new ThemeableJPanel();
        titlePanel.setBackground("background");
        titlePanel.setLayout(new BorderLayout());

        ThemeableJLabel titleLabel = new ThemeableJLabel("$ACCOUNT_MANAGEMENT$");
        titleLabel.setForeground("foreground");
        titleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(20f));


        titlePanel.add(titleLabel);

        AccountPanel infoPanel = new AccountPanel(platform, null);
        infoPanel.login(username, icon);



        ThemeableJPanel buttonsPanel = new ThemeableJPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2,5, 5));
        buttonsPanel.setPreferredSize(new Dimension(100, 40));
        buttonsPanel.setBackground("background");

        RoundedButton refreshLoginButton = new RoundedButton("$REFRESH_LOGIN$");
        RoundedButton logoutButton = new RoundedButton("$LOGOUT$");

        ThemeableJPanel cancelPanel = new ThemeableJPanel();
        cancelPanel.setLayout(new GridLayout(1, 2,5, 5));
        cancelPanel.setBackground("background");

        RoundedButton cancelButton = new RoundedButton("$CANCEL$");
        cancelPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        cancelPanel.add(cancelButton);
        cancelPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        cancelPanel.setPreferredSize(new Dimension(100, 40));

        logoutButton.setForeground("error-red", "error-red");

        refreshLoginButton.addActionListener(e -> loginFunction.run());

        logoutButton.addActionListener(e -> {
            logoutFunction.run();
            panel.logout();
            DialogBox.closeDialogBox();
        });

        cancelButton.addActionListener(e -> DialogBox.closeDialogBox());

        buttonsPanel.add(refreshLoginButton);

        buttonsPanel.add(logoutButton);


        panel1.add(titlePanel);
        panel1.add(Box.createRigidArea(new Dimension(0, 15)));
        panel1.add(infoPanel);
        panel1.add(Box.createRigidArea(new Dimension(0, 5)));
        panel1.add(buttonsPanel);
        panel1.add(Box.createRigidArea(new Dimension(0, 30)));

        panel1.add(cancelPanel);

        parent.add(panel1, BorderLayout.PAGE_START);

        DialogBox.showDialogBox(parent, false);

    }
}
