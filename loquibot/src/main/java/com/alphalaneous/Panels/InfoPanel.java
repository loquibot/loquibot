package com.alphalaneous.Panels;

import com.alphalaneous.Defaults;
import com.alphalaneous.Requests;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoPanel {

	private static final JPanel descPanel = new JPanel();
	private static final JPanel fullPanel = new JPanel(null);

	private static final JTextPane description = new JTextPane();
	private static final JPanel window = new JPanel();

	public static void createPanel() {


		int height = 110;
		descPanel.setPreferredSize(new Dimension(240, height));
		int width = 400;
		descPanel.setBounds(0, 0, width, height);
		descPanel.setBackground(Defaults.SUB_MAIN);
		descPanel.setLayout(null);
		descPanel.setOpaque(true);


		description.setText("N/A");
		StyledDocument doc = description.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		description.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		description.setSelectionColor(new Color(44, 144, 250));
		description.setOpaque(false);
		description.setEditable(false);
		description.setForeground(Defaults.FOREGROUND);
		description.setBackground(new Color(0, 0, 0, 0));
		description.setBounds(5, 5, width - 10, height - 10);
		description.setSelectionColor(Defaults.ACCENT);

		descPanel.add(description);
		fullPanel.setBounds(1, 31, 400, 110);
		fullPanel.add(descPanel);
		window.add(fullPanel);
		refreshInfo();
	}

	public static void resetDimensions(int width, int height) {
		descPanel.setBounds(0, 0, width, height);
		description.setBounds(5, 5, width - 10, height - 10);

	}
	private static void appendToPane(JTextPane tp, String msg, Color c) {
		description.setEditable(true);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_CENTER);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
		description.setEditable(false);
	}

	public static JPanel getInfoWindow() {
		return fullPanel;
	}

	public static void refreshInfo() {
		description.setVisible(false);
		appendToPane(description, "", Defaults.FOREGROUND);
		if (Requests.levels.size() == 0) {
			description.setText("NA");
		} else {
			description.setText("");

			ArrayList<String> colored = new ArrayList<>();
			boolean hasColored = false;


			String desc = Requests.levels.get(LevelButton.selectedID).getLevelData().description();
			Matcher matcher = Pattern.compile("<(c[a-zA-Z])>(.+?)</c>").matcher(desc);
			while (matcher.find()) {
				hasColored = true;
				colored.add(matcher.group(1).substring(1) + ">" + matcher.group(2));
			}
			if(hasColored){
				String[] descSplit = desc.split("</c>");
				char colorVal = 0;
				String colorSectionA = "";
				boolean colorGotten = false;
				for(String descSection : descSplit){
					String[] descSectionSplit = descSection.split("<c");
					all: for(String descSectionSplitSection : descSectionSplit){
						if(descSectionSplitSection.length() > 0 && descSectionSplitSection.substring(1).startsWith(">")) {
							for (String colorSection : colored) {
								if (descSectionSplitSection.equals(colorSection)) {
									colorVal = colorSection.charAt(0);
									colorGotten = true;
									colorSectionA = colorSection;
									break all;
								}
							}
						}
						else{
							appendToPane(description, descSection.split("<c")[0], Defaults.FOREGROUND);
						}
					}
					if(colorGotten) {
						Color color = Defaults.FOREGROUND;
						switch (colorVal){
							case 'o': color = new Color(255, 165,75); break;
							case 'y': color = new Color(255, 255, 72); break;
							case 'g': color = new Color(64, 227,72); break;
							case 'j': color = new Color(50, 200,255); break;
							case 'b': color = new Color(74, 82,225); break;
							case 'p': color = new Color(255, 0,255); break;
							case 'l': color = new Color(96, 171,239); break;
							case 'r': color = new Color(255, 74, 74); break;
							case 'x': color = new Color(255, 0,0); break;
							default: break;
						}
						appendToPane(description, colorSectionA.substring(2), color);
					}

				}
			}
			else{
				description.setText(desc);
			}
		}
		description.setVisible(true);
	}

	public static void refreshUI() {
		descPanel.setBackground(Defaults.SUB_MAIN);
		description.setForeground(Defaults.FOREGROUND);
	}

	//region SetLocation
	public static void setLocation(Point point) {
		window.setLocation(point);
	}

	public String getName() {
		return "Description";
	}
	//endregion

}
