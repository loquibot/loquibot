package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJFXPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.GraphicsFunctions;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class TwitchChat extends ThemeableJPanel{

    private final ThemeableJFXPanel jfxPanel = new ThemeableJFXPanel(){

        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
            super.paintComponent(g);

        };
    };
    private final JPanel loadingPanel = new JPanel();
    WebView webView;
    public TwitchChat() {

        jfxPanel.setOpaque(false);
        jfxPanel.setVisible(false);

        loadingPanel.setOpaque(false);

        setLayout(new GridBagLayout());



        ThemeableJLabel loadingLabel = new ThemeableJLabel(){{
            setText("Waiting for Twitch, Chat Loading...");
            setForeground("foreground");
        }};

        loadingLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

        LoadingCircle loadingCircle = new LoadingCircle();

        loadingPanel.add(loadingCircle);
        loadingPanel.add(Box.createHorizontalStrut(10));
        loadingPanel.add(loadingLabel);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(loadingPanel, gbc);

        Platform.runLater(() -> {
            webView = new WebView();
            jfxPanel.setScene(new Scene(webView));
        });

        setBackground("background");
        setPreferredSize(new Dimension(400, 600));
    }



    public void loadChat(String username){

        Platform.runLater(() -> {
            webView.getEngine().load("https://www.twitch.tv/popout/" + username + "/chat?parent=icelz.s3.amazonaws.com&darkpopout");
            webView.setContextMenuEnabled(false);
            //doesn't work anymore, need an up to date webview so I can support logging in.
            String hideElementsCSS =
                    ".Layout-sc-1xcs6mc-0.GAUVP, .Layout-sc-1xcs6mc-0.knyTGL, .ScCoreButton-sc-ocjdkq-0.hZACqf{\n" +
                            "  display: none !important;\n" +
                            "}";

            if(webView.getEngine().getLocation().toLowerCase().startsWith("https://www.twitch.tv/popout/")) {

                webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {

                        jfxPanel.setVisible(true);
                        remove(loadingPanel);
                        revalidate();
                        setLayout(new BorderLayout(0,0));
                        add(jfxPanel);
                        Document doc = webView.getEngine().getDocument();
                        Element styleNode = doc.createElement("style");
                        Text styleContent = doc.createTextNode(hideElementsCSS);
                        styleNode.appendChild(styleContent);
                        doc.getDocumentElement().getElementsByTagName("head").item(0).appendChild(styleNode);
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
                            e.printStackTrace();
                        }
                        //prevent from opening in webView
                        return null;
                    }
            );
        });

        jfxPanel.setBackground("background");
    }

}
