/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author klarson
 */
public class MapGrid {

    public int xsize;
    public int ysize;
    public MapLoc[][] map;
    public LinkedList<BuildingTile> usedTileList;
//    static int count = 0;

    /**
     *
     * @param xsize
     * @param ysize
     */
    public MapGrid(int xsize, int ysize) {
        Allocate(xsize, ysize);
    }

    public MapGrid() {
        xsize = 0;
        ysize = 0;
    }

    public final void Allocate(int newx, int newy) {
        // Initialize the map structure with MapLoc classes
        map = new MapLoc[newx][newy];
        for (int x = 0; x < newx; x++) {
            for (int y = 0; y < newy; y++) {
                map[x][y] = new MapLoc();
            }
        }
        // Specify the overall map size
        xsize = newx;
        ysize = newy;
        // Create Tile list objects
        usedTileList = new LinkedList<>();
    }

    public int findYExtent(int startX, int startY, int finalY) {
        int extent = startY;
        int stepSize = (finalY < startY) ? -1 : 1;
        int curY = startY;
        finalY += stepSize;

        do {
            if (map[startX][curY].contents != 0) {
                break;
            } else {
                extent = curY;
            }
            curY += stepSize;
        } while (curY != finalY);
        return extent;
    }

    public int findXExtent(int startX, int startY, int finalX) {
        int extent = startX;
        int stepSize = (finalX < startX) ? -1 : 1;
        int curX = startX;
        finalX += stepSize;

        do {
            if (map[curX][startY].contents != 0) {
                break;
            } else {
                extent = curX;
            }
            curX += stepSize;
        } while (curX != finalX);
        return extent;
    }

    public int findMinYExtent(int startX, int startY, int endX, int maxY) {
        int minExtent = maxY;
        int stepsize = (endX >= startX) ? 1 : -1;
        boolean isDown = (maxY < startY) ? true : false;
        int curX = startX;
        endX += stepsize;

        do {
            int curExtent = findYExtent(curX, startY, minExtent);
            if (((isDown == true) && (curExtent > minExtent))
                    || ((isDown == false) && (curExtent < minExtent))) {
                minExtent = curExtent;
            }
            curX += stepsize;
        } while (curX != endX);
        return minExtent;
    }

    public Tile getLargestEmptyTile() {
        TreeSet<Tile> myTileList = new TreeSet<>();

        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                // Only check if the contents are empty
                if (map[x][y].contents == 0) {
                    int neighbors = GetNeighborIndex(x, y);
//                    System.out.printf("Neighbors: %d\n", neighbors);
                    switch (neighbors) {
                        case 0: // north: 0, east: 0, south: 0, west: 0
                        case 1: // north: 0, east: 0, south: 0, west: 1
                        case 2: // north: 0, east: 0, south: 1, west: 0
                        case 4: // north: 0, east: 1, south: 0, west: 0
                        case 5: // north: 0, east: 1, south: 0, west: 1
                        case 8: // north: 1, east: 0, south: 0, west: 0
                        case 10: // north: 1, east: 0, south: 1, west: 0
                            // Not a vertex
                            break;
                        case 7: // north: 0, east: 1, south: 1, west: 1
                        // Open up
                        case 11: // north: 1, east: 0, south: 1, west: 1
                        // Open right
                        case 3: // north: 0, east: 0, south: 1, west: 1
                            // Lower left corner vertex
//                                System.out.printf("LL: (%d,%d)\n", x, y);
                            myTileList.addAll(findTilesFromVertex(x, y, true, false));
                            break;
                        case 6: // north: 0, east: 1, south: 1, west: 0
                            // Lower right corner vertex
//                                System.out.printf("LR: (%d,%d)\n", x, y);
                            myTileList.addAll(findTilesFromVertex(x, y, false, false));
                            break;
                        case 9: // north: 1, east: 0, south: 0, west: 1
                            // Upper left corner vertex
//                                System.out.printf("UL: (%d,%d)\n", x, y);
                            myTileList.addAll(findTilesFromVertex(x, y, true, true));
                            break;
                        case 13: // north: 1, east: 1, south: 0, west: 1
                        // Open down
                        case 14: // north: 1, east: 1, south: 1, west: 0
                        // Open left
                        case 12: // north: 1, east: 1, south: 0, west: 0
                            // Upper right corner vertex
//                                System.out.printf("UR: (%d,%d)\n", x, y);
                            myTileList.addAll(findTilesFromVertex(x, y, false, true));
                            break;
                        case 15: // north: 1, east: 1, south: 1, west: 1
                            // Enclosed
                            myTileList.add(new Tile(x, y, x, y));
                            break;
                        default:
                            throw new IllegalArgumentException("Can't determine piece type");
                    }
                }
            }
        }
//        System.out.printf("Count: %d\n", count++);
//        System.out.println("New Tile List:");
//        System.out.println(myTileList);

        // Now return the last element of the tree, which will have the largest area
        return myTileList.last();
    }

    public void allocateTile(BuildingTile newTile) {
        usedTileList.add(newTile);
        for (int y = newTile.y1; y <= newTile.y2; y++) {
            for (int x = newTile.x1; x <= newTile.x2; x++) {
                if (map[x][y].contents != 0) {
                    MapLoc debug = map[x][y];
                    throw new RuntimeException("Placing tiles on top of occupied space");
                }
                map[x][y].contents = 2;
                map[x][y].tile = newTile;
            }
        }
    }

//    public void findTilesFromVertex(int x, int y, boolean isRight, boolean isDown) {
    public LinkedList<Tile> findTilesFromVertex(int x, int y, boolean isRight, boolean isDown) {
        LinkedList<Tile> vTile = new LinkedList<>();
        int horizontalExt;
        int curVerticalExt;
        int newVerticalExt;
        int stepX;
        int curX;

        horizontalExt = findXExtent(x, y, (isRight == true) ? xsize - 1 : 0);
        curVerticalExt = findYExtent(x, y, (isDown == true) ? 0 : ysize - 1);
        stepX = (isRight == true) ? 1 : -1;
        curX = x;

        while (curX != horizontalExt) {
            curX += stepX;
            newVerticalExt = findYExtent(curX, y, curVerticalExt);
            if (((isDown == true) && (newVerticalExt > curVerticalExt))
                    || ((isDown == false) && (newVerticalExt < curVerticalExt))) {
                vTile.add(new Tile(x, y, curX - stepX, curVerticalExt));
                curVerticalExt = newVerticalExt;
            }
        }
        vTile.add(new Tile(x, y, horizontalExt, curVerticalExt));

        return vTile;
    }

    public void addVertice(int x, int y) {
        System.out.printf("Vertice: (%d,%d)\n", x, y);
    }

    public void readFile(String filename) {
        BufferedReader br;
        String s;
        String[] lines;
        ArrayList<String> list = new ArrayList<>();
        int newxdim, newydim;

        // Try to open up the file as a text file, and suck it into a list.
        try {
            File f = new File(filename);
            FileReader fr = new FileReader(f);
            br = new BufferedReader(fr);
            while ((s = br.readLine()) != null) {
                list.add(0, s);
            }
            br.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MapGrid.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapGrid.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Now calculate the size of the map, and allocate it.
        newydim = list.size();
        if (newydim == 0) {
            throw new RuntimeException("Zero length y dimension for " + filename);
        }

        lines = list.get(0).split(" ");
        newxdim = lines.length;
        if (newxdim == 0) {
            return;
        }
        Allocate(newxdim, newydim);

        // Now mark any location with an 'X' as occupied.
        for (int y = 0; y < newydim; y++) {
            s = list.get(y);
//            System.out.println(s);
            lines = s.split(" ");
            for (int x = 0; x < newxdim; x++) {
                map[x][y].contents = ("X".equals(lines[x])) ? 1 : 0;
            }
        }
    }

    public void writeFile(String filename) {
        try {
            File f = new File(filename);
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            for (int y = ysize - 1; y >= 0; y--) {
                for (int x = 0; x < xsize; x++) {
                    br.write((map[x][y].contents == 0) ? "." : "X");
                    if (x < xsize - 1) {
                        br.write(" ");
                    }
                }
                br.newLine();
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(MapGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printMap() {
        String mapChar;
        for (int y = ysize - 1; y >= 0; y--) {
            for (int x = 0; x < xsize; x++) {
                switch (map[x][y].contents) {
                    case 0:
                        mapChar = ".";
                        break;
                    case 1:
                        mapChar = "X";
                        break;
                    case 2:
                        mapChar = "#";
                        break;
                    default:
                        mapChar = "?";
                        break;
                }
                System.out.print(mapChar);
//                System.out.print((map[x][y].contents == 0) ? "." : "X");
                if (x < xsize - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    public void printPieces() {
        String s;
        for (int y = ysize - 1; y >= 0; y--) {
            for (int x = 0; x < xsize; x++) {
                switch (map[x][y].ptype) {
                    case CROSS:
                        s = "+";
                        break;
                    case STRAIGHT:
                        s = "S";
                        break;
                    case CORNER:
                        s = "C";
                        break;
                    case TEE:
                        s = "T";
                        break;
                    case END:
                        s = "E";
                        break;
                    case NONE:
                        s = ".";
                        break;
                    default:
                        s = "~";
                        break;
                }
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    public void printTiles() {
        String s;
        int c = 48; // ASCII '0'

        for (int y = ysize - 1; y >= 0; y--) {
            for (int x = 0; x < xsize; x++) {
                switch (map[x][y].contents) {
                    case 0:
                        s = ".";
                        break;
                    case 1:
                        s = "#";
                        break;
                    case 2:
                        int i = usedTileList.indexOf(map[x][y].tile);
                        i = i % 74;
                        s = Character.toString((char) (c + i));
                        break;
                    default:
                        s = " ";
                        break;
                }
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    public void PrintForks() {
        String s;
        System.out.printf("Map (%dx%d)\n", xsize, ysize);
        for (int y = ysize - 1; y >= 0; y--) {
            for (int x = 0; x < xsize; x++) {
                if (map[x][y].contents > 1) {
                    s = "#";
                } else if (map[x][y].contents == 0) {
                    s = ".";
                } else {
                    int forks = map[x][y].numForks();
                    s = (forks == 0) ? "X" : Integer.toString(forks);
                }
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    boolean validX(int x) {
        return ((x >= 0) && (x < xsize));
    }

    boolean validY(int y) {
        return ((y >= 0) && (y < ysize));
    }

    int hasContents(int x, int y) {
        if ((validX(x) == false) || (validY(y) == false)) {
            // Boundary counts as something that is already occupied
            return (1);
        } else {
            return ((map[x][y].contents != 0) ? 1 : 0);
        }
    }

    int GetNeighborIndex(int x, int y) {
        return ((hasContents(x, y + 1) * 8) // North
                + (hasContents(x + 1, y) * 4) // East
                + (hasContents(x, y - 1) * 2) // South
                + hasContents(x - 1, y));     // West
    }

    int hasRoad(int x, int y) {
        if ((validX(x) == false) || (validY(y) == false)) {
            // Boundary counts as empty with no roads
            return (0);
        } else {
            return ((map[x][y].contents == 1) ? 1 : 0);
        }
    }

    int GetSurroundingRoadIndex(int x, int y) {
        return ((hasRoad(x, y + 1) * 8) // North
                + (hasRoad(x + 1, y) * 4) // East
                + (hasRoad(x, y - 1) * 2) // South
                + hasRoad(x - 1, y));     // West

    }

    public boolean isFull() {
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                if (map[x][y].contents == 0) {
//                    System.out.printf("Found empty (%d,%d)\n", x, y);
                    return false;
                }
            }
        }
        return true;
    }

    public void calculatePieces() {
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                // Only Put down a piece if we have contents, and no previous piece type
                if ((map[x][y].contents == 1) && (map[x][y].ptype == PieceType.NONE)) {
                    int neighbors = GetSurroundingRoadIndex(x, y);
//                    System.out.printf("Neighbors: %d\n", neighbors);
                    switch (neighbors) {
                        case 0: // north: 0, east: 0, south: 0, west: 0
                            map[x][y].ptype = PieceType.CROSS;
                            map[x][y].prot = 0;
                            break;
                        case 1: // north: 0, east: 0, south: 0, west: 1
                            map[x][y].ptype = PieceType.END;
                            map[x][y].prot = 2;
                            break;
                        case 2: // north: 0, east: 0, south: 1, west: 0
                            map[x][y].ptype = PieceType.END;
                            map[x][y].prot = 3;
                            break;
                        case 3: // north: 0, east: 0, south: 1, west: 1
                            map[x][y].ptype = PieceType.CORNER;
                            map[x][y].prot = 3;
                            break;
                        case 4: // north: 0, east: 1, south: 0, west: 0
                            map[x][y].ptype = PieceType.END;
                            map[x][y].prot = 0;
                            break;
                        case 5: // north: 0, east: 1, south: 0, west: 1
                            map[x][y].ptype = PieceType.STRAIGHT;
                            map[x][y].prot = 0;
                            break;
                        case 6: // north: 0, east: 1, south: 1, west: 0
                            map[x][y].ptype = PieceType.CORNER;
                            map[x][y].prot = 0;
                            break;
                        case 7: // north: 0, east: 1, south: 1, west: 1
                            map[x][y].ptype = PieceType.TEE;
                            map[x][y].prot = 0;
                            break;
                        case 8: // north: 1, east: 0, south: 0, west: 0
                            map[x][y].ptype = PieceType.END;
                            map[x][y].prot = 1;
                            break;
                        case 9: // north: 1, east: 0, south: 0, west: 1
                            map[x][y].ptype = PieceType.CORNER;
                            map[x][y].prot = 2;
                            break;
                        case 10: // north: 1, east: 0, south: 1, west: 0
                            map[x][y].ptype = PieceType.STRAIGHT;
                            map[x][y].prot = 1;
                            break;
                        case 11: // north: 1, east: 0, south: 1, west: 1
                            map[x][y].ptype = PieceType.TEE;
                            map[x][y].prot = 3;
                            break;
                        case 12: // north: 1, east: 1, south: 0, west: 0
                            map[x][y].ptype = PieceType.CORNER;
                            map[x][y].prot = 1;
                            break;
                        case 13: // north: 1, east: 1, south: 0, west: 1
                            map[x][y].ptype = PieceType.TEE;
                            map[x][y].prot = 2;
                            break;
                        case 14: // north: 1, east: 1, south: 1, west: 0
                            map[x][y].ptype = PieceType.TEE;
                            map[x][y].prot = 1;
                            break;
                        case 15: // north: 1, east: 1, south: 1, west: 1
                            map[x][y].ptype = PieceType.CROSS;
                            map[x][y].prot = 0;
                            break;
                        default:
                            throw new IllegalArgumentException("Can't determine piece type");
                    }
                }
            }
        }
    }
}
