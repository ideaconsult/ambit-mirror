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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.ListModel;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyStats;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.property.PropertyStatsNumeric;
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
	protected AmbitRows<PropertyStats> stats;
	protected PropertyStatsNumeric statsQuery = new PropertyStatsNumeric();
	protected PropertyStats propertyStats ;
	protected PresentationModel<PropertyStats> statsAdapter ;       

	
	protected PresentationModel<QueryFieldNumeric> createPresentationModel() {
		return new PresentationModel<QueryFieldNumeric>(new ValueHolder(null, true));

	}
	public JComponent buildPanel() {
		properties = new AmbitRows<Property>();
		stats = new AmbitRows<PropertyStats>();
		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,61dlu,3dlu,61dlu",
				"pref,pref,pref,pref");        
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
	     
	     panel.add(createStatsButton(), cc.xywh(1,4,1,1));
	     JComponent[] s = createStatsFields();
	     for (JComponent c:s)
	    	 c.setBackground(panel.getPanel().getBackground());

	     //panel.add(createStatsCheckBox(), cc.xywh(1,3,1,1));
	     panel.addSeparator("Average", cc.xywh(3,3,1,1));
	     panel.addSeparator("Min", cc.xywh(5,3,1,1));
	     panel.addSeparator("Max", cc.xywh(7,3,1,1));
	     panel.add(s[0],cc.xywh(3,4,1,1));
	     panel.add(s[1],cc.xywh(5,4,1,1));
	     panel.add(s[2],cc.xywh(7,4,1,1));
	     return panel.getPanel();
	}

	
	protected JComponent[] createStatsFields() {
		propertyStats = new PropertyStats();
		statsAdapter = new PresentationModel<PropertyStats>(propertyStats);       
		
		return new JComponent[] {
				BasicComponentFactory.createFormattedTextField(statsAdapter.getModel("avg"),NumberFormat.getNumberInstance()),
				BasicComponentFactory.createFormattedTextField(statsAdapter.getModel("min"),NumberFormat.getNumberInstance()),
				BasicComponentFactory.createFormattedTextField(statsAdapter.getModel("max"),NumberFormat.getNumberInstance())};
	}
	protected JButton createStatsButton() {
    
		
		JButton b = new JButton(new AbstractAction("Retrieve statistics") {
			public void actionPerformed(ActionEvent e) {
				try {
					stats.setQuery(statsQuery);

					while (stats.next()) {
						statsAdapter.setBean(stats.getObject());
						System.out.println(statsQuery.getFieldname() + " " +propertyStats);
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		});
		return b;
	}

	@Override
	protected JComponent createFieldnameComponent() {
		
		ListModel fieldnames = new RowsModel<Property>(properties) {
			@Override
			public int getSize() {
				return super.getSize()+1;
			}
			@Override
			public Property getElementAt(int index) {
				if (index == 0) return null;
				else
					return super.getElementAt(index-1);
			}
		};
		SelectionInList<Property> p = new SelectionInList<Property>(fieldnames, presentationModel.getModel("fieldname"));
		p.addPropertyChangeListener("value",new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				statsQuery.setFieldname((Property)evt.getNewValue());
				
			}
		});		
		JComboBox box = BasicComponentFactory.createComboBox(p);
		AutoCompleteDecorator.decorate(box);
		return box;

				

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
		JComboBox box = BasicComponentFactory.createComboBox(selectionInList);
		AutoCompleteDecorator.decorate(box);
		return box;
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
		stats.setConnection(connection);
	}
}


