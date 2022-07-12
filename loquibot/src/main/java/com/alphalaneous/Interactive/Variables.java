package com.alphalaneous.Interactive;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Variables {

	private static final HashMap<String, Object> vars = new HashMap<>();

	public static void loadVars() {
		Path path = Paths.get(Defaults.saveDirectory + "/loquibot/vars.board");
		if (Files.exists(path)) {

			Scanner sc = null;
			try {
				sc = new Scanner(path.toFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			assert sc != null;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("=")) {
					vars.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
				}
			}
			sc.close();
		}
	}

	public static void saveVars() {
		Path file = Paths.get(Defaults.saveDirectory + "/loquibot/vars.board");

		try {
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Iterator it = vars.entrySet().iterator();
			StringBuilder pairs = new StringBuilder();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				pairs.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
				it.remove();
			}
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Files.write(
					file,
					pairs.toString().getBytes());
		} catch (IOException e1) {
			DialogBox.showDialogBox("Error!", e1.toString(), "There was an error writing to the file!", new String[]{"OK"});

		}
	}

	public static void setVar(String name, Object object) {
		vars.put(name, object);
	}

	public static String getVar(String name) {
		if(vars.containsKey(name)) {
			return String.valueOf(vars.get(name));
		}
		else return "";
	}

	public static void clearVars(){
		vars.clear();
	}
}
