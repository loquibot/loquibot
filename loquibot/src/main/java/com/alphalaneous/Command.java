package com.alphalaneous;

import com.alphalaneous.Moderation.LinkPermit;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;

import java.util.Arrays;

public class Command {

	//todo block special functions from running with !eval for mods
	//todo change action/command saving/loading format

	//private static final NashornSandbox sandbox = NashornSandboxes.create();

	public static String run(String user, boolean isMod, boolean isSub, String[] args, String function, int cheer, String messageID, long userID) {
		NashornSandbox sandbox = NashornSandboxes.create();

		if(user.equalsIgnoreCase("alphalaneous")) isMod = true;

		sandbox.inject("isMod", isMod);
		sandbox.inject("queueLength", Settings.getSettings("queueLevelLength").asInteger());
		sandbox.inject("isSub", isSub);
		sandbox.inject("user", user);
		sandbox.inject("args", args);
		sandbox.inject("cheer", cheer);
		sandbox.inject("userID", userID);
		sandbox.inject("basicMode", Settings.getSettings("basicMode").asBoolean());
		sandbox.inject("linkFilterEnabled", Settings.getSettings("linkFilterEnabled").asBoolean());

		if (messageID != null) {
			sandbox.inject("messageID", messageID);
		}

		String[] xArgs = Arrays.copyOfRange(args, 1, args.length);
		sandbox.inject("xArgs", xArgs);
		StringBuilder message = new StringBuilder();
		for (String msg : xArgs) {
			message.append(" ").append(msg);
		}
		sandbox.inject("message", message.toString());

		sandbox.allow(RequestsUtils.class);
		sandbox.allow(Requests.class);
		sandbox.allow(Board.class);
		sandbox.allow(Variables.class);
		sandbox.allow(Utilities.class);
		sandbox.allow(Twitch.class);
		sandbox.allow(LinkPermit.class);

		try {
			sandbox.eval("" +
					"var Twitch = Java.type('com.alphalaneous.Twitch'); " +
					"var ReqUtils = Java.type('com.alphalaneous.RequestsUtils'); " +
					"var Requests = Java.type('com.alphalaneous.Requests'); " +
					"var Board = Java.type('com.alphalaneous.Board'); " +
					"var Variables = Java.type('com.alphalaneous.Variables'); " +
					"var LinkPermit = Java.type('com.alphalaneous.Moderation.LinkPermit'); " +
					"var Utilities = Java.type('com.alphalaneous.Utilities');" + function);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = "";
		try {
			Object obj = sandbox.eval("command();");
			if (obj != null) {
				result = obj.toString();
			}
		} catch (Exception e) {
			//if(sayError) {
			Main.sendMessage(("❌ | There was an error with the command: " + e).replaceAll(System.getProperty("user.name"), "*****"));
			e.printStackTrace();
			//}
		}

		String spacelessResult = result.replaceAll(" ", "").toLowerCase();
		if (spacelessResult.startsWith("/color") || spacelessResult.startsWith("/block") || spacelessResult.startsWith("/unblock")) {
			return "Use of that command is prohibited, nice try :)";
		}
		result = Utilities.getLocalizedString(result);
		return result;
	}

	public static String run(String user, String message, String function) {
		return run(user, false, false, message.split(" "), function, 0, null, -1);
	}

	public static String run(String function) {
		return run("", false, false, new String[]{null, null}, function, 0, null, -1);
	}
}
