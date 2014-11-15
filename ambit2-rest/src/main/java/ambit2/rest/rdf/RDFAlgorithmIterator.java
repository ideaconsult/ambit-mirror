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

import ambit2.core.data.model.Algorithm;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Parses RDF representation of {@link Algorithm}
 * @author nina
 *
 */
public class RDFAlgorithmIterator extends RDFObjectIterator<Algorithm> {

	public RDFAlgorithmIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.Algorithm.toString());
	}
		
	public RDFAlgorithmIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		super(reference,OT.OTClass.Algorithm.toString());
	}	
	public RDFAlgorithmIterator(Reference reference,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		super(reference,mediaType,OT.OTClass.Algorithm.toString());
	}
	
	public RDFAlgorithmIterator(InputStream in,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		super(in,mediaType,OT.OTClass.Algorithm.toString());
	}	
	
	public RDFAlgorithmIterator(OntModel model) {
		super(model,OT.OTClass.Algorithm.toString());
	}
	
	@Override
	protected Algorithm createRecord() {
		return new Algorithm();
	}

	@Override
	protected Template createTemplate() {
		return OpenTox.URI.algorithm.getTemplate(baseReference);
	}

	@Override
	protected void parseObjectURI(RDFNode uri, Algorithm record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			getTemplate().parse(getURI(uri), vars);
			record.setId(vars.get(OpenTox.URI.algorithm.getKey()).toString()); } 
		catch (Exception x) { };
		
	}

	@Override
	protected Algorithm parseRecord(RDFNode newEntry, Algorithm record) {
		if (newEntry.isLiteral()) {
			return record;
		} else {
			StmtIterator alg =  jenaModel.listStatements(
					new SimpleSelector((Resource)newEntry,OT.OTProperty.parameters.createProperty(jenaModel),(RDFNode)null));
			while (alg.hasNext()) {
				Statement st = alg.next();
				parseParameters(st.getObject(),record);
			}	
			return record;
		}
	}
	protected void parseParameters(RDFNode paramEntry,Algorithm record) {
		//TODO
	}
}
