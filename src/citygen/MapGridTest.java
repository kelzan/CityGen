/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package citygen;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import schematic.ClassicNotSupportedException;
import schematic.ParseException;
import schematic.Schematic;
import schematic.SchematicReader;
import schematic.SchematicWriter;

/**
 *
 * @author klarson
 */
public class MapGridTest {

    MapGrid submap = new MapGrid(10, 10);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MapGridTest test = new MapGridTest();
//        test.CityGenTest();
        test.LayRoadTest();
//        test.StretchTest();
//        test.ShrinkTest();
//        test.BuildingPackTest();
    }

    public void StretchTest() {
        Building myPagoda;
        Schematic schematic;

        myPagoda = new Building(2, 2, 10, "buildings/pagodatower.schematic");
        myPagoda.addRepeatedFloor(9, 12, 5, 30);
        myPagoda.addRepeatedFloor(17, 20, 5, 30);
        myPagoda.addRepeatedFloor(24, 24, 1, 3);
        myPagoda.addRepeatedFloor(33, 33, 1, 10);
//        myPagoda.addRepeatedFloor(0, 0, 0, 0);
        schematic = myPagoda.getSchematic();

        try {
            SchematicWriter.writeSchematicsFile(schematic, "stretchedtower.schematic");
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(MapGridTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ShrinkTest() {
//        Building myBigPagoda = new Building(5, "stretched.schematic");
//        Building myBigPagoda = new Building(5, "buildings/Strygler Tower.schematic");
//        Building myBigPagoda = new Building(5, "buildings/Kaerakin Office Tower v.5.schematic");
//        Building myBigPagoda = new Building(5, "buildings/Netherrack Hotel.schematic");
//        Building myBigPagoda = new Building(5, "buildings/Bayside Tower.schematic");
//        Building myBigPagoda = new Building(5, "buildings/Tzameret Tower (single).schematic");
//        Building myBigPagoda = new Building(5, "buildings/Ardity Tower v.2.schematic");
//        Building myBigPagoda = new Building(5, "buildings/Hesperia Tower v.4.schematic");
//        Building myBigPagoda = new Building(5, "buildings/The Skelcere Building v.2.schematic");
        BuildingShrinker buildingShrinker = new BuildingShrinker();

        buildingShrinker.shrinkBuilding("stretched.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Hesperia Tower v.4.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Netherrack Hotel.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Bayside Tower.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Netherrack Hotel.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Tzameret Tower (single).schematic");
//        buildingShrinker.shrinkBuilding("buildings/Ardity Tower v.2.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Strygler Tower.schematic");
//        buildingShrinker.shrinkBuilding("buildings/The Skelcere Building v.2.schematic");
//        buildingShrinker.shrinkBuilding("buildings/Kaerakin Office Tower v.5.schematic");
//        buildingShrinker.shrinkBuilding("buildings/10x10store.schematic");
//        buildingShrinker.shrinkBuilding("buildings/bSIED6.schematic");
        
    }

    public void LayRoadTest() {
        MapGrid mapgrid = new MapGrid(50, 50);
        RoadBuilder roadBuilder = new RoadBuilder();
        roadBuilder.setStart(mapgrid, 5, 5);
        roadBuilder.genRoads(mapgrid);
        System.out.println("Final:");
        mapgrid.printMap();
        mapgrid.writeFile("maptest.txt");
    }

    public void CityGenTest() {
        BuildingPack myBuildings = new BuildingPack();
        Schematic streetSchematic = null, levelSchematic;

//        myBuildings.addBuilding(new SolidBuilding(4, 4, 5, (byte) 1));
//        myBuildings.addBuilding(new SolidBuilding(1, 1, 5, (byte) 1));
//        myBuildings.addBuilding(new SolidBuilding(2, 2, 5, (byte) 1));
//        myBuildings.addBuilding(new SolidBuilding(3, 3, 5, (byte) 1));
//        myBuildings.addBuilding(new SolidBuilding(4, 4, 5, (byte) 41));
//        myBuildings.addBuilding(new SolidBuilding(1, 1, 5, (byte) 41));
//        myBuildings.addBuilding(new SolidBuilding(2, 2, 5, (byte) 41));
//        myBuildings.addBuilding(new SolidBuilding(3, 3, 5, (byte) 41));
//        myBuildings.addBuilding(new SolidBuilding(4, 4, 5, (byte) 5));
//        myBuildings.addBuilding(new SolidBuilding(1, 1, 5, (byte) 5));
//        myBuildings.addBuilding(new SolidBuilding(2, 2, 5, (byte) 5));
//        myBuildings.addBuilding(new SolidBuilding(3, 3, 5, (byte) 5));

//        for (int gsize = 1; gsize < 3; gsize++) {
//            Building newBuilding = new SolidBuilding(gsize, gsize, 10, (byte) 1);
//            myBuildings.addBuilding(newBuilding);
//            newBuilding = new SolidBuilding(gsize, 2 * gsize, 10, (byte) 5);
//            myBuildings.addBuilding(newBuilding);
//            newBuilding = new SolidBuilding(3 * gsize, gsize, 10, (byte) 41);
//            myBuildings.addBuilding(newBuilding);
////            newBuilding = new SolidBuilding(gsize, gsize, 10, (byte) 5);
////            newBuilding.setUseLimit(3);
////            myBuildings.addBuilding(newBuilding);
////            newBuilding = new SolidBuilding(gsize, gsize, 10, (byte) 41);
////            newBuilding.setUseLimit(1);
////            myBuildings.addBuilding(newBuilding);
//        }
        myBuildings.readFile("buildings/buildings.txt");

        MapGrid streetMap = new MapGrid(40, 40);
        RoadBuilder roadBuilder = new RoadBuilder();
        roadBuilder.setStart(streetMap, 10, 10);
        roadBuilder.genRoads(streetMap);
        streetMap.printMap();

        StreetPack doubleStreets = new StreetPack();
        doubleStreets.setFilename(PieceType.CORNER, "streets/dstreet-corner.schematic");
        doubleStreets.setFilename(PieceType.TEE, "streets/dstreet-tee.schematic");
        doubleStreets.setFilename(PieceType.STRAIGHT, "streets/dstreet-straight.schematic");
        doubleStreets.setFilename(PieceType.CROSS, "streets/dstreet-cross.schematic");
        doubleStreets.setFilename(PieceType.END, "streets/dstreet-end.schematic");
        doubleStreets.scale = 10;
        doubleStreets.fillEmpty = 0;
        doubleStreets.fillEmptyBlock = 2; // Dirt

        Schematic doubleSchematic = new Schematic(400, 400, 191);
        doubleSchematic.Allocate();

        doubleStreets.layStreets(streetMap, doubleSchematic, 0);

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
            doubleSchematic.insertSchematic(bTile.x1 * 10, bTile.y1 * 10, 0, bSchematic);
        }
        try {
            SchematicWriter.writeSchematicsFile(doubleSchematic, "citylevel2.schematic");
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(MapGridTest.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void BuildingPackTest() {
        BuildingPack buildingPack;
        buildingPack = new BuildingPack();
        buildingPack.readFile("buildings/buildings.txt");
//        buildingPack.readFile("buildings/foodir/foo.txt");
//        buildingPack.readFile("foo.txt");
    }
}
