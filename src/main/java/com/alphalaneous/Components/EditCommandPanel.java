package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.DialogBox;
import com.alphalaneous.Fonts;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Interfaces.SaveAdapter;
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

        RoundedButton saveButton = new RoundedButton("Save");
        saveButton.addActionListener(e -> {

            Logging.getLogger().info(values.get("name"));

            if(values.get("name") == null || values.get("name").isBlank()){
                titleLabels.get(0).setForeground("error-red");
            }
            else{
                titleLabels.get(0).setForeground("foreground");
                saveAdapter.save(values, data, this);

            }
        });
        saveButton.setPreferredSize(new Dimension(100,40));

        RoundedButton cancelButton = new RoundedButton("Cancel");
        cancelButton.addActionListener(e -> close());
        cancelButton.setPreferredSize(new Dimension(100,40));

        RoundedButton deleteButton = createDeleteButton(data);

        bottomPanel.setPreferredSize(new Dimension(100,60));

        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        if(data.getName() != null) bottomPanel.add(deleteButton);

        innerGbc.weighty= 0;
        innerGbc.gridy = 1;
        add(bottomPanel, innerGbc);

        setBackground("background");

    }

    private RoundedButton createDeleteButton(CustomData data) {
        RoundedButton deleteButton = new RoundedButton("Delete");
        deleteButton.setForeground("error-red", "error-red");
        AtomicInteger delClicks = new AtomicInteger();
        deleteButton.addActionListener(e -> {
            delClicks.getAndIncrement();
            if(delClicks.get() == 1){
                deleteButton.setText("Sure?");
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

    public void addNameInput(String name, String desc){
        ThemeableJPanel cmdInput = createSettingTextInput(name, "name", desc, data.getName(),1);
        innerPanel.add(cmdInput, gbc);
        gbc.gridy++;
    }

    public void addMessageInput(){
        ThemeableJPanel messageInput = createSettingTextInput("Message:", "message", "The response sent in chat when this action runs.", data.getMessage(),4);
        innerPanel.add(messageInput, gbc);
        gbc.gridy++;
    }

    public void addAliasesInput(){
        ThemeableJPanel messageInput = createSettingTextInput("Aliases:", "aliases", "Other command names that will activate this command (comma separated).", String.join(",", ((CommandData)data).getAliases()),1);
        innerPanel.add(messageInput, gbc);
        gbc.gridy++;
    }

    public void addRunCommandInput(){
        ThemeableJPanel runCommandInput = createSettingTextInput("Command:", "runCommand", "A command that will activate when this runs, overrides message.", ((TimerData)data).getRunCommand(),1);
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
            ThemeableJPanel sliderInput = createSettingSliderInput("Cooldown:", "The waiting time before the action can be ran again.", "cooldown", "%s Second", "%s Seconds", 0, 300, (Integer) data.getClass().getMethod("getCooldown").invoke(data));
            innerPanel.add(sliderInput, gbc);
            gbc.gridy++;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addMessagesInput(){

        ThemeableJPanel sliderInput = createSettingSliderInput("Messages:", "Amount of messages within 5 minutes for a timer to execute.", "lines", "%s message", "%s messages", 2, 100, ((TimerData)data).getLines());
        innerPanel.add(sliderInput, gbc);
        gbc.gridy++;
    }

    public void addIntervalInput(){

        int value = 15;

        if(data.getName() != null){
            value = ((TimerData)data).getInterval();
        }

        ThemeableJPanel sliderInput = createSettingSliderInput("Interval:", "Repeat Interval (Exact per hour, example: 15 minutes is 2:00, 2:15, 2:30, 2:45)", "interval", "%s minute", "%s minutes", 1, 60, value);
        innerPanel.add(sliderInput, gbc);
        gbc.gridy++;
    }

    public ThemeableJPanel createSettingTextInput(String text, String identifier, String description, String value, int lineCount){

        ThemeableJPanel panel = new ThemeableJPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        panel.setOpaque(false);

        panel.setLayout(new GridBagLayout());

        SpecialTextArea textArea = new SpecialTextArea(false, false, false);

        if(value != null) {
            values.put(identifier, value);
            textArea.setText(value);
        }

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
        if(levelText.equals("Vip")) levelText = "VIP";

        //todo make User Level button work
        RoundedButton ulButton = new RoundedButton(levelText);

        ulButton.addActionListener(e -> UserLevelsMenu.show(Utilities.getRectInFrame(ulButton, Window.getFrame()), c -> {

            String levelText2 = c.toString();
            if (levelText2.equals("Vip")) levelText2 = "VIP";

            ulButton.setText(levelText2);
            values.put("userlevel", String.valueOf(c.value));

        }));


        ThemeableJLabel textLabel = new ThemeableJLabel("User Level:");
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
            sliderValue.setText(String.format(formattedCounter, value));
        }
        else{
            sliderValue.setText(String.format(formattedCounterPlural, value));
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
                sliderValue.setText(String.format(formattedCounter, slider.getValue()));
            } else {
                sliderValue.setText(String.format(formattedCounterPlural, slider.getValue()));
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
