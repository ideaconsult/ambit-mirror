package ambit2.rest.tuple;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.tuple.QueryTuple;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.structure.CompoundURIReporter;

/**
 * Returns uri of a tuple
 * @author nina
 *
 */
public class TupleURIReporter extends QueryURIReporter<Integer, QueryTuple> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4926639282953725551L;
	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> reporter;
	protected IStructureRecord record;
	public IStructureRecord getRecord() {
		return record;
	}
	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	public TupleURIReporter(Reference baseRef,IStructureRecord record) {
		this.baseReference = baseRef;
		reporter = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(baseRef);
		this.record = record;
	}
	@Override
	public String getURI(String ref, Integer item) {
		return String.format("%s/%s/%d", reporter.getURI(record),TupleResource.resourceTag,item);
	}

}
