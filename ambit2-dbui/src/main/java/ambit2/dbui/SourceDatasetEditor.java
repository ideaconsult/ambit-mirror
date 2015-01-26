package ambit2.dbui;

import ambit2.base.data.SourceDataset;
import ambit2.ui.editors.BeanEditor;

public class SourceDatasetEditor extends BeanEditor<SourceDataset> {

    public SourceDatasetEditor() {
	super(null, new String[] { "name", "title", "URL", }, new String[] { "Dataset name", "Reference", "WWW" }, "");
    }
}
