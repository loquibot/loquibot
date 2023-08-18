package com.alphalaneous.Moderation;

import com.alphalaneous.GibberishDetector.GibberishDetector;
import com.alphalaneous.GibberishDetector.GibberishDetectorExtended;
import com.alphalaneous.GibberishDetector.GibberishDetectorFactory;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.ChatBot.ChatMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Moderation {


	private static final HashMap<String, Warning> warningHashMap = new HashMap<>();

	private static final GibberishDetector gibberishDetector;
	static {
		BufferedReader bigEnglishReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Moderation.class.getResourceAsStream("/GibberishResources/bigEnglish.txt"))));
		BufferedReader goodEnglishReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Moderation.class.getResourceAsStream("/GibberishResources/goodEnglish.txt"))));
		BufferedReader badEnglishReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Moderation.class.getResourceAsStream("/GibberishResources/badEnglish.txt"))));

		String bigEnglishString = bigEnglishReader.lines().collect(Collectors.joining());
		String goodEnglishString = goodEnglishReader.lines().collect(Collectors.joining());
		String badEnglishString = badEnglishReader.lines().collect(Collectors.joining());

		GibberishDetectorFactory gibberishDetectorFactory = new GibberishDetectorFactory(GibberishDetectorExtended.class);
		gibberishDetector = gibberishDetectorFactory.createGibberishDetector(Arrays.asList(bigEnglishString.split("\n")), Arrays.asList(goodEnglishString.split("\n")), Arrays.asList(badEnglishString.split("\n")), "abcdefghijklmnopqrstuvwxyz ");
	}

	public static void checkMessage(ChatMessage chatMessage) {

		if(chatMessage.isMod()) return;

		String emotes = chatMessage.getTag("emotes");
		String messageEmoteless = removeEmotes(emotes, chatMessage.getMessage());
		double emotePercent = checkEmotePercent(emotes, chatMessage.getMessage());
		double capsPercent = checkCapPercent(messageEmoteless);
		double symPercent = checkSymbolPercent(messageEmoteless);
		int emoteCount = getEmoteCount(emotes);
		long capCount = getCapCount(messageEmoteless);
		int symCount = getSymbolCount(messageEmoteless);

		//System.out.println(emoteCount + " : " + emotePercent);
		//System.out.println(capCount + " :: " + capsPercent);
		//System.out.println(symCount + " ::: " + symPercent);

		boolean emoteEnabled = SettingsHandler.getSettings("emoteFilterEnabled").asBoolean();
		boolean capitalEnabled = SettingsHandler.getSettings("capitalFilterEnabled").asBoolean();
		boolean symbolEnabled = SettingsHandler.getSettings("symbolFilterEnabled").asBoolean();
		boolean linkDetectionEnabled = SettingsHandler.getSettings("linkFilterEnabled").asBoolean();
		boolean gibberishDetectionEnabled = SettingsHandler.getSettings("gibberishFilterEnabled").asBoolean();

		float setEmotePercent;
		float setCapsPercent;
		float setSymbolPercent;

		int setEmoteCount;
		int setCapsCount;
		int setSymbolCount;

		if(!SettingsHandler.getSettings("emotePercent").exists()) setEmotePercent = 0.5f;
		else setEmotePercent = SettingsHandler.getSettings("emotePercent").asFloat();

		if(!SettingsHandler.getSettings("capitalPercent").exists()) setCapsPercent = 0.5f;
		else setCapsPercent = SettingsHandler.getSettings("capitalPercent").asFloat();

		if(!SettingsHandler.getSettings("symbolPercent").exists()) setSymbolPercent = 0.5f;
		else setSymbolPercent = SettingsHandler.getSettings("symbolPercent").asFloat();

		if(!SettingsHandler.getSettings("emoteCount").exists()) setEmoteCount = 5;
		else setEmoteCount = SettingsHandler.getSettings("emoteCount").asInteger();

		if(!SettingsHandler.getSettings("capitalCount").exists()) setCapsCount = 5;
		else setCapsCount = SettingsHandler.getSettings("capitalCount").asInteger();

		if(!SettingsHandler.getSettings("symbolCount").exists()) setSymbolCount = 5;
		else setSymbolCount = SettingsHandler.getSettings("symbolCount").asInteger();

		if(checkIfLink(messageEmoteless) && linkDetectionEnabled && !LinkPermit.checkPermit(chatMessage.getSender())){
			Main.sendMessage("@" + chatMessage.getSender() + ", links are not allowed here!");
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
			addWarning(chatMessage.getSender(), "linkFilter");
			return;
		}
		if((emotePercent > setEmotePercent && emoteCount > setEmoteCount) && emoteEnabled){
			Main.sendMessage("@" + chatMessage.getSender() + ", please don't spam emotes!");
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
			addWarning(chatMessage.getSender(), "emote");
			return;
		}
		if((capsPercent > setCapsPercent && capCount > setCapsCount) && capitalEnabled){
			Main.sendMessage("@" + chatMessage.getSender() + ", please don't spam capital letters!");
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
			addWarning(chatMessage.getSender(), "capital");
			return;
		}
		if((symPercent > setSymbolPercent && symCount > setSymbolCount) && symbolEnabled){
			Main.sendMessage("@" + chatMessage.getSender() + ", please don't spam symbols!");
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
			addWarning(chatMessage.getSender(), "symbol");
			return;
		}
		//if contains no space and is smaller than 16 but greater than 8
		if((((!messageEmoteless.contains(" ") && messageEmoteless.length() <= 16 && messageEmoteless.length() >= 8)
				|| messageEmoteless.length() > 16)
				&& gibberishDetector.isGibberish(messageEmoteless))
				&& gibberishDetectionEnabled){
			Main.sendMessage("@" + chatMessage.getSender() + ", please don't send gibberish!");
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
			addWarning(chatMessage.getSender(), "gibberishFilter");
			return;
		}
		if(SettingsHandler.getSettings("autoDeleteGDLevelIDs").asBoolean() && checkIfLevelID(messageEmoteless)){
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
			return;
		}
		if(isFollowerBot(chatMessage) && SettingsHandler.getSettings("autoDeleteBigFollows").asBoolean()){
			Main.sendMessageWithoutCooldown("/delete " + chatMessage.getTag("id"));
		}
	}

	public static void addWarning(String username, String type){
		if(warningHashMap.containsKey(username)) warningHashMap.get(username).addWarning(type);
		else warningHashMap.put(username, new Warning(username){{ addWarning(type); }});
	}

	public static boolean checkIfLevelID(String message){
		return Pattern.compile("\\s*(\\d{6,})\\s*").matcher(message.replace(",", "")).find();
	}

	public static boolean checkIfNormalLink(String message){
		try {
			URL url = new URL(message);
			url.toURI();
			return true;
		} catch (Exception e) {
			return false;
		}

	}


	public static boolean checkIfLink(String message){
		String[] replaceSymbols = { "\\", "|", "{", "}", "\"", "'", ";", "<", ">", ",", "`", "!", "$", "^", "*"};
		String[] endSymbols = {"(", ")", "&", "%", "#", "@", "~", "?", ":", "_", "-", "+", "="};
		String[] messageSplitSpaces = message.split(" ");
		for(String spaceSplit : messageSplitSpaces){
			for(String replace : replaceSymbols){
				spaceSplit = spaceSplit.replace(replace, "");
			}
			spaceSplit = spaceSplit + " ";
			String[] possibleLink = spaceSplit.split("\\.");
			if(possibleLink.length > 1){
				// removed && possibleLink[possibleLink.length-1].trim().length() <= 6
				if(possibleLink[possibleLink.length-1].trim().length() >= 2){

					for(String endSymbol : endSymbols) {
						if(possibleLink[possibleLink.length - 1].trim().endsWith(endSymbol)){
							return false;
						}
						else if(!possibleLink[possibleLink.length - 1].trim().endsWith(endSymbol)){
							if(!(possibleLink[0].startsWith("http") || possibleLink[0].startsWith("https"))){
								try {
									URL url = new URL("http://" + spaceSplit.trim());
									URLConnection conn = url.openConnection();
									conn.connect();
									return true;
								} catch (IOException e) {
									return false;
								}
							}
							else{
								try {
									URL url = new URL(spaceSplit.trim());
									URLConnection conn = url.openConnection();
									conn.connect();
									return true;
								} catch (IOException e) {
									Main.logger.error(e.getLocalizedMessage(), e);
									return false;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static double checkCapPercent(String message) {
		//starting limit then percent after
		message = message.replaceAll(" ", "");
		double upperCase = message.chars().filter(c -> c >= 'A' && c <= 'Z').count();
		double result = upperCase / message.length();
		if(Double.isNaN(result)){
			return 0;
		}
		return result;
	}
	public static long getCapCount(String message){
		return message.chars().filter(c -> c >= 'A' && c <= 'Z').count();
	}
	public static int getEmoteCount(String emoteTag){
		if(emoteTag.equalsIgnoreCase("")){
			return 0;
		}
		String[] emotes = emoteTag.split("/");
		int count = 0;
		for(String emote : emotes){
			String positionsString = emote.split(":")[1];
			String[] positionsEach = positionsString.split(",");
			for(String ignored : positionsEach){
				count++;
			}
		}
		return count;
	}

	public static String removeEmotes(String emoteTag, String message){
		if(emoteTag.equalsIgnoreCase("")){
			return message;
		}
		message = message.replaceAll("\n", "").replaceAll("\r", "");
		String newMessage = message + " ";
		String[] emotes = emoteTag.split("/");
		for(String emote : emotes){
			String positionsString = emote.split(":")[1];
			String[] positionsEach = positionsString.split(",");
			for(String position : positionsEach){
				int start = Integer.parseInt(position.split("-")[0]);
				int stop = Integer.parseInt(position.split("-")[1]) + 1;
				String strToReplace = message.substring(start, stop) + " ";
				newMessage = newMessage.replaceFirst(Pattern.quote(strToReplace), "");
			}
		}
		return newMessage;
	}


	public static double checkEmotePercent(String emoteTag, String message) {
		if(emoteTag.equalsIgnoreCase("")){
			return 0;
		}
		message = message.replaceAll("\n", "").replaceAll("\r", "");
		String newMessage = message + " ";
		String[] emotes = emoteTag.split("/");
		int emoteCount = 0;
		for(String emote : emotes){
			String positionsString = emote.split(":")[1];
			String[] positionsEach = positionsString.split(",");
			for(String position : positionsEach){
				emoteCount++;
				int start = Integer.parseInt(position.split("-")[0]);
				int stop = Integer.parseInt(position.split("-")[1]) + 1;
				String strToReplace = message.substring(start, stop) + " ";
				newMessage = newMessage.replaceFirst(strToReplace, "");
			}
		}

		double length = newMessage.length() + emoteCount;

		return emoteCount/length;
	}

	public static double checkSymbolPercent(String message) {
		return getSymbolCount(message) / (double) message.length();
	}
	public static int getSymbolCount(String message) {
		int symbolCount = 0;
		for (int i = 0; i < message.length(); i++) {
			if (message.substring(i, i+1).matches("[^A-Za-z0-9 ]")) {
				symbolCount++;
			}
		}
		return symbolCount;
	}
	public static boolean isFollowerBot(ChatMessage message) {

		if(message.isFirstMessage()){
			if(message.getMessage().toLowerCase().contains("(remove the space)")){
				return true;
			}
			if(message.getMessage().toLowerCase().matches("\\b(b *i *g *f *o *l *l *o *w *s *([.,]) *c *o *m)+")){
				return true;
			}
			if(message.getMessage().toLowerCase().contains("mystrm")){
				return true;
			}
		}
		return false;
	}
}
