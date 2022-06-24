package com.alphalaneous;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TTS {

    public static void playTTS(String text, boolean overlap) {
        if (!text.trim().equalsIgnoreCase("")) {
            Sounds.playSound("https://api.streamelements.com/kappa/v2/speech?voice=Brian&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8), false, overlap, false, true);
        }
    }
}
