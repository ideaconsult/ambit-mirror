package ambit2.rest.propertyvalue;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.PropertyValue;
import ambit2.rest.OT;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.structure.CompoundURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * FeatureValue
 * @author nina
 *
 * @param <T>
 */
public class PropertyValueRDFReporter<T> extends QueryRDFReporter<T,IQueryRetrieval<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8129407642828025102L;
	protected ReferenceURIReporter referenceReporter;
	protected PropertyURIReporter propertyReporter;
	protected CompoundURIReporter cmpReporter;
	public PropertyValueRDFReporter(Request req, MediaType mediaType) {
		super(req,mediaType);
		propertyReporter = new PropertyURIReporter(req);
		referenceReporter = new ReferenceURIReporter(req);
	}
	@Override
	protected QueryURIReporter<T, IQueryRetrieval<T>> createURIReporter(
			Request req) {
		return new PropertyValueURIReporter<T, IQueryRetrieval<T>>(req);
	}
	public  Individual jenaProcess(Property property) throws AmbitException {
		Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(property),
				OT.OTClass.Feature.getOntClass(getJenaModel()));
		feature.addProperty(DC.title, property.getName());
		feature.addLiteral(DC.identifier,
				 getJenaModel().createTypedLiteral(propertyReporter.getURI(property),XSDDatatype.XSDanyURI));
		feature.addProperty(OT.DataProperty.units.createProperty(getJenaModel()),property.getUnits());
		feature.addProperty(OWL.sameAs,property.getLabel());
		feature.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()), referenceReporter.getURI(property.getReference()));
		return feature;
	}	
	public  Individual valueProcess(Object item,Individual feature) throws AmbitException {
		Individual featureValue = getJenaModel().createIndividual(OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
		featureValue.addLiteral(OT.DataProperty.value.createProperty(getJenaModel()),getJenaModel().createTypedLiteral(item.toString(),	XSDDatatype.XSDstring));
		if (feature!=null) featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()),feature);
		return featureValue;
	}	
	public  Individual valueProcess(Number item,Individual feature) throws AmbitException {
		Individual featureValue = getJenaModel().createIndividual(OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
		featureValue.addLiteral(OT.DataProperty.value.createProperty(getJenaModel()),getJenaModel().createTypedLiteral(item,	XSDDatatype.XSDdouble));
		if (feature!=null) featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()),feature);
		return featureValue;
	}		
	public  Individual jenaProcess(PropertyValue item) throws AmbitException {
		Individual feature = jenaProcess(item.getProperty());
		return valueProcess(item.getValue(),feature);
	}
	public void jenaProcess(IStructureRecord record) throws AmbitException {
		Individual dataEntry = null;
		if (record != null) {
			dataEntry = getJenaModel().createIndividual(OT.OTClass.DataEntry.getOntClass(getJenaModel()));
			Individual compound = getJenaModel().createIndividual(
				cmpReporter.getURI(record),OT.OTClass.Compound.getOntClass(getJenaModel()));
			dataEntry.addProperty(OT.OTProperty.compound.createProperty(getJenaModel()), compound);			
		}		
		for (Property p : record.getProperties())  {
			Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(p),
					OT.OTClass.Feature.getOntClass(getJenaModel()));
			Individual featureValue = valueProcess(record.getProperty(p),feature);
			if (dataEntry!=null)
				dataEntry.addProperty(OT.OTProperty.values.createProperty(getJenaModel()),featureValue);
		}
	}	
	public void jenaProcess(Object item) throws AmbitException {
		valueProcess(item, null);
		
		
	}	
	@Override
	public void processItem(T item) throws AmbitException {
		if (item instanceof PropertyValue)
			jenaProcess((PropertyValue)item);
		else if (item instanceof IStructureRecord)
			jenaProcess((IStructureRecord)item);	
		else
			jenaProcess(item);
	}


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void header(OntModel jenaModel, IQueryRetrieval<T> query) {
		super.header(output,query);
		OT.OTClass.DataEntry.createOntClass(jenaModel);
		OT.OTClass.Feature.createOntClass(jenaModel);
		OT.OTClass.FeatureValue.createOntClass(jenaModel);
		OT.OTClass.Compound.createOntClass(jenaModel);
		
	}	

}
