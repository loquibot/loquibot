package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class ChatSettingsPage {

    static SettingsSubPage page = new SettingsSubPage();

    @OnLoad(order = 4)
    public static void init(){

        SettingsPage.addPage("Chat", "\uF162", page, null);

    }
}
