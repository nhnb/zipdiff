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
	public static String removeDirectoryPrefix(String name, int p) {
		int pos = 0;
		for (int i = 0; i < p; i++) {
			pos = name.indexOf("/", pos) + 1;
		}
		if (pos < 0) {
			return null;
		}
		return name.substring(pos);
	}
}
