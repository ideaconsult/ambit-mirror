package ambit2.rest.template;

import org.restlet.data.Reference;
import org.restlet.data.Request;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class OntologyURIReporter extends QueryURIReporter<Object, IQueryRetrieval<Object>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5978886153193570408L;
	protected PropertyURIReporter reporter;
	public OntologyURIReporter(Request ref) {
		super(ref);
		reporter = new PropertyURIReporter(ref);
	}
	@Override
	public String getURI(String ref, Object item) {
		if (item instanceof Dictionary)
			return String.format("%s%s/%s",
					ref,
					OntologyResource.resource,
					Reference.encode(((Dictionary)item).getTemplate().toString())
					);
		else if (item instanceof Property) 
			return reporter.getURI((Property)item);
		else return item.toString();
	}

}
