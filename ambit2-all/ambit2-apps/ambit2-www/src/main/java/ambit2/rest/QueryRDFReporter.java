package ambit2.rest;

import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Request;
import org.restlet.data.MediaType;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

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
	public void setOutput(OntModel output) throws Exception {
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
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		try {
			Resource rdfDataType = output.createResource(OT.NS + "ToxicityCategory");
			output.add(rdfDataType, RDFS.subClassOf, XSD.xstring);
		} catch (Exception x) {	}
		
	}
	public void header(OntModel output, Q query) {};
	public void footer(OntModel output, Q query) {};
	@Override
	public void close() throws Exception {
		super.close();
	}
	
	@Override
	public String getFileExtension() {
		if (MediaType.APPLICATION_RDF_XML.equals(mediaType)) return "rdf";
		else if (MediaType.TEXT_RDF_N3.equals(mediaType)) return "n3";
		return "rdf";
	}
}
