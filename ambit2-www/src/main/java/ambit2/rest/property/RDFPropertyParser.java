package ambit2.rest.property;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Reference;
import org.restlet.util.Template;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.rest.OT;
import ambit2.rest.RDFBatchParser;
import ambit2.rest.reference.RDFReferenceParser;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Parsers RDF representation of Feature
 * @author nina
 *
 */
public class RDFPropertyParser extends RDFBatchParser<Property> {
	protected Template featureTemplate;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1257623392289416245L;

	public RDFPropertyParser(String baseReference) {
		super(baseReference, OT.OTClass.Feature.toString());
		featureTemplate = new Template(String.format("%s%s%s",baseReference==null?"":baseReference,
				PropertyResource.featuredef,
				PropertyResource.featuredefID));
	}

	@Override
	protected Property createRecord() {
		return new Property("");
	}

	@Override
	protected Property parseRecord(Resource newEntry, Property record) {
		Property p = RDFPropertyParser.parseRecord(jenaModel, newEntry, record,baseReference);
		parseFeatureURI(newEntry.toString(),p,featureTemplate);
		return p;
	}


	public static Property parseRecord(OntModel jenaModel, Resource newEntry, final Property property, String baseReference) {
		//name
		String name = newEntry.toString();
		String title = name;
		String url = name;
		String label = name;
		LiteratureEntry ref;
		try {	
			name = (((Literal)newEntry.getProperty(DC.title)
						.getObject()).getString()); 
		} catch (Exception x) {
			logger.warn(x);
		}	
		//label
		try {	
			label = newEntry.getProperty(OWL.sameAs).getObject().toString();
		} catch (Exception x) {
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
		
		//reference 
		try {	
			url = newEntry.getProperty(OT.OTProperty.hasSource.createProperty(jenaModel)).getObject().toString();
		} catch (Exception x) {
			logger.warn(x);
			url = name;
		}		
		try {	
			RDFReferenceParser parser = new RDFReferenceParser(baseReference) {
				@Override
				protected ILiteratureEntry parseRecord(Resource newEntry,
						ILiteratureEntry le) {
					ILiteratureEntry ref = super.parseRecord(newEntry, le);
					property.setReference((LiteratureEntry)ref);
					return ref;
				}
			};
			parser.process(new Reference(url.toString()));
		} catch (Exception x) {
			property.setReference(new LiteratureEntry(url.toString(),url.toString()));
			logger.warn(x);
		}		

		
		
		return property;
	}
	
	public static void parseFeatureURI(String uri,Property property, Template featureTemplate) {
		Map<String, Object> vars = new HashMap<String, Object>();
		featureTemplate.parse(uri, vars);
		try {property.setId(Integer.parseInt(vars.get(PropertyResource.idfeaturedef).toString())); } 
		catch (Exception x) {property.setId(-1);};

	}	
}
