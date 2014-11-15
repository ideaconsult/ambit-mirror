package ambit2.rest.reporters;

import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.processors.batch.ListReporter;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * RDF output for non DB based objects
 * @author nina
 *
 * @param <Q>
 */
public abstract class CatalogRDFReporter<T> extends ListReporter<T,Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1805248585197091514L;
	protected MediaType mediaType;
	protected OntModel jenaModel;
	
	public CatalogRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super();
		this.mediaType = mediaType;
	}

	public OntModel getJenaModel() {
		return jenaModel;
	}
	public void setJenaModel(OntModel jenaModel) {
		this.jenaModel = jenaModel;
	}
	
	@Override
	public void footer(Writer output, Iterator<T> query) {
		if (mediaType.equals(MediaType.APPLICATION_RDF_XML))
			//getJenaModel().write(output,"RDF/XML");
			getJenaModel().write(output,"RDF/XML-ABBREV");	
		else if (mediaType.equals(MediaType.APPLICATION_RDF_TURTLE))
			getJenaModel().write(output,"TURTLE");
		else if (mediaType.equals(MediaType.TEXT_RDF_N3))
			getJenaModel().write(output,"N3");
		else if (mediaType.equals(MediaType.TEXT_RDF_NTRIPLES))
			getJenaModel().write(output,"N-TRIPLE");	
		else 
			getJenaModel().write(output,"RDF/XML-ABBREV");
	}

	@Override
	public void header(Writer output, Iterator<T> query) {
		try {
			setJenaModel(jenaModel==null?OT.createModel():jenaModel);
		} catch (Exception x) {
			Context.getCurrentLogger().warning(x.getMessage());
		}	}

	public void close() throws Exception {
	}

}
