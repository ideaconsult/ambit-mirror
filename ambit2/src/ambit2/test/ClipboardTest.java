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

package ambit2.test;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;


public class ClipboardTest extends TestCase {
	// Takes a list of flavors and returns the HTML Unicode string flavor
	private static DataFlavor getHtmlFlavor(DataFlavor[] listOfFlavors) {
		for ( int i = 0; i < listOfFlavors.length; i++ ) {
			//String s = "text/html; class=java.lang.String; charset=Unicode";
			String s = "text/plain; class=java.io.InputStream; charset=UTF-8";
			try {
				System.out.println(listOfFlavors[i]);
				
				if ( listOfFlavors[i].equals(new DataFlavor(s))) {
					return listOfFlavors[i];
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void test() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor availableFlavors[] = clipboard.getAvailableDataFlavors();
		
		DataFlavor htmlFlavor = getHtmlFlavor(availableFlavors);
		if ( htmlFlavor != null ) {
			try {
				InputStream in = (InputStream)clipboard.getData( htmlFlavor );
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null)
					System.out.println(line);
				/*
				String s = clipboard.getData( htmlFlavor ).toString();
				System.out.println(s);
				*/	
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No HTML Unicode String Flavor found");
		}
	}
}


