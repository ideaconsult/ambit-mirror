/* AmbitRowsTest.java
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
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.idea.modbcum.i.IDBProcessor;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.results.DictionaryRows;
import ambit2.db.search.DictionaryObjectQuery;
import ambit2.db.search.DictionaryQuery;
import ambit2.db.search.DictionarySubjectQuery;
import ambit2.dbui.dictionary.DictionaryQueryPanel;
import ambit2.ui.EditorPreferences;


public class DictionaryRowsTest extends QueryTest<DictionaryQuery>{

	@Override
	protected DictionaryQuery createQuery() throws Exception {
		DictionaryObjectQuery q = new DictionaryObjectQuery();
		q.setValue("Endpoints");
		return q;
	}
	@Override
	public void testSelect() throws Exception {
		DictionaryRows rows = new DictionaryRows();
		Connection c = datasource.getConnection();
		DictionaryQueryPanel panel = (DictionaryQueryPanel) EditorPreferences.getEditor(query);
		panel.setConnection(c);
		panel.setObject(query);
		JOptionPane.showMessageDialog(null,panel.getJComponent());
		rows.close();
		Iterator<Property> i = panel.getProfile().getProperties(true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		/*
		JOptionPane.showMessageDialog(null,new SelectFieldsPanel(
				new ProfileListModel(panel.getProfile()),null));
				*/
	}
	/*
    public void xtestCachedRecordSet() throws Exception {
        
        Connection c = datasource.getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT idstructure,uncompress(structure),format FROM structure where idstructure>?",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1,100);
        //ps.setFetchSize(10);
        ps.setMaxRows(100);
        ResultSet rs = ps.executeQuery();
        
        CachedRowSet join = CachedRowSetFactory.getCachedRowSet();
        //join.setFetchSize(10);
        join.setPageSize(10);
        //join.setMaxRows(30);
        //join.setCommand("SELECT idstructure,uncompress(structure),format FROM structure");
        //join.setCommand("SELECT s.idstructure,selected,uncompress(structure),format FROM query_results as q join structure as s using (idstructure)");
        //join.setCommand("SELECT s.idstructure,selected,format FROM query_results as q join structure as s using (idstructure)");
        join.populate(rs,1);

        int [] keys = {1,2};
        //join.execute(c);
        //c.close();

        int records = 0;
        int pages = 0;

        assertTrue(join.size() > 0);
        join.nextPage();
        join.first();
        final WorkflowContext wc = new WorkflowContext();
        IWorkflowContextFactory f = new IWorkflowContextFactory() {
            public WorkflowContext getWorkflowContext() {
                return wc;
            }
        };
        AmbitWorkflowContextPanel panel = new AmbitWorkflowContextPanel(wc);
        Vector<String> p = new Vector<String>();
        p.add(DBWorkflowContext.STOREDQUERY);
        p.add(DBWorkflowContext.ERROR);
        panel.setProperties(p);        
        wc.put(WorkflowContextEvent.WF_ANIMATE, new Boolean(true));
        wc.put(DBWorkflowContext.STOREDQUERY, join);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JOptionPane.showMessageDialog(null,panel);


        try {
            join.acceptChanges(datasource.getConnection());
        } catch (SyncProviderException spe) {
        	SyncResolver resolver = spe.getSyncResolver();
        	Object crsValue; // value in crs
        	Object resolverValue; // value in the SyncResolver object
        	Object resolvedValue; // value to be persisted
        	while (resolver.nextConflict()) {
	        	if (resolver.getStatus() == SyncResolver.UPDATE_ROW_CONFLICT) {
	        	int row = resolver.getRow();
	        	join.absolute(row);
	        	int colCount = join.getMetaData().getColumnCount();
	        	for (int j = 1; j <= colCount; j++) {
		        	if (resolver.getConflictValue(j) != null) {
			        	crsValue = join.getObject(j);
			        	resolverValue = resolver.getConflictValue(j);
			        	 // compare crsValue and resolverValue to determine the
			//        	 value to be persisted
			        	if ("uncompress(structure)".equals(join.getMetaData().getColumnName(j))) continue;
			        	
			        	System.out.println(join.getMetaData().getColumnName(j)+ crsValue);
			        	resolvedValue = crsValue;
			        	resolver.setResolvedValue(j, resolvedValue);
			        }
	        	}
	        	}
        	}
        }

        join.close();
        c.close();
        //System.out.println(pages+"\t"+records);

    }
    */
	@Override
	protected void verify(DictionaryQuery query, ResultSet rs) throws Exception {
		// TODO Auto-generated method stub
		
	}
	protected void demo() {
		 
		DictionarySubjectQuery query = new DictionarySubjectQuery();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
			initDatasource();
			Connection c = datasource.getConnection();
			IAmbitEditor editor = EditorPreferences.getEditor(query);
			if (editor instanceof IDBProcessor) {
				((IDBProcessor)editor).setConnection(c);
				((IDBProcessor)editor).open();
			}
			JOptionPane.showMessageDialog(null,editor.getJComponent());
			c.close();
		} catch (Exception x) {
			x.printStackTrace();
		}		
	}
	public static void main(String args[]) {
		new DictionaryRowsTest().demo();
	}
}
