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
//    public int nFork, sFork, eFork,wFork;
    public boolean forks[];

    public MapLoc() {
        ptype = PieceType.NONE;
//        nFork = sFork = eFork = wFork = 0;
        forks = new boolean[4];
        forks[NORTH] = forks[SOUTH] = forks[EAST] = forks[WEST] = false;
    }

    public int numForks() {
        int cnt = 0;
        for (boolean b : forks) {
            cnt = (b == true) ? cnt + 1 : cnt;
        }
        return cnt;
    }
}
