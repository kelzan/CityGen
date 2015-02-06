package schematic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

/**
 * Handles all stuff for writing schematic files
 *
 * @author klarson
 *
 */
public class SchematicWriter {

    public static void writeSchematicsFile(Schematic schematic, String filename) throws IOException,
            ClassicNotSupportedException, ParseException {
        File f = new File(filename);
        writeSchematicsFile(schematic, f);
    }

    /**
     * Writes the given schematic to a new file
     *
     * @param f the File
     * @param schematic a SliceStack-object to build the schematics out of
     * @throws IOException
     * @throws ClassicNotSupportedException
     * @throws ParseException
     */
    public static void writeSchematicsFile(Schematic schematic, File f) throws IOException,
            ClassicNotSupportedException, ParseException {
        try {
//            int numOfBlocks = stack.getHeight() * stack.getWidth() * stack.getLength();

            Map<String, Tag> masterMap = new HashMap<>();
            masterMap.put("Entities", new ListTag("Entities", ByteTag.class, new ArrayList<Tag>()));
//            masterMap.put("TileEntities", new ListTag("TileEntities", ByteTag.class, new ArrayList<Tag>()));
            masterMap.put("Materials", new StringTag("Materials", "Alpha"));
            masterMap.put("Height", new ShortTag("Height", (short) schematic.height));
            // length in MC means the depth (height of stack) but in MCSchematicTool, it means the width of the slice
            masterMap.put("Length", new ShortTag("Length", (short) schematic.length));
            masterMap.put("Width", new ShortTag("Width", (short) schematic.width));

//			byte[] blocks = new byte[numOfBlocks];
//			byte[] data = new byte[numOfBlocks];
//			ArrayList<Tag> tileEntities = new ArrayList<Tag>();


            // Blocks in MC are saved as a byte array which is ordered first by the height (lowest first),
            // then by the length (nord-south) and finally by the width (west-east)
            // substituting letters for blocks, that means the array [a, b, c, d, e, f, g, h] would result in two slices like the following ones (assuming
            // a height, width and length of 2):
            // (top)
            // ef
            // gh
            //
            // ab
            // cd
            // (bottom)

//			int blocknumber = 0;

//			masterMap.put("TileEntities", new ListTag("TileEntities", CompoundTag.class, tileEntities));
            masterMap.put("Blocks", new ByteArrayTag("Blocks", schematic.blocks));
            masterMap.put("Data", new ByteArrayTag("Data", schematic.data));

            if (schematic.tileEntities != null) {
                masterMap.put("TileEntities", new ListTag("TileEntities", CompoundTag.class, schematic.tileEntities));
            } else {
                masterMap.put("TileEntities", new ListTag("TileEntities", ByteTag.class, new ArrayList<Tag>()));
            }

            CompoundTag master = new CompoundTag("Schematic", masterMap);
            NBTOutputStream nos = new NBTOutputStream(new FileOutputStream(f));
            nos.writeTag(master);
            nos.close();
        } catch (ClassCastException e) {
            throw new ParseException(e);
        }
    }
}
