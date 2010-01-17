package ambit2.rest.rdf;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Parses RDF representation of a Feature {@link Property}
 * @author nina
 *
 */
public class RDFPropertyIterator extends RDFObjectIterator<Property> {
	protected RDFReferenceIterator referenceIterator;
	
	public RDFPropertyIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.Feature.toString());
	}
		
	public RDFPropertyIterator(Reference reference) throws ResourceException {
		super(reference,OT.OTClass.Feature.toString());
	}	
	public RDFPropertyIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType,OT.OTClass.Feature.toString());
	}
	
	public RDFPropertyIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,OT.OTClass.Feature.toString());
	}	
	public RDFPropertyIterator(OntModel model,StmtIterator recordIterator) {
		super(model,OT.OTClass.Feature.toString(),recordIterator);
	}	
	public RDFPropertyIterator(OntModel model) {
		super(model,OT.OTClass.Feature.toString());
	}

	@Override
	protected Property createRecord() {
		
		return reference==null?new Property(""):new Property("",new LiteratureEntry(reference.toString(),reference.toString()));
	}

	@Override
	protected Template createTemplate() {
		return OpenTox.URI.feature.getTemplate(baseReference);
	}

	
	@Override
	protected Property parseRecord(Resource newEntry, Property record) {
		Property p = parseRecord(jenaModel, newEntry, record,baseReference);
		parseObjectURI(newEntry,p);
		return p;
	}

	@Override
	protected void parseObjectURI(RDFNode uri, Property property) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getIdentifier(uri), vars);
			property.setId(Integer.parseInt(vars.get(OpenTox.URI.feature.getKey()).toString())); } 
		catch (Exception x) {property.setId(-1);};
	}
	public static Property parseRecord(OntModel jenaModel, Resource newEntry, final Property property, Reference baseReference) {
		//name
		String name = newEntry.getURI();
		String label = name;
		
		try { name = getTitle(newEntry);	} catch (Exception x) {	
		}	
		//label
		try { 
			RDFNode resource = newEntry.getProperty(OWL.sameAs).getObject();
			if (resource.isLiteral()) label = ((Literal)resource).getString();
			else label = resource.isURIResource()?((Resource)resource).getURI():resource.toString();
		}	catch (Exception x) {
			label = Property.guessLabel(name);
			label = label==null?name:label;
            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            x.printStackTrace(new PrintWriter(stackTraceWriter));				
			Context.getCurrentLogger().info(stackTraceWriter.toString());
		}	
		property.setName(name==null?label:name);
		property.setLabel(label);		
		//units
		try {	
			property.setUnits(((Literal)newEntry.getProperty(OT.DataProperty.units.createProperty(jenaModel))
						.getObject()).getString()); 
		} catch (Exception x) {
			property.setUnits("");
		}
		ILiteratureEntry ref;
		try {
			ref = RDFReferenceIterator.readReference(jenaModel, newEntry, baseReference,OT.OTProperty.hasSource.createProperty(jenaModel));
		} catch (Exception x) { ref = null;}
 
		if (ref == null)
		try {
			ref = new LiteratureEntry(getCreator(newEntry),baseReference.toString());
		} catch (Exception e) {
			ref = new LiteratureEntry(baseReference.toString(),baseReference.toString());
		}		
		property.setReference(ref);
		return property;
	}
}
