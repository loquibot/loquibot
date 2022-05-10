package com.alphalaneous;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TTS {

    public static void test(String text) {
        Sounds.playSound("https://api.streamelements.com/kappa/v2/speech?voice=Brian&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8), false, true, false, true);
    }
}
