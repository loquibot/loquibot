package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Components.*;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.Language;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;

public class SettingsSubPage extends ThemeableJPanel {

    private final JPanel settingsPane = new JPanel();
    private final GridBagConstraints gbc = new GridBagConstraints();
    public SettingsSubPage(String title){

        settingsPane.setLayout(new GridBagLayout());
        setLayout(new GridLayout());
        setOpaque(false);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 20, 0, 0);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ThemeableJPanel borderPanel = new ThemeableJPanel(new BorderLayout());
        SmoothScrollPane scrollPane = new SmoothScrollPane(borderPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setOpaque(false);

        SettingsTitle settingsTitle = new SettingsTitle(title);
        settingsTitle.setOpaque(false);

        JPanel titlePane = new JPanel();
        titlePane.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        titlePane.setOpaque(false);
        titlePane.setBorder(new EmptyBorder(5,0,10,0));
        titlePane.add(settingsTitle);

        borderPanel.setLayout(new BorderLayout());
        borderPanel.add(settingsPane, BorderLayout.NORTH);
        borderPanel.setOpaque(false);

        settingsPane.setOpaque(false);
        settingsPane.add(titlePane, gbc);

        gbc.insets = new Insets(0, 24, 8, 0);

        add(scrollPane);
    }

    public void addComponent(ThemeableJPanel component){
        settingsPane.add(component, gbc);
    }

    public void addSlider(String text, String description, String setting, String amountText, String amountTextSingular, int min, int max, int defaultValue, Function onChange){
        settingsPane.add(new Slider(text, description, setting, amountText, amountTextSingular, min, max, defaultValue, onChange), gbc);
    }

    public void addButton(String text, Function function){
        settingsPane.add(new Button(text, function), gbc);
    }

    public void addRadioOption(String text, String description, HashMap<String, String> options, String setting, String defaultOption, Function function){
        settingsPane.add(new RadioOption(text, description, options, setting, defaultOption, function), gbc);
    }
    public void addRadioOption(String text, String description, HashMap<String, String> options, String setting, String defaultOption){
        addRadioOption(text, description, options, setting, defaultOption, null);
    }

    public void addCheckbox(String text, String description, String setting, boolean defaultOption, Function function){
        settingsPane.add(new SettingsCheckBox(text, description, setting, defaultOption, function), gbc);
    }
    public void addCheckbox(String text, String description, String setting, Function function){
        addCheckbox(text, description, setting, false, function);
    }
    public void addCheckbox(String text, String description, String setting){
        addCheckbox(text, description, setting, false, null);
    }

    public void addInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String setting, String defaultInput, boolean editable, boolean noNewLine){
        settingsPane.add(new TextInput(text, description, lines, intFilter, allowNegative, allowDecimal, setting, defaultInput, editable, noNewLine), gbc);
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

    public void addCheckedInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String checkSetting, String inputSetting, boolean defaultOption, String defaultInput){
        settingsPane.add(new CheckedTextInput(text, description, lines, intFilter, allowNegative, allowDecimal, checkSetting, inputSetting, defaultOption, defaultInput), gbc);
    }
    public void addCheckedInput(String text, String description, int lines,  String checkSetting, String inputSetting, boolean defaultOption, String defaultInput){
        addCheckedInput(text, description, lines, false, true, true, checkSetting, inputSetting, defaultOption, defaultInput);
    }
    public void addCheckedInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String checkSetting, String inputSetting){
        addCheckedInput(text, description, lines, intFilter, allowNegative, allowDecimal, checkSetting, inputSetting, false, "");
    }
    public void addCheckedInput(String text, String description, int lines,  String checkSetting, String inputSetting){
        addCheckedInput(text, description, lines, false, true, true, checkSetting, inputSetting, false, "");
    }

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

        Button(String text, Function function){
            this.text = text;
            RoundedButton roundedButton = new RoundedButton(text);
            roundedButton.setToolTipText("");
            roundedButton.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            roundedButton.setBackground("list-background-normal", "list-background-normal");
            roundedButton.setForeground("foreground", "foreground");

            roundedButton.addActionListener(e -> {
                if(function != null) function.run();
            });
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0,0,5,0));
            add(roundedButton);
        }
        public String getText(){
            return text;
        }
        public void remove(){
            getParent().remove(this);
        }
    }


    private static class RadioOption extends JPanel {

        RadioOption(String text, String description, HashMap<String, String> options, String setting, String defaultOption, Function function){

            setLayout(new MigLayout("flowy, insets 0"));
            setOpaque(false);

            ThemeableJLabel titleText = new ThemeableJLabel("");
            titleText.setForeground("foreground");
            titleText.setText(text);
            titleText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            titleText.setOpaque(false);
            titleText.setMinimumSize(new Dimension(30, 32));

            ThemeableJLabel descriptionText = new ThemeableJLabel("");
            descriptionText.setForeground("foreground-darker");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setOpaque(false);
            descriptionText.setLineWrap(true);
            descriptionText.setText(description);

            RadioPanel radioPanel = new RadioPanel(options) {
                @Override
                public void changeFired(String identifier) {
                    SettingsHandler.writeSettings(setting, identifier);
                    if (function != null) function.run();
                }
            };

            if(SettingsHandler.getSettings(setting).exists()) radioPanel.setChecked(SettingsHandler.getSettings(setting).asString());
            else {
                radioPanel.setChecked(defaultOption);
                SettingsHandler.writeSettings(setting, defaultOption);
            }


            add(titleText, "width 100%, height 32px");
            add(radioPanel, "width 100%");
            if(!description.isEmpty()){
                add(descriptionText, "width 100%");
            }

        }
    }

    private static class CheckedTextInput extends ThemeableJPanel {
        private final TitledCheckbox checkbox = new TitledCheckbox("");
        private final SpecialTextArea textArea;

        CheckedTextInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String checkSetting, String inputSetting, boolean defaultOption, String defaultInput){

            int height = lines * 32 - (lines-1) * 10;

            setLayout(new MigLayout("flowy, insets 0"));
            setOpaque(false);

            checkbox.setText(text);
            checkbox.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            checkbox.setOpaque(false);
            checkbox.setMinimumSize(new Dimension(10, 30));

            if(SettingsHandler.getSettings(checkSetting).exists()) checkbox.setChecked(SettingsHandler.getSettings(checkSetting).asBoolean());
            else {
                checkbox.setChecked(defaultOption);
                SettingsHandler.writeSettings(checkSetting, String.valueOf(defaultOption));
            }

            ThemeableJLabel descriptionText = new ThemeableJLabel("");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setForeground("foreground-darker");
            descriptionText.setOpaque(false);
            descriptionText.setLineWrap(true);
            descriptionText.setText(description);

            textArea = createTextArea(intFilter, allowNegative, allowDecimal, inputSetting, false);

            checkbox.addCheckListener(b -> {
                textArea.setFocusable(false);
                textArea.setFocusable(checkbox.getSelectedState());
                textArea.setEditable(checkbox.getSelectedState());
                SettingsHandler.writeSettings(checkSetting, String.valueOf(checkbox.getSelectedState()));
            });

            textArea.clearUndo();
            textArea.setMinimumSize(new Dimension(10, height));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            if (SettingsHandler.getSettings(inputSetting).exists()) textArea.setText(SettingsHandler.getSettings(inputSetting).asString());
            else {
                textArea.setText(defaultInput);
                SettingsHandler.writeSettings(inputSetting, defaultInput);
            }
            textArea.setEditable(SettingsHandler.getSettings(checkSetting).asBoolean());
            textArea.setFocusable(SettingsHandler.getSettings(checkSetting).asBoolean());

            add(checkbox, "width 100%, height 30px");
            add(textArea, "width 100%, height " + height + "px");
            if(!description.isEmpty()){
                add(descriptionText, "width 100%");
            }
        }
    }


    private static class TextInput extends ThemeableJPanel {

        TextInput(String text, String description, int lines, boolean intFilter, boolean allowNegative, boolean allowDecimal, String setting, String defaultInput, boolean editable, boolean noNewLine) {
            int height = lines * 32 - (lines - 1) * 10;

            setLayout(new MigLayout("flowy, insets 0"));

            setOpaque(false);

            ThemeableJLabel titleText = new ThemeableJLabel("");
            titleText.setText(text);
            titleText.setForeground("foreground");
            titleText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

            ThemeableJLabel descriptionText = new ThemeableJLabel("");
            descriptionText.setText(description);
            descriptionText.setForeground("foreground-darker");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setLineWrap(true);

            SpecialTextArea textArea = createTextArea(intFilter, allowNegative, allowDecimal, setting, noNewLine);

            textArea.setOpaque(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            if (setting != null) {
                if (SettingsHandler.getSettings(setting).exists())
                    textArea.setText(SettingsHandler.getSettings(setting).asString());
                else {
                    textArea.setText(defaultInput);
                    SettingsHandler.writeSettings(setting, defaultInput);
                }
            } else textArea.setText(defaultInput);

            textArea.clearUndo();
            textArea.setEditable(editable);
            textArea.setMinimumSize(new Dimension(10, height));
            setBorder(new EmptyBorder(0,0,5,0));
            add(titleText, "width 100%");
            add(textArea, "width 100%, height " + height + "px");
            add(descriptionText, "width 100%");
        }
    }

    public static class SettingsCheckBox extends JPanel {

        private final TitledCheckbox checkbox;

        SettingsCheckBox(String text, String description, String setting, boolean defaultOption, Function function){

            setOpaque(false);
            setLayout(new MigLayout("flowy, insets 0"));

            checkbox = new TitledCheckbox(text);
            checkbox.setMinimumSize(new Dimension(30, 30));

            if (SettingsHandler.getSettings(setting).exists()) checkbox.setChecked(SettingsHandler.getSettings(setting).asBoolean());
            else {
                checkbox.setChecked(defaultOption);
                SettingsHandler.writeSettings(setting, String.valueOf(defaultOption));
            }

            checkbox.addCheckListener(b -> {
                SettingsHandler.writeSettings(setting, String.valueOf(checkbox.getSelectedState()));
                if(function != null) function.run();
            });

            ThemeableJLabel descriptionText = new ThemeableJLabel("");
            descriptionText.setForeground("foreground-darker");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setOpaque(false);
            descriptionText.setLineWrap(true);
            descriptionText.setText(description);


            setBackground(new Color(0,0,0,0));
            add(checkbox, "width 100%");


            if(!description.isEmpty()){
                add(descriptionText, "width 100%");
            }
        }
    }


    public static class Slider extends JPanel {

        Slider(String text, String description, String setting, String amountText, String amountTextSingular, int min, int max, int defaultValue, Function onChange) {
            setOpaque(false);
            setLayout(new MigLayout("flowy, insets 0"));

            ThemeableJLabel titleText = new ThemeableJLabel("");
            titleText.setText(text);
            titleText.setForeground("foreground");
            titleText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

            ThemeableJLabel descriptionText = new ThemeableJLabel("");
            descriptionText.setText(description);
            descriptionText.setForeground("foreground-darker");
            descriptionText.setFont(Fonts.getFont("Poppins-Regular").deriveFont(13f));
            descriptionText.setLineWrap(true);

            //todo add slider

            JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
            ThemeableJLabel sliderValue = new ThemeableJLabel("");

            sliderValue.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            sliderValue.setText(String.format(amountText, defaultValue));
            sliderValue.setForeground("foreground-darker");

            slider.setUI(new LightSliderUI(slider));

            slider.setOpaque(false);
            slider.setBorder(BorderFactory.createEmptyBorder());

            if(SettingsHandler.getSettings(setting).exists()) {
                int value = SettingsHandler.getSettings(setting).asInteger();

                slider.setValue(value);
                sliderValue.setText(String.format(Language.setLocale(amountText), value));
            }

            slider.addChangeListener(e -> {
                if (slider.getValue() == 1) {
                    sliderValue.setText(String.format(Language.setLocale(amountTextSingular), slider.getValue()));
                } else {
                    sliderValue.setText(String.format(Language.setLocale(amountText), slider.getValue()));
                }
                SettingsHandler.writeSettings(setting, String.valueOf(slider.getValue()));
                onChange.run();
            });



            add(titleText, "width 100%");

            add(slider, "width 100%");
            add(sliderValue, "width 100px");

            add(descriptionText, "width 100%");
        }
    }
}
