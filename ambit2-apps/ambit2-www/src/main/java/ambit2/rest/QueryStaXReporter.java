package ambit2.rest;

import javax.xml.stream.XMLStreamWriter;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.rest.dataset.AbstractStaxRDFWriter;

public abstract class QueryStaXReporter<T,Q extends IQueryRetrieval<T>,R extends AbstractStaxRDFWriter<T, T>> 
										extends QueryReporter<T, Q, XMLStreamWriter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected QueryURIReporter<T, IQueryRetrieval<T>> uriReporter;
	protected abstract QueryURIReporter<T, IQueryRetrieval<T>> createURIReporter(Request req,ResourceDoc doc);

	
	protected R recordWriter;

	protected String compoundInDatasetPrefix;
	
	
	public QueryStaXReporter(Request request) {
		
	}
	public QueryStaXReporter(String prefix,Request request) {
		super();
		this.compoundInDatasetPrefix = prefix;
		uriReporter = createURIReporter(request,null);
		recordWriter = createRecordWriter(request,null);
		recordWriter.setUriReporter(uriReporter);
	}
	

	
	public QueryURIReporter<T, IQueryRetrieval<T>> getUriReporter() {
		return uriReporter;
	}
	public void setUriReporter(QueryURIReporter<T, IQueryRetrieval<T>> uriReporter) {
		this.uriReporter = uriReporter;
	}
	protected abstract R createRecordWriter(Request request, ResourceDoc doc);
	
	@Override
	public void footer(XMLStreamWriter writer, Q query) {
		recordWriter.footer(writer);

		
	}

	@Override
	public void header(XMLStreamWriter writer, Q query) {
		
		recordWriter.header(writer);
		
	}
   
}
