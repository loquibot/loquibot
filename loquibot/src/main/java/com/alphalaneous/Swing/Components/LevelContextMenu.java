package com.alphalaneous.Swing.Components;

import com.alphalaneous.RequestFunctions;
import com.alphalaneous.Tabs.RequestsTab;

public class LevelContextMenu extends ContextMenu {

    public LevelContextMenu(int levelPos) {

        boolean isYouTube = RequestsTab.getRequest(levelPos).getLevelData().isYouTube();

        addButton(new ContextButton("Remove", () -> RequestFunctions.skipFunction(levelPos, false)));
        addButton(new ContextButton("Copy", () -> RequestFunctions.copyFunction(levelPos)));
        addButton(new ContextButton("Block ID", () -> RequestFunctions.blockFunction(levelPos)));
        //addButton(new ContextButton("Report", () -> RequestFunctions.reportFunction(levelPos)));
        //addButton(new ContextButton("Block User", () -> RequestFunctions.blockFunction(levelPos)));
        //addButton(new ContextButton("Block Creator", () -> RequestFunctions.blockFunction(levelPos)));
        if(!isYouTube) addButton(new ContextButton("Moderation", () -> RequestsTab.showModPane(levelPos)));
        addButton(new ContextButton("View in GDBrowser", () -> RequestFunctions.openGDBrowser(levelPos)));
    }
}
