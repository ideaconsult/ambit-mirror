package ambit2.rest.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.data.model.ModelWrapper;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Parses RDF representation of a OpenTox Model {@link ModelQueryResults}
 * @author nina
 *
 */
public abstract class RDFModelIterator<T,TrainingInstances extends T,TestInstances extends T,Content> 
								extends RDFObjectIterator<ModelWrapper<T,TrainingInstances,TestInstances,Content,String>> {

	public RDFModelIterator(Representation representation,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		super(representation,mediaType,OT.OTClass.Model.toString());
	}
		
	public RDFModelIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		super(reference,OT.OTClass.Model.toString());
	}	
	public RDFModelIterator(Reference reference,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		super(reference,mediaType,OT.OTClass.Model.toString());
	}
	
	public RDFModelIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,OT.OTClass.Model.toString());
	}	
	
	public RDFModelIterator(OntModel model) {
		super(model,OT.OTClass.Model.toString());
	}	
	
	@Override
	protected Template createTemplate() {
		return OpenTox.URI.model.getTemplate(baseReference);
	}

	@Override
	protected void parseObjectURI(RDFNode uri, ModelWrapper<T,TrainingInstances,TestInstances,Content,String> record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getURI(uri), vars);
			record.setId(Integer.parseInt(vars.get(OpenTox.URI.model.getKey()).toString())); } 
		catch (Exception x) { record.setId(-1);};
		
	}

	@Override
	protected ModelWrapper<T,TrainingInstances,TestInstances,Content,String> parseRecord(RDFNode newEntry,
			ModelWrapper<T,TrainingInstances,TestInstances,Content,String> record) {
		//TODO
		return null;
	}

}
