package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class PersonalizationPage {

    static SettingsSubPage page = new SettingsSubPage();

    @OnLoad(order = 5)
    public static void init(){

        SettingsPage.addPage("Personalization", "\uF1B9", page, null);
    }
}
