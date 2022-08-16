package com.alphalaneous.Utils;

import com.alphalaneous.Swing.BrowserWindow;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

public class LoquiBrowser implements AuthorizationCodeInstalledApp.Browser {

    BrowserWindow browserWindow;


    @Override
    public void browse(String s) {
        browserWindow = new BrowserWindow(s, true);
        browserWindow.setOnTop(true);
        browserWindow.setSize(550, 750);
    }

    public void close(){
        if(browserWindow != null) browserWindow.close();
    }


}
