/* QueryFieldNumericEditor.java
 * Author: nina
 * Date: Apr 6, 2009
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.text.NumberFormat;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.ListModel;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.property.RetrieveFieldNamesByType;
import ambit2.db.search.structure.QueryFieldNumeric;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QueryFieldNumericEditor  extends 
							QueryEditor<Property,Number, NumberCondition,IStructureRecord,QueryFieldNumeric>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3347607199840621061L;
	protected AmbitRows<Property> properties;
	
	protected PresentationModel<QueryFieldNumeric> createPresentationModel() {
		return new PresentationModel<QueryFieldNumeric>(new ValueHolder(null, true));

	}
	public JComponent buildPanel() {
		properties = new AmbitRows<Property>();
		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,61dlu,3dlu,61dlu",
				"pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     panel.addSeparator("Field name", cc.xywh(1,1,1,1));
	     panel.addSeparator("Condition", cc.xywh(3,1,1,1));
	     panel.addSeparator("Value", cc.xywh(5,1,3,1));	     
	        
	     panel.add(createFieldnameComponent(), cc.xywh(1,2,1,1));
	     panel.add(createConditionComponent(), cc.xywh(3,2,1,1));
	     panel.add(createValueComponent(), cc.xywh(5,2,1,1));
	     JComponent mv = createMaxValueComponent();
	     panel.add(mv, cc.xywh(7,2,1,1));
	     
	     return panel.getPanel();
	}

	@Override
	protected JComponent createFieldnameComponent() {
		
		ListModel fieldnames = new RowsModel<Property>(properties);		
		return BasicComponentFactory.createComboBox(
				new SelectionInList<Object>(fieldnames, presentationModel.getModel("fieldname")));
	}
	@Override
	protected JComponent createConditionComponent() {
		NumberCondition[] nc = new NumberCondition[NumberCondition.conditions.length];
		for (int i=0;i < nc.length;i++) 
			nc[i] = NumberCondition.getInstance(NumberCondition.conditions[i]);
	
		SelectionInList<NumberCondition> selectionInList = new SelectionInList<NumberCondition>(nc, presentationModel.getModel("condition"));
		selectionInList.addPropertyChangeListener("value",new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				presentationModel.getComponentModel("maxValue").setVisible(NumberCondition.between.equals(evt.getNewValue().toString()));
			}
		});
		return BasicComponentFactory.createComboBox(selectionInList);
	}
	@Override
	protected JComponent createValueComponent() {
		JFormattedTextField t = BasicComponentFactory.createFormattedTextField(
				presentationModel.getModel("value"),
				NumberFormat.getNumberInstance()
				);
		t.setToolTipText("Numeric value, or minimum value if \"between\" selected as a condition");
		return t;		
					
	}

	protected JComponent createMaxValueComponent() {
		JFormattedTextField t = BasicComponentFactory.createFormattedTextField(
				presentationModel.getComponentModel("maxValue"),
				NumberFormat.getNumberInstance()
				);
		t.setToolTipText("Ignored, or maximum value if \"between\" selected as a condition");
		return t;
					
	}	
	public QueryFieldNumeric process(IQueryRetrieval<Property> target)
			throws AmbitException {
		return getObject();
	}

	public void open() throws DbAmbitException {
		try {
			properties.setQuery(new RetrieveFieldNamesByType(false));
		} catch (Exception x) {
			throw new DbAmbitException(this,x);
		}
		
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		properties.setConnection(connection);
	}
}


