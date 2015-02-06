/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import schematic.ClassicNotSupportedException;
import schematic.ParseException;
import schematic.Schematic;
import schematic.SchematicReader;
import schematic.SchematicWriter;

/**
 *
 * @author Daddy
 */
public class BuildingShrinker {

    class StepRep {

        int startLev;
        int stride;
        int repeat;
        boolean used = false;

        public StepRep(int startLev, int stride, int repeat) {
            this.startLev = startLev;
            this.stride = stride;
            this.repeat = repeat;
        }

        public int getRepeatLength() {
            return (stride * repeat);
        }

        public int getEndLev() {
            return (startLev + (stride * repeat) - 1);
        }
    }
    static Comparator<StepRep> StepRepComparator = new Comparator<StepRep>() {
        @Override
        public int compare(StepRep rep1, StepRep rep2) {
            int diff = (rep2.stride * rep2.repeat) - (rep1.stride * rep1.repeat);
            if (diff == 0) {
                return (rep1.stride - rep2.stride);
            }
            return (diff);
        }
    };
    static Comparator<StepRep> StepRepStartLevComparator = new Comparator<StepRep>() {
        @Override
        public int compare(StepRep rep1, StepRep rep2) {
            return (rep1.startLev - rep2.startLev);
        }
    };

    class Strides {

        Map<Integer, StepRep> strideMap = new TreeMap<>();

        Strides(int startLev, int height, MatchLevels matchLevel) {
            for (Integer curLev : matchLevel.levels) {
                if (curLev > startLev) {
                    int curStride = curLev - startLev;
                    int curRepeat = 0;
                    for (int i = 1; i * curStride <= height - curStride + 1; i++) {
//                        curRepeat++;
                        if (matchLevel.levels.contains(startLev + (i * curStride))) {
                            curRepeat = i;
                        } else {
                            break;
                        }
                    }
                    if (curRepeat > 0) {
                        addStride(startLev, curStride, curRepeat + 1);
                    }
                }
            }
//            System.out.println(strideMap);
        }

        public void addStride(int startLev, int stride, int repeat) {
//            System.out.printf("Adding: start %d, stride %d, repeat %d\n", startLev, stride, repeat);
            strideMap.put(stride, new StepRep(startLev, stride, repeat));
        }
    }

    class MatchLevels {

        TreeSet<Integer> levels;

        public MatchLevels(int initLevel) {
            levels = new TreeSet<>();
            addMatchingLevel(initLevel);
        }

        public final void addMatchingLevel(int mLevel) {
            levels.add(mLevel);
        }
    }

    class RepeatPattern {

        int startLevel;
        int stride;
        int repeatCount;

        RepeatPattern(int startLevel, int stride, int repeatCount) {
            this.startLevel = startLevel;
            this.stride = stride;
            this.repeatCount = repeatCount;
        }

        public int getRepeatLength() {
            return (stride * repeatCount);
        }
    }

    public void shrinkBuilding(String schemFilename) {
        Schematic schematic = null;
        MatchLevels matchingLevels[];
        Strides strideLevels[];
        ArrayList<StepRep> levelGroups;

        // First read in the schematic so that we know the original size that we're starting with
        try {
            schematic = SchematicReader.readSchematicsFile(schemFilename);
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Find all of the groups of repeated schematic levels
        matchingLevels = findRepeatingLevels(schematic);

        // Now map out the distances between identical levels
        strideLevels = findStridePatterns(matchingLevels);

        // Now see if we can find groups of repeating levels (floors)
        levelGroups = findRepeatingGroups(strideLevels);

        // Now we just have to throw out any overlapping level patterns.
        eliminateOverlappingGroups(levelGroups);
        
        // Now create a new trimmed down schematic
        String basename, newFilename, newConfig, reinflatedFilename;
        if (schemFilename.endsWith(".schematic")) {
            basename = schemFilename.substring(0,schemFilename.length()-10);
        } else {
            basename = "building";
        }
        newFilename = basename + "_shrink.schematic";
        newConfig = basename + "_shrink.txt";
        reinflatedFilename = basename + "_reinflate.schematic";
        Building shrinkBuilding = new Building(0,0,5,newFilename);
        
        //        System.out.println(newFilename);
        Collections.sort(levelGroups,StepRepStartLevComparator);
        int levIndex = 0;
        int shrinkHeight = 0;
        for (StepRep sRep: levelGroups) {
            shrinkHeight += sRep.startLev - levIndex + sRep.stride;
//            System.out.println("shrinkHeight: " + shrinkHeight);
            levIndex = sRep.getEndLev()+1;
        }
        shrinkHeight += schematic.height - levIndex;
//        System.out.println("shrinkHeight: " + shrinkHeight);
        Schematic shrinkSchematic = new Schematic(schematic.width,schematic.length,shrinkHeight);
        shrinkSchematic.Allocate();
 
        int newStartIndex = 0; // Current start step of shrink schematic
        int newEndIndex = 0;   // Current end step of shrink schematic
        int oldIndex = 0; // Current index of original schematic
        int curIndex = 0; // Current index of shrink schematic
        int oldEndLev = 0;// Previous level end of original schematic

        for (StepRep sRep: levelGroups) {
//            System.out.println("new sRep");
            newEndIndex = curIndex + sRep.startLev - oldEndLev + sRep.stride;
            shrinkBuilding.addRepeatedFloor(newEndIndex - sRep.stride,newEndIndex - 1, sRep.repeat, sRep.repeat);
            for (curIndex = newStartIndex; curIndex < newEndIndex; curIndex++) {
//                System.out.printf("Inserting: new - %d, old %d\n",curIndex,oldIndex);
                shrinkSchematic.insertSchematicSlice(curIndex,oldIndex++,schematic);
            }
            newStartIndex = curIndex;
            oldIndex = sRep.getEndLev() + 1;
            oldEndLev = oldIndex;
        }

        for (curIndex = newStartIndex; curIndex < shrinkHeight; curIndex++) {
//            System.out.printf("Inserting: new - %d, old %d\n",curIndex,oldIndex);
            shrinkSchematic.insertSchematicSlice(curIndex,oldIndex++,schematic);
        }
        
        try {
            SchematicWriter.writeSchematicsFile(shrinkSchematic, newFilename);
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(BuildingShrinker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        shrinkBuilding.setDimsFromSchematic();
        Schematic reinflatedSchematic = shrinkBuilding.getSchematic();
        
          try {
            SchematicWriter.writeSchematicsFile(reinflatedSchematic, reinflatedFilename);
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(BuildingShrinker.class.getName()).log(Level.SEVERE, null, ex);
        }
      

    }

    MatchLevels[] findRepeatingLevels(Schematic schematic) {
        ArrayList<MatchLevels> matchLevList = new ArrayList<>();
        MatchLevels[] matchingLevels = new MatchLevels[schematic.height];
        // Find all of the sets of repeated floors.
        for (int startPos = 0; startPos < schematic.height - 1; startPos++) {
            if (matchingLevels[startPos] == null) {
                for (int curFloor = startPos + 1; curFloor < schematic.height; curFloor++) {
                    if (schematic.slicesEqual(startPos, curFloor) == true) {
                        if (matchingLevels[startPos] == null) {
                            matchingLevels[startPos] = new MatchLevels(startPos);
                            matchLevList.add(matchingLevels[startPos]);
                        }
                        matchingLevels[startPos].addMatchingLevel(curFloor);
                        matchingLevels[curFloor] = matchingLevels[startPos];
                    }
                }
            }
        }

        System.out.println("Matching Levels:");
        for (MatchLevels mLevel : matchLevList) {
            System.out.println(mLevel.levels);
        }
        return matchingLevels;
    }

    Strides[] findStridePatterns(MatchLevels[] matchingLevels) {
        int height = matchingLevels.length;
        // Now calculate the stride patterns for each level, i.e. the distances between identical
        // floors. Example: floors 4, 8, 12, 16, 20 are identical, so we'd have a stride pattern starting
        // at '4' of length '4', and also one of length '8' (4, 12, 20), etc.
        Strides[] strideLevels = new Strides[height];
        for (int startPos = 0; startPos < height - 1; startPos++) {
            if (matchingLevels[startPos] != null) {
                strideLevels[startPos] = new Strides(startPos, height, matchingLevels[startPos]);
            }
        }
        return strideLevels;
    }

    ArrayList<StepRep> findRepeatingGroups(Strides[] strideLevels) {
        ArrayList<StepRep> levelPatterns = new ArrayList<>();
        StepRep bestLevelPattern = null, curLevelPattern = null;
        int height = strideLevels.length;

        // Now looking for repeating patterns between the levels themselves.
        for (int startPos = 0; startPos < height - 1; startPos++) {
            if (strideLevels[startPos] != null) {
                bestLevelPattern = null;
                // Iterating through each stride from this floor location. Goal is to find the longest
                // stride that is a repeated pattern.
                for (Map.Entry<Integer, StepRep> entry : strideLevels[startPos].strideMap.entrySet()) {
                    StepRep stepRep = entry.getValue();
                    if (stepRep.used == true) { // If true, we've already used this in an earlier chain
                        continue;
                    }
                    int curRepeat = stepRep.repeat;
                    for (int stepCheck = 1; stepCheck < stepRep.stride; stepCheck++) {
                        // Check through the entire stride length for a repeating pattern
                        if (strideLevels[startPos + stepCheck] == null) {
                            // Whoops, this level never repeats. No pattern here.
                            curRepeat = 0;
                            break;
                        }
                        StepRep checkRep = strideLevels[startPos + stepCheck].strideMap.get(stepRep.stride);
                        if (checkRep == null) {
                            // This level repeats, but not at this stride amount. Punt.
                            curRepeat = 0;
                            break;
                        }
                        if (checkRep.repeat < curRepeat) {
                            // This level repeats at the same stride, but check to see if
                            // maybe it doesn't go as far as the other levels, and adjust.
                            curRepeat = checkRep.repeat;
                        }
                    }
                    if (curRepeat > 0) { // Found a pattern!
                        // If we found a repeating set, flag all of the blocks higher up to not check again at
                        // the same stride value, since they can only find a shorter version of the same
                        // pattern, which is not interesting.
                        for (int i = startPos; i < startPos + (curRepeat * stepRep.stride); i++) {
                            if ((strideLevels[i] != null)
                                    && (strideLevels[i].strideMap.get(stepRep.stride) != null)) {
                                strideLevels[i].strideMap.get(stepRep.stride).used = true;
                            }
                        }
                        curLevelPattern = new StepRep(startPos, stepRep.stride, curRepeat);
                        if ((bestLevelPattern == null)
                                || (curLevelPattern.getRepeatLength() > bestLevelPattern.getRepeatLength())) {
                            bestLevelPattern = curLevelPattern;
                        }
                    }
                }
            }
            if (bestLevelPattern != null) {
                levelPatterns.add(bestLevelPattern);
            }
        }
        return levelPatterns;

    }

    void eliminateOverlappingGroups(ArrayList<StepRep> levelPatterns) {
        int height = 0;
        // OK, find the maximum pattern height
        for (StepRep sRep : levelPatterns) {
            if (sRep.getEndLev() > height) {
                height = sRep.getEndLev();
            }
        }
        // Now we just have to throw out any overlapping level patterns.
        Collections.sort(levelPatterns, StepRepComparator);
        boolean usedLevels[] = new boolean[height + 1];
        Iterator<StepRep> rsIter = levelPatterns.iterator();
        while (rsIter.hasNext()) {
            StepRep curStep = rsIter.next();
            boolean isOverlap = false;
            for (int i = curStep.startLev; i <= curStep.getEndLev(); i++) {
                if (usedLevels[i] == true) {
                    isOverlap = true;
                    break;
                }
            }
            if (isOverlap == true) {
                rsIter.remove();
            } else {
                for (int i = curStep.startLev; i <= curStep.getEndLev(); i++) {
                    usedLevels[i] = true;
                }
            }
        }

        for (StepRep sRep : levelPatterns) {
            System.out.printf("Found -- Start: %d, stride: %d, max repeat: %d\n",
                    sRep.startLev, sRep.stride, sRep.repeat);
        }
        
    }
}
