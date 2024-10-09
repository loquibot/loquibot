package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Pages.SettingsPage;

import java.util.HashMap;

public class TestingPage {

    static SettingsSubPage page = new SettingsSubPage("Testing");

    @OnLoad(order = 10003, debug = true)
    public static void init(){

        page.addButton("Test button", ()-> {});
        page.addInput("Test input", "Test description", 1, "testInputLines1", "default input");
        page.addInput("Test input 2", "Test description\nMultiline", 2, "testInputLines2", "default input 2");
        page.addShortInput("Test short input", "Test short description", "testShortInputLines", "test");
        page.addCheckbox("Test Checkbox", "Test checkbox description", "testCheckboxButton");
        page.addCheckbox("Test Checkbox 2", "Test checkbox\ndescription multiline", "testCheckboxButton2");
        page.addCheckedInput("Test Checked Input", "Test Checked Input Description", 1, "testCheckedInputEnabled", "testCheckedInput");
        page.addCheckedInput("Test Checked Input 2", "Test Checked Input Description 2\nMultiline", 2, "testCheckedInputEnabled2", "testCheckedInput2");

        HashMap<String, String> options = new HashMap<>();
        options.put("Test1", "testOption1");
        options.put("Test2", "testOption2");

        page.addRadioOption("Test Radio Input", "Test Radio Input Description", options, "testRadioInput", "Test1");

        page.addSlider("Test Slider", "Test Slider Desc", "testSliderSetting", "%d amounts", "%d amount", 0, 120, 100, () -> System.out.println("Action ran"));

        SettingsPage.addPage("Testing", "\uF161", page, null);

    }
}
