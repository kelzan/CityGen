/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import citygen.BuildingTile.BuildingDirection;

/**
 *
 * @author Daddy
 */
public class BuildingPack {

    LinkedList<Building> buildingList = new LinkedList<>();
    static Random rnd = new Random();
    String name;
    int gridScale;

    public enum RotationType {

        ANY_ROTATION, MUST_ROTATE, CANT_ROTATE
    }

    public void addBuilding(Building newBuilding) {
        buildingList.add(newBuilding);
        Collections.sort(buildingList, Building.AreaComparator);
//        System.out.println(buildingList);
    }

    public BuildingTile findMatchingBuilding(Tile mapTile) {
        int x1, y1, x2, y2;
        boolean found = false;
        boolean willRotate = false;
        Building building;
        int maxGridArea = 0;
        int choice;
        int gridWidth, gridLength;
        ArrayList<Building> suitableBuildings = new ArrayList<>();

        // buildingList is sorted in descending area order
        for (int i = 0; i < buildingList.size(); i++) {
            building = buildingList.get(i);
            // Find a building that fits, and still has some uses left
            if (building.fitsTile(mapTile) && building.underUseLimit()) {
                if (found != true) {
                    maxGridArea = building.getGridArea();
                    found = true;
                    suitableBuildings.add(building);
                } else {
                    int dummy = building.getGridArea();
                    if (building.getGridArea() >= maxGridArea) {
                        suitableBuildings.add(building);
                    }
                }
            }
        }
        if (found == false) {
            // Don't want to handle this right now.
            throw new RuntimeException("Building Pack can't find match");
        }

        // Pick randomly from the list of suitable buildings. TODO: You could add weights here.
        choice = rnd.nextInt(suitableBuildings.size());
        building = suitableBuildings.get(choice);
//        System.out.println("Picked Building: " + building);
        building.flagUsed();

        // OK, we know the building will fit, but we need to know if it will fit at any rotation, or
        // if it's rectangular it may only fit one way.
        RotationType rotType = getRotationType(mapTile, building);

        // Now decide if we're going to rotate it (90/270) or not (0/180).
        switch (rotType) {
            case ANY_ROTATION:
                willRotate = (rnd.nextInt(2) == 0);
                break;
            case CANT_ROTATE:
                willRotate = false;
                break;
            case MUST_ROTATE:
                willRotate = true;
                break;
        }

        // Now based on whether we'll rotate or not, we know how much space we'll take up in the
        // x and y directions.
        gridWidth = (willRotate == false) ? building.gridWidth : building.gridLength;
        gridLength = (willRotate == false) ? building.gridLength : building.gridWidth;

        // Randomly pick a corner to put the building in, and rotate accordingly. Note that rotating
        // NORTH <-> SOUTH is considered not rotating. Buildings are assumed to all be naturally
        // facing south (after preRotation is applied). Rotations are done so that the building is
        // facing towards the outside of the tile it is being placed in.
        int corner = rnd.nextInt(4);
        BuildingDirection bDir = BuildingDirection.SOUTH;
        switch (corner) {
            case 0: // LL Corner
                x1 = mapTile.x1;
                y1 = mapTile.y1;
                x2 = mapTile.x1 + gridWidth - 1;
                y2 = mapTile.y1 + gridLength - 1;
                bDir = (willRotate == false) ? BuildingDirection.SOUTH : BuildingDirection.WEST;
                break;
            case 1: // LR Corner
                x1 = mapTile.x2 - gridWidth + 1;
                y1 = mapTile.y1;
                x2 = mapTile.x2;
                y2 = mapTile.y1 + gridLength - 1;
                bDir = (willRotate == false) ? BuildingDirection.SOUTH : BuildingDirection.EAST;
                break;
            case 2: // UL Corner
                x1 = mapTile.x1;
                y1 = mapTile.y2 - gridLength + 1;
                x2 = mapTile.x1 + gridWidth - 1;
                y2 = mapTile.y2;
                bDir = (willRotate == false) ? BuildingDirection.NORTH : BuildingDirection.WEST;
                break;
            case 3: // UR Corner
                x1 = mapTile.x2 - gridWidth + 1;
                y1 = mapTile.y2 - gridLength + 1;
                x2 = mapTile.x2;
                y2 = mapTile.y2;
                bDir = (willRotate == false) ? BuildingDirection.NORTH : BuildingDirection.EAST;
                break;
            default:
                x1 = y1 = x2 = y2 = 0;
                break;
        }
        return (new BuildingTile(x1, y1, x2, y2, bDir, building));

    }

    RotationType getRotationType(Tile tile, Building building) {
        RotationType rotType;
        if ((building.gridWidth <= tile.getWidth()) && (building.gridLength <= tile.getLength())
                && (building.gridWidth <= tile.getLength()) && (building.gridLength <= tile.getWidth())) {
            rotType = RotationType.ANY_ROTATION;
        } else if ((building.gridWidth <= tile.getWidth()) && (building.gridLength <= tile.getLength())) {
            rotType = RotationType.CANT_ROTATE;
        } else {
            rotType = RotationType.MUST_ROTATE;
        }
        return rotType;
    }

    public void readFile(String filename) {
        BufferedReader br;
        String s;
        String patternStr = "^([^#][^\\s]*)\\s+(.*)";
        Pattern p = Pattern.compile(patternStr);
        boolean parseBuildings = false;
        Building building = null;

        // Try to open up the file as a text file, then parse it one line at a time.
        try {
            File f = new File(filename);
            FileReader fr = new FileReader(f);
            br = new BufferedReader(fr);
            String path = f.getParentFile().getPath();
            while ((s = br.readLine()) != null) {
                Matcher m = p.matcher(s);
                if (m.find()) {
//                    System.out.println("Variable: " + m.group(1));
//                    System.out.println("Value: " + m.group(2));
                    switch (m.group(1)) {
                        case "schematic":
                            parseBuildings = true;
                            if (building != null) {
                                addBuilding(building);
                            }
                            building = new Building(gridScale, path + "\\" + m.group(2));
                            break;
                        case "name":
                            if (parseBuildings == false) {
                                name = m.group(2);
                            } else {
                                building.buildingName = m.group(2);
                            }
                            break;
                        case "credit":
                            building.credits = m.group(2);
                            break;
                        case "scale":
                            gridScale = Integer.parseInt(m.group(2));
                            break;
                        case "initialrot":
                            building.preRotation = Integer.parseInt(m.group(2));
                            break;
                        case "uselimit":
                            building.setUseLimit(Integer.parseInt(m.group(2)));
                            break;
                        case "repeat":
                            String args[] = m.group(2).split(" ");
                            if (args.length != 4) {
                                throw new RuntimeException("Bad number of parameters for 'repeat'");
                            }
                            building.addRepeatedFloor(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                                    Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                            break;
                        default:
                            throw new RuntimeException("Unknown file argument: " + m.group(2));
                    }
                }
            }
            if (building != null) {
                addBuilding(building);
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(BuildingPack.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < buildingList.size(); i++) {
            building = buildingList.get(i);
            System.out.printf("%d: %s (%dx%d) Limit: %d\n", i, 
                    building.buildingName, building.gridWidth, building.gridLength, building.useLimit);
        }

//        // Now calculate the size of the map, and allocate it.
//        newydim = list.size();
//        if (newydim == 0) {
//            throw new RuntimeException("Zero length y dimension for " + filename);
//        }
//
//        lines = list.get(0).split(" ");
//        newxdim = lines.length;
//        if (newxdim == 0) {
//            return;
//        }
//        Allocate(newxdim, newydim);
//
//        // Now mark any location with an 'X' as occupied.
//        for (int y = 0; y < newydim; y++) {
//            s = list.get(y);
////            System.out.println(s);
//            lines = s.split(" ");
//            for (int x = 0; x < newxdim; x++) {
//                map[x][y].contents = ("X".equals(lines[x])) ? 1 : 0;
//            }
//        }

    }
}
