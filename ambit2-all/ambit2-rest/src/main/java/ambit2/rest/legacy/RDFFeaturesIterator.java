package ambit2.rest.legacy;

import java.io.IOException;
import java.net.MalformedURLException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.rest.rdf.sparql.RDFAbstractFeatureIterator;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


@Deprecated
public class RDFFeaturesIterator extends RDFAbstractFeatureIterator<OTFeature> {

	public RDFFeaturesIterator(String string,String referer) throws ResourceException ,IOException , MalformedURLException{
		super(new Reference(string),referer);
	}
	public RDFFeaturesIterator(Reference reference,String referer) throws ResourceException,IOException , MalformedURLException {
		super(reference,referer);
	}

	@Override
	protected OTFeature newProperty(RDFNode node) {
		try {
		if (node.isURIResource())
			return OTFeature.feature(((Resource)node).getURI(),referer);
		else
			return OTFeature.feature(null,referer);
		} catch (Exception x) {return null;}
	}

	@Override
	protected void setSameAs(OTFeature record, RDFNode node) {
		
	}
	/*
	@Override
	protected void setNameValuePairs(OTFeature record, RDFNode node,
			String title, String units, RDFNode source, RDFNode sameAs) {
		try {
			if (source.isURIResource()) {
				record.withAlgorithm(OTAlgorithm.algorithm(((Resource)source).getURI()));
			}
			record.withName(title);
		} catch (Exception x) {
			
		}
	}
	*/
	@Override
	protected void setUnits(OTFeature record, String units) {
		record.setUnits(units);
	}

	@Override
	protected void setOrigin(OTFeature record, RDFNode node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setName(OTFeature record, String name) {
		record.setName(name);
		
	}
}
