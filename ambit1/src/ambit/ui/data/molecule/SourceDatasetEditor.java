package ambit.ui.data.molecule;

import ambit.data.AmbitObject;
import ambit.data.molecule.SourceDataset;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.AbstractPropertyTableModel;

public class SourceDatasetEditor extends AbstractAmbitEditor {
    public SourceDatasetEditor(String caption, SourceDataset object) {
        super(caption,object);
    }
	@Override
	protected AbstractPropertyTableModel createTableModel(AmbitObject object) {

		return new SourceDataSetTableModel((SourceDataset)object);
	}

}
