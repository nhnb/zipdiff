/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.util;

/**
 * String manipulation methods
 *
 * @author Hendrik Brummermann
 */
public class StringUtil {

	/**
	 * removes the specified number of prefix components from a path
	 * @param name file name with path
	 * @param number number of directory prefixed to remove
	 * @return path without prefix
	 */
	public static String removeDirectoryPrefix(String name, int number) {
		int pos = 0;
		for (int i = 0; i < number; i++) {
			pos = name.indexOf("/", pos) + 1;
		}
		if (pos < 0) {
			return null;
		}
		return name.substring(pos);
	}
}
