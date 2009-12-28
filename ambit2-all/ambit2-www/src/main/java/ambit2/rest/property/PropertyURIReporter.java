package ambit2.rest.property;

import org.restlet.Request;
import org.restlet.data.Reference;

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
		if (item.getId()>0)
			return String.format("%s%s/%d%s",ref,PropertyResource.featuredef,item.getId(),getDelimiter());
		else
			return String.format("%s%s/%s%s",ref,PropertyResource.featuredef,
					Reference.encode(item.getName()+item.getTitle()),
					getDelimiter());
	}

}
