package ambit2.rest.dataset;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Retrieves metadata of the dataset in RDF format
 * @author nina
 *
 * @param <Q>
 */
public class MetadataRDFReporter<M extends ISourceDataset,Q extends IQueryRetrieval<M>> extends QueryRDFReporter<M,Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6747452583425280704L;
	protected ReferenceURIReporter referenceReporter;
	public MetadataRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType);
		referenceReporter = new ReferenceURIReporter(request);
	}
	
	@Override
	protected QueryURIReporter<M, IQueryRetrieval<M>> createURIReporter(
			Request req,ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<M>,M>(req);
	}
	public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
		OT.OTClass.Dataset.createOntClass(output);
	};
	@Override
	public Object processItem(M item) throws AmbitException {
		return addToModel(output,(ISourceDataset) item,  uriReporter.getURI(item));
	}
	
	public static Individual addToModel(OntModel output,ISourceDataset item,String uri) throws AmbitException {
		Individual dataset = output.createIndividual(uri,
				OT.OTClass.Dataset.getOntClass(output));
		
		if (item.getName()!=null) dataset.addProperty(DC.title,item.getName());
		
		if (item.getSource()!=null) dataset.addProperty(DC.source,item.getSource());
		
		if (item.getLicenseURI()!=null) {
			Property rights = DCTerms.rights;
			for (ISourceDataset.license l : ISourceDataset.license.values())
				if (l.getURI().equals(item.getLicenseURI())) {
					rights = DCTerms.license;
					break;
				}
			if (!item.getLicenseURI().startsWith("http")) {
				item.setLicenseURI(String.format("http://ambit.sf.net/resolver/rights/%s",Reference.encode(item.getLicenseURI())));
			}
			Resource licenseNode = output.createResource(item.getLicenseURI());
			dataset.addProperty(rights,licenseNode);
		} else {
			//dataset.addProperty(DCTerms.license,ISourceDataset.license.Unknown.toString());
		}
		

		
		if (item.getrightsHolder()!=null) {
			Resource rightsNode = output.createResource(item.getrightsHolder());
			dataset.addProperty(DCTerms.rightsHolder,rightsNode);
		} else {
			//dataset.addProperty(DCTerms.rightsHolder,"Unknown");
		}
		
		if (item instanceof SourceDataset) {
			SourceDataset ditem = (SourceDataset) item;
			//Individual ref = ReferenceRDFReporter.addToModel(output, ditem.getReference(), referenceReporter);
			//dataset.addProperty(RDFS.seeAlso,ref);
			dataset.addProperty(RDFS.seeAlso,ditem.getURL());

		}
		return dataset;
	}

	public void open() throws DbAmbitException {
		
	}

}
