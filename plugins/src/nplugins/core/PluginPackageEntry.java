/*
 * Copyright Ideaconsult Ltd. (C) 2005-2008 
 *
 * Contact: nina@acad.bg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package nplugins.core;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A class, containing information about className, its package and a toString() output
 * @author Nina Jeliazkova
 *
 */
public class PluginPackageEntry {
	protected String className;
	protected String packageName;
	protected JarFile jar;
 	
	public PluginPackageEntry(String className, String packageName, JarFile jar) throws Exception {
		super();
		this.className = className;
		this.packageName = packageName;
		this.jar = jar;
		/*
		this.objectTitle = objectTitle;
		if (objectTitle.equals("")) {
				Object o = Introspection.loadCreateObject(className);
				this.objectTitle = o.toString();
		}
		*/
	}

	public String getClassName() {
		return className;
	}

	
	public String getPackageName() {
		return packageName;
	}

	@Override
	public String toString() {
		return className;
	}

	public JarFile getJar() {
		return jar;
	}

}
