/*
 * 
 * 
 */
package zipdiff.output;

import zipdiff.Differences;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Set;
import java.util.Iterator;

/**
 * 
 * Generates html output for a Differences instance
 * 
 * @author Sean C. Sullivan
 *
 * 
 */
public class HtmlBuilder extends AbstractBuilder {

    public void build(OutputStream out, Differences d) {
        PrintWriter pw = new PrintWriter(out);

        pw.println("<html>");
        pw.println("<META http-equiv=\"Content-Type\" content=\"text/html\">");
        pw.println("<head>");
        pw.println("<title>File differences</title>");
        pw.println("</head>");

        pw.println("<body text=\"#000000\" vlink=\"#000000\" alink=\"#000000\" link=\"#000000\">");
        
        pw.println(getStyleTag());
        pw.print("<p>First file: ");
        String filename1 = d.getFilename1();

        if (filename1 == null) {
            filename1 = "filename1.zip";
        }
        pw.print(filename1);
        pw.println("<br>");

        pw.print("Second file: ");

        String filename2 = d.getFilename2();

        if (filename2 == null) {
            filename2 = "filename2.zip";
        }
        pw.print(filename2);
        pw.println("</p>");

        writeAdded(pw, d.getAdded().keySet());
        writeRemoved(pw, d.getRemoved().keySet());
        writeChanged(pw, d.getChanged().keySet());
        pw.println("<hr>");
        pw.println("<p>");
        pw.println("Generated at " + new java.util.Date());
        pw.println("</p>");
        pw.println("</body>");

        pw.println("</html>");

        pw.flush();

    }

    protected void writeAdded(PrintWriter pw, Set added) {
    	writeDiffSet(pw, "Added", added);
    }

    protected void writeRemoved(PrintWriter pw, Set removed) {
    	writeDiffSet(pw, "Removed", removed);
    }

    protected void writeChanged(PrintWriter pw, Set changed) {
    	writeDiffSet(pw, "Changed", changed);
    }

    protected void writeDiffSet(PrintWriter pw, String name, Set s) {
    	pw.println("<TABLE CELLSPACING=\"1\" CELLPADDING=\"3\" WIDTH=\"100%\" BORDER=\"0\">");
    	pw.println("<tr>");
        pw.println("<td class=\"diffs\" colspan=\"2\">" + name  + " (" + s.size() + " entries)</td>");
        pw.println("</tr>");
        pw.println("<tr>");
        pw.println("<td width=\"20\">");
        pw.println("</td>");
        pw.println("<td>");
        if (s.size() > 0) {
            pw.println("<ul>");
            Iterator iter = s.iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                pw.print("<li>");
                pw.print(key);
                pw.println("</li>");
            }
            pw.println("</ul>");
        }
        pw.println("</td>");
        pw.println("</tr>");
        pw.println("</table>");

    }

    protected String getStyleTag()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<style type=\"text/css\">");
        sb.append(" body, p { ");
        sb.append(" font-family: verdana,arial,helvetica; ");
        sb.append(" font-size: 80%; ");
        sb.append(" color:#000000; ");
        sb.append(" } \n");
        sb.append(" 	  .diffs { \n");
        sb.append("         font-family: verdana,arial,helvetica; \n");
        sb.append("         font-size: 80%; \n");
        sb.append(" font-weight: bold; \n");
        sb.append(" text-align:left; \n");
        sb.append(" background:#a6caf0; \n");
	  	sb.append(" } \n"); 
        sb.append(" tr, td { \n");
        sb.append(" font-family: verdana,arial,helvetica; \n");
        sb.append(" font-size: 80%; \n");
        sb.append(" background:#eeeee0; \n");
        sb.append(" } \n");  
        sb.append(" </style>\n");
        
        return sb.toString();
    }

}
