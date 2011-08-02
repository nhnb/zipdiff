/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import zipdiff.Differences;

/**
 * abstract base class for Builders.
 *
 * @author Sean C. Sullivan, Hendrik Brummermann
 */
public abstract class AbstractBuilder implements Builder {

	/** number of directory prefixes to skip in the output file */
	protected int numberOfOutputPrefixesToSkip;

	/**
	 * builds the output
	 *
	 * @param filename name of output file
	 * @param numberOfPrefixesToSkip number of directory prefixes to skip
	 * @param d differences
	 * @throws IOException in case of an input/output error
	 */
	public void build(String filename, int numberOfPrefixesToSkip, Differences d) throws IOException {
		this.numberOfOutputPrefixesToSkip = numberOfPrefixesToSkip;
		OutputStream os = null;
		if ((filename == null) || filename.equals("-")) {
			os = System.out;
		} else {
			os = new FileOutputStream(filename);
		}
		build(os, d);
		os.flush();
	}

	/**
	 * builds the output
	 *
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	public abstract void build(OutputStream out, Differences d);
}
