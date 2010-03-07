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

package ambit.test.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.tools.LoggingTool;

public class IteratingMDLReaderTest extends TestCase {
	static LoggingTool logger = new LoggingTool(IteratingMDLReaderTest.class);
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		logger.configureLog4j();
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(IteratingMDLReaderTest.class);
	}
	/**
	 *  In Java, a line is considered terminated by any one of the following: a line feed \n, a carriage return \r, or a carriage return followed immediately by a line feed. 
	 *
	 */
	
	public void test() {
		try {
			String lineSeparator = (String) java.security.AccessController.doPrivileged(
					  new sun.security.action.GetPropertyAction("line.separator"));

			IteratingMDLReader reader = new IteratingMDLReader(
				new FileInputStream(
				new File("E:/nina/LRI_Ambit/Petra-skin sens/Petra_-_Skin_Sens.sdf.fixed.sdf")
				//new File("E:/nina/LRI_Ambit/Petra-skin sens/110200.sdf")
						
				),
				DefaultChemObjectBuilder.getInstance());
			int r = 0;
			Object o;
			while (reader.hasNext()) {
				o = reader.next();
				r++;
			}
			System.out.println(r);
			assertTrue(r>2);
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	public static void convertFile(String string) {
		File file_txt = new File(string);
		     File file_tmp = new File(string + ".fixed.sdf");
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
	public void testConvert() {
		//convertFile("E:/nina/LRI_Ambit/Petra-skin sens/Petra_-_Skin_Sens.sdf");
	}
}


