package com.alphalaneous.Interactive;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
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

	private static final HashMap<String, String> vars = new HashMap<>();

	public static void loadVars() {
		Utilities.load("/loquibot/vars.board", vars);
	}

	public static void saveVars() {
		Utilities.save("/loquibot/vars.board", vars);
	}

	public static void setVar(String name, Object object) {
		vars.put(name, String.valueOf(object));
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
