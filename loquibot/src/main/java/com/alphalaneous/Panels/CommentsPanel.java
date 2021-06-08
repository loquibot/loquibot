package com.alphalaneous.Panels;

import com.alphalaneous.*;
import com.alphalaneous.Components.JButtonUI;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.Components.ScrollbarUI;
import com.alphalaneous.Components.SmoothScrollPane;
import jdash.client.exception.GDClientException;
import jdash.common.IconType;
import jdash.common.entity.GDComment;
import jdash.common.entity.GDUser;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

public class CommentsPanel {

    //todo fix framebuffer issues

    private static final JPanel panel = new JPanel();
    private static final JPanel mainPanel = new JPanel(null);
    private static final JButtonUI buttonUI = new JButtonUI();
    private static final int width = 300;
    private static final JPanel window = new JPanel();
    private static final JButtonUI newUI = new JButtonUI();
    private static final JScrollPane scrollPane = new SmoothScrollPane(panel);
    private static final JPanel buttons = new JPanel();
    private static boolean topC = false;
    private static int page = 0;

    public static void createPanel() {

        //region Panel attributes
        panel.setLayout(null);
        panel.setBounds(0, 0, width, 0);
        int height = 350;
        mainPanel.setBounds(1, 1, width, height + 30);
        panel.setBackground(Defaults.SUB_MAIN);
        panel.setPreferredSize(new Dimension(width, 0));
        //endregion
        buttonUI.setBackground(Defaults.TOP);
        buttonUI.setHover(Defaults.BUTTON_HOVER);
        buttonUI.setSelect(Defaults.SELECT);
        //region ScrollPane attributes

        scrollPane.setBounds(0, 30, width, height - 40);
        mainPanel.add(scrollPane);
        //endregion

        //region Buttons Panel attributes
        buttons.setLayout(null);
        buttons.setBounds(0, mainPanel.getHeight() - 40, width, 40);
        buttons.setBackground(Defaults.TOP);
        mainPanel.add(buttons);
        //endregion


        //region Create Previous Page Button
        JButton prev = createButton("\uF305", 0, "$PREV_PAGE$");
        prev.addActionListener(e -> {
            if (page != 0) {
                page = page - 2;
                try {
                    loadComments(page, topC);
                } catch (Exception ignored) {
                }
            }

        });
        buttons.add(prev);
        //endregion

        JButton next = createButton("\uF304", 35, "$NEXT_PAGE$");
        next.addActionListener(e -> {
            page = page + 2;
            if (!loadComments(page, topC)) {
                page = page - 2;
                try {
                    loadComments(page, topC);
                } catch (Exception f) {
                    f.printStackTrace();
                }

            }
        });
        buttons.add(next);

        JButton top = createButton("\uF138", 90, "$TOP_COMMENTS$");
        top.addActionListener(e -> {
            page = 0;
            try {
                loadComments(0, true);
            } catch (Exception ignored) {
            }

        });
        buttons.add(top);

        JButton newest = createButton("\uF22B", 125, "$LATEST_COMMENTS$");
        newest.addActionListener(e -> {
            topC = false;
            page = 0;
            try {
                loadComments(0, false);
            } catch (Exception ignored) {

            }
        });
        buttons.add(newest);


        newUI.setBackground(Defaults.MAIN);
        newUI.setHover(Defaults.HOVER);
        panel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 4));
        panel.setVisible(false);
        window.add(mainPanel);
    }

    //endregion
    public static void unloadComments(boolean reset) {
        panel.setVisible(false);
        if (reset) {
            topC = false;
            page = 0;
        }
        panel.removeAll();
        panel.setPreferredSize(new Dimension(width, 0));
    }

    public static JPanel getComWindow() {
        scrollPane.setBounds(0, 0, width, 472);
        buttons.setBounds(0, 472, width, 40);

        return mainPanel;
    }

    public static void resetDimensions(int width, int height) {
        scrollPane.setBounds(0, 0, width, height - 80);
        buttons.setBounds(0, height - 80, width, 40);
    }

    public static boolean loadComments(int page, boolean top) {
        return loadComments(page, top, LevelButton.selectedID);
    }

    public static boolean loadComments(int page, boolean top, int pos) {
        topC = top;
        int width = CommentsPanel.width - 15;
        if (Requests.levels.size() == 0) {
            return false;
        }
        try {
            int panelHeight = 0;
            panel.removeAll();
            panel.setVisible(false);

            List<GDComment> commentA = GDAPI.getGDComments(Requests.levels.get(pos).getLevelData().id(), top, page);

            if (commentA == null || commentA.size() == 0) {
                return false;
            }
            for (int i = 0; i < commentA.size(); i++) {
				if (Requests.levels.size() != 0) {
					String percent;
					GDUser username = commentA.get(i).author().get();
					String likes = String.valueOf(commentA.get(i).likes());
					String comment = String.format("<html><div WIDTH=%d>%s</div></html>", width - 15, StringEscapeUtils.unescapeHtml4(commentA.get(i).content()));
					try {
						percent = StringEscapeUtils.unescapeHtml4(commentA.get(i).percentage().get() + "%");
					} catch (Exception e) {
						percent = "";
					}
					if (percent.equalsIgnoreCase("0%")) {
						percent = "";
					}
					JPanel cmtPanel = new JPanel(null);
					cmtPanel.setBackground(Defaults.SUB_MAIN);

					JLabel commenter = new JLabel(username.name());
					commenter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					commenter.setFont(Defaults.MAIN_FONT.deriveFont(12f));
					if (username.name().equalsIgnoreCase(Requests.levels.get(LevelButton.selectedID).getLevelData().creatorName().get())) {
						commenter.setForeground(new Color(47, 62, 195));
					} else {
						commenter.setForeground(Defaults.FOREGROUND);
					}
					commenter.setBounds(30, 2, commenter.getPreferredSize().width, 18);
					int finalI = i;
					final List<GDComment>[] finalCommentA = new List[]{commentA};
					commenter.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							super.mouseClicked(e);
							if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
								try {
									Runtime rt = Runtime.getRuntime();
									rt.exec("rundll32 url.dll,FileProtocolHandler " + "https://www.gdbrowser.com/profile/" + finalCommentA[0].get(finalI).author().get().name());
								} catch (IOException ex) {
									ex.printStackTrace();
								}
							}
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							super.mouseEntered(e);
							int center = (commenter.getPreferredSize().width) / 2;
							commenter.setFont(Defaults.MAIN_FONT.deriveFont(13f));
							commenter.setBounds(30 + center - (commenter.getPreferredSize().width) / 2, 2, commenter.getPreferredSize().width + 5, 18);
						}

						@Override
						public void mouseExited(MouseEvent e) {
							super.mouseExited(e);
							commenter.setFont(Defaults.MAIN_FONT.deriveFont(12f));
							commenter.setBounds(30, 2, commenter.getPreferredSize().width, 18);
						}
					});

					JLabel percentLabel = new JLabel(percent);
					percentLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
					percentLabel.setForeground(Defaults.FOREGROUND2);
					percentLabel.setBounds(commenter.getPreferredSize().width + 42, 2, percentLabel.getPreferredSize().width + 5, 18);


					JLabel likeIcon = new JLabel();
					if (Integer.parseInt(likes.replaceAll("%", "")) < 0) {
						likeIcon.setText("\uF139");
						likeIcon.setBounds(width - 20, 7, (int) (width * 0.5), 18);
					} else {
						likeIcon.setText("\uF138");
						likeIcon.setBounds(width - 20, 2, (int) (width * 0.5), 18);
					}
					likeIcon.setFont(Defaults.SYMBOLS.deriveFont(14f));
					likeIcon.setForeground(Defaults.FOREGROUND);


					JLabel likesLabel = new JLabel(likes);
					likesLabel.setFont(Defaults.MAIN_FONT.deriveFont(10f));
					likesLabel.setForeground(Defaults.FOREGROUND);
					likesLabel.setBounds(width - likesLabel.getPreferredSize().width - 26, 4, likesLabel.getPreferredSize().width + 5, 18);
					JLabel playerIcon = new JLabel();
					GDUser gdUser = commentA.get(i).author().get();
					new Thread(() -> {
						try {
							playerIcon.setIcon(GDAPI.getIcon(gdUser.mainIconType().get(), gdUser.mainIconId().get(), gdUser.color1Id(), gdUser.color2Id(), gdUser.hasGlowOutline()));
						} catch (Exception e) {
							playerIcon.setIcon(GDAPI.getIcon(IconType.CUBE, 1, 1, 1, false));
						}
					}).start();
					playerIcon.setBounds(2, -5, 30 + 2, 30 + 2);

					JLabel content = new JLabel(comment);
					content.setFont(Defaults.MAIN_FONT.deriveFont(12f));
					content.setForeground(Defaults.FOREGROUND);
					content.setBounds(9, 24, width - 15, content.getPreferredSize().height);
					panelHeight = panelHeight + 32 + content.getPreferredSize().height;

					cmtPanel.add(commenter);
					cmtPanel.add(content);
					cmtPanel.add(percentLabel);
					cmtPanel.add(likesLabel);
					cmtPanel.add(likeIcon);
					cmtPanel.add(playerIcon);

					cmtPanel.setPreferredSize(new Dimension(width, 28 + content.getPreferredSize().height));

					panel.add(cmtPanel);
					panel.setPreferredSize(new Dimension(width, panelHeight));
					Thread.sleep(0);
				}
			}
            scrollPane.getViewport().setViewPosition(new Point(0, 0));
            panel.setVisible(true);
            return true;
            //}
        } catch (GDClientException e) {
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static JButton createButton(String icon, int x, String tooltip) {
        JButton button = new RoundedJButton(icon, tooltip);
        button.setFont(Defaults.SYMBOLS.deriveFont(16f));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setForeground(Defaults.FOREGROUND);
        button.setBackground(Defaults.TOP);
        button.setUI(buttonUI);
        button.setBounds(x + 5, 5, 30, 30);
        return button;
    }

    public static void refreshUI() {
        buttonUI.setBackground(Defaults.TOP);
        buttonUI.setHover(Defaults.BUTTON_HOVER);
        buttonUI.setSelect(Defaults.SELECT);
        newUI.setBackground(Defaults.MAIN);
        newUI.setHover(Defaults.HOVER);
        newUI.setSelect(Defaults.SELECT);
        scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());
        scrollPane.setBackground(Defaults.SUB_MAIN);
        scrollPane.getViewport().setBackground(Defaults.SUB_MAIN);

        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                component.setBackground(Defaults.SUB_MAIN);
                for (Component component1 : ((JPanel) component).getComponents()) {
                    if (component1 instanceof JLabel) {
                        if (((JLabel) component1).getText().contains("%")) {
                            component1.setForeground(Defaults.FOREGROUND2);
                        } else if (((JLabel) component1).getText().equalsIgnoreCase(Requests.levels.get(LevelButton.selectedID).getLevelData().creatorName().get())) {
                            component1.setForeground(new Color(16, 164, 0));
                        } else {
                            component1.setForeground(Defaults.FOREGROUND);
                        }
                    }
                    if (component1 instanceof JTextPane) {
                        component1.setForeground(Defaults.FOREGROUND);
                    }
                }
            }
        }
        for (Component component : buttons.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(Defaults.TOP);
                component.setForeground(Defaults.FOREGROUND);
            }
        }
        panel.setBackground(Defaults.SUB_MAIN);
        buttons.setBackground(Defaults.TOP);
    }

}

