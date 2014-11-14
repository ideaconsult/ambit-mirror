package ambit2.db.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

public abstract class AbstractDescriptorCalculator<INTERNAL> extends AbstractDBProcessor<IStructureRecord,IStructureRecord> {
    protected Profile<Property> descriptors = null;
    protected boolean assignProperties = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3717059242431165100L;
	
	
	public Profile<Property> getDescriptors() {
		return descriptors;
	}
	public void setDescriptors(Profile<Property> descriptors) {
		this.descriptors = descriptors;
	}
	
    public boolean isAssignProperties() {
		return assignProperties;
	}
	public void setAssignProperties(boolean assignProperties) {
		this.assignProperties = assignProperties;
	}

	
	public abstract INTERNAL preprocess(IStructureRecord target) throws AmbitException ;
	
	
}
