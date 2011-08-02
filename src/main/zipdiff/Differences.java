/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;

/**
 * Used to keep track of difference between 2 zip files.
 *
 * @author Sean C. Sullivan
 */
public class Differences {
	private final Map added = new TreeMap();

	private final Map removed = new TreeMap();

	private final Map changed = new TreeMap();

	private final Map ignored = new TreeMap();

	private String filename1;

	private String filename2;

	public Differences() {
		// todo
	}

	public void setFilename1(String filename) {
		filename1 = filename;
	}

	public void setFilename2(String filename) {
		filename2 = filename;
	}

	public String getFilename1() {
		return filename1;
	}

	public String getFilename2() {
		return filename2;
	}

	public void fileAdded(String fqn, ZipEntry ze) {
		added.put(fqn, ze);
	}

	public void fileRemoved(String fqn, ZipEntry ze) {
		removed.put(fqn, ze);
	}

	public void fileIgnored(String fqn, ZipEntry ze) {
		ignored.put(fqn, ze);
	}

	public void fileChanged(String fqn, ZipEntry z1, ZipEntry z2) {
		ZipEntry[] entries = new ZipEntry[2];
		entries[0] = z1;
		entries[1] = z2;
		changed.put(fqn, entries);
	}

	public Map getAdded() {
		return added;
	}

	public Map getRemoved() {
		return removed;
	}

	public Map getChanged() {
		return changed;
	}

	public Map getIgnored() {
		return ignored;
	}

	public boolean hasDifferences() {
		return ((getChanged().size() > 0) || (getAdded().size() > 0) || (getRemoved().size() > 0));
	}

	@Override
    public String toString() {
		StringBuffer sb = new StringBuffer();

		if (added.size() == 1) {
			sb.append("1 file was");
		} else {
			sb.append(added.size() + " files were");
		}
		sb.append(" added to " + this.getFilename2() + "\n");

		Iterator iter = added.keySet().iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			sb.append("\t[added] " + name + "\n");
		}

		if (removed.size() == 1) {
			sb.append("1 file was");
		} else {
			sb.append(removed.size() + " files were");
		}
		sb.append(" removed from " + this.getFilename2() + "\n");

		iter = removed.keySet().iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			sb.append("\t[removed] " + name + "\n");
		}

		if (changed.size() == 1) {
			sb.append("1 file changed\n");
		} else {
			sb.append(changed.size() + " files changed\n");
		}

		Set keys = getChanged().keySet();
		iter = keys.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			ZipEntry[] entries = (ZipEntry[]) getChanged().get(name);
			sb.append("\t[changed] " + name + " ");
			sb.append(" ( size " + entries[0].getSize());
			sb.append(" : " + entries[1].getSize());
			sb.append(" )\n");
		}
		int differenceCount = added.size() + changed.size() + removed.size();

		sb.append("Total differences: " + differenceCount);
		return sb.toString();
	}

}
