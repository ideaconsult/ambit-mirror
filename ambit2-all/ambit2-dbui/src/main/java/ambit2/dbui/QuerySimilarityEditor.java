/* QuerySimilarityEditor.java
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

import java.text.NumberFormat;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.openscience.cdk.interfaces.IAtomContainerSet;

import ambit2.base.data.ClassHolder;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityStructure;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QuerySimilarityEditor extends QueryEditor<ClassHolder, IAtomContainerSet,NumberCondition,IStructureRecord,
															QuerySimilarityStructure>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7423741371866787902L;
	/* UNCOMMENT TO USE JCHEMPAIN
	protected StructureDiagramEditor editor;
	*/
	public QuerySimilarityEditor() {
		super();
	}

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,40dlu,3dlu,40dlu,2dlu,40dlu",
				"pref,3dlu,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   
	      
	     JComponent c = createFieldnameComponent();
	     panel.add(new JLabel("<html><b>Similarity</b></html>"), cc.xywh(1,3,1,1));
	     if (c!= null)  panel.add(c, cc.xywh(3,3,1,1));
	     c = createConditionComponent();
	     panel.add(new JLabel("Threshold"), cc.xywh(5,3,1,1));
	     if (c != null) panel.add(c, cc.xywh(7,3,1,1));
	     c = createThresholdComponent();
	     if (c != null) panel.add(c, cc.xywh(9,3,1,1));

	     c = createStructureComponent();
	     panel.add(c, cc.xywh(1,1,9,1));	 
  
	     return panel.getPanel();
	}			
	protected JComponent createStructureComponent() {
		/* UNCOMMENT TO USE JCHEMPAINT
		if (editor == null)
			editor = new StructureDiagramEditor();
		return editor.getJComponent();
		*/
		return null;
	}
	@Override
	public boolean confirm() {
		/* UNCOMMENT TO USE JCHEMPAINT
		getObject().setValue(editor.getObject());
		*/
		return super.confirm();
	}
	@Override
	protected JComponent createConditionComponent() {
		NumberCondition[] nc = new NumberCondition[NumberCondition.conditions.length-1];
		for (int i=0;i < nc.length;i++) {
			nc[i] = NumberCondition.getInstance(NumberCondition.conditions[i]);
		}
		SelectionInList<NumberCondition> selectionInList = new SelectionInList<NumberCondition>(nc, presentationModel.getModel("condition"));
		JComboBox box = BasicComponentFactory.createComboBox(selectionInList);
		AutoCompleteDecorator.decorate(box);
		return box;
	}

	@Override
	protected JComponent createFieldnameComponent() {
		SelectionInList<ClassHolder> selectionInList = new SelectionInList<ClassHolder>(
				QuerySimilarityStructure.methods, presentationModel.getModel("fieldname"));
		JComboBox box = BasicComponentFactory.createComboBox(selectionInList);
		AutoCompleteDecorator.decorate(box);
		return box;
	}
	protected JComponent createThresholdComponent() {
		/*
		ValueModel levelModel = new PropertyAdapter(settings, "level", true);
		SpinnerNumberModel spinnerModel =
		SpinnerAdapterFactory.create(presentationModel.getModel("threshold"), 0.75, 0, 1);
		JSpinner levelSpinner = new JSpinner(spinnerModel);
		*/
		return BasicComponentFactory.createFormattedTextField(
				presentationModel.getModel("threshold"),
				NumberFormat.getNumberInstance()
				);
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
