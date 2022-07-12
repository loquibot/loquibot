package com.alphalaneous.Swing.Components;

import com.alphalaneous.*;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import static com.alphalaneous.Defaults.defaultUI;

public class BasicLevelButton extends CurvedButtonAlt {

    private static final JButtonUI selectUI = new JButtonUI();
    private static final JButtonUI warningUI = new JButtonUI();
    private static final JButtonUI noticeUI = new JButtonUI();
    private static final JButtonUI warningSelectUI = new JButtonUI();
    private static final JButtonUI noticeSelectUI = new JButtonUI();
    public static int selectedID = 0;

    public long ID;
    private final String requester;
    public boolean selected;

    private final RoundedJButton analyzeButton = new RoundedJButton("\uE7BA", "WARNING");
    private final JButton moveUp = new JButton("\uE010");
    private final JButton moveDown = new JButton("\uE011");


    private final JLabel lRequester = new JLabel();
    private final JPanel info = new JPanel(new GridLayout(0, 2, 1, 1));
    private boolean viewership = false;
    private int gonePoints = 3;


    public BasicLevelButton(long ID, String requester) {
        super("");

        this.ID = ID;

        this.requester = requester;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    Window.destroyContextMenu();
                    Window.addContextMenu(new LevelContextMenu(Requests.getPosFromIDBasic(ID)));
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
            lName.setText(String.valueOf(ID));
            lRequester.setText(requester);

            add(lName);
            add(lRequester);
            setLayout(null);


            lName.setFont(Defaults.MAIN_FONT.deriveFont(18f));
            lName.setBounds(10, 5, (int) lName.getPreferredSize().getWidth() + 5, 30);


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
                    if (Requests.getPosFromIDBasic(ID) != 0) {
                        //Requests.levels.remove(data);
                        //Requests.levels.add(Requests.getPosFromID(ID) - 1, data);
                        //com.alphalaneous.Tabs.Window.getLevelsPanel().refreshButtons();
                        RequestsTab.movePosition(Requests.getPosFromIDBasic(ID), Requests.getPosFromIDBasic(ID) - 1);
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
                    if (Requests.getPosFromIDBasic(ID) != RequestsTab.getQueueSize() - 1) {
                        //Requests.levels.remove(data);
                        //Requests.levels.add(Requests.getPosFromID(ID) + 1, data);
                        RequestsTab.movePosition(Requests.getPosFromIDBasic(ID), Requests.getPosFromIDBasic(ID) + 1);
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
            add(moveDown);

            lRequester.setFont(Defaults.MAIN_FONT.deriveFont(12f));
            lRequester.setBounds(10, 30, (int) lRequester.getPreferredSize().getWidth() + 5, 20);


            lName.setForeground(Defaults.FOREGROUND_A);
            lRequester.setForeground(Defaults.FOREGROUND_B);

            setBackground(Defaults.COLOR);
            setUI(defaultUI);

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
                RequestsTab.getLevelsPanel().deselectAll();
                select();

            });

        } catch (Exception e) {
            e.printStackTrace();
            DialogBox.showDialogBox("Error!", e.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
        }
    }

    public long getID() {
        return ID;
    }

    public String getRequester() {
        return requester;
    }

    public void setViewership(boolean viewer) {
        if (viewer) {
            lRequester.setForeground(Defaults.FOREGROUND_B);
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
        if(!Settings.getSettings("basicMode").asBoolean()) {
            RequestsTab.getRequest(Requests.getPosFromID(ID)).getLevelData().setViewership(viewership);
        }
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

    public void select() {
        selectedID = getComponentIndex();
        this.selected = true;

        setBackground(Defaults.COLOR4);
        setUI(selectUI);
    }

    void deselect() {
        if (this.selected) {
            info.setVisible(false);
            info.removeAll();
            setPreferredSize(new Dimension(RequestsTab.getLevelsPanel().getButtonWidth()-50, 60));
        }
        this.selected = false;

            setBackground(Defaults.COLOR);
            setUI(defaultUI);


    }

    public void refresh(boolean image, boolean vulgar) {
        for (Component component : getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(Defaults.FOREGROUND_A);
            }
        }
        moveDown.setForeground(Defaults.FOREGROUND_A);
        moveUp.setForeground(Defaults.FOREGROUND_A);

        analyzeButton.setForeground(Defaults.FOREGROUND_A);
        lRequester.setForeground(Defaults.FOREGROUND_B);
        if (selected) {
            if (image) {
                setBackground(new Color(200, 0, 0));
                setUI(warningSelectUI);
            } else if (vulgar) {
                setBackground(new Color(200, 150, 0));
                setUI(noticeSelectUI);
            } else {
                setBackground(Defaults.COLOR);
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
                setBackground(Defaults.COLOR);
                setUI(defaultUI);
            }
        }
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
        if (selected) {

                setBackground(Defaults.COLOR);
                setUI(selectUI);

            select();
        } else {

                setBackground(Defaults.COLOR);
                setUI(defaultUI);

        }
    }
}