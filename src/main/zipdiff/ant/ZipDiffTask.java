/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.ant;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import zipdiff.DifferenceCalculator;
import zipdiff.Differences;
import zipdiff.output.Builder;
import zipdiff.output.BuilderFactory;

/**
 * Ant task for running zipdiff from a build.xml file
 *
 *
 * @author Sean C. Sullivan
 */
public class ZipDiffTask extends Task {
	private String filename1;

	private String filename2;

	private int numberOfOutputPrefixesToSkip;

	private int skipPrefixes1 = 0;

	private int SkipPrefixes2 = 0;

	private String destfile;

	private boolean ignoreTimestamps = false;

	private boolean ignoreCVSFiles = false;

	private boolean compareCRCValues = true;

	public void setFilename1(String name) {
		filename1 = name;
	}

	public void setFilename2(String name) {
		filename2 = name;
	}



	public int getNumberOfOutputPrefixesToSkip() {
		return numberOfOutputPrefixesToSkip;
	}

	public void setNumberOfOutputPrefixesToSkip(int numberOfOutputPrefixesToSkip) {
		this.numberOfOutputPrefixesToSkip = numberOfOutputPrefixesToSkip;
	}

	public int getSkipPrefixes1() {
		return skipPrefixes1;
	}

	public void setSkipPrefixes1(int numberOfPrefixesToSkip1) {
		this.skipPrefixes1 = numberOfPrefixesToSkip1;
	}

	public int getSkipPrefixes2() {
		return SkipPrefixes2;
	}

	public void setSkipPrefixes2(int numberOfPrefixesToSkip2) {
		this.SkipPrefixes2 = numberOfPrefixesToSkip2;
	}

	public void setIgnoreTimestamps(boolean b) {
		ignoreTimestamps = b;
	}

	public boolean getIgnoreTimestamps() {
		return ignoreTimestamps;
	}

	public void setIgnoreCVSFiles(boolean b) {
		ignoreCVSFiles = b;
	}

	public boolean getIgnoreCVSFiles() {
		return ignoreCVSFiles;
	}

	public void setCompareCRCValues(boolean b) {
		compareCRCValues = b;
	}

	public boolean getCompareCRCValues() {
		return compareCRCValues;
	}

	@Override
	public void execute() throws BuildException {
		validate();

		// this.log("Filename1=" + filename1, Project.MSG_DEBUG);
		// this.log("Filename2=" + filename2, Project.MSG_DEBUG);
		// this.log("destfile=" + getDestFile(), Project.MSG_DEBUG);

		Differences d = calculateDifferences();

		try {
			writeDestFile(d);
		} catch (java.io.IOException ex) {
			throw new BuildException(ex);
		}

	}

	/**
	 * writes the output file
	 *
	 * @param d set of Differences
	 * @throws IOException
	 */
	protected void writeDestFile(Differences d) throws IOException {
		String destfilename = getDestFile();
		Builder builder = BuilderFactory.create(destfilename);
		builder.build(destfilename, numberOfOutputPrefixesToSkip, d);
	}

	/**
	 * gets the name of the target file
	 *
	 * @return target file
	 */
	public String getDestFile() {
		return destfile;
	}

	/**
	 * sets the name of the target file
	 *
	 * @param name filename
	 */
	public void setDestFile(String name) {
		destfile = name;
	}

	/**
	 * calculates the differences
	 *
	 * @return set of Differences
	 * @throws BuildException in case of an input/output error
	 */
	protected Differences calculateDifferences() throws BuildException {
		DifferenceCalculator calculator;

		Differences d = null;

		try {
			calculator = new DifferenceCalculator(filename1, filename2);
			calculator.setNumberOfPrefixesToSkip1(skipPrefixes1);
			calculator.setNumberOfPrefixesToSkip2(SkipPrefixes2);
			calculator.setCompareCRCValues(getCompareCRCValues());
			calculator.setIgnoreTimestamps(getIgnoreTimestamps());
			calculator.setIgnoreCVSFiles(getIgnoreCVSFiles());

			// todo : calculator.setFilenamesToIgnore(patterns);

			d = calculator.getDifferences();
		} catch (IOException ex) {
			throw new BuildException(ex);
		}

		return d;
	}

	/**
	 * validates the parameters
	 *
	 * @throws BuildException in case of invalid parameters
	 */
	protected void validate() throws BuildException {
		if ((filename1 == null) || (filename1.length() < 1)) {
			throw new BuildException("filename1 is required");
		}

		if ((filename2 == null) || (filename2.length() < 1)) {
			throw new BuildException("filename2 is required");
		}

		String destinationfile = getDestFile();

		if ((destinationfile == null) || (destinationfile.length() < 1)) {
			throw new BuildException("destfile is required");
		}
	}

}
