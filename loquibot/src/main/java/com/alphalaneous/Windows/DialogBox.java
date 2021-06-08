package com.alphalaneous.Windows;

import com.alphalaneous.Components.LangButton;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Defaults;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URL;

import static com.alphalaneous.Defaults.defaultUI;

public class DialogBox {

	public static boolean active = false;
	private static JFrame frame = new JFrame();
	private static boolean setFocus = true;

	public static String showDialogBox(String title, String info, String subInfo, String[] options) {

		return showDialogBox(title, info, subInfo, options, new Object[]{});
	}

	public static void setUnfocusable() {
		setFocus = false;
	}

	public static String showDialogBox(String title, String info, String subInfo, String[] options, Object[] args) {
		final String[] value = {null};


		if (!active) {
			active = true;
			frame = new JFrame();
			frame.setFocusableWindowState(setFocus);
			frame.setFocusable(setFocus);
			frame.setTitle("GDBoard - Dialog");
			URL iconURL = Window.class.getResource("/Icons/windowIcon.png");
			ImageIcon icon = new ImageIcon(iconURL);
			Image newIcon = icon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
			frame.setIconImage(newIcon);
			JPanel textPanel = new JPanel();
			JPanel titlePanel = new JPanel();
			textPanel.setOpaque(false);
			titlePanel.setOpaque(false);
			textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
			JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 6, 6));
			buttonPanel.setOpaque(false);
			frame.setUndecorated(true);
			frame.setLayout(null);
			LangLabel titleLabel = new LangLabel("");
			titleLabel.setTextLangFormat(title, args);
			LangLabel infoLabel = new LangLabel(info);
			LangLabel subInfoLabel = new LangLabel(subInfo);
			infoLabel.setOpaque(false);
			infoLabel.setBackground(Defaults.TOP);
			subInfoLabel.setOpaque(false);
			subInfoLabel.setBackground(Defaults.TOP);

			JFrame finalFrame = frame;
			MouseInputAdapter mia = new MouseInputAdapter() {
				Point location;
				Point pressed;

				public void mousePressed(MouseEvent me) {
					pressed = me.getLocationOnScreen();
					location = finalFrame.getLocation();
				}

				public void mouseDragged(MouseEvent me) {
					Point dragged = me.getLocationOnScreen();
					int x = (int) (location.getX() + dragged.getX() - pressed.getX());
					int y = (int) (location.getY() + dragged.getY() - pressed.getY());
					finalFrame.setLocation(x, y);
				}
			};
			frame.getContentPane().addMouseListener(mia);
			frame.getContentPane().addMouseMotionListener(mia);

			titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
			infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			subInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));


			titlePanel.setBounds(30, 25, 340, 110);
			titlePanel.setBackground(new Color(0, 0, 0, 0));

			textPanel.setBounds(30, 70, 340, 110);
			textPanel.setBackground(new Color(0, 0, 0, 0));

			buttonPanel.setBounds(30, 140, 340, 35);
			buttonPanel.setBackground(new Color(0, 0, 0, 0));


			titlePanel.add(titleLabel);
			textPanel.add(infoLabel);
			textPanel.add(subInfoLabel);

			frame.setSize(new Dimension(400, 200));
			frame.setPreferredSize(new Dimension(400, 400));


			frame.getRootPane().setBorder(new LineBorder(Defaults.ACCENT, 1));
			frame.getContentPane().setBackground(Defaults.TOP);
			titleLabel.setForeground(Defaults.FOREGROUND);
			infoLabel.setForeground(Defaults.FOREGROUND);
			subInfoLabel.setForeground(Defaults.FOREGROUND);
			frame.setLocationRelativeTo(Window.windowFrame);
			//frame.setLocation((int) (Defaults.screenSize.getX() + Defaults.screenSize.getWidth() / 2 - 200), (int) (Defaults.screenSize.getY() + Defaults.screenSize.getHeight() / 2 - 100));


			for (String option : options) {
				LangButton button = createButton(option);
				button.setForeground(Defaults.FOREGROUND);
				button.setBackground(Defaults.MAIN);
				button.addActionListener(e -> value[0] = button.getIdentifier());
				buttonPanel.add(button);
			}


			frame.setResizable(false);
			frame.add(titlePanel);
			frame.add(textPanel);
			frame.add(buttonPanel);
			frame.setAlwaysOnTop(true);
			frame.invalidate();
			frame.revalidate();
			frame.setVisible(true);

			while (value[0] == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			frame.setVisible(false);
			frame.removeAll();
			frame.dispose();
			active = false;
			setFocus = true;
			return value[0];
		} else {
			frame.requestFocus();
		}
		return "";
	}

	private static LangButton createButton(String text) {

		LangButton button = new LangButton(text);

		button.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		button.setUI(defaultUI);

		if (Defaults.programLoaded.get()) {
			button.setForeground(Defaults.FOREGROUND);
			button.setBackground(Defaults.BUTTON);

		} else {
			button.setForeground(Color.WHITE);
			button.setBackground(new Color(50, 50, 50));
		}

		button.setBorder(BorderFactory.createEmptyBorder());

		return button;
	}

	public static void closeDialogBox() {

		frame.setVisible(false);
		frame.removeAll();
		frame.dispose();
		active = false;
		setFocus = true;

	}
}
