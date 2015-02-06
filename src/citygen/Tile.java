/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

/**
 *
 * @author klarson
 */
public class Tile implements Comparable<Tile> {

    public int x1;
    public int x2;
    public int y1;
    public int y2;

    public Tile(int x1, int y1, int x2, int y2) {
        // Normalize the two vertices. Always LL, UR
        if (x2 < x1) {
            this.x2 = x1;
            this.x1 = x2;
        } else {
            this.x1 = x1;
            this.x2 = x2;
        }
        if (y2 < y1) {
            this.y2 = y1;
            this.y1 = y2;
        } else {
            this.y1 = y1;
            this.y2 = y2;
        }
    }

    boolean isOverlap(Tile otherTile) {
        return !(((x1 < otherTile.x1) && (x2 < otherTile.x1)) || ((x1 > otherTile.x2) && (x2 > otherTile.x2)) || ((y1 < otherTile.y1) && (y2 < otherTile.y1)) || ((y1 > otherTile.y2) && (y2 > otherTile.y2)));
    }

    public int getArea() {
        return (x2 - x1 + 1) * (y2 - y1 + 1);
    }

    public int getWidth() {
        return (x2 - x1 + 1);
    }

    public int getLength() {
        return (y2 - y1 + 1);
    }

    @Override
    public int compareTo(Tile t) {
        return getArea() - t.getArea();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tile)) {
            return false;
        }
        Tile rhs = (Tile) obj;
        return (x1 == rhs.x1) && (x2 == rhs.x2) && (y1 == rhs.y1) && (y2 == rhs.y2);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.x1;
        hash = 13 * hash + this.x2;
        hash = 13 * hash + this.y1;
        hash = 13 * hash + this.y2;
        return hash;
    }

    @Override
    public String toString() {
        return (String.format("(%d,%d)/(%d,%d):%d", x1, y1, x2, y2, getArea()));
    }
}
