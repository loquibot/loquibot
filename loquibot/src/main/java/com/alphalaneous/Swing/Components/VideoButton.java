package com.alphalaneous.Swing.Components;

import com.alphalaneous.*;
import com.alphalaneous.Services.YouTube.YouTubeVideo;
import com.alphalaneous.Tabs.MediaShareTab;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class VideoButton extends CurvedButton {

	private static final JButtonUI selectUI = new JButtonUI();
	public static int selectedID = 0;

	public boolean selected;

	private final JButton moveUp = new JButton("\uE010");
	private final JButton moveDown = new JButton("\uE011");

	private final YouTubeVideo videoData;
	private final MultiLineLabel title;
	private final JLabel creator = new JLabel();


	public VideoButton(YouTubeVideo data) {
		super("");
		setOpaque(false);

		this.videoData = data;

		this.title = new MultiLineLabel(data.getTitle(), 200, Defaults.MAIN_FONT.deriveFont(12f));

		title.setBackground(new Color(0,0,0,0));
		title.setOpaque(false);

		creator.setText(data.getUsername());

		title.setBounds(175, 5, 200, title.getPreferredSize().height);

		title.setForeground(Defaults.FOREGROUND_A);
		creator.setBounds(175, title.getPreferredSize().height-35, 200, 100);
		creator.setForeground(Defaults.FOREGROUND_B);
		creator.setFont(Defaults.MAIN_FONT.deriveFont(12f));


		ImageIcon icon = new ImageIcon(VideoDetails.imageDownloader(data.getThumbnailURL()));

		data.setImage(icon);

		JLabel thumbnail = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(160, 90, Image.SCALE_SMOOTH)));
		thumbnail.setBounds(5,5, 160, 90);


		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)){
					Window.destroyContextMenu();
					//Window.addContextMenu(new LevelContextMenu(Requests.getPosFromID(data.getGDLevel().id())));
				}
			}
		});


		try {

			JButtonUI clear = new JButtonUI();
			clear.setBackground(new Color(0, 0, 0, 0));
			clear.setHover(new Color(0, 0, 0, 0));
			clear.setSelect(new Color(0, 0, 0, 0));

			selectUI.setBackground(Defaults.COLOR4);
			selectUI.setHover(Defaults.COLOR5);

			setLayout(null);




			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if(RequestsTab.getQueueSize() > 1) {
						moveUp.setVisible(true);
						moveDown.setVisible(true);
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveUp.setVisible(false);
					moveDown.setVisible(false);
				}
			});

			moveUp.setVisible(false);
			moveDown.setVisible(false);
			moveUp.setFont(Defaults.SYMBOLSalt.deriveFont(15f));
			moveUp.setUI(clear);
			moveUp.setForeground(Defaults.FOREGROUND_A);
			moveUp.setBackground(new Color(0, 0, 0, 0));
			moveUp.setOpaque(false);
			moveUp.setBorder(BorderFactory.createEmptyBorder());
			moveUp.setBounds(MediaShareTab.getVideosPanel().getButtonWidth() - 34, 0, 25, 30);
			moveUp.addActionListener(e -> {
				if (Main.programLoaded) {
					//if (Requests.getPosFromID(ID) != 0) {

						//MediaShareTab.movePosition(Requests.getPosFromID(ID), Requests.getPosFromID(ID) - 1);
					//}
				}
			});
			final boolean[] moveUpExited = {true};
			final boolean[] moveDownExited = {true};

			moveUp.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					moveUp.setForeground(Defaults.FOREGROUND_B);
					moveUp.setVisible(true);
					moveDown.setVisible(true);
					moveUpExited[0] = false;
				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveUp.setForeground(Defaults.FOREGROUND_A);
					moveUpExited[0] = true;
					if(moveDownExited[0]){
						moveUp.setVisible(false);
						moveDown.setVisible(false);
					}
				}
			});
			//add(moveUp);


			moveDown.setFont(Defaults.SYMBOLSalt.deriveFont(15f));
			moveDown.setUI(clear);
			moveDown.setForeground(Defaults.FOREGROUND_A);
			moveDown.setBackground(new Color(0, 0, 0, 0));
			moveDown.setOpaque(false);
			moveDown.setBorder(BorderFactory.createEmptyBorder());
			moveDown.setBounds(MediaShareTab.getVideosPanel().getButtonWidth() - 34, 30, 25, 100);
			moveDown.addActionListener(e -> {
				if (Main.programLoaded) {
					//if (Requests.getPosFromID(ID) != RequestsTab.getQueueSize() - 1) {
						//Requests.levels.remove(data);
						//Requests.levels.add(Requests.getPosFromID(ID) + 1, data);
						//RequestsTab.movePosition(Requests.getPosFromID(ID), Requests.getPosFromID(ID) + 1);
					//}
				}
			});
			moveDown.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					moveDown.setForeground(Defaults.FOREGROUND_B);
					moveUp.setVisible(true);
					moveDown.setVisible(true);
					moveDownExited[0] = false;

				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveDown.setForeground(Defaults.FOREGROUND_A);
					moveDownExited[0] = true;
					if(moveUpExited[0]){
						moveUp.setVisible(false);
						moveDown.setVisible(false);

					}
				}
			});
			//add(moveDown);

			add(thumbnail);
			add(title);
			add(creator);

			setBackground(new Color(0,0,0,0));
			setUI(SettingsTab.settingsUI);

			setBorder(BorderFactory.createEmptyBorder());
			setPreferredSize(new Dimension(MediaShareTab.getVideosPanel().getButtonWidth()-50, 100));

			addActionListener(e -> {
				boolean selected = this.selected;
				MediaShareTab.getVideosPanel().deselectAll();
				select(!selected);
			});

		} catch (Exception e) {
			e.printStackTrace();
			DialogBox.showDialogBox("Error!", e.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
		}
	}



	public void removeSelf(){
		getParent().remove(this);
	}

	public int getComponentIndex(){
		int i = 0;
		for(Component component : this.getParent().getComponents()){
			if(component.equals(this)){
				return i;
			}
			i++;
		}
		return i;
	}
	public void select(){
		select(false);
	}

	public void select(boolean refresh) {
		selectedID = getComponentIndex();
		this.selected = true;

		setBackground(Defaults.COLOR4);
		setOpaque(false);
		setUI(SettingsTab.selectUI);

		if (refresh) {
			VideoDetailsPanel.setPanel(getVideoData());
		}
	}

	void deselect() {
		this.selected = false;
		setBackground(new Color(0,0,0,0));
		setUI(SettingsTab.settingsUI);
	}

	void resizeButton() {
		moveUp.setBounds(MediaShareTab.getVideosPanel().getButtonWidth() - 34, 0, 25, 30);
		moveDown.setBounds(MediaShareTab.getVideosPanel().getButtonWidth() - 34, 30, 25, 30);
		setPreferredSize(new Dimension(MediaShareTab.getVideosPanel().getButtonWidth()-50, getHeight()));
	}

	public void refresh() {
		selectUI.setBackground(Defaults.COLOR4);
		selectUI.setHover(Defaults.COLOR5);
		selectUI.setSelect(Defaults.COLOR4);
		title.setForeground(Defaults.FOREGROUND_A);
		creator.setForeground(Defaults.FOREGROUND_B);


		moveDown.setForeground(Defaults.FOREGROUND_A);
		moveUp.setForeground(Defaults.FOREGROUND_A);

		if (selected) {

			setBackground(Defaults.COLOR4);
			setOpaque(false);
			setUI(selectUI);

			select();
		} else {
			setUI(SettingsTab.settingsUI);
		}
	}

	public YouTubeVideo getVideoData(){
		return videoData;
	}

	private JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Defaults.FOREGROUND_B);
		label.setFont(Defaults.MAIN_FONT.deriveFont(11f));
		return label;

	}
	private static Color average(Icon icon){

		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();
		int total = 0;

		int avrR = 0;
		int avrG = 0;
		int avrB = 0;
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(!(new Color(bi.getRGB(x, y)).equals(new Color(0,0,0)) || new Color(bi.getRGB(x, y)).getAlpha() == 0)) {
					avrR = avrR + new Color(bi.getRGB(x, y)).getRed();
					avrG = avrG + new Color(bi.getRGB(x, y)).getGreen();
					avrB = avrB + new Color(bi.getRGB(x, y)).getBlue();
					total++;
				}
			}
		}
		avrR = avrR / total;
		avrG = avrG / total;
		avrB = avrB / total;
		return new Color(avrR, avrG, avrB);
	}
}