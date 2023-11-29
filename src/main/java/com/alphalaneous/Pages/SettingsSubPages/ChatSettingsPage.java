package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class ChatSettingsPage {

    static SettingsSubPage page = new SettingsSubPage("$CHAT_TITLE$");

    @OnLoad(order = 10005)
    public static void init(){

        SettingsPage.addPage("$CHAT_TITLE$", "\uF162", page, null);

    }
}
