/* QueryEditor.java
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

import javax.swing.JComponent;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.search.AbstractQuery;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public abstract class QueryEditor<F,T,C extends IQueryCondition,ResultType,Q extends AbstractQuery<F,T,C,ResultType>> extends 
							AbstractDBProcessor<IQueryRetrieval<Property>, Q>  
							implements IAmbitEditor<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4697669908562921410L;
	protected JComponent mainPanel; 
	protected PresentationModel<Q> presentationModel;
	
	
	public QueryEditor() {
		presentationModel = 	createPresentationModel();	
		mainPanel = buildPanel();
	}	
	protected PresentationModel<Q> createPresentationModel() {
		return new PresentationModel<Q>(new ValueHolder(null, true));
	}
	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     panel.addSeparator("Field name", cc.xywh(1,1,1,1));
	     panel.addSeparator("Condition", cc.xywh(3,1,1,1));
	     panel.addSeparator("Value", cc.xywh(5,1,1,1));	     
	        
	     JComponent c = createFieldnameComponent();
	     if (c!= null)  panel.add(c, cc.xywh(1,2,1,1));
	     c = createConditionComponent();
	     if (c != null) panel.add(c, cc.xywh(3,2,1,1));
	     c = createValueComponent();
	     if (c != null) panel.add(c, cc.xywh(5,2,1,1));	  
	     return panel.getPanel();
	}	
	protected abstract JComponent createFieldnameComponent();
	protected abstract JComponent createConditionComponent();

	protected JComponent createValueComponent() {
		return BasicComponentFactory.createTextField(presentationModel.getModel("value"),true);
	}

	
	public JComponent getJComponent() {
		return mainPanel;
	}

	public Q getObject() {
		return presentationModel.getBean();
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
	
	}

	public void setObject(Q object) {
		if (presentationModel != null) {
			presentationModel.setBean(object);
		}
		
	}

	public Q process(IQueryRetrieval<Property> target) throws AmbitException {
		return null;
	}

	public boolean confirm() {
		return true;
	}
}
