package com.alphalaneous.Services.Twitch;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
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
		new Thread(() -> waitOnMessage(chatMessage)).start();
	}

	private void waitOnMessage(ChatMessage chatMessage) {
		CommandHandler.run(chatMessage);
		KeywordHandler.run(chatMessage);
	}

	@Override
	public void onRawMessage(String s) {
	}

}
