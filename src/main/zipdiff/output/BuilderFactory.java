/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

/**
 * creates builders based on the filename extension.
 *
 * @author Hendrik Brummermann, HIS GmbH
 */
public class BuilderFactory {

	/**
	 * creates a builder based on the name of the output file
	 *
	 * @param filename name of output file
	 * @return Builder
	 */
	public static Builder create(String filename) {
		Builder builder = null;

		if ((filename == null) || filename.equals("-")) {
			builder = new TextBuilder();

		} else if (filename.endsWith(".html")) {
			builder = new HtmlBuilder();

		} else if (filename.endsWith(".txt")) {
			builder = new TextBuilder();

		} else if (filename.endsWith(".xml")) {
			builder = new XmlBuilder();

		} else if (filename.endsWith(".zip")) {
			builder = new ZipBuilder();

		} else {
			System.err.println("Unknown extension, using text output");
			builder = new TextBuilder();
		}
		return builder;
	}
}
