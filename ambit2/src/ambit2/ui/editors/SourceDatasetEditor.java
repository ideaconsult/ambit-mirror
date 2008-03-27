package ambit2.ui.editors;

import ambit2.data.AmbitObject;
import ambit2.data.molecule.SourceDataset;
import ambit2.ui.data.AbstractPropertyTableModel;
import ambit2.ui.data.molecule.SourceDataSetTableModel;

public class SourceDatasetEditor extends AbstractAmbitEditor {
    public SourceDatasetEditor(String caption, SourceDataset object) {
        super(caption,object);
    }
	@Override
	protected AbstractPropertyTableModel createTableModel(AmbitObject object) {

		return new SourceDataSetTableModel((SourceDataset)object);
	}

}
