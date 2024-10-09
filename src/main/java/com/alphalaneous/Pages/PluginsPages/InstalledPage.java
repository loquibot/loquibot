package com.alphalaneous.Pages.PluginsPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Pages.PluginsPage;

import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InstalledPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 11)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        PluginsPage.addPage("$INSTALLED_TITLE$", page, InstalledPage::load, null);
    }

    public static void load(){

        buttonPanel.removeAll();
        //todo show installed
        buttonPanel.updateUI();
    }
}
