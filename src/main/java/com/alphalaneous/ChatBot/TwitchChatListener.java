package com.alphalaneous.ChatBot;

import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.Chat.SelfDestructingMessage;
import com.alphalaneous.Utilities.Chat.SelfDestructingViewer;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;

public class TwitchChatListener extends TwitchEventUtils {

	private static TwitchChatListener currentListener;


	public TwitchChatListener(String channel) {
		super(channel);
		currentListener = this;
	}

	public static TwitchChatListener getCurrentListener(){
		return currentListener;
	}

	@Override
	public void onOpen() {
		Logging.getLogger().info("Connected to Twitch IRC");
		Utilities.sleep(1000);
		sendMessage("Loquibot has connected to chat!");
	}

	@Override
	public void onClose() {
		Logging.getLogger().info("Disconnected from Chat Listener");
	}

	@Override
	public void onMessage(ChatMessage chatMessage) {

		new SelfDestructingMessage(chatMessage);
		new SelfDestructingViewer(chatMessage.getSender());

		if (SettingsHandler.getSettings("multiMode").asBoolean()) {
			new Thread(() -> waitOnMessage(chatMessage)).start();
		} else {
			waitOnMessage(chatMessage);
		}
	}

	private void waitOnMessage(ChatMessage chatMessage) {
		CommandHandler.run(chatMessage);
		KeywordHandler.run(chatMessage);
	}

	@Override
	public void onRawMessage(String s) {
	}

}
