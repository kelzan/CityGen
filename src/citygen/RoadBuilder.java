/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Daddy
 */
public class RoadBuilder {

    static Random rnd = new Random();

    class RoadWay {

        int x;
        int y;
        int direction;
        MapGrid mapGrid;
        int lastRightFork;
        int lastLeftFork;

        public RoadWay(int x, int y, int direction, MapGrid mapGrid) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.mapGrid = mapGrid;
            lastRightFork = lastLeftFork = -1;
        }

        public void moveOnDownTheRoad() {
            switch (direction) {
                case MapLoc.NORTH:
                    y++;
                    break;
                case MapLoc.SOUTH:
                    y--;
                    break;
                case MapLoc.EAST:
                    x++;
                    break;
                case MapLoc.WEST:
                    x--;
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            if (lastRightFork >= 0) {
                lastRightFork++;
            }
            if (lastLeftFork >= 0) {
                lastLeftFork++;
            }
        }

        boolean isCurLocBad() {
            return ((x < 0) || (y < 0) || (x >= mapGrid.xsize) || (y >= mapGrid.ysize)
                    || (mapGrid.map[x][y].contents != 0));
        }

        void putDownAsphalt() {
            mapGrid.map[x][y].contents = 1;
        }

        public boolean atEnd() {
            int newX = x;
            int newY = y;
            boolean retVal = false;

            switch (direction) {
                case MapLoc.NORTH:
                    newY++;
                    retVal = (newY == mapGrid.ysize);
                    break;
                case MapLoc.SOUTH:
                    newY--;
                    retVal = (y == 0);
                    break;
                case MapLoc.EAST:
                    newX++;
                    retVal = (newX == mapGrid.xsize);
                    break;
                case MapLoc.WEST:
                    newX--;
                    retVal = (x == 0);
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            // Treat a non-road obstruction the same as the end of the map
            if ((retVal == false) && (mapGrid.map[newX][newY].contents > 1)) {
                retVal = true;
            }
            return retVal;
        }

        boolean leftDiagBlocked() {
            boolean retVal;
            retVal = false;
//            switch (direction) {
//                case MapLoc.NORTH:
//                    retVal = ULHasRoad() || LLHasRoad();
//                    break;
//                case MapLoc.SOUTH:
//                    retVal = URHasRoad() || LRHasRoad();
//                    break;
//                case MapLoc.EAST:
//                    retVal = URHasRoad() || ULHasRoad();
//                    break;
//                case MapLoc.WEST:
//                    retVal = LLHasRoad() || LRHasRoad();
//                    break;
//                default:
//                    throw new RuntimeException("Unknown Direction");
//            }
            return retVal;
        }

        boolean rightDiagBlocked() {
            boolean retVal;
            retVal = false;
//            switch (direction) {
//                case MapLoc.NORTH:
//                    retVal = URHasRoad() || LRHasRoad();
//                    break;
//                case MapLoc.SOUTH:
//                    retVal = ULHasRoad() || LLHasRoad();
//                    break;
//                case MapLoc.EAST:
//                    retVal = LLHasRoad() || LRHasRoad();
//                    break;
//                case MapLoc.WEST:
//                    retVal = ULHasRoad() || URHasRoad();
//                    break;
//                default:
//                    throw new RuntimeException("Unknown Direction");
//            }
            return retVal;
        }

        boolean noLeftTurn() {
            boolean retVal;
            switch (direction) {
                case MapLoc.NORTH:
                    retVal = ((x == 0) || (mapGrid.map[x - 1][y].contents != 0));
                    break;
                case MapLoc.SOUTH:
                    retVal = ((x + 1 == mapGrid.xsize) || (mapGrid.map[x + 1][y].contents != 0));
                    break;
                case MapLoc.EAST:
                    retVal = ((y + 1 == mapGrid.ysize) || (mapGrid.map[x][y + 1].contents != 0));
                    break;
                case MapLoc.WEST:
                    retVal = ((y == 0) || (mapGrid.map[x][y - 1].contents != 0));
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            return retVal;
        }

        boolean noRightTurn() {
            boolean retVal;
            switch (direction) {
                case MapLoc.NORTH:
                    retVal = ((x + 1 == mapGrid.xsize) || (mapGrid.map[x + 1][y].contents != 0));
                    break;
                case MapLoc.SOUTH:
                    retVal = ((x == 0) || (mapGrid.map[x - 1][y].contents != 0));
                    break;
                case MapLoc.EAST:
                    retVal = ((y == 0) || (mapGrid.map[x][y - 1].contents != 0));
                    break;
                case MapLoc.WEST:
                    retVal = ((y + 1 == mapGrid.ysize) || (mapGrid.map[x][y + 1].contents != 0));
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            return retVal;
        }

        boolean isRunningParallel() {
            boolean retVal;
            switch (direction) {
                case MapLoc.NORTH:
                    retVal = (URHasRoad() && RHasRoad()) || (LHasRoad() && ULHasRoad());
                    break;
                case MapLoc.SOUTH:
                    retVal = (LLHasRoad() && LHasRoad()) || (LRHasRoad() && RHasRoad());
                    break;
                case MapLoc.EAST:
                    retVal = (URHasRoad() && UHasRoad()) || (LRHasRoad() && DHasRoad());
                    break;
                case MapLoc.WEST:
                    retVal = (ULHasRoad() && UHasRoad()) || (LLHasRoad() && DHasRoad());
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            return retVal;
        }

        boolean recentRightFork(int tolerance) {
            return (((lastRightFork >= 0) && (lastRightFork < tolerance)) ? true : false);
        }

        boolean recentLeftFork(int tolerance) {
            return (((lastLeftFork >= 0) && (lastLeftFork < tolerance)) ? true : false);
        }

        void setLeftFork() {
            switch (direction) {
                case MapLoc.NORTH:
                    mapGrid.map[x][y].forks[MapLoc.WEST] = true;
                    break;
                case MapLoc.SOUTH:
                    mapGrid.map[x][y].forks[MapLoc.EAST] = true;
                    break;
                case MapLoc.EAST:
                    mapGrid.map[x][y].forks[MapLoc.NORTH] = true;
                    break;
                case MapLoc.WEST:
                    mapGrid.map[x][y].forks[MapLoc.SOUTH] = true;
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            lastLeftFork = 0;
        }

        void setRightFork() {
            switch (direction) {
                case MapLoc.NORTH:
                    mapGrid.map[x][y].forks[MapLoc.EAST] = true;
                    break;
                case MapLoc.SOUTH:
                    mapGrid.map[x][y].forks[MapLoc.WEST] = true;
                    break;
                case MapLoc.EAST:
                    mapGrid.map[x][y].forks[MapLoc.SOUTH] = true;
                    break;
                case MapLoc.WEST:
                    mapGrid.map[x][y].forks[MapLoc.NORTH] = true;
                    break;
                default:
                    throw new RuntimeException("Unknown Direction");
            }
            lastRightFork = 0;
        }

        void markForkDone() {
            mapGrid.map[x][y].forks[direction] = false;
        }

        boolean isValid(int checkx, int checky) {
            return ((checkx >= 0) && (checkx < mapGrid.xsize)
                    && (checky >= 0) && (checky < mapGrid.ysize));
        }

        boolean ULHasRoad() {
            int newX = x - 1;
            int newY = y + 1;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean URHasRoad() {
            int newX = x + 1;
            int newY = y + 1;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean LLHasRoad() {
            int newX = x - 1;
            int newY = y - 1;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean LRHasRoad() {
            int newX = x + 1;
            int newY = y - 1;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean RHasRoad() {
            int newX = x + 1;
            int newY = y;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean LHasRoad() {
            int newX = x - 1;
            int newY = y;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean UHasRoad() {
            int newX = x;
            int newY = y + 1;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }

        boolean DHasRoad() {
            int newX = x;
            int newY = y - 1;
            return (isValid(newX, newY) && (mapGrid.map[newX][newY].contents == 1));
        }
    }

    public enum Chances {

        HARD_LEFT, HARD_RIGHT, LEFT_FORK, RIGHT_FORK, T_INTERSECTION,
        DEAD_END, X_CROSSING, STRAIGHT
    };
    EnumMap<Chances, Integer> chanceMap = new EnumMap<>(Chances.class);

    public RoadBuilder() {
        chanceMap.put(Chances.HARD_LEFT, 5);
        chanceMap.put(Chances.HARD_RIGHT, 5);
        chanceMap.put(Chances.LEFT_FORK, 15);
        chanceMap.put(Chances.RIGHT_FORK, 15);
        chanceMap.put(Chances.T_INTERSECTION, 5);
        chanceMap.put(Chances.DEAD_END, 5);
        chanceMap.put(Chances.X_CROSSING, 15);
        chanceMap.put(Chances.STRAIGHT, 300);
    }

    public RoadBuilder(EnumMap<Chances, Integer> chanceMap) {
        this.chanceMap = chanceMap;
    }

    public void setStart(MapGrid mapGrid, int x, int y) {
        // TODO: Should check for contents first, and find the closest available if not
        mapGrid.map[x][y].forks[MapLoc.NORTH] = true;
        mapGrid.map[x][y].forks[MapLoc.SOUTH] = true;
        mapGrid.map[x][y].forks[MapLoc.EAST] = true;
        mapGrid.map[x][y].forks[MapLoc.WEST] = true;
        mapGrid.map[x][y].contents = 1;
    }

    public void setEntrance(MapGrid mapGrid, int x, int y, boolean northsouth) {
        mapGrid.map[x][y].contents = 1;
        mapGrid.map[x][y].ptype = PieceType.ENTRANCE;
        if (northsouth) {
            mapGrid.map[x][y].forks[MapLoc.NORTH] = true;
            mapGrid.map[x][y].forks[MapLoc.SOUTH] = true;
            mapGrid.map[x][y].prot = 1;
        } else {
            mapGrid.map[x][y].forks[MapLoc.EAST] = true;
            mapGrid.map[x][y].forks[MapLoc.WEST] = true;
            mapGrid.map[x][y].prot = 2;
        }
    }

    public void genRoads(MapGrid mapGrid) {
        RoadWay curRoad;
//        setStart(mapGrid,startx,starty);
//        mapGrid.PrintForks();
        while ((curRoad = pickRoad(mapGrid)) != null) {
            layRoad(curRoad);
//            mapGrid.PrintForks();
        }
    }

    RoadWay pickRoad(MapGrid mapGrid) {
        ArrayList<RoadWay> available = new ArrayList<>();
        RoadWay chosenRoad = null;
        int totalChoices;

        // Create a list of all available road choices
        for (int x = 0; x < mapGrid.xsize; x++) {
            for (int y = 0; y < mapGrid.ysize; y++) {
                for (int d = 0; d < 4; d++) {
                    if (mapGrid.map[x][y].forks[d] == true) {
                        available.add(new RoadWay(x, y, d, mapGrid));
                    }
                }
            }
        }
        if ((totalChoices = available.size()) > 0) {
            // Make a random selection
            chosenRoad = available.get(rnd.nextInt(totalChoices));
            chosenRoad.markForkDone();
        }

        return chosenRoad;
    }

    void layRoad(RoadWay road) {
        boolean goodToGo = true;

        while (goodToGo == true) {
            road.moveOnDownTheRoad();
            // If we ran off the map, or into another road we exit
            if (road.isCurLocBad() || road.isRunningParallel()) {
                goodToGo = false;
                continue;
            }
            // Mark our current location as having road
            road.putDownAsphalt();
            // Now mark any forks generated for the location
            goodToGo = markForks(road);
//            road.mapGrid.PrintForks();
        }
    }

    boolean markForks(RoadWay road) {
        boolean continueOn;
        // First see which directions are blocked to us because of the end
        // of the map, other roads, or other obstacles.
        boolean atEndOfMap = road.atEnd();
        boolean rightDiagBlocked = road.rightDiagBlocked();
        boolean leftDiagBlocked = road.leftDiagBlocked();
        boolean noLeftTurn = road.noLeftTurn();
        boolean noRightTurn = road.noRightTurn();
        boolean recentRightTurn = road.recentRightFork(1);
        boolean recentLeftTurn = road.recentLeftFork(1);
        boolean isRunningParallel = road.isRunningParallel();

        // Make a copy of chance weightings
        EnumMap<Chances, Integer> currentChances = chanceMap.clone();
        // Now modify our chances based on what is possible to do
        if (noLeftTurn || leftDiagBlocked || recentLeftTurn) { // No Left Turn/Fork possible
            currentChances.put(Chances.HARD_LEFT, 0);
            currentChances.put(Chances.LEFT_FORK, 0);
        }
        if (noRightTurn || rightDiagBlocked || recentRightTurn) { // No Right Turn/Fork possible
            currentChances.put(Chances.HARD_RIGHT, 0);
            currentChances.put(Chances.RIGHT_FORK, 0);
        }
        if (noLeftTurn || leftDiagBlocked || noRightTurn || rightDiagBlocked
                || recentRightTurn || recentLeftTurn) { // T-Intersection
            currentChances.put(Chances.T_INTERSECTION, 0);
            if (atEndOfMap || isRunningParallel) { // X-Intersection
                currentChances.put(Chances.X_CROSSING, 0);
            }
        }
        if (atEndOfMap || isRunningParallel) {
            currentChances.put(Chances.STRAIGHT, 0);
        }
        if (isRunningParallel) {
            currentChances.put(Chances.RIGHT_FORK, 0);
            currentChances.put(Chances.LEFT_FORK, 0);
        }

        // Now select action based on current chances
        Chances selection = makeSelection(currentChances);
//        System.out.println("Chances: " + currentChances);
//        System.out.println("Selection: " + selection);

        switch (selection) {
            case HARD_LEFT:
                continueOn = false;
                road.setLeftFork();
                break;
            case HARD_RIGHT:
                continueOn = false;
                road.setRightFork();
                break;
            case LEFT_FORK:
                continueOn = true;
                road.setLeftFork();
                break;
            case RIGHT_FORK:
                continueOn = true;
                road.setRightFork();
                break;
            case T_INTERSECTION:
                continueOn = false;
                road.setLeftFork();
                road.setRightFork();
                break;
            case DEAD_END:
                continueOn = false;
                break;
            case X_CROSSING:
                continueOn = true;
                road.setLeftFork();
                road.setRightFork();
                break;
            case STRAIGHT:
                continueOn = true;
                break;
            default:
                continueOn = true;
                break;
        }
        return continueOn;
    }

    Chances makeSelection(EnumMap<Chances, Integer> currentChances) {
        Chances retChance = null;
        int totalChances = 0;
        for (Map.Entry<Chances, Integer> entry : currentChances.entrySet()) {
            totalChances += entry.getValue();
        }
        int rollTheDice = rnd.nextInt(totalChances);
        int curTally = 0;

        for (Map.Entry<Chances, Integer> entry : currentChances.entrySet()) {
            curTally += entry.getValue();
            if (curTally > rollTheDice) {
                retChance = entry.getKey();
                break;
            }
        }
        if (retChance == null) {
            throw new RuntimeException("Unexpected randomization error");
        }
        return retChance;
    }
}
