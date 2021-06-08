package com.alphalaneous;

import com.alphalaneous.Panels.LevelsPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Assets {

	public static HashMap<String, ImageIcon> difficultyIconsNormal = new HashMap<>();
	public static HashMap<String, ImageIcon> difficultyIconsFeature = new HashMap<>();
	public static HashMap<String, ImageIcon> difficultyIconsEpic = new HashMap<>();
	public static ImageIcon verifiedCoin;
	public static ImageIcon loquibot;
	public static ImageIcon loquibotLarge;
	public static ImageIcon unverifiedCoin;
	public static ImageIcon Alphalaneous;
	public static ImageIcon EncodedLua;
	public static ImageIcon discord;
	public static ImageIcon donate;
	public static ImageIcon settings;
	public static ImageIcon channelPoints;
	public static ImageIcon commands;
	public static ImageIcon requests;
	private static final int width = 30;
	private static final int height = 30;

	static {
		try {
			verifiedCoin = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("GDAssets/verifiedCoin.png")))
					.getScaledInstance(15, 15, Image.SCALE_SMOOTH));
			unverifiedCoin = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("GDAssets/unverifiedCoin.png")))
					.getScaledInstance(15, 15, Image.SCALE_SMOOTH));
			loquibot = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/windowIcon.png")))
					.getScaledInstance(40, 40, Image.SCALE_SMOOTH));
			loquibotLarge = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/loqui.png")))
					.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
			Alphalaneous = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/Alphalaneous.png")))
					.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
			EncodedLua = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/EncodedLua.png")))
					.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
			discord = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/discord.png")))
					.getScaledInstance(25, 18, Image.SCALE_SMOOTH));
			donate = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/donate.png")))
					.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
			settings = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/settings.png")))
					.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
			commands = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/chat.png")))
					.getScaledInstance(23, 23, Image.SCALE_SMOOTH));
			channelPoints = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/channelPoint.png")))
					.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
			requests = new ImageIcon(ImageIO
					.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
							.getResource("Icons/gd.png")))
					.getScaledInstance(23, 23, Image.SCALE_SMOOTH));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void loadAssets() {

			try {
				String[] difficulties = {"NA", "auto", "easy", "normal", "hard", "harder", "insane", "easy demon", "medium demon",
						"hard demon", "insane demon", "extreme demon"};
				for (String difficulty : difficulties) {
					difficultyIconsNormal.put(difficulty, new ImageIcon(ImageIO
							.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
									.getResource("DifficultyIcons/Normal/" + difficulty + ".png")))
							.getScaledInstance(width, height, Image.SCALE_SMOOTH)));

					if (!difficulty.equalsIgnoreCase("NA")) {
						difficultyIconsFeature.put(difficulty, new ImageIcon(ImageIO
								.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
										.getResource("DifficultyIcons/Featured/" + difficulty + ".png")))
								.getScaledInstance(width, height, Image.SCALE_SMOOTH)));

						difficultyIconsEpic.put(difficulty, new ImageIcon(ImageIO
								.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
										.getResource("DifficultyIcons/Epic/" + difficulty + ".png")))
								.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}


	}
}
