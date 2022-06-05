package com.alphalaneous.SettingsPanels;

import com.alphalaneous.ChatbotTab.DefaultCommands;
import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.RequestFunctions;
import com.alphalaneous.Settings;
import com.alphalaneous.Tabs.RequestsTab;

import javax.swing.*;

public class RequestsSettings {

    public static JPanel createPanel() {
        Settings.writeSettings("basicMode", "false");
        SettingsPage settingsPage = new SettingsPage("$REQUESTS_SETTINGS$");
        settingsPage.addCheckbox("$GD_MODE$", "$GD_MODE_DESC$", "gdMode", true, DefaultCommands::loadCommands);
        //settingsPage.addCheckbox("$BASIC_MODE$", "$BASIC_MODE_DESC$", "basicMode", RequestsSettings::setBasicMode);
        settingsPage.addCheckbox("$REMOVE_IF_OFFLINE$", "$REMOVE_IF_OFFLINE_DESC$", "removeIfOffline");
        settingsPage.addCheckbox("$FOLLOWERS_ONLY$ (Twitch)", "", "followers");
        settingsPage.addCheckbox("$SUBSCRIBERS_ONLY$ (Twitch)", "", "subscribers");
        settingsPage.addCheckbox("$STREAMER_BYPASS$", "$STREAMER_BYPASS_DESC$", "streamerBypass", true, null);
        settingsPage.addCheckbox("$MODS_BYPASS$", "$MODS_BYPASS_DESC$", "modsBypass");
        settingsPage.addCheckbox("$AUTOMATIC_SONG_DOWNLOADS$", "", "autoDL");
        settingsPage.addCheckbox("$ANNOUNCE_NOW_PLAYING$", "", "announceNP", true, null);
        settingsPage.addCheckbox("$DISABLE_NOW_PLAYING$", "", "disableNP");
        settingsPage.addCheckbox("$DISABLE_QUEUE_FULL$", "", "disableQF");
        settingsPage.addCheckbox("$DISABLE_CONFIRMATION$", "", "disableConfirm");
        settingsPage.addCheckbox("$WHISPER_CONFIRMATION$", "$WHISPER_CONFIRMATION_DESC$", "whisperConfirm");
        settingsPage.addCheckbox("$DISABLE_SHOW_POSITION$", "$DISABLE_SHOW_POSITION_DESC$", "disableShowPosition");
        settingsPage.addCheckbox("$DISABLE_REPEATED$", "$DISABLE_REPEATED_DESC$", "repeatedRequests");
        settingsPage.addCheckbox("$DISABLE_REPEATED_ALL$", "$DISABLE_REPEATED_ALL_DESC$", "repeatedRequestsAll");
        settingsPage.addCheckbox("$ALLOW_UPDATED_REPEATED$", "$ALLOW_UPDATED_REPEATED_DESC$", "updatedRepeated");
        settingsPage.addCheckedInput("$MAX_QUEUE_SIZE$", "", 1, true, false, false, "queueLimitEnabled", "queueLimit");
        settingsPage.addCheckedInput("$REQUEST_LIMIT_QUEUE$", "", 1, true, false, false, "userLimitEnabled", "userLimit");
        settingsPage.addCheckedInput("$STREAM_REQUEST_LIMIT$", "",1, true, false, false, "userLimitStreamEnabled", "userLimitStream");
        settingsPage.addInput("$QUEUE_COMMAND_LABEL$", "", 1, true, false, false, "queueLevelLength", "10", true);

        return settingsPage;
    }
}
