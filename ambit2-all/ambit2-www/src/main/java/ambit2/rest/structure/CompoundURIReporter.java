package ambit2.rest.structure;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

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
	
	public CompoundURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public CompoundURIReporter(Request request,ResourceDoc doc) {
		this(request,false,doc);
	}
	public CompoundURIReporter(Request request,boolean readStructure,ResourceDoc doc) {
		super(request,doc);
		this.readStructure = readStructure;
		
	}
	public CompoundURIReporter(ResourceDoc doc) {
		this(null,false,doc);
	}	


	public void open() throws DbAmbitException {
		
	}
	@Override
	public String getURI(String ref, IStructureRecord item) {
		if ((item.getIdstructure()==-1) || (item.getType().equals(STRUC_TYPE.NA)))
			return String.format("%s%s/%d%s",ref,CompoundResource.compound,item.getIdchemical(),delimiter);
		else
			return String.format("%s%s/%d%s/%d%s",
						ref,
						CompoundResource.compound,item.getIdchemical(),ConformerResource.conformerKey,item.getIdstructure(),getDelimiter());				
		
	}
	@Override
	protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
		if (readStructure) {
			DbReader<IStructureRecord> reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else return super.createBatch(query);
	}	
}	