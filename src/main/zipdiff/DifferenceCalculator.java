/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import zipdiff.util.StringUtil;

/**
 * Checks and compiles differences between two zip files.
 * It also has the ability to exclude entries from the comparison
 * based on a regular expression.
 *
 * @author Sean C. Sullivan, Hendrik Brummermann
 */
public class DifferenceCalculator {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private final ZipFile file1;

	private final ZipFile file2;

	private int numberOfPrefixesToSkip1 = 0;

	private int numberOfPrefixesToSkip2 = 0;

	private boolean ignoreTimestamps = false;

	private boolean ignoreCVSFiles = false;

	private boolean compareCRCValues = true;

	private Pattern filesToIgnorePattern;

	private boolean bVerbose = false;

	protected void debug(Object msg) {
		if (isVerboseEnabled()) {
			System.out.println("[" + DifferenceCalculator.class.getName() + "] " + String.valueOf(msg));
		}
	}

	/**
	 * Set the verboseness of the debug output.
	 * @param b true to make verbose
	 */
	public void setVerbose(boolean b) {
		bVerbose = b;
	}

	protected boolean isVerboseEnabled() {
		return bVerbose;
	}

	/**
	 * Constructor taking 2 filenames to compare
	 * @throws java.io.IOException
	 */
	public DifferenceCalculator(String filename1, String filename2) throws java.io.IOException {
		this(new File(filename1), new File(filename2));
	}

	/**
	 * Constructor taking 2 Files to compare
	 * @throws java.io.IOException
	 */
	public DifferenceCalculator(File f1, File f2) throws java.io.IOException {
		this(new ZipFile(f1), new ZipFile(f2));
	}

	/**
	 * Constructor taking 2 ZipFiles to compare
	 */
	public DifferenceCalculator(ZipFile zf1, ZipFile zf2) {
		file1 = zf1;
		file2 = zf2;
	}

	/**
	 *
	 * @param Set A set of regular expressions that when matched against a ZipEntry
	 * then that ZipEntry will be ignored from the comparison.
	 * @see java.util.regex
	 */
	public void setFilenameRegexToIgnore(Set patterns) {
		if (patterns == null) {
			filesToIgnorePattern = null;
		} else if (patterns.isEmpty()) {
			filesToIgnorePattern = null;
		} else {
			String regex = "";

			Iterator iter = patterns.iterator();
			while (iter.hasNext()) {
				String pattern = (String) iter.next();
				if (regex.length() > 0) {
					regex += "|";
				}
				regex += "(" + pattern + ")";
			}
			filesToIgnorePattern = Pattern.compile(regex);
			logger.log(Level.FINE, "Regular expression is : " + regex);
		}
	}

	/**
	 * returns true if fileToIgnorePattern matches the filename given.
	 * @param filepath
	 * @param filename The name of the file to check to see if it should be ignored.
	 * @return true if the file should be ignored.
	 */
	protected boolean ignoreThisFile(String filepath, String entryName) {
		if (entryName == null) {
			return false;
		} else if (isCVSFile(filepath, entryName) && (ignoreCVSFiles())) {
			return true;
		} else if (filesToIgnorePattern == null) {
			return false;
		} else {
			Matcher m = filesToIgnorePattern.matcher(entryName);
			boolean match = m.matches();
			if (match) {
				logger.log(Level.FINEST, "Found a match against : " + entryName + " so excluding");
			}
			return match;
		}
	}

	protected boolean isCVSFile(String filepath, String entryName) {
		if (entryName == null) {
			return false;
		} else if ((filepath.indexOf("CVS/") != -1) || (entryName.indexOf("CVS/") != -1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Ensure that the comparison checks against the CRCs of the entries.
	 * @param b true ensures that CRCs will be checked
	 */
	public void setCompareCRCValues(boolean b) {
		compareCRCValues = b;
	}

	/**
	 * @return true if this instance will check the CRCs of each ZipEntry
	 */
	public boolean getCompareCRCValues() {
		return compareCRCValues;
	}

	/**
	 * sets the number of directory prefixes to skip in the first file
	 *
	 * @param numberOfPrefixesToSkip1 number of directory prefixes to skip
	 */
	public void setNumberOfPrefixesToSkip1(int numberOfPrefixesToSkip1) {
		this.numberOfPrefixesToSkip1 = numberOfPrefixesToSkip1;
	}

	/**
	 * @return number of directory prefixes to skip
	 */
	public int getNumberOfPrefixesToSkip1() {
		return numberOfPrefixesToSkip1;
	}

	/**
	 * sets the number of directory prefixes to skip in the first file
	 *
	 * @param numberOfPrefixesToSkip2 number of directory prefixes to skip
	 */
	public void setNumberOfPrefixesToSkip2(int numberOfPrefixesToSkip2) {
		this.numberOfPrefixesToSkip2 = numberOfPrefixesToSkip2;
	}

	/**
	 * @return number of directory prefixes to skip
	 */
	public int getNumberOfPrefixesToSkip2() {
		return numberOfPrefixesToSkip2;
	}


	/**
	 * Opens the ZipFile and builds up a map of all the entries. The key is the name of
	 * the entry and the value is the ZipEntry itself.
	 * @param zf The ZipFile for which to build up the map of ZipEntries
	 * @return The map containing all the ZipEntries. The key being the name of the ZipEntry.
	 * @throws java.io.IOException
	 * @Deprecated
	 */
	protected Map buildZipEntryMap(ZipFile zf) throws java.io.IOException {
		return buildZipEntryMap(zf, 0);
	}

	/**
	 * Opens the ZipFile and builds up a map of all the entries. The key is the name of
	 * the entry and the value is the ZipEntry itself.
	 * @param zf The ZipFile for which to build up the map of ZipEntries
	 * @param number of directory prefixes to skip
	 * @return The map containing all the ZipEntries. The key being the name of the ZipEntry.
	 * @throws java.io.IOException
	 */
	protected Map buildZipEntryMap(ZipFile zf, int p) throws java.io.IOException {
		Map zipEntryMap = new HashMap();
		try {
			Enumeration entries = zf.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				InputStream is = null;
				try {
					is = zf.getInputStream(entry);
					processZipEntry("", entry, is, zipEntryMap, p);
				} finally {
					if (is != null) {
						is.close();
					}
				}
			}
		} finally {
			zf.close();
		}

		return zipEntryMap;
	}

	/**
	 * Will place ZipEntries for a given ZipEntry into the given Map. More ZipEntries will result
	 * if zipEntry is itself a ZipFile. All embedded ZipFiles will be processed with their names
	 * prefixed onto the names of their ZipEntries.
	 * @param prefix The prefix of the ZipEntry that should be added to the key. Typically used
	 * when processing embedded ZipFiles. The name of the embedded ZipFile would be the prefix of
	 * all the embedded ZipEntries.
	 * @param zipEntry The ZipEntry to place into the Map. If it is a ZipFile then all its ZipEntries
	 * will also be placed in the Map.
	 * @param is The InputStream of the corresponding ZipEntry.
	 * @param zipEntryMap The Map in which to place all the ZipEntries into. The key will
	 * be the name of the ZipEntry.
	 * @throws IOException
	 * @Deprecated
	 */
	protected void processZipEntry(String prefix, ZipEntry zipEntry, InputStream is, Map zipEntryMap) throws IOException {
		processZipEntry(prefix, zipEntry, is, zipEntryMap, 0);
	}


	/**
	 * Will place ZipEntries for a given ZipEntry into the given Map. More ZipEntries will result
	 * if zipEntry is itself a ZipFile. All embedded ZipFiles will be processed with their names
	 * prefixed onto the names of their ZipEntries.
	 * @param prefix The prefix of the ZipEntry that should be added to the key. Typically used
	 * when processing embedded ZipFiles. The name of the embedded ZipFile would be the prefix of
	 * all the embedded ZipEntries.
	 * @param zipEntry The ZipEntry to place into the Map. If it is a ZipFile then all its ZipEntries
	 * will also be placed in the Map.
	 * @param is The InputStream of the corresponding ZipEntry.
	 * @param zipEntryMap The Map in which to place all the ZipEntries into. The key will
	 * be the name of the ZipEntry.
	 * @param p number of directory prefixes to skip
	 * @throws IOException
	 */
	protected void processZipEntry(String prefix, ZipEntry zipEntry, InputStream is, Map zipEntryMap, int p) throws IOException {
		if (ignoreThisFile(prefix, zipEntry.getName())) {
			logger.log(Level.FINE, "ignoring file: " + zipEntry.getName());
		} else {
			String name = StringUtil.removeDirectoryPrefix(prefix + zipEntry.getName(), p);
			if ((name == null) || name.equals("")) {
				return;
			}

			logger.log(Level.FINEST, "processing ZipEntry: " + name);
			zipEntryMap.put(name, zipEntry);

			if (!zipEntry.isDirectory() && isZipFile(name)) {
				processEmbeddedZipFile(name + "!", is, zipEntryMap);
			}
		}
	}



	protected void processEmbeddedZipFile(String prefix, InputStream is, Map m) throws java.io.IOException {
		ZipInputStream zis = new ZipInputStream(is);

		ZipEntry entry = zis.getNextEntry();

		while (entry != null) {
			processZipEntry(prefix, entry, zis, m);
			zis.closeEntry();
			entry = zis.getNextEntry();
		}

	}

	/**
	 * Returns true if the filename has a valid zip extension.
	 * i.e. jar, war, ear, zip etc.
	 * @param filename The name of the file to check.
	 * @return true if it has a valid extension.
	 */
	public static boolean isZipFile(String filename) {
		boolean result;

		if (filename == null) {
			result = false;
		} else {
			String lowercaseName = filename.toLowerCase();
			if (lowercaseName.endsWith(".zip")) {
				result = true;
			} else if (lowercaseName.endsWith(".ear")) {
				result = true;
			} else if (lowercaseName.endsWith(".war")) {
				result = true;
			} else if (lowercaseName.endsWith(".rar")) {
				result = true;
			} else if (lowercaseName.endsWith(".jar")) {
				result = true;
			} else {
				result = false;
			}
		}

		return result;
	}

	/**
	 * Calculates all the differences between two zip files.
	 * It builds up the 2 maps of ZipEntries for the two files
	 * and then compares them.
	 * @param zf1 The first ZipFile to compare
	 * @param zf2 The second ZipFile to compare
	 * @return All the differences between the two files.
	 * @throws java.io.IOException
	 * @Deprecated
	 */
	protected Differences calculateDifferences(ZipFile zf1, ZipFile zf2) throws java.io.IOException {
		return calculateDifferences(zf1, zf2, 0, 0);
	}

	/**
	 * Calculates all the differences between two zip files.
	 * It builds up the 2 maps of ZipEntries for the two files
	 * and then compares them.
	 * @param zf1 The first ZipFile to compare
	 * @param zf2 The second ZipFile to compare
	 * @param p1 number of directory prefixes to skip in the 1st file
	 * @param p2 number of directory prefixes to skip in the 2nd file
	 * @return All the differences between the two files.
	 * @throws java.io.IOException
	 */
	protected Differences calculateDifferences(ZipFile zf1, ZipFile zf2, int p1, int p2) throws java.io.IOException {
		Map map1 = buildZipEntryMap(zf1, p1);
		Map map2 = buildZipEntryMap(zf2, p2);

		return calculateDifferences(map1, map2);
	}

	/**
	 * Given two Maps of ZipEntries it will generate a Differences of all the
	 * differences found between the two maps.
	 * @return All the differences found between the two maps
	 */
	protected Differences calculateDifferences(Map m1, Map m2) {
		Differences d = new Differences();

		Set names1 = m1.keySet();
		Set names2 = m2.keySet();

		Set allNames = new HashSet();
		allNames.addAll(names1);
		allNames.addAll(names2);

		Iterator iterAllNames = allNames.iterator();
		while (iterAllNames.hasNext()) {
			String name = (String) iterAllNames.next();
			if (ignoreThisFile("", name)) {
				// do nothing
			} else if (names1.contains(name) && (!names2.contains(name))) {
				d.fileRemoved(name, (ZipEntry) m1.get(name));
			} else if (names2.contains(name) && (!names1.contains(name))) {
				d.fileAdded(name, (ZipEntry) m2.get(name));
			} else if (names1.contains(name) && (names2.contains(name))) {
				ZipEntry entry1 = (ZipEntry) m1.get(name);
				ZipEntry entry2 = (ZipEntry) m2.get(name);
				if (!entriesMatch(entry1, entry2)) {
					d.fileChanged(name, entry1, entry2);
				}
			} else {
				throw new IllegalStateException("unexpected state");
			}
		}

		return d;
	}

	/**
	 * returns true if the two entries are equivalent in type, name, size, compressed size
	 * and time or CRC.
	 * @param entry1 The first ZipEntry to compare
	 * @param entry2 The second ZipEntry to compare
	 * @return true if the entries are equivalent.
	 */
	protected boolean entriesMatch(ZipEntry entry1, ZipEntry entry2) {
		boolean result;

		result = (entry1.isDirectory() == entry2.isDirectory()) && (entry1.getSize() == entry2.getSize()) && (entry1.getCompressedSize() == entry2.getCompressedSize());

		if (!isIgnoringTimestamps()) {
			result = result && (entry1.getTime() == entry2.getTime());
		}

		if (getCompareCRCValues()) {
			result = result && (entry1.getCrc() == entry2.getCrc());
		}
		return result;
	}

	public void setIgnoreTimestamps(boolean b) {
		ignoreTimestamps = b;
	}

	public boolean isIgnoringTimestamps() {
		return ignoreTimestamps;
	}

	public boolean ignoreCVSFiles() {
		return ignoreCVSFiles;
	}

	public void setIgnoreCVSFiles(boolean b) {
		ignoreCVSFiles = b;
	}

	/**
	 *
	 * @return all the differences found between the two zip files.
	 * @throws java.io.IOException
	 */
	public Differences getDifferences() throws java.io.IOException {
		Differences d = calculateDifferences(file1, file2, numberOfPrefixesToSkip1, numberOfPrefixesToSkip2);
		d.setFilename1(file1.getName());
		d.setFilename2(file2.getName());

		return d;
	}
}
