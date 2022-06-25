package com.alphalaneous;

import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.player.Player;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Sounds {

	public static HashMap<String, Sound> sounds = new HashMap<>();
	static HashMap<String, Player> cachedSoundDownloads = new HashMap<>();


	public static void playSound(String location, boolean restart, boolean overlap) {
		playSound(location, restart,  overlap, true, false);
	}

	public static void playSound(String location, boolean restart, boolean overlap, boolean isFile, boolean isURL) {

		if(!overlap){
			while(true){
				Utilities.sleep(1);
				boolean isPlaying = false;
				for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
					if (!((Sound) ((Map.Entry) stringSoundEntry).getValue()).complete) {
						isPlaying = true;
						break;
					}
				}
				if(!isPlaying){
					break;
				}
			}
		}
		if (sounds.size() <= 5 && (!contains(location) || overlap)) {
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
			if (!((Sound) ((Map.Entry) stringSoundEntry).getValue()).location.equalsIgnoreCase(location)) {
				return stringSoundEntry.toString();
			}
		}
		return null;
	}

	public static boolean contains(String location){
		for (Map.Entry<String, Sound> stringSoundEntry : sounds.entrySet()) {
			if (!((Sound) ((Map.Entry) stringSoundEntry).getValue()).location.equalsIgnoreCase(location)) {
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
	}

	public static class Sound {

		String location;
		boolean complete = false;
		boolean isFile;
		boolean isURL;
		MediaPlayer player;


		public Sound(String location, boolean isFile, boolean isURL) {
			this.location = location;
			this.isFile = isFile;
			this.isURL = isURL;
			String UUID = java.util.UUID.randomUUID().toString().replace("-", "");
			Sounds.sounds.put(UUID, this);
		}

		public void playSound() {
			new Thread(() -> {
				try {
					if(Window.getWindow().isVisible() || Settings.getSettings("playSoundsWhileHidden").asBoolean()) {

						String locationA = location;
						if(isFile) locationA = "file://" + location;
						else if(!isURL) locationA = Objects.requireNonNull(Main.class.getResource(location)).toURI().toString();

						player = new MediaPlayer(new Media(locationA));
						if(Settings.getSettings("volume").exists()) player.setVolume(Settings.getSettings("volume").asDouble());
						else player.setVolume(1);

						player.setOnEndOfMedia(() -> {
							System.out.println("complete");
							complete = true;
						});

						player.play();
					}

				} catch (Exception f) {
					f.printStackTrace();
					DialogBox.showDialogBox("Error!", f.toString(), "There was an error playing the sound!", new String[]{"OK"});

				}
				Sounds.sounds.remove(location, this);
			}).start();

		}

		public void stopSound() {
			if(player != null) {
				player.stop();
			}
			complete = true;
		}
	}
}

