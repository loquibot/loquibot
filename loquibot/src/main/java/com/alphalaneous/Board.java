package com.alphalaneous;

import com.alphalaneous.SettingsPanels.RequestsSettings;
import com.alphalaneous.Windows.DialogBox;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import jdash.client.exception.GDClientException;
import org.json.JSONObject;

import java.time.ZonedDateTime;

@SuppressWarnings("unused")

public class Board {

	private static boolean bwomp = false;
	private static final NashornSandbox sandbox = NashornSandboxes.create();

	public static void sendDeathMessagesToggle() {
		Settings.writeSettings("SendDeathMessages", String.valueOf(!Settings.getSettings("SendDeathMessages").asBoolean()));
		RequestsSettings.deathMessage.setChecked(Settings.getSettings("SendDeathMessages").asBoolean());
	}

	public static void playSound(String location) {
		Sounds.playSound(location, true, false, true, false);
	}

	public static void playSound(String location, boolean restart, boolean overlap, boolean isURL) {
		Sounds.playSound(location, restart, overlap, true, isURL);
	}

	public static void stopSound(String location) {
		Sounds.stopSound(location);
	}

	public static void stopAllSounds() {
		Sounds.stopAllSounds();
	}

	public static void sendMessage(String message, boolean whisper, String user) {
		Main.sendMessage(message, whisper, user);
	}

	public static void sendMessage(String message) {
		Main.sendMessage(message, false, null);
	}

	public static void sendAsMain(String message) {
		Main.sendMainMessage(message);
	}

	public static void playNewgrounds(String songID) {
		Sounds.playSound(GDAPI.getSong(Long.parseLong(songID)).downloadUrl().get(), true, false, false, true);
	}

	public static String eval(String function) {
		return Command.run("function command(){" + function + "}");
	}

	public static void showPopup(String title, String text) {
		new Thread(() -> {
			DialogBox.showDialogBox(title, "<html>" + text + "</html>", "", new String[]{"OK"});
		}).start();
	}


	public static String getenv(String name) {
		return System.getenv(name);
	}

	public static void toggleBwomp() {
		bwomp = !bwomp;
	}

	public static void endGDBoard() {
		Main.close();
	}

	public static void signal() {
		JSONObject messageObj = new JSONObject();
		messageObj.put("request_type", "get_blocked_ids");
		Main.sendBotMessage(messageObj.toString());
	}

	public static void rick() {
		if (Sounds.sounds.containsKey("/rick.mp3")) {
			Sounds.stopSound("/rick.mp3");
		}
		Sounds.playSound("/rick.mp3", true, false, false, false);
	}

	public static void stopRick() {
		Sounds.stopSound("/rick.mp3");
	}

	public static void knock() {
		Sounds.playSound("/knock.mp3", true, true, false, false);
	}

	public static void stopKnock() {
		Sounds.stopSound("/knock.mp3");

	}

	public static void bwomp() {
		Sounds.playSound("/bwomp.mp3", true, true, false, false);
	}

	public static void stopBwomp() {
		Sounds.stopSound("/bwomp.mp3");
	}

	public static String testSearchPing() {
		long time = ZonedDateTime.now().toInstant().toEpochMilli();
		try {
			GDAPI.getTopLevelByName("");
			long timeAffter = ZonedDateTime.now().toInstant().toEpochMilli();
			return String.valueOf(timeAffter - time);
		} catch (GDClientException e) {
			if(e.getCause().toString().equals("ActionFailedException") || e.getCause().toString().equals("HttpResponseException"))
			return "Servers Down!";
		}
		return "Servers Down!";
	}
}
