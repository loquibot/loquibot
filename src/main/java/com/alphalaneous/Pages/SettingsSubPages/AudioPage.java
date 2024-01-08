package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

public class AudioPage {

    static SettingsSubPage page = new SettingsSubPage("$AUDIO_TITLE$");

    @OnLoad(order = 10008)
    public static void init(){

        SettingsPage.addPage("$AUDIO_TITLE$", "\uF0E2", page, null);

        page.addSlider("$SOUND_VOLUME$", "", "soundVolume", "$VOLUME_TEXT$", "$VOLUME_TEXT$", 0, 120, 100, () -> {});
        page.addSlider("$TTS_VOLUME$", "", "ttsVolume", "$VOLUME_TEXT$", "$VOLUME_TEXT$", 0, 120, 100, () -> {});
        page.addCheckbox("$PLAY_SOUNDS_WHILE_HIDDEN$", "$PLAY_SOUNDS_WHILE_HIDDEN_DESCRIPTION$", "playSoundsWhileHidden");


    }
}
