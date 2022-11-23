package com.alphalaneous.Audio;

import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Sounds {

	static HashMap<String, Sound> sounds = new HashMap<>();


	public static void playSound(String location, boolean restart, boolean overlap) {
		playSound(location, restart, overlap, true, false);
	}

	public static void playSound(String location, boolean restart, boolean overlap, boolean isFile, boolean isURL) {

		if (!contains(location) || overlap) {
			new Sound(location, isFile, isURL).playSound();
		} else if (contains(location) && restart) {
			sounds.get(getLocationID(location)).stopSound();
			new Sound(location, isFile, isURL).playSound();
		}
	}

	public static void stopSound(String location) {
		sounds.get(location).stopSound();
	}

	public static String getLocationID(String location){
		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			if (((Sound) ((Map.Entry) stringSoundEntry).getValue()).location.equalsIgnoreCase(location)) {
				return stringSoundEntry.toString();
			}
		}
		return null;
	}

	public static boolean contains(String location){
		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			if (((Sound) ((Map.Entry) stringSoundEntry).getValue()).location.equalsIgnoreCase(location)) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("rawtypes")
	public static void stopAllSounds() {

		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			((Sound) ((Map.Entry) stringSoundEntry).getValue()).stopSound();
		}
		Sounds.sounds.clear();
	}

	public static class Sound {

		String UUID = java.util.UUID.randomUUID().toString().replace("-", "");
		String location;
		boolean complete = false;
		boolean isFile;
		boolean isURL;
		Player mp3player;

		public Sound(String location, boolean isFile, boolean isURL) {
			this.location = location;
			this.isFile = isFile;
			this.isURL = isURL;
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
					f.printStackTrace();
					DialogBox.showDialogBox("Error!", f.toString(), "There was an error playing the sound!", new String[]{"OK"});

				}
				complete = true;
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

