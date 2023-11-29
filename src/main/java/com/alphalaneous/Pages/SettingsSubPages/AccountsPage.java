package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.AccountPanel;
import com.alphalaneous.Pages.SettingsPage;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.Twitch.TwitchBotAccount;
import com.alphalaneous.Utilities.SettingsHandler;

import javax.swing.*;
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


        page.addComponent(twitchAccount);
        page.addComponent(youTubeAccount);
        page.addComponent(botAccount);

        if(!SettingsHandler.getSettings("differentBotAccount.use").asBoolean()){
            botAccount.refreshInfo("Loquibot", Assets.getImage("loquibot-account-icon"));
        }

        SettingsPage.addPage("$ACCOUNTS_TITLE$", "\uF161", page, null);
    }

    public static void setTwitchAccountInfo(){
        twitchAccount.refreshInfo(TwitchAccount.display_name, new ImageIcon(Assets.makeRoundedCorner(TwitchAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    }

    public static void setBotAccountInfo(){
        botAccount.refreshInfo(TwitchAccount.display_name, new ImageIcon(Assets.makeRoundedCorner(TwitchBotAccount.profileImage).getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    }

}
