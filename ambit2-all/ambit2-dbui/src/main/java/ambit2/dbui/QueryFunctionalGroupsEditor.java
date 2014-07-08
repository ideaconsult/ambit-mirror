/* QueryFunctionalGroupsEditor.java
 * Author: nina
 * Date: May 1, 2009
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

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;

import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.PropertyTemplateStats;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryFunctionalGroups;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QueryFunctionalGroupsEditor  extends  QueryEditor<
								List<PropertyTemplateStats>,
								Boolean,StringCondition,
								IStructureRecord,
								QueryFunctionalGroups> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6825082093596853959L;
	protected JTable table;

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     panel.addSeparator("Functional groups", cc.xywh(1,1,5,1));
	        
	     JComponent c = createFieldnameComponent();
	     panel.add(c, cc.xywh(1,2,5,1));
	     return panel.getPanel();
	}		
	@Override
	protected JComponent createConditionComponent() {
		return null;
	}
	
	@Override
	protected JComponent createFieldnameComponent() {
		SelectionInList<PropertyTemplateStats> selectionInList = new SelectionInList<PropertyTemplateStats>(presentationModel.getModel("fieldname"));

        table = new JTable(new PropertyStatsTableAdapter(selectionInList,
        		new String[]{"Select", "Group", "# of structures"}));		
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
		return new JScrollPane(table);
	}

	public void open() throws DbAmbitException {
		
	} 
	@Override
	public void setObject(QueryFunctionalGroups object) {

		super.setObject(object);
	}
	
}

class PropertyStatsTableAdapter extends AbstractTableAdapter {
    public PropertyStatsTableAdapter(ListModel listModel, String[] columnNames) {
        super(listModel, columnNames);

    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	PropertyTemplateStats p = (PropertyTemplateStats) getRow(rowIndex);
        if (columnIndex == 0) {
            return p.getProperty().isEnabled();
        } else if (columnIndex == 1) {
            return  p.getProperty().getName();
        } else {
            return p.getCount();
        }
    }
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
    	if (columnIndex ==0) {
    		PropertyTemplateStats p = (PropertyTemplateStats) getRow(rowIndex);
    		p.getProperty().setEnabled((Boolean)value);
    	}
    	
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

    	return columnIndex==0;
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
    	if (columnIndex==0) return Boolean.class;
    	return super.getColumnClass(columnIndex);
    }
}

