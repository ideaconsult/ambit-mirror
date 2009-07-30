package ambit2.rest.dataset;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.XMLTags;


public class DatasetURIReporter<Q extends IQueryRetrieval<SourceDataset>> extends QueryURIReporter<SourceDataset, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public DatasetURIReporter(Reference baseRef) {
		super(baseRef);
	}
	public DatasetURIReporter() {
	}	
	@Override
	public String getURI(String ref, SourceDataset dataset) {
		return
		String.format("%s/%s/%d", 
				ref,
				XMLTags.node_dataset,dataset==null?"":dataset.getId());
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	