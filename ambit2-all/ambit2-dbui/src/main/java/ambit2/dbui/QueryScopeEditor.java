/* QueryScopeEditor.java
 * Author: nina
 * Date: May 3, 2009
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

import javax.swing.AbstractListModel;
import javax.swing.JComponent;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.search.structure.SCOPE;
import ambit2.db.search.structure.ScopeQuery;
import ambit2.ui.EditorPreferences;
import ambit2.ui.editors.ListEditor;

public class QueryScopeEditor extends AbstractDBProcessor<ScopeQuery, ScopeQuery> implements IAmbitEditor<ScopeQuery>{
	protected ListEditor editor;
	protected ScopeListModel model = new ScopeListModel();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1745393417109602866L;
	public QueryScopeEditor() {
		editor = new ListEditor(model) {
			@Override
			protected IAmbitEditor getEditor(Object object) {
				try {
					IAmbitEditor e =  EditorPreferences.getEditor(object);
					if (e instanceof IDBProcessor) {
						((IDBProcessor)e).setConnection(getConnection());
						((IDBProcessor)e).open();
					}					
					return e;
				} catch (Exception x) {
					return null;
				}
			}
		};
	}
	public JComponent getJComponent() {
		return editor.getJComponent();
	}
	public boolean confirm() {
		return editor.confirm();
	}
	public boolean isEditable() {
		return editor.isEditable();
	}
	public void setEditable(boolean editable) {
		editor.setEditable(editable);
		
	}
	public ScopeQuery getObject() {
		return model.getQuery();
	}
	public void setObject(ScopeQuery object) {
		model.setQuery(object);
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public ScopeQuery process(ScopeQuery target) throws AmbitException {
		return target;
	}
	
}

class ScopeListModel extends AbstractListModel {
	protected ScopeQuery query;
	public ScopeQuery getQuery() {
		return query;
	}
	public void setQuery(ScopeQuery query) {
		this.query = query;
		fireContentsChanged(this,0,getSize());
	}
	public ScopeListModel() {

	}
	public ScopeListModel(ScopeQuery query) {
		setQuery(query);
	}	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4348933973843558101L;
	public Object getElementAt(int index) {
		SCOPE scope = SCOPE.values()[index];
		if (query==null) return scope;
		query.setFieldname(scope);
		if (query.getValue()!=null) return query.getValue(); else return scope;
	}
	public int getSize() {
		return SCOPE.values().length;
	}
}