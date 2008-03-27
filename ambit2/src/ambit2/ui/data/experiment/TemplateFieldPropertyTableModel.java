package ambit2.ui.data.experiment;

import ambit2.data.experiment.TemplateField;
import ambit2.ui.data.AbstractPropertyTableModel;

public class TemplateFieldPropertyTableModel extends AbstractPropertyTableModel {
	protected String[] names = {"Field name","Units","Numeric","Study result"};
	public TemplateFieldPropertyTableModel(TemplateField field) {
	      super(field);
	}	
	public int getRowCount() {
		return 4;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) return names[rowIndex];
		else 
			switch (rowIndex) {
			case 0: return ((TemplateField)object).getName();
			case 1: return ((TemplateField)object).getUnits();
			case 2: return ((TemplateField)object).isNumeric();
			case 3: return ((TemplateField)object).isResult();
			default: return "";
			}
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex > 0)
			switch (rowIndex) {
			case 0: ((TemplateField)object).setName(aValue.toString());
			case 1: ((TemplateField)object).setUnits(aValue.toString());
			case 2: ((TemplateField)object).setNumeric((Boolean)aValue);
			case 3: ((TemplateField)object).setResult((Boolean)aValue);
			default: ;
			}
	}
	

}
