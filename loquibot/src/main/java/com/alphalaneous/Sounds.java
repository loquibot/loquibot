package com.alphalaneous;

import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import javazoom.jl.player.Player;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Sounds {

	static HashMap<String, Sound> sounds = new HashMap<>();
	static HashMap<String, Player> cachedSoundDownloads = new HashMap<>();


	public static void playSound(String location, boolean restart, boolean overlap) {
		if (sounds.size() <= 5 && (!sounds.containsKey(location) || overlap)) {
			new Sound(location, true, false).playSound();
		} else if (sounds.containsKey(location) && restart) {
			sounds.get(location).stopSound();
			new Sound(location, true, false).playSound();
		}
	}

	public static void playSound(String location, boolean restart, boolean overlap, boolean isFile, boolean isURL) {


		if (sounds.size() <= 5 && (!sounds.containsKey(location) || overlap)) {
			new Sound(location, isFile, isURL).playSound();
		} else if (sounds.containsKey(location) && restart) {
			System.out.println(restart);
			sounds.get(location).stopSound();
			new Sound(location, isFile, isURL).playSound();
		}
	}

	public static void stopSound(String location) {
		sounds.get(location).stopSound();
	}



	@SuppressWarnings("rawtypes")
	public static void stopAllSounds() {

		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			((Sound) ((Map.Entry) stringSoundEntry).getValue()).stopSound();
		}
	}

	public static class Sound {

		String location;
		boolean complete = false;
		boolean isFile;
		boolean isURL;
		Player mp3player;

		public Sound(String location, boolean isFile, boolean isURL) {
			this.location = location;
			this.isFile = isFile;
			this.isURL = isURL;
			Sounds.sounds.put(location, this);
		}

		public void playSound() {
			new Thread(() -> {
				try {
					if(Window.getWindow().isVisible() || Settings.getSettings("playSoundsWhileHidden").asBoolean()) {
						BufferedInputStream inp;
						if (isURL) {
							inp = new BufferedInputStream(new URL(location).openStream());
						} else if (isFile) {
							inp = new BufferedInputStream(new FileInputStream(location));
						} else {
							inp = new BufferedInputStream(BotHandler.class
									.getResource(location).openStream());
						}
						mp3player = new Player(inp);
						mp3player.play();
					}

				} catch (Exception f) {
					f.printStackTrace();
					DialogBox.showDialogBox("Error!", f.toString(), "There was an error playing the sound!", new String[]{"OK"});

				}
				complete = true;
				Sounds.sounds.remove(location, this);
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

