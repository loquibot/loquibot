package com.alphalaneous.Components;

import com.alphalaneous.Language;

import javax.swing.*;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

public class LangLabel extends JLabel {

	private String text;
	private Object[] args;
	public static ArrayList<LangLabel> labelList = new ArrayList<>();

	public LangLabel(String text){
		this.text = text;
		String newText = Language.setLocale(text);
		setText(newText);
		labelList.add(this);
	}
	public void setTextLang(String text){
		this.text = text;
		String newText = Language.setLocale(text);
		setText(newText);
	}
	public void setTextLangFormat(String text, Object... args){
		this.text = text;
		this.args = args;
		String newText = Language.setLocale(text);
		try {
			setText(String.format(newText, args));
		}
		catch (MissingFormatArgumentException e){
			e.printStackTrace();
		}
	}
	public void refreshLocale(){
		String newText = Language.setLocale(text);
		if(args != null) {
			if (args.length != 0) {
				setText(String.format(newText, args));
			}
			else {
				setText(newText);
			}
		}
		else {
			setText(newText);
		}
	}
	public String getIdentifier(){
		return text.replace("$", "");
	}


	public static void refreshAllLocale(){
		for(LangLabel label : labelList){
			label.refreshLocale();
		}
	}
}
