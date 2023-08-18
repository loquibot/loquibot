package com.alphalaneous.Services.Twitch;

import com.alphalaneous.ChatBot.*;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.Main;
import com.alphalaneous.Moderation.Moderation;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Utils.Utilities;
import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TwitchChatListener extends NewChatBot {

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
		Main.logger.info("Connected to Twitch IRC");

	}

	@Override
	public void onClose() {
		Main.logger.info("Disconnected from Chat Listener");
	}

	@Override
	public void onMessage(ChatMessage chatMessage) {
		//TwitchChat.addMessage(chatMessage);

		if (!chatMessage.getSender().equalsIgnoreCase("loquibot")) {
			new SelfDestructingMessage();
			new SelfDestructingViewer(chatMessage.getSender());

			if (SettingsHandler.getSettings("multiMode").asBoolean()) {
				new Thread(() -> waitOnMessage(chatMessage)).start();
			} else {
				waitOnMessage(chatMessage);
			}
			Moderation.checkMessage(chatMessage);

		}
	}

	private void waitOnMessage(ChatMessage chatMessage) {
		BotHandler.onMessage(chatMessage);
		CommandHandler.run(chatMessage);
		KeywordHandler.run(chatMessage);
	}

	@Override
	public void onRawMessage(String s) {
	}

	public static class SelfDestructingMessage{

		private static final List<SelfDestructingMessage> selfDestructingMessages = Collections.synchronizedList(new ArrayList<>());

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

	public static class SelfDestructingViewer{

		private static final List<SelfDestructingViewer> selfDestructingViewers = Collections.synchronizedList(new ArrayList<>());
		private final String viewer;
		public SelfDestructingViewer(String viewer){

			if(containsViewer(viewer)) {
				removeByViewer(viewer);
			}

			this.viewer = viewer;
			new Thread(() -> {
				selfDestructingViewers.add(this);
				Utilities.sleep(60000*3);
				selfDestructingViewers.remove(this);
			}).start();
		}

		public String getViewer(){
			return viewer;
		}

		public static void removeByViewer(String viewer){
			for(SelfDestructingViewer viewer1 : selfDestructingViewers){
				if(viewer1 != null) {
					if (viewer1.getViewer().equalsIgnoreCase(viewer)) {
						selfDestructingViewers.remove(viewer1);
						break;
					}
				}
			}
		}

		public static boolean containsViewer(String viewer){
			for(SelfDestructingViewer viewer1 : selfDestructingViewers){
				if(viewer1 != null) {
					if (viewer1.getViewer().trim().equalsIgnoreCase(viewer.trim())) {
						return true;
					}
				}
			}
			return false;
		}

		public static int getSize(){
			return selfDestructingViewers.size();
		}
	}

}
