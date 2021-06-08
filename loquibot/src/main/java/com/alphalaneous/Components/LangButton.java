package com.alphalaneous.Components;

import com.alphalaneous.Language;
import com.alphalaneous.ThemedComponents.ThemedJButton;

import java.util.ArrayList;

public class LangButton extends ThemedJButton {
	private String text;
	private Object[] args;
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
	public void setTextLangFormat(String text, Object... args){
		this.text = text;
		this.args = args;
		String newText = Language.setLocale(text);
		setText(String.format(newText, args));
	}
	public void refreshLocale(){
		String newText = Language.setLocale(text);
		if (args != null) {
			if (args.length != 0) {
				setText(String.format(newText, args));
			} else {
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
		for(LangButton button : buttonList){
			button.refreshLocale();
		}
	}
}
