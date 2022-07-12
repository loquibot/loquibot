package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Language;
import com.alphalaneous.Swing.ThemedComponents.ThemedJButton;

import java.util.ArrayList;

public class LangButton extends ThemedJButton {
	private String text;
	public static ArrayList<LangButton> buttonList = new ArrayList<>();

	public LangButton(String text){
		this.text = text;
		String newText = Language.setLocale(text);
		setText(newText);
		buttonList.add(this);
	}
	public void setTextLang(String text){
		this.text = text;
		String newText = Language.setLocale(text);
		setText(newText);
	}
	public void refreshLocale(){
		String newText = Language.setLocale(text);
		setText(newText);
	}
}
