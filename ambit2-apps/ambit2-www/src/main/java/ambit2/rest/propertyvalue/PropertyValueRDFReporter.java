package ambit2.rest.propertyvalue;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.structure.CompoundURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * FeatureValue
 * 
 * @author nina
 * 
 * @param <T>
 */
public class PropertyValueRDFReporter<T> extends QueryRDFReporter<T, IQueryRetrieval<T>> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8129407642828025102L;
    protected ReferenceURIReporter referenceReporter;
    protected PropertyURIReporter propertyReporter;
    protected CompoundURIReporter cmpReporter;

    public PropertyValueRDFReporter(Request req, MediaType mediaType) {
	super(req, mediaType);
	propertyReporter = new PropertyURIReporter(req);
	referenceReporter = new ReferenceURIReporter(req);
    }

    @Override
    protected QueryURIReporter<T, IQueryRetrieval<T>> createURIReporter(Request req, ResourceDoc doc) {
	return new PropertyValueURIReporter<T, IQueryRetrieval<T>>(req);
    }

    public Individual jenaProcess(Property property) throws AmbitException {
	return PropertyRDFReporter.addToModel(getJenaModel(), property, propertyReporter, referenceReporter);

    }

    public Individual valueProcess(Object item, Individual feature) throws AmbitException {
	Individual featureValue = getJenaModel().createIndividual(OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
	featureValue.addLiteral(OT.DataProperty.value.createProperty(getJenaModel()), getJenaModel()
		.createTypedLiteral(item.toString(), XSDDatatype.XSDstring));
	if (feature != null)
	    featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()), feature);
	return featureValue;
    }

    public Individual valueProcess(Number item, Individual feature) throws AmbitException {
	Individual featureValue = getJenaModel().createIndividual(OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
	featureValue.addLiteral(OT.DataProperty.value.createProperty(getJenaModel()), getJenaModel()
		.createTypedLiteral(item, XSDDatatype.XSDdouble));
	if (feature != null)
	    featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()), feature);
	return featureValue;
    }

    public Individual jenaProcess(PropertyValue item) throws AmbitException {
	Individual feature = jenaProcess(item.getProperty());
	return valueProcess(item.getValue(), feature);
    }

    public Individual jenaProcess(IStructureRecord record) throws AmbitException {
	Individual dataEntry = null;
	if (record != null) {
	    dataEntry = getJenaModel().createIndividual(OT.OTClass.DataEntry.getOntClass(getJenaModel()));
	    Individual compound = getJenaModel().createIndividual(cmpReporter.getURI(record),
		    OT.OTClass.Compound.getOntClass(getJenaModel()));
	    dataEntry.addProperty(OT.OTProperty.compound.createProperty(getJenaModel()), compound);
	}
	for (Property p : record.getRecordProperties()) {
	    Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(p),
		    OT.OTClass.Feature.getOntClass(getJenaModel()));
	    Individual featureValue = valueProcess(record.getRecordProperty(p), feature);
	    if (dataEntry != null)
		dataEntry.addProperty(OT.OTProperty.values.createProperty(getJenaModel()), featureValue);
	}
	return dataEntry;
    }

    public Object jenaProcess(Object item) throws AmbitException {
	return valueProcess(item, null);

    }

    @Override
    public Object processItem(T item) throws AmbitException {
	if (item instanceof PropertyValue)
	    return jenaProcess((PropertyValue) item);
	else if (item instanceof IStructureRecord)
	    return jenaProcess((IStructureRecord) item);
	else
	    return jenaProcess(item);
    }

    public void open() throws DbAmbitException {
	// TODO Auto-generated method stub

    }

    @Override
    public void header(OntModel jenaModel, IQueryRetrieval<T> query) {
	super.header(output, query);
	OT.OTClass.DataEntry.createOntClass(jenaModel);
	OT.OTClass.Feature.createOntClass(jenaModel);
	OT.OTClass.FeatureValue.createOntClass(jenaModel);
	OT.OTClass.Compound.createOntClass(jenaModel);

    }

}
