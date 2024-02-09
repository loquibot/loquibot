package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJFXPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import org.apache.commons.validator.routines.UrlValidator;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class ChatPanel extends ThemeableJPanel{

    private final ThemeableJFXPanel twitchJfxPanel = new ThemeableJFXPanel();

    private final ThemeableJFXPanel youtubeJfxPanel = new ThemeableJFXPanel();
    private final ThemeableJPanel parentPanel = new ThemeableJPanel();

    private final JPanel twitchLoadingPanel = new JPanel();
    private final JPanel youtubeLoadingPanel = new JPanel();
    WebView twitchWebView;
    WebView youtubeWebView;

    public ChatPanel() {

        twitchJfxPanel.setOpaque(false);
        twitchJfxPanel.setVisible(false);

        twitchLoadingPanel.setOpaque(false);

        setLayout(new GridBagLayout());
        ThemeableJLabel loadingLabel = new ThemeableJLabel();
        if(SettingsHandler.getSettings("isTwitchLoggedIn").asBoolean()) {
            loadingLabel.setText("Waiting for chat, loading...");
        }
        else{
            loadingLabel.setText("Go live to view chat!");
        }

        loadingLabel.setForeground("foreground");

        loadingLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

        LoadingCircle loadingCircle = new LoadingCircle();

        twitchLoadingPanel.add(loadingCircle);
        twitchLoadingPanel.add(Box.createHorizontalStrut(10));
        twitchLoadingPanel.add(loadingLabel);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(twitchLoadingPanel, gbc);

        if(SettingsHandler.getSettings("isTwitchLoggedIn").asBoolean()) {
            Platform.runLater(() -> {
                twitchWebView = new WebView();
                twitchJfxPanel.setScene(new Scene(twitchWebView));
                twitchJfxPanel.setVisible(false);
                parentPanel.add(twitchJfxPanel);
            });
        }

        if(SettingsHandler.getSettings("isYouTubeLoggedIn").asBoolean()) {
            Platform.runLater(() -> {
                youtubeWebView = new WebView();
                youtubeJfxPanel.setScene(new Scene(youtubeWebView));
                youtubeJfxPanel.setVisible(false);
            });
        }

        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        parentPanel.setBackground("background");

        setBackground("background");
        setPreferredSize(new Dimension(400, 600));

    }

    public void loadYouTubeChat(String streamID){
        Platform.runLater(() -> {
            if(streamID != null) {
                youtubeWebView.getEngine().load("https://www.youtube.com/live_chat?is_popout=1&v=" + streamID + "&dark_theme=1");
                setupChat(youtubeWebView, youtubeJfxPanel, twitchLoadingPanel);
                parentPanel.add(youtubeJfxPanel);
            }
        });

        youtubeJfxPanel.setBackground("background");
    }


    public void loadTwitchChat(String username){

        Platform.runLater(() -> {
            twitchWebView.getEngine().load("https://www.twitch.tv/popout/" + username + "/chat?parent=icelz.s3.amazonaws.com&darkpopout");
            setupChat(twitchWebView, twitchJfxPanel, twitchLoadingPanel);
        });

        twitchJfxPanel.setBackground("background");
    }

    private void setupChat(WebView webView, ThemeableJFXPanel jfxPanel, JPanel loadingPanel) {
        //webView.setContextMenuEnabled(false);


        if(webView.getEngine().getLocation().toLowerCase().startsWith("https://www.twitch.tv/popout/") ||
                webView.getEngine().getLocation().toLowerCase().startsWith("https://www.youtube.com/live_chat")) {

            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    jfxPanel.setVisible(true);
                    parentPanel.setVisible(true);
                    remove(loadingPanel);
                    revalidate();
                    setLayout(new BorderLayout(0,0));
                    add(parentPanel);
                }
            });
        }
        webView.getEngine().setCreatePopupHandler(
                //hacky workaround to make links open in the system browser from the webview
                popupFeatures ->{
                    Object o = webView
                            .getEngine()
                            .executeScript(
                                    "var list = document.querySelectorAll(':hover');"
                                            + "for (i = list.length-1; i >- 1; i--) {"
                                            + "if (list.item(i).getAttribute('href')){"
                                            + "list.item(i).getAttribute('href'); break; } }");

                    try {
                        if (o != null) {
                            //avoid exception if URL is malformed, faster than catching if it is
                            UrlValidator urlValidator = new UrlValidator();
                            if(urlValidator.isValid(o.toString())) {
                                Desktop.getDesktop().browse(new URI(o.toString()));
                            }
                        }
                    } catch (Exception e) {
                        Logging.getLogger().error(e.getMessage(), e);
                    }
                    //prevent from opening in webView
                    return null;
                }
        );
    }

}
