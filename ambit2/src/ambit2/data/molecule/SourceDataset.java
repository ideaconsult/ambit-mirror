/**
 * Created on 2005-3-28
 *
 */
package ambit2.data.molecule;

import ambit2.ui.data.molecule.SourceDataSetTableModel;
import ambit2.ui.editors.AbstractAmbitEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.SourceDatasetEditor;
import ambit2.data.AmbitObject;
import ambit2.data.literature.LiteratureEntry;


/**
 * An origin dataset . See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class SourceDataset extends AmbitObject {
	protected LiteratureEntry reference = null;
	protected int numberofstructures = 0;
	/**
	 * 
	 */
	public SourceDataset() {
		super();
		reference = new LiteratureEntry();
	}

	/**
	 * @param name
	 */
	public SourceDataset(String name, LiteratureEntry reference) {
		super();
		setName(name);
		this.reference = reference;
	}

	/**
	 * @param name
	 * @param id
	 */
	public SourceDataset(String name, int id, LiteratureEntry reference) {
		super("", id);
		setName(name);
		this.reference = reference;
	}
	public Object clone() throws CloneNotSupportedException {
		return new SourceDataset(name,id,(LiteratureEntry)reference.clone());
	}

	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}
	public void setName(String name) {
		int i = name.lastIndexOf("\\");
		if (i > 0) name = name.substring(i+1);
		super.setName(name);	
	}
	@Override
	public IAmbitEditor editor(boolean editable) {
		return new SourceDatasetEditor("Dataset",this);
	}
	/* (non-Javadoc)
     * @see ambit2.data.AmbitObject#toString()
     */
    public String toString() {
        return getName() + " Origin: " + reference.toString();
    }

	public int getNumberofstructures() {
		return numberofstructures;
	}

	public void setNumberofstructures(int numberofstructures) {
		this.numberofstructures = numberofstructures;
	}
	@Override
	public void setEditable(boolean editable) {
			super.setEditable(editable);
			if (reference!= null) reference.setEditable(editable);
	}
}
