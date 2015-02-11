/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schematic;

import java.util.List;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.Tag;

/**
 *
 * @author klarson
 */
public class Schematic {

    public int width;
    public int length;
    public int height;
    byte[] blocks;
    byte[] data;
    List<Tag> tileEntities;

    public Schematic() {
    }

    public Schematic(Schematic copyobj) {
        width = copyobj.width;
        length = copyobj.length;
        height = copyobj.height;
        blocks = new byte[copyobj.blocks.length];
        data = new byte[copyobj.data.length];
        System.arraycopy(copyobj.blocks, 0, blocks, 0, copyobj.blocks.length);
        System.arraycopy(copyobj.data, 0, data, 0, copyobj.data.length);
    }

    public Schematic(int width, int length, int height) {
        this.width = width;
        this.length = length;
        this.height = height;
    }

    public void Allocate() {
        blocks = new byte[width * length * height];
        data = new byte[width * length * height];
    }

    public void rotXY() {
        // Save the old data/block arrays
        byte[] olddata = data;
        byte[] oldblocks = blocks;

        // Create new arrays for the rotated data
        data = new byte[width * length * height];
        blocks = new byte[width * length * height];

        // Step through the old arrays and update the new array at the new offsets
        // CW:
        // x' = y
        // y' = width - x - 1
        // CCW:
        // x' = length - y - 1
        // y' = x
        for (int z = 0; z < height; z++) {
            for (int y = 0; y < length; y++) {
                for (int x = 0; x < width; x++) {
                    int oldoff = x + (y * width) + (z * width * length);
                    int newoff = (length - y - 1) + (x * length) + (z * width * length); // CCW
//                    int newoff = (y) + ((width - x - 1) * length) + (z * width * length); // CW
//                    System.out.printf("(%d,%d,%d):%d-%d\n", x, y, z, oldoff, newoff);
//                    if ((oldoff > blocks.length) || (newoff > blocks.length)) {
//                        int dummy =0;
//                    }
                    blocks[newoff] = oldblocks[oldoff];
                    data[newoff] = transDataXY(oldblocks[oldoff], olddata[oldoff]);
                }
            }
        }
        rotTileEntities();
        // Now update the width/length values, which have swapped
        int temp = width;
        width = length;
        length = temp;
    }

    public void rotTileEntities() {
        int oldX, oldY, newX, newY;
        if (tileEntities != null) {
            for (Tag tag : tileEntities) {
                CompoundTag cTag = (CompoundTag) tag;
                oldX = (int) cTag.getValue().get("x").getValue();
                oldY = (int) cTag.getValue().get("z").getValue();
                newX = length - oldY - 1;
                newY = oldX;
                cTag.getValue().put("x", new IntTag("x", newX));
                cTag.getValue().put("z", new IntTag("z", newY));
            }
        }
    }

    public byte getBlock(int x, int y, int z) {
        return blocks[x + (y * width) + (z * width * length)];
    }

    public byte getData(int x, int y, int z) {
        return data[x + (y * width) + (z * width * length)];
    }

    public void setBlock(int x, int y, int z, byte b) {
        blocks[x + (y * width) + (z * width * length)] = b;
    }

    public void setData(int x, int y, int z, byte d) {
        data[x + (y * width) + (z * width * length)] = d;
    }

    public void fillSchematic(int x, int y, int z,
            int xoff, int yoff, int zoff, byte b, byte d) {
        for (int curZ = z; curZ < z + zoff; curZ++) {
            for (int curY = y; curY < y + yoff; curY++) {
                for (int curX = x; curX < x + xoff; curX++) {
                    setBlock(curX, curY, curZ, b);
                    setData(curX, curY, curZ, d);
                }
            }
        }
    }

    public void fillSchematic(byte b, byte d) {
        //TODO Should fix this, should not subtract 1 here.
//        fillSchematic(0, 0, 0, width - 1, length - 1, height - 1, b, d);
        fillSchematic(0, 0, 0, width, length, height, b, d);
    }

    public void printSchematic() {
        System.out.println("--Blocks--");
        for (int z = 0; z < height; z++) {
            for (int y = 0; y < length; y++) {
                for (int x = 0; x < width; x++) {
                    System.out.printf("%02x ", getBlock(x, y, z));
                }
                System.out.println();
            }
            System.out.println("---");
        }
        System.out.println("--Data--");
        for (int z = 0; z < height; z++) {
            for (int y = 0; y < length; y++) {
                for (int x = 0; x < width; x++) {
                    System.out.printf("%02x ", getData(x, y, z));
                }
                System.out.println();
            }
            System.out.println("---");
        }
    }

    public void insertSchematic(int xoff, int yoff, int zoff, Schematic sub) {
        if ((xoff + sub.width) > width) {
            System.out.printf("Can't insert beyond object width (%d > %d)\n", xoff + sub.width, width);
            return;
        }
        if ((yoff + sub.length) > length) {
            System.out.printf("Can't insert beyond object length (%d > %d)\n", yoff + sub.length, length);
            return;
        }
        if ((zoff + sub.height) > height) {
            System.out.printf("Can't insert beyond object height (%d > %d)\n", zoff + sub.height, height);
            return;
        }
        for (int z = 0; z < sub.height; z++) {
            for (int y = 0; y < sub.length; y++) {
                for (int x = 0; x < sub.width; x++) {
                    setData(xoff + x, yoff + y, zoff + z, sub.getData(x, y, z));
                    setBlock(xoff + x, yoff + y, zoff + z, sub.getBlock(x, y, z));
                }
            }
        }
    }

    public void insertSchematicSlice(int zDest, int zSrc, Schematic sub) {
        if ((width < sub.width) || (length < sub.length) || (zDest >= height) || (zSrc >= sub.height)) {
            throw new RuntimeException("Out of bounds slice insertion");
        }
        for (int y = 0; y < sub.length; y++) {
            for (int x = 0; x < sub.width; x++) {
                setData(x, y, zDest, sub.getData(x, y, zSrc));
                setBlock(x, y, zDest, sub.getBlock(x, y, zSrc));
            }
        }
    }

    public boolean slicesEqual(int slice1, int slice2) {
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if ((getBlock(x, y, slice1) != getBlock(x, y, slice2))
                        || (getData(x, y, slice1) != getData(x, y, slice2))) {
                    return false;
                }
            }
        }
        return true;
    }

    public byte transDataXY(byte ob, byte od) {
        byte nd;
        // Check to see if we're transforming stairs
        if ((ob == 53) || (ob == 67) || (ob == 108) || (ob == 109) || (ob == 114)
                || (ob == -128) || (ob == -122) || (ob == -121) || (ob == -120) || (ob == -100)
                || (ob == -93) || (ob == -92) || (ob == -76)) {
            switch (od & 0x03) { // Lower two bits
                case 0: // east -> south
                    nd = (byte) ((od & 0xfc) + 2);
                    break;
                case 1: // west -> north
                    nd = (byte) ((od & 0xfc) + 3);
                    break;
                case 2: // south -> west
                    nd = (byte) ((od & 0xfc) + 1);
                    break;
                case 3: // north -> east
                    nd = (byte) ((od & 0xfc) + 0);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if (ob == 17) { // Wood
            switch (od & 0x0c) { // bits 2-3
                case 4: // east/west -> north/south
                    nd = (byte) ((od & 0xf3) + 8);
                    break;
                case 8: // north/south -> east/west
                    nd = (byte) ((od & 0xf3) + 4);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 50) || (ob == 75) || (ob == 76)) { // Torches
            switch (od) {
                case 1: // east -> south
                    nd = 3;
                    break;
                case 2: // west -> north
                    nd = 4;
                    break;
                case 3: // south -> west
                    nd = 2;
                    break;
                case 4: // north -> east
                    nd = 1;
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 33) || (ob == 34)) { // Piston/Piston Extension
            switch (od & 0x07) { // Lower 3 bits
                case 2: // north -> east
                    nd = (byte) ((od & 0xf8) + 5);
                    break;
                case 3: //south -> west
                    nd = (byte) ((od & 0xf8) + 4);
                    break;
                case 4: // west -> north
                    nd = (byte) ((od & 0xf8) + 2);
                    break;
                case 5: // east -> south
                    nd = (byte) ((od & 0xf8) + 3);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 63) || (ob == -80)) { // Sign Post/Standing Banner
            // It's mapped like a 16 point compass, so rotating 4 notches is like 90 degrees.
            nd = (byte) ((od + 4) & 0xf);
        } else if (((ob == 64) || (ob == 71)) && ((od & 0x08) == 0)) { // Doors (bottom half)
            switch (od & 0x03) { // Lower two bits
                case 0: // west -> north
                    nd = (byte) ((od & 0xfc) + 1);
                    break;
                case 1: // north -> east
                    nd = (byte) ((od & 0xfc) + 2);
                    break;
                case 2: // east -> south
                    nd = (byte) ((od & 0xfc) + 3);
                    break;
                case 3: // south -> west
                    nd = (byte) ((od & 0xfc) + 0);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if (ob == 66) { // Rails
            switch (od) {
                case 0: // north/south -> east/west
                    nd = 1;
                    break;
                case 1: // east/west -> north/south
                    nd = 0;
                    break;
                case 2: // east -> south
                    nd = 5;
                    break;
                case 3: // west -> north
                    nd = 4;
                    break;
                case 4: // north -> east
                    nd = 2;
                    break;
                case 5: // south -> west
                    nd = 3;
                    break;
                case 6: // northwest corner -> northeast corner
                    nd = 7;
                    break;
                case 7: // northeast corner -> southeast corner
                    nd = 8;
                    break;
                case 8: // southeast corner -> southwest corner
                    nd = 9;
                    break;
                case 9: // southwest corner -> northwest corner
                    nd = 6;
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 27) || (ob == 28) || (ob == -99)) { // Powered/Activator/Detector Rails
            switch (od & 0x07) { // Bottom three bits
                case 0: // north/south -> east/west
                    nd = (byte) ((od & 0xf8) + 1);
                    break;
                case 1: // east/west -> north/south
                    nd = (byte) ((od & 0xf8) + 0);
                    break;
                case 2: // east -> south
                    nd = (byte) ((od & 0xf8) + 5);
                    break;
                case 3: // west -> north
                    nd = (byte) ((od & 0xf8) + 4);
                    break;
                case 4: // north -> east
                    nd = (byte) ((od & 0xf8) + 2);
                    break;
                case 5: // south -> west
                    nd = (byte) ((od & 0xf8) + 3);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 65) || (ob == 68) || (ob == 61) || (ob == 62) || (ob == 54) 
                || (ob == 130) || (ob == -79) || (ob == -110)) {
            // Ladders, Wall Signs, Wall Banners, Furnaces, Chests
            switch (od) {
                case 2: // north -> east
                    nd = 5;
                    break;
                case 3: // south -> west
                    nd = 4;
                    break;
                case 4: // west -> north
                    nd = 2;
                    break;
                case 5: // east -> south
                    nd = 3;
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if (ob == 69) { // Levers
            switch (od & 0x07) { // Bottom three bits
                case 1: // Wall: east -> south
                    nd = (byte) ((od & 0xf8) + 3);
                    break;
                case 2: // Wall: west -> north
                    nd = (byte) ((od & 0xf8) + 4);
                    break;
                case 3: // Wall: south -> west
                    nd = (byte) ((od & 0xf8) + 2);
                    break;
                case 4: // Wall: north -> east
                    nd = (byte) ((od & 0xf8) + 1);
                    break;
                case 5: // Ground: south -> east
                    nd = (byte) ((od & 0xf8) + 6);
                    break;
                case 6: // Ground: east -> south
                    nd = (byte) ((od & 0xf8) + 5);
                    break;
                case 7: // Ceiling: south -> east
                    nd = (byte) ((od & 0xf8) + 8);
                    break;
                case 8: // Ceiling: east -> south
                    nd = (byte) ((od & 0xf8) + 7);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 77) || (ob == -113)) { // Buttons
            switch (od & 0x07) { // Lower three bits
                case 1: // east -> south
                    nd = (byte) ((od & 0xf8) + 3);
                    break;
                case 2: // west -> north
                    nd = (byte) ((od & 0xf8) + 4);
                    break;
                case 3: // south -> west
                    nd = (byte) ((od & 0xf8) + 2);
                    break;
                case 4: // north -> east
                    nd = (byte) ((od & 0xf8) + 1);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 86) || (ob == 91) || (ob == 96) || (ob == 107) || (ob == 119)
                || (ob == -73) || (ob == -72) || (ob == -71) || (ob == -70) || (ob == -69) 
                || (ob == -89) || (ob == -125) || (ob == 26)) {
            // Pumpkins/Jack O Lanterns, Trapdoors, Gates, End Portal Block, Tripwire Hook, Beds
            switch (od & 0x03) { // Lower two bits
                case 0: // south -> west
                    nd = (byte) ((od & 0xfc) + 1);
                    break;
                case 1: // west -> north
                    nd = (byte) ((od & 0xfc) + 2);
                    break;
                case 2: // north -> east
                    nd = (byte) ((od & 0xfc) + 3);
                    break;
                case 3: // east -> south
                    nd = (byte) ((od & 0xfc) + 0);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 93) || (ob == 94)) { // Redstone Repeater
            switch (od & 0x03) { // Lower two bits
                case 0: // north -> east
                    nd = (byte) ((od & 0xfc) + 1);
                    break;
                case 1: // east -> south
                    nd = (byte) ((od & 0xfc) + 2);
                    break;
                case 2: // south -> west
                    nd = (byte) ((od & 0xfc) + 3);
                    break;
                case 3: // west -> north
                    nd = (byte) ((od & 0xfc) + 0);
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if ((ob == 99) || (ob == 100)) { // Huge Brown and Red Mushroom
            switch (od) {
                case 1: // Corner: top west north -> top north east
                    nd = 3;
                    break;
                case 2: // Side: top north -> top east
                    nd = 6;
                    break;
                case 3: // Corner: top north east -> top east south
                    nd = 9;
                    break;
                case 4: // Side: top west -> top north
                    nd = 2;
                    break;
                case 6: // Side: top east -> top south
                    nd = 8;
                    break;
                case 7: // Corner: top south west -> top west north
                    nd = 1;
                    break;
                case 8: // Side: top south -> top west
                    nd = 4;
                    break;
                case 9: // Corner: top east south -> top south west
                    nd = 7;
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if (ob == 106) { // Vines
            // Vines are bit flags with possibly multiple directions
            nd = 0;
            if ((od & 0x01) == 1) { // south -> west
                nd += 2;
            }
            if ((od & 0x02) == 2) { // west -> north
                nd += 4;
            }
            if ((od & 0x04) == 4) { // north -> east
                nd += 8;
            }
            if ((od & 0x08) == 8) { // east -> south
                nd += 1;
            }
        } else if (ob == -112) { // Mob heads
            switch (od) {
                case 2: // north -> east
                    nd = 4;
                    break;
                case 3: // south -> west
                    nd = 5;
                    break;
                case 4: // east -> south
                    nd = 3;
                    break;
                case 5: // west -> north
                    nd = 2;
                    break;
                default:
                    nd = od;
                    break;
            }
        } else if (ob == -101) { // Block of Quartz
            switch (od) {
                case 3: // north/south -> east/west
                    nd = 4;
                    break;
                case 4: // east/west -> north/south
                    nd = 3;
                    break;
                default:
                    nd = od;
                    break;
            }

        } else {
            nd = od;
        }
        return nd;
    }
}
