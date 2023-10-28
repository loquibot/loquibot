package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.SettingsHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SettingsComponent extends ThemeableJPanel {

    private final JPanel borderPanel = new JPanel(new BorderLayout());
    private final JPanel settingsPane = new JPanel();
    private final SmoothScrollPane scrollPane = new SmoothScrollPane(borderPanel);
    private final JPanel titlePane = new JPanel();
    private final SettingsTitle settingsTitle;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JPanel bottomPanel = new JPanel();

    public SettingsComponent(String title){
        settingsPane.setLayout(new GridBagLayout());
        setLayout(null);
        setOpaque(false);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        scrollPane.setBounds(0,0,100,100);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        settingsTitle = new SettingsTitle(title);
        settingsTitle.setOpaque(false);
        settingsTitle.setBounds(0,0,Integer.MAX_VALUE, 80);

        titlePane.setPreferredSize(new Dimension(Integer.MAX_VALUE,80));
        titlePane.setOpaque(false);
        titlePane.setLayout(null);
        titlePane.add(settingsTitle);

        bottomPanel.setOpaque(false);
        bottomPanel.setBackground(new Color(0,0,0,0));
        bottomPanel.setPreferredSize(new Dimension(0, 20));

        borderPanel.add(settingsPane, BorderLayout.NORTH);
        borderPanel.setOpaque(false);

        settingsPane.setOpaque(false);
        settingsPane.add(titlePane, gbc);
        settingsPane.add(bottomPanel);

        add(scrollPane);
    }

    public void addComponent(SettingsComponent component){
        settingsPane.add(component, gbc, settingsPane.getComponentCount()-1);
    }

    public void addButton(String text, Function function){
        settingsPane.add(new Button(text, function), gbc, settingsPane.getComponentCount()-1);
    }

    public void addRadioOption(String text, String description, String[] options, String setting, String defaultOption, Function function){
        settingsPane.add(new RadioOption(text, description, options, setting, defaultOption, function), gbc, settingsPane.getComponentCount()-1);
    }
    public void addRadioOption(String text, String description, String[] options, String setting, String defaultOption){
        addRadioOption(text, description, options, setting, defaultOption, null);
    }

    /*public void addCheckbox(String text, String description, String setting, boolean defaultOption, Function function){
        settingsPane.add(new CheckBox(text, description, setting, defaultOption, function), gbc, settingsPane.getComponentCount()-1);
    }
    public void addCheckbox(String text, String description, String setting, Function function){
        addCheckbox(text, description, setting, false, function);
    }
    public void addCheckbox(String text, String description, String setting){
        addCheckbox(text, description, setting, false, null);
    }*/

    /*public void addConfigCheckbox(String text, String description, String setting, boolean defaultOption, Function function){
        settingsPane.add(new CheckBoxConfig(text, description, setting, defaultOption, function), gbc, settingsPane.getComponentCount()-1);
    }
    public void addConfigCheckbox(String text, String description, String setting, Function function){
        addConfigCheckbox(text, description, setting, false, function);
    }
    public void addConfigCheckbox(String text, String description, String setting){
        addConfigCheckbox(text, description, setting, false, null);
    }*/


    public void addInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String setting, String defaultInput, boolean editable, boolean noNewLine){
        settingsPane.add(new TextInput(text, description, lines, intFilter, allowNegative, allowDecimal, setting, defaultInput, editable, noNewLine), gbc,settingsPane.getComponentCount()-1);
    }

    public void addInput(String text, String description,int lines, String setting, boolean noNewLine, String defaultInput){
        addInput(text, description, lines, setting, defaultInput, true, noNewLine);
    }
    public void addInput(String text, String description,int lines, String setting, String defaultInput){
        addInput(text, description, lines, setting, defaultInput, true, false);
    }
    public void addInput(String text, String description,int lines, String setting, String defaultInput, boolean editable, boolean noNewLine){
        addInput(text, description, lines, false, true, true, setting, defaultInput, editable, noNewLine);
    }
    public void addInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String setting){
        addInput(text, description, lines, intFilter, allowNegative, allowDecimal, setting, "", true, false);
    }
    public void addInput(String text, String description, int lines, String setting){
        addInput(text, description, lines, false, true, true, setting, "", true, false);
    }

    /*public void addCheckedInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String checkSetting, String inputSetting, boolean defaultOption, String defaultInput){
        settingsPane.add(new CheckedTextInput(text, description, lines, intFilter, allowNegative, allowDecimal, checkSetting, inputSetting, defaultOption, defaultInput), gbc,settingsPane.getComponentCount()-1);
    }
    public void addCheckedInput(String text, String description, int lines,  String checkSetting, String inputSetting, boolean defaultOption, String defaultInput){
        addCheckedInput(text, description, lines, false, true, true, checkSetting, inputSetting, defaultOption, defaultInput);
    }
    public void addCheckedInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String checkSetting, String inputSetting){
        addCheckedInput(text, description, lines, intFilter, allowNegative, allowDecimal, checkSetting, inputSetting, false, "");
    }
    public void addCheckedInput(String text, String description, int lines,  String checkSetting, String inputSetting){
        addCheckedInput(text, description, lines, false, true, true, checkSetting, inputSetting, false, "");
    }*/

    public void removeButton(String text){
        for(Component component : settingsPane.getComponents()){
            if(component instanceof Button){
                if (((Button) component).getText().equalsIgnoreCase(text)){
                    ((Button) component).remove();
                }
            }
        }
    }

    public void moveComponent(int pos, int newPos){
        settingsPane.add(settingsPane.getComponent(pos), gbc, newPos);

    }

    private void resizePage(int width, int height){
        int subtractWidth = 280;
        int subtractHeight = 39;

        scrollPane.setBounds(0,0,width - subtractWidth,height - subtractHeight);
        settingsTitle.setPreferredSize(new Dimension(width - subtractWidth,80));
        titlePane.setPreferredSize(new Dimension(width - subtractWidth,80));
        bottomPanel.setPreferredSize(new Dimension(width - subtractWidth, 20));
        for(Component component : settingsPane.getComponents()){
            if(component instanceof OptionComponent) ((OptionComponent) component).resizeComponent(new Dimension(width, height));
            //if(component instanceof CheckBoxConfig) ((CheckBoxConfig) component).resize(width);
            if(component instanceof Button) ((Button) component).resize(width);
            //if(component instanceof CheckedTextInput) ((CheckedTextInput) component).resize(width);
            if(component instanceof TextInput) ((TextInput) component).resize(width);

        }

        scrollPane.updateUI();
        setBounds(0,0,width - subtractWidth, height - subtractHeight);
    }

    public void showPage(){
        titlePane.setVisible(true);
        scrollPane.setVisible(true);
        scrollPane.getVerticalScrollBar().setValue(0);
        setVisible(true);
    }

    private static SpecialTextArea createTextArea(boolean intFilter, boolean allowNegative, boolean allowDecimal, String setting, boolean noNewLine) {
        SpecialTextArea textArea = new SpecialTextArea(intFilter, allowNegative, allowDecimal);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }
            public void changed() {
                if(noNewLine) {
                    if (!textArea.getText().contains("\n") && !textArea.getText().contains("\r")) {
                        SettingsHandler.writeSettings(setting, textArea.getText());
                    }
                }
                else{
                    SettingsHandler.writeSettings(setting, textArea.getText());
                }
            }
        });
        return textArea;
    }

    private static class Button extends JPanel {
        private final String text;
        private final RoundedButton roundedButton;

        Button(String text, Function function){
            this.text = text;
            roundedButton = new RoundedButton(text);
            roundedButton.setToolTipText("");
            roundedButton.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            roundedButton.setBackground("list-background-normal", "list-background-normal");
            roundedButton.setForeground("foreground", "foreground");
            roundedButton.setBounds(28, 0, 460, 30);

            roundedButton.addActionListener(e -> {
                if(function != null) function.run();
            });
            setLayout(null);
            setOpaque(false);
            setPreferredSize(new Dimension(460, 40));
            add(roundedButton);
        }
        public void resize(int width){
            roundedButton.setBounds(28, 0, width-340, 30);
            setPreferredSize(new Dimension(width-340, 40));
        }
        public String getText(){
            return text;
        }
        public void remove(){
            getParent().remove(this);
        }
    }


    private static class RadioOption extends JPanel {

        private final ThemeableJLabel titleText = new ThemeableJLabel("");
        private final ThemeableJLabel descriptionText = new ThemeableJLabel("");
        private final RadioPanel radioPanel;

        RadioOption(String text, String description, String[] options, String setting, String defaultOption, Function function){
            setLayout(null);
            setBackground(new Color(0,0,0,0));
            setOpaque(false);
            titleText.setText(text);
            titleText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            titleText.setOpaque(false);
            titleText.setPreferredSize(new Dimension(450, 30));
            titleText.setBounds(29,2, 450, 30);

            descriptionText.setText("<html><div WIDTH=450> " + description + " </div></html>");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));

            if(!description.isEmpty()){
                descriptionText.setBounds(29, 27, 450, descriptionText.getPreferredSize().height);
                add(descriptionText);
            }

            radioPanel = new RadioPanel(options){
                @Override
                public void changeFired(String identifier) {
                    SettingsHandler.writeSettings(setting, identifier);
                    if(function != null) function.run();
                }
            };

            if(SettingsHandler.getSettings(setting).exists()) radioPanel.setChecked(SettingsHandler.getSettings(setting).asString());
            else {
                radioPanel.setChecked(defaultOption);
                SettingsHandler.writeSettings(setting, defaultOption);
            }

            radioPanel.setBounds(30,30 + descriptionText.getPreferredSize().height,460, radioPanel.getPreferredSize().height);
            radioPanel.setBackground(new Color(0,0,0,0));

            setPreferredSize(new Dimension(460, radioPanel.getPreferredSize().height + descriptionText.getPreferredSize().height + 10));


            add(titleText);
            add(radioPanel);
        }
    }

    /*private static class CheckedTextInput extends JPanel {
        private final ThemedCheckbox checkbox = new ThemedCheckbox("");
        private final LangLabel descriptionText = new LangLabel("");
        private final FancyTextArea textArea;
        private final String description;

        CheckedTextInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String checkSetting, String inputSetting, boolean defaultOption, String defaultInput){

            this.description = description;

            int height = lines * 32 - (lines-1) * 10;

            checkbox.setText(text);
            checkbox.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            checkbox.setOpaque(false);
            checkbox.setPreferredSize(new Dimension(450, 30));
            checkbox.setBounds(29,0, 450, 30);
            if(SettingsHandler.getSettings(checkSetting).exists()) checkbox.setChecked(SettingsHandler.getSettings(checkSetting).asBoolean());
            else {
                checkbox.setChecked(defaultOption);
                SettingsHandler.writeSettings(checkSetting, String.valueOf(defaultOption));
            }

            descriptionText.setTextLang("<html><div WIDTH=450> " + description + " </div></html>");
            descriptionText.setFont(Defaults.MAIN_FONT.deriveFont(13f));
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));

            textArea = createTextArea(intFilter, allowNegative, allowDecimal, inputSetting, false);

            if(!description.equals("")){
                setPreferredSize(new Dimension(460, 47+height + descriptionText.getPreferredSize().height));
                descriptionText.setBounds(29, 30, 450, descriptionText.getPreferredSize().height);
                textArea.setBounds(29,38 + descriptionText.getPreferredSize().height,460, height);
                add(descriptionText);
            }
            else{
                textArea.setBounds(29,34,460, height);
                setPreferredSize(new Dimension(460, 41+height));
            }

            checkbox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    textArea.setFocusable(false);
                    textArea.setFocusable(checkbox.getSelectedState());
                    textArea.setEditable(checkbox.getSelectedState());
                    SettingsHandler.writeSettings(checkSetting, String.valueOf(checkbox.getSelectedState()));
                }
            });
            setOpaque(false);
            setLayout(null);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            if (SettingsHandler.getSettings(inputSetting).exists()) textArea.setText(SettingsHandler.getSettings(inputSetting).asString());
            else {
                textArea.setText(defaultInput);
                SettingsHandler.writeSettings(inputSetting, defaultInput);
            }
            textArea.setEditable(SettingsHandler.getSettings(checkSetting).asBoolean());
            textArea.setFocusable(SettingsHandler.getSettings(checkSetting).asBoolean());
            setBackground(new Color(0,0,0,0));
            add(textArea);
            add(checkbox);
        }
        public void refreshUI(){
            setBackground(new Color(0,0,0,0));
            descriptionText.setForeground(Defaults.FOREGROUND_B);
            checkbox.setForeground(Defaults.FOREGROUND_A);
        }
        public void resize(int width){
            if(!description.equals("")) descriptionText.setBounds(29, 30, width-340, descriptionText.getPreferredSize().height);
            textArea.setBounds(29,textArea.getY(),width-340, textArea.getHeight());
            setPreferredSize(new Dimension(width-340, getPreferredSize().height));
            checkbox.setPreferredSize(new Dimension(width-340, 30));
            checkbox.setBounds(29,0, width-340, 30);
        }
    }*/


    private static class TextInput extends JPanel {
        private final ThemeableJLabel titleText = new ThemeableJLabel("");
        private final ThemeableJLabel descriptionText = new ThemeableJLabel("");
        private final String description;
        private final SpecialTextArea textArea;


        TextInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String setting, String defaultInput, boolean editable, boolean noNewLine){
            this.description = description;
            int height = lines * 32 - (lines-1) * 10;

            setOpaque(false);
            titleText.setText(text);
            titleText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            titleText.setOpaque(false);
            titleText.setPreferredSize(new Dimension(450, 30));
            titleText.setBounds(29,0, 450, 30);

            descriptionText.setText("<html><div WIDTH=450> " + description +" </div></html>");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));

            textArea = createTextArea(intFilter, allowNegative, allowDecimal, setting, noNewLine);

            if(!description.equals("")){
                setPreferredSize(new Dimension(460, 43+height + descriptionText.getPreferredSize().height));
                descriptionText.setBounds(29, 26, 450, descriptionText.getPreferredSize().height);
                textArea.setBounds(29,36 + descriptionText.getPreferredSize().height,460, height);
                add(descriptionText);
            }
            else{
                textArea.setBounds(29,30,460, height);
                setPreferredSize(new Dimension(460, 35+height));
            }

            setLayout(null);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            if(setting != null) {
                if (SettingsHandler.getSettings(setting).exists()) textArea.setText(SettingsHandler.getSettings(setting).asString());
                else {
                    textArea.setText(defaultInput);
                    SettingsHandler.writeSettings(setting, defaultInput);
                }
            }
            else textArea.setText(defaultInput);

            textArea.clearUndo();
            textArea.setEditable(editable);
            add(textArea);
            add(titleText);
        }
        public void resize(int width){
            if(!description.equals("")) descriptionText.setBounds(29, 30, width-340, descriptionText.getPreferredSize().height);
            textArea.setBounds(29,textArea.getY(),width-340, textArea.getHeight());
            setPreferredSize(new Dimension(width-340, getPreferredSize().height));
        }
    }

    /*public static class CheckBox extends JPanel {

        private final ThemedCheckbox themedCheckbox;
        private final LangLabel descriptionText = new LangLabel("");

        private final String setting;
        private final boolean defaultOption;

        private static ArrayList<CheckBox> checkBoxes = new ArrayList<>();

        CheckBox(String text, String description, String setting, boolean defaultOption, Function function){
            this.setting = setting;
            this.defaultOption = defaultOption;
            themedCheckbox = new ThemedCheckbox(text);
            if (SettingsHandler.getSettings(setting).exists()) themedCheckbox.setChecked(SettingsHandler.getSettings(setting).asBoolean());
            else {
                themedCheckbox.setChecked(defaultOption);
                SettingsHandler.writeSettings(setting, String.valueOf(defaultOption));
            }
            setLayout(null);
            setOpaque(false);
            themedCheckbox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    SettingsHandler.writeSettings(setting, String.valueOf(themedCheckbox.getSelectedState()));
                    if(function != null) function.run();
                }
            });

            descriptionText.setTextLang("<html><div WIDTH=450> " + description + " </div></html>");
            descriptionText.setFont(Defaults.MAIN_FONT.deriveFont(13f));
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));

            if(!description.equals("")){
                setPreferredSize(new Dimension(450, 38 + descriptionText.getPreferredSize().height));
                descriptionText.setBounds(29, 30, 450, descriptionText.getPreferredSize().height);
                add(descriptionText);
            }
            else{
                setPreferredSize(new Dimension(450, 40));
            }

            themedCheckbox.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            themedCheckbox.setBounds(30,0, 450, 30);
            setBackground(new Color(0,0,0,0));
            themedCheckbox.refresh();
            add(themedCheckbox);
            checkBoxes.add(this);
        }
        public void refreshUI(){
            setBackground(new Color(0,0,0,0));
            descriptionText.setForeground(Defaults.FOREGROUND_B);
        }
        public void resetCheck(){
            if (SettingsHandler.getSettings(setting).exists()) themedCheckbox.setChecked(SettingsHandler.getSettings(setting).asBoolean());
            else {
                themedCheckbox.setChecked(defaultOption);
            }
        }

        public static void resetCheckbox(String setting){
            for(CheckBox checkBox : checkBoxes){
                if(checkBox.setting.equals(setting)){
                    checkBox.resetCheck();
                }
            }
        }


    }*/
    /*public static class CheckBoxConfig extends JPanel {

        private final ThemedConfigCheckbox themedCheckbox;

        public CheckBoxConfig(String text, String description, String setting, boolean defaultOption, Function function){
            themedCheckbox = new ThemedConfigCheckbox(text, description, function, false);
            if (SettingsHandler.getSettings(setting).exists()) themedCheckbox.setChecked(SettingsHandler.getSettings(setting).asBoolean());
            else {
                themedCheckbox.setChecked(defaultOption);
                SettingsHandler.writeSettings(setting, String.valueOf(defaultOption));
            }
            setLayout(null);
            setOpaque(false);
            themedCheckbox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    SettingsHandler.writeSettings(setting, String.valueOf(themedCheckbox.getSelectedState()));
                }
            });

            setPreferredSize(new Dimension(450, 76));

            themedCheckbox.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            themedCheckbox.setBounds(10,0, 450, 70);
            setBackground(new Color(0,0,0,0));
            themedCheckbox.refresh();
            add(themedCheckbox);
        }
        public void resize(int width){
            setPreferredSize(new Dimension(width-300, 76));
            themedCheckbox.setBounds(10,0, width-300, 70);
            themedCheckbox.resize(width-300);
        }
    }*/
}
