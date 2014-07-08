/* QueryChemicalEditor.java
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

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryStructure;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;


public class QueryChemicalEditor  extends QueryEditor<ExactStructureSearchMode,String,StringCondition,IStructureRecord,QueryStructure>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3727097962149438857L;


	@Override
	protected JComponent createConditionComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<StringCondition>(
                		new StringCondition[] {
                			StringCondition.getInstance(StringCondition.C_EQ),
                			StringCondition.getInstance(StringCondition.C_LIKE),
                			StringCondition.getInstance(StringCondition.C_NOTLIKE),
                			StringCondition.getInstance(StringCondition.C_REGEXP),
                			StringCondition.getInstance(StringCondition.C_SOUNDSLIKE),
                		},
                		presentationModel.getModel("condition")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}
	@Override
	protected JComponent createFieldnameComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<String>(
                		new String[] {
                			ExactStructureSearchMode.inchi.name(),
                			ExactStructureSearchMode.smiles.name(),
                			ExactStructureSearchMode.formula.name(),
                		},
                		presentationModel.getModel("fieldname")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
