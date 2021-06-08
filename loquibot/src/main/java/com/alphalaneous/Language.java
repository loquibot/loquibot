package com.alphalaneous;

import com.alphalaneous.Components.CurvedButtonAlt;
import com.alphalaneous.Components.LangButton;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Components.RoundedJButton;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

public class Language {

	private static final String lang = "en_us";
	private static Path myPath;

	static {
		try {
			URI uri = Main.class.getResource("/Languages/").toURI();
			if (uri.getScheme().equals("jar")) {
				myPath = BotHandler.fileSystem.getPath("/Languages/");
			} else {
				myPath = Paths.get(uri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String setLocale(String text) {
		String newText = text;
		String[] words = newText.split(" ");
		for (String word : words) {
			if (word.startsWith("$") && word.endsWith("$")) {
				String newWord = Language.getString(word.replace("$", ""));
				newText = newText.replace(word, newWord);
			}
		}
		return newText;
	}

	static String getString(String identifier) {
		try {
			Path comPath = Paths.get(Defaults.saveDirectory + "/GDBoard/Languages/");
			if (Files.exists(comPath)) {
				Stream<Path> walk1 = Files.walk(comPath, 1);
				for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
					Path path = it.next();
					String[] file = path.toString().split("\\\\");
					String fileName = file[file.length - 1];
					if (fileName.equals(lang + ".lang")) {
						Scanner sc = new Scanner(path.toFile());
						while (sc.hasNextLine()) {
							String line = sc.nextLine();
							if (line.startsWith("//")) {
								continue;
							}
							if (line.split("=", 2)[0].trim().equals(identifier)) {
								sc.close();
								return line.split("=", 2)[1].trim();
							}
						}
						sc.close();
					}
				}
			}
			Stream<Path> walk = Files.walk(myPath, 1);
			for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
				Path path = it.next();

				String[] file;
				if (BotHandler.uri.getScheme().equals("jar")) {
					file = path.toString().split("/");
				} else {
					file = path.toString().split("\\\\");
				}
				String fileName = file[file.length - 1];
				if (fileName.equals(lang + ".lang")) {
					if (BotHandler.uri.getScheme().equals("jar")) {
						InputStream is = Main.class
								.getClassLoader().getResourceAsStream(path.toString().substring(1));
						assert is != null;
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);

						String line;
						while ((line = br.readLine()) != null) {
							if (line.startsWith("//")) {
								continue;
							}
							if (line.split("=", 2)[0].trim().equals(identifier)) {

								is.close();
								isr.close();
								br.close();
								return line.split("=", 2)[1].trim();
							}
						}
						is.close();
						isr.close();
						br.close();
					} else {
						Scanner sc = null;
						try {
							sc = new Scanner(path);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						String line;
						assert sc != null;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (line.startsWith("//")) {
								continue;
							}
							try {
								if (line.split("=", 2)[0].trim().equals(identifier)) {
									return line.split("=", 2)[1].trim();
								}
							} catch (IndexOutOfBoundsException e) {
								return identifier;
							}
						}
						sc.close();
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return identifier;
	}

	@SuppressWarnings("unchecked")
	static void startFileChangeListener() {
		new Thread(() -> {
			try {
				WatchService watcher = FileSystems.getDefault().newWatchService();
				Path dir = Paths.get(Defaults.saveDirectory + "/GDBoard/Languages");
				if (Files.exists(dir)) {
					dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

					while (true) {
						WatchKey key;
						try {
							key = watcher.take();
						} catch (InterruptedException ex) {
							return;
						}

						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent.Kind<?> kind = event.kind();

							WatchEvent<Path> ev = (WatchEvent<Path>) event;
							Path fileName = ev.context();

							if (kind == ENTRY_MODIFY && fileName.toString().equals(lang + ".lang")) {
								for (int i = 0; i < LangButton.buttonList.size(); i++) {
									LangButton.buttonList.get(i).refreshLocale();
								}
								for (int i = 0; i < LangLabel.labelList.size(); i++) {
									LangLabel.labelList.get(i).refreshLocale();
								}
								for (int i = 0; i < RoundedJButton.buttons.size(); i++) {
									RoundedJButton.buttons.get(i).refreshLocale();
								}
								for (int i = 0; i < CurvedButtonAlt.buttonList.size(); i++) {
									CurvedButtonAlt.buttonList.get(i).refreshLocale();
								}
							}
						}

						boolean valid = key.reset();
						if (!valid) {
							break;
						}
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
}