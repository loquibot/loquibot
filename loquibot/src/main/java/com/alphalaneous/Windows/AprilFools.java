package com.alphalaneous.Windows;

import com.alphalaneous.Main;
import com.alphalaneous.Services.GeometryDash.LevelData;
import com.alphalaneous.Services.GeometryDash.RequestsUtils;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Swing.Components.LevelContextMenu;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class AprilFools{
    private static final JPanel panel = new JPanel();

    private static final String[] columnNames = {
            " "
    };
    private static final DefaultTableModel model = new DefaultTableModel(new Object[][]{
    }, columnNames);
    private static final JTable table = new JTable(model){
        public boolean isCellEditable(int row, int column) {
            return false;
        };
    };;
    public static void create(){

        panel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(new Color(246, 247, 248, 255));
        centerRenderer.setForeground(new Color(94, 98, 103, 255));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);



        //table.setAutoCreateColumnsFromModel(false);
        table.setRowHeight(20);
        table.setGridColor(new Color(224, 224, 225, 255));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(246, 247, 248, 255));
        table.getTableHeader().setForeground(new Color(94, 98, 103, 255));
        table.setSelectionBackground(new Color(229, 237, 251, 255));
        table.setSelectionForeground(Color.BLACK);
        table.addMouseListener( new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint( e.getPoint() );
                int col = source.columnAtPoint( e.getPoint() );

                if(row != 0 && row-1 < RequestsTab.getQueueSize()) {
                    if (e.isPopupTrigger()) {
                        LevelContextMenu menu = new LevelContextMenu(row - 1);
                        source.changeSelection(row + 1, col, false, false);
                        Window.addContextMenu(menu);
                    }
                    RequestsTab.setRequestSelect(row - 1);
                    LevelButton button = RequestsTab.getRequest(row - 1);
                    LevelDetailsPanel.setPanel(button.getLevelData());
                }
                if(col == 0){
                    table.setColumnSelectionInterval(0, 26);
                }
            }

        });

        for(int i = 1; i <= 1000; i++){
            model.addRow(new Object[]{i});
        }

        for(int i = 0; i < 26; i++){
            model.addColumn((char) (i + 65) + "");
        }
        for(int i = 0; i < 26; i++){
            table.getColumnModel().getColumn(i+1).setPreferredWidth(100);
        }

        //table.setRowSelectionAllowed(false);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(46);

        table.getColumnModel().setColumnSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        model.setValueAt("ID", 0, 1);
        model.setValueAt("Name", 0, 2);
        model.setValueAt("Creator", 0, 3);
        model.setValueAt("Requester", 0, 4);
        model.setValueAt("Difficulty", 0, 5);
        model.setValueAt("Stars", 0, 6);
        model.setValueAt("Length", 0, 7);
        model.setValueAt("Likes", 0, 8);
        model.setValueAt("Downloads", 0, 9);

        for(int i = 1; i < table.getColumnCount(); i++) {
            if(table.getValueAt(0,i) != null) {
                table.setValueAt("<html><b>" + table.getValueAt(0, i) + "</b></html>", 0, i);
            }
        }

        panel.add(scrollPane);

    }

    public static void setSelect(int pos){
        if(Defaults.isAprilFools) {
            try {
                table.changeSelection(pos + 1, 1, false, false);
                table.setColumnSelectionInterval(0, 26);
            }
            catch (Exception e){
                Main.logger.error(e.getLocalizedMessage(), e);

            }
        }
    }

    public static JPanel getPanel(){
        return panel;
    }

    public static void setSize(int width, int height){
        if(Defaults.isAprilFools) {
            panel.setBounds(0, 0, width, height);
        }
    }

    public static void loadLevels(){

        if(Defaults.isAprilFools) {

            if (table.getRowCount() == 1000 && table.getColumnCount() == 27) {
                reset();

                for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
                    LevelButton button = RequestsTab.getRequest(i);
                    String creatorName = "";
                    Optional<String> creatorNameOpt = button.getLevelData().getGDLevel().getLevel().creatorName();
                    if (creatorNameOpt.isPresent()) {
                        creatorName = creatorNameOpt.get();
                    }
                    model.setValueAt(button.getID(), i+1, 1);
                    model.setValueAt(button.getLevelData().getGDLevel().getLevel().name(), i+1, 2);
                    model.setValueAt(creatorName, i+1, 3);
                    model.setValueAt(button.getRequester(), i+1, 4);
                    model.setValueAt(button.getLevelData().getSimpleDifficulty(), i+1, 5);
                    model.setValueAt(button.getLevelData().getGDLevel().getLevel().stars(), i+1, 6);
                    model.setValueAt(button.getLevelData().getGDLevel().getLevel().length().name(), i+1, 7);
                    model.setValueAt(button.getLevelData().getGDLevel().getLevel().likes(), i+1, 8);
                    model.setValueAt(button.getLevelData().getGDLevel().getLevel().downloads(), i+1, 9);

                }
            }
        }
    }

    public static void reset(){
        if(Defaults.isAprilFools) {
            for (int i = 1; i < table.getRowCount(); i++) {
                model.setValueAt(null, i, 1);
                model.setValueAt(null, i, 2);
                model.setValueAt(null, i, 3);
                model.setValueAt(null, i, 4);
                model.setValueAt(null, i, 5);
                model.setValueAt(null, i, 6);
                model.setValueAt(null, i, 7);
                model.setValueAt(null, i, 8);
                model.setValueAt(null, i, 9);

            }
        }
    }
}
