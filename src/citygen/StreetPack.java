/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import schematic.ClassicNotSupportedException;
import schematic.ParseException;
import schematic.Schematic;
import schematic.SchematicReader;
import subgen.SubGenTest;

/**
 *
 * @author Daddy
 */
public class StreetPack {

    EnumMap<PieceType, String> fileNames = new EnumMap<>(PieceType.class);
    EnumMap<PieceType, ArrayList<Schematic>> schematics = new EnumMap<>(PieceType.class);
    public int scale = 1;
    public int fillEmpty = 0;
    public byte fillEmptyBlock = 0;

    public void setFilename(PieceType ptype, String fname) {
        fileNames.put(ptype, fname);
    }

    public void initPieceArrays() {
        Iterator<PieceType> enumKeySet = fileNames.keySet().iterator();
        schematics.clear();
        while (enumKeySet.hasNext()) {
            PieceType currentPiece = enumKeySet.next();
            schematics.put(currentPiece, new ArrayList<Schematic>());
        }
    }

    public void layStreets(MapGrid map, Schematic schematic, int zOffset) {
        Schematic curPiece;
        ArrayList<Schematic> curArray;

        // Initialize arrays of pieces with all possible rotations
        initPieceArrays();

        map.calculatePieces();

        // TODO: Check scaling size here

        FillRotArray(4, schematics.get(PieceType.CORNER), fileNames.get(PieceType.CORNER));
        FillRotArray(4, schematics.get(PieceType.TEE), fileNames.get(PieceType.TEE));
        FillRotArray(2, schematics.get(PieceType.STRAIGHT), fileNames.get(PieceType.STRAIGHT));
        FillRotArray(1, schematics.get(PieceType.CROSS), fileNames.get(PieceType.CROSS));
        FillRotArray(4, schematics.get(PieceType.END), fileNames.get(PieceType.END));
        if (fileNames.containsKey(PieceType.ENTRANCE)) {
            FillRotArray(4, schematics.get(PieceType.ENTRANCE), fileNames.get(PieceType.ENTRANCE));
        }

        for (int y = 0; y < map.ysize; y++) {
            for (int x = 0; x < map.xsize; x++) {
                if ((map.map[x][y].contents == 1)
                        && (map.map[x][y].ptype != PieceType.NONE)) {
                    curArray = schematics.get(map.map[x][y].ptype);
                    curPiece = curArray.get(map.map[x][y].prot);
                    schematic.insertSchematic(x * scale, y * scale, zOffset, curPiece);
                } else if ((map.map[x][y].contents == 0) && (fillEmpty > 0)) {
                    schematic.fillSchematic(x * scale, y * scale, zOffset, 
                            scale, scale, fillEmpty, fillEmptyBlock, (byte) 0);
                }
            }
        }
//        try {
//            SchematicWriter.writeSchematicsFile(schematic, "mysubway.schematic");
//        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
//            Logger.getLogger(SubGenTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void FillRotArray(int rotnum, ArrayList<Schematic> rotarray, String filename) {
        Schematic subpiece, rotpiece;
        try {
            subpiece = SchematicReader.readSchematicsFile(filename);
            for (int i = 0; i < rotnum; i++) {
                rotpiece = new Schematic(subpiece);
                rotarray.add(rotpiece);
                subpiece.rotXY();
            }
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(SubGenTest.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
