/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.core.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openscience.cdk.tools.LoggingTool;

public class SDFBrokenLineTerminationConverter  {
	static LoggingTool logger = new LoggingTool(SDFBrokenLineTerminationConverter.class);
	/**
	 *  In Java, a line is considered terminated by any one of the following: a line feed \n, a carriage return \r, or a carriage return followed immediately by a line feed. 
	 *
	 */
	
	public static void convertFile(String fileName) {
		File file_txt = new File(fileName);
		     File file_tmp = new File(fileName + ".fixed.sdf");
		      try {
		        BufferedReader bufferedreader = new BufferedReader(new FileReader(file_txt));

		        BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(file_tmp));
		        String line;
		        while ((line = bufferedreader.readLine()) != null) {
		          int p = line.indexOf('\r');	
		          if (p > 0) {
		        	  bufferedwriter.write(line.substring(0,p-1));
		        	  bufferedwriter.write(line.substring(p+1,line.length()));
		          } else 
		        	  bufferedwriter.write(line);
		          bufferedwriter.newLine();
		        }
		        bufferedreader.close();
		        bufferedwriter.close();
		        /*
		        if (file_txt.delete()) {
		          file_tmp.renameTo(file_txt);
		        }
		        */
		      } catch (FileNotFoundException filenotfoundexception) {
		      } catch (IOException e) {
		      }
	}

}


