/* QueryStoredResultsEditor.java
 * Author: nina
 * Date: Apr 10, 2009
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

package ambit2.dbui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.update.storedquery.SearchStoredQueries;

import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QueryStoredResultsEditor extends QueryEditor<IStoredQuery, Boolean, EQCondition,IStructureRecord,QueryStoredResults>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2851443117151701351L;
	protected AmbitRows<IStoredQuery> queries;
	protected SearchStoredQueries searchStoredQuery;
	public QueryStoredResultsEditor() {
		super();
		
	}
	@Override
	public JComponent buildPanel() {
		queries = new AmbitRows<IStoredQuery>();
		searchStoredQuery = new SearchStoredQueries();
		searchStoredQuery.setCondition(
					StringCondition.getInstance(StringCondition.STRING_CONDITION.S_STARTS_WITH.getName()));		
		

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     panel.addSeparator("Query name [number of structures]", cc.xywh(1,1,5,1));
	        
	     JComponent c = createFieldnameComponent();
	     if (c!= null)  panel.add(c, cc.xywh(1,2,5,1));
	     return panel.getPanel();		
	}
	@Override
	protected JComponent createConditionComponent() {
		return null;
	}
	@Override
	protected JComponent createValueComponent() {

		return null;
	}
	protected JComponent createFieldnameComponent() {		
		RowsModel<IStoredQuery> rows = new RowsModel<IStoredQuery>(queries);

		ComboBoxAdapter<IStoredQuery> adapter = new ComboBoxAdapter<IStoredQuery>(rows,
					presentationModel.getModel("fieldname"));
		final JComboBox box = new JComboBox(adapter);

		box.setEditable(true);
		box.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				if ((KeyEvent.VK_ENTER == e.getKeyCode()) || (KeyEvent.VK_DOWN == e.getKeyCode())) 
				try {
					searchStoredQuery.setValue(box.getEditor().getItem().toString());
					queries.setQuery(searchStoredQuery);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		});

		return box;
	}		
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		queries.setConnection(connection);
	}
	public void open() throws DbAmbitException {
		try {
			searchStoredQuery.setValue(null);
			queries.setQuery(searchStoredQuery);
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}
}
