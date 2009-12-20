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
import ambit2.db.SourceDataset;
import ambit2.rest.dataset.DatasetResource;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;

public class RDFMetaDatasetIterator extends RDFObjectIterator<SourceDataset> {

	public RDFMetaDatasetIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.Dataset.toString());
	}
		
	public RDFMetaDatasetIterator(Reference reference) throws ResourceException {
		super(reference,OT.OTClass.Dataset.toString());
	}	
	public RDFMetaDatasetIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType,OT.OTClass.Dataset.toString());
	}
	
	public RDFMetaDatasetIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,OT.OTClass.Dataset.toString());
	}	
	public RDFMetaDatasetIterator(OntModel model, StmtIterator recordIterator) {
		super(model,OT.OTClass.Dataset.toString(),recordIterator);
	}
	
	public RDFMetaDatasetIterator(OntModel model) {
		super(model,OT.OTClass.Dataset.toString());
	}	

	@Override
	protected SourceDataset createRecord() {
		return new SourceDataset();
	}

	@Override
	protected Template createTemplate() {
		return new Template(String.format("%s%s/{%s}",baseReference==null?"":baseReference,DatasetResource.dataset,DatasetResource.datasetKey));
	}

	@Override
	protected void parseObjectURI(RDFNode uri, SourceDataset record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getIdentifier(uri), vars);
			record.setId(Integer.parseInt(vars.get(DatasetResource.datasetKey).toString())); } 
		catch (Exception x) { record.setId(-1);};
		
	}

	@Override
	protected SourceDataset parseRecord(Resource newEntry, SourceDataset dataset) {
		String id = newEntry.getURI();
		try {	dataset.setName(getTitle(newEntry));} catch (Exception x) { }
		try {	id = getIdentifier(newEntry);} catch (Exception x) { }

		try { dataset.setUsername(getPropertyValue(DC.publisher, newEntry)); } catch (Exception x) {}
		try {
			ILiteratureEntry ref = RDFReferenceIterator.readReference(jenaModel, newEntry, baseReference,RDFS.seeAlso);
			dataset.setTitle(ref.getTitle());
			dataset.setURL(ref.getTitle());
		} catch (Exception x) {
			dataset.setTitle(id);
			dataset.setURL(id);
		}
		parseObjectURI(newEntry, dataset);
		return dataset;
	}

}
