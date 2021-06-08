package com.alphalaneous;

import com.alphalaneous.SettingsPanels.ChaosModeSettings;
import com.alphalaneous.SettingsPanels.RequestsSettings;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;

import java.util.Arrays;

public class Command {

	private static final NashornSandbox sandbox = NashornSandboxes.create();

	public static String run(String user, boolean isMod, boolean isSub, String[] args, String function, int cheer, String messageID) {
		sandbox.inject("isMod", isMod);
		sandbox.inject("isChaos", ChaosModeSettings.enableChaos);
		sandbox.inject("isModChaos", ChaosModeSettings.modOnly);
		sandbox.inject("isSubChaos", ChaosModeSettings.subOnly);
		sandbox.inject("queueLength", RequestsSettings.queueLevelLength);
		sandbox.inject("isSub", isSub);
		sandbox.inject("user", user);
		sandbox.inject("args", args);
		sandbox.inject("cheer", cheer);
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
		sandbox.allow(GDMod.class);
		sandbox.allow(Board.class);
		sandbox.allow(Variables.class);
		sandbox.allow(Utilities.class);
		sandbox.allow(Twitch.class);
		sandbox.allow(GDHelper.class);

		try {
			sandbox.eval("" +
					"var Twitch = Java.type('com.alphalaneous.Twitch'); " +
					"var ReqUtils = Java.type('com.alphalaneous.RequestsUtils'); " +
					"var Requests = Java.type('com.alphalaneous.Requests'); " +
					"var GD = Java.type('com.alphalaneous.GDMod'); " +
					"var Board = Java.type('com.alphalaneous.Board'); " +
					"var Variables = Java.type('com.alphalaneous.Variables'); " +
					"var GDHelper = Java.type('com.alphalaneous.GDHelper'); " +
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
		return run(user, false, false, message.split(" "), function, 0, null);
	}

	public static String run(String function) {
		return run("", false, false, new String[]{null, null}, function, 0, null);
	}
}
