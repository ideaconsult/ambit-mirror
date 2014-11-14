package ambit2.rest;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.c.RepresentationConvertor;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

/**
 * An abstract {@link IProcessor} , converting between a query results {@link IQueryRetrieval} and restlet Representation.
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryRepresentationConvertor<T,Q extends IQueryRetrieval<T>,Output> 
						extends RepresentationConvertor<T,Q,Output,QueryAbstractReporter<T,Q,Output>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458607015810762273L;


	public QueryRepresentationConvertor(QueryAbstractReporter<T,Q,Output> reporter) {
		this(reporter,MediaType.TEXT_PLAIN,null);
		if (this.reporter != null) ((QueryAbstractReporter<T,Q,Output>)this.reporter).setMaxRecords(5000);
	}
	public QueryRepresentationConvertor(QueryAbstractReporter<T,Q,Output> reporter,MediaType media) {
		this(reporter,media,null);
	}
	public QueryRepresentationConvertor(QueryAbstractReporter<T,Q,Output> reporter,MediaType media,String fileNamePrefix) {
		super(reporter,media,fileNamePrefix);
	}
	

	@Override
	public abstract Representation process(Q query) throws Exception;
	
	
	@Override
	public QueryAbstractReporter<T, Q, Output> getReporter() {
		return super.getReporter();
	}

	
}
