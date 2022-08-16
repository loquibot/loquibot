package com.alphalaneous.Swing;

import com.alphalaneous.Main;
import com.alphalaneous.Windows.Window;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class BasicBrowserWindow extends JDialog {

    public BasicBrowserWindow(String link){
        Platform.runLater(() -> new Thread(() -> Platform.runLater(() -> {

            setTitle("loquibot - Browser");
            setIconImages(Main.getIconImages());
            setAlwaysOnTop(true);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

            WebView webView = new WebView();

            webView.getEngine().setOnError(event -> System.out.println(event.getMessage()));
            webView.getEngine().setOnAlert(event -> System.out.println(event.getData()));

            JFXPanel panel1 = new JFXPanel();
            Scene scene = new Scene(webView);
            panel1.setScene(scene);

            setLayout(new BorderLayout());

            setSize(550, 750);
            setLocationRelativeTo(Window.getWindow());

            add(panel1);
            setVisible(true);


            webView.getEngine().load(link);
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    setTitle("loquibot - " + webView.getEngine().getTitle());
                }
            });

        })).start());
    }
    public void close(){
        setVisible(false);
        dispose();
    }

}
