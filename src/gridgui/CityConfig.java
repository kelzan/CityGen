/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridgui;

import citygen.BuildingPack;
import citygen.BuildingTile;
import citygen.MapGrid;
import citygen.PieceType;
import citygen.RoadBuilder;
import citygen.StreetPack;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import schematic.ClassicNotSupportedException;
import schematic.ParseException;
import schematic.Schematic;
import schematic.SchematicWriter;

/**
 *
 * @author Daddy
 */
public class CityConfig {

    BuildingPack buildings;

    int xSize;
    int ySize;
    int zoom;

    MapGrid cityMap;
    MapGrid subwayMap;

    GridMapPane cityPane;
    GridMapPane subwayPane;

    static int mapscale = 10;

    CityConfig(GridGUI gui) {
        xSize = 30;
        ySize = 30;
        zoom = 20;

        gui.jTextXSize.setText(Integer.toString(xSize));
        gui.jTextYSize.setText(Integer.toString(ySize));

        cityPane = (GridMapPane) gui.jPanelCity;
        subwayPane = (GridMapPane) gui.jPanelSubway;
        
        setMaps(30, 30);

        buildings = new BuildingPack();
        buildings.readFile("buildings/buildings.txt");

        cityPane.buildings = buildings;

        setZoom(zoom);
    }

    void setZoom(int newZoom) {
        cityPane.setZoom(newZoom);
        subwayPane.setZoom(newZoom * 3 / 2);
    }

    void resizeMaps(int newX, int newY) {
        if ((xSize != newX) || (ySize != newY)) {
            setMaps(newX, newY);
        }
    }
    void setShowPaths(boolean showPaths) {
        cityPane.showPaths = showPaths;
        subwayPane.showPaths = showPaths;
    }
    
    void setAutoLink(boolean autoPaths) {
        cityPane.autoPaths = autoPaths;
        subwayPane.autoPaths = autoPaths;
    }
    void setMaps(int newX, int newY) {
        xSize = newX;
        ySize = newY;
        cityMap = new MapGrid(xSize, ySize);
        subwayMap = new MapGrid(xSize * 2 / 3, ySize * 2 / 3);
        cityPane.setMap(cityMap);
        subwayPane.setMap(subwayMap);        
    }

    void resetMaps() {
        cityMap.resetMap();
        subwayMap.resetMap();
        buildings.resetUsages();
//        cityPane.repaint();
//       subwayPane.repaint();
    }

    void layRoad(LevelType activeLevel) {
        switch (activeLevel) {
            case CITY: {
                RoadBuilder roadBuilder = new RoadBuilder();
//                roadBuilder.setStart(cityMap, 5, 5); // Fix eventually
                roadBuilder.genRoads(cityMap/*, cityPane*/);
                cityPane.repaint();
                break;
            }
            case SUBWAY: {
                RoadBuilder subBuilder = new RoadBuilder();
                subBuilder.setStart(subwayMap, 10, 10);
                setEntranceLocation(subBuilder, subwayMap, cityMap, 6, 6, true);
                setEntranceLocation(subBuilder, subwayMap, cityMap, 15, 16, false);
                setEntranceLocation(subBuilder, subwayMap, cityMap, 6, 16, true);
                setEntranceLocation(subBuilder, subwayMap, cityMap, 15, 6, false);
                subBuilder.genRoads(subwayMap);
                subwayPane.repaint();
                break;
            }
        }
    }

    public void setEntranceLocation(RoadBuilder subBuilder, MapGrid subwayMap, MapGrid cityMap,
            int subX, int subY, boolean northsouth) {
        subBuilder.setEntrance(subwayMap, subX, subY, northsouth);
        int streetX = (subX * 3) / 2;
        int streetY = (subY * 3) / 2;
        if (northsouth == true) {
            cityMap.map[streetX][streetY].contents = 2;
        } else {
            cityMap.map[streetX + 1][streetY].contents = 2;
        }
    }

    void saveSchematic(File file) {
        // Todo: Lot's of messy manual code needs to be packaged up in a more
        // modular way.

        // First generate a big ol' subway
        StreetPack bigSubway = new StreetPack();
        bigSubway.setFilename(PieceType.CORNER, "subway/mc-corner.schematic");
        bigSubway.setFilename(PieceType.TEE, "subway/mc-tee.schematic");
        bigSubway.setFilename(PieceType.STRAIGHT, "subway/mc-straight.schematic");
        bigSubway.setFilename(PieceType.CROSS, "subway/mc-cross.schematic");
        bigSubway.setFilename(PieceType.END, "subway/mc-end.schematic");
        bigSubway.setFilename(PieceType.ENTRANCE, "subway/mc-entrance.schematic");
        bigSubway.scale = 15;

        Schematic guiSchematic = new Schematic(15 * subwayMap.xsize, 15 * subwayMap.ysize, 226);
        guiSchematic.Allocate();

        bigSubway.layStreets(subwayMap, guiSchematic, 0); // Subway at the bottom level

        // Put together the street pack (this needs to be more automated
        StreetPack streets = new StreetPack();
        streets.setFilename(PieceType.CORNER, "streets/dstreet-corner.schematic");
        streets.setFilename(PieceType.TEE, "streets/dstreet-tee.schematic");
        streets.setFilename(PieceType.STRAIGHT, "streets/dstreet-straight.schematic");
        streets.setFilename(PieceType.CROSS, "streets/dstreet-cross.schematic");
        streets.setFilename(PieceType.END, "streets/dstreet-end.schematic");
        streets.scale = mapscale;
        streets.fillEmpty = 0;
        streets.fillEmptyBlock = 2; // Dirt

//        Schematic guiSchematic = new Schematic(cityMap.xsize * mapscale, cityMap.ysize * mapscale, 191);
//        guiSchematic.Allocate();

        streets.layStreets(cityMap, guiSchematic, 20);

        // Plop down the buildings
        for (int i = 0; i < cityMap.usedTileList.size(); i++) {
            BuildingTile bTile = cityMap.usedTileList.get(i);
            Schematic bSchematic = bTile.building.getSchematic();
            int numRots = bTile.numberCWRotations();
            for (int rot = 0; rot < numRots; rot++) {
                bSchematic.rotXY();
            }
            guiSchematic.insertSchematic(bTile.x1 * 10, bTile.y1 * 10, 20, bSchematic);
        }

        try {
            SchematicWriter.writeSchematicsFile(guiSchematic, file.getCanonicalPath());
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(CityConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
