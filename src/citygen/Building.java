
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import schematic.ClassicNotSupportedException;
import schematic.ParseException;
import schematic.Schematic;
import schematic.SchematicReader;

/**
 *
 *
 * @author Daddy
 */
public class Building {

    static Random rnd = new Random();
    int gridWidth;
    int gridLength;
    int gridScale;
    int schemWidth;
    int schemLength;
    int schemHeight; // height of Schematic, not necessarily final building height
    int maxSchemHeight = 191;
    int useLimit;
    int useCount;
    String schemFileName;
    ArrayList<RepeatedFloor> rFloors;
    public String buildingName;
    String credits;
    int preRotation;
    public Color renderColor; // Color of building tile in GUI

    class FloorStepper {

        int schemHeight;
        int maxHeight;
        int totalHeight;
//        ArrayList<Integer> zList;
        int[] zList;
        int curArrayIdx;

        FloorStepper(int schemHeight, int maxHeight) {
            this.schemHeight = schemHeight;
            this.maxHeight = maxHeight;
            Collections.sort(rFloors, LevelComparator);
            totalHeight = calculateHeight();
            zList = new int[totalHeight];
            fillArray();
            curArrayIdx = 0;
        }

        boolean hasNext() {
            return (curArrayIdx < zList.length);
        }

        int next() {
            return (zList[curArrayIdx++]);
        }

        final int calculateHeight() {
            int headroom;
            int maxAdd;
            int calcHeight = schemHeight;

            for (int i = 0; i < rFloors.size(); i++) {
                calcHeight += rFloors.get(i).floorSize() * (rFloors.get(i).minRepeat - 1);
                rFloors.get(i).curRepeat = rFloors.get(i).minRepeat;
            }
            for (int i = 0; i < rFloors.size(); i++) {
                headroom = maxHeight - calcHeight;
                maxAdd = headroom / rFloors.get(i).floorSize();
                if (maxAdd == 0) {
                    continue;
                }
                maxAdd = ((maxAdd + rFloors.get(i).minRepeat) > rFloors.get(i).maxRepeat)
                        ? rFloors.get(i).maxRepeat - rFloors.get(i).minRepeat : maxAdd;
//                int temp = maxAdd;
                maxAdd = rnd.nextInt(maxAdd + 1);
                rFloors.get(i).curRepeat = rFloors.get(i).minRepeat + maxAdd;
//                System.out.printf("Picked %d out of %d. Total is %d\n", maxAdd, temp, rFloors.get(i).curRepeat);
                calcHeight += maxAdd * rFloors.get(i).floorSize();
            }
            return calcHeight;
        }

        final void fillArray() {
            int arrayIdx = 0;
            int level = 0;

            for (int rf = 0; rf < rFloors.size(); rf++) {
                for (int i = level; i < rFloors.get(rf).startLevel; i++) {
                    zList[arrayIdx++] = level++;
                }
                for (int repeat = 0; repeat < rFloors.get(rf).curRepeat; repeat++) {
                    level = rFloors.get(rf).startLevel;
                    for (int ii = 0; ii < rFloors.get(rf).floorSize(); ii++) {
                        zList[arrayIdx++] = level++;
                    }
                }
                level = rFloors.get(rf).endLevel + 1;
            }
            for (int i = arrayIdx; i < totalHeight; i++) {
                zList[arrayIdx++] = level++;
            }
        }
    }

    class RepeatedFloor {
        // 9-12, 17-20

        int startLevel;
        int endLevel;
        int minRepeat;
        int maxRepeat;
        int curRepeat;

        RepeatedFloor(int startLevel, int endLevel, int minRepeat, int maxRepeat) {
            this.startLevel = startLevel;
            this.endLevel = endLevel;
            this.minRepeat = minRepeat;
            this.maxRepeat = maxRepeat;
        }

        int floorSize() {
            return (endLevel - startLevel + 1);
        }
    }

    public Building(int width, int length, int scale, String schemFileName) {
        this.gridWidth = width;
        this.gridLength = length;
        this.gridScale = scale;
        this.schemFileName = schemFileName;
        initVars();
    }

    public Building(int scale, String schemFileName) {
        this.gridScale = scale;
        this.schemFileName = schemFileName;
        setDimsFromSchematic();
        initVars();
    }

    private void initVars() {
        rFloors = new ArrayList<>();
        useLimit = -1;
        preRotation = 0;
        renderColor = new Color(rnd.nextInt()>>8);
    }

    public void addRepeatedFloor(int startLevel, int endLevel, int minRepeat, int maxRepeat) {
        if (minRepeat > maxRepeat) {
            throw new RuntimeException("Illegal repeat parameters");
        }
        if (startLevel > endLevel) {
            int temp = startLevel;
            startLevel = endLevel;
            endLevel = startLevel;
        }
        rFloors.add(new RepeatedFloor(startLevel, endLevel, minRepeat, maxRepeat));
    }

    public Schematic getSchematic() {
        Schematic schematic = null;

        try {
            schematic = SchematicReader.readSchematicsFile(schemFileName);
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (rFloors.size() > 0) {
            schematic = stretchBuilding(schematic);
        }
        return schematic;
    }

    public final void setDimsFromSchematic() {
        Schematic schematic = null;
        try {
            schematic = SchematicReader.readSchematicsFile(schemFileName);
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
        }
        schemLength = schematic.length;
        schemWidth = schematic.width;
        schemHeight = schematic.height;
        gridWidth = ((schematic.width - 1) / gridScale) + 1;
        gridLength = ((schematic.length - 1) / gridScale) + 1;
    }

    public Schematic stretchBuilding(Schematic original) {
        Schematic stretched;
        FloorStepper floorStepper = new FloorStepper(original.height, maxSchemHeight);
        int destZ = 0;
        int srcZ;

        stretched = new Schematic(original.width, original.length, floorStepper.totalHeight);
        stretched.Allocate();

        while (floorStepper.hasNext()) {
            srcZ = floorStepper.next();
//            System.out.println(destZ + " Floor: " + srcZ);
            stretched.insertSchematicSlice(destZ++, srcZ, original);
        }

        return stretched;
    }

    public int getGridArea() {
        return (gridWidth * gridLength);
    }

    public void setUseLimit(int useLimit) {
        this.useLimit = useLimit;
        useCount = 0;
    }

    public void resetUseLimit() {
        useCount = 0;
    }

    public boolean underUseLimit() {
        return ((useLimit == -1) || (useCount < useLimit));
    }

    public void flagUsed() {
        useCount++;
    }

    public int getUseCount() {
        return useCount;
    }

    /**
     * Returns true if the building can fit inside the given Tile. This will return true even if the
     * building must be rotated in order for it to fit.
     *
     * @param tile the tile to check against
     * @return true if the building will fit the tile at any rotation, false if it will not.
     */
    public boolean fitsTile(Tile tile) {
        return (((gridWidth <= tile.getWidth()) && (gridLength <= tile.getLength()))
                || ((gridWidth <= tile.getLength()) && (gridLength <= tile.getWidth())));
    }
    public static Comparator<Building> AreaComparator = new Comparator<Building>() {
        @Override
        public int compare(Building building1, Building building2) {
            return (building2.getGridArea() - building1.getGridArea());
        }
    };
    static Comparator<RepeatedFloor> LevelComparator = new Comparator<RepeatedFloor>() {
        @Override
        public int compare(RepeatedFloor level1, RepeatedFloor level2) {
            return (level1.startLevel - level2.startLevel);
        }
    };

    @Override
    public String toString() {
        return (String.format("(%d x %d):%d %s", gridWidth, gridLength, getGridArea(), schemFileName));
    }
}
