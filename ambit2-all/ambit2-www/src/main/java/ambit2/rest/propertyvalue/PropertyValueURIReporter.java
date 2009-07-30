package ambit2.rest.propertyvalue;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.structure.CompoundResource;

public class PropertyValueURIReporter<Q extends IQueryRetrieval<Property>> extends QueryURIReporter<Property, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected IStructureRecord record;
	
	public IStructureRecord getRecord() {
		return record;
	}
	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	public PropertyValueURIReporter(Reference baseRef) {
		super(baseRef);
	}
	public PropertyValueURIReporter() {
	}	

	@Override
	public String getURI(String ref, Property item) {
		return String.format("%s%s/%d/%s/%s",ref,CompoundResource.compound,getRecord().getIdchemical(),PropertyValueResource.featureKey,item.getName());
	}

}	