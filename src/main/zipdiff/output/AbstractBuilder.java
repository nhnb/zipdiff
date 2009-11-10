/*
 * 
 * 
 */
package zipdiff.output;

import zipdiff.Differences;
import java.io.*;

/**
 * 
 * @author Sean C. Sullivan
 *
 * 
 * 
 */
public abstract class AbstractBuilder
	implements Builder
{
	public void build(String filename, Differences d) throws IOException
	{
		FileOutputStream fos = null;
		
		fos = new FileOutputStream(filename);
		build(fos, d);
		fos.flush();
	}
	
	public abstract void build(OutputStream out, Differences d);
}
