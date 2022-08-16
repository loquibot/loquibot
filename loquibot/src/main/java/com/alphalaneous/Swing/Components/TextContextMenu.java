package com.alphalaneous.Swing.Components;

import javax.swing.*;

public class TextContextMenu extends ContextMenu {

    public TextContextMenu(JTextArea area) {

        String selectedText = area.getSelectedText();

        if(selectedText != null) {
            addButton(new ContextButton("Cut", () -> cut(area)));
            addButton(new ContextButton("Copy", () -> copy(area)));
        }
        addButton(new ContextButton("Paste", () -> paste(area)));
    }

    private void cut(JTextArea area){
        area.cut();
        area.requestFocus();
    }
    private void copy(JTextArea area){
        area.copy();
        area.requestFocus();
    }

    private void paste(JTextArea area){
        area.paste();
        area.requestFocus();
    }
}
