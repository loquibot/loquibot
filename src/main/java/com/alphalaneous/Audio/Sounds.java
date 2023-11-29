package com.alphalaneous.Audio;

import com.alphalaneous.Enums.SoundType;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.LimitedList;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Sounds {

	static ConcurrentHashMap<String, Sound> sounds = new ConcurrentHashMap<>();
	static List<String> playedSounds = Collections.synchronizedList(new LimitedList<>(2048));

	static List<Object> playingSoundsOverlap = Collections.synchronizedList(new ArrayList<>());

	static {
		querySound();
		queryOverlapSound();
	}

	public static void playSound(String location, boolean restart, boolean overlap, SoundType soundType) {
		playSound(location, restart, overlap, true, false, soundType);
	}


	public static void queryOverlapSound(){
		new Thread(() -> {
			while(true){
				sounds.forEach((u, s) -> {
					if(s.overlap && !playingSoundsOverlap.contains(s)) {
						s.playSound();
						playingSoundsOverlap.add(s);
					}
				});

				Utilities.sleep(10);
			}
		}).start();
	}


	public static void querySound(){
		new Thread(() -> {
			while(true){

				sounds.forEach((u, s) -> {
					if(!s.overlap && !s.complete) {
						s.playSound();
						while (!s.complete) {
							Utilities.sleep(10);
						}
						Utilities.sleep(1000);
					}
				});

				Utilities.sleep(10);
			}
		}).start();
	}

	public static void playSound(String location, boolean restart, boolean overlap, boolean isFile, boolean isURL, SoundType soundType) {
		new Sound(location, isFile, isURL, overlap, soundType);
	}

	public static void stopSound(String location) {
		sounds.get(location).stopSound();
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

		sounds.forEach((u, s) -> s.stopSound());
		Sounds.sounds.clear();
	}

	public static class Sound {

		String UUID = java.util.UUID.randomUUID().toString().replace("-", "");
		String location;
		boolean complete = false;
		boolean isFile;
		boolean isURL;
		boolean overlap;
		SoundEngine soundPlayer;

		SoundType soundType;

		public Sound(String location, boolean isFile, boolean isURL, boolean overlap, SoundType soundType) {
			this.location = location;
			this.isFile = isFile;
			this.isURL = isURL;
			this.overlap = overlap;
			this.soundType = soundType;

			Sounds.sounds.put(UUID, this);
		}

		public void playSound() {
			if(playedSounds.contains(UUID)) return;
			new Thread(() -> {
				try {
					if(Window.isVisible() || SettingsHandler.getSettings("playSoundsWhileHidden").asBoolean()) {
						BufferedInputStream inp = null;

						if (isFile) {
							if(Files.exists(Paths.get(location))) {
								inp = new BufferedInputStream(new FileInputStream(location));
							}

                        } else if (!isURL){
							inp = new BufferedInputStream(Objects.requireNonNull(Sounds.class
									.getResource(location)).openStream());
						}

						if(inp != null || isURL) {
							playedSounds.add(UUID);

							if(isURL) soundPlayer = new SoundEngine(new URL(location));
							else soundPlayer = new SoundEngine(inp);

							switch (soundType){
								case SOUND:
									soundPlayer.setVolume(SettingsHandler.getSettings("soundVolume").asInteger());
									break;
								case TTS:
									soundPlayer.setVolume(SettingsHandler.getSettings("ttsVolume").asInteger());
									break;
							}
							soundPlayer.play();
							//inp.close();

							complete = true;
							Logging.getLogger().info("Sound Complete: " + location + " (" + UUID + ")");
							if(overlap) {
								Utilities.sleep(5000);
								playingSoundsOverlap.remove(this);
							}
							sounds.remove(UUID);
						}
						else{
							Logging.getLogger().info("Sound doesn't exist");
						}
					}
				} catch (Exception f) {
					f.printStackTrace();
				}
			}).start();
		}

		public void stopSound() {
			if(soundPlayer != null) {
				soundPlayer.close();
			}
			complete = true;
		}
	}
}

