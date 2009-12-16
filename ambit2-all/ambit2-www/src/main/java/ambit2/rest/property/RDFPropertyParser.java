package ambit2.rest.property;

import ambit2.base.data.Property;
import ambit2.rest.OT;
import ambit2.rest.RDFBatchParser;
import ambit2.rest.OT.OTClass;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Parsers RDF representation of Feature
 * @author nina
 *
 */
public class RDFPropertyParser extends RDFBatchParser<Property> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1257623392289416245L;

	public RDFPropertyParser(String baseReference) {
		super(baseReference, OT.OTClass.Feature.toString());
	}

	@Override
	protected Property createRecord() {
		return null;
	}

	@Override
	protected Property parseRecord(Resource newEntry, Property record) {
		RDFNode value = newEntry.getProperty(DC.title).getObject();
		if (value.isLiteral()) {
			record = new Property(((Literal)value).getString());
		} else {
			//value = newEntry.getProperty(DC.identifier).getObject();
			record = new Property(value.toString());
		}
		try {	record.setUnits(((Literal)newEntry.getProperty(
				OT.DataProperty.units.createProperty(jenaModel)).getObject()).getString()); } catch (Exception x) {}

		RDFNode label = newEntry.getProperty(OWL.sameAs).getObject();
		record.setLabel(label==null?record.getName():label.toString());
		return record;
	}
/*
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
 */
}
