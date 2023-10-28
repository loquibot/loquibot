package com.alphalaneous.Audio;

import com.alphalaneous.SettingsHandler;
import com.alphalaneous.Utilities.LimitedList;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;
import javazoom.jl.player.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

	public static void playSound(String location, boolean restart, boolean overlap) {
		playSound(location, restart, overlap, true, false);
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

	public static void playSound(String location, boolean restart, boolean overlap, boolean isFile, boolean isURL) {
		new Sound(location, isFile, isURL, overlap);
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
		SoundEngine mp3player;

		public Sound(String location, boolean isFile, boolean isURL, boolean overlap) {
			this.location = location;
			this.isFile = isFile;
			this.isURL = isURL;
			this.overlap = overlap;
			Sounds.sounds.put(UUID, this);
		}

		public void playSound() {
			if(playedSounds.contains(UUID)) return;
			new Thread(() -> {
				try {
					if(Window.isVisible() || SettingsHandler.getSettings("playSoundsWhileHidden").asBoolean()) {
						BufferedInputStream inp;

						if (isURL) {
							inp = new BufferedInputStream(new URL(location).openStream());
						} else if (isFile) {
							if(Files.exists(Paths.get(location))) {
								inp = new BufferedInputStream(new FileInputStream(location));
							} else {
                                inp = null;
                            }
                        } else {
							inp = new BufferedInputStream(Objects.requireNonNull(Sounds.class
									.getResource(location)).openStream());
						}

						if(inp != null) {
							playedSounds.add(UUID);
							mp3player = new SoundEngine(inp);
							if(SettingsHandler.getSettings("soundVolume").exists()){
								mp3player.setVolume(SettingsHandler.getSettings("soundVolume").asInteger());
							}
							mp3player.play();
							inp.close();

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
			if(mp3player != null) {
				mp3player.close();
			}
			complete = true;
		}
	}
}

