package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Board;
import com.alphalaneous.Swing.Components.SettingsPage;

import javax.swing.*;

public class Messages {

    private static final SettingsPage settingsPage = new SettingsPage("$MESSAGE_SETTINGS$");


    public static JPanel createPanel() {
        settingsPage.addCheckbox("TestCheckBoxDescMultiLine", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas pellentesque diam ipsum, a tincidunt ipsum fermentum sit amet. ", "");
        settingsPage.addCheckbox("TestCheckBoxEmpty", "", "");
        settingsPage.addCheckbox("TestCheckBoxDescOneLine", "Aliquam porta pretium quam, vel accumsan ligula.", "");
        settingsPage.addInput("TestInputOneLine", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas pellentesque diam ipsum, a tincidunt ipsum fermentum sit amet.", 1, "testInputSetting");
        settingsPage.addInput("TestInputTwoLines", "Aliquam porta pretium quam, vel accumsan ligula.", 2, "testInputSetting");
        settingsPage.addInput("TestInputThreeLines", "", 3, "testInputSetting");
        settingsPage.addCheckedInput("TestCheckedInput", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas pellentesque diam ipsum, a tincidunt ipsum fermentum sit amet.", 3, "testCheckedSetting", "testCheckedInputSetting");
        settingsPage.addCheckedInput("TestCheckedInput", "Aliquam porta pretium quam, vel accumsan ligula.", 3, "testCheckedSetting", "testCheckedInputSetting");
        settingsPage.addCheckedInput("TestCheckedInput", "", 3, "testCheckedSetting", "testCheckedInputSetting");
        settingsPage.addRadioOption("TestRadioOption", "Lorem ipsum dolor sit amet.", new String[]{"testA", "testB"}, "testRadioSetting", "testA");
        settingsPage.addButton("TestButton", Board::bwomp);
        settingsPage.addButton("TestButton2", Board::bwomp);
        settingsPage.addConfigCheckbox("TestCheckBoxDescOneLine", "Aliquam porta pretium quam, vel accumsan ligula.", "");

        return settingsPage;
    }
}
