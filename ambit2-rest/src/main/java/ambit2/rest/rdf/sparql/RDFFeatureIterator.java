package ambit2.rest.rdf.sparql;

import java.io.IOException;
import java.net.MalformedURLException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFFeatureIterator extends RDFAbstractFeatureIterator<Property> {


	public RDFFeatureIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		super(reference);
	}
	public RDFFeatureIterator(Model jenaModel) {
		super(jenaModel);
	}
	@Override
	protected Property newProperty(RDFNode node) {
		return new Property("");
	}

	@Override
	protected void setName(Property record, String name) {
		record.setName(name);
	}

	@Override
	protected void setOrigin(Property record, RDFNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setSameAs(Property record, RDFNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setUnits(Property record, String units) {
		record.setUnits(units);
		
	}
}
