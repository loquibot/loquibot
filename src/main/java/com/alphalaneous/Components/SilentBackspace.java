package com.alphalaneous.Components;

import javax.swing.text.*;
import java.awt.event.ActionEvent;

public class SilentBackspace extends TextAction {

    //Stops sound playing when cannot backspace

    public SilentBackspace() {
        super(DefaultEditorKit.deletePrevCharAction);
    }

    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if ((target != null) && (target.isEditable())) {
            try {
                Document doc = target.getDocument();
                Caret caret = target.getCaret();
                int dot = caret.getDot();
                int mark = caret.getMark();
                if (dot != mark) {
                    doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
                } else if (dot > 0) {
                    int delChars = 1;

                    if (dot > 1) {
                        String dotChars = doc.getText(dot - 2, 2);
                        char c0 = dotChars.charAt(0);
                        char c1 = dotChars.charAt(1);

                        if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
                                c1 >= '\uDC00' && c1 <= '\uDFFF') {
                            delChars = 2;
                        }
                    }
                    doc.remove(dot - delChars, delChars);
                }
            } catch (BadLocationException ignored) {
            }
        }
    }
}