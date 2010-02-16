package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.Dictionary;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

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
		
		try { 
			RDFNode resource = newEntry.getProperty(OWL.sameAs).getObject();
			if (resource.isLiteral()) label = ((Literal)resource).getString();
			else label = resource.isURIResource()?((Resource)resource).getURI():resource.toString();
		}	catch (Exception x) {
			label = Property.guessLabel(name);
			label = label==null?name:label;
		}	
		
		property.setName(name==null?label:name);
		property.setLabel(label);		
		
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
		
		property.setClazz(String.class);
		StmtIterator it = null;
		property.setNominal(false);
		try {
			it = jenaModel.listStatements(new SimpleSelector(newEntry,RDF.type,(RDFNode)null));
			while (it.hasNext()) {
				Statement st = it.next();

				if (!st.getObject().isResource()) continue;
				Resource resource = st.getResource();
				if (resource.hasURI(OT.OTClass.Feature.getNS())) continue;				
				if (resource.hasURI(OT.OTClass.NumericFeature.getNS())) 
					property.setClazz(Number.class);
				else if (resource.hasURI(OT.OTClass.TupleFeature.getNS())) 
					property.setClazz(Dictionary.class);				
				else if (resource.hasURI(OT.OTClass.NominalFeature.getNS())) 
					property.setNominal(true);
					

			}
		} catch (Exception x) {

		} finally {
			try {it.close(); } catch (Exception x) {}
		}

		return property;
	}
	
	public static void readFeaturesRDF(String uri,final ambit2.base.data.Template profile, Reference rootRef) {
		if (uri==null) return;
		Representation r = null;
		RDFPropertyIterator iterator  = null;
		OntModel jenaModel = null;
		try {
			
			iterator = new RDFPropertyIterator(new Reference(uri));
			jenaModel = iterator.getJenaModel();
			iterator.setBaseReference(
					uri.startsWith("riap://application/")?new Reference("riap://application/"):
					rootRef
					);
			while (iterator.hasNext()) {
				Property p = iterator.next();
				p.setEnabled(true);
				profile.add(p);
			};
			
		} catch (Exception x) {
			//getLogger().severe(x.getMessage());

		} finally {
			try {iterator.close();} catch (Exception x) {}
			try {jenaModel.close();} catch (Exception x) {}
			try {if (r != null) r.release(); } catch (Exception x) {}
			
		}
	}		
}
