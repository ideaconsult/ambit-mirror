package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.util.Template;

import ambit2.db.model.ModelQueryResults;
import ambit2.rest.model.ModelResource;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Parses RDF representation of a OpenTox Model {@link ModelQueryResults}
 * @author nina
 *
 */
public class RDFModelIterator extends RDFObjectIterator<ModelQueryResults> {

	public RDFModelIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.Model.toString());
	}
		
	public RDFModelIterator(Reference reference) throws ResourceException {
		super(reference,OT.OTClass.Model.toString());
	}	
	public RDFModelIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType,OT.OTClass.Model.toString());
	}
	
	public RDFModelIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,OT.OTClass.Model.toString());
	}	
	
	public RDFModelIterator(OntModel model) {
		super(model,OT.OTClass.Model.toString());
	}	
	@Override
	protected ModelQueryResults createRecord() {
		return new ModelQueryResults();
	}

	@Override
	protected Template createTemplate() {
		return new Template(String.format("%s%s",baseReference==null?"":baseReference,
				ModelResource.resourceID));
	}

	@Override
	protected void parseObjectURI(RDFNode uri, ModelQueryResults record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getIdentifier(uri), vars);
			record.setId(Integer.parseInt(vars.get(ModelResource.resourceKey).toString())); } 
		catch (Exception x) { record.setId(-1);};
		
	}

	@Override
	protected ModelQueryResults parseRecord(Resource newEntry,
			ModelQueryResults record) {
		//TODO
		return null;
	}

}
