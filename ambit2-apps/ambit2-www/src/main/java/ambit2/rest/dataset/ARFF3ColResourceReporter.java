package ambit2.rest.dataset;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.ARFF3ColReporter;
import ambit2.rest.property.PropertyURIReporter;

public class ARFF3ColResourceReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFF3ColReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7827748100581322937L;

	protected PropertyURIReporter reporter;

	public ARFF3ColResourceReporter(Template template, Request request, String urlPrefix) {
		this(template,null,request,urlPrefix);
	}
	public ARFF3ColResourceReporter(Template template,Profile groupedProperties, Request request,String urlPrefix) {
		super(template,groupedProperties);
		setUrlPrefix(urlPrefix);
		reporter = new PropertyURIReporter(request);
	}
	@Override
	protected String getRelationName() {
		return reporter.getBaseReference().toString();
	}
	@Override
	protected String getItemIdentifier(Property p) {
		return String.format("%d", p.getId());
	}

}
