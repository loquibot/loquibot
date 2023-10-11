package com.alphalaneous.Audio;

import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import javazoom.jl.decoder.Equalizer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;
import javazoom.jl.player.advanced.AdvancedPlayer;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Sounds {


	static ConcurrentHashMap<String, Sound> sounds = new ConcurrentHashMap<>();
	static {
		querySound();
		queryOverlapSound();
	}

	public static void playSound(String location, boolean restart, boolean overlap) {
		playSound(location, restart, overlap, true, false);
	}

	static List<Object> playingSoundsOverlap = Collections.synchronizedList(new ArrayList<Object>());
	public static void queryOverlapSound(){
		new Thread(() -> {
			while(true){
				for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
					if(stringSoundEntry.getValue().overlap) {
						if(!playingSoundsOverlap.contains(stringSoundEntry)){
							stringSoundEntry.getValue().playSound();
							playingSoundsOverlap.add(stringSoundEntry);
						}
					}
				}
				Utilities.sleep(10);
			}
		}).start();
	}


	public static void querySound(){
		new Thread(() -> {
			while(true){
				for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
					if(!stringSoundEntry.getValue().overlap) {
						if (!stringSoundEntry.getValue().complete) {
							stringSoundEntry.getValue().playSound();
							while (!stringSoundEntry.getValue().complete) {
								Utilities.sleep(10);
							}
							Utilities.sleep(1000);
						}
					}
				}
				Utilities.sleep(10);
			}
		}).start();
	}

	public static void playSound(String location, boolean restart, boolean overlap, boolean isFile, boolean isURL) {
		new Sound(location, isFile, isURL, overlap);
	}

	public static void stopSound(String location) {
		sounds.get(location).stopSound();
	}

	public static String getLocationID(String location){
		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			if (stringSoundEntry.getValue().location.equalsIgnoreCase(location)) {
				return stringSoundEntry.toString();
			}
		}
		return null;
	}

	public static boolean contains(String location){
		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			if (stringSoundEntry.getValue().location.equalsIgnoreCase(location)) {
				return true;
			}
		}
		return false;
	}

	public static void stopAllSounds() {

		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			stringSoundEntry.getValue().stopSound();
		}
		Sounds.sounds.clear();
	}

	public static class Sound {

		String UUID = java.util.UUID.randomUUID().toString().replace("-", "");
		String location;
		boolean complete = false;
		boolean isFile;
		boolean isURL;
		boolean overlap;
		Player mp3player;



		public Sound(String location, boolean isFile, boolean isURL, boolean overlap) {
			this.location = location;
			this.isFile = isFile;
			this.isURL = isURL;
			this.overlap = overlap;
			Sounds.sounds.put(UUID, this);
		}

		public void playSound() {
			new Thread(() -> {
				try {
					if(Window.getWindow().isVisible() || SettingsHandler.getSettings("playSoundsWhileHidden").asBoolean()) {
						BufferedInputStream inp;
						if (isURL) {
							inp = new BufferedInputStream(new URL(location).openStream());
						} else if (isFile) {
							inp = new BufferedInputStream(new FileInputStream(location));
						} else {
							inp = new BufferedInputStream(Sounds.class
									.getResource(location).openStream());
						}

						mp3player = new Player(inp);

						mp3player.play();
						inp.close();
					}

				} catch (Exception f) {
					Main.logger.error(f.getLocalizedMessage(), f);
				}
				complete = true;
				Main.logger.info("Sound Complete: " + UUID);
				if(overlap) {
					Utilities.sleep(5000);
					playingSoundsOverlap.remove(this);
				}
				//Sounds.sounds.remove(UUID, this);
			}).start();

		}

		public void stopSound() {
			if(mp3player != null) {
				mp3player.close();
			}
			complete = true;
		}
	}
}

