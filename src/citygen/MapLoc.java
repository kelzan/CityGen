/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

/**
 * What type of piece at a specific location.
 *
 * @author klarson
 */
//enum Direction {
//
//    NORTH, SOUTH, EAST, WEST
//}
/**
 *
 * @author klarson
 */
public class MapLoc {

    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public int contents;
    public PieceType ptype;
    public int prot;
    public Tile tile;
    public ForkType forks[];

    public MapLoc() {
        forks = new ForkType[4];
        resetLoc();
    }

    public int numForks() {
        int cnt = 0;
        for (ForkType b : forks) {
            cnt = (b != ForkType.NO_FORK) ? cnt + 1 : cnt;
        }
        return cnt;
    }

    public void resetLoc() {
        ptype = PieceType.NONE;
        forks[NORTH] = forks[SOUTH] = forks[EAST] = forks[WEST] = ForkType.NO_FORK;
        contents = 0;
        prot = 0;
        tile = null;
    }

    public void setForks(ForkType ft) {
        forks[NORTH] = forks[SOUTH] = forks[EAST] = forks[WEST] = ft;
    }

    public void printForks() {
        System.out.printf("NORTH: %s, SOUTH: %s, EAST: %s, WEST: %s\n",
                forks[NORTH].name(), forks[SOUTH].name(), forks[EAST].name(), forks[WEST].name());
    }

    public static int getOppositeDirection(int direction) {
        int retval;
        switch (direction) {
            case NORTH:
                retval = SOUTH;
                break;
            case SOUTH:
                retval = NORTH;
                break;
            case EAST:
                retval = WEST;
                break;
            case WEST:
                retval = EAST;
                break;
            default:
                throw new RuntimeException("Unknown Direction");
        }
        return retval;
    }
}
