/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subgen;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import citygen.BuildingPack;
import citygen.BuildingTile;
import citygen.MapGrid;
import citygen.StreetPack;
import citygen.PieceType;
import citygen.RoadBuilder;
import citygen.Tile;
import schematic.ClassicNotSupportedException;
import schematic.ParseException;
import schematic.Schematic;
import schematic.SchematicReader;
import schematic.SchematicWriter;

/**
 *
 * @author klarson
 */
public class SubGenTest {

    public static void main(String[] args) {
        SubGenTest test = new SubGenTest();

        test.DoNewTest();
    }

    public void DoNewTest() {
        StreetPack bigSubway;
//        StreetPack lameStreets;
        StreetPack doubleStreets;
        Schematic subSchematic;
//        Schematic streetSchematic;
//        Schematic doubleSchematic;
        MapGrid subMap;
        MapGrid streetMap;
        RoadBuilder subBuilder;
        BuildingPack myBuildings = new BuildingPack();

        subMap = new MapGrid(20, 20);
        streetMap = new MapGrid(30, 30);


        subBuilder = new RoadBuilder();
        subBuilder.setStart(subMap, 10, 10);
        setEntranceLocation(subBuilder, subMap, streetMap, 6, 6, true);
        setEntranceLocation(subBuilder, subMap, streetMap, 15, 16, false);
        setEntranceLocation(subBuilder, subMap, streetMap, 6, 16, true);
        setEntranceLocation(subBuilder, subMap, streetMap, 15, 6, false);
//        subBuilder.setEntrance(subMap, 5, 5, true);
//        subBuilder.setEntrance(subMap, 15, 15, false);
//        subBuilder.setEntrance(subMap, 5, 15, true);
//        subBuilder.setEntrance(subMap, 15, 5, false);
        subMap.printMap();
        subBuilder.genRoads(subMap);

//        subMap.readFile("smallmap.txt");
//
        // First generate a big ol' subway
        bigSubway = new StreetPack();
        bigSubway.setFilename(PieceType.CORNER, "subway/mc-corner.schematic");
        bigSubway.setFilename(PieceType.TEE, "subway/mc-tee.schematic");
        bigSubway.setFilename(PieceType.STRAIGHT, "subway/mc-straight.schematic");
        bigSubway.setFilename(PieceType.CROSS, "subway/mc-cross.schematic");
        bigSubway.setFilename(PieceType.END, "subway/mc-end.schematic");
        bigSubway.setFilename(PieceType.ENTRANCE, "subway/mc-entrance.schematic");
        bigSubway.scale = 15;

        subSchematic = new Schematic(15 * subMap.xsize, 15 * subMap.ysize, 226);
        subSchematic.Allocate();

        bigSubway.layStreets(subMap, subSchematic, 0);
        subMap.printMap();
        streetMap.printMap();

        // Now generate some streets

        RoadBuilder roadBuilder = new RoadBuilder();
        roadBuilder.setStart(streetMap, 15, 15);
        roadBuilder.genRoads(streetMap);
        System.out.println("Final:");
        streetMap.printMap();

        doubleStreets = new StreetPack();
        doubleStreets.setFilename(PieceType.CORNER, "streets/dstreet-corner.schematic");
        doubleStreets.setFilename(PieceType.TEE, "streets/dstreet-tee.schematic");
        doubleStreets.setFilename(PieceType.STRAIGHT, "streets/dstreet-straight.schematic");
        doubleStreets.setFilename(PieceType.CROSS, "streets/dstreet-cross.schematic");
        doubleStreets.setFilename(PieceType.END, "streets/dstreet-end.schematic");
        doubleStreets.scale = 10;
        doubleStreets.fillEmpty = 1;
        doubleStreets.fillEmptyBlock = 2; // Dirt

//        doubleSchematic = new Schematic(300, 300, 6);
//        doubleSchematic.Allocate();

        doubleStreets.layStreets(streetMap, subSchematic, 20);
//        subSchematic.insertSchematic(0, 0, 20, doubleSchematic);


        // OK, now plop some buildings down
        myBuildings.readFile("buildings/buildings.txt");
        while (streetMap.isFull() == false) {
            Tile curEmptyTile = streetMap.getLargestEmptyTile();
            BuildingTile newBuildingTile = myBuildings.findMatchingBuilding(curEmptyTile);
            streetMap.allocateTile(newBuildingTile);
        }
        streetMap.printTiles();


        LinkedList<BuildingTile> buildingTileList = streetMap.usedTileList;
        for (int i = 0; i < buildingTileList.size(); i++) {
            BuildingTile bTile = buildingTileList.get(i);
            Schematic bSchematic = bTile.building.getSchematic();
            int numRots = bTile.numberCWRotations();
            for (int rot = 0; rot < numRots; rot++) {
                bSchematic.rotXY();
            }
            subSchematic.insertSchematic(bTile.x1 * 10, bTile.y1 * 10, 20, bSchematic);
        }


        // Now save the schematic
        try {
            SchematicWriter.writeSchematicsFile(subSchematic, "subandstreets.schematic");
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(SubGenTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setEntranceLocation(RoadBuilder subBuilder, MapGrid subMap, MapGrid streetMap,
            int subX, int subY, boolean northsouth) {
        subBuilder.setEntrance(subMap, subX, subY, northsouth);
        int streetX = (subX * 3) / 2;
        int streetY = (subY * 3) / 2;
        if (northsouth == true) {
            streetMap.map[streetX][streetY].contents = 2;
        } else {
            streetMap.map[streetX + 1][streetY].contents = 2;
        }
    }
//    public void DoTest() {
//        MapGrid submap;
//        Schematic subschematic;
//        Schematic curpiece;
//
//        ArrayList<Schematic> mc_corner = new ArrayList<>();
//        ArrayList<Schematic> mc_tee = new ArrayList<>();
//        ArrayList<Schematic> mc_straight = new ArrayList<>();
//        ArrayList<Schematic> mc_cross = new ArrayList<>();
//        ArrayList<Schematic> mc_end = new ArrayList<>();
//
//        submap = new MapGrid();
//        submap.readFile("smallmap.txt");
//        submap.calculatePieces();
//
//        subschematic = new Schematic(15 * submap.xsize, 15 * submap.ysize, 15);
//        subschematic.Allocate();
//
//        FillRotArray(4, mc_corner, "subway/new-corner.schematic");
//        FillRotArray(4, mc_tee, "subway/mc-tee.schematic");
//        FillRotArray(2, mc_straight, "subway/mc-straight.schematic");
//        FillRotArray(1, mc_cross, "subway/mc-cross.schematic");
//        FillRotArray(4, mc_end, "subway/mc-end.schematic");
////        subschematic.insertSchematic(0, 0, 0, mc_corner.get(0));
////        subschematic.insertSchematic(20, 0, 0, mc_corner.get(1));
////        subschematic.insertSchematic(40, 0, 0, mc_corner.get(2));
////        subschematic.insertSchematic(60, 0, 0, mc_corner.get(3));
//        for (int y = 0; y < submap.ysize; y++) {
//            for (int x = 0; x < submap.xsize; x++) {
//                if (submap.subMap[x][y].contents == 1) {
//                    switch (submap.subMap[x][y].ptype) {
//                        case CORNER:
//                            curpiece = mc_corner.get(submap.subMap[x][y].prot);
//                            break;
//                        case TEE:
//                            curpiece = mc_tee.get(submap.subMap[x][y].prot);
//                            break;
//                        case STRAIGHT:
//                            curpiece = mc_straight.get(submap.subMap[x][y].prot);
//                            break;
//                        case CROSS:
//                            curpiece = mc_cross.get(submap.subMap[x][y].prot);
//                            break;
//                        case END:
//                            curpiece = mc_end.get(submap.subMap[x][y].prot);
//                            break;
//                        default:
//                            curpiece = mc_cross.get(0); // Default
//                            break;
//                    }
//                    subschematic.insertSchematic(x * 15, y * 15, 0, curpiece);
//                }
//            }
//        }
//        try {
////            Schematic subpiece = SchematicReader.readSchematicsFile("subway/mc-corner.schematic");
////            SchematicWriter.writeSchematicsFile(subpiece, "corner1.schematic");
////            subpiece.printSchematic();
////            subpiece.rotXY();
////            subpiece.printSchematic();
////            SchematicWriter.writeSchematicsFile(subpiece, "corner2.schematic");
////            subpiece.rotXY();
////            SchematicWriter.writeSchematicsFile(subpiece, "corner3.schematic");
////            subpiece.rotXY();
////            SchematicWriter.writeSchematicsFile(subpiece, "corner4.schematic");
//
//            SchematicWriter.writeSchematicsFile(subschematic, "mysubway.schematic");
////            subschematic.printSchematic();
////            subschematic = SchematicReader.readSchematicsFile("mysubway.schematic");
//        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
//            Logger.getLogger(SubGenTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void FillRotArray(int rotnum, ArrayList<Schematic> rotarray, String filename) {
//        Schematic subpiece, rotpiece;
//        try {
//            subpiece = SchematicReader.readSchematicsFile(filename);
////            subpiece.printSchematic();
//            for (int i = 0; i < 4; i++) {
//                rotpiece = new Schematic(subpiece);
////                rotpiece.printSchematic();
//                rotarray.add(rotpiece);
//                subpiece.rotXY();
//
//
//            }
//        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
//            Logger.getLogger(SubGenTest.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//        lameMap = new MapGrid();
//        lameMap.readFile("lamestreetmap.txt");
//        lameStreets = new StreetPack();
//        lameStreets.setFilename(PieceType.CORNER, "streets/lamestreet.schematic");
//        lameStreets.setFilename(PieceType.TEE, "streets/lamestreet.schematic");
//        lameStreets.setFilename(PieceType.STRAIGHT, "streets/lamestreet.schematic");
//        lameStreets.setFilename(PieceType.CROSS, "streets/lamestreet.schematic");
//        lameStreets.setFilename(PieceType.END, "streets/lamestreet.schematic");
//        lameStreets.scale = 5;
//        lameStreets.fillEmpty = 1;
//        lameStreets.fillEmptyBlock = 2; // Dirt
//        streetSchematic = new Schematic(5 * lameMap.xsize, 5 * lameMap.ysize, 1);
//        streetSchematic.Allocate();
//        lameStreets.layStreets(lameMap, streetSchematic);
//        streetSchematic.printSchematic();
}
