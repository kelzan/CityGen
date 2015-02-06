/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.util.Random;

/**
 *
 * @author Daddy
 */
public class BuildingTile extends Tile {

    /**
     * Enum to keep track of which direction this building will face. By default, buildings face
     * SOUTH (after applying the preRotation amount). Any direction other than SOUTH will require
     * that the building be rotated as it is laid down.
     */
    public enum BuildingDirection {

        SOUTH(0), NORTH(2), EAST(1), WEST(3);
        private final int rotAmount;

        BuildingDirection(int rotAmount) {
            this.rotAmount = rotAmount;
        }

        int getRotAmount() {
            return rotAmount;
        }
    }
    public Building building;
    static Random rnd = new Random();
    BuildingDirection buildingDirection; // How many times to rotate CW when we place it.

    public BuildingTile(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    public BuildingTile(int x1, int y1, int x2, int y2, BuildingDirection bDir, Building building) {
        super(x1, y1, x2, y2);
        this.building = building;
        this.buildingDirection = bDir;
    }

    public BuildingTile(Tile sizeTile) {
        super(sizeTile.x1, sizeTile.y1, sizeTile.x2, sizeTile.y2);
    }

    public int numberCWRotations() {
        return (building.preRotation + buildingDirection.getRotAmount()) % 4;
    }
}
