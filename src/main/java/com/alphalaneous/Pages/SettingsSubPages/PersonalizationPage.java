package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.SidebarSwitcher;
import com.alphalaneous.Pages.SettingsPage;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.Theme;
import com.alphalaneous.Window;

import java.util.HashMap;

public class PersonalizationPage {

    static SettingsSubPage page = new SettingsSubPage("$PERSONALIZATION_TITLE$");

    @OnLoad(order = 10006)
    public static void init(){

        HashMap<String, String> themes = new HashMap<>();
        themes.put("$LIGHT_MODE$", "light");
        themes.put("$DARK_MODE$", "dark");

        page.addRadioOption("$THEME_TEXT$", "", themes, "theme", "dark", PersonalizationPage::setTheme);

        page.addCheckbox("$ALWAYS_ON_TOP$", "$ON_TOP_DESCRIPTION$", "onTop", PersonalizationPage::setOnTop);
        page.addCheckbox("$DISABLE_FOCUS$", "$DISABLE_FOCUS_DESCRIPTION$", "disableFocus", PersonalizationPage::setFocusable);

        SettingsPage.addPage("$PERSONALIZATION_TITLE$", "\uF1B9", page, null);
    }

    public static void setTheme(){
        String theme = SettingsHandler.getSettings("theme").asString();
        if (theme.equalsIgnoreCase("light")) {
            Theme.loadTheme("light-theme");
            SidebarSwitcher.setTheme(true);
        }
        else {
            Theme.loadTheme("dark-theme");
            SidebarSwitcher.setTheme(false);
        }
    }


    public static void setOnTop(){
        Window.getFrame().setAlwaysOnTop(SettingsHandler.getSettings("onTop").asBoolean());
    }
    public static void setFocusable(){
        Window.getFrame().setFocusable(!SettingsHandler.getSettings("disableFocus").asBoolean());
    }


}
