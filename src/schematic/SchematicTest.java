/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schematic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author klarson
 */
public class SchematicTest {

    public static void main(String[] args) {
        // TODO code application logic here
        SchematicTest test = new SchematicTest();
        test.DoTest();
    }

    public void DoTest() {
        Schematic schematic, schematicx, schematicy;
        Schematic bigschematic;

        try {
            schematic = SchematicReader.readSchematicsFile("shack.schematic");
            schematic.printSchematic();
            schematic.rotXY();
            schematic.printSchematic();
            SchematicWriter.writeSchematicsFile(schematic, "shackrotxy.schematic");
            
//            schematicx = new Schematic(5,5,1);
//            schematicx.Allocate();
//            Arrays.fill(schematicx.blocks,(byte)35);
//            Arrays.fill(schematicx.data,(byte)15);
//            schematicx.printSchematic();
//            SchematicWriter.writeSchematicsFile(schematicx, "lameroad.schematic");
//            schematicx = SchematicReader.readSchematicsFile("mc-corner.schematic");
//            schematicy = SchematicReader.readSchematicsFile("corner_rot.schematic");
//            System.out.println("TESTX Original");
//            schematicx.printSchematic();
//            System.out.println("TESTY Original");
//            schematicy.printSchematic();
//
//            SchematicWriter.writeSchematicsFile(schematicx, "mytestx.schematic");
//            SchematicWriter.writeSchematicsFile(schematicy, "mytesty.schematic");
//
//            schematicx.rotXY();
//            SchematicWriter.writeSchematicsFile(schematicx, "testx-xy.schematic");
//            schematicy.rotXY();
//            SchematicWriter.writeSchematicsFile(schematicy, "testy-xy.schematic");
//            System.out.println("TESTX Rotated");
//            schematicx.printSchematic();
//            System.out.println("TESTY Rotated");
//            schematicy.printSchematic();


//            bigschematic = new Schematic(20, 20, 20);
//            bigschematic.Allocate();
//            schematic = SchematicReader.readSchematicsFile("4x3x1.schematic");
////            schematicx = new Schematic(schematic);
////            schematicx.printSchematic();
//            bigschematic.insertSchematic(0, 0, 0, schematic);
//            bigschematic.insertSchematic(5, 0, 0, schematic);
//            bigschematic.insertSchematic(10, 0, 0, schematic);
//            schematic.rotXY();
//            bigschematic.insertSchematic(0, 10, 0, schematic);
//            bigschematic.insertSchematic(5, 10, 0, schematic);
//            bigschematic.insertSchematic(10, 10, 0, schematic);
//            bigschematic.insertSchematic(0, 0, 10, schematic);
//            bigschematic.insertSchematic(5, 0, 10, schematic);
//            bigschematic.insertSchematic(10, 0, 10, schematic);
//
//            SchematicWriter.writeSchematicsFile(bigschematic, "big.schematic");

            //            f = new File("foo.schematic");
//            SchematicWriter.writeSchematicsFile(schematic, f);
//            SchematicWriter.writeSchematicsFile(schematic, "foo.schematic");
//            schematic = SchematicReader.readSchematicsFile("foo.schematic");
//            schematic = new Schematic(10, 10, 5);
//            schematic.Allocate();
//            Arrays.fill(schematic.blocks, (byte) 2);
//            SchematicWriter.writeSchematicsFile(schematic, "bigblock.schematic");
        } catch (IOException | ClassicNotSupportedException | ParseException ex) {
            Logger.getLogger(SchematicTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        //        submap.ReadFile("smallmap.txt");
        //        submap.ReadFile("medmap.txt");
        //        submap.CalculatePieces();
        //        submap.WriteFile("foofile.txt");
        //        submap.PrintPieces();
        //        submap.ReadFile("foofile.txt");
        //submap.ReadFile("foofile.txt");
        //submap.ReadFile("foofile.txt");

    }
}
