package ambit.ui.data.experiment;

import ambit.data.experiment.Experiment;
import ambit.ui.data.AbstractPropertyTableModel;

public class ExperimentPropertyModel extends AbstractPropertyTableModel {
	protected String[] columns = {"Results","Reference","Study"};
	public ExperimentPropertyModel(Experiment e) {
		super(e);
	}
	public int getRowCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0) return columns[rowIndex];
		else 
			switch (rowIndex) {
			case 0: return ((Experiment) object).getResults();
			case 1: return ((Experiment) object).getReference();
			case 2: return ((Experiment) object).getStudy();
			default: return "";
			}
	}
	@Override
	public boolean isExpanded(int rowIndex) {
		switch (rowIndex) {
		case 0: return true;
		case 1: return false;
		case 2: return false;
		default: return true;
		}
	}

}
