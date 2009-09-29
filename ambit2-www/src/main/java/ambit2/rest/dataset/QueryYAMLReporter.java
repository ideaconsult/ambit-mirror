package ambit2.rest.dataset;

import org.ho.yaml.YamlEncoder;
import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.QueryURIReporter;

/**
 * 
 * @author nina
 *
 */
public abstract class QueryYAMLReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,YamlEncoder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7558988426166255067L;
	protected QueryURIReporter<T, IQueryRetrieval<T>> uriReporter;

	public QueryYAMLReporter() {
		this(null);
	}
	public QueryYAMLReporter(Reference baseRef) {
		super();
		uriReporter = createURIReporter(baseRef); 
	}
	protected abstract QueryURIReporter createURIReporter(Reference reference);


	public void open() throws DbAmbitException {
		
	}


}
