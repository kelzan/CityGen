/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gridgui;

import citygen.BuildingPack;
import citygen.MapGrid;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Daddy
 */
public class GridMapPane extends JPanel {

    private int squareW = 20; // Width of each square in pixels
    private int squareH = 20; // Height of each square in pixels
    private int borderW = 2;  // Spacing horizontal
    private int borderH = 2;  // Spacing Vertical

    MapGrid map;
    int xActive = -1;
    int yActive = -1;
    
    BuildingPack buildings;
        
    public GridMapPane() {

        map = new MapGrid(20, 20);
        buildings = new BuildingPack();
        buildings.readFile("buildings/buildings.txt");

        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    setGrid(e.getX(), e.getY(), 1);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    setGrid(e.getX(), e.getY(), 0);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                int xOld = xActive;
                int yOld = yActive;
                xActive = yActive = -1;
                repaintGrid(xOld, yOld);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setActive(e.getX(), e.getY());
            }
        });

    }

    private void setActive(int x, int y) {
        int xNew = x / (borderW + squareW);
        int yNew = y / (borderH + squareH);
        int xOld = xActive;
        int yOld = yActive;
        if ((xNew != xOld) || (yNew != yOld)) {
            xActive = xNew;
            yActive = yNew;
            repaintGrid(xNew, yNew);
            repaintGrid(xOld, yOld);
        }
    }

    private void repaintGrid(int xGrid, int yGrid) {
        repaint(borderW + (xGrid * (borderW + squareW)), borderH + (yGrid * (borderH + squareH)),
                squareW + borderW, squareH + borderH);
    }

    private void setGrid(int x, int y, int value) {
        int xGrid = x / (borderW + squareW);
        int yGrid = y / (borderH + squareH);
        if ((xGrid < map.xsize) && (yGrid < map.ysize) && (value != map.map[xGrid][yGrid].contents)) {
            map.map[xGrid][yGrid].contents = value;
            repaint(borderW + (xGrid * (borderW + squareW)), borderH + (yGrid * (borderH + squareH)), squareW, squareH);
        }
    }

    private void resetGrid(int x, int y) {
        int xGrid = x / (borderW + squareW);
        int yGrid = y / (borderH + squareH);
        if ((x < map.xsize) && (y < map.ysize)) {
            map.map[x][y].contents = 0;
            repaint(borderW + (x * (borderW + squareW)), borderH + (y * (borderH + squareH)), squareW, squareH);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(map.xsize * (borderW + squareW) + borderW,
                map.ysize * (borderH + squareH) + borderH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int rectX, rectY;
        for (int y = 0; y < map.ysize; y++) {
            for (int x = 0; x < map.xsize; x++) {
                rectX = borderW + (x * (borderW + squareW));
                rectY = borderH + (y * (borderH + squareH));
                switch (map.map[x][y].contents) {
                    case 1:
                        g.setColor(Color.RED);
                        break;
                    case 2:
                        int i = map.usedTileList.indexOf(map.map[x][y].tile);
                        g.setColor(map.usedTileList.get(i).building.renderColor);
                       // System.out.printf("(%02d,%02d):%d %s\n",x,y,i,map.usedTileList.get(i).building.buildingName);
                        break;
                    default:
                        g.setColor(Color.LIGHT_GRAY);
                        break;                     
                }

                g.fillRect(rectX, rectY, squareW, squareH);
                if ((x == xActive) && (y == yActive)) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawRect(rectX, rectY, squareW, squareH);
            }

        }
//        g.drawString("This is my custom Panel!", 10, 20);
//        g.setColor(Color.RED);
//        g.fillRect(squareX, squareY, squareW, squareH);
//        g.setColor(Color.BLACK);
//        g.drawRect(squareX, squareY, squareW, squareH);

    }

    void resizeMap(int newX, int newY) {
        map = new MapGrid(newX, newY);
        setScrollArea();
    }

    void rescaleMap(int newScale) {
        squareW = newScale;
        squareH = newScale;
        setScrollArea();
    }
    
    void setScrollArea() {
        setPreferredSize(new Dimension((map.xsize * (borderW + squareW)) + borderW,
                (map.ysize * (borderH + squareH)) + borderH));
        revalidate();
        repaint();    
    }
}
