/*
 *
 *
 */
package zipdiff.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import zipdiff.Differences;

/**
 *
 * @author Sean C. Sullivan
 *
 *
 *
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
