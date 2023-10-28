package com.alphalaneous.Pages.CommandPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Pages.ChatPage;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TimersPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 9)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        ChatPage.addPage("Timers", page, TimersPage::load, TimersPage::showEditMenu);
    }

    public static void showEditMenu(){
        showEditMenu(new TimerData(null));
    }

    public static void showEditMenu(TimerData dataParam){

        String title = "Edit Timer";

        if(dataParam.getName() == null) title = "Add Timer";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            TimerData data;
            if(d.getName() == null){
                data = new TimerData(kv.get("name"));
                data.register();
            }
            else{
                data = (TimerData) d;
            }

            Utilities.ifNotNull(kv.get("message"), o -> data.setMessage((String)o));
            Utilities.ifNotNull(kv.get("interval"), o -> data.setInterval(Integer.parseInt((String)o)));
            Utilities.ifNotNull(kv.get("lines"), o -> data.setLines(Integer.parseInt((String)o)));
            Utilities.ifNotNull(kv.get("runCommand"), o -> data.setRunCommand((String)o));

            data.setName(kv.get("name"));

            //todo check name collisions

            data.save(true);
            e.close();
        });
        editCommandPanel.addNameInput("Timer Name:", "The name of the timer to be run.");
        editCommandPanel.addMessageInput();
        editCommandPanel.addIntervalInput();
        editCommandPanel.addMessagesInput();
        editCommandPanel.addRunCommandInput();
        editCommandPanel.setBounds(0,0,800,570);

        editCommandPanel.showMenu();
    }

    public static void load(){

        buttonPanel.removeAll();
        for(TimerData timerData : TimerData.getRegisteredTimers()){

            buttonPanel.add(new ConfigCheckbox(timerData, () -> showEditMenu(timerData), false), gbc);
            buttonPanel.add(Box.createVerticalStrut(5), gbc);
        }
        buttonPanel.updateUI();
    }
}
