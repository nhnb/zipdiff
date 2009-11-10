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
        OutputStream os = null;
        if ((filename == null) || filename.equals("-")) {
            os = System.out;
        } else {
            os = new FileOutputStream(filename);
        }
        build(os, d);
        os.flush();
    }
    
    public abstract void build(OutputStream out, Differences d);
}
