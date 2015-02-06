/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import schematic.Schematic;

/**
 * Currently a Building is just a solid block of material.
 *
 * @author Daddy
 */
public class SolidBuilding extends Building {

    byte material;

    public SolidBuilding(int width, int length, int scale, byte material) {
        super(width, length, scale, "");
        this.material = material;
    }

    @Override
    public Schematic getSchematic() {
        Schematic schematic;
//        int newHeight = gridWidth * (rnd.nextInt(8) + 1);
        int newHeight = 10 + (rnd.nextInt(170));

        schematic = new Schematic(gridWidth * gridScale, gridLength * gridScale, newHeight);
        schematic.Allocate();
        schematic.fillSchematic(material, (byte) 0);
        return schematic;
    }
}
