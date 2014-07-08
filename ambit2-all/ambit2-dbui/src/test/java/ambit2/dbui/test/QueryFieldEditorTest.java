/* QueryFieldEditorTest.java
 * Author: nina
 * Date: Apr 5, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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
 * 
 */

package ambit2.dbui.test;


import java.sql.Connection;

import javax.swing.JOptionPane;

import net.idea.modbcum.i.IDBProcessor;

import org.junit.After;
import org.junit.Before;

import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.search.structure.QueryField;
import ambit2.ui.EditorPreferences;

public class QueryFieldEditorTest  extends  RepositoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	protected void demo() {
		QueryField query = new QueryField();
		try {
			initDatasource();
			Connection c = datasource.getConnection();
			IAmbitEditor editor = EditorPreferences.getEditor(query);
			if (editor instanceof IDBProcessor) {
				((IDBProcessor)editor).setConnection(c);
				((IDBProcessor)editor).open();
			}
			JOptionPane.showMessageDialog(null,editor.getJComponent());
			System.out.println(query.toString());
			System.out.println(query.getSQL());
			System.out.println(query.getParameters());
			c.close();
		} catch (Exception x) {
			x.printStackTrace();
		}		
	}
	public static void main(String args[]) {
		new QueryFieldEditorTest().demo();
	}	
}
