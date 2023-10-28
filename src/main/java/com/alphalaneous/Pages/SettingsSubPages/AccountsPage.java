package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class AccountsPage {

    static SettingsSubPage page = new SettingsSubPage();

    @OnLoad(order = 3)
    public static void init(){

        SettingsPage.addPage("Accounts", "\uF161", page, null);

    }
}
