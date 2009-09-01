package ambit2.rest.structure;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
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

	public CompoundURIReporter(Reference baseRef) {
		super(baseRef);
	}
	public CompoundURIReporter() {
	}	


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getURI(String ref, IStructureRecord item) {
	
		return String.format("%s%s/%d%s",ref,CompoundResource.compound,item.getIdchemical(),delimiter);
	}
}	