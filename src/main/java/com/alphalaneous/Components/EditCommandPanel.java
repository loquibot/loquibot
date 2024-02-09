package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interactive.Actions.ActionData;
import com.alphalaneous.Interactive.TwitchExclusive.Cheers.CheerData;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Interfaces.SaveAdapter;
import com.alphalaneous.Utilities.Language;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;
import com.alphalaneous.Enums.UserLevel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EditCommandPanel extends ThemeableJPanel {

    private final ThemeableJLabel titleLabel = new ThemeableJLabel();

    private final CustomData data;

    private final HashMap<String, String> values = new HashMap<>();
    private final ThemeableJPanel innerPanel = new ThemeableJPanel();
    private final GridBagConstraints gbc = new GridBagConstraints();

    private final ArrayList<ThemeableJLabel> titleLabels = new ArrayList<>();

    private final RoundedButton deleteButton;


    public EditCommandPanel(String titleText, CustomData data, SaveAdapter saveAdapter) {

        this.data = data;
        innerPanel.setOpaque(false);
        innerPanel.setBorder(new EmptyBorder(20,20,0,20));

        setOpaque(true);
        setBackground("background");
        setLayout(new GridBagLayout());

        innerPanel.setLayout(new GridBagLayout());

        titleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(25f));
        titleLabel.setForeground("foreground");
        titleLabel.setBorder(new EmptyBorder(0,0,10,0));


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy=0;
        gbc.weightx = 1;

        innerPanel.add(titleLabel, gbc);
        gbc.gridy++;

        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.fill = GridBagConstraints.HORIZONTAL;
        innerGbc.anchor = GridBagConstraints.NORTHWEST;

        innerGbc.weightx = 1;
        innerGbc.weighty= 1;

        innerGbc.gridx = 0;

        add(innerPanel, innerGbc);

        setTitleText(titleText);

        ThemeableJPanel bottomPanel = new ThemeableJPanel();
        bottomPanel.setOpaque(false);

        RoundedButton saveButton = new RoundedButton("$SAVE$");
        saveButton.addActionListener(e -> {

            if(values.get("name") == null || values.get("name").isBlank()){
                titleLabels.get(0).setForeground("error-red");
            }
            else{
                titleLabels.get(0).setForeground("foreground");
                saveAdapter.save(values, data, this);

            }
        });
        saveButton.setPreferredSize(new Dimension(100,40));

        RoundedButton cancelButton = new RoundedButton("$CANCEL$");
        cancelButton.addActionListener(e -> close());
        cancelButton.setPreferredSize(new Dimension(100,40));

        deleteButton = createDeleteButton(data);

        bottomPanel.setPreferredSize(new Dimension(100,60));

        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        if(data.getName() != null) bottomPanel.add(deleteButton);

        innerGbc.weighty= 0;
        innerGbc.gridy = 1;
        add(bottomPanel, innerGbc);

        setBackground("background");

    }

    public void removeDeleteButton(){
        JPanel p = (JPanel) deleteButton.getParent();
        deleteButton.getParent().remove(deleteButton);
        p.updateUI();
    }

    private RoundedButton createDeleteButton(CustomData data) {
        RoundedButton deleteButton = new RoundedButton("$DELETE$");
        deleteButton.setForeground("error-red", "error-red");
        AtomicInteger delClicks = new AtomicInteger();
        deleteButton.addActionListener(e -> {
            delClicks.getAndIncrement();
            if(delClicks.get() == 1){
                deleteButton.setText("$SURE$");
            }
            if(delClicks.get() == 2) {
                delete(data);
            }
        });
        deleteButton.setPreferredSize(new Dimension(100,40));
        return deleteButton;
    }

    public void delete(CustomData data){

        try {
            //call method on subclass dynamically instead of parent
            data.getClass().getMethod("deregister").invoke(data);
            close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setTitleLabelError(boolean b){

        if(b) titleLabels.get(0).setForeground("error-red");
        else titleLabels.get(0).setForeground("foreground");
    }

    public void setRangeLabelError(boolean b){

        if(b) titleLabels.get(2).setForeground("error-red");
        else titleLabels.get(2).setForeground("foreground");
    }

    public void addDisabledNameInput(String name, String desc){
        ThemeableJPanel cmdInput = createSettingTextInput(name, "name", desc, data.getName(),1, false);
        innerPanel.add(cmdInput, gbc);
        gbc.gridy++;
    }

    public void addNameInput(String name, String desc){
        ThemeableJPanel cmdInput = createSettingTextInput(name, "name", desc, data.getName(),1, true);
        innerPanel.add(cmdInput, gbc);
        gbc.gridy++;
    }

    public void addMessageInput(){
        ThemeableJPanel messageInput = createSettingTextInput("$MESSAGE_INPUT$", "message", "$MESSAGE_INPUT_DESC$", data.getMessage(),4, true);
        innerPanel.add(messageInput, gbc);
        gbc.gridy++;
    }

    public void addKeybindInput(){
        ThemeableJPanel keybindInput = createSettingKeybindInput("$KEYBIND_INPUT$", "keybind", "$KEYBIND_INPUT_DESC$", ((ActionData)data).getKeyBind(), ((ActionData)data).isUsesCtrl(), ((ActionData)data).isUsesAlt(), ((ActionData)data).isUsesShift());
        innerPanel.add(keybindInput, gbc);
        gbc.gridy++;
    }
    public void addAliasesInput(){
        ThemeableJPanel messageInput = createSettingTextInput("$ALIASES_INPUT$", "aliases", "$ALIASES_INPUT_DESC$", String.join(",", ((CommandData)data).getAliases()),1, true);
        innerPanel.add(messageInput, gbc);
        gbc.gridy++;
    }

    public void addRangeInput(){
        ThemeableJPanel messageInput = createSettingTextInput("$RANGES_INPUT$", "range", "$RANGES_INPUT_DESC$", ((CheerData)data).getRange() ,1, true);
        innerPanel.add(messageInput, gbc);
        gbc.gridy++;
    }

    public void addRunCommandInput(){
        ThemeableJPanel runCommandInput = createSettingTextInput("$COMMAND_INPUT$", "runCommand", "$COMMAND_INPUT_DESC$", ((TimerData)data).getRunCommand(),1, true);
        innerPanel.add(runCommandInput, gbc);
        gbc.gridy++;
    }

    public void addUserLevelsInput(){

        try {
            ThemeableJPanel userLevelInput = createSettingUserLevelInput((UserLevel) data.getClass().getMethod("getUserLevel").invoke(data));
            innerPanel.add(userLevelInput, gbc);
            gbc.gridy++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCooldownInput(){

        try {
            ThemeableJPanel sliderInput = createSettingSliderInput("$COOLDOWN_INPUT$", "$COOLDOWN_INPUT_DESC$", "cooldown", "$COOLDOWN_COUNTER_S$", "$COOLDOWN_COUNTER_P$", 0, 300, (Integer) data.getClass().getMethod("getCooldown").invoke(data));
            innerPanel.add(sliderInput, gbc);
            gbc.gridy++;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addMessagesInput(){

        ThemeableJPanel sliderInput = createSettingSliderInput("$MESSAGES_INPUT$", "$MESSAGES_INPUT_DESC$", "lines", "$MESSAGES_COUNTER_S$", "$MESSAGES_COUNTER_P$", 2, 100, ((TimerData)data).getLines());
        innerPanel.add(sliderInput, gbc);
        gbc.gridy++;
    }



    public void addIntervalInput(){

        int value = 15;

        if(data.getName() != null){
            value = ((TimerData)data).getInterval();
        }

        ThemeableJPanel sliderInput = createSettingSliderInput("$INTERVAL_INPUT$", "$INTERVAL_INPUT_DESC$", "interval", "$INTERVAL_COUNTER_S$", "$INTERVAL_COUNTER_P$", 1, 60, value);
        innerPanel.add(sliderInput, gbc);
        gbc.gridy++;
    }

    public ThemeableJPanel createSettingTextInput(String text, String identifier, String description, String value, int lineCount, boolean enabled){

        ThemeableJPanel panel = new ThemeableJPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        panel.setOpaque(false);

        panel.setLayout(new GridBagLayout());

        SpecialTextArea textArea = new SpecialTextArea(false, false, false);

        if(value != null) {
            values.put(identifier, value);
            textArea.setText(value);
        }

        textArea.clearUndo();
        textArea.getTextInput().setEditable(enabled);

        textArea.getTextInput().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                values.put(identifier, textArea.getText());
            }
        });

        ThemeableJLabel textLabel = new ThemeableJLabel(text);
        textLabel.setPreferredSize(new Dimension(100, 0));
        textLabel.setForeground("foreground");
        textLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
        titleLabels.add(textLabel);

        ThemeableJLabel descLabel = new ThemeableJLabel(description);
        descLabel.setPreferredSize(new Dimension(100, 5));
        descLabel.setForeground("foreground-darker");
        descLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(12f));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipady = 30;

        panel.add(textLabel, gbc);

        gbc.weightx = 0.9;
        gbc.gridx = 2;
        gbc.ipady = 5 + (25 * lineCount);

        panel.add(textArea, gbc);

        gbc.gridy = 1;
        gbc.ipady = 30;

        panel.setBorder(new EmptyBorder(2,0,6,0));
        panel.add(descLabel, gbc);

        return panel;
    }

    public ThemeableJPanel createSettingKeybindInput(String text, String identifier, String description, int keyValue, boolean isCtrlPressed, boolean isAltPressed, boolean isShiftPressed){

        ThemeableJPanel panel = new ThemeableJPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        panel.setOpaque(false);

        panel.setLayout(new GridBagLayout());

        SpecialTextArea textArea = new SpecialTextArea(false, false, false, false);

        if(keyValue != -1) {
            values.put(identifier, String.valueOf(keyValue));
            values.put(identifier + "Ctrl", String.valueOf(isCtrlPressed));
            values.put(identifier + "Alt", String.valueOf(isAltPressed));
            values.put(identifier + "Shift", String.valueOf(isShiftPressed));

            String keybindText = "";

            if(isCtrlPressed) keybindText += "Ctrl ";
            if(isAltPressed) keybindText += "Alt ";
            if(isShiftPressed) keybindText += "Shift ";
            keybindText += KeyEvent.getKeyText(keyValue);

            textArea.setText(keybindText);
        }

        textArea.clearUndo();
        textArea.setEditable(false);


        final boolean[] isCtrlPressedHere = {false};
        final boolean[] isAltPressedHere = {false};
        final boolean[] isShiftPressedHere = {false};

        textArea.getTextInput().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                boolean hasModifier = isCtrlPressedHere[0] || isAltPressedHere[0] || isShiftPressedHere[0];

                boolean cleared = false;

                if((e.getKeyCode() == 8 || e.getKeyCode() == 127) && !hasModifier) {
                    values.put(identifier, "-1");
                    values.put(identifier + "Ctrl", "false");
                    values.put(identifier + "Alt", "false");
                    values.put(identifier + "Shift", "false");

                    textArea.clearUndo();
                    textArea.setText("");
                    cleared = true;
                }

                if(e.getKeyCode() == 17) {
                    isCtrlPressedHere[0] = false;
                }
                if(e.getKeyCode() == 18) {
                    isAltPressedHere[0] = false;
                }
                if(e.getKeyCode() == 16) {
                    isShiftPressedHere[0] = false;
                }

                if(!cleared) {
                    if (!(e.getKeyCode() == 17 || e.getKeyCode() == 18 || e.getKeyCode() == 16)) {

                        values.put(identifier, String.valueOf(e.getKeyCode()));
                        values.put(identifier + "Ctrl", String.valueOf(isCtrlPressedHere[0]));
                        values.put(identifier + "Alt", String.valueOf(isAltPressedHere[0]));
                        values.put(identifier + "Shift", String.valueOf(isShiftPressedHere[0]));
                        textArea.clearUndo();

                        String keybindText = "";

                        if (isCtrlPressedHere[0]) keybindText += "Ctrl ";
                        if (isAltPressedHere[0]) keybindText += "Alt ";
                        if (isShiftPressedHere[0]) keybindText += "Shift ";

                        keybindText += KeyEvent.getKeyText(e.getKeyCode());

                        textArea.setText(keybindText);
                    }
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyReleased(e);

                if(e.getKeyCode() == 17) {
                    isCtrlPressedHere[0] = true;
                }
                if(e.getKeyCode() == 18) {
                    isAltPressedHere[0] = true;
                }
                if(e.getKeyCode() == 16) {
                    isShiftPressedHere[0] = true;
                }
            }
        });

        ThemeableJLabel textLabel = new ThemeableJLabel(text);
        textLabel.setPreferredSize(new Dimension(100, 0));
        textLabel.setForeground("foreground");
        textLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
        titleLabels.add(textLabel);

        ThemeableJLabel descLabel = new ThemeableJLabel(description);
        descLabel.setPreferredSize(new Dimension(100, 5));
        descLabel.setForeground("foreground-darker");
        descLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(12f));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipady = 30;

        panel.add(textLabel, gbc);

        gbc.weightx = 0.9;
        gbc.gridx = 2;
        gbc.ipady = 30;

        panel.add(textArea, gbc);

        gbc.gridy = 1;
        gbc.ipady = 30;

        panel.setBorder(new EmptyBorder(2,0,6,0));
        panel.add(descLabel, gbc);

        return panel;
    }


    public void showMenu(){
        DialogBox.showDialogBox(this, true);
    }

    public void close(){
        DialogBox.closeDialogBox();
    }

    public ThemeableJPanel createSettingUserLevelInput(UserLevel value){

        ThemeableJPanel panel = new ThemeableJPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        panel.setOpaque(false);

        panel.setLayout(new GridBagLayout());

        values.put("userLevel", String.valueOf(value.value));

        String levelText = value.toString();

        switch (levelText){
            case "Vip" :
                levelText = "VIP (Twitch)";
                break;
            case "Subscriber" :
                levelText = "Subscriber (Twitch)";
                break;
        }

        RoundedButton ulButton = new RoundedButton(levelText);

        ulButton.addActionListener(e -> UserLevelsMenu.show(Utilities.getRectInFrame(ulButton, Window.getFrame()), c -> {

            String levelText2 = c.toString();
            switch (levelText2){
                case "Vip" :
                    levelText2 = "VIP (Twitch)";
                    break;
                case "Subscriber" :
                    levelText2 = "Subscriber (Twitch)";
                    break;
            }

            ulButton.setText(levelText2);
            values.put("userlevel", String.valueOf(c.value));

        }));


        ThemeableJLabel textLabel = new ThemeableJLabel("$USER_LEVEL$");
        textLabel.setPreferredSize(new Dimension(100, 0));
        textLabel.setForeground("foreground");
        textLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));



        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.ipady = 30;

        panel.add(textLabel, gbc);

        gbc.weightx = 0.9;
        gbc.gridx = 2;
        gbc.ipady = 4;

        panel.add(ulButton, gbc);

        panel.setBorder(new EmptyBorder(2,0,6,0));

        return panel;

    }

    public ThemeableJPanel createSettingSliderInput(String name, String description, String identifier, String formattedCounter, String formattedCounterPlural, int min, int max, int value){

        ThemeableJPanel panel = new ThemeableJPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        panel.setOpaque(false);

        panel.setLayout(new GridBagLayout());

        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        ThemeableJLabel sliderValue = new ThemeableJLabel("");
        sliderValue.setHorizontalAlignment(SwingConstants.RIGHT);

        sliderValue.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

        if(value == 1){
            sliderValue.setText(String.format(Language.setLocale(formattedCounter), value));
        }
        else{
            sliderValue.setText(String.format(Language.setLocale(formattedCounterPlural), value));
        }

        values.put(identifier, String.valueOf(value));


        sliderValue.setPreferredSize(new Dimension(120, 30));
        sliderValue.setForeground("foreground");
        sliderValue.setBorder(new EmptyBorder(0,0,0,5));

        slider.setUI(new LightSliderUI(slider));
        slider.setOpaque(false);
        slider.setBorder(BorderFactory.createEmptyBorder());
        slider.addChangeListener(e -> {
            if (slider.getValue() == 1) {
                sliderValue.setText(String.format(Language.setLocale(formattedCounter), slider.getValue()));
            } else {
                sliderValue.setText(String.format(Language.setLocale(formattedCounterPlural), slider.getValue()));
            }

            values.put(identifier, String.valueOf(slider.getValue()));

        });
        slider.setValue(value);

        ThemeableJLabel textLabel = new ThemeableJLabel(name);
        textLabel.setPreferredSize(new Dimension(100, 0));
        textLabel.setForeground("foreground");
        textLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

        ThemeableJLabel descLabel = null;
        if(description != null) {
            descLabel = new ThemeableJLabel(description);
            descLabel.setPreferredSize(new Dimension(100, 5));
            descLabel.setForeground("foreground-darker");
            descLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(12f));
        }


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipady = 25;

        panel.add(textLabel, gbc);

        gbc.weightx = 0.9;
        gbc.gridx = 2;
        gbc.ipady = 5;

        panel.add(slider, gbc);

        gbc.gridy = 1;

        if(description != null) {

            gbc.ipady = 10;
            panel.add(descLabel, gbc);
        }

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.EAST;

        panel.add(sliderValue);
        panel.setBorder(new EmptyBorder(8,0,16,0));

        return panel;

    }


    public void setTitleText(String text){
        titleLabel.setText(text);

    }
}
