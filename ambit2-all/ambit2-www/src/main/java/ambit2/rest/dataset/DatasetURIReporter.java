package ambit2.rest.dataset;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.data.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;


public class DatasetURIReporter<Q extends IQueryRetrieval<SourceDataset>> extends QueryURIReporter<SourceDataset, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public DatasetURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public DatasetURIReporter() {
	}	
	@Override
	public String getURI(String ref, SourceDataset dataset) {
		if (dataset == null) return null;
		if (dataset.getId()<0)
			return String.format("%s/%s/%s", 
					ref,
					OpenTox.URI.dataset.name(),dataset==null?"":dataset.getName());
		else
			return String.format("%s/%s/%d", 
					ref,
					OpenTox.URI.dataset.name(),dataset==null?"":dataset.getId());

	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	
}	