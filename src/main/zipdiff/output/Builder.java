/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.OutputStream;

import zipdiff.Differences;

/**
 * Builder pattern: <a href="http://wiki.cs.uiuc.edu/patternStories/BuilderPattern">
 *     http://wiki.cs.uiuc.edu/patternStories/BuilderPattern</a>
 *
 * @author Sean C. Sullivan
 */
public interface Builder {
	public void build(OutputStream out, Differences d);

	public void build(String filename, int numberOfOutputPrefixesToSkip, Differences d) throws java.io.IOException;
}
