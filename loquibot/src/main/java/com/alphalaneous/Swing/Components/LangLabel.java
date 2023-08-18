package com.alphalaneous.Swing.Components;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Language;

import javax.swing.*;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LangLabel extends JLabel {

	private String text;
	private Object[] args;
	public static ConcurrentLinkedQueue<LangLabel> labelList = new ConcurrentLinkedQueue<>();

	public LangLabel(String text){
		this.text = text;
		String newText = Language.setLocale(text);
		setText(newText);
		labelList.add(this);
	}

	public void setAvailableFont(float size){
		super.setFont(Defaults.getPreferredFontForText(getText()).deriveFont(size));
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
			Main.logger.error(e.getLocalizedMessage(), e);

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
		return text.replace("$", "").replace("〈", "").replace("〉","");
	}
}
