/* TemplateRowsTest.java
 * Author: nina
 * Date: Feb 6, 2009
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
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import junit.framework.Assert;
import ambit2.db.readers.TemplateRows;
import ambit2.db.search.TemplateQuery;
import ambit2.dbui.dictionary.TemplatePropertyPanel;

public class TemplateRowsTest extends QueryTest<TemplateQuery>{

		@Override
		protected TemplateQuery createQuery() throws Exception {
			TemplateQuery q = new TemplateQuery();
			q.setValue("ECETOC skin irritation");
			return q;
		}
		@Override
		public void testSelect() throws Exception {
			TemplateRows rows = new TemplateRows();
			rows.setQuery(query);
			Connection c = datasource.getConnection();
			rows.open(c);
			Assert.assertTrue(rows.size()>0);
			TemplatePropertyPanel panel = new TemplatePropertyPanel();
			panel.setObject(rows);
			JOptionPane.showMessageDialog(null,panel);
			rows.close();
		};
		@Override
		protected void verify(TemplateQuery query, ResultSet rs) throws Exception {
		// TODO Auto-generated method stub
		
		}
}