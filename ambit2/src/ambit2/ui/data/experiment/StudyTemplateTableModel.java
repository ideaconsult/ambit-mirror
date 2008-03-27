/* StudyTemplateModel.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-6 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
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

package ambit2.ui.data.experiment;

import ambit2.data.experiment.StudyTemplate;
import ambit2.data.experiment.TemplateField;
import ambit2.ui.data.AbstractPropertyTableModel;

/**
 * table model for {@link ambit2.data.experiment.StudyTemplate}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-6
 */
public class StudyTemplateTableModel extends AbstractPropertyTableModel {
	protected String[] columnNames = {"Name","Units","Type","Mode"};

	public StudyTemplateTableModel(StudyTemplate study) {
		super(study);
	}
	public int getColumnCount() {
		return 4;
	}
	public int getRowCount() {
		if (object == null) return 0; else
		return ((StudyTemplate)object).size();
	}
	public Object getValueAt(int row, int col) {
		TemplateField f = ((StudyTemplate)object).getField(row);
		switch (col) {
		case 0: { return f;}
		case 1: { return f.getUnits();}
		case 2: { return f.getType();}
		case 3: { return f.getMode();}
		default: return "";
		}
	}
	/*
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		TemplateField f = ((StudyTemplate)object).getField(rowIndex);
		switch (columnIndex) {
		case 0: { f.setName(aValue.toString());}
		case 1: { f.setUnits(aValue.toString());}
		case 2: { f.setType(aValue.toString());}
		
		default: return;
		}
	}
	*/
	public String getColumnName(int arg0) {
		return columnNames[arg0];
	}
	public StudyTemplate getStudy() {
		return ((StudyTemplate)object);
	}
	public void setStudy(StudyTemplate study) {
	    setObject(study);
	}
}