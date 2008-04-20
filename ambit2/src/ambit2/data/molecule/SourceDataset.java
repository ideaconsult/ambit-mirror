/**
 * Created on 2005-3-28
 *
 */
package ambit2.data.molecule;

import ambit2.data.literature.LiteratureEntry;
import ambit2.data.qmrf.QMRFAttributes;


/**
 * An origin dataset . See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class SourceDataset extends QMRFAttributes {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6881106188176017447L;
	protected LiteratureEntry reference = null;

	protected static final String[] names={
		"Name","User name"};	
	/**
	 * 
	 */
	public SourceDataset() {
		this("");
	}
	public SourceDataset(String name) {
		this(name,new LiteratureEntry());
	}
	/**
	 * @param name
	 */
	public SourceDataset(String name, LiteratureEntry reference) {
		super(names);
		setName(name);
		setReference(reference);
	}

	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}
	public void setName(String name) {
		put(names[0],name);
	}
	public void setUsername(String name) {
		put(names[1],name);
	}	
	public String getName() {
		return get(names[0]);
	}
	public String getUsername() {
		return get(names[1]);
	}
	/* (non-Javadoc)
     * @see ambit2.data.AmbitObject#toString()
     */
    public String toString() {
        return getName() + " Origin: " + reference.toString();
    }

}
