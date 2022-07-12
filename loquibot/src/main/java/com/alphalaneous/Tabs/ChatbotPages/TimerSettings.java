package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Interactive.Timers.LoadTimers;
import com.alphalaneous.Swing.Components.ListView;
import com.alphalaneous.Swing.Components.TimerConfigCheckbox;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.util.ArrayList;

public class TimerSettings {

    private static final ListView listView = new ListView("$TIMERS_SETTINGS$");

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", () -> TimerConfigCheckbox.openTimerSettings(true));
        return listView;
    }

    public static void loadTimers(){
        listView.clearElements();
        ArrayList<TimerData> timers = new ArrayList<>(LoadTimers.getCustomTimers());

        ArrayList<TimerData> alphabetizedTimers = Utilities.alphabetizeTimerData(timers);

        for(TimerData timerData : alphabetizedTimers){
            TimerConfigCheckbox timerConfigCheckbox = new TimerConfigCheckbox(timerData);
            timerConfigCheckbox.resize(Window.getWindow().getWidth());
            listView.addElement(timerConfigCheckbox);
        }
        listView.updateUI();
    }
}
