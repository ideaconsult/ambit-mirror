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
public class StructureURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected Reference strucReference = new Reference("/structure/");
	public StructureURIReporter(Reference baseRef) {
		strucReference.setBaseRef(baseRef);
	}
	public StructureURIReporter() {
	}	
	@Override
	protected void processItem(IStructureRecord item, Writer output) {
		try {
			output.write(strucReference.getTargetRef().toString());
			output.write(Integer.toString(item.getIdstructure()));
			output.write('\n');
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	