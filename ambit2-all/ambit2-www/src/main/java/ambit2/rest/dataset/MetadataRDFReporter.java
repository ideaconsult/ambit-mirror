package ambit2.rest.dataset;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OT;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Retrieves metadata of the dataset in RDF format
 * @author nina
 *
 * @param <Q>
 */
public class MetadataRDFReporter<Q extends IQueryRetrieval<SourceDataset>> extends QueryRDFReporter<SourceDataset,Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6747452583425280704L;
	
	public MetadataRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType);
	}

	@Override
	protected QueryURIReporter<SourceDataset, IQueryRetrieval<SourceDataset>> createURIReporter(
			Request req) {
		return new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(req);
	}
	public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
		OT.OTClass.Dataset.createOntClass(output);
	};
	@Override
	public void processItem(SourceDataset item) throws AmbitException {
		Individual dataset = output.createIndividual(uriReporter.getURI(item),
				OT.OTClass.Dataset.getOntClass(output));
		
		dataset.addProperty(DC.identifier, uriReporter.getURI(item));
		dataset.addProperty(DC.title,item.getName());
		dataset.addProperty(RDFS.seeAlso,item.getURL());
		dataset.addProperty(DC.publisher,item.getUsername());
		dataset.addProperty(DC.source,item.getTitle());
		
	}

	public void open() throws DbAmbitException {
		
	}

}
