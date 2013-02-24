ZipDiff compares two .zip (.jar, .war, .jar) files and creates a list of differences. Plain text, .xml, .html and even a .zip file are supported as output formats.

ZipDiff can be executed as command line tool or ant task.


Command line arguments
----------------------

java -jar zipdiff.jar -file1 foo.zip -file2 bar.zip [ options]

Valid options are:

--comparecrcvalues     compares the crc values instead of the file content
--comparetimestamps    compares timestamps instead of file content
--ignorecvsfiles       ignores differences in CVS folders
--outputfile           name of the output file
--skipoutputprefixes n number of path segment to skip in the output file
--skipprefixes1 n      number of path segment to skip in the first file
--skipprefixes2 n      number of path segment to skip in the second file
--exitwitherrorondifference   use an error code other than 0, if differences have been detected
--verbose              print detail messages



This version can be found at https://github.com/nhnb/zipdiff


The original zipdiff project was developed by Sean C. Sullivan and James Stewart at http://zipdiff.sourceforge.net/

License:  see LICENSE.txt
