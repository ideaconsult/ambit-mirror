package ambit2.rest;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryAbstractReporter;

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
	public abstract Representation process(Q query) throws AmbitException;
	
	
	@Override
	public QueryAbstractReporter<T, Q, Output> getReporter() {
		return super.getReporter();
	}

	
}
