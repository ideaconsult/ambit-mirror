/*
Copyright (C) 2005-2008  

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

package nplugins.test;

import java.io.File;

import nplugins.core.Introspection;
import nplugins.core.PluginClassPath;
import junit.framework.TestCase;

public class PluginClassPathtest extends TestCase {
	public void test() throws Exception {
		PluginClassPath path = new PluginClassPath("dist,ext");
		assertEquals(2,path.size());
		//assertEquals(".",path.get(0));
		assertEquals("dist",path.get(0));
		assertEquals("ext",path.get(1));
	}
	
	public void testAdd() throws Exception {
		PluginClassPath path = new PluginClassPath(".,dist,ext");
		path.add("test");
		assertEquals(4,path.size());
		assertEquals("test",path.get(3));
		
		PluginClassPath path1 = new PluginClassPath(".,dist,ext");
		System.out.println(path1);
		assertEquals(3,path1.size());
	}
	
	public void testEnumerateJars() throws Exception {
		System.out.print("Plugins classpath:\t");
		Introspection.setPref_key("test/nplugins");
		PluginClassPath path = Introspection.getDefaultDirectories();
		System.out.println(path);
		
		Introspection.setLoader(this.getClass().getClassLoader());
		System.out.println(Introspection.getLoader());
		
		for (int i=0; i < path.size(); i++) {
			File file = new File(path.get(i));
			if (file.exists() && file.isDirectory()) {
				File[] files = Introspection.enumerateJars(file);
				if (files != null)
				for (int j=0; j < files.length; j++)
					System.out.println(files[j]);
			}
		}
	}
	public void testStorage() throws Exception {
	    PluginClassPath path = new PluginClassPath();
	    path.setPref_key("test/nplugins");
	    path.add("test");
	    
	    PluginClassPath path1 = new PluginClassPath();
        path1.setPref_key("test/nplugins");
        assertEquals(path.size(),path1.size());
        for (int i=0; i < path.size();i++)
            assertTrue(path1.contains(path.get(i)));
	    
	}
}


