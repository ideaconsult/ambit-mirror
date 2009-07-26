package ambit2.rest.structure;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * {@link MediaType.TEXT_URI_LIST}
 * @author nina
 *
 * @param <Q>
 */
public class CompoundURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected Reference baseReference;
	public CompoundURIReporter(Reference baseRef) {
		this.baseReference = baseRef;
	}
	public CompoundURIReporter() {
	}	
	@Override
	public void processItem(IStructureRecord item, Writer output) {
		try {
			String ref = baseReference.toString();
			if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);			
			output.write(String.format("%s%s/%d",ref,CompoundResource.compound,item.getIdchemical()));
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
		
	}
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	