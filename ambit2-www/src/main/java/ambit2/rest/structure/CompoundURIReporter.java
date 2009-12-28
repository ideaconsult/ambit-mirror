package ambit2.rest.structure;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;

/**
 * {@link MediaType.TEXT_URI_LIST}
 * @author nina
 *
 * @param <Q>
 */
public class CompoundURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryURIReporter<IStructureRecord, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected boolean readStructure = false;
	public CompoundURIReporter(Request request) {
		this(request,false);
	}
	public CompoundURIReporter(Request request,boolean readStructure) {
		super(request);
		this.readStructure = readStructure;
		
	}
	public CompoundURIReporter() {
		this(null,false);
	}	


	public void open() throws DbAmbitException {
		
	}
	@Override
	public String getURI(String ref, IStructureRecord item) {
	
		return String.format("%s%s/%d%s",ref,CompoundResource.compound,item.getIdchemical(),delimiter);
	}
	@Override
	protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch() {
		if (readStructure) {
			DbReader<IStructureRecord> reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else return super.createBatch();
	}	
}	