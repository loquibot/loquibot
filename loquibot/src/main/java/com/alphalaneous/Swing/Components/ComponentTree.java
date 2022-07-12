package com.alphalaneous.Swing.Components;

import com.alphalaneous.Windows.Window;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

public class ComponentTree {

   private static JTree tree;

   public static void updateTree(JComponent component){
      JComponent root = SwingUtilities.getRootPane(component);
      tree.setModel(new ComponentTreeModel(root));
      for (int i = 0; i < tree.getRowCount(); i++) {
         tree.expandRow(i);
      }
   }


   public static void showTree(JComponent component) {
      JComponent root = SwingUtilities.getRootPane(component);
      if (root == null) {
         JComponent parent = component;
         while (parent != null) {
            root = parent;
            parent = (JComponent) parent.getParent();
         }
      }
      tree = new JTree(new ComponentTreeModel(root));
      tree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               int row = tree.getClosestRowForLocation(e.getX(), e.getY());
               TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
               tree.setSelectionRow(row);
               System.out.println(row);
               Component component1 = ((Component)((DefaultMutableTreeNode) selPath.getPath()[selPath.getPathCount()-1]).getUserObject());
               //component1.setVisible(!component1.isVisible());
               try {
                  Class<Component> thisClass = Component.class;
                  Method[] methods = thisClass.getDeclaredMethods();

                  for (Method method : methods) {
                     if (method.getName().startsWith("set")) {
                        System.out.println(method.getName());
                     }
                  }
                  Method setVisible = JComponent.class.getMethod("setVisible", boolean.class);
                  setVisible.invoke(component1, !component1.isVisible());
               } catch (Throwable f) {
                  f.printStackTrace();
               }

               System.out.println(selPath.getPath()[selPath.getPathCount()-1]);
            }
         }
      });

      for (int i = 0; i < tree.getRowCount(); i++) {
         tree.expandRow(i);

      }
      JScrollPane scrollPane = new JScrollPane(tree);

      JPanel panel = new JPanel();

      panel.setMinimumSize(new Dimension(200,0));
      scrollPane.setMinimumSize(new Dimension(600,0));

      JFrame frame = new JFrame();

      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
              scrollPane, panel);

      splitPane.setContinuousLayout(true);


      frame.add(splitPane);

      frame.setLocationRelativeTo(root);
      frame.setTitle("Component Tree");
      frame.setIconImages(Window.getWindow().getIconImages());
      frame.setSize(new Dimension(1000,800));
      frame.setVisible(true);
   }
}
