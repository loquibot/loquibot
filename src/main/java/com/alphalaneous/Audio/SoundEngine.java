package com.alphalaneous.Audio;

import java.io.File;
import java.io.InputStream;

import com.alphalaneous.Interfaces.Function;

import javax.sound.sampled.*;
class SoundEngine{

    private final InputStream inputStream;
    private float gain = 0;

    public SoundEngine(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void play() {

        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream mp3In = AudioSystem.getAudioInputStream(inputStream);
            AudioFormat mp3Format = mp3In.getFormat();
            AudioFormat pcmFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    mp3Format.getSampleRate(),
                    16,
                    mp3Format.getChannels(),
                    16 * mp3Format.getChannels() / 8,
                    mp3Format.getSampleRate(),
                    mp3Format.isBigEndian()
            );
            AudioInputStream pcmIn = AudioSystem.getAudioInputStream(pcmFormat, mp3In);

            clip.open(pcmIn);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gain);
            clip.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){

    }

    public void setVolume(int volume){

        int finalVolume = Math.max(0, Math.min(volume, 120));
        this.gain = (float) (20 * Math.log((double) finalVolume /100));
    }
}