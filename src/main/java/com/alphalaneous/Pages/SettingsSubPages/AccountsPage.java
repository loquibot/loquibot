package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.SettingsPage;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Services.YouTube.YouTubeAuth;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.AccountPanel;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.Twitch.TwitchBotAccount;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountsPage {

    static SettingsSubPage page = new SettingsSubPage("$ACCOUNTS_TITLE$");

    static AccountPanel twitchAccount = new AccountPanel("Twitch Account", () -> {
        System.out.println("clicked twitch");
    });

    static AccountPanel youTubeAccount = new AccountPanel("YouTube Account", () -> {
        System.out.println("clicked youtube");
    });

    static AccountPanel botAccount = new AccountPanel("Twitch Bot Account", () -> {
        System.out.println("clicked bot");
    });

    @OnLoad(order = 10004)
    public static void init(){

        youTubeAccount.setLoginButton(createLoginButton("Log in with YouTube", Assets.getImage("youtube-logo"), () -> {
            try {
                YouTubeAccount.setCredential(true, false);
            }
            catch (Exception e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }));

        twitchAccount.setLoginButton(createLoginButton("Log in with Twitch", Assets.getImage("twitch-logo"), () -> {
            TwitchAPI.setOauth(false);
        }));

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
}
