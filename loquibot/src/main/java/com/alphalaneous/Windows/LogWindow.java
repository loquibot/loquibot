package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Components.SmoothScrollPane;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogWindow {

    private static final JFrame frame = new JFrame();
    private static SmoothScrollPane scrollPane;

    public static void createWindow(){

        frame.setSize(new Dimension(800,500));
        frame.setUndecorated(true);
        frame.setLayout(new BorderLayout());
        frame.setIconImages(Main.getIconImages());
        frame.setTitle("loquibot - Console");
        frame.setBackground(new Color(0,0,0,200));
        frame.getRootPane().setOpaque(false);


        JTextPane pane = new JTextPane();
        pane.setBackground(new Color(0,0,0,0));
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setForeground(Color.WHITE);
        pane.setSelectionColor(Color.WHITE);
        pane.setSelectedTextColor(Color.BLACK);
        pane.setFont(new Font("Consolas", Font.PLAIN, 14));
        setTabs(pane, 4);
        AlphaContainer alphaContainer = new AlphaContainer(pane);

        JTextPane commandArea = new JTextPane();
        commandArea.setBackground(new Color(0,0,0));
        commandArea.setBorder(BorderFactory.createEmptyBorder());
        commandArea.setForeground(Color.WHITE);
        commandArea.setSelectionColor(Color.WHITE);
        commandArea.setSelectedTextColor(Color.BLACK);
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


        AlphaContainer commandAlphaContainer = new AlphaContainer(commandArea);
        scrollPane = new SmoothScrollPane(alphaContainer);
        scrollPane.setHorizontalScrollEnabled(true);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(new Color(0,0,0,200));
        scrollPane.setPreferredSize(new Dimension(800, 476));
        scrollPane.setBorder(null);

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(commandAlphaContainer, BorderLayout.SOUTH);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scrollPane.setPreferredSize(new Dimension(800, frame.getPreferredSize().height-24));

            }
        });

        MessageConsole mc = new MessageConsole(pane);
        mc.redirectOut(Color.WHITE, System.out);
        mc.redirectErr(Color.RED, null);

    }

    public static void toggleLogWindow(){
        frame.setVisible(!frame.isVisible());
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
                    Requests.request(TwitchAccount.display_name, true, true, text, null, -1);
                }
                catch (Exception e){
                    System.out.println("! Could not add level! Reason: " + e);
                }
                break;
            }

            default: {
                System.out.println("! That command doesn't exist!");
            }
        }

        //System.out.println(text);
    }



    public static void setTabs( final JTextPane textPane, int charactersPerTab)
    {
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
