package com.alphalaneous.Utilities;

import com.alphalaneous.Interfaces.ObjectHandler;
import com.alphalaneous.Services.Twitch.TwitchAPI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Utilities {

	public static Path saveDirectory;

	static {

		String OS = (System.getProperty("os.name")).toUpperCase();
		String directory;

		if (OS.contains("WIN")) {
			directory = System.getenv("APPDATA") + "/Alphalaneous/Loquibot/";
		} else if (OS.contains("MAC")){
			directory = System.getProperty("user.home") + "/Library/Application Support/Alphalaneous/Loquibot/";
		} else if (OS.contains("NUX")){
			directory = System.getProperty("user.home") + "/.Alphalaneous/Loquibot/";
		} else {
			directory = System.getProperty("user.dir") + "/Alphalaneous/Loquibot/";
		}

		saveDirectory = Path.of(directory);

		if (!Files.isDirectory(saveDirectory)) {
			try {
				Files.createDirectories(saveDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final String os = (System.getProperty("os.name")).toUpperCase();

	public static boolean isWindows(){
		return (os.toLowerCase().contains("win"));
	}

	public static void openURL(URI uri){
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("rundll32 url.dll,FileProtocolHandler " + uri);
		} catch (Exception e) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException ignored) {
			}
		}
	}

	public static void ifNotNull(Object o, ObjectHandler oh){
		if(o != null){
			oh.run(o);
		}
	}

	public static Rectangle getRectInFrame(Container container, JFrame frame){
		Point p = container.getLocationOnScreen();
		Dimension d = container.getSize();

		int x = Math.abs(Math.abs(p.x) - Math.abs(frame.getX()));
		int y = Math.abs(Math.abs(p.y) - Math.abs(frame.getY()));

		return new Rectangle(x, y, d.width, d.height);
	}

	public static String toFirstUpper(String value){
		return value.toUpperCase().charAt(0) + value.toLowerCase().substring(1);
	}

	public static String readIntoString(BufferedReader reader){
		return readIntoString(reader, false);
	}

	public static String readIntoString(BufferedReader reader, boolean includeLineBreaks){
		StringBuilder builder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if(includeLineBreaks) builder.append(line).append("\n");
				else builder.append(line);
			}
			reader.close();
		}
		catch (IOException ignored){}
		return builder.toString();
	}

	public static String fetchURL(String url) {
		return TwitchAPI.fetchURL(url);
	}

	public static void load(String file, HashMap<String, String> location) throws IOException {

		Path path = Paths.get(saveDirectory + "/" + file);
		if(Files.exists(path)) {
			String text = new String(Files.readAllBytes(path));
			String[] textSplit = text.split("\n");
			for (String line : textSplit) {
				if (line.contains("=")) {
					location.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
				}
			}
		}
	}

	public static void save(String file, HashMap<String, String> values){
		Path fileA = Paths.get(saveDirectory + "/" + file).toAbsolutePath();

		try {

			if (!Files.exists(fileA, LinkOption.NOFOLLOW_LINKS)) {
				Files.createFile(fileA);
			}
			Files.createDirectories(saveDirectory);
			Iterator<Map.Entry<String, String>> it = values.entrySet().iterator();
			StringBuilder pairs = new StringBuilder();
			while (it.hasNext()) {
				Map.Entry<String, String> pair = it.next();
				pairs.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
			}

			Files.write(fileA, pairs.toString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void wait(AtomicBoolean value){
		while(!value.get()){
			Utilities.sleep(10);
		}
	}

	public static void sleep(int milliseconds){
		sleep(milliseconds, 0);
	}
	public static void sleep(int milliseconds, int nano) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
			TimeUnit.NANOSECONDS.sleep(nano);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static TimerTask createTimerTask(Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}
}
