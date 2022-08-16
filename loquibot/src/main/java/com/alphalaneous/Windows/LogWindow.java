package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utils.Utilities;
import com.sun.jna.platform.WindowUtils;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogWindow {

    private static final JFrame frame = new JFrame();
    private static JScrollPane scrollPane;
    public static void createWindow(){

        frame.setSize(new Dimension(800,500));
        frame.setLayout(new BorderLayout());
        frame.setTitle("loquibot - Console");
        frame.setBackground(new Color(12, 12, 12));

        JTextPane pane = new JTextPane();
        pane.setBackground(new Color(12, 12, 12));
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setForeground(Color.WHITE);
        pane.setSelectionColor(new Color(132, 132, 132));
        pane.setSelectedTextColor(Color.WHITE);
        pane.setFont(new Font("Consolas", Font.PLAIN, 14));
        setTabs(pane, 4);

        JTextPane commandArea = new JTextPane();
        commandArea.setBackground(new Color(39, 39, 39));
        commandArea.setBorder(BorderFactory.createEmptyBorder());
        commandArea.setForeground(Color.WHITE);
        commandArea.setSelectionColor(new Color(132, 132, 132));
        commandArea.setSelectedTextColor(Color.WHITE);
        commandArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        commandArea.setPreferredSize(new Dimension(800, 24));

        int condition = JComponent.WHEN_FOCUSED;
        InputMap inputMap = commandArea.getInputMap(condition);
        ActionMap actionMap = commandArea.getActionMap();

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        inputMap.put(enterKey, enterKey.toString());
        actionMap.put(enterKey.toString(), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane textPane = (JTextPane) e.getSource();
                if (!textPane.getText().equalsIgnoreCase("")) {
                    new Thread(() -> {
                        runCommand(textPane.getText());
                        textPane.setText("");
                        textPane.requestFocus();
                    }).start();
                }
            }
        });

        scrollPane = new JScrollPane(pane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(new Color(0,0,0));
        scrollPane.setBorder(null);

        frame.add(scrollPane);
        frame.add(commandArea, BorderLayout.SOUTH);

        WindowUtils.setWindowAlpha(frame, 0.9f);

        MessageConsole mc = new MessageConsole(pane);
        mc.redirectOut(Color.WHITE, System.out);
        mc.redirectErr(Color.RED, null);
    }

    public static void toggleLogWindow(){
        frame.setLocationRelativeTo(Window.getWindow());
        frame.setVisible(!frame.isVisible());
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        frame.setIconImages(Main.getIconImages());
    }

    public static JFrame getWindow(){
        return frame;
    }

    private static void runCommand(String text){

        String command = text.split(" ")[0];
        String argString = text.replaceFirst(command, "").trim();
        ArrayList<String> args = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(argString);
        while (m.find()) args.add(m.group(1).replace("\"", ""));

        switch (command){

            case "/help" : {
                System.out.println("List of commands: " +
                        "\n\t/request | Manually request an ID"+
                        "\n\t/fill | Fills the queue with levels");
                break;
            }
            case "/fill":{

                JSONObject dummyLevels = new JSONObject(Utilities.readIntoString(new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                        Main.class.getClassLoader().getResourceAsStream("dummyLevelList.json"))))));
                Requests.loadLevels(dummyLevels);
                break;
            }
            case "/request" : {
                try {
                    Requests.request(TwitchAccount.display_name, true, true, text, null, -1, null);
                }
                catch (Exception e){
                    System.out.println("! Could not add level! Reason: " + e);
                }
                break;
            }
            case "/browser": {
                new BrowserWindow("https://google.com");
                break;
            }
            default: {
                System.out.println("! That command doesn't exist!");
            }
        }
    }

    public static void setTabs( final JTextPane textPane, int charactersPerTab) {
        FontMetrics fm = textPane.getFontMetrics( textPane.getFont() );
        int charWidth = fm.charWidth( ' ' );
        int tabWidth = charWidth * charactersPerTab;

        TabStop[] tabs = new TabStop[5];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop( tab * tabWidth );
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = textPane.getDocument().getLength();
        textPane.getStyledDocument().setParagraphAttributes(0, length, attributes, false);
    }
}
