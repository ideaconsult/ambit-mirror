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

package ambit2.workflow.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.ui.EditorPreferences;


public class EditorPreferencesTest {
	@Test
	public void test() throws Exception {
		boolean failed = false;
		for (int i=0; i <  EditorPreferences.default_values.length; i++) {
			try {
				Class.forName(EditorPreferences.default_values[i][0].toString());
			} catch (ClassNotFoundException x) {
				failed = true;
				System.err.println(EditorPreferences.default_values[i][0]);
			}
			try {
				Class.forName(EditorPreferences.default_values[i][1].toString());
			} catch (ClassNotFoundException x) {
				failed = true;
				System.err.println(EditorPreferences.default_values[i][1]);
			}			
		}
		Assert.assertFalse(failed);
	}
}
