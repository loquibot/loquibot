package com.alphalaneous.Browser;

import com.alphalaneous.Utils.Utilities;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ValueReader extends CefDisplayHandlerAdapter {

    static List<String> responses = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {

        if(message.startsWith("loquibot,")){
            message = message.substring("loquibot,".length());
            responses.add(message);
        }

        return false;
    }


    public static String getValue(CefBrowser browser, String js){

        String ID = UUID.randomUUID().toString();

        browser.executeJavaScript("console.log('loquibot," + ID + ",' + " + js +" )", browser.getURL(), 0);



        while(true) {
            for (String s : responses) {
                if (s.split(",",2)[0].equals(ID)) {
                    responses.remove(s);
                    return s.split(",",2)[1];
                }
            }
            Utilities.sleep(1);
        }
    }
}
