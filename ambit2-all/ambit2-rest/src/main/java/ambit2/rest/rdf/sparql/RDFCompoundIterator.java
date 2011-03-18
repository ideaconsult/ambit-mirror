package ambit2.rest.rdf.sparql;

import java.io.InputStream;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFCompoundIterator extends RDFAbstractCompoundIterator<IStructureRecord,Property> {
	public RDFCompoundIterator(Representation representation, MediaType mediaType) throws ResourceException {
		super(representation,mediaType);
	}
	
	public RDFCompoundIterator(Reference reference) throws ResourceException {
		super(reference);
	}
	
	public RDFCompoundIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType);
	}
	
	public RDFCompoundIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType);
	}	
	public RDFCompoundIterator(OntModel model, String sparql) throws ResourceException {
		super(model,sparql);

	}
	@Override
	protected RDFAbstractFeatureIterator<Property> getPropertyIterator(
			OntModel jenaModel) {
		return new RDFFeatureIterator(jenaModel);
	}

	protected void setCompoundIDS(IStructureRecord compound, int idchemical,
			int idstructure) {
		
		record.setIdchemical(idchemical);
		record.setIdstructure(idstructure);	
	}

	protected Property newProperty(com.hp.hpl.jena.rdf.model.RDFNode node) {
		return new Property(((Resource)node).getURI());
	}

	@Override
	protected void setValue(IStructureRecord record, Property property,
			double value) {
		record.setProperty(property, value);
		
	}

	@Override
	protected void setValue(IStructureRecord record, Property property,
			float value) {
		record.setProperty(property, value);
		
	}

	@Override
	protected void setValue(IStructureRecord record, Property property,
			int value) {
		record.setProperty(property, value);
		
	}

	@Override
	protected void setValue(IStructureRecord record, Property property,
			String value) {
		record.setProperty(property, value);
		
	}

	@Override
	protected IStructureRecord newCompound(RDFNode node) {
		return new StructureRecord();
	};

}
	
