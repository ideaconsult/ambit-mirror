package ambit2.rest.property;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.OTClass;
import ambit2.rest.reference.ReferenceRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Feature reporter
 * @author nina
 *
 * @param <Q>
 */
public class PropertyRDFReporter<Q extends IQueryRetrieval<Property>> extends QueryRDFReporter<Property, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected ReferenceURIReporter referenceReporter;
	public PropertyRDFReporter(Request request,MediaType mediaType) {
		super(request,mediaType);
		referenceReporter = new ReferenceURIReporter(request);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
		return new PropertyURIReporter(reference);
	}
	public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
		super.header(output, query);
		OT.OTClass.Feature.createOntClass(getJenaModel());
	
	}
	@Override
	public Object processItem(Property item) throws AmbitException {
		return addToModel(getJenaModel(), item, uriReporter,referenceReporter);

	}

	public static Individual addToModel(OntModel jenaModel,Property item, 
			QueryURIReporter<Property, IQueryRetrieval<Property>> uriReporter,
			ReferenceURIReporter referenceReporter
			) {
		Individual feature = null;
		OTClass featureType = (item.getClazz()==Number.class)?OTClass.NumericFeature:OTClass.Feature;
		
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null) || (item.getId()<0)) {
			feature = jenaModel.createIndividual(featureType.getOntClass(jenaModel));
		} else {
			feature = jenaModel.createIndividual(uriReporter.getURI(item),featureType.getOntClass(jenaModel));
			feature.addLiteral(DC.identifier,
					jenaModel.createTypedLiteral(uriReporter.getURI(item),XSDDatatype.XSDanyURI));
		}
		feature.addProperty(DC.title, item.getName());
		feature.addProperty(OT.DataProperty.units.createProperty(jenaModel),item.getUnits());
		
		String uri = item.getLabel();
		if(uri==null) uri  = Property.guessLabel(item.getName());
		if (uri.indexOf("http://")<0) {
			uri = String.format("%s%s",OT.NS,Reference.encode(uri));
		}
		feature.addProperty(OWL.sameAs,jenaModel.createResource(uri));
		
		Individual reference = ReferenceRDFReporter.addToModel(jenaModel, item.getReference(), referenceReporter);
		feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), reference);
		if (item.getClazz()!=null)
		feature.addProperty(DC.type,
				 (item.getClazz()==Number.class)?
						AbstractPropertyRetrieval._PROPERTY_TYPE.NUMERIC.getXSDType():
						AbstractPropertyRetrieval._PROPERTY_TYPE.STRING.getXSDType()
						);
		
		return feature;
	}	
	
	public void open() throws DbAmbitException {
		
	}

}
