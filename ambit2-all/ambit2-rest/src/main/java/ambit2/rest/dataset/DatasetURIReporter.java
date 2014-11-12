package ambit2.rest.dataset;

import java.io.Writer;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;


public class DatasetURIReporter<Q extends IQueryRetrieval<M>,M extends ISourceDataset> extends QueryURIReporter<M, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public DatasetURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public DatasetURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}	
	public DatasetURIReporter() {
	}	
	@Override
	public String getURI(String ref, M dataset) {
		if (dataset == null) return null;
		String id;
		if (dataset instanceof SourceDataset) {
			if (dataset.getID()<0)
				return String.format("%s/%s/%s", 
						ref,
						OpenTox.URI.dataset.name(),dataset==null?"":dataset.getName());
			else
				return String.format("%s/%s/%d", 
						ref,
						OpenTox.URI.dataset.name(),dataset==null?"":dataset.getID());
		} else if (dataset instanceof SubstanceEndpointsBundle) {
			return String.format("%s/%s/%d", 
					ref,
					OpenTox.URI.bundle.name(),dataset==null?"":dataset.getID());
			
		} else 
			return String.format("%s/%s/R%d", 
					ref,
					OpenTox.URI.dataset.name(),dataset==null?"":dataset.getID());

	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	@Override
	public void open() throws DbAmbitException {

		super.open();
	}
	
}	