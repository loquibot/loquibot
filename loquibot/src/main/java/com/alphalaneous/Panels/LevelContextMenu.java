package com.alphalaneous.Panels;

import com.alphalaneous.RequestFunctions;
import com.alphalaneous.Windows.Window;

public class LevelContextMenu extends ContextMenu {

    public LevelContextMenu(int levelPos) {

        addButton(new ContextButton("Remove", () -> RequestFunctions.skipFunction(levelPos)));
        addButton(new ContextButton("Copy", () -> RequestFunctions.copyFunction(levelPos)));
        addButton(new ContextButton("Block ID", () -> RequestFunctions.blockFunction(levelPos)));
        addButton(new ContextButton("Block User", () -> RequestFunctions.blockFunction(levelPos)));
        addButton(new ContextButton("Block Creator", () -> RequestFunctions.blockFunction(levelPos)));
        addButton(new ContextButton("Moderation", () -> Window.showModPane(levelPos)));
        addButton(new ContextButton("View in GDBrowser", () -> RequestFunctions.openGDBrowser(levelPos)));
    }
}
