package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Language;

import javax.swing.*;
import java.util.ArrayList;

public class LangButton extends JButton {
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
