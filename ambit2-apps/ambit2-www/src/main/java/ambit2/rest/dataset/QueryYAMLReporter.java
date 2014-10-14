package ambit2.rest.dataset;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.ho.yaml.YamlEncoder;
import org.restlet.Request;

import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

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

	public QueryYAMLReporter(ResourceDoc doc) {
		this(null,doc);
	}
	public QueryYAMLReporter(Request request,ResourceDoc doc) {
		super();
		uriReporter = createURIReporter(request,doc); 
	}
	protected abstract QueryURIReporter createURIReporter(Request request,ResourceDoc doc);


	public void open() throws DbAmbitException {
		
	}


}
