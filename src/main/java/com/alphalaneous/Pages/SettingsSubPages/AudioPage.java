package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class AudioPage {

    static SettingsSubPage page = new SettingsSubPage();

    @OnLoad(order = 6)
    public static void init(){

        SettingsPage.addPage("Audio", "\uF0E2", page, null);

    }
}
