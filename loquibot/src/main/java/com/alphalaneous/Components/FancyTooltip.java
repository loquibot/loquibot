package com.alphalaneous.Components;

import com.alphalaneous.Defaults;

import javax.swing.*;
import java.util.ArrayList;

public class FancyTooltip extends JToolTip {

	public static ArrayList<FancyTooltip> tooltips = new ArrayList<>();

	public FancyTooltip(JComponent component) {
		super();
		setComponent(component);
		setBackground(Defaults.TOP);
		setForeground(Defaults.FOREGROUND);
		setFont(Defaults.MAIN_FONT.deriveFont(14f));
		setBorder(BorderFactory.createEmptyBorder());
		tooltips.add(this);
	}

	public void refresh() {
		setBackground(Defaults.TOP);
		setForeground(Defaults.FOREGROUND);
	}

	public static void refreshAll(){
		for(FancyTooltip tooltip : tooltips){
			tooltip.refresh();
		}
	}

}
