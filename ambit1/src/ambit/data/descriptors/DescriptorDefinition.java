/**
 * Created on 2005-1-26
 *
 */
package ambit.data.descriptors;

import ambit.data.AmbitObject;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;

/**
 * Contains information about a single descriptor ( no descriptor values!) 
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorDefinition extends AmbitObject {

	DescriptorGroups descriptorGroups = null;
	LiteratureEntry reference = null;

	protected String units = "";
	protected String remark = "";
	protected boolean local = false;

	protected double defaultError = 0;
	protected DescriptorType typeValue = null ;

	/**
	 * @param name unique descriptor name
	 */
	public DescriptorDefinition(String name, LiteratureEntry reference) {
		super(name);
		typeValue = new DescriptorType();
		if (reference == null) this.reference = ReferenceFactory.createEmptyReference();
		else this.reference = reference;
	}
	/**
	 * 
	 * @param name unique descriptor name
	 * @param type Double=0, Integer=1, String=2
	 */
	public DescriptorDefinition(String name, int type, LiteratureEntry reference) {
		super(name);
		typeValue = new DescriptorType(type);
		if (reference == null) this.reference = ReferenceFactory.createEmptyReference();
		else this.reference = reference;		
	}
	/**
	 * 
	 * @param name unique descriptor name
	 * @param type Double=0, Integer=1, String=2
	 * @param dtype as in IColumnTypeSelection types, default is IColumnTypeSelection._ctX
	 */
	public DescriptorDefinition(String name, int type, int dtype,LiteratureEntry reference) {
		super(name);
		this.typeValue = new DescriptorType(type);
		if (reference == null) this.reference = ReferenceFactory.createEmptyReference();
		else this.reference = reference;		
	}	
	
	/**
	 * @return Returns the typeValue.
	 */
	public DescriptorType getValuetype() {
		return typeValue;
	}
	public void clear() {
		super.clear();
		if (descriptorGroups != null) descriptorGroups.clear();;
		if (reference != null) 	reference.clear();

		units = "";
		remark = "";
		local = false;

		defaultError = 0;
		typeValue.clear();
		
	}
	/**
	 * @param type The type to set.
	 */
	public void setValuetype(DescriptorType type) {
		boolean m =  (!typeValue.equals(type));		
		this.typeValue.setId(type.getId());
		setModified(m);
	}

	public String toString() {
		String s = name; 
		if (units.equals("")) return s;
		else return s + " [" + units + "]";
	}
	public boolean equals(Object obj) {
	    if (obj instanceof DescriptorDefinition) {
			DescriptorDefinition d = (DescriptorDefinition) obj;
			boolean b =  super.equals(d) && 
				(local == d.local) &&
				(defaultError == d.defaultError) &&			
				typeValue.equals(d.typeValue) &&
				reference.equals(d.reference);
			if (!b) 
				System.err.println("Different descriptors\t"+d.toString()+ "\n" + toString());
			return b;
	    } else 
	        return false;
	}	
	public Object clone()  throws CloneNotSupportedException {
		DescriptorDefinition d = new DescriptorDefinition(
				name,typeValue.getId(),
				(LiteratureEntry) reference.clone());
		d.units = units;
		d.remark = remark;
		d.local = local;
		d.defaultError = defaultError;
		d.descriptorGroups = (DescriptorGroups) descriptorGroups.clone();
		return d;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String comment) {
		boolean m = (!remark.equals(comment));		
		this.remark = comment;
		setModified(m);
	}
	public boolean isLocal() {
		return local;
	}
	public void setLocal(boolean local) {
		boolean m = (this.local != local);		
		this.local = local;
		setModified(m);
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		boolean m =  (!units.equals(units));		
		this.units = units;
		setModified(m);
	}
	public DescriptorGroups getDescriptorGroups() {
		return descriptorGroups;
	}
	public void setDescriptorGroups(DescriptorGroups groups) {
		boolean m = true;
		if (descriptorGroups != null) 
			m = (!descriptorGroups.equals(groups));		
		this.descriptorGroups = groups;
		setModified(m);
	}
	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		boolean m = true;
		if (this.reference != null) 
			m =  (!this.reference.equals(reference));		
		this.reference = reference;
		setModified(m);
	}
	public double readDefaultError() {
		return defaultError;
	}
	public void writeDefaultError(double error) {
		boolean m =  (defaultError != error);				
		this.defaultError = error;
		setModified(m);
	}
	
    public synchronized double getDefaultError() {
        return defaultError;
    }
    public synchronized void setDefaultError(double defaultError) {
        this.defaultError = defaultError;
    }
}
