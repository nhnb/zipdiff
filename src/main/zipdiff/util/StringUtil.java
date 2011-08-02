package zipdiff.util;

/**
 * String manipulation methods
 *
 * @author hendrik
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
