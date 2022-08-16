package com.alphalaneous.Swing;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class BrowserWindow extends JFrame {
    private static final CefAppBuilder builder = new CefAppBuilder();
    private static CefClient  client_ = null;
    private static boolean initialized = false;
    public static void init() {
        builder.setInstallDir(new File(Defaults.saveDirectory + "/loquibot/browser"));
        builder.setProgressHandler(new ConsoleProgressHandler());

        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;

        CefApp cefApp_;
        try {
            cefApp_ = builder.build();
            cefApp_.setSettings(settings);
        } catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
            throw new RuntimeException(e);
        }

        client_ = cefApp_.createClient();


        initialized = true;
    }

    private final CefBrowser browser_;

    public BrowserWindow(String link){
        this(link, false);
    }
    public BrowserWindow(String link, boolean dontClose){

        if(!initialized) {
            new Thread(() -> {
                JOptionPane.showMessageDialog(null, "Browser is initializing, please wait...");
            }).start();

            while (!initialized) Utilities.sleep(5);

        }
        setTitle("loquibot - Browser");
        setIconImages(Main.getIconImages());
        if(dontClose) {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }


        browser_ = client_.createBrowser(link, false, false);

        Component browerUI_ = browser_.getUIComponent();

        add(browerUI_);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                browser_.close(false);
                dispose();
            }
        });

        setSize(750, 750);
        setLocationRelativeTo(Window.getWindow());
        setVisible(true);
    }
    public void close(){
        setVisible(false);
        dispose();
    }
    public void setOnTop(boolean onTop){
        setAlwaysOnTop(onTop);
    }

    public void executeJavaScript(String script){
        browser_.executeJavaScript(script, browser_.getURL(), 0);
    }
}
