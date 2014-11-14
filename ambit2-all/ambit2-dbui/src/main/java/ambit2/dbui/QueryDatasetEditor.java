/* QueryDatasetEditor.java
 * Author: nina
 * Date: Apr 7, 2009
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

import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListModel;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryDataset;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QueryDatasetEditor extends QueryEditor<String,SourceDataset,StringCondition,IStructureRecord,QueryDataset>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3741781796086220256L;
	protected AmbitRows<SourceDataset> datasets;
	
	public JComponent buildPanel() {
		datasets = new AmbitRows<SourceDataset>();
		FormLayout layout = new FormLayout(
	            "75dlu,3dlu,125dlu,3dlu,125dlu",
				"pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     panel.addSeparator("Dataset name", cc.xywh(1,1,5,1));	     
	        
	     JComponent c  = createConditionComponent();
	     if (c != null) panel.add(c, cc.xywh(1,2,1,1));
	     c = createValueComponent();
	     if (c != null) panel.add(c, cc.xywh(3,2,3,1));	  
	     return panel.getPanel();
	}		
	
	@Override
	protected JComponent createConditionComponent() {

		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<StringCondition>(
                		new StringCondition[] {
                			StringCondition.getInstance(StringCondition.C_EQ),
                			StringCondition.getInstance(StringCondition.C_NOTEQ),                			
                			StringCondition.getInstance(StringCondition.C_LIKE),
                			StringCondition.getInstance(StringCondition.C_NOTLIKE),
                			StringCondition.getInstance(StringCondition.C_REGEXP),
                			StringCondition.getInstance(StringCondition.C_SOUNDSLIKE),
                		},
                		presentationModel.getModel("condition")));
		//AutoCompleteDecorator.decorate(box);
		return box;
	}

	@Override
	protected JComponent createFieldnameComponent() {
		return new JLabel("Dataset name");
	
	}

	@Override
	protected JComponent createValueComponent() {
		ListModel fieldnames = new RowsModel<SourceDataset>(datasets);		
		JComboBox box = BasicComponentFactory.createComboBox(
				new SelectionInList<SourceDataset>(fieldnames, presentationModel.getModel("dataset")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}

	public void open() throws DbAmbitException {
		try {
			datasets.setQuery(new RetrieveDatasets());
		} catch (Exception x) {
			throw new DbAmbitException(this,x);
		}
		
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		datasets.setConnection(connection);
	}
}
