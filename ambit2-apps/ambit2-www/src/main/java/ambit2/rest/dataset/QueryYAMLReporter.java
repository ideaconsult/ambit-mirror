package ambit2.rest.dataset;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.ho.yaml.YamlEncoder;
import org.restlet.Request;

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
