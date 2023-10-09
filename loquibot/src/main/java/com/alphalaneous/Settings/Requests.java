package com.alphalaneous.Settings;

import com.alphalaneous.Tabs.ChatbotPages.DefaultCommands;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Windows.DialogBox;

import javax.swing.*;

public class Requests {

    public static JPanel createPanel() {
        SettingsHandler.writeSettings("basicMode", "false");
        SettingsPage settingsPage = new SettingsPage("$REQUESTS_SETTINGS$");
        settingsPage.addCheckbox("$GD_MODE$", "$GD_MODE_DESC$", "gdMode", true, DefaultCommands::loadCommands);
        settingsPage.addCheckbox("$AUTO_DELETE_REPEAT_SEND$", "$AUTO_DELETE_REPEAT_SEND_DESC$", "autoDeleteRepeatSend");
        settingsPage.addCheckbox("$WAIT_A_SECOND$", "$WAIT_A_SECOND_DESC$", "waitForRequests");
        //settingsPage.addCheckbox("$FOLLOWERS_ONLY$ (Twitch)", "", "followers");
        settingsPage.addCheckbox("$SUBSCRIBERS_ONLY$ (Twitch)", "", "subscribers");
        settingsPage.addCheckbox("$CHANNEL_POINTS_ONLY$ (Twitch)", "", "requestsChannelPointsOnly");
        settingsPage.addCheckbox("$STREAMER_BYPASS$", "$STREAMER_BYPASS_DESC$", "streamerBypass", true, null);
        settingsPage.addCheckbox("$MODS_BYPASS$", "$MODS_BYPASS_DESC$", "modsBypass");
        settingsPage.addCheckbox("$AUTOMATIC_SONG_DOWNLOADS$", "", "autoDL");
        settingsPage.addCheckbox("$ANNOUNCE_NOW_PLAYING$", "", "announceNP", true, null);
        settingsPage.addCheckbox("$DISABLE_NOW_PLAYING$", "", "disableNP");
        settingsPage.addCheckbox("$DISABLE_IN_QUEUE$", "", "disableInQueue");
        settingsPage.addCheckbox("$DISABLE_QUEUE_FULL$", "", "disableQF");
        settingsPage.addCheckbox("$DISABLE_CONFIRMATION$", "", "disableConfirm");
        settingsPage.addCheckbox("$DISABLE_SHOW_POSITION$", "$DISABLE_SHOW_POSITION_DESC$", "disableShowPosition");
        settingsPage.addCheckbox("$DISABLE_REPEATED$", "$DISABLE_REPEATED_DESC$", "repeatedRequests");
        settingsPage.addCheckbox("$DISABLE_REPEATED_ALL$", "$DISABLE_REPEATED_ALL_DESC$", "repeatedRequestsAll");
        settingsPage.addCheckbox("$SHOW_REPEATED_ALL$", "$SHOW_REPEATED_ALL_DESC$", "showRepeatedRequestsAll");
        settingsPage.addCheckbox("$ALLOW_UPDATED_REPEATED$", "$ALLOW_UPDATED_REPEATED_DESC$", "updatedRepeated");
        settingsPage.addCheckedInput("$MAX_QUEUE_SIZE$", "", 1, true, false, false, "queueLimitEnabled", "queueLimit");
        settingsPage.addCheckedInput("$MAX_LEVELS_SIZE$", "$MAX_LEVELS_SIZE_DESC$", 1, true, false, false, "levelLimitEnabled", "levelLimit");
        settingsPage.addCheckedInput("$SEQUENTIAL_LEVELS_SIZE$", "$SEQUENTIAL_LEVELS_SIZE_DESC$", 1, true, false, false, "sequentialLevelLimitEnabled", "sequentialLevelLimit");
        settingsPage.addCheckedInput("$REQUEST_COOLDOWN$", "$REQUEST_COOLDOWN_DESC$", 1, true, false, false, "requestCooldownEnabled", "requestCooldown");
        settingsPage.addButton("$REQUEST_COOLDOWN_RESET$", () -> {
            new Thread(() -> {
                String response = DialogBox.showDialogBox("$REQUEST_COOLDOWN_RESET_TITLE$", "$REQUEST_COOLDOWN_RESET_DESC$", "", new String[]{"Reset", "Cancel"});

                if(response.equalsIgnoreCase("Reset")){
                    com.alphalaneous.Services.GeometryDash.Requests.RequestCooldown.resetCooldowns();
                }


            }).start();
        });

        settingsPage.addCheckedInput("$REQUEST_LIMIT_QUEUE$", "$REQUEST_LIMIT_QUEUE_DESC$", 1, true, false, false, "userLimitEnabled", "userLimit");
        settingsPage.addCheckedInput("$STREAM_REQUEST_LIMIT$", "$STREAM_REQUEST_LIMIT_DESC$",1, true, false, false, "userLimitStreamEnabled", "userLimitStream");
        settingsPage.addInput("$QUEUE_COMMAND_LABEL$", "", 1, true, false, false, "queueLevelLength", "10", true, false);

        return settingsPage;
    }
}
