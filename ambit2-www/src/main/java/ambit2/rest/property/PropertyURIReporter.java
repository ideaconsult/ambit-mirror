package ambit2.rest.property;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.structure.CompoundResource;

public class PropertyURIReporter<Q extends IQueryRetrieval<Property>> extends QueryReporter<Property, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected Reference baseReference;
	protected IStructureRecord record;
	
	public IStructureRecord getRecord() {
		return record;
	}
	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	public PropertyURIReporter(Reference baseRef) {
		this.baseReference = baseRef;
	}
	public PropertyURIReporter() {
	}	
	@Override
	public void processItem(Property item, Writer output) {
		try {
			String ref = baseReference.toString();
			if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);			
			output.write(String.format("%s%s/%d/feature/%s",ref,CompoundResource.compound,getRecord().getIdchemical(),item.getName()));
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
		
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	