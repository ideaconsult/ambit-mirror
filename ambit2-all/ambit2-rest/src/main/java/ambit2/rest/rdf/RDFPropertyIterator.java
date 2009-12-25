package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.util.Template;

import ambit2.base.data.ILiteratureEntry;
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
		return new Property("");
	}

	@Override
	protected Template createTemplate() {
		return new Template(String.format("%s%s",baseReference==null?"":baseReference,OpenTox.URI.feature.getResourceID()));
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
		String name = newEntry.toString();
		String label = name;
		
		try { name = getTitle(newEntry);	} catch (Exception x) {	logger.warn(x);
		}	
		//label
		try { 
			label = newEntry.getProperty(OWL.sameAs).getObject().toString();	} 
		catch (Exception x) {
			label = Property.guessLabel(name);
			logger.warn(x); 
		}	
		property.setName(name);
		property.setLabel(label);		
		//units
		try {	
			property.setUnits(((Literal)newEntry.getProperty(OT.DataProperty.units.createProperty(jenaModel))
						.getObject()).getString()); 
		} catch (Exception x) {
			logger.warn(x);
			property.setUnits("");
		}
		
		try {
			ILiteratureEntry ref = RDFReferenceIterator.readReference(jenaModel, newEntry, baseReference,OT.OTProperty.hasSource.createProperty(jenaModel));
			property.setReference(ref);
		} catch (Exception x) {
			
		}
		/*
		RDFReferenceIterator iterator = null;
		String url;
		//reference 
		try {	
			
			RDFNode reference = newEntry.getProperty(OT.OTProperty.hasSource.createProperty(jenaModel)).getObject();
			if (reference.isResource()) {
				try {
					url = getIdentifier(reference);
					StmtIterator st = jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.hasSource.createProperty(jenaModel),(RDFNode)null));
					iterator = new RDFReferenceIterator(jenaModel,st);
					iterator.setBaseReference(baseReference);
					iterator.setIterateSubjects(false);
				} catch (Exception x) {
					property.setReference(RDFReferenceIterator.parseRecord(jenaModel,(Resource) reference, property.getReference()));
					return property;
				}
			} else {
				url = ((Literal)reference).getString();
				iterator = new RDFReferenceIterator(new Reference(url));
			}
			
			try {
				
				iterator.setBaseReference(baseReference);
				while (iterator.hasNext()) {
					property.setReference(iterator.next());
					return property;
				}
				property.setReference(new LiteratureEntry(url.toString(),url.toString()));
			} catch (Exception x) {
				property.setReference(new LiteratureEntry(url.toString(),url.toString()));
				logger.warn(x);
			} finally {
				try { iterator.close();} catch (Exception x) {}
			}			
		} catch (Exception x) {
			logger.warn(x);
			url = name;
		}		
		*/

		return property;
	}
}
