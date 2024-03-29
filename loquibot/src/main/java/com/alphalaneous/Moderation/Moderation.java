package com.alphalaneous.Moderation;

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
			if(emote.split(":").length > 1) {
				String positionsString = emote.split(":")[1];
				String[] positionsEach = positionsString.split(",");
				for (String ignored : positionsEach) {
					count++;
				}
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
			if(emote.split(":").length > 1) {
				String positionsString = emote.split(":")[1];
				String[] positionsEach = positionsString.split(",");
				for (String position : positionsEach) {
					int start = Integer.parseInt(position.split("-")[0]);
					int stop = Integer.parseInt(position.split("-")[1]) + 1;
					String strToReplace = message.substring(start, stop) + " ";
					newMessage = newMessage.replaceFirst(Pattern.quote(strToReplace), "");
				}
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
			if(emote.split(":").length > 1) {
				String positionsString = emote.split(":")[1];
				String[] positionsEach = positionsString.split(",");
				for (String position : positionsEach) {
					emoteCount++;
					int start = Integer.parseInt(position.split("-")[0]);
					int stop = Integer.parseInt(position.split("-")[1]) + 1;
					String strToReplace = message.substring(start, stop) + " ";
					newMessage = newMessage.replaceFirst(strToReplace, "");
				}
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
