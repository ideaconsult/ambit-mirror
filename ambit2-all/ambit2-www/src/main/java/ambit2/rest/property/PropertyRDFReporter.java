package ambit2.rest.property;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.OT;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
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
	public void processItem(Property item) throws AmbitException {

		Individual feature = getJenaModel().createIndividual(uriReporter.getURI(item),
				OT.OTClass.Feature.getOntClass(getJenaModel()));
		feature.addProperty(DC.title, item.getName());
		feature.addLiteral(DC.identifier,
				 getJenaModel().createTypedLiteral(uriReporter.getURI(item),XSDDatatype.XSDanyURI));
		feature.addProperty(OT.units,item.getUnits());
		feature.addProperty(OWL.sameAs,item.getLabel());
		feature.addProperty(OT.OTProperty.hasSource.createProperty(getJenaModel()), referenceReporter.getURI(item.getReference()));
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
