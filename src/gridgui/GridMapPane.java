/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gridgui;

import citygen.BuildingPack;
import citygen.ForkType;
import citygen.MapGrid;
import citygen.MapLoc;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
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
    int xActive1 = -1;
    int yActive1 = -1;
    int xActive2 = -1;
    int yActive2 = -1;
    
//    int foox,fooy,foow,fooh;

    boolean showPaths = false;
    boolean autoPaths = false;

    BuildingPack buildings;

    class MapPoint {

        int gX; // Grid array X offset
        int gY; // Grid array Y offset
        int cX; // Screen X coordinate
        int cY; // Screen Y coordinate

        public void coordToGrid(int coordX, int coordY) {
            gX = coordX / (borderW + squareW);
            gY = coordY / (borderH + squareH);
            gY = map.ysize - gY - 1;
        }

        public void gridToCoord() {
            cX = borderW + (gX * (borderW + squareW));
            cY = borderH + ((map.ysize - gY - 1) * (borderH + squareH));
        }

        public void gridToCoord(int gridX, int gridY) {
            gX = gridX;
            gY = gridY;
            gridToCoord();
        }

        public boolean inGrid() {
            return ((gX < map.xsize) && (gY < map.ysize) && (gX > 0) && (gY > 0));
        }
    }

    MapPoint mappoint = new MapPoint();

    public GridMapPane() {

        //map = new MapGrid(20, 20);
        //buildings = new BuildingPack();
        //buildings.readFile("buildings/buildings.txt");
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                        unsetGrid(e.getX(), e.getY()); // Remove road (shift pressed)
                    } else {
                        setGrid(e.getX(), e.getY()); // Add Road
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    unsetGrid(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                int xOld = xActive1;
                int yOld = yActive1;
                int xOld2 = xActive2;
                int yOld2 = yActive2;
                xActive1 = yActive1 = xActive2 = yActive2 = -1;
                repaintGrid(xOld, yOld, xOld2, yOld2);
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
        int xOld1, yOld1, xOld2, yOld2, locContents;
        mappoint.coordToGrid(x, y); // screen coords to map array translate
        // If we're in the same 'box', no need to update anything
        if ((mappoint.gX != xActive1) || (mappoint.gY != yActive1)) {
            xOld1 = xActive1;
            yOld1 = yActive1;
            xOld2 = xActive2;
            yOld2 = yActive2;
            locContents = (mappoint.inGrid()) ? map.map[mappoint.gX][mappoint.gY].contents : 0;
            if (locContents == 2) {
//                System.out.printf("gX: %d, gY: %d\n",mappoint.gX,mappoint.gY);
                xActive1 = map.map[mappoint.gX][mappoint.gY].tile.x1;
                yActive1 = map.map[mappoint.gX][mappoint.gY].tile.y1;
                xActive2 = map.map[mappoint.gX][mappoint.gY].tile.x2;
                yActive2 = map.map[mappoint.gX][mappoint.gY].tile.y2;
                if ((xOld1 != xActive1) || (yOld1 != yActive1)) {
                    repaintGrid(xOld1, yOld1, xOld2, yOld2);
                    repaintGrid(xActive1, yActive1, xActive2, yActive2);
                }
            } else {
                xActive1 = mappoint.gX;
                yActive1 = mappoint.gY;
                xActive2 = mappoint.gX;
                yActive2 = mappoint.gY;
                repaintGrid(xOld1, yOld1, xOld2, yOld2);
                repaintGrid(xActive1, yActive1, xActive2, yActive2);
            }
//            System.out.printf("x1:%d, y1: %d, x2: %d, y2: %d\n", xActive1, yActive1, xActive2, yActive2);
//            System.out.printf("gX:%d, gY:%d\n", mappoint.gX, mappoint.gY);
        }
    }

    private void repaintGrid(int xGrid, int yGrid, int xGrid2, int yGrid2) {
        int xOffset = xGrid2 - xGrid + 1;
        int yOffset = yGrid2 - yGrid + 1;
        yGrid2 = map.ysize - yGrid2 - 1; // Using yGrid2 because we need to start at UR corner
        if (!showPaths) {
 //           foox = borderW + (xGrid * (borderW + squareW));
 //           fooy = borderH + (yGrid2 * (borderH + squareH));
 //           foow = xOffset * (squareW + borderW);
 //           fooh = yOffset * (squareH + borderH);
 //           repaint();
            repaint(borderW + (xGrid * (borderW + squareW)), borderH + (yGrid2 * (borderH + squareH)),
                    xOffset * (squareW + borderW), yOffset * (squareH + borderH));
//            System.out.printf("Repaint: (%d,%d) %d x %d\n",
//                    borderW + (xGrid * (borderW + squareW)), borderH + (yGrid2 * (borderH + squareH)),
//                    xOffset * (squareW + borderW), yOffset * (squareH + borderH));
        } else {
            repaint((xGrid * (borderW + squareW)) - 1, (yGrid2 * (borderH + squareH) - 1),
                    xOffset * (squareW + borderW) + 4, yOffset * (squareH + borderH) + 4);
        }
    }

    private void unsetGrid(int x, int y) {
        mappoint.coordToGrid(x, y);
        if ((mappoint.gX >= map.xsize) || (mappoint.gY >= map.ysize)) {
            return; // Off the grid, don't do anything
        }
        if (map.map[mappoint.gX][mappoint.gY].contents == 1) {
            map.map[mappoint.gX][mappoint.gY].contents = 0;
            map.map[mappoint.gX][mappoint.gY].setForks(ForkType.NO_FORK);
            if (autoPaths) {
                map.unlinkSurroundingPaths(mappoint.gX, mappoint.gY);
            }
            mappoint.gridToCoord();
            repaint(mappoint.cX, mappoint.cY, squareW, squareH);
        }
    }

    private void setGrid(int x, int y) {
        mappoint.coordToGrid(x, y);
        if ((mappoint.gX >= map.xsize) || (mappoint.gY >= map.ysize)) {
            return; // Off the grid, don't do anything
        }
        if (map.map[mappoint.gX][mappoint.gY].contents == 0) {
            map.map[mappoint.gX][mappoint.gY].contents = 1;
            if (autoPaths) {
                map.linkSurroundingPaths(mappoint.gX, mappoint.gY);
            } else {
                // Mark forks for later generation
                map.map[mappoint.gX][mappoint.gY].setForks(ForkType.PROPOSED);
            }
            mappoint.gridToCoord();
            repaint(mappoint.cX, mappoint.cY, squareW, squareH);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (map == null) {
            return new Dimension(500, 500);
        } else {
            return new Dimension(map.xsize * (borderW + squareW) + borderW,
                    map.ysize * (borderH + squareH) + borderH);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
//        g.setColor(Color.WHITE);
//        g.fillRect(foox,fooy,foow,fooh);

        int rectX, rectY;
        for (int y = 0; y < map.ysize; y++) {
            for (int x = 0; x < map.xsize; x++) {
                rectX = borderW + (x * (borderW + squareW));
                rectY = borderH + ((map.ysize - y - 1) * (borderH + squareH));
                switch (map.map[x][y].contents) {
                    case 1:
                        g.setColor(Color.RED);
                        g.fillRect(rectX, rectY, squareW, squareH);
//                        g2d.setColor(Color.YELLOW);
//                        drawForks(g2d, rectX, rectY, map.map[x][y]);
                        break;
                    case 2:
                        int i = map.usedTileList.indexOf(map.map[x][y].tile);
                        if (i == -1) { // Unknown building/Reserved Space
                            g.setColor(Color.BLACK);
                        } else {
                            g.setColor(map.usedTileList.get(i).building.renderColor);
                        }
                        // System.out.printf("(%02d,%02d):%d %s\n",x,y,i,map.usedTileList.get(i).building.buildingName);
                        g.fillRect(rectX, rectY, squareW, squareH);
                        break;
                    default:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(rectX, rectY, squareW, squareH);
                        break;
                }

                if ((x >= xActive1) && (x <= xActive2) && (y >= yActive1) && (y <= yActive2)) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawRect(rectX, rectY, squareW, squareH);

            }
        }

        if (showPaths) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(Color.YELLOW);
            for (int y = 0; y < map.ysize; y++) {
                for (int x = 0; x < map.xsize; x++) {
                    switch (map.map[x][y].contents) {
                        case 1:
                            rectX = borderW + (x * (borderW + squareW));
                            rectY = borderH + ((map.ysize - y - 1) * (borderH + squareH));
                            drawForks(g2d, rectX, rectY, map.map[x][y]);
                            break;
                    }
                }
            }
        }
//        g.drawString("This is my custom Panel!", 10, 20);
//        g.setColor(Color.RED);
//        g.fillRect(squareX, squareY, squareW, squareH);
//        g.setColor(Color.BLACK);
//        g.drawRect(squareX, squareY, squareW, squareH);
    }

    void drawForks(Graphics2D g, int x, int y, MapLoc loc) {
        if (loc.forks[MapLoc.NORTH] == ForkType.NO_FORK) {
            g.drawLine(x - 1, y - 1, x + squareW + 1, y - 1);
        }
        if (loc.forks[MapLoc.SOUTH] == ForkType.NO_FORK) {
            g.drawLine(x - 1, y + squareH + 1, x + squareW + 1, y + squareH + 1);
        }
        if (loc.forks[MapLoc.EAST] == ForkType.NO_FORK) {
            g.drawLine(x + squareW + 1, y - 1, x + squareW + 1, y + squareH + 1);
        }
        if (loc.forks[MapLoc.WEST] == ForkType.NO_FORK) {
            g.drawLine(x - 1, y - 1, x - 1, y + squareH + 1);
        }
    }

    void setMap(MapGrid newMap) {
        map = newMap;
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
//        repaint();    
    }

    public void setZoom(int newZoom) {
        squareW = newZoom;
        squareH = newZoom;
    }
}
