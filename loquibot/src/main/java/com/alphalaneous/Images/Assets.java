package com.alphalaneous.Images;


import com.alphalaneous.Main;
import com.alphalaneous.Utilities;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Assets {

	public static HashMap<String, ImageIcon> difficultyIconsNormal = new HashMap<>();
	public static HashMap<String, ImageIcon> difficultyIconsFeature = new HashMap<>();
	public static HashMap<String, ImageIcon> difficultyIconsEpic = new HashMap<>();

	public static HashMap<String, ImageIcon> difficultyIconsNormalLarge = new HashMap<>();
	public static HashMap<String, ImageIcon> difficultyIconsFeatureLarge = new HashMap<>();
	public static HashMap<String, ImageIcon> difficultyIconsEpicLarge = new HashMap<>();
	public static HashMap<String, ImageIcon> lengthIcons = new HashMap<>();

	public static ImageIcon verifiedCoin;
	public static ImageIcon loquibot;
	public static ImageIcon loquibotLarge;
	public static ImageIcon music;
	public static ImageIcon unverifiedCoin;
	public static ImageIcon Alphalaneous;
	public static ImageIcon settings;
	public static ImageIcon channelPoints;
	public static ImageIcon commands;
	public static ImageIcon actions;
	public static ImageIcon requests;
	public static ImageIcon loading;
	public static ImageIcon YouTube;
	public static ImageIcon Twitch;
	public static ImageIcon Kick;
	public static ImageIcon KickText;


	public static ImageIcon YouTubeLarge;
	public static ImageIcon TwitchLarge;
	public static ImageIcon KickLarge;

	public static ImageIcon tutorial;

	private static final int size = 30;
	private static final int largeSize = 50;

	public static void load() {

		ExecutorService es = Executors.newCachedThreadPool();

		es.execute(new	Thread(() -> verifiedCoin = loadStartAsset("GDAssets/verifiedCoin.png", 15, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> unverifiedCoin = loadStartAsset("GDAssets/unverifiedCoin.png", 15, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> loquibot = loadStartAsset("Icons/windowIcon.png", 40, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> loquibotLarge = loadStartAsset("Icons/loqui.png", 200, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> music = loadStartAsset("Icons/music.png", 50, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> Alphalaneous = loadStartAsset("Icons/Alphalaneous.png", 80, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> settings = loadStartAsset("Icons/settings.png", 30, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> commands = loadStartAsset("Icons/chat.png", 30, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> actions = loadStartAsset("Icons/actions.png", 30, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> channelPoints = loadStartAsset("Icons/channelPoint.png", 30, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> requests = loadStartAsset("Icons/gd.png", 30, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> loading = loadStartAsset("Icons/loading.gif", 30, Scalr.Method.QUALITY)));
		es.execute(new	Thread(() -> YouTube = loadStartAsset("Icons/YouTube.png", 16, Scalr.Method.BALANCED)));
		es.execute(new	Thread(() -> Twitch = loadStartAsset("Icons/Twitch.png", 16, Scalr.Method.BALANCED)));
		es.execute(new	Thread(() -> Kick = loadStartAsset("Icons/Kick.png", 16, Scalr.Method.BALANCED)));
		es.execute(new	Thread(() -> KickText = loadStartAsset("Icons/KickText.png", 70, Scalr.Method.BALANCED)));
		es.execute(new	Thread(() -> YouTubeLarge = loadStartAsset("Icons/YouTube.png", 40, Scalr.Method.BALANCED)));
		es.execute(new	Thread(() -> TwitchLarge = loadStartAsset("Icons/Twitch.png", 40, Scalr.Method.BALANCED)));
		es.execute(new	Thread(() -> KickLarge = loadStartAsset("Icons/Kick.png", 40, Scalr.Method.BALANCED)));

		es.execute(new	Thread(() -> tutorial = loadStartAsset("tutorial.png", 450, null)));

		es.shutdown();
		while (!es.isTerminated()) Utilities.sleep(1);
	}

	public static ImageIcon loadStartAsset(String path, int size, Scalr.Method scalingMethod){
		try {
			if(scalingMethod == null){
				return new ImageIcon(Scalr.resize(ImageIO
						.read(Objects.requireNonNull(Main.class.getClassLoader()
								.getResource(path))),size, Scalr.OP_ANTIALIAS));
			}
			else{
				return new ImageIcon(Scalr.resize(ImageIO
						.read(Objects.requireNonNull(Main.class.getClassLoader()
								.getResource(path))),scalingMethod,size, Scalr.OP_ANTIALIAS));
			}

		} catch (Exception e) {
			System.out.println("Failed to load " + path);
		}
		return null;
	}

	public static void loadAsset(String value, String path, HashMap<String, ImageIcon> save){
		es.execute(
		new Thread(() -> {
			try {
				save.put(value, new ImageIcon(Scalr.resize(ImageIO
						.read(Objects.requireNonNull(Main.class.getClassLoader()
								.getResource(path))), Scalr.Method.QUALITY, 100, Scalr.OP_ANTIALIAS)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
	}

	private static ExecutorService es = Executors.newCachedThreadPool();

	public static void loadAsset(String value, String path, HashMap<String, ImageIcon> save, HashMap<String, ImageIcon> saveLarge){
		es.execute(
		new Thread(() -> {
			try {
				save.put(value, new ImageIcon(Scalr.resize(ImageIO
						.read(Objects.requireNonNull(Main.class.getClassLoader()
								.getResource(path))), Scalr.Method.QUALITY, size)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		if(saveLarge != null) {
			es.execute(
			new Thread(() -> {
				try {
					saveLarge.put(value, new ImageIcon(Scalr.resize(ImageIO
							.read(Objects.requireNonNull(Main.class.getClassLoader()
									.getResource(path))), Scalr.Method.QUALITY, largeSize, Scalr.OP_ANTIALIAS)));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}));
		}
	}

	public static void loadAssets() {

		String[] difficulties = {"NA", "auto", "easy", "normal", "hard", "harder", "insane", "easy demon", "medium demon",
				"hard demon", "insane demon", "extreme demon"};
		for (String difficulty : difficulties) {

			loadAsset(difficulty, "DifficultyIcons/Normal/" + difficulty + ".png", difficultyIconsNormal, difficultyIconsNormalLarge);
			if (!difficulty.equalsIgnoreCase("NA")) {
				loadAsset(difficulty, "DifficultyIcons/Featured/" + difficulty + ".png", difficultyIconsFeature, difficultyIconsFeatureLarge);
				loadAsset(difficulty, "DifficultyIcons/Epic/" + difficulty + ".png", difficultyIconsEpic, difficultyIconsEpicLarge);
			}
		}
		String[] lengths = {"Tiny", "Short", "Medium", "Long", "XL"};

		for(String length : lengths){
			loadAsset(length, "LengthIcons/" + length + ".png", lengthIcons);
		}
		es.shutdown();
		while (!es.isTerminated()) Utilities.sleep(1);
	}
}
