package com.alphalaneous.Services.GeometryDash;

import com.alphalaneous.*;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Commands.DefaultCommandFunctions;
import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Settings.BlockedIDs;
import com.alphalaneous.Settings.BlockedUsers;
import com.alphalaneous.Settings.SettingData;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utils.Board;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;
import jdash.common.DemonDifficulty;
import jdash.common.Difficulty;
import jdash.common.Length;
import jdash.common.entity.GDLevel;
import jdash.common.entity.GDSong;
import jdash.common.entity.GDUserStats;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.file.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class RequestsUtils {

	static boolean bwomp = false;


	public static JSONObject getInfoObject(LevelData data) {
		return getInfoObject(data, false);

	}
		public static JSONObject getInfoObject(LevelData data, boolean forGD){
		JSONObject object = new JSONObject();
		if(forGD){
			object.put("service", "gd");
		}
		else{
			object.put("service", "StreamDeck");
		}
		if(data != null) {
			object.put("type", "level");
			object.put("difficulty", data.getSimpleDifficulty());
			object.put("id", data.getGDLevel().id());
			String creator = "-";
			if(data.getGDLevel().creatorName().isPresent()){
				creator = data.getGDLevel().creatorName().get();
			}



			object.put("name", data.getGDLevel().name());
			object.put("creator", creator);

			if(data.getDisplayName() == null){
				object.put("requester", "-");
			}
			else {
				object.put("requester", data.getDisplayName());
			}

			int songID = 0;
			if(data.getGDLevel().songId().isPresent()){
				songID = data.getGDLevel().songId().get().intValue();
			}
			object.put("songID", songID);
			object.put("stars", data.getGDLevel().stars());
			object.put("likes", data.getGDLevel().likes());
			object.put("downloads", data.getGDLevel().downloads());
			object.put("length", data.getGDLevel().length());
			object.put("accountID", 0);

			if(!creator.equalsIgnoreCase("-")) {
				for(int i = 0; i < 5; i++) {
					try {
						GDUserStats gdUserStats = GDAPI.getGDUserStats(creator);
						object.put("accountID", gdUserStats.accountId());
						break;
					} catch (Exception e) {
						System.out.println("Fails: " + i);
					}
				}
			}
		}
		else {
			object.put("status", "empty");
		}
		return object;
	}
	public static JSONObject getNextInfoObject(LevelData data){
		JSONObject object = new JSONObject();
		if(data != null) {
			object.put("type", "nextlevel");
			object.put("difficulty", data.getSimpleDifficulty());
			object.put("id", data.getGDLevel().id());
			String creator = "-";
			if(data.getGDLevel().creatorName().isPresent()){
				creator = data.getGDLevel().creatorName().get();
			}

			object.put("name", data.getGDLevel().name());
			object.put("creator", creator);

			int songID = 0;
			if(data.getGDLevel().songId().isPresent()){
				songID = data.getGDLevel().songId().get().intValue();
			}
			object.put("songID", songID);
			object.put("creatorID", data.getGDLevel().creatorPlayerId());
			object.put("stars", data.getGDLevel().stars());
			object.put("likes", data.getGDLevel().likes());
			object.put("downloads", data.getGDLevel().downloads());
			object.put("length", data.getGDLevel().length());
		}
		else {
			object.put("type", "nextlevel");
			object.put("status", "empty");
		}
		return object;
	}
	public static JSONObject getCurrentInfoObject(LevelData data){
		JSONObject object = new JSONObject();
		if(data != null) {
			object.put("type", "currentlevel");
			object.put("difficulty", data.getSimpleDifficulty());
			object.put("id", data.getGDLevel().id());
			String creator = "-";
			if(data.getGDLevel().creatorName().isPresent()){
				creator = data.getGDLevel().creatorName().get();
			}

			object.put("name", data.getGDLevel().name());
			object.put("creator", creator);

			int songID = 0;
			if(data.getGDLevel().songId().isPresent()){
				songID = data.getGDLevel().songId().get().intValue();
			}
			object.put("songID", songID);
			object.put("creatorID", data.getGDLevel().creatorPlayerId());
			object.put("stars", data.getGDLevel().stars());
			object.put("likes", data.getGDLevel().likes());
			object.put("downloads", data.getGDLevel().downloads());
			object.put("length", data.getGDLevel().length());
		}
		else {
			object.put("type", "nextlevel");
			object.put("status", "empty");
		}
		return object;
	}

	public static void forceAdd(String name, String author, long levelID, String difficulty, String demonDifficulty, boolean isDemon, boolean isAuto, boolean epic, int featuredScore, int stars, int requestedStars, String requester, int gameVersion, int coins, String description, int likes, int downloads, String length, int levelVersion, long songID, String songName, String songAuthor, int objects, long original, boolean verifiedCoins, boolean isYouTube, String displayName) {

		GDLevel level = new GDLevel() {
			@Override
			public long id() {
				return levelID;
			}

			@Override
			public String name() {
				return name;
			}

			@Override
			public long creatorPlayerId() {
				return 0;
			}

			@Override
			public String description() {
				return description;
			}

			@Override
			public Difficulty difficulty() {
				return Difficulty.valueOf(difficulty);
			}

			@Override
			public DemonDifficulty demonDifficulty() {
				return DemonDifficulty.valueOf(demonDifficulty);
			}

			@Override
			public int stars() {
				return stars;
			}

			@Override
			public int featuredScore() {
				return featuredScore;
			}

			@Override
			public boolean isEpic() {
				return epic;
			}

			@Override
			public int downloads() {
				return downloads;
			}

			@Override
			public int likes() {
				return likes;
			}

			@Override
			public Length length() {
				Length length1;
				switch (length){
					case "SHORT": length1 = Length.SHORT; break;
					case "MEDIUM": length1 = Length.MEDIUM; break;
					case "LONG": length1 = Length.LONG; break;
					case "XL": length1 = Length.XL; break;
					default: length1 = Length.TINY; break;
				}
				return length1;
			}

			@Override
			public int coinCount() {
				return coins;
			}

			@Override
			public boolean hasCoinsVerified() {
				return verifiedCoins;
			}

			@Override
			public int levelVersion() {
				return levelVersion;
			}

			@Override
			public int gameVersion() {
				return gameVersion;
			}

			@Override
			public int objectCount() {
				return objects;
			}

			@Override
			public boolean isDemon() {
				return isDemon;
			}

			@Override
			public boolean isAuto() {
				return isAuto;
			}

			@Override
			public Optional<Long> originalLevelId() {
				return Optional.of(original);
			}

			@Override
			public int requestedStars() {
				return requestedStars;
			}

			@Override
			public Optional<Long> songId() {
				return Optional.of(songID);
			}

			@Override
			public Optional<GDSong> song() {
				return Optional.of(new GDSong() {
					@Override
					public long id() {
						return songID;
					}

					@Override
					public String title() {
						return songName;
					}

					@Override
					public String artist() {
						return songAuthor;
					}

					@Override
					public Optional<String> size() {
						return Optional.empty();
					}

					@Override
					public Optional<String> downloadUrl() {
						return Optional.empty();
					}
				});
			}

			@Override
			public Optional<String> creatorName() {
				return Optional.of(author);
			}
		};
		LevelData levelData = new LevelData();
		levelData.setLevelData(level);
		levelData.setEpic(epic);
		if (featuredScore > 0) {
			levelData.setFeatured();
		}
		levelData.setMessage("Reloaded");
		levelData.setRequester(requester);
		levelData.setYouTube(isYouTube);
		levelData.setDisplayName(displayName);
		RequestsTab.addRequest(new LevelButton(levelData));

		if (RequestsTab.getQueueSize() == 1) {
			LevelDetailsPanel.setPanel(levelData);
		}
		RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
	}

	@SuppressWarnings("unused")
	public static String getLevel(int level, String attribute) {

		String result = "NA";
		try {

				switch (attribute) {
					case "name":
						result = RequestsTab.getRequest(level).getLevelData().getGDLevel().name();
						break;
					case "id":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getGDLevel().id());
						break;
					case "author":
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().creatorName().isPresent())
							result = RequestsTab.getRequest(level).getLevelData().getGDLevel().creatorName().get();
						break;
					case "requester":
						result = RequestsTab.getRequest(level).getLevelData().getDisplayName();
						break;
					case "difficulty":
						result = RequestsTab.getRequest(level).getLevelData().getGDLevel().difficulty().toString();
						break;
					case "likes":
						result = NumberFormat.getInstance().format(RequestsTab.getRequest(level).getLevelData().getGDLevel().likes());
						break;
					case "downloads":
						result = NumberFormat.getInstance().format(RequestsTab.getRequest(level).getLevelData().getGDLevel().downloads());
						break;
					case "description":
						result = RequestsTab.getRequest(level).getLevelData().getGDLevel().description();
						break;
					case "songName":
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().song().isPresent())
							result = RequestsTab.getRequest(level).getLevelData().getGDLevel().song().get().title();
						break;
					case "songID":
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().song().isPresent())
							result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getGDLevel().song().get().id());
						break;
					case "songArtist":
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().song().isPresent())
							result = RequestsTab.getRequest(level).getLevelData().getGDLevel().song().get().artist();
						break;
					case "songURL":
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().song().isPresent())
							if(RequestsTab.getRequest(level).getLevelData().getGDLevel().song().get().downloadUrl().isPresent())
								result = RequestsTab.getRequest(level).getLevelData().getGDLevel().song().get().downloadUrl().get();
						break;
					case "stars":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getGDLevel().stars());
						break;
					case "epic":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getEpic());
						break;
					case "version":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getGDLevel().levelVersion());
						break;
					case "length":
						result = RequestsTab.getRequest(level).getLevelData().getGDLevel().length().toString();
						break;
					case "coins":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getGDLevel().coinCount());
						break;
					case "objects":
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().objectCount() > 0)
							result = NumberFormat.getInstance().format(RequestsTab.getRequest(level).getLevelData().getGDLevel().objectCount());
						if(RequestsTab.getRequest(level).getLevelData().getGDLevel().objectCount() == 65535)
							result = "≥65535";
						break;
					case "original":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getGDLevel().originalLevelId());
						break;
					case "image":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getContainsImage());
						break;
					case "vulgar":
						result = String.valueOf(RequestsTab.getRequest(level).getLevelData().getContainsVulgar());
						break;
					default:
						result = "Error: Invalid type.";

			}
		} catch (Exception e) {
			result = "Exception: " + e;
		}
		return result;
	}

	@SuppressWarnings("unused")
	public static int getSelection() {
		return LevelButton.selectedID;
	}

	public static int getSize() {
		return RequestsTab.getQueueSize();
	}

	@SuppressWarnings("unused")
	public static void toggleRequests() {
		RequestFunctions.requestsToggleFunction();
	}

	@SuppressWarnings("unused")
	public static void clear() {
		RequestsTab.clearRequests();
		RequestFunctions.saveFunction();
		LevelDetailsPanel.setPanel(null);
		RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
	}

	public static String remove(String user, boolean isMod, int intArg) {
		if (intArg - 1 == LevelButton.selectedID) {
			return "";
		}
		String response = "";
		for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
			try {
				if (RequestsTab.getRequest(i).getLevelData().getGDLevel().id() == RequestsTab.getRequest(intArg - 1).getLevelData().getGDLevel().id()
						&& (isMod || String.valueOf(user).equalsIgnoreCase(RequestsTab.getRequest(i).getRequester()))) {
					response = "@" + user + ", " + RequestsTab.getRequest(i).getLevelData().getGDLevel().name() + " (" + RequestsTab.getRequest(i).getLevelData().getGDLevel().id() + ") has been removed!";

					int sel = LevelButton.selectedID;

					if(i == sel){
						break;
					}

					RequestsTab.removeRequest(i);

					if(i < sel){
						RequestsTab.getLevelsPanel().setSelect(sel-1, false, false);
					}

					RequestFunctions.saveFunction();
					if (i == 0) {
						StringSelection selection = new StringSelection(
								String.valueOf(RequestsTab.getRequest(0).getLevelData().getGDLevel().id()));
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(selection, selection);
					}
					break;
				}
			} catch (Exception ignored) {
			}
		}
		RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
		return response;
	}

	public static String replaceLatest(ChatMessage chatMessage){
		String response = "";
		for (int i = RequestsTab.getQueueSize() - 1; i >= 0; i--) {
			try {
				if (String.valueOf(chatMessage.getSender()).equalsIgnoreCase(RequestsTab.getRequest(i).getRequester())) {
					if (i == LevelButton.selectedID) {
						return "";
					}
					String prevLevelName = RequestsTab.getRequest(i).getLevelData().getGDLevel().name();
					long prevLevelID = RequestsTab.getRequest(i).getLevelData().getGDLevel().id();

					RequestsTab.removeRequest(i);

					Requests.request(chatMessage.getSender(), chatMessage.isMod(), chatMessage.isSub(), chatMessage.getMessage(), chatMessage.getTag("id"),  Long.parseLong(chatMessage.getTag("user-id")), chatMessage);

					for (int j = RequestsTab.getQueueSize(); j >= 0; j--) {
						try {

							System.out.println(chatMessage.getSender() + " | " + RequestsTab.getRequest(j).getRequester());

							if (chatMessage.getSender().equalsIgnoreCase(RequestsTab.getRequest(j).getRequester())) {

								System.out.println(j + " | " + i);

								RequestsTab.movePosition(j, i);
								RequestFunctions.saveFunction();
								break;
							}
						} catch (Exception ignored) {
						}
					}


					response = "@" + chatMessage.getSenderElseDisplay() + ", " + prevLevelName + " (" + prevLevelID + ") has been replaced with + " + RequestsTab.getRequest(i).getLevelData().getGDLevel().name() + "(" + RequestsTab.getRequest(i).getLevelData().getGDLevel().id() + ").";
					RequestFunctions.saveFunction();
					break;
				}
			} catch (Exception ignored) {
			}
		}
		RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
		return response;
	}

	@SuppressWarnings("unused")
	public static String removeLatest(ChatMessage message) {
		String response = "";
		for (int i = RequestsTab.getQueueSize() - 1; i >= 0; i--) {
			try {
				if (String.valueOf(message.getSender()).equalsIgnoreCase(RequestsTab.getRequest(i).getRequester())) {
					if (i == LevelButton.selectedID) {
						return "";
					}
					response = "@" + message.getSenderElseDisplay() + ", " + RequestsTab.getRequest(i).getLevelData().getGDLevel().name() + " (" + RequestsTab.getRequest(i).getLevelData().getGDLevel().id() + ") has been removed!";
					RequestsTab.removeRequest(i);
					RequestFunctions.saveFunction();
					break;
				}
			} catch (Exception ignored) {
			}
		}
		RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
		return response;
	}

	@SuppressWarnings("unused")
	public static long testForID(String message) {
		Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(message);
		if (m.find()) {
			try {
				String[] messages = message.split(" ");
				String mention = "";
				for (String s : messages) {
					if (s.contains("@")) {
						mention = s;
						break;
					}
				}
				if (!mention.contains(m.group(1))) {
					return Long.parseLong(m.group(1).replaceFirst("^0+(?!$)", ""));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@SuppressWarnings("unused")
	public static void toggleBwomp() {
		bwomp = !bwomp;
	}

	@SuppressWarnings("unused")
	public static void bwomp() {
		Board.bwomp();
	}

	@SuppressWarnings("unused")
	public static void stopBwomp() {
		Board.stopBwomp();
	}

	@SuppressWarnings("unused")
	public static void movePosition(int position, int newPosition) {
		System.out.println(newPosition);
		RequestsTab.getLevelsPanel().movePosition(position, newPosition);
	}

	public static int getPosFromID(long ID) {
		for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
			if (RequestsTab.getLevelsPanel().getButton(i).getID() == ID) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unused")
	public static String block(String user, String[] arguments) {
		String response;
		try {
			boolean start = false;
			int blockedID = Integer.parseInt(arguments[1]);
			if (blockedID == RequestsTab.getRequest(0).getLevelData().getGDLevel().id() && RequestsTab.getQueueSize() > 1) {
				StringSelection selection = new StringSelection(
						String.valueOf(RequestsTab.getRequest(1).getLevelData().getGDLevel().id()));
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
				start = true;
			}
			for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
				if (RequestsTab.getRequest(i).getLevelData().getGDLevel().id() == blockedID) {
					RequestsTab.removeRequest(i);
					RequestFunctions.saveFunction();
					break;
				}
			}
			Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\blocked.txt");
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Scanner sc = new Scanner(file.toFile());
			while (sc.hasNextLine()) {
				if (String.valueOf(blockedID).equals(sc.nextLine())) {
					sc.close();
					return Utilities.format("$BLOCK_EXISTS_MESSAGE$", user);
				}
			}
			sc.close();

			response = Utilities.format("$BLOCK_MESSAGE$", user, arguments[1]);
			BlockedIDs.addBlockedLevel(arguments[1]);
			if (start) {
				RequestsTab.getLevelsPanel().setSelect(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Utilities.format("$BLOCK_FAILED_MESSAGE$", user);
		}
		return response;
	}

	@SuppressWarnings("unused")
	public static String unblock(String user, String[] arguments) {
		String unblocked = arguments[1];
		String response = "";
		try {
			boolean exists = false;
			Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\blocked.txt");
			if (Files.exists(file)) {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					if (String.valueOf(unblocked).equals(sc.nextLine())) {
						exists = true;
						break;
					}
				}
				sc.close();

				if (exists) {
					BlockedIDs.removeBlockedLevel(arguments[1]);
					response = Utilities.format("$UNBLOCK_MESSAGE$", user, arguments[1]);
				} else {
					response = Utilities.format("$UNBLOCK_DOESNT_EXISTS_MESSAGE$", user);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			response = Utilities.format("$UNBLOCK_FAILED_MESSAGE$", user);
		}
		return response;
	}

	@SuppressWarnings("unused")
	public static String blockUser(String user, String[] arguments) {
		String response;
		try {
			String blockedUser = arguments[1].replace("@", "");
			Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\blockedUsers.txt");
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Scanner sc = new Scanner(file.toFile());
			while (sc.hasNextLine()) {
				if (String.valueOf(blockedUser).equals(sc.nextLine())) {
					sc.close();
					return Utilities.format("$BLOCK_USER_EXISTS_MESSAGE$", user);
				}
			}
			sc.close();

			response = Utilities.format("$BLOCK_USER_MESSAGE$", user, blockedUser);
			BlockedUsers.addBlockedUser(blockedUser);

		} catch (Exception e) {
			e.printStackTrace();
			response = Utilities.format("$BLOCK_USER_FAILED_MESSAGE$", user);
		}
		return response;
	}

	@SuppressWarnings("unused")
	public static String unblockUser(String user, String[] arguments) {
		String response = "";
		String unblocked = arguments[1].replace("@", "");

		try {
			boolean exists = false;
			Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\blockedUsers.txt");
			if (Files.exists(file)) {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					if (String.valueOf(unblocked).equals(sc.nextLine())) {
						exists = true;
						break;
					}
				}
				sc.close();
				if (exists) {

					response = Utilities.format("$UNBLOCK_USER_MESSAGE$", user, unblocked);
					BlockedUsers.removeBlockedUser(unblocked);
				} else {
					response = Utilities.format("$UNBLOCK_USER_DOESNT_EXISTS_MESSAGE$", user);

				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			response = Utilities.format("$UNBLOCK_USER_FAILED_MESSAGE$", user);
		}
		return response;
	}

	@SuppressWarnings("unused")

	public static String getHelp(ChatMessage message) {

		String command = null;
		if(message.getArgs().length > 1){
			command = message.getArgs()[1];
		}
		String defaultCommandPrefix = "!";
		String geometryDashCommandPrefix = "!";

		if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
		if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();

		String info = null;
		if(command != null) {
			for (CommandData commandData : CommandData.getRegisteredCommands()) {
				String prefix = "";
				if(commandData.isGD()) prefix = geometryDashCommandPrefix;
				else if(commandData.isDefault()) prefix = defaultCommandPrefix;
				if ((prefix + commandData.getCommand()).equalsIgnoreCase(command) && commandData.hasDescription()) {
					if(commandData.isGD()) info = commandData.getDescription().replace("%p", geometryDashCommandPrefix);
					else if(commandData.isDefault()) info = commandData.getDescription().replace("%p", defaultCommandPrefix);
					else info = commandData.getDescription();
				}
			}
			if (info == null) info = "$HELP_NO_INFO$";
		}
		else info = "$HELP_NO_COMMAND$";
		return Utilities.format("$DEFAULT_MENTION$", message.getSenderElseDisplay()) + " " + Utilities.format(info);
	}

	public static String getCommand(ChatMessage message) {
		int page = 1;
		boolean isPage = true;
		try {
			if(message.getArgs().length > 1) page = Integer.parseInt(message.getArgs()[1]);
		}
		catch (NumberFormatException ignored){
			isPage = false;
		}
		if(isPage || !message.isMod()) {
			ArrayList<String> existingCommands = new ArrayList<>();

			StringBuilder response = new StringBuilder();

			String defaultCommandPrefix = "!";
			String geometryDashCommandPrefix = "!";
			//String mediaShareCommandPrefix = "!";

			if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
			if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();
			//if(SettingsHandler.getSettings("mediaShareCommandPrefix").exists()) mediaShareCommandPrefix = SettingsHandler.getSettings("mediaShareCommandPrefix").asString();


			for (CommandData commandData : LoadCommands.getDefaultCommands()) {
				if (CommandHandler.checkUserLevel(commandData, message) && commandData.isEnabled() && !existingCommands.contains(commandData.getCommand())) {
					existingCommands.add(defaultCommandPrefix + commandData.getCommand());
				}
			}
			for (CommandData commandData : LoadCommands.getGeometryDashCommands()) {
				if (CommandHandler.checkUserLevel(commandData, message) && commandData.isEnabled() && !existingCommands.contains(commandData.getCommand()) && SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) {
					existingCommands.add(geometryDashCommandPrefix + commandData.getCommand());
				}
			}
			//for (CommandData commandData : LoadCommands.getMediaShareCommands()) {
			//	if (CommandHandler.checkUserLevel(commandData, message) && commandData.isEnabled() && !existingCommands.contains(commandData.getCommand())) {
			//		existingCommands.add(mediaShareCommandPrefix + commandData.getCommand());
			//	}
			//}
			for (CommandData commandData : LoadCommands.getCustomCommands()) {
				if (CommandHandler.checkUserLevel(commandData, message) && commandData.isEnabled() && !existingCommands.contains(commandData.getCommand())) {
					existingCommands.add(commandData.getCommand());
				}
			}
			Path comPath = Paths.get(Defaults.saveDirectory + "/loquibot/commands/");
			if (Files.exists(comPath)) {
				Stream<Path> walk1 = null;
				try {
					walk1 = Files.walk(comPath, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				assert walk1 != null;
				for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
					Path path = it.next();
					String[] file = path.toString().split("\\\\");
					String fileName = file[file.length - 1];

					if (fileName.endsWith(".js")) {
						boolean exists = false;
						String commandName = fileName.substring(0, fileName.length() - 3);
						for (CommandData data : LoadCommands.getDefaultCommands()) {
							String prefix = "";
							if(data.isDefault()) prefix = defaultCommandPrefix;
							if(data.isGD()) prefix = geometryDashCommandPrefix;

							if ((prefix + data.getCommand()).equalsIgnoreCase(commandName)) {
								exists = true;
								break;
							}
						}
						if (!exists) existingCommands.add(commandName);
					}
				}
			}

			existingCommands.sort(String.CASE_INSENSITIVE_ORDER);

			int pages = ((existingCommands.size() - 1) / 20) + 1;

			if (page > pages) return "@" + message.getSenderElseDisplay() + ", No commands on page " + page;
			if (page < 1) page = 1;
			response.append(Utilities.format("@%s, Command List Page %s of %s | Type !help <command> for command help.", message.getSender(), page, pages)).append(" | ");

			for (int i = (page - 1) * 20; i < page * 20; i++) {
				if (i < existingCommands.size()) {
					if (i % 20 != 0) {
						response.append(" | ");
					}
					response.append(existingCommands.get(i));
				}
			}
			return response.toString();
		}
		else {
			String action = null;
			if(message.getArgs().length > 1) action = message.getArgs()[1];

			String[] newArgs = Arrays.copyOfRange(message.getArgs(), 1, message.getArgs().length);

			message.setArgs(newArgs);
			message.setMessage(message.getMessage().substring(message.getMessage().split(" ")[0].length()+1));
			if (action != null) {
				switch (action.trim()){
					case "add":
						DefaultCommandFunctions.runAddcom(message);
						break;
					case "edit":
						DefaultCommandFunctions.runEditcom(message);
						break;
					case "delete" :
						DefaultCommandFunctions.runDelcom(message);
						break;
					default :
						return Utilities.format("$INVALID_ACTION_MESSAGE$", message.getSender());
				}
			}
			return "";
		}
	}

	@SuppressWarnings("unused")
	public static SettingData getOAuth() {
		return SettingsHandler.getSettings("oauth");
	}

	@SuppressWarnings("unused")
	public static String getClientID() {
		//noinspection SpellCheckingInspection
		return "fzwze6vc6d2f7qodgkpq2w8nnsz3rl";
	}

	@SuppressWarnings("unused")
	public static void endLoquibot() {
		Main.close();
	}

	public static String parseInfoString(String text) {
		return CommandHandler.replaceBetweenParentheses(new ChatMessage(null,"OutputHandler", "OutputHandler", "", null, true, true, true, 0, false), text, text.split(" "), null);
	}
}
