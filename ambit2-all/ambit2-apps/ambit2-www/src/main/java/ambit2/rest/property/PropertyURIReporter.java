package ambit2.rest.property;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
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

	public PropertyURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public PropertyURIReporter(Request ref,ResourceDoc doc) {
		super(ref,doc);
	}
	public PropertyURIReporter() {
		this((Request)null,null);
	}
	@Override
	public String getURI(String ref, Property record) {
		
		if (record==null) return null;
		boolean isDictionary= record.getClazz().equals(Dictionary.class);
		
		if (isDictionary) {
			return String.format("%s%s/%s/%s%s",
					ref,
					OntologyResource.resource,
					record.getReference()==null?"All":
					Reference.encode(record.getTitle()),
					Reference.encode(record.getName()),
					getDelimiter()
					);
		} else
			return String.format("%s%s%s",ref,record.getRelativeURI(),getDelimiter());

	}

}
