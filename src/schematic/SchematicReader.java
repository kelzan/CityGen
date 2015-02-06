package schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

/**
 * Handles all stuff for reading schematic files
 *
 * @author klarson
 *
 */
public class SchematicReader {

    private static boolean hasErrorHappened = false; // a bit ugly, but oh well

    /**
     * checks if any single block could not be read, as that does not throw the parse exception
     * (instead the faulty block gets replaced by air)
     *
     * @return true if error happened
     */
    public static boolean hasErrorHappened() {
        return hasErrorHappened;
    }

    public static Schematic readSchematicsFile(String filename) throws IOException,
            ClassicNotSupportedException, ParseException {
        File f = new File(filename);
        return (readSchematicsFile(f));
    }

    /**
     * Reads the given schematics file
     *
     * @param f the File
     * @return a Schematic-object of the Schematics
     * @throws IOException
     * @throws ClassicNotSupportedException
     * @throws ParseException
     */
    public static Schematic readSchematicsFile(File f) throws IOException,
            ClassicNotSupportedException, ParseException {
        FileInputStream fis = new FileInputStream(f);

        // special case if f is not a gzip file (maybe unzipped schematic)
        try {
            GZIPInputStream gis = new GZIPInputStream(fis);
            gis.close();
        } catch (IOException e) {
            if (!e.getMessage().toLowerCase().contains("not in gzip format")) {
                // rethrow
                throw e;
            }
            f = GzipFile(f);
        }

        // Suck in the big CompoundTag which is outer wrapper for the schematic file
        NBTInputStream nis = new NBTInputStream(new FileInputStream(f));
        CompoundTag master = (CompoundTag) nis.readTag();
//        System.out.println(master);
        nis.close();
        Map<String, Tag> masterMap = master.getValue();

        if (masterMap.get("Materials") == null
                || !((StringTag) masterMap.get("Materials")).getValue().equalsIgnoreCase("alpha")) {
            throw new ClassicNotSupportedException();
        }

        try {

            // length in MC means the depth (height of stack) but in MCSchematicTool, it means the width of the slice
            Schematic schematic = new Schematic();
            schematic.length = ((ShortTag) masterMap.get("Length")).getValue();
            schematic.width = ((ShortTag) masterMap.get("Width")).getValue();
            schematic.height = ((ShortTag) masterMap.get("Height")).getValue();

            schematic.blocks = ((ByteArrayTag) masterMap.get("Blocks")).getValue();
            schematic.data = ((ByteArrayTag) masterMap.get("Data")).getValue();

            // get tile entities
            schematic.tileEntities = ((ListTag) masterMap.get("TileEntities")).getValue();

            return schematic;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseException(e);
        }
    }

    private static File GzipFile(File f) throws FileNotFoundException, IOException {
        // not gzip - zip it to temp
        String name = f.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        File outFile = File.createTempFile(name, ".schematic");
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFile));
        FileInputStream in = new FileInputStream(f);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        // Complete the GZIP file
        out.finish();
        out.close();
        f = outFile;
        f.deleteOnExit();
        System.err.println("Input schematic was not in gzip format! Tried to fix it to " + f.getAbsolutePath());
        return f;
    }
}
