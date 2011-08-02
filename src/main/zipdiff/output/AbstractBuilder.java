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

	protected int numberOfOutputPrefixesToSkip;

	public void build(String filename, int numberOfOutputPrefixesToSkip, Differences d) throws IOException {
		this.numberOfOutputPrefixesToSkip = numberOfOutputPrefixesToSkip;
		OutputStream os = null;
		if ((filename == null) || filename.equals("-")) {
			os = System.out;
		} else {
			os = new FileOutputStream(filename);
		}
		build(os, d);
		os.flush();
	}

	public abstract void build(OutputStream out, Differences d);
}
