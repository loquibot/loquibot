package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class DefaultCommandsPage {

    static SettingsSubPage page = new SettingsSubPage("$DEFAULT_COMMANDS_TITLE$");

    @OnLoad(order = 10006)
    public static void init(){

        SettingsPage.addPage("$DEFAULT_COMMANDS_TITLE$", "\uF162", page, null);

    }
}
