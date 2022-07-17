package com.alphalaneous.Services.Twitch;

import com.alphalaneous.*;
import com.alphalaneous.ChatBot.BotHandler;
import com.alphalaneous.ChatBot.ChatterActivity;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.Moderation.Moderation;
import com.alphalaneous.ChatBot.ChatBot;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Utils.Utilities;
import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;

public class TwitchChatListener extends ChatBot {

	public static boolean sentStartupMessage = false;
	private static TwitchChatListener currentListener;


	public TwitchChatListener(String channel) {
		super(channel);
		currentListener = this;
	}

	public static TwitchChatListener getCurrentListener(){
		return currentListener;
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		System.out.println("> Connected to Twitch IRC");

	}

	@Override
	public void onClose(int i, String s, boolean b) {
		System.out.println("> Disconnected from Chat Listener");
		Utilities.sleep(2000);
		new TwitchChatListener(TwitchAccount.login).connect(SettingsHandler.getSettings("oauth").asString(), TwitchAccount.login);
	}

	@Override
	public void onMessage(ChatMessage chatMessage) {
		//TwitchChat.addMessage(chatMessage);
		if(chatMessage.getSender().equalsIgnoreCase("loquibot") && chatMessage.isMod()){
			SettingsHandler.writeSettings("isMod", "true");
		}

		if (!chatMessage.getSender().equalsIgnoreCase("loquibot")) {
			new SelfDestructingMessage();
			new ChatterActivity(chatMessage.getSender());
			if (SettingsHandler.getSettings("multiMode").asBoolean()) {
				new Thread(() -> waitOnMessage(chatMessage)).start();
			} else {
				waitOnMessage(chatMessage);
			}
			Moderation.checkMessage(chatMessage);

		}
	}

	private void waitOnMessage(ChatMessage chatMessage) {
		CommandHandler.run(chatMessage);
		KeywordHandler.run(chatMessage);
		long userID;
		if (chatMessage.getTag("user-id") != null) {
			userID = Long.parseLong(chatMessage.getTag("user-id"));
			BotHandler.onMessage(chatMessage.getSender(), chatMessage.getMessage(), chatMessage.isMod(), chatMessage.isSub(), chatMessage.getCheerCount(), chatMessage.getTag("id"), userID, chatMessage);
		}
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
