package com.alphalaneous.VBSShortcut;

import com.alphalaneous.Main;

import java.io.*;

/*
 * @author Jackson N. Brienen
 * Content Protected VIA GPL-2.0-only
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 * Copyright (c) 2021 Jackson Nicholas Brienen
 * https://github.com/JacksonBrienen/VBS-Shortcut
 *
 * **PLEASE DO NOT REMOVE THIS HEADER**
 */
/**
 * @author Jackson N. Brienen
 * @version 1.1
 */
public class ShortcutFactory {
	/**
	 * Creates a Shortcut on the desktop with the passed name and linked to the passed source<br>
	 * Note - this will pause thread until shortcut has been created
	 * @param source - The path to the source file to create a Shortcut to
	 * @param linkName - The name of the Shortcut that will be created
	 * @throws FileNotFoundException - if source File does not exist
	 */
	public static void createDesktopShortcut(String source, String linkName) throws FileNotFoundException {
		String linkPath = System.getProperty("user.home")+"/Desktop/"+linkName;
		createShortcut(source, linkPath);
	}
	
	/**
	 * Creates a Shortcut at the passed location linked to the passed source<br>
	 * Note - this will pause thread until shortcut has been created
	 * @param source - The path to the source file to create a Shortcut to
	 * @param linkPath - The path of the Shortcut that will be created
	 * @throws FileNotFoundException 
	 */
	public static void createShortcut(String source, String linkPath) throws FileNotFoundException {
		File sourceFile = new File(source);
		if(!sourceFile.exists()) {
			throw new FileNotFoundException("The Path: "+sourceFile.getAbsolutePath()+" does not exist!");
		}
		try {
			source = sourceFile.getAbsolutePath();
			
			String vbsCode = String.format(
				  "Set wsObj = WScript.CreateObject(\"WScript.shell\")%n"
				+ "scPath = \"%s\"%n"
				+ "Set scObj = wsObj.CreateShortcut(scPath)%n"
				+ "\tscObj.TargetPath = \"%s\"%n"
				+ "scObj.Save%n",
				linkPath, source
				);
		
			newVBS(vbsCode);
		} catch (IOException | InterruptedException e) {
			System.err.println("Could not create and run VBS!");
			Main.logger.error(e.getLocalizedMessage(), e);
		} 
	}
	
	/*
	 * Creates a VBS file with the passed code and runs it, deleting it after the run has completed
	 */
	private static void newVBS(String code) throws IOException, InterruptedException {
		File script = File.createTempFile("scvbs", ".vbs"); // File where script will be created
		
		// Writes to script file
		FileWriter writer = new FileWriter(script);
		writer.write(code);
		writer.close();
		
		Process p = Runtime.getRuntime().exec( "wscript \""+script.getAbsolutePath()+"\""); // executes vbs code via cmd
		p.waitFor(); // waits for process to finish
		if(!script.delete()) { // deletes script
			System.err.println("Warning Failed to delete tempory VBS File at: \""+script.getAbsolutePath()+"\"");
		}
	}
}