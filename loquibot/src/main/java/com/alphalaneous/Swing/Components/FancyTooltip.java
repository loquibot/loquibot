package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.util.ArrayList;

public class FancyTooltip extends JToolTip {

	public static ArrayList<FancyTooltip> tooltips = new ArrayList<>();

	public FancyTooltip(JComponent component) {
		super();
		setComponent(component);
		setBackground(Defaults.COLOR6);
		setForeground(Defaults.FOREGROUND_A);
		setFont(Defaults.MAIN_FONT.deriveFont(14f));
		setBorder(BorderFactory.createEmptyBorder());
		tooltips.add(this);
	}

	public void refresh() {
		setBackground(Defaults.COLOR6);
		setForeground(Defaults.FOREGROUND_A);
	}

	public static void refreshAll(){
		for(FancyTooltip tooltip : tooltips){
			tooltip.refresh();
		}
	}

}
