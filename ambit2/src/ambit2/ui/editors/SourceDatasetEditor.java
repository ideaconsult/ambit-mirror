package ambit2.ui.editors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import ambit2.config.EditorPreferences;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.molecule.SourceDataset;
import ambit2.data.qmrf.QMRFAttributesPanel;

public class SourceDatasetEditor extends JPanel implements IAmbitEditor<SourceDataset> {
	protected QMRFAttributesPanel attrPanel;
	protected IAmbitEditor<LiteratureEntry> referencePanel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3948461193960718058L;
	public SourceDatasetEditor() {
		this(null);
	}	
	public SourceDatasetEditor(SourceDataset attributes) {
		super();
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		attrPanel = new QMRFAttributesPanel();

		add(attrPanel);
		referencePanel = createReferencePanel();
		if (referencePanel != null)
		add(referencePanel.getJComponent());
		setBorder(BorderFactory.createTitledBorder("Dataset"));		
		setObject(attributes);
	}
	protected IAmbitEditor<LiteratureEntry> createReferencePanel() {
		 IAmbitEditor<LiteratureEntry> referencePanel;
		try {
			referencePanel = EditorPreferences.getEditor("ambit2.data.literature.LiteratureEntry");
			referencePanel.getJComponent().setBorder(BorderFactory.createTitledBorder("Reference"));
			add(referencePanel.getJComponent());
		} catch (IllegalAccessException x) {
			referencePanel = null;
		} catch (ClassNotFoundException x) {
			referencePanel = null;
		} catch (InstantiationException x) {
			referencePanel = null;
		}
		return referencePanel;
	}
	public void setObject(SourceDataset object) {
		attrPanel.setObject(object);
		if (object != null)
			referencePanel.setObject(object.getReference());
		
	}
	public SourceDataset getObject() {
		return (SourceDataset)attrPanel.getObject();
	}
	public JComponent getJComponent() {
		return this;
	}

}
