package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class ChatSettingsPage {

    static SettingsSubPage page = new SettingsSubPage("$CHAT_TITLE$");

    @OnLoad(order = 10005)
    public static void init(){

        page.addCheckbox("$DISABLE_MESSAGES$", "$DISABLE_MESSAGES_DESCRIPTION$", "disableMessages");
        page.addShortInput("$DEFAULT_COMMAND_PREFIX$", "$DEFAULT_COMMAND_PREFIX_DESCRIPTION$", "defaultCommandPrefix", "!");

        SettingsPage.addPage("$CHAT_TITLE$", "\uF162", page, null);

    }
}
