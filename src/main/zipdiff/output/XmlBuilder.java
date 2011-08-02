/*
 * 
 * 
 */
package zipdiff.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import zipdiff.Differences;

/**
 * 
 * Generates xml output for a Differences instance
 * 
 * @author Sean C. Sullivan
 * 
 */
public class XmlBuilder extends AbstractBuilder {

	public void build(OutputStream out, Differences d) {
		PrintWriter pw = new PrintWriter(out);

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.print("<zipdiff filename1=\"");

		String filename1 = d.getFilename1();

		if (filename1 == null) {
			filename1 = "filename1.zip";
		}
		pw.print(filename1);
		pw.print("\" filename2=\"");

		String filename2 = d.getFilename2();

		if (filename2 == null) {
			filename2 = "filename2.zip";
		}
		pw.print(filename2);
		pw.println("\">");

		pw.println("<differences>");
		writeAdded(pw, d.getAdded().keySet());
		writeRemoved(pw, d.getRemoved().keySet());
		writeChanged(pw, d.getChanged().keySet());
		pw.println("</differences>");
		pw.println("</zipdiff>");

		pw.flush();
	}

	protected void writeAdded(PrintWriter pw, Set added) {
		Iterator iter = added.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			pw.print("<added>");
			pw.print(key);
			pw.println("</added>");
		}

	}

	protected void writeRemoved(PrintWriter pw, Set removed) {
		Iterator iter = removed.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			pw.print("<removed>");
			pw.print(key);
			pw.println("</removed>");
		}
	}

	protected void writeChanged(PrintWriter pw, Set changed) {
		Iterator iter = changed.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			pw.print("<changed>");
			pw.print(key);
			pw.println("</changed>");
		}
	}

}
