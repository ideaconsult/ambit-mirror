package ambit2.rest.rdf;

import org.restlet.routing.Template;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFResourceIterator extends RDFObjectIterator<RDFNode> {

	public RDFResourceIterator(OntModel model, String topObject) {
		super(model, topObject);
	}

	@Override
	protected Resource createRecord() {
		return null;
	}

	@Override
	protected Template createTemplate() {
		return null;
	}

	@Override
	protected void parseObjectURI(RDFNode uri, RDFNode record) {
		
	}
	@Override
	protected RDFNode parseRecord(RDFNode newEntry, RDFNode record) {
		return newEntry;
	}
}
