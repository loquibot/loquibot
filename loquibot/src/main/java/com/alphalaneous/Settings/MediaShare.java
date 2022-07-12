package com.alphalaneous.Settings;

import com.alphalaneous.Swing.Components.SettingsPage;

import javax.swing.*;

public class MediaShare {

    private static final SettingsPage settingsPage = new SettingsPage("$MEDIA_SHARE_SETTINGS$");


    public static JPanel createPanel() {
        settingsPage.addCheckbox("$MEDIA_SHARE_ENABLED$", "", "mediaShareEnabled");
        settingsPage.addCheckbox("$MEDIA_SHARE_AUTOMATICALLY_PLAY$", "$MEDIA_SHARE_AUTOMATICALLY_PLAY_DESC$", "mediaShareAutoPlay", true, null);
        settingsPage.addCheckbox("$MEDIA_SHARE_REMOVE_WHEN_DONE$", "", "mediaShareRemoveWhenDone");
        settingsPage.addCheckbox("$MEDIA_SHARE_DISABLE_REPEATED_SHARES$", "", "disableRepeatedShares");
        settingsPage.addCheckbox("$MEDIA_SHARE_DISABLE_REPEATED_SHARES_ALL_TIME$", "", "disableRepeatedSharesAllTime");
        settingsPage.addCheckedInput("$MEDIA_SHARE_MAX_DURATION$", "$MEDIA_SHARE_MAX_DURATION_DESC$", 1, true, false, false, "mediaShareMaxDurationEnabled", "mediaShareMaxDuration");


        return settingsPage;
    }
}
