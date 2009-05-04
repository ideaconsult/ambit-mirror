package ambit2.rest;

import org.restlet.data.MediaType;
import org.restlet.resource.Representation;

import ambit2.core.processors.AbstractRepresentationConvertor;
import ambit2.core.processors.Reporter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public abstract class RepresentationConvertor<T,Q extends IQueryRetrieval<T>,Output> 
						extends AbstractRepresentationConvertor<T,Q,Output,Representation,MediaType,QueryReporter<T,Q,Output>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458607015810762273L;


	public RepresentationConvertor(QueryReporter<T,Q,Output> reporter) {
		this(reporter,MediaType.TEXT_PLAIN);
		if (this.reporter != null) ((QueryReporter<T,Q,Output>)this.reporter).setMaxRecords(5000);
	}
	public RepresentationConvertor(QueryReporter<T,Q,Output> reporter,MediaType media) {
		super(reporter,media);
	}
	

	@Override
	public abstract Representation process(Q query) throws ambit2.base.exceptions.AmbitException;
	
	@Override
	public QueryReporter<T, Q, Output> getReporter() {
		return super.getReporter();
	}
	
	
	

	
}
