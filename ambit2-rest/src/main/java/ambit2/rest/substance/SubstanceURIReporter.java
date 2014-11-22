package ambit2.rest.substance;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;

public class SubstanceURIReporter<Q extends IQueryRetrieval<SubstanceRecord>> extends QueryURIReporter<SubstanceRecord, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3811264823419677254L;
	
	public SubstanceURIReporter(Request request) {
		super(request);
	}
	public SubstanceURIReporter(Reference reference) {
		super(reference);
	}
	@Override
	public String getURI(String ref, SubstanceRecord item) {
		return SubstanceRecord.getURI(ref, item);
	}

	
}
