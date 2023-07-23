package com.alphalaneous.Swing.Components;

import com.alphalaneous.Interactive.Timers.LoadTimers;
import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Tabs.ChatbotPages.TimerSettings;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Swing.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

public class TimerConfigCheckbox extends JPanel {

    private static final ArrayList<TimerConfigCheckbox> commandConfigCheckboxes = new ArrayList<>();

    private final ThemedConfigCheckbox themedCheckbox;

    public TimerConfigCheckbox(TimerData timerData){

        themedCheckbox = new ThemedConfigCheckbox(timerData.getName(), StringEscapeUtils.escapeHtml4(timerData.getMessage()), () -> openTimerSettings(timerData), true, null);

        themedCheckbox.setChecked(timerData.isEnabled());
        setLayout(null);
        setOpaque(false);
        themedCheckbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for(TimerData data : TimerData.getRegisteredTimers()){
                    if(data.getName().equalsIgnoreCase(timerData.getName())){
                        data.setEnabled(themedCheckbox.getSelectedState());
                    }
                }
            }
        });

        setPreferredSize(new Dimension(450, 72));

        themedCheckbox.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        themedCheckbox.setBounds(0,0, 450, 70);
        setBackground(Defaults.COLOR3);
        themedCheckbox.refresh();
        add(themedCheckbox);
        commandConfigCheckboxes.add(this);
    }
    public void refreshUI(){
        setBackground(Defaults.COLOR3);
    }
    public void resize(int width){
        setPreferredSize(new Dimension(width-300, 72));
        themedCheckbox.setBounds(0,0, width-300, 70);
        themedCheckbox.resize(width-300);
    }

    public static void resizeAll(int width){
        for(TimerConfigCheckbox commandConfigCheckbox : commandConfigCheckboxes) commandConfigCheckbox.resize(width);
    }

    private static final JPanel timerSettingsPanel = new JPanel();

    public static void openTimerSettings(TimerData data){
        openTimerSettings(data, false);
    }
    public static void openTimerSettings(boolean newTimer){
        openTimerSettings(null, newTimer);
    }

    public static void openTimerSettings(TimerData data, boolean newTimer){

        String timerName = "";
        String message = "";
        String runCommand = "";
        int interval = 15;
        int lines = 2;

        if(!newTimer && data != null){
            timerName = data.getName();
            message = data.getMessage();
            interval = data.getInterval();
            lines = data.getLines();
            runCommand = data.getRunCommand();
        }

        timerSettingsPanel.removeAll();
        timerSettingsPanel.setLayout(null);
        timerSettingsPanel.setBounds(0,0,600,600);
        timerSettingsPanel.setBackground(Defaults.COLOR3);
        LangLabel langLabel;
        if(newTimer) langLabel = new LangLabel("$ADD_TIMER$");
        else langLabel = new LangLabel("$EDIT_TIMER$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        timerSettingsPanel.add(langLabel);

        TextInput timerNameInput = new TextInput("$TIMER_NAME_INPUT$", "$TIMER_NAME_INPUT_DESC$", timerName, 1);
        timerNameInput.setBounds(10,50,600, timerNameInput.getPreferredSize().height);
        timerSettingsPanel.add(timerNameInput);

        TextInput messageInput = new TextInput("$TIMER_MESSAGE_INPUT$", "$TIMER_MESSAGE_INPUT_DESC$", message, 3);
        messageInput.setBounds(10,120,600, messageInput.getPreferredSize().height);


        LangLabel intervalText = new LangLabel("$INTERVAL_LABEL$");
        intervalText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        intervalText.setOpaque(false);
        intervalText.setForeground(Defaults.FOREGROUND_A);
        intervalText.setPreferredSize(new Dimension(450, 30));


        JSlider intervalSlider = new JSlider(JSlider.HORIZONTAL, 1, 60, 15);
        LangLabel intervalValue = new LangLabel("");

        intervalValue.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        intervalValue.setTextLangFormat("$INTERVAL$", 15);
        intervalValue.setForeground(Defaults.FOREGROUND_A);

        intervalSlider.setUI(new LightSliderUI(intervalSlider));
        intervalSlider.setBackground(Defaults.COLOR3);
        intervalSlider.setBorder(BorderFactory.createEmptyBorder());
        intervalSlider.addChangeListener(e -> intervalValue.setTextLangFormat("$INTERVAL$", intervalSlider.getValue()));
        intervalSlider.setValue(interval);
        timerSettingsPanel.add(intervalText);
        timerSettingsPanel.add(intervalSlider);
        timerSettingsPanel.add(intervalValue);

        LangLabel intervalDescription = new LangLabel("");
        intervalDescription.setTextLang("<html><div WIDTH=450> " + "$INTERVAL_DESCRIPTION$" + " </div></html>");
        intervalDescription.setFont(Defaults.MAIN_FONT.deriveFont(13f));
        intervalDescription.setForeground(Defaults.FOREGROUND_B);
        intervalDescription.setOpaque(false);
        intervalDescription.setPreferredSize(new Dimension(450, intervalDescription.getPreferredSize().height));
        timerSettingsPanel.add(intervalDescription);

        LangLabel linesText = new LangLabel("$LINES_LABEL$");
        linesText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        linesText.setOpaque(false);
        linesText.setForeground(Defaults.FOREGROUND_A);
        linesText.setPreferredSize(new Dimension(450, 30));


        JSlider linesSlider = new JSlider(JSlider.HORIZONTAL, 2, 100, 2);
        LangLabel linesSliderValue = new LangLabel("");

        linesSliderValue.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        linesSliderValue.setTextLangFormat("$LINES_TEXT$", 2);
        linesSliderValue.setForeground(Defaults.FOREGROUND_A);

        linesSlider.setUI(new LightSliderUI(intervalSlider));
        linesSlider.setBackground(Defaults.COLOR3);
        linesSlider.setBorder(BorderFactory.createEmptyBorder());
        linesSlider.addChangeListener(e -> linesSliderValue.setTextLangFormat("$LINES_TEXT$", linesSlider.getValue()));
        linesSlider.setValue(lines);

        timerSettingsPanel.add(linesText);
        timerSettingsPanel.add(linesSlider);
        timerSettingsPanel.add(linesSliderValue);

        LangLabel linesDescription = new LangLabel("");
        linesDescription.setTextLang("<html><div WIDTH=450> " + "$LINES_DESCRIPTION$" + " </div></html>");
        linesDescription.setFont(Defaults.MAIN_FONT.deriveFont(13f));
        linesDescription.setForeground(Defaults.FOREGROUND_B);
        linesDescription.setOpaque(false);
        linesDescription.setPreferredSize(new Dimension(450, linesDescription.getPreferredSize().height));
        timerSettingsPanel.add(linesDescription);

        TextInput runCommandInput = new TextInput("$RUN_COMMAND_INPUT$", "$RUN_COMMAND_INPUT_DESC$", Objects.requireNonNullElse(runCommand, ""), 1);

        CurvedButton saveButton = new CurvedButton("$SAVE$");

        saveButton.setUI(Defaults.settingsButtonUI);
        saveButton.setBackground(Defaults.COLOR2);
        saveButton.setForeground(Defaults.FOREGROUND_A);
        saveButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        String finalTimerName = timerName;
        saveButton.addActionListener(e -> {

            if(checkIfNameExists(timerNameInput.getText(), finalTimerName)
                    || timerNameInput.getText().equalsIgnoreCase("")){
                timerNameInput.setErrorRed();
            }
            else {
                if(newTimer){
                    if(checkIfNameExists(timerNameInput.getText(), finalTimerName)
                            || timerNameInput.getText().equalsIgnoreCase("")){
                        timerNameInput.setErrorRed();
                    }
                    else {
                        TimerData data1 = new TimerData(timerNameInput.getText().trim(), messageInput.getText().trim());
                        data1.setRunCommand(runCommandInput.getText());
                        data1.setInterval(intervalSlider.getValue());
                        data1.setLines(linesSlider.getValue());
                        data1.setRunCommand(runCommandInput.getText());
                        data1.registerTimer();
                        LoadTimers.reloadCustomTimers();
                        TimerData.saveCustomTimers();
                        DialogBox.closeDialogBox();
                    }
                }
                else {
                    for (TimerData existingData : TimerData.getRegisteredTimers()) {
                        if (existingData.getName().equalsIgnoreCase(finalTimerName)) {

                            existingData.setName(timerNameInput.getText().trim());
                            existingData.setMessage(messageInput.getText().trim());
                            existingData.setRunCommand(runCommandInput.getText());
                            existingData.setInterval(intervalSlider.getValue());
                            LoadTimers.reloadCustomTimers();
                            TimerData.saveCustomTimers();
                            break;
                        }
                    }
                    DialogBox.closeDialogBox();
                }
            }
        });

        CurvedButton helpButton = new CurvedButton("\uF0A3");

        helpButton.setUI(Defaults.settingsButtonUI);
        helpButton.setBackground(Defaults.COLOR2);
        helpButton.setForeground(Defaults.FOREGROUND_A);
        helpButton.setFont(Defaults.SYMBOLS.deriveFont(14f));
        helpButton.setBorder(BorderFactory.createEmptyBorder());

        helpButton.addActionListener(e -> new BrowserWindow("https://loquibot.com/Docs.html"));


        CurvedButton cancelButton = new CurvedButton("$CANCEL$");

        cancelButton.setUI(Defaults.settingsButtonUI);
        cancelButton.setBackground(Defaults.COLOR2);
        cancelButton.setForeground(Defaults.FOREGROUND_A);
        cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        cancelButton.setBorder(BorderFactory.createEmptyBorder());

        cancelButton.addActionListener(e -> DialogBox.closeDialogBox());

        CurvedButton deleteButton = new CurvedButton("\uF0CE");

        deleteButton.setUI(Defaults.settingsButtonUI);
        deleteButton.setBackground(Defaults.COLOR2);
        deleteButton.setForeground(Color.RED);
        deleteButton.setFont(Defaults.SYMBOLS.deriveFont(16f));
        deleteButton.setBorder(BorderFactory.createEmptyBorder());

        final boolean[] firstClick = {false};

        deleteButton.addActionListener(e -> {
            deleteButton.setBounds(465,535,120,30);

            deleteButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            deleteButton.setText("Are you sure?");
            if(firstClick[0]) {
                for(TimerData data1 : TimerData.getRegisteredTimers()){
                    if(data1.getName().equalsIgnoreCase(finalTimerName)){
                        data1.deRegisterTimer();
                        LoadTimers.reloadCustomTimers();
                        TimerSettings.loadTimers();
                        break;
                    }
                }
                DialogBox.closeDialogBox();
            }
            firstClick[0] = true;

        });


        timerSettingsPanel.add(saveButton);
        timerSettingsPanel.add(cancelButton);

        timerSettingsPanel.setBounds(0,0,600,580);

        intervalText.setBounds(10,250, 450, 30);
        intervalSlider.setBounds(108, 250, 480, 30);
        intervalValue.setBounds(110, 275, 475, intervalValue.getPreferredSize().height + 5);
        intervalDescription.setBounds(110, 305, 450, intervalDescription.getPreferredSize().height);

        linesText.setBounds(10,350, 450, 30);
        linesSlider.setBounds(108, 350, 480, 30);
        linesSliderValue.setBounds(110, 375, 475, linesSliderValue.getPreferredSize().height + 5);
        linesDescription.setBounds(110, 395, 450, intervalDescription.getPreferredSize().height);

        saveButton.setBounds(195,530,100,40);
        cancelButton.setBounds(305,530,100,40);
        helpButton.setBounds(15,535,30,30);
        deleteButton.setBounds(555,535,30,30);

        runCommandInput.setBounds(10,440,600, runCommandInput.getPreferredSize().height);
        timerSettingsPanel.add(messageInput);
        timerSettingsPanel.add(runCommandInput);
        timerSettingsPanel.add(helpButton);
        if(!newTimer) timerSettingsPanel.add(deleteButton);

        DialogBox.showDialogBox(timerSettingsPanel, true);
    }

    private static boolean checkIfNameExists(String newName, String originalName){
        if(newName.equalsIgnoreCase(originalName)){
            return false;
        }
        boolean exists = false;
        for(TimerData existingData : TimerData.getRegisteredTimers()) {
            if (existingData.getName().equalsIgnoreCase(newName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }


    public static class TextInput extends JPanel {
        private final FancyTextArea textArea;
        private final LangLabel titleText = new LangLabel("");
        TextInput(String text, String description, String defaultInput, int lines){
            int height = 0;
            for(int i = 0; i < lines; i++){
                height += 32;
            }



            LangLabel descriptionText = new LangLabel("");
            descriptionText.setTextLang("<html><div WIDTH=450> " + description + " </div></html>");
            descriptionText.setFont(Defaults.MAIN_FONT.deriveFont(13f));
            descriptionText.setForeground(Defaults.FOREGROUND_B);
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));
            descriptionText.setBounds(100, height+4, 450, descriptionText.getPreferredSize().height);
            add(descriptionText);


            titleText.setTextLang(text);
            titleText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            titleText.setOpaque(false);
            titleText.setForeground(Defaults.FOREGROUND_A);
            titleText.setPreferredSize(new Dimension(450, 30));
            titleText.setBounds(0,0, 450, 30);

            textArea = new FancyTextArea(false, false);
            textArea.setText(defaultInput);
            textArea.clearUndo();
            textArea.setBounds(100,0,475, height);
            setPreferredSize(new Dimension(600, height+descriptionText.getPreferredSize().height+20));

            setLayout(null);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            setBackground(Defaults.COLOR3);
            add(textArea);
            add(titleText);
        }
        public String getText(){
            return textArea.getText();
        }
        public void setEditable(boolean editable){
            textArea.setEditable(editable);
        }
        public void setErrorRed(){
            titleText.setForeground(Color.RED);
        }
    }
}
