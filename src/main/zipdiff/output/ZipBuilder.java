/* zipdiff is available under the terms of the 
 * Apache License, version 2.0
 * 
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import zipdiff.Differences;

/**
 * creates a zip file with the new versions of files that have been added or modified
 * 
 * @author Hendrik Brummermann, HIS GmbH
 */
public class ZipBuilder extends AbstractBuilder {

    public void build(OutputStream out, Differences d) {
        try {
            ZipOutputStream os = new ZipOutputStream(out);
            Set entrySet = getEntries(d);
            writeEntries(os, entrySet);
        } catch (IOException e) {
            System.err.println("Error while writing zip file: " + e);
            e.printStackTrace();
        }
    }

    /**
     * gets a set of entries which should be included in the new zip file
     *
     * @param d Differences
     * @return set of Map.Entries
     */
    private Set getEntries(Differences d) {
        Set entrySet = new HashSet();
        entrySet.addAll(d.getAdded().values());
        entrySet.addAll(d.getChanged().values());
        return entrySet;
    }

    /**
     * returns the ZipEntry from the value of the specified Map.Entry
     *
     * @param mapEntry Map.Entry
     * @return ZipEntry
     */
    private ZipEntry getZipEntry(Map.Entry mapEntry) {
        if (mapEntry.getValue() instanceof ZipEntry) {
            return (ZipEntry) mapEntry.getValue();
        } else {
            return ((ZipEntry[]) mapEntry.getValue())[1];
        }
    }

    /**
     * writes the entries to the new zip file. 
     * Note: This method ignores the content of nested zip 
     * files and simply adds the complete zip file.
     *
     * @param os ZipOutputStream
     * @param entrySet set of map entries to write
     * @throws IOException in case of an input/output error
     */
    private void writeEntries(ZipOutputStream os, Set entrySet) throws IOException {
        Iterator itr = entrySet.iterator();
        while (itr.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) itr.next();
            if (mapEntry.getKey().toString().indexOf("!") < 0) {
                ZipEntry entry = getZipEntry(mapEntry);
                os.putNextEntry(entry);
            }
        }
    }
}
