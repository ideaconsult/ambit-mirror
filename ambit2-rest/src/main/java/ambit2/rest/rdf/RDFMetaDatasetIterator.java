package ambit2.rest.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.opentox.rdf.OT;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.AbstractDataset;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Iterates over {@link SourceDataset}
 * @author nina
 *
 */
public class RDFMetaDatasetIterator extends RDFObjectIterator<ISourceDataset> {
	protected RDFReferenceIterator refIterator;
	public RDFMetaDatasetIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.Dataset.toString());
	}
		
	public RDFMetaDatasetIterator(Reference reference) throws ResourceException  , MalformedURLException,IOException{
		super(reference,OT.OTClass.Dataset.toString());
	}	
	public RDFMetaDatasetIterator(Reference reference,MediaType mediaType) throws ResourceException , MalformedURLException,IOException{
		super(reference,mediaType,OT.OTClass.Dataset.toString());
	}
	
	public RDFMetaDatasetIterator(InputStream in,MediaType mediaType) throws ResourceException , MalformedURLException,IOException {
		super(in,mediaType,OT.OTClass.Dataset.toString());
	}	
	public RDFMetaDatasetIterator(Model model, StmtIterator recordIterator) {
		super(model,OT.OTClass.Dataset.toString(),recordIterator);
	}
	
	public RDFMetaDatasetIterator(Model model) {
		super(model,OT.OTClass.Dataset.toString());
	}	

	@Override
	protected ISourceDataset createRecord() {
		return new SourceDataset();
	}

	@Override
	protected Template createTemplate() {
		return OpenTox.URI.dataset.getTemplate(baseReference);
	}

	@Override
	protected void parseObjectURI(RDFNode uri, ISourceDataset record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getURI(uri), vars);
			record.setID(Integer.parseInt(vars.get(OpenTox.URI.dataset.getKey()).toString())); } 
		catch (Exception x) { record.setID(-1);};
		
	}

	@Override
	protected ISourceDataset parseRecord(RDFNode newEntry, ISourceDataset dataset) {
		if (newEntry.isLiteral()) {
			return dataset;
		} else {
			//parseObjectURI(newEntry, dataset);  inline here
			Map<String, Object> vars = new HashMap<String, Object>();
			
			try {
				getTemplate().parse(getURI(newEntry), vars);
				dataset.setID(Integer.parseInt(vars.get(OpenTox.URI.dataset.getKey()).toString())); 
			} catch (Exception x) { //we have these two types of datasets, stored in two different kind of tables
				if (vars.get(OpenTox.URI.dataset.getKey()).toString().startsWith("R")) try {
					dataset = new AbstractDataset();
					dataset.setID(Integer.parseInt(vars.get(OpenTox.URI.dataset.getKey()).toString().substring(1)));
				} catch (Exception xx) { 
					dataset = new AbstractDataset();
				}	
			};
			//end of inline parseObjectURI(newEntry, dataset);
			String id = null;
			try {	dataset.setName(getTitle(newEntry));} catch (Exception x) { dataset.setName(null);}
			try {	id = getURI(newEntry);} catch (Exception x) { }
			try { dataset.setSource(getPropertyValue(DC.source, newEntry)); } catch (Exception x) {}
			try { 
				String license = null;
				if (newEntry.isResource()) {
					Statement st = ((Resource)newEntry).getProperty(DCTerms.license);
					RDFNode licenseNode = st.getObject();
					license = licenseNode.isLiteral()?((Literal)licenseNode).getString():((Resource)licenseNode).getURI();
				} else throw new Exception("Not a resource");
				if (license!=null) dataset.setLicenseURI(license); 
			}catch (Exception x) {}

			try { 
				String rights = null;
				if (newEntry.isResource()) {
					Statement st = ((Resource)newEntry).getProperty(DCTerms.rightsHolder);
					RDFNode rightsNode = st.getObject();
					rights = rightsNode.isLiteral()?((Literal)rightsNode).getString():((Resource)rightsNode).getURI();
				} else throw new Exception("Not a resource");
				if (rights!=null) dataset.setrightsHolder(rights); 
			}catch (Exception x) {}
			
			if (dataset instanceof SourceDataset) {
				SourceDataset srcdataset = (SourceDataset) dataset;
				try { srcdataset.setUsername(getPropertyValue(DC.publisher, newEntry)); } catch (Exception x) {}
				try {
					
					RDFNode reference = getPropertyNode(RDFS.seeAlso, newEntry);
					if (reference!=null) {
						srcdataset.setURL(reference.isResource()?((Resource)reference).getURI():
							reference.isLiteral()?reference.asLiteral().getString():reference.toString());
					}
					
				} catch (Exception x) {
					srcdataset.setTitle(id);
					srcdataset.setURL(id);
				}
			}
			//parseObjectURI(newEntry, dataset);
			return dataset;
		}
	}

}

