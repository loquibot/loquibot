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
import java.net.URI;

public class BrowserWindow {

    public BrowserWindow(String link){
        Utilities.openURL(URI.create(link));
    }
}
