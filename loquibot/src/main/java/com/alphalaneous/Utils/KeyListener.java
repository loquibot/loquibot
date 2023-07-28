package com.alphalaneous.Utils;

import com.alphalaneous.Memory.Hacks;
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

import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
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

		System.out.println(e.getKeyCode());

		if (keyReleased || ctrlPressed) {

			int key = e.getKeyCode();

			/*if (key == 187) {
				key = 61;
			} else if (key == 189) {
				key = 45;
			} else if (key == 190) {
				key = 46;
			} else if (key == 188) {
				key = 44;
			} else if (key == 186) {
				key = 59;
			} else if (key == 220) {
				key = 92;
			} else if (key == 221) {
				key = 93;
			} else if (key == 219) {
				key = 91;
			} else if (key == 191) {
				key = 47;
			} else if (key == 46) {
				key = 127;
			} else if (key == 45) {
				key = 155;
			}*/
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
				if(!Defaults.isMac()) {
					if (key == SettingsHandler.getSettings("safeNoclipKeybind").asInteger()) {
						boolean isNoclip = SettingsHandler.getSettings("safeNoclipHack").asBoolean();

						SettingsHandler.writeSettings("safeNoclipHack", String.valueOf(!isNoclip));
						SettingsPage.CheckBox.resetCheckbox("safeNoclipHack");
						Modifications.setSafeMode();
						Hacks.setNoclip(SettingsHandler.getSettings("safeNoclipHack").asBoolean());
					}
				}
			}
			if (Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/actions/keybinds.txt"))) {
				Scanner sc3 = null;
				try {
					sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/loquibot/actions/keybinds.txt").toFile());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				assert sc3 != null;
				while (sc3.hasNextLine()) {
					String line = sc3.nextLine();
					if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(String.valueOf(e.getKeyCode()))) {
						Path path = Paths.get(Defaults.saveDirectory + "/loquibot/actions/" + line.split("=")[1] + ".js");

                        break;
					}
				}
				sc3.close();
			}
			if(Window.getWindow().isFocused()
					|| LogWindow.getWindow().isFocused()
					|| Main.getStartingFrame().isFocused()){
				if(ctrlPressed && key == 123){
					LogWindow.toggleLogWindow();
				}
				if(ctrlPressed && key == 82){
					Main.restart();
				}
			}
			keyReleased = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		keyReleased = true;

		if(Defaults.isMac()){
			if (e.getKeyCode() == 157) {
				ctrlPressed = true;
			}
		}
		if(e.getKeyCode() == 17){
			ctrlPressed = true;
		}
	}
}
