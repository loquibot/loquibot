package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.CurvedButton;
import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.ListButton;
import com.alphalaneous.Components.ListView;
import com.alphalaneous.Defaults;
import com.alphalaneous.Windows.DialogBox;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Scanner;

public class BlockedUserSettings {

    private static final FancyTextArea blockedInput = new FancyTextArea(false, false);
    private static final Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\blockedUsers.txt");
    private static final ListView listView = new ListView("$BLOCKED_USERS$");

    public static JPanel createPanel(){

        blockedInput.setBounds(380, 30, 200, 32);
        blockedInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);

        listView.addInput("\uF0D1", blockedInput, () -> {
            addBlockedUser(listView.getInputText());
            blockedInput.setText("");

        });
        if (Files.exists(file)) {
            Scanner sc = null;
            try {
                sc = new Scanner(file.toFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert sc != null;
            while (sc.hasNextLine()) {
                listView.addElement(createButton(sc.nextLine()));
            }
            sc.close();
        }
        return listView;
    }

    public static void removeBlockedUser(String ID){
        if (Files.exists(file)) {
            try {
                Path temp = Paths.get(Defaults.saveDirectory + "\\loquibot\\_tempUsers_");
                PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
                Files.lines(file)
                        .filter(line -> !line.contains(ID))
                        .forEach(out::println);
                out.flush();
                out.close();
                Files.delete(file);
                Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\loquibot\\blockedUsers.txt"), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException ex) {

                DialogBox.showDialogBox("Error!", ex.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
            }
        }
        ListButton button = getButtonFromID(ID);
        if(button != null) listView.removeElement(button);
    }

    private static ListButton getButtonFromID(String ID){
        for(Component component : listView.getAddedComponents()){
            if(component instanceof ListButton){
                if(((ListButton) component).getText().equalsIgnoreCase(ID)){
                    return (ListButton) component;
                }
            }
        }
        return null;
    }

    public static void addBlockedUser(String ID){
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
            boolean goThrough = true;
            Scanner sc = new Scanner(file.toFile());
            while (sc.hasNextLine()) {
                if (String.valueOf(ID).equals(sc.nextLine())) {
                    goThrough = false;
                    break;
                }
            }
            sc.close();
            if (goThrough) {
                if (!ID.equalsIgnoreCase("")) {

                    Files.write(file, (ID + "\n").getBytes(), StandardOpenOption.APPEND);
                    listView.addElement(createButton(ID));
                }
            }
        } catch (IOException e1) {
            DialogBox.showDialogBox("Error!", e1.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
        }
    }

    public static CurvedButton createButton(String text){
        ListButton button = new ListButton(text, 164);
        button.addActionListener(e -> new Thread(() ->{
            String option = DialogBox.showDialogBox("$UNBLOCK_USER_DIALOG_TITLE$", "$UNBLOCK_USER_DIALOG_INFO$", "", new String[]{"$YES$", "$NO$"}, new Object[]{button.getLText()});
            if (option.equalsIgnoreCase("YES")) {
                if (Files.exists(file)) {
                    try {
                        Path temp = Paths.get(Defaults.saveDirectory + "\\loquibot\\_tempUsers_");
                        PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
                        Files.lines(file)
                                .filter(line -> !line.contains(button.getLText()))
                                .forEach(out::println);
                        out.flush();
                        out.close();
                        Files.delete(file);
                        Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\loquibot\\blockedUsers.txt"), StandardCopyOption.REPLACE_EXISTING);

                    } catch (IOException ex) {

                        DialogBox.showDialogBox("Error!", ex.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
                    }
                }
                listView.removeElement(button);
            }
        }).start());
        return button;
    }
}
