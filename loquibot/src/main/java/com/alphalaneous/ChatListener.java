package com.alphalaneous;

import com.alphalaneous.Moderation.Moderation;
import com.alphalaneous.TwitchBot.ChatBot;
import com.alphalaneous.TwitchBot.ChatMessage;
import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;

public class ChatListener extends ChatBot {

	private static boolean sentStartupMessage = false;

	ChatListener(String channel) {
		super(channel);
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		System.out.println("> Connected to Twitch IRC");
		if(!sentStartupMessage) {
			Main.sendMessage(Utilities.format("🔷 | $STARTUP_MESSAGE$"));
			sentStartupMessage = true;
		}
	}

	@Override
	public void onClose(int i, String s, boolean b) {

	}

	@Override
	public void onMessage(ChatMessage chatMessage) {
		//TwitchChat.addMessage(chatMessage);
		if(chatMessage.getSender().equalsIgnoreCase("loquibot") && chatMessage.isMod()){
			Settings.writeSettings("isMod", "true");
		}

		if (!chatMessage.getSender().equalsIgnoreCase("loquibot")) {
			new SelfDestructingMessage();
			if (Settings.getSettings("multiMode").asBoolean()) {
				new Thread(() -> waitOnMessage(chatMessage)).start();
			} else {
				waitOnMessage(chatMessage);
			}
			Moderation.checkMessage(chatMessage);

		}
	}

	private void waitOnMessage(ChatMessage chatMessage) {
		CommandNew.run(chatMessage);
		KeywordHandler.run(chatMessage);
		BotHandler.onMessage(chatMessage.getSender(), chatMessage.getMessage(), chatMessage.isMod(), chatMessage.isSub(), chatMessage.getCheerCount(), chatMessage.getTag("id"), Long.parseLong(chatMessage.getTag("user-id")));
	}

	@Override
	public void onRawMessage(String s) {
	}

	@Override
	public void onError(Exception e) {
		e.printStackTrace();
	}

	public static class SelfDestructingMessage{

		private static final ArrayList<SelfDestructingMessage> selfDestructingMessages = new ArrayList<>();

		public SelfDestructingMessage(){
			new Thread(() -> {
				selfDestructingMessages.add(this);
				Utilities.sleep(60000*5);
				selfDestructingMessages.remove(this);
			}).start();
		}
		public static int getSize(){
			return selfDestructingMessages.size();
		}
	}

}
