package com.alphalaneous.Utils;

import com.alphalaneous.Audio.Sounds;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.Modifications;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Windows.LogWindow;
import com.alphalaneous.Windows.Window;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.SwingKeyAdapter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class KeyListener extends SwingKeyAdapter {
	private static boolean keyReleased = false;
	private static boolean ctrlPressed = false;

	public static boolean isCtrlPressed() {
		return ctrlPressed;
	}

	public void keyPressed(KeyEvent e) {

		//System.out.println(e.getKeyCode());

		if(Defaults.isMac()){
			if (e.getKeyCode() == 157) {
				ctrlPressed = true;
			}
		}
		if(e.getKeyCode() == 17){
			ctrlPressed = true;
		}

		if(e.getKeyCode() == 0) return;

		if (keyReleased || ctrlPressed) {

			int key = e.getKeyCode();

			if (!KeybindButton.getInFocus()) {
				if (key == SettingsHandler.getSettings("openKeybind").asInteger()) {
					Window.focus();
				}
				if (key == SettingsHandler.getSettings("skipKeybind").asInteger()) {
					RequestFunctions.skipFunction();
				}
				if (key == SettingsHandler.getSettings("undoKeybind").asInteger()) {
					RequestFunctions.undoFunction();
				}
				if (key == SettingsHandler.getSettings("randomKeybind").asInteger()) {
					RequestFunctions.randomFunction();
				}
				if (key == SettingsHandler.getSettings("copyKeybind").asInteger()) {
					RequestFunctions.copyFunction();
				}
				if (key == SettingsHandler.getSettings("blockKeybind").asInteger()) {
					RequestFunctions.blockFunction();
				}
				if (key == SettingsHandler.getSettings("clearKeybind").asInteger()) {
					RequestFunctions.clearFunction();
				}
				if (key == SettingsHandler.getSettings("stopSoundsKeybind").asInteger()) {
					Sounds.stopAllSounds();
				}
			}
			if(Window.getWindow().isFocused()
					|| LogWindow.getWindow().isFocused()
					|| Main.getStartingFrame().isFocused()){
				if(ctrlPressed && key == 123){
					LogWindow.toggleLogWindow();
				}
				if(ctrlPressed && key == 122){
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().edit(new File(Main.logFile + ".txt"));
						} catch (IOException ex) {
							Main.logger.error(ex.getLocalizedMessage(), ex);
						}
					}
				}
			}
			keyReleased = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		keyReleased = true;

		if(Defaults.isMac()){
			if (e.getKeyCode() == 157) {
				ctrlPressed = false;
			}
		}
		if(e.getKeyCode() == 17){
			ctrlPressed = false;
		}
	}
}
