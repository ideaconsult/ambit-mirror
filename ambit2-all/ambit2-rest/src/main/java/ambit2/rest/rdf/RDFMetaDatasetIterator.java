package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Iterates over {@link SourceDataset}
 * @author nina
 *
 */
public class RDFMetaDatasetIterator extends RDFObjectIterator<SourceDataset> {
	protected RDFReferenceIterator refIterator;
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
		return OpenTox.URI.dataset.getTemplate(baseReference);
	}

	@Override
	protected void parseObjectURI(RDFNode uri, SourceDataset record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getURI(uri), vars);
			record.setId(Integer.parseInt(vars.get(OpenTox.URI.dataset.getKey()).toString())); } 
		catch (Exception x) { record.setId(-1);};
		
	}

	@Override
	protected SourceDataset parseRecord(RDFNode newEntry, SourceDataset dataset) {
		if (newEntry.isLiteral()) {
			return dataset;
		} else {
			String id = null;
			try {	dataset.setName(getTitle(newEntry));} catch (Exception x) { dataset.setName(null);}
			try {	id = getURI(newEntry);} catch (Exception x) { }
	
			try { dataset.setUsername(getPropertyValue(DC.publisher, newEntry)); } catch (Exception x) {}
			try {
				
				RDFNode reference = getPropertyNode(RDFS.seeAlso, newEntry);
				if (reference!=null) {
					if (refIterator == null) refIterator = new RDFReferenceIterator(jenaModel);
					ILiteratureEntry ref = refIterator.parseRecord(reference, null);
					dataset.setTitle(ref.getTitle());
					dataset.setURL(ref.getTitle());
				}
				
			} catch (Exception x) {
				dataset.setTitle(id);
				dataset.setURL(id);
			}
			parseObjectURI(newEntry, dataset);
			return dataset;
		}
	}

}
