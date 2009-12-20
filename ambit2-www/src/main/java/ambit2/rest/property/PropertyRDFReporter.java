package ambit2.rest.property;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.BibTex;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.rdf.OT;
import ambit2.rest.reference.ReferenceRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

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
	public void processItem(Property item) throws AmbitException {
		addToModel(getJenaModel(), item, uriReporter,referenceReporter);

	}

	public static void addToModel(OntModel jenaModel,Property item, 
			QueryURIReporter<Property, IQueryRetrieval<Property>> uriReporter,
			ReferenceURIReporter referenceReporter
			) {
		Individual feature = null;
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null) || (item.getId()<0)) {
			feature = jenaModel.createIndividual(OT.OTClass.Feature.getOntClass(jenaModel));
		} else {
			feature = jenaModel.createIndividual(uriReporter.getURI(item),OT.OTClass.Feature.getOntClass(jenaModel));
			feature.addLiteral(DC.identifier,
					jenaModel.createTypedLiteral(uriReporter.getURI(item),XSDDatatype.XSDanyURI));
		}
		feature.addProperty(DC.title, item.getName());
		feature.addProperty(OT.DataProperty.units.createProperty(jenaModel),item.getUnits());
		
		
		if(item.getLabel()==null) {
			String label = Property.guessLabel(item.getName());
			feature.addProperty(OWL.sameAs,String.format("%s%s",OT.NS,label==null?item.getName():label));
		} else feature.addProperty(OWL.sameAs,
				String.format("%s%s",OT.NS,item.getLabel()));
		
		
		Individual reference = ReferenceRDFReporter.addToModel(jenaModel, item.getReference(), referenceReporter);
		feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), reference);
		if (item.getClazz()!=null)
		feature.addProperty(DC.type,
				 (item.getClazz()==Number.class)?
						AbstractPropertyRetrieval._PROPERTY_TYPE.NUMERIC.getXSDType():
						AbstractPropertyRetrieval._PROPERTY_TYPE.STRING.getXSDType()
						);
	
	}	
	public void open() throws DbAmbitException {
		
	}

}
