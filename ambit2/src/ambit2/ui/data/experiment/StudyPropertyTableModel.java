package ambit2.ui.data.experiment;

import ambit2.data.experiment.Study;
import ambit2.data.experiment.StudyTemplate;
import ambit2.ui.data.AbstractPropertyTableModel;

public class StudyPropertyTableModel extends AbstractPropertyTableModel {
	protected String[] columns = {"Conditions","Results","Template"};
	public StudyPropertyTableModel(Study study) {
		super(study);
	}
	public int getRowCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0) return columns[rowIndex];
		else 
			switch (rowIndex) {
			
			case 0: return ((Study) object).getStudyConditions();
			case 1: return ((Study) object).getStudyResults();
			case 2: return ((Study) object).getTemplate();
			default: return "";
			}
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex>0)
			switch (rowIndex) {
			/*
			case 0: ((Study) object).getStudyConditions();
			case 1: ((Study) object).getStudyResults();
			*/
			case 2: ((Study) object).setTemplate((StudyTemplate)aValue);
			default:;
			}
	}
	@Override
	public boolean isExpanded(int rowIndex) {
		return false;
	}

}
