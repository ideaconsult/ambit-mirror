package ambit2.rest;

import java.sql.SQLException;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Parent class for RDF reporters
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryRDFReporter<T,Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, OntModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1160842325900158717L;
	protected QueryURIReporter<T, IQueryRetrieval<T>> uriReporter;
	protected abstract QueryURIReporter<T, IQueryRetrieval<T>> createURIReporter(Request req,ResourceDoc doc);
	protected MediaType mediaType;
	protected String compoundInDatasetPrefix;
	
	public QueryRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		this("",request,mediaType,doc);
	}
	public QueryRDFReporter(String prefix,Request request,MediaType mediaType,ResourceDoc doc) {
		super();
		this.compoundInDatasetPrefix = prefix;
		uriReporter = createURIReporter(request,doc);
		this.mediaType = mediaType;
	}
	public OntModel getJenaModel() {
		return output;
	}
	
	@Override
	public void setOutput(OntModel output) throws AmbitException {
		super.setOutput(output);
		if (output!=null)
		try {
			output.setNsPrefix("",uriReporter.getBaseReference().toString()+"/");
			output.setNsPrefix("af",uriReporter.getBaseReference().toString()+"/feature/");
			output.setNsPrefix("am",uriReporter.getBaseReference().toString()+"/model/");
			output.setNsPrefix("ac",uriReporter.getBaseReference().toString()+"/compound/");
			output.setNsPrefix("ad",uriReporter.getBaseReference().toString()+"/dataset/");
			
			if (!"".equals(compoundInDatasetPrefix))
				output.setNsPrefix("cmpd",
						String.format("%s%s/compound/",uriReporter.getBaseReference().toString(),compoundInDatasetPrefix) );
			
			output.setNsPrefix("ag",uriReporter.getBaseReference().toString()+"/algorithm/");

		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}
	public void header(OntModel output, Q query) {};
	public void footer(OntModel output, Q query) {};
	@Override
	public void close() throws SQLException {
		super.close();
	}
}
