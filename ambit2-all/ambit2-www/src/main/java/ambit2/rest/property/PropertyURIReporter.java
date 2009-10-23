package ambit2.rest.property;

import org.restlet.data.Request;

import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;

/**
 * Generates uri of {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyURIReporter extends QueryURIReporter<Property, IQueryRetrieval<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 711954935147788056L;

	public PropertyURIReporter(Request ref) {
		super(ref);
	}
	public PropertyURIReporter() {
		this(null);
	}
	
	@Override
	public String getURI(String ref, Property item) {
		return String.format("%s%s/%d%s",ref,PropertyResource.featuredef,item.getId(),getDelimiter());
	}

}
