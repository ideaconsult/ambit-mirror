package ambit2.rest.template;

import org.restlet.Request;
import org.restlet.data.Reference;

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
	public String getURI(String ref, Object record) {
		
		boolean isDictionary=(record instanceof Dictionary) || 
		((record instanceof Property) && ((Property)record).getClazz().equals(Dictionary.class));
		
		if (isDictionary) {
			Dictionary dict = (Dictionary)record;
			return String.format("%s%s/%s/%s",
					ref,
					OntologyResource.resource,
					dict.getParentTemplate()==null?"All":
					Reference.encode(((Dictionary)record).getParentTemplate().toString()),
					Reference.encode(((Dictionary)record).getTemplate().toString())
					);
		} else 
			return reporter.getURI((Property)record);
		//else return item.toString();
		//else return "";
	}

}
