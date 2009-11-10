/*
 * 
 * 
 */

package zipdiff.ant;

import zipdiff.DifferenceCalculator;
import zipdiff.Differences;
import zipdiff.output.*;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * 
 * 
 * @author Sean C. Sullivan
 *
 * 
 */
public class ZipDiffTask extends Task {
    private String filename1;
    private String filename2;
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

    public void setIgnoreCVSFiles(boolean b)
    {
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

    protected void writeDestFile(Differences d) throws java.io.IOException {
        String destfilename = getDestFile();

        Builder builder = null;

        if (destfilename.endsWith(".html")) {
            builder = new HtmlBuilder();
        } else if (destfilename.endsWith(".xml")) {
            builder = new XmlBuilder();
        } else {
            builder = new TextBuilder();
        }

        builder.build(destfilename, d);
    }

    public String getDestFile() {
        return destfile;
    }

    public void setDestFile(String name) {
        destfile = name;
    }

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
        } catch (java.io.IOException ex) {
            throw new BuildException(ex);
        }

        return d;
    }

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
