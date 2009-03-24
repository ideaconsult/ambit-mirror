package ambit2.rest;

import org.restlet.data.MediaType;
import org.restlet.resource.Representation;

import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public abstract class RepresentationConvertor<T,Q extends IQueryRetrieval<T>,Output> 
						extends DefaultAmbitProcessor<Q,Representation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458607015810762273L;
	protected QueryReporter<T,Q,Output> reporter;
	protected MediaType mediaType = MediaType.TEXT_PLAIN;
	
	public MediaType getMediaType() {
		return mediaType;
	}
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	public QueryReporter<T, Q, Output> getReporter() {
		return reporter;
	}
	public void setReporter(QueryReporter<T, Q, Output> reporter) {
		this.reporter = reporter;
	}
	public RepresentationConvertor(QueryReporter<T,Q,Output> reporter) {
		this.reporter = reporter;
		if (this.reporter != null) this.reporter.setMaxRecords(5000);
	}
	public abstract Representation process(Q query) throws ambit2.base.exceptions.AmbitException;
	
	

	
}
