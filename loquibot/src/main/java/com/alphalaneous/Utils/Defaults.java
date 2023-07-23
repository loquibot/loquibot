package com.alphalaneous.Utils;

import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.JButtonUI;
import com.alphalaneous.Settings.Personalization;
import com.alphalaneous.Theming.Themes;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public class Defaults {

	public static int screenNum;
	public static final int globalArc = 20;
	public static String saveDirectory;
	public static boolean isLight = false;
	public static boolean isAprilFools;
	static {
		String today = new SimpleDateFormat("MMdd").format(Calendar.getInstance().getTime());
		isAprilFools = today.equalsIgnoreCase("0401");
	}
	public static Color ACCENT = Color.decode(String.valueOf(RegQuery.getColor()));
	public static Color COLOR;
	public static Color COLOR1;
	public static Color COLOR2;
	public static Color COLOR3;
	public static Color COLOR4;
	public static Color COLOR6;
	public static Color COLOR7;
	public static Color FOREGROUND_A;
	public static Color FOREGROUND_B;
	public static Color FOREGROUND_C;
	public static Color OVERLAY;

	public static JButtonUI defaultUI = new JButtonUI();
	public static JButtonUI settingsButtonUI = new JButtonUI();
	public static Color COLOR5;
	public static Font SYMBOLS;
	public static Font SYMBOLSalt;
	public static Font MAIN_FONT;
	private static final String os = (System.getProperty("os.name")).toUpperCase();

	public static boolean isMac(){

		return (os.toLowerCase().contains("mac")) || (os.toLowerCase().contains("darwin"));
	}

	public static Font SEGOE_FONT = new Font("Segoe UI", Font.PLAIN, 20);
	public static HashMap<String, Color> colors = new HashMap<>() {{
		put("foreground", Defaults.FOREGROUND_A);
		put("foreground2", Defaults.FOREGROUND_B);
		put("main", Defaults.COLOR);
		put("sub_main", Defaults.COLOR3);
		put("button", Defaults.COLOR2);
		put("button_hover", Defaults.COLOR5);
		put("top", Defaults.COLOR6);
		put("select", Defaults.COLOR4);
	}};
	public static HashMap<String, Color> defaultColors = new HashMap<>() {{
		put("color", new Color(31, 29, 46));
		put("color1", new Color(47, 44, 66));
		put("color2", new Color(39, 38, 59));
		put("color3", new Color(23, 22, 35));
		put("color4", new Color(45, 42, 66));
		put("color5", new Color(58, 56, 80));
		put("color6", new Color(8, 7, 20));
		put("color7", new Color(161, 161, 250,50));
		put("foreground_a", Color.WHITE);
		put("foreground_b", new Color(165, 165, 165));

	}};

	static {
		if (os.contains("WIN")) {
			File file = FileUtils.getUserDirectory();
			if(Files.exists(Paths.get(file.toString() + "\\.loquibot\\.save"))){
				saveDirectory = file.toString() + "\\.loquibot";

				if(!Files.isDirectory(Paths.get(saveDirectory))) {
					try {
						Files.createDirectory(Paths.get(saveDirectory));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				saveDirectory = System.getenv("APPDATA");
			}
		} else {
			saveDirectory = System.getProperty("user.home") + "/Library/Application Support";
		}
		try {
			screenNum = SettingsHandler.getSettings("monitor").asInteger();
		} catch (Exception e) {
			screenNum = 0;
		}
	}

	static {
		try {
			SYMBOLS = Font.createFont(Font.TRUETYPE_FONT,
					Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("Fonts/webhostinghub-glyphs.ttf")));
			MAIN_FONT = Font.createFont(Font.TRUETYPE_FONT,
					Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("Fonts/Poppins-Regular.ttf")));
			SYMBOLSalt = Font.createFont(Font.TRUETYPE_FONT,
					Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("Fonts/SegoeFluent.ttf")));
		} catch (FontFormatException | IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}


	static String[] preferredFonts = {
			"DejaVu Sans Mono",
			"Microsoft JhengHei",
			"Poppins-Regular",
	};

	private static final String[] fontFamilies = GraphicsEnvironment.
			getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	private static HashMap<String, Font> getCompatibleFonts(String text) {
		HashMap<String, Font> cF = new HashMap<>();
		for (String font : fontFamilies) {
			Font f;
			if(font.equalsIgnoreCase("Poppins-Regular")) f = MAIN_FONT;
			else f = new Font(font, Font.PLAIN, 1);

			if (f.canDisplayUpTo(text) < 0) {
				cF.put(font, f);
			}
		}
		return cF;
	}

	public static Font getPreferredFontForText(String text) {
		HashMap<String, Font> compatibleFonts = getCompatibleFonts(text);
		for (String preferredFont : preferredFonts) {
			Font font;
			if(preferredFont.equalsIgnoreCase("Poppins-Regular")) font = MAIN_FONT;
			else font = compatibleFonts.get(preferredFont);
			if (font != null) {
				return font;
			}
		}
		Set<String> keySet = compatibleFonts.keySet();
		String firstCompatibleFont = keySet.iterator().next();
		return compatibleFonts.get(firstCompatibleFont);
	}

	public static void setDark(){
		setDark(true);
	}
	public static void setLight(){
		setLight(true);
	}
	public static void setCustom(){
		setCustom(true);
	}
	public static void setSystem(){
		setSystem(true);
	}

	public static void setDark(boolean refresh) {
		isLight = false;
		COLOR = new Color(31, 29, 46);
		COLOR1 = new Color(47, 44, 66);
		COLOR2 = new Color(39, 38, 59);
		COLOR3 = new Color(23, 22, 35);
		COLOR4 = new Color(45, 42, 66);
		COLOR5 = new Color(58, 56, 80);
		COLOR6 = new Color(8, 7, 20);
		COLOR7 = new Color(161, 161, 250,50);
		OVERLAY = new Color(30,30,30);
		FOREGROUND_A = Color.WHITE;
		FOREGROUND_B = new Color(165, 165, 165);
		FOREGROUND_C = new Color(255,255,255,150);
		if(refresh) Themes.refreshUI();
	}

	public static void setLight(boolean refresh) {
		isLight = true;
		COLOR = new Color(230, 230, 230);
		COLOR1 = new Color(205, 205, 205);
		COLOR2 = new Color(224, 224, 224);
		COLOR3 = new Color(240, 240, 240);
		COLOR4 = new Color(215, 215, 215);
		COLOR5 = new Color(204, 204, 204);
		COLOR6 = Color.WHITE;
		COLOR7 = new Color(122, 122, 122,50);
		OVERLAY = new Color(255,255,255);
		FOREGROUND_A = Color.BLACK;
		FOREGROUND_B = new Color(114, 114, 114);
		FOREGROUND_C = new Color(0,0,0,150);
		if(refresh) Themes.refreshUI();
	}
	public static void setCustom(boolean refresh) {
		isLight = Themes.getIsLight();
		COLOR = Themes.getThemeSetting("color");
		COLOR1 = Themes.getThemeSetting("color1");
		COLOR2 = Themes.getThemeSetting("color2");
		COLOR3 = Themes.getThemeSetting("color3");
		COLOR4 = Themes.getThemeSetting("color4");
		COLOR5 = Themes.getThemeSetting("color5");
		COLOR6 = Themes.getThemeSetting("color6");
		COLOR7 = Themes.getThemeSetting("color7");
		FOREGROUND_A = Themes.getThemeSetting("foreground_a");
		FOREGROUND_B = Themes.getThemeSetting("foreground_b");
		if(refresh) Themes.refreshUI();
	}

	public static void setSystem(boolean refresh) {
		final int[] prevTheme = new int[1];
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = RegQuery::getTheme;
		Future<Object> future = executor.submit(task);

		try {
			prevTheme[0] = (int) future.get(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			prevTheme[0] = 1;
			future.cancel(true);
		}
		if(SettingsHandler.getSettings("theme").asString().equalsIgnoreCase("SYSTEM_MODE")) {
			if (prevTheme[0] == 0) {
				Defaults.setDark(refresh);
			} else if (prevTheme[0] == 1) {
				Defaults.setLight(refresh);
			}
		}
		else{
			setDark(refresh);
		}

		ExecutorService executorC = Executors.newCachedThreadPool();
		Callable<Object> taskC = RegQuery::getColor;
		Future<Object> futureC = executorC.submit(taskC);
		int color = 0;

		try {
			color = (int) futureC.get(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			futureC.cancel(true);
		}

		if(isMac()){
			ACCENT = new Color(160, 143, 255, 255);
		}
		else {
			ACCENT = Color.decode(String.valueOf(color));
		}
		if(refresh) Themes.refreshUI();

	}
	private static final int[] prevTheme = new int[1];

	public static void initializeThemeInfo(){

			if (SettingsHandler.getSettings("theme").asString().equalsIgnoreCase("LIGHT_MODE")) setLight();
			else if (SettingsHandler.getSettings("theme").asString().equalsIgnoreCase("DARK_MODE")) setDark();
			else if (SettingsHandler.getSettings("theme").asString().equalsIgnoreCase("CUSTOM_MODE")) {
				setCustom();
				Personalization.showCustomizeButton();
			}
			else {
				if (os.contains("WIN")) Defaults.setSystem();
				else Defaults.setDark();
			}

	}

	@SuppressWarnings("InfiniteLoopStatement")
	public static void startMainThread() {
			while (true) {
				try {
					if (os.contains("WIN")) {
						int theme = 0;
						int color = 0;

						ExecutorService executor = Executors.newCachedThreadPool();
						Callable<Object> task = RegQuery::getTheme;
						Future<Object> future = executor.submit(task);

						try {
							theme = (int) future.get(10, TimeUnit.SECONDS);

						} catch (Exception e) {
							future.cancel(true);
						}

						ExecutorService executorC = Executors.newCachedThreadPool();
						Callable<Object> taskC = RegQuery::getColor;
						Future<Object> futureC = executorC.submit(taskC);

						try {
							color = (int) futureC.get(10, TimeUnit.SECONDS);
						} catch (Exception e) {
							futureC.cancel(true);
						}

						if (!ACCENT.equals(Color.decode(String.valueOf(color)))) {
							if(!isMac()) {
								ACCENT = Color.decode(String.valueOf(color));
								Themes.refreshUI();
							}
						}


						if(SettingsHandler.getSettings("theme").asString().equalsIgnoreCase("SYSTEM_MODE")) {
							if (theme == 0 && prevTheme[0] == 1) {
								Defaults.setDark(false);
								prevTheme[0] = 0;
							} else if (theme == 1 && prevTheme[0] == 0) {
								Defaults.setLight(false);
								prevTheme[0] = 1;
							}
						}

					} else {
						Defaults.setDark(false);
						prevTheme[0] = 0;
					}

					Utilities.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

	}
}
