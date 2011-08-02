/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import zipdiff.Differences;
import zipdiff.util.StringUtil;

/**
 * creates a zip file with the new versions of files that have been added or modified
 *
 * @author Hendrik Brummermann, HIS GmbH
 */
public class ZipBuilder extends AbstractBuilder {
	private Differences differences;

	private final Set filenames = new TreeSet();

	/**
	 * builds the output
	 *
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	@Override
	public void build(OutputStream out, Differences d) {
		differences = d;
		try {
			collectAddedFiles();
			collectModifiedFiles();
			copyEntries(out);
		} catch (IOException e) {
			System.err.println("Error while writing zip file: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * collects all the files that have been added in the second zip archive
	 */
	private void collectAddedFiles() {
		Set entrySet = differences.getAdded().entrySet();
		Iterator itr = entrySet.iterator();
		while (itr.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) itr.next();
			if (mapEntry.getKey().toString().indexOf("!") < 0) {
				filenames.add(((ZipEntry) mapEntry.getValue()).getName());
			}
		}
	}

	/**
	 * collects all the files that have been added modified in the second zip archive
	 */
	private void collectModifiedFiles() {
		Set entrySet = differences.getChanged().entrySet();
		Iterator itr = entrySet.iterator();
		while (itr.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) itr.next();
			if (mapEntry.getKey().toString().indexOf("!") < 0) {
				filenames.add(((ZipEntry[]) mapEntry.getValue())[1].getName());
			}
		}
	}

	/**
	 * copies the zip entries (with data) from the second archive file to the output file.
	 *
	 * @param out output file
	 * @throws IOException in case of an input/output error
	 */
	private void copyEntries(OutputStream out) throws IOException {
		ZipOutputStream os = new ZipOutputStream(out);
		ZipFile zipFile = new ZipFile(differences.getFilename2());
		Iterator itr = filenames.iterator();

		while (itr.hasNext()) {
			String filename = (String) itr.next();
			ZipEntry zipEntry = zipFile.getEntry(filename);
			InputStream is = zipFile.getInputStream(zipEntry);
			ZipEntry z = new ZipEntry(StringUtil.removeDirectoryPrefix(filename, numberOfOutputPrefixesToSkip));
			os.putNextEntry(z);
			copyStream(is, os);
			os.closeEntry();
			is.close();
		}

		zipFile.close();
		os.close();
	}

	/**
	 * copies data from an input stream to an output stream
	 *
	 * @param input InputStream
	 * @param output OutputStream
	 * @throws IOException in case of an input/output error
	 */
	private void copyStream(InputStream input, OutputStream output) throws IOException {
		byte buffer[] = new byte[4096];
		int count = input.read(buffer);
		while (count > -1) {
			output.write(buffer, 0, count);
			count = input.read(buffer);
		}
	}
}
