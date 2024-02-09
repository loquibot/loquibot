package com.alphalaneous.Utilities;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Components.SpecialTextArea;
import com.alphalaneous.Interactive.Actions.ActionData;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.github.kwhat.jnativehook.keyboard.SwingKeyAdapter;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyListener extends SwingKeyAdapter {
	private static boolean isCtrlPressed = false;
	private static boolean isAltPressed = false;
	private static boolean isShiftPressed = false;

	private static final HashSet<Integer> keysPressed = new HashSet<>();

	public void keyPressed(KeyEvent e) {

		if(!SpecialTextArea.isTextAreaFocused()) {
			if (!keysPressed.contains(e.getKeyCode())) {

				for (ActionData data : ActionData.getRegisteredActions()) {

					boolean ctrl = true;
					boolean alt = true;
					boolean shift = true;

					if (data.isUsesCtrl()) {
						ctrl = isCtrlPressed;
					}
					if (data.isUsesAlt()) {
						alt = isAltPressed;
					}
					if (data.isUsesShift()) {
						shift = isShiftPressed;
					}

					if ((e.getKeyCode() == data.getKeyBind()) && ctrl && alt && shift) {

						new Thread(() -> {
							ChatMessage chatMessage = new ChatMessage(new String[0], "ActionHandler", "ActionHandler", "", new String[0], true, true, true, false, false);
							TwitchChatListener.getCurrentListener().sendMessage(CommandHandler.replaceBetweenParentheses(chatMessage, data.getMessage(), data, null));
						}).start();
					}
				}
			}
		}
		if(e.getKeyCode() == 17){
			isCtrlPressed = true;
		}
		else if(e.getKeyCode() == 18) {
			isAltPressed = true;
		}
		else if(e.getKeyCode() == 16) {
			isShiftPressed = true;
		}
		else {
			keysPressed.add(e.getKeyCode());
		}
	}

	public void keyReleased(KeyEvent e) {

		keysPressed.remove(e.getKeyCode());

		if(e.getKeyCode() == 17){
			isCtrlPressed = false;
		}
		if(e.getKeyCode() == 18) {
			isAltPressed = false;
		}
		if(e.getKeyCode() == 16) {
			isShiftPressed = false;
		}
	}
}
