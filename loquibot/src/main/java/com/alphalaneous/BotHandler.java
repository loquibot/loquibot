package com.alphalaneous;

import com.alphalaneous.SettingsPanels.RequestsSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BotHandler {
	public static FileSystem fileSystem = null;
	static boolean processing = false;
	static URI uri;
	private static final Path myPath;
	private static final String[] gdCommands = {"!gd", "!kill", "!block", "!blockuser", "!unblock", "!unblockuser", "!clear", "!info", "!move", "!next", "!position", "!queue", "!remove", "!request", "!song", "!stop", "!toggle", "!top", "!wronglevel"};
	private static final ArrayList<String> comCooldown = new ArrayList<>();

	static {
		try {
			uri = Main.class.getResource("/Commands/").toURI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		if (uri.getScheme().equals("jar")) {
			try {
				fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
			} catch (IOException e) {
				e.printStackTrace();
			}
			myPath = fileSystem.getPath("/Commands/");
		} else {
			myPath = Paths.get(uri);
		}
	}

	static void onMessage(String user, String message, boolean isMod, boolean isSub, int cheer, String ID) {
		boolean whisper = false;
		processing = true;
		boolean goThrough = true;
		String com = message.split(" ")[0];
		String[] arguments = message.split(" ");
		String response = "";
		String messageNoComma = message.replace(",", "");
		Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(messageNoComma);
		if (m.find() && !message.startsWith("!")) {
			try {
				String[] msgs = message.split(" ");
				String mention = "";
				for (String s : msgs) {
					if (s.contains("@")) {
						mention = s;
						break;
					}
				}
				if (!mention.contains(m.group(1))) {
					if (RequestsSettings.gdModeOption) {
						Requests.addRequest(Long.parseLong(m.group(1).replaceFirst("^0+(?!$)", "")), user, isMod, isSub, message, ID, false);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (com.equalsIgnoreCase("!sudo") && (isMod || user.equalsIgnoreCase("Alphalaneous"))) {
					user = arguments[1].toLowerCase();
					com = arguments[2];
					arguments = Arrays.copyOfRange(arguments, 2, arguments.length);
					isMod = true;
					isSub = true;

					if (com.equalsIgnoreCase("!eval") ||
							com.equalsIgnoreCase("!end") ||
							com.equalsIgnoreCase("!gd") ||
							com.equalsIgnoreCase("!rick") ||
							com.equalsIgnoreCase("!popup") ||
							com.equalsIgnoreCase("!stop")) {
						goThrough = false;
					}
				}
				if (com.equalsIgnoreCase("b!bypass") && user.equalsIgnoreCase("Alphalaneous")) {
					com = arguments[1];
					arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
					isMod = true;
					isSub = true;
				}
				boolean aliasesExist = false;
				if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/commands/aliases.txt"))) {
					Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/commands/aliases.txt").toFile());
					while (sc2.hasNextLine()) {
						String line = sc2.nextLine();
						if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(com)) {
							aliasesExist = true;
							com = line.split("=")[1].replace(" ", "");
							break;
						}
					}
					sc2.close();
				}
				if (!aliasesExist) {
					InputStream is = Main.class
							.getClassLoader().getResourceAsStream("Commands/aliases.txt");
					assert is != null;
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(com)) {
							com = line.split("=")[1].replace(" ", "");
							break;
						}
					}
					is.close();
					isr.close();
					br.close();
				}
				if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/disable.txt"))) {
					Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/disable.txt").toFile());
					while (sc2.hasNextLine()) {
						String line = sc2.nextLine();
						if (!line.equalsIgnoreCase("!eval")) {
							if (line.equalsIgnoreCase(com)) {
								goThrough = false;
								break;
							}
						}
					}
					sc2.close();
				}
				if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/mod.txt"))) {
					Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/mod.txt").toFile());
					while (sc2.hasNextLine()) {
						String line = sc2.nextLine();
						if (line.equalsIgnoreCase(com) && !isMod) {
							goThrough = false;
							break;
						}
					}
					sc2.close();
				} else {
					InputStream is = Main.class
							.getClassLoader().getResourceAsStream("Commands/mod.txt");
					assert is != null;
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						if (line.equalsIgnoreCase(com) && !isMod) {
							goThrough = false;
							break;
						}
					}
					is.close();
					isr.close();
					br.close();
				}
				if (!RequestsSettings.gdModeOption) {
					for (String command : gdCommands) {
						if (com.equalsIgnoreCase(command)) {
							goThrough = false;
							break;
						}
					}
				}
				if (goThrough) {
					boolean whisperExists = false;
					if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/whisper.txt"))) {
						Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/whisper.txt").toFile());
						while (sc2.hasNextLine()) {
							String line = sc2.nextLine();
							if (line.equalsIgnoreCase(com)) {
								whisperExists = true;
								whisper = true;
								break;
							}
						}
						sc2.close();
					}
					if (!whisperExists) {
						InputStream is = Main.class
								.getClassLoader().getResourceAsStream("Commands/whisper.txt");
						assert is != null;
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);
						String line;
						while ((line = br.readLine()) != null) {
							if (line.equalsIgnoreCase(com)) {
								whisper = true;
								break;
							}
						}
						is.close();
						isr.close();
						br.close();
					}

					if (comCooldown.contains(com)) {
						processing = false;
						return;
					}
					int cooldown = 0;
					boolean coolExists = false;
					if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt"))) {
						Scanner sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt").toFile());
						while (sc3.hasNextLine()) {
							String line = sc3.nextLine();
							if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(com)) {
								coolExists = true;
								cooldown = Integer.parseInt(line.split("=")[1].replace(" ", ""));
								break;
							}
						}
						sc3.close();
					}
					if (!coolExists) {
						InputStream is = Main.class
								.getClassLoader().getResourceAsStream("Commands/cooldown.txt");
						assert is != null;
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);
						String line;
						while ((line = br.readLine()) != null) {
							if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(com)) {
								cooldown = Integer.parseInt(line.split("=")[1].replace(" ", ""));
								break;
							}
						}
						is.close();
						isr.close();
						br.close();
					}
					if (cooldown > 0) {
						String finalCom = com;
						int finalCooldown = cooldown * 100;
						Thread thread = new Thread(() -> {
							comCooldown.add(finalCom);
							try {
								Thread.sleep(finalCooldown);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							comCooldown.remove(finalCom);
						});
						thread.start();
					}
					boolean comExists = false;

					Path comPath = Paths.get(Defaults.saveDirectory + "/GDBoard/commands/");
					if (Files.exists(comPath)) {
						Stream<Path> walk1 = Files.walk(comPath, 1);
						for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
							Path path = it.next();
							String[] file = path.toString().split("\\\\");
							String fileName = file[file.length - 1];
							if (fileName.equalsIgnoreCase(com + ".js")) {
								comExists = true;
								response = Command.run(user, isMod, isSub, arguments, Files.readString(path, StandardCharsets.UTF_8), cheer, ID);

							}
						}
					}

					if (!comExists) {

						Stream<Path> walk = Files.walk(myPath, 1);
						for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
							Path path = it.next();
							String[] file;
							if (uri.getScheme().equals("jar")) {
								file = path.toString().split("/");
							} else {
								file = path.toString().split("\\\\");
							}
							String fileName = file[file.length - 1];
							if (fileName.equalsIgnoreCase(com + ".js")) {
								if (uri.getScheme().equals("jar")) {
									InputStream is = Main.class
											.getClassLoader().getResourceAsStream(path.toString().substring(1));
									assert is != null;
									InputStreamReader isr = new InputStreamReader(is);
									BufferedReader br = new BufferedReader(isr);
									StringBuilder function = new StringBuilder();
									String line;
									while ((line = br.readLine()) != null) {
										function.append(line);
									}
									is.close();
									isr.close();
									br.close();
									response = Command.run(user, isMod, isSub, arguments, function.toString(), cheer, ID);
								} else {
									response = Command.run(user, isMod, isSub, arguments, Files.readString(path, StandardCharsets.UTF_8), cheer, ID);
								}
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (response != null && !response.equalsIgnoreCase("")) {
				Main.sendMessage(response, whisper, user);
			}
		}
		processing = false;
	}
}
