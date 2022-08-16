package com.alphalaneous.Utils;

import com.alphalaneous.Swing.BasicBrowserWindow;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import java.io.IOException;

public class LoquiBrowser implements AuthorizationCodeInstalledApp.Browser {

    BasicBrowserWindow basicBrowserWindow;


    @Override
    public void browse(String s) throws IOException {
        basicBrowserWindow = new BasicBrowserWindow(s);
    }

    public void close(){
        if(basicBrowserWindow != null) basicBrowserWindow.close();
    }


}
