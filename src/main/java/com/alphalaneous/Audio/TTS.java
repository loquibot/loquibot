package com.alphalaneous.Audio;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TTS {

    public static void playTTS(String text, boolean overlap, String voice) {
        if (!text.trim().equalsIgnoreCase("")) {
            Sounds.playSound("https://api.streamelements.com/kappa/v2/speech?voice=" + voice + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8), false, overlap, false, true);
        }
    }

    public static void runTTS(String data, boolean overlap){
        String finalData = data;

        String voice = finalData.split(" ")[0];
        String voiceValue = "Brian";
        if(voice.startsWith("voice=")){
            finalData = finalData.substring(voice.length());
            voiceValue = voice.substring("voice=".length());
        }

        String finalData1 = finalData;
        String finalVoiceValue = voiceValue;
        new Thread(() -> TTS.playTTS(finalData1, overlap, finalVoiceValue)).start();
    }

}
