/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gridgui;

//import grid.Grid;
import citygen.BuildingTile;
import citygen.Tile;
import java.awt.event.ItemEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

//import citygen.MapGrid;
/**
 *
 * @author Daddy
 */
public class GridGUI extends javax.swing.JFrame {

    /**
     * Creates new form NewTry
     */
    public GridGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonGenerate = new javax.swing.JButton();
        jTextXSize = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextYSize = new javax.swing.JTextField();
        jButtonResize = new javax.swing.JButton();
        jButtonReset = new javax.swing.JButton();
        jTextScale = new javax.swing.JTextField();
        jButtonZoom = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonGenBuildings = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPaneCity = new javax.swing.JScrollPane();
        jPanelCity = new gridgui.GridMapPane();
        jScrollPaneSubway = new javax.swing.JScrollPane();
        jPanelSubway = new gridgui.GridMapPane();
        jScrollPaneSewer = new javax.swing.JScrollPane();
        jScrollPaneDungeon = new javax.swing.JScrollPane();
        jCheckBoxShowPaths = new javax.swing.JCheckBox();
        jCheckBoxAutoLink = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonGenerate.setText("Gen Roads");
        jButtonGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateActionPerformed(evt);
            }
        });

        jTextXSize.setColumns(3);
        jTextXSize.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextXSize.setText("20");

        jLabel1.setText("X");

        jTextYSize.setColumns(3);
        jTextYSize.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextYSize.setText("20");

        jButtonResize.setText("Resize");
        jButtonResize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResizeActionPerformed(evt);
            }
        });

        jButtonReset.setText("Reset");
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });

        jTextScale.setColumns(3);
        jTextScale.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextScale.setText("20");

        jButtonZoom.setText("Zoom");
        jButtonZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZoomActionPerformed(evt);
            }
        });

        jButtonSave.setText("Save...");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonGenBuildings.setText("Gen Buildings");
        jButtonGenBuildings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenBuildingsActionPerformed(evt);
            }
        });

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelCityLayout = new javax.swing.GroupLayout(jPanelCity);
        jPanelCity.setLayout(jPanelCityLayout);
        jPanelCityLayout.setHorizontalGroup(
            jPanelCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 517, Short.MAX_VALUE)
        );
        jPanelCityLayout.setVerticalGroup(
            jPanelCityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 385, Short.MAX_VALUE)
        );

        jScrollPaneCity.setViewportView(jPanelCity);

        jTabbedPane1.addTab("City", jScrollPaneCity);

        javax.swing.GroupLayout jPanelSubwayLayout = new javax.swing.GroupLayout(jPanelSubway);
        jPanelSubway.setLayout(jPanelSubwayLayout);
        jPanelSubwayLayout.setHorizontalGroup(
            jPanelSubwayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 517, Short.MAX_VALUE)
        );
        jPanelSubwayLayout.setVerticalGroup(
            jPanelSubwayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 385, Short.MAX_VALUE)
        );

        jScrollPaneSubway.setViewportView(jPanelSubway);

        jTabbedPane1.addTab("Subway", jScrollPaneSubway);
        jTabbedPane1.addTab("Sewer", jScrollPaneSewer);
        jTabbedPane1.addTab("Dungeon", jScrollPaneDungeon);

        jCheckBoxShowPaths.setText("Show Paths");
        jCheckBoxShowPaths.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxShowPathsItemStateChanged(evt);
            }
        });

        jCheckBoxAutoLink.setText("Auto Link Paths");
        jCheckBoxAutoLink.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxAutoLinkItemStateChanged(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Save Schematic...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextXSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextYSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jButtonResize))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonZoom))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonReset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSave))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButtonGenerate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonGenBuildings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBoxShowPaths)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxAutoLink)))
                .addGap(87, 87, 87)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextXSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextYSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonResize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonZoom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonGenerate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonGenBuildings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxShowPaths)
                    .addComponent(jCheckBoxAutoLink))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonReset)
                    .addComponent(jButtonSave))
                .addContainerGap())
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateActionPerformed
        // TODO add your handling code here:
        config.layRoad(activeLevel);

    }//GEN-LAST:event_jButtonGenerateActionPerformed

    private void jButtonResizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResizeActionPerformed
        int newX = Integer.parseInt(jTextXSize.getText());
        int newY = Integer.parseInt(jTextYSize.getText());
        config.resizeMaps(newX, newY);
        jTabbedPane1.repaint();
    }//GEN-LAST:event_jButtonResizeActionPerformed

    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        config.resetMaps();
        jTabbedPane1.repaint();
    }//GEN-LAST:event_jButtonResetActionPerformed

    private void jButtonZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomActionPerformed
        int newScale = Integer.parseInt(jTextScale.getText());
        config.setZoom(newScale);
        jTabbedPane1.repaint();
    }//GEN-LAST:event_jButtonZoomActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed

    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonGenBuildingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenBuildingsActionPerformed
        myPane = (GridMapPane) jPanelCity;
        while (myPane.map.isFull() == false) {
            Tile curEmptyTile = myPane.map.getLargestEmptyTile();
            BuildingTile newBuildingTile = myPane.buildings.findMatchingBuilding(curEmptyTile);
            myPane.map.allocateTile(newBuildingTile);
        }
        myPane.map.printTiles();
        myPane.repaint();
    }//GEN-LAST:event_jButtonGenBuildingsActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Schematics", "schematic");

        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(GridGUI.this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fname = file.getAbsolutePath();

            if (!fname.endsWith(".schematic")) {
                // Add proper extension if necessary
                file = new File(fname + ".schematic");
            }
            config.saveSchematic(file);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        JTabbedPane tabbedPane = (JTabbedPane) evt.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                activeLevel = LevelType.CITY;
                break;
            case 1:
                activeLevel = LevelType.SUBWAY;
                break;
            case 2:
                activeLevel = LevelType.SEWER;
                break;
            case 3:
                activeLevel = LevelType.DUNGEON;
                break;
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jCheckBoxShowPathsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxShowPathsItemStateChanged
        config.setShowPaths(evt.getStateChange() == ItemEvent.SELECTED);
        jTabbedPane1.repaint();
    }//GEN-LAST:event_jCheckBoxShowPathsItemStateChanged

    private void jCheckBoxAutoLinkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxAutoLinkItemStateChanged
        config.setAutoLink(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_jCheckBoxAutoLinkItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GridGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //myGrid = new Grid(20,20);
        myPane = (GridMapPane) jPanelCity;
//        myPane.map = myGrid;

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GridGUI gui;
                gui = new GridGUI();
                config = new CityConfig(gui);
                gui.setVisible(true);
//                new GridGUI().setVisible(true);
//                System.out.print("WOO");
            }
        });
    }
    //static Grid myGrid
    static GridMapPane myPane;

    static CityConfig config;
    static LevelType activeLevel;
    //static MapGrid myMapGrid;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonGenBuildings;
    private javax.swing.JButton jButtonGenerate;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JButton jButtonResize;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonZoom;
    private javax.swing.JCheckBox jCheckBoxAutoLink;
    private javax.swing.JCheckBox jCheckBoxShowPaths;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    public static javax.swing.JPanel jPanelCity;
    public static javax.swing.JPanel jPanelSubway;
    private static javax.swing.JScrollPane jScrollPaneCity;
    private javax.swing.JScrollPane jScrollPaneDungeon;
    private javax.swing.JScrollPane jScrollPaneSewer;
    private javax.swing.JScrollPane jScrollPaneSubway;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextScale;
    public javax.swing.JTextField jTextXSize;
    public javax.swing.JTextField jTextYSize;
    // End of variables declaration//GEN-END:variables
}
