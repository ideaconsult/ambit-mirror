package ambit2.rest.property;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.template.OntologyResource;

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
	public String getURI(String ref, Property record) {
		
		boolean isDictionary= record.getClazz().equals(Dictionary.class);
		
		if (isDictionary) {
			return String.format("%s%s/%s/%s",
					ref,
					OntologyResource.resource,
					record.getReference()==null?"All":
					Reference.encode(record.getTitle()),
					Reference.encode(record.getName())
					);
		} else
		if (record.getId()>0)
			return String.format("%s%s/%d%s",ref,PropertyResource.featuredef,record.getId(),getDelimiter());
		else
			return String.format("%s%s/%s%s",ref,PropertyResource.featuredef,
					Reference.encode(record.getName()+record.getTitle()),
					getDelimiter());

	}

}
