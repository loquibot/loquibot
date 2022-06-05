package com.alphalaneous;

import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.Windows.DialogBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TutorialWindow{

    public static void show(){

        JPanel panel = new JPanel();
        panel.setBackground(Defaults.COLOR3);
        panel.setBounds(0,0,500,500);
        panel.setLayout(null);


        JFXPanel panel1 = new JFXPanel();

        panel1.setBounds(10,220,480,270);

        Platform.runLater(() -> {
            WebView webView = new WebView();

            GetInternalFiles getInternalFiles = new GetInternalFiles("/");
            FileList files = getInternalFiles.getFiles();


            String url = files.getFile("Video.html").getPath().toString();
            System.out.println(url);

            webView.getEngine().load("file://"+ url);

            VBox vBox = new VBox(webView);
            Scene scene = new Scene(vBox, 480, 270);


            panel1.setScene(scene);

            panel.add(panel1);

            DialogBox.showDialogBox(panel,true);
        });

    }
}
