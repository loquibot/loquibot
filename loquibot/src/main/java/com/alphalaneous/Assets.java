package com.alphalaneous;


import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

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

	private static final int size = 30;
	private static final int largeSize = 50;


	static {
		try {
			verifiedCoin = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("GDAssets/verifiedCoin.png"))), Scalr.Method.QUALITY,15));
			unverifiedCoin = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("GDAssets/unverifiedCoin.png"))),Scalr.Method.QUALITY,15));

			loquibot = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/windowIcon.png"))),Scalr.Method.QUALITY,40));
			loquibotLarge = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/loqui.png"))),Scalr.Method.QUALITY,200, Scalr.OP_ANTIALIAS));
			music = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/music.png"))),Scalr.Method.QUALITY,50));
			Alphalaneous = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/Alphalaneous.png"))),Scalr.Method.QUALITY,80));
			settings = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/settings.png"))),Scalr.Method.QUALITY,30));
			commands = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/chat.png"))),Scalr.Method.QUALITY,30));
			actions = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/actions.png"))),Scalr.Method.QUALITY,30));
			channelPoints = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/channelPoint.png"))),Scalr.Method.QUALITY,30));
			requests = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/gd.png"))),Scalr.Method.QUALITY,30));
			loading = new ImageIcon(Scalr.resize(ImageIO
					.read(Objects.requireNonNull(Main.class.getClassLoader()
							.getResource("Icons/loading.gif"))),Scalr.Method.BALANCED,30));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void loadAssets() {

			try {
				String[] difficulties = {"NA", "auto", "easy", "normal", "hard", "harder", "insane", "easy demon", "medium demon",
						"hard demon", "insane demon", "extreme demon"};
				for (String difficulty : difficulties) {
					difficultyIconsNormal.put(difficulty, new ImageIcon(Scalr.resize(ImageIO
							.read(Objects.requireNonNull(Main.class.getClassLoader()
									.getResource("DifficultyIcons/Normal/" + difficulty + ".png"))),Scalr.Method.QUALITY, size)));
					difficultyIconsNormalLarge.put(difficulty, new ImageIcon(Scalr.resize(ImageIO
							.read(Objects.requireNonNull(Main.class.getClassLoader()
									.getResource("DifficultyIcons/Normal/" + difficulty + ".png"))),Scalr.Method.QUALITY, largeSize, Scalr.OP_ANTIALIAS)));

					if (!difficulty.equalsIgnoreCase("NA")) {
						difficultyIconsFeature.put(difficulty, new ImageIcon(Scalr.resize(ImageIO
								.read(Objects.requireNonNull(Main.class.getClassLoader()
										.getResource("DifficultyIcons/Featured/" + difficulty + ".png"))),Scalr.Method.QUALITY, size)));
						difficultyIconsFeatureLarge.put(difficulty, new ImageIcon(Scalr.resize(ImageIO
								.read(Objects.requireNonNull(Main.class.getClassLoader()
										.getResource("DifficultyIcons/Featured/" + difficulty + ".png"))),Scalr.Method.QUALITY, largeSize, Scalr.OP_ANTIALIAS)));

						difficultyIconsEpic.put(difficulty, new ImageIcon(Scalr.resize(ImageIO
								.read(Objects.requireNonNull(Main.class.getClassLoader()
										.getResource("DifficultyIcons/Epic/" + difficulty + ".png"))),Scalr.Method.QUALITY, size)));
						difficultyIconsEpicLarge.put(difficulty, new ImageIcon(Scalr.resize(ImageIO
								.read(Objects.requireNonNull(Main.class.getClassLoader()
										.getResource("DifficultyIcons/Epic/" + difficulty + ".png"))),Scalr.Method.QUALITY, largeSize, Scalr.OP_ANTIALIAS)));
					}
				}
				String[] lengths = {"Tiny", "Short", "Medium", "Long", "XL"};

				for(String length : lengths){
					lengthIcons.put(length, new ImageIcon(ImageIO
							.read(Objects.requireNonNull(Main.class.getClassLoader()
									.getResource("LengthIcons/" + length + ".png")))));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}


	}
}
