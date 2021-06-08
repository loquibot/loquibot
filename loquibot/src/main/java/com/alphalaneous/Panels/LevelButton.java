package com.alphalaneous.Panels;

import com.alphalaneous.*;
import com.alphalaneous.Components.CurvedButtonAlt;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import jdash.common.entity.GDSong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

import static com.alphalaneous.Defaults.defaultUI;

public class LevelButton extends CurvedButtonAlt {

	private static final JButtonUI selectUI = new JButtonUI();
	private static final JButtonUI warningUI = new JButtonUI();
	private static final JButtonUI noticeUI = new JButtonUI();
	private static final JButtonUI warningSelectUI = new JButtonUI();
	private static final JButtonUI noticeSelectUI = new JButtonUI();
	private static int prevSelectedID = 0;
	public static int selectedID = 0;


	private final String name;
	public long ID;
	private final String requester;
	private boolean image;
	private boolean vulgar;
	public boolean selected;

	private final RoundedJButton analyzeButton = new RoundedJButton("\uE7BA", "WARNING");
	private final JButton moveUp = new JButton("\uE010");
	private final JButton moveDown = new JButton("\uE011");


	private final JLabel lAuthorID = new JLabel();
	private final JLabel lRequester = new JLabel();
	private final JLabel lAnalyzed = new JLabel();
	private final JPanel info = new JPanel(new GridLayout(0, 2, 1, 1));
	private boolean viewership = false;
	private int gonePoints = 3;

	public LevelButton(LevelData data) {
		super("");
		this.name = data.getLevelData().name();
		this.ID = data.getLevelData().id();

		Optional<String> creatorName = data.getLevelData().creatorName();
		String author = "";
		if(creatorName.isPresent()){
			author = creatorName.get();
		}

		String difficulty = data.getLevelData().difficulty().toString();
		boolean epic = data.getEpic();
		boolean featured = data.getFeatured();
		int starCount = data.getLevelData().stars();
		this.requester = data.getRequester();
		double version = data.getLevelData().levelVersion();
		ImageIcon playerIcon = data.getPlayerIcon();
		int coins = data.getLevelData().coinCount();
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)){
					Window.destroyContextMenu();
					Window.addContextMenu(new LevelContextMenu(Requests.getPosFromID(data.getLevelData().id())));
				}
			}
		});
		try {

			JButtonUI clear = new JButtonUI();
			clear.setBackground(new Color(0, 0, 0, 0));
			clear.setHover(new Color(0, 0, 0, 0));
			clear.setSelect(new Color(0, 0, 0, 0));

			selectUI.setBackground(Defaults.SELECT);
			selectUI.setHover(Defaults.BUTTON_HOVER);

			warningUI.setBackground(new Color(150, 0, 0));
			warningUI.setHover(new Color(170, 0, 0));
			warningUI.setSelect(new Color(150, 0, 0));

			noticeUI.setBackground(new Color(150, 150, 0));
			noticeUI.setHover(new Color(170, 170, 0));
			noticeUI.setSelect(new Color(150, 150, 0));

			warningSelectUI.setBackground(new Color(190, 0, 0));
			warningSelectUI.setHover(new Color(200, 0, 0));
			warningSelectUI.setSelect(new Color(150, 0, 0));

			noticeSelectUI.setBackground(new Color(190, 190, 0));
			noticeSelectUI.setHover(new Color(200, 200, 0));
			noticeSelectUI.setSelect(new Color(150, 150, 0));

			JLabel lName = new JLabel();
			lName.setText(name);
			lAuthorID.setText("By " + author + " (" + ID + ")");
			lRequester.setText("Sent by " + requester);
			JLabel lStarCount = new JLabel();
			lStarCount.setText(String.valueOf(starCount));


			String[] difficulties = {"NA", "easy", "normal", "hard", "harder", "insane"};
			String[] demonDifficulties = {"easy", "medium", "hard", "insane", "extreme"};
			JLabel reqDifficulty = new JLabel();

			if(data.getLevelData().isAuto()){
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
			else if(data.getLevelData().isDemon()){
				for (String difficultyA : demonDifficulties) {
					if (data.getLevelData().demonDifficulty().name().equalsIgnoreCase(difficultyA)) {
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
			JLabel lPlayerIcon = new JLabel();
			add(lPlayerIcon);
			add(reqDifficulty);
			JLabel lStar = new JLabel("\uE24A");
			if (starCount != 0) {
				add(lStarCount);
				add(lStar);
			}
			setLayout(null);


			lName.setFont(Defaults.MAIN_FONT.deriveFont(18f));
			lName.setBounds(50, -1, (int) lName.getPreferredSize().getWidth() + 5, 30);

			int pos = 0;

			for (int i = 0; i < coins; i++) {
				JLabel coin;
				if (data.getLevelData().hasCoinsVerified()) {
					coin = new JLabel(Assets.verifiedCoin);
				} else {
					coin = new JLabel(Assets.unverifiedCoin);
				}
				coin.setBounds((int) lName.getPreferredSize().getWidth() + lName.getX() + 5 + pos, 7, 15, 15);
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
			analyzeButton.setForeground(Defaults.FOREGROUND);
			analyzeButton.setBackground(new Color(0, 0, 0, 0));
			analyzeButton.setVisible(false);
			add(analyzeButton);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if(Requests.levels.size() > 1) {
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

			info.setBackground(new Color(0, 0, 0, 0));
			info.setBounds(50, 62, LevelsPanel.getButtonWidth() - 100, 50);
			info.setOpaque(false);
			info.setVisible(false);
			add(info);


			moveUp.setVisible(false);
			moveDown.setVisible(false);
			moveUp.setFont(Defaults.SYMBOLSalt.deriveFont(15f));
			moveUp.setUI(clear);
			moveUp.setForeground(Defaults.FOREGROUND);
			moveUp.setBackground(new Color(0, 0, 0, 0));
			moveUp.setOpaque(false);
			moveUp.setBorder(BorderFactory.createEmptyBorder());
			moveUp.setBounds(LevelsPanel.getButtonWidth() - 34, 0, 25, 30);
			moveUp.addActionListener(e -> {
				if (Main.programLoaded) {
					if (Requests.getPosFromID(ID) != 0) {
						Requests.levels.remove(data);
						Requests.levels.add(Requests.getPosFromID(ID) - 1, data);
						LevelsPanel.refreshButtons();
						//LevelsPanel.movePosition(Requests.getPosFromID(ID), Requests.getPosFromID(ID) - 1);
					}
				}
			});
			final boolean[] moveUpExited = {true};
			final boolean[] moveDownExited = {true};

			moveUp.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					moveUp.setForeground(Defaults.FOREGROUND2);
					moveUp.setVisible(true);
					moveDown.setVisible(true);
					moveUpExited[0] = false;
				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveUp.setForeground(Defaults.FOREGROUND);
					moveUpExited[0] = true;
					if(moveDownExited[0]){
						moveUp.setVisible(false);
						moveDown.setVisible(false);
					}
				}
			});
			add(moveUp);


			moveDown.setFont(Defaults.SYMBOLSalt.deriveFont(15f));
			moveDown.setUI(clear);
			moveDown.setForeground(Defaults.FOREGROUND);
			moveDown.setBackground(new Color(0, 0, 0, 0));
			moveDown.setOpaque(false);
			moveDown.setBorder(BorderFactory.createEmptyBorder());
			moveDown.setBounds(LevelsPanel.getButtonWidth() - 34, 30, 25, 30);
			moveDown.addActionListener(e -> {
				if (Main.programLoaded) {
					if (Requests.getPosFromID(ID) != Requests.levels.size() - 1) {
						Requests.levels.remove(data);
						Requests.levels.add(Requests.getPosFromID(ID) + 1, data);
						LevelsPanel.refreshButtons();
						//LevelsPanel.movePosition(Requests.getPosFromID(ID), Requests.getPosFromID(ID) + 1);
					}
				}
			});
			moveDown.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					moveDown.setForeground(Defaults.FOREGROUND2);
					moveUp.setVisible(true);
					moveDown.setVisible(true);
					moveDownExited[0] = false;

				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveDown.setForeground(Defaults.FOREGROUND);
					moveDownExited[0] = true;
					if(moveUpExited[0]){
						moveUp.setVisible(false);
						moveDown.setVisible(false);
					}
				}
			});
			add(moveDown);

			lAuthorID.setFont(Defaults.MAIN_FONT.deriveFont(12f));
			lAuthorID.setBounds(50, 23, (int) lAuthorID.getPreferredSize().getWidth() + 5, 20);
			lPlayerIcon.setIcon(playerIcon);
			lPlayerIcon.setBounds(50 + lAuthorID.getPreferredSize().width + 2, 13, 40, 40);


			lRequester.setFont(Defaults.MAIN_FONT.deriveFont(12f));
			lRequester.setBounds(50, 40, (int) lRequester.getPreferredSize().getWidth() + 5, 20);
			lStarCount.setFont(Defaults.MAIN_FONT.deriveFont(12f));
			lStarCount.setBounds(25 - (lStarCount.getPreferredSize().width + lStar.getPreferredSize().width) / 2, 37,
					(int) lStarCount.getPreferredSize().getWidth() + 5, 20);
			lStar.setFont(Defaults.SYMBOLSalt.deriveFont(12f));
			lStar.setBounds(2 + lStarCount.getPreferredSize().width + lStarCount.getX(), 36,
					(int) lStar.getPreferredSize().getWidth() + 5, 20);
			lAnalyzed.setFont(Defaults.MAIN_FONT.deriveFont(12f));

			lName.setForeground(Defaults.FOREGROUND);
			lRequester.setForeground(Defaults.FOREGROUND2);
			lAuthorID.setForeground(Defaults.FOREGROUND2);
			lAnalyzed.setForeground(Defaults.FOREGROUND);
			lStarCount.setForeground(Defaults.FOREGROUND);
			lStar.setForeground(Defaults.FOREGROUND);

			setBackground(Defaults.MAIN);
			setUI(defaultUI);
			if (starCount > 0) {
				lAnalyzed.setText("");
			} else if (version / 10 < 2) {
				lAnalyzed.setText("Old Level");
			} else {
				lAnalyzed.setText("Analyzing...");
			}

			lAnalyzed.setBounds((int) (LevelsPanel.getButtonWidth() - lAnalyzed.getPreferredSize().getWidth()) - 10, 28,
					(int) lAnalyzed.getPreferredSize().getWidth(), 20);

			setBorder(BorderFactory.createEmptyBorder());
			setPreferredSize(new Dimension(LevelsPanel.getButtonWidth()-50, 60));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isMiddleMouseButton(e)) {

							try {
								Runtime rt = Runtime.getRuntime();
								rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://www.gdbrowser.com/" + ID);
							} catch (IOException ex) {
								ex.printStackTrace();
							}

					}
				}
			});

			addActionListener(e -> {
				boolean selected = this.selected;
				LevelsPanel.deselectAll();
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

	public boolean getImage() {
		return image;
	}

	public void setViewership(boolean viewer) {
		if (viewer) {
			lRequester.setForeground(Defaults.FOREGROUND2);
			viewership = true;
			gonePoints = 3;
		} else {
			gonePoints = gonePoints - 1;
			if (gonePoints == 0) {
				lRequester.setForeground(Color.RED);
				viewership = false;
				gonePoints = 0;
			}
		}
		Requests.levels.get(Requests.getPosFromID(ID)).setViewership(viewership);
	}

	void setAnalyzed(boolean analyzed, boolean image, boolean vulgar) {
		this.image = image;
		this.vulgar = vulgar;

		if (image) {
			analyzeButton.setTooltip("IMAGE HACK");
			analyzeButton.setVisible(true);
			setBackground(new Color(150, 0, 0));
			setUI(warningUI);
		} else if (vulgar) {
			analyzeButton.setTooltip("VULGAR LANGUAGE");
			analyzeButton.setVisible(true);
			setBackground(new Color(150, 150, 0));
			setUI(noticeUI);
		} else if (analyzed) {
			lAnalyzed.setText("Analyzed");
		} else {
			lAnalyzed.setText("Failed Analyzing");
		}
		lAnalyzed.setBounds((int) (LevelsPanel.getButtonWidth() - lAnalyzed.getPreferredSize().getWidth()) - 10, 28,
				(int) lAnalyzed.getPreferredSize().getWidth(), 20);

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
		if(Requests.levels.size() == 1){
			prevSelectedID = -1;
		}
		else {
			prevSelectedID = selectedID;
		}
		selectedID = getComponentIndex();
		this.selected = true;
		if (image) {
			setUI(warningSelectUI);
			setBackground(new Color(200, 0, 0));
		} else if (vulgar) {
			setUI(noticeSelectUI);
			setBackground(new Color(200, 150, 0));

		} else {
			setBackground(Defaults.SELECT);
			setUI(selectUI);

		}
		info.setVisible(false);

		info.removeAll();
		if (Requests.getPosFromID(ID) != -1) {
			JLabel likes = createLabel("Likes: " + Requests.levels.get(Requests.getPosFromID(ID)).getLevelData().likes());
			JLabel downloads = createLabel("Downloads: " + Requests.levels.get(Requests.getPosFromID(ID)).getLevelData().downloads());
			JLabel length = createLabel("Length: " + Requests.levels.get(Requests.getPosFromID(ID)).getLevelData().length().toString());
			JLabel songID;
			JLabel songName;
			JLabel songAuthor;
			Optional<GDSong> song = Requests.levels.get(Requests.getPosFromID(ID)).getLevelData().song();

			if(song.isPresent()) {
				songName = createLabel("Song: " + song.get().title());
				songAuthor = createLabel("Song Artist: " + song.get().artist());
				if(song.get().isCustom()) {
					songID = createLabel("Song ID: " + song.get().id());
				}
				else {
					songID = createLabel("Default Song");
				}
			}
			else {
				songName = createLabel("");
				songAuthor = createLabel("");
				songID = createLabel("No song info");
			}

			info.add(likes);
			info.add(downloads);
			info.add(length);
			info.add(songID);
			info.add(songName);
			info.add(songAuthor);
		}
		info.setVisible(true);
		setPreferredSize(new Dimension(LevelsPanel.getButtonWidth()-50, 120));

		if (refresh) {
			if (Requests.levels.size() != 0) {
				new Thread(() -> {
					CommentsPanel.unloadComments(true);
					CommentsPanel.loadComments(0, false);
				}).start();
			}
			InfoPanel.refreshInfo();
		}
	}

	void deselect() {
		if (this.selected) {
			info.setVisible(false);
			info.removeAll();
			setPreferredSize(new Dimension(LevelsPanel.getButtonWidth()-50, 60));
		}
		this.selected = false;
		if (image) {
			setUI(warningUI);
			setBackground(new Color(150, 0, 0));
		} else if (vulgar) {
			setUI(noticeUI);
			setBackground(new Color(150, 150, 0));
		} else {
			setBackground(Defaults.MAIN);
			setUI(defaultUI);
		}

	}

	void resizeButton(int width) {
		moveUp.setBounds(LevelsPanel.getButtonWidth() - 34, 0, 25, 30);
		moveDown.setBounds(LevelsPanel.getButtonWidth() - 34, 30, 25, 30);
		info.setBounds(50, 60, LevelsPanel.getButtonWidth() - 100, 50);
		setPreferredSize(new Dimension(LevelsPanel.getButtonWidth()-50, getHeight()));
	}

	public void refresh(boolean image, boolean vulgar) {
		for (Component component : getComponents()) {
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);
			}
		}
		moveDown.setForeground(Defaults.FOREGROUND);
		moveUp.setForeground(Defaults.FOREGROUND);

		analyzeButton.setForeground(Defaults.FOREGROUND);
		lRequester.setForeground(Defaults.FOREGROUND2);
		lAuthorID.setForeground(Defaults.FOREGROUND2);
		if (selected) {
			if (image) {
				setBackground(new Color(200, 0, 0));
				setUI(warningSelectUI);
			} else if (vulgar) {
				setBackground(new Color(200, 150, 0));
				setUI(noticeSelectUI);
			} else {
				setBackground(Defaults.MAIN);
				setUI(selectUI);
			}
			select();
		} else {
			if (image) {
				setBackground(new Color(150, 0, 0));
				setUI(warningUI);
			} else if (vulgar) {
				setBackground(new Color(150, 150, 0));
				setUI(noticeUI);
			} else {
				setBackground(Defaults.MAIN);
				setUI(defaultUI);
			}
		}
	}

	public void refresh() {
		selectUI.setBackground(Defaults.SELECT);
		selectUI.setHover(Defaults.BUTTON_HOVER);
		selectUI.setSelect(Defaults.SELECT);
		for (Component component : getComponents()) {
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);
			}
		}
		moveDown.setForeground(Defaults.FOREGROUND);
		moveUp.setForeground(Defaults.FOREGROUND);
		analyzeButton.setForeground(Defaults.FOREGROUND);
		lRequester.setForeground(Defaults.FOREGROUND2);
		lAuthorID.setForeground(Defaults.FOREGROUND2);
		if (selected) {
			if (image) {
				setBackground(new Color(200, 0, 0));
				setUI(warningSelectUI);
			} else if (vulgar) {
				setBackground(new Color(200, 150, 0));
				setUI(noticeSelectUI);
			} else {
				setBackground(Defaults.MAIN);
				setUI(selectUI);
			}
			select();
		} else {
			if (image) {
				setBackground(new Color(150, 0, 0));
				setUI(warningUI);
			} else if (vulgar) {
				setBackground(new Color(150, 150, 0));
				setUI(noticeUI);
			} else {
				setBackground(Defaults.MAIN);
				setUI(defaultUI);
			}
		}
	}

	JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Defaults.FOREGROUND2);
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