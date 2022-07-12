package com.alphalaneous.Swing.Components;

import com.alphalaneous.*;
import com.alphalaneous.Images.Assets;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


public class LevelButton extends CurvedButtonAlt {

	private static final JButtonUI selectUI = new JButtonUI();
	public static int selectedID = 0;

	private final String name;
	public long ID;
	private final String requester;
	public boolean selected;

	private final RoundedJButton analyzeButton = new RoundedJButton("\uE7BA", "WARNING");
	private final JButton moveUp = new JButton("\uE010");
	private final JButton moveDown = new JButton("\uE011");


	private final JLabel lAuthorID = new JLabel();
	private final JLabel lRequester = new JLabel();
	private final JLabel lAnalyzed = new JLabel();
	private final JPanel info = new JPanel(new GridLayout(0, 2, 1, 1));
	private final JLabel logo = new JLabel();
	private boolean viewership = false;
	private int gonePoints = 4;
	private final LevelData levelData;


	public LevelButton(LevelData data) {
		super("");
		setCurve(Defaults.globalArc);
		setOpaque(false);
		this.name = data.getGDLevel().name();
		this.ID = data.getGDLevel().id();
		this.levelData = data;
		Optional<String> creatorName = data.getGDLevel().creatorName();
		String author = "";
		if(creatorName.isPresent()){
			author = creatorName.get();
		}

		String difficulty = data.getGDLevel().difficulty().toString();
		boolean epic = data.getGDLevel().isEpic();
		boolean featured = data.getFeatured();
		int starCount = data.getGDLevel().stars();

		String displayRequester;

		if(data.isYouTube()) {
			logo.setIcon(Assets.YouTube);
			displayRequester = data.getDisplayName();
		}
		else {
			logo.setIcon(Assets.Twitch);
			displayRequester = data.getRequester();
		}

		this.requester = data.getRequester();
		double version = data.getGDLevel().levelVersion();
		ImageIcon playerIcon = data.getPlayerIcon();
		int coins = data.getGDLevel().coinCount();
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)){
					Window.destroyContextMenu();
					Window.addContextMenu(new LevelContextMenu(Requests.getPosFromID(data.getGDLevel().id())));
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

			JLabel lName = new JLabel();
			lName.setText(name);
			lAuthorID.setText("By " + author + " (" + ID + ")");
			lRequester.setText("Sent by " + displayRequester);
			JLabel lStarCount = new JLabel();
			lStarCount.setText(String.valueOf(starCount));


			String[] difficulties = {"NA", "easy", "normal", "hard", "harder", "insane"};
			String[] demonDifficulties = {"easy", "medium", "hard", "insane", "extreme"};
			JLabel reqDifficulty = new JLabel();

			if(data.getGDLevel().isAuto()){
				if (epic) {
					reqDifficulty.setIcon(Assets.difficultyIconsEpic.get("auto"));
				} else if (featured) {
					reqDifficulty.setIcon(Assets.difficultyIconsFeature.get("auto"));
				} else if (starCount != 0) {
					reqDifficulty.setIcon(Assets.difficultyIconsNormal.get("auto"));
				} else {
					reqDifficulty.setIcon(new ImageIcon(Assets.difficultyIconsNormal.get("auto").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
				}
			}
			else if(data.getGDLevel().isDemon()){
				for (String difficultyA : demonDifficulties) {
					if (data.getGDLevel().demonDifficulty().name().equalsIgnoreCase(difficultyA)) {
						difficultyA = difficultyA + " demon";
						if (epic) {
							reqDifficulty.setIcon(Assets.difficultyIconsEpic.get(difficultyA));
						} else if (featured) {
							reqDifficulty.setIcon(Assets.difficultyIconsFeature.get(difficultyA));
						} else if (starCount != 0) {
							reqDifficulty.setIcon(Assets.difficultyIconsNormal.get(difficultyA));
						} else {
							reqDifficulty.setIcon(new ImageIcon(Assets.difficultyIconsNormal.get(difficultyA).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
						}
					}
				}
			}
			else {
				for (String difficultyA : difficulties) {
					if (difficulty.equalsIgnoreCase(difficultyA)) {
						if (epic) {
							reqDifficulty.setIcon(Assets.difficultyIconsEpic.get(difficultyA));
						} else if (featured) {
							reqDifficulty.setIcon(Assets.difficultyIconsFeature.get(difficultyA));
						} else if (starCount != 0) {
							reqDifficulty.setIcon(Assets.difficultyIconsNormal.get(difficultyA));
						} else {
							reqDifficulty.setIcon(new ImageIcon(Assets.difficultyIconsNormal.get(difficultyA).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
						}

					}
				}
			}

			if (starCount != 0) {
				reqDifficulty.setBounds(10, 5, 30, 30);
			} else {
				reqDifficulty.setBounds(10, 15, 30, 30);
			}

			add(lName);
			add(lAuthorID);
			add(lRequester);
			add(logo);
			JLabel lPlayerIcon = new JLabel();
			//add(lPlayerIcon);
			add(reqDifficulty);
			JLabel lStar = new JLabel("\uE24A");
			if (starCount != 0) {
				add(lStarCount);
				add(lStar);
			}
			setLayout(null);


			lName.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			lName.setBounds(50, -1, (int) lName.getPreferredSize().getWidth() + 5, 30);

			logo.setBounds(RequestsTab.getLevelsPanel().getButtonWidth() - 25, 0, 30, 30);

			int pos = 0;

			for (int i = 0; i < coins; i++) {
				JLabel coin;
				if (data.getGDLevel().hasCoinsVerified()) {
					coin = new JLabel(Assets.verifiedCoin);
				} else {
					coin = new JLabel(Assets.unverifiedCoin);
				}
				coin.setBounds((int) lName.getPreferredSize().getWidth() + lName.getX() + 5 + pos, 5, 15, 15);
				pos = pos + 10;
				add(coin);
			}

			if (coins != 0) {
				analyzeButton.setBounds((int) lName.getPreferredSize().getWidth() + lName.getX() + 15 + pos, 3, 20, 20);
			} else {
				analyzeButton.setBounds((int) lName.getPreferredSize().getWidth() + lName.getX() + 5 + pos, 3, 20, 20);
			}
			analyzeButton.setFont(Defaults.SYMBOLSalt.deriveFont(18f));
			analyzeButton.setUI(clear);
			analyzeButton.setForeground(Defaults.FOREGROUND_A);
			analyzeButton.setBackground(new Color(0, 0, 0, 0));
			analyzeButton.setVisible(false);
			add(analyzeButton);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if(RequestsTab.getQueueSize() > 1) {
						moveUp.setVisible(true);
						moveDown.setVisible(true);
						logo.setVisible(false);
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveUp.setVisible(false);
					moveDown.setVisible(false);
					logo.setVisible(true);
				}
			});

			info.setBackground(new Color(0, 0, 0, 0));
			info.setBounds(50, 62, RequestsTab.getLevelsPanel().getButtonWidth() - 100, 50);
			info.setOpaque(false);
			info.setVisible(false);
			add(info);


			moveUp.setVisible(false);
			moveDown.setVisible(false);
			moveUp.setFont(Defaults.SYMBOLSalt.deriveFont(15f));
			moveUp.setUI(clear);
			moveUp.setForeground(Defaults.FOREGROUND_A);
			moveUp.setBackground(new Color(0, 0, 0, 0));
			moveUp.setOpaque(false);
			moveUp.setBorder(BorderFactory.createEmptyBorder());
			moveUp.setBounds(RequestsTab.getLevelsPanel().getButtonWidth() - 34, 0, 25, 30);
			moveUp.addActionListener(e -> {
				if (Main.programLoaded) {
					if (Requests.getPosFromID(ID) != 0) {
						//Requests.levels.remove(data);
						//Requests.levels.add(Requests.getPosFromID(ID) - 1, data);
						//com.alphalaneous.Tabs.Window.getLevelsPanel().refreshButtons();
						RequestsTab.movePosition(Requests.getPosFromID(ID), Requests.getPosFromID(ID) - 1);
					}
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
					logo.setVisible(false);
					moveUpExited[0] = false;
				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveUp.setForeground(Defaults.FOREGROUND_A);
					moveUpExited[0] = true;
					if(moveDownExited[0]){
						moveUp.setVisible(false);
						moveDown.setVisible(false);
						logo.setVisible(true);
					}
				}
			});
			add(moveUp);


			moveDown.setFont(Defaults.SYMBOLSalt.deriveFont(15f));
			moveDown.setUI(clear);
			moveDown.setForeground(Defaults.FOREGROUND_A);
			moveDown.setBackground(new Color(0, 0, 0, 0));
			moveDown.setOpaque(false);
			moveDown.setBorder(BorderFactory.createEmptyBorder());
			moveDown.setBounds(RequestsTab.getLevelsPanel().getButtonWidth() - 34, 30, 25, 30);
			moveDown.addActionListener(e -> {
				if (Main.programLoaded) {
					if (Requests.getPosFromID(ID) != RequestsTab.getQueueSize() - 1) {
						//Requests.levels.remove(data);
						//Requests.levels.add(Requests.getPosFromID(ID) + 1, data);
						RequestsTab.movePosition(Requests.getPosFromID(ID), Requests.getPosFromID(ID) + 1);
					}
				}
			});
			moveDown.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					moveDown.setForeground(Defaults.FOREGROUND_B);
					moveUp.setVisible(true);
					moveDown.setVisible(true);
					moveDownExited[0] = false;
					logo.setVisible(false);

				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveDown.setForeground(Defaults.FOREGROUND_A);
					moveDownExited[0] = true;
					if(moveUpExited[0]){
						moveUp.setVisible(false);
						moveDown.setVisible(false);
						logo.setVisible(true);

					}
				}
			});
			add(moveDown);

			lAuthorID.setFont(Defaults.MAIN_FONT.deriveFont(10f));
			lAuthorID.setBounds(50, 20, (int) lAuthorID.getPreferredSize().getWidth() + 5, 20);
			lPlayerIcon.setIcon(playerIcon);
			lPlayerIcon.setBounds(50 + lAuthorID.getPreferredSize().width + 2, 13, 40, 40);


			lRequester.setFont(Defaults.MAIN_FONT.deriveFont(10f));
			lRequester.setBounds(50, 34, (int) lRequester.getPreferredSize().getWidth() + 5, 20);
			lStarCount.setFont(Defaults.MAIN_FONT.deriveFont(12f));
			lStarCount.setBounds(25 - (lStarCount.getPreferredSize().width + lStar.getPreferredSize().width) / 2, 37,
					(int) lStarCount.getPreferredSize().getWidth() + 5, 20);
			lStar.setFont(Defaults.SYMBOLSalt.deriveFont(12f));
			lStar.setBounds(2 + lStarCount.getPreferredSize().width + lStarCount.getX(), 36,
					(int) lStar.getPreferredSize().getWidth() + 5, 20);
			lAnalyzed.setFont(Defaults.MAIN_FONT.deriveFont(12f));

			lName.setForeground(Defaults.FOREGROUND_A);
			lRequester.setForeground(Defaults.FOREGROUND_B);
			lAuthorID.setForeground(Defaults.FOREGROUND_B);
			lAnalyzed.setForeground(Defaults.FOREGROUND_A);
			lStarCount.setForeground(Defaults.FOREGROUND_A);
			lStar.setForeground(Defaults.FOREGROUND_A);

			setBackground(new Color(0,0,0,0));
			setUI(SettingsTab.settingsUI);
			if (starCount > 0) {
				lAnalyzed.setText("");
			} else if (version / 10 < 2) {
				lAnalyzed.setText("Old Level");
			} else {
				lAnalyzed.setText("Analyzing...");
			}

			lAnalyzed.setBounds((int) (RequestsTab.getLevelsPanel().getButtonWidth() - lAnalyzed.getPreferredSize().getWidth()) - 10, 28,
					(int) lAnalyzed.getPreferredSize().getWidth(), 20);

			setBorder(BorderFactory.createEmptyBorder());
			setPreferredSize(new Dimension(RequestsTab.getLevelsPanel().getButtonWidth()-50, 60));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isMiddleMouseButton(e)) {

							try {
								Utilities.openURL(new URI("http://www.gdbrowser.com/" + ID));
							} catch (URISyntaxException ex) {
								ex.printStackTrace();
							}

					}
				}
			});

			addActionListener(e -> {
				boolean selected = this.selected;
				RequestsTab.getLevelsPanel().deselectAll();
				select(!selected);
			});

		} catch (Exception e) {
			e.printStackTrace();
			DialogBox.showDialogBox("Error!", e.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
		}
	}

	public long getID() {
		return ID;
	}

	public String getUsername() {
		return name;
	}

	public String getRequester() {
		return requester;
	}

	public void setViewership(boolean viewer) {
		if(!levelData.isYouTube()) {
			RequestsTab.getRequest(Requests.getPosFromID(ID)).getLevelData().setViewership(viewership);

			if (viewer) {
				lRequester.setForeground(Defaults.FOREGROUND_B);
				viewership = true;
				gonePoints = 4;
				markedForRemoval = false;
			} else {
				gonePoints = gonePoints - 1;
				if (gonePoints <= 0) {
					lRequester.setForeground(Color.RED);
					viewership = false;
					gonePoints = 0;
					markedForRemoval = true;
				}
			}
		}
	}
	private boolean markedForRemoval = false;

	public void resetGonePoints(){
		markedForRemoval = false;
		gonePoints = 4;
	}

	public boolean isMarkedForRemoval(){
		if(selected) return false;
		else return markedForRemoval;
	}

	public void removeSelfViewer(){
		Requests.addRemovedForOffline(this);
		getParent().remove(this);
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
			LevelDetailsPanel.setPanel(getLevelData());
		}
	}




	void deselect() {
		if (this.selected) {
			setPreferredSize(new Dimension(RequestsTab.getLevelsPanel().getButtonWidth()-50, 60));
		}
		this.selected = false;
		setBackground(new Color(0,0,0,0));
		setUI(SettingsTab.settingsUI);
	}

	void resizeButton() {
		moveUp.setBounds(RequestsTab.getLevelsPanel().getButtonWidth() - 34, 0, 25, 30);
		moveDown.setBounds(RequestsTab.getLevelsPanel().getButtonWidth() - 34, 30, 25, 30);
		logo.setBounds(RequestsTab.getLevelsPanel().getButtonWidth() - 25, 0, 30, 30);

		info.setBounds(50, 60, RequestsTab.getLevelsPanel().getButtonWidth() - 100, 50);
		setPreferredSize(new Dimension(RequestsTab.getLevelsPanel().getButtonWidth()-50, getHeight()));
	}

	public void refresh() {
		selectUI.setBackground(Defaults.COLOR4);
		selectUI.setHover(Defaults.COLOR5);
		selectUI.setSelect(Defaults.COLOR4);
		for (Component component : getComponents()) {
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND_A);
			}
		}
		moveDown.setForeground(Defaults.FOREGROUND_A);
		moveUp.setForeground(Defaults.FOREGROUND_A);
		analyzeButton.setForeground(Defaults.FOREGROUND_A);
		lRequester.setForeground(Defaults.FOREGROUND_B);
		lAuthorID.setForeground(Defaults.FOREGROUND_B);
		if (selected) {

			setBackground(Defaults.COLOR4);
			setOpaque(false);
			setUI(selectUI);

			select();
		} else {
			setUI(SettingsTab.settingsUI);
		}
	}

	public LevelData getLevelData(){
		return levelData;
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