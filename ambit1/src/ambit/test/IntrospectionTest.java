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

package ambit.test;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class IntrospectionTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(IntrospectionTest.class);
	}
	public Class implementsInterface(String className,
			String interfaceName) throws Exception {
		try {
			Class clazz = Class.forName(className);
			int modifier = clazz.getModifiers();
			if (Modifier.isAbstract(modifier))
				return null;
			else if (Modifier.isInterface(modifier))
				return null;
			return implementsInterface(clazz, interfaceName);
		} catch (ClassNotFoundException x) {
			//
			return null;
		} catch (Exception x) {
			throw new toxTree.exceptions.IntrospectionException(x);
		}
	}
	public Class implementsInterface(Class clazz, String interfaceName) {
		Class[] interfaces = clazz.getInterfaces();

		for (int i = 0; i < interfaces.length; i++)
			if (interfaces[i].getName().equals(interfaceName))
				return clazz;

		// try base class
		Class base = clazz.getSuperclass();
		if (base == null)
			return null;
		else
			return implementsInterface(base, interfaceName);

	}	
	public void test() {
		try {
			Class c = implementsInterface("ambit.data.descriptors.PlanarityDescriptor", "org.openscience.cdk.qsar.IMolecularDescriptor");
			System.out.println(c);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}


