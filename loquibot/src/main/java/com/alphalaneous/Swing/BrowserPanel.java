package com.alphalaneous.Swing;

import com.alphalaneous.Browser.MessageRouterHandler;
import com.alphalaneous.Browser.MessageRouterHandlerEx;
import com.alphalaneous.Browser.ValueReader;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import me.friwi.jcefmaven.*;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BrowserPanel extends JPanel {
    private static final CefAppBuilder builder = new CefAppBuilder();
    private static CefClient client_ = null;
    private static boolean initialized = false;
    private WebView webView;
    private boolean loaded = false;

    private static final ArrayList<BrowserPanel> browserPanels = new ArrayList<>();


    public static void init() {

        builder.addJcefArgs("--autoplay-policy=no-user-gesture-required");
        builder.setInstallDir(new File(Defaults.saveDirectory + "/loquibot/browser"));
        builder.setProgressHandler((e, v) -> {});

        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;


        CefApp cefApp_;
        try {
            cefApp_ = builder.build();
            cefApp_.setSettings(settings);
        } catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
            throw new RuntimeException(e);
        }

        CefMessageRouter msgRouter = CefMessageRouter.create();
        msgRouter.addHandler(new MessageRouterHandler(), true);
        msgRouter.addHandler(new MessageRouterHandlerEx(client_), false);

        client_ = cefApp_.createClient();
        client_.addMessageRouter(msgRouter);
        client_.addDisplayHandler(new ValueReader());
        client_.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                System.out.println("here");
                for(BrowserPanel panel : browserPanels){
                    if(panel.browser_ == browser){
                        new Thread(() -> {

                            panel.function.run();
                        }).start();
                        break;
                    }
                }
            }
        });

        initialized = true;
    }

    private CefBrowser browser_;

    public BrowserPanel() {
        this(null);
    }

    public BrowserPanel(String link) {
        browserPanels.add(this);
        if(link == null) link = "about::blank";

        if (!initialized) {
            String finalLink = link;
            Platform.runLater(() -> {
                new Thread(() -> {
                    Platform.runLater(() -> {

                        webView = new WebView();
                        JFXPanel panel1 = new JFXPanel();
                        Scene scene = new Scene(webView);
                        panel1.setScene(scene);

                        setLayout(new BorderLayout());
                        add(panel1);

                        webView.getEngine().load(finalLink);

                    });
                }).start();

            });

        } else {

            browser_ = client_.createBrowser(link, false, false);
            Component browserUI = browser_.getUIComponent();
            setLayout(new BorderLayout());

            add(browserUI);

        }
    }


    public String getJSObject(String js) {
        if(initialized){
            return ValueReader.getValue(browser_, js);
        }
        else {
            return webView.getEngine().executeScript(js).toString();
        }
    }

    public void closeBrowser(){
        browser_.close(false);
    }

    public void loadURL(String link){

        if(initialized){
            browser_.loadURL(link);
        }
        else {
            webView.getEngine().load(link);
        }
    }

    public void runJavaScript(String js){

        if(initialized){
            browser_.executeJavaScript(js, browser_.getURL(), 0);
        }
        else {
            webView.getEngine().executeScript(js);
        }

    }

    public void waitUntilLoaded(){
        while(!loaded){
            Utilities.sleep(300);
        }
        Utilities.sleep(300);
    }

    private Function function;

    public void onLoad(Function function){
        this.function = function;
    }
}
