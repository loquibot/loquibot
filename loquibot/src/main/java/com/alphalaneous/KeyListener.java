package com.alphalaneous;

import com.alphalaneous.Components.KeybindButton;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.LogWindow;
import com.alphalaneous.Windows.Window;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.SwingKeyAdapter;

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

	public void nativeKeyPressed(NativeKeyEvent e) {

		if (e.getRawCode() == 162 || e.getRawCode() == 163) {
			ctrlPressed = true;
		}

		if (keyReleased || ctrlPressed) {

			int key = e.getRawCode();

			if (key == 187) {
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
			}
			if (!KeybindButton.getInFocus()) {
				if (key == Settings.getSettings("openKeybind").asInteger()) {
					Window.focus();
				}
				if (key == Settings.getSettings("skipKeybind").asInteger()) {
					RequestFunctions.skipFunction();
				}
				if (key == Settings.getSettings("undoKeybind").asInteger()) {
					RequestFunctions.undoFunction();
				}
				if (key == Settings.getSettings("randomKeybind").asInteger()) {
					RequestFunctions.randomFunction();
				}
				if (key == Settings.getSettings("copyKeybind").asInteger()) {
					RequestFunctions.copyFunction();
				}
				if (key == Settings.getSettings("blockKeybind").asInteger()) {
					RequestFunctions.blockFunction();
				}
				if (key == Settings.getSettings("clearKeybind").asInteger()) {
					RequestFunctions.clearFunction();
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
					if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(String.valueOf(e.getRawCode()))) {
						Path path = Paths.get(Defaults.saveDirectory + "/loquibot/actions/" + line.split("=")[1] + ".js");
						if (Files.exists(path))
                            RequestsTab.sendCommandResponse(path);
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
			}
			keyReleased = false;
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		keyReleased = true;
		if (e.getRawCode() == 162 || e.getRawCode() == 163) {
			ctrlPressed = false;
		}
	}
}
