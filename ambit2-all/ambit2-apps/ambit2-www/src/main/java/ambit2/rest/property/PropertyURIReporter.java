package ambit2.rest.property;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;

/**
 * Generates uri of {@link PropertyResource}
 * 
 * @author nina
 * 
 */
public class PropertyURIReporter extends
		QueryURIReporter<Property, IQueryRetrieval<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 711954935147788056L;
	public static String resource = "/template";

	public PropertyURIReporter(Reference baseRef) {
		super(baseRef);
	}

	public PropertyURIReporter(Request ref) {
		super(ref);
	}

	@Override
	public String getURI(String ref, Property record) {

		if (record == null)
			return null;
		boolean isDictionary = record.getClazz().equals(Dictionary.class);

		if (isDictionary) {
			return String.format(
					"%s%s/%s/%s%s",
					ref,
					resource,
					record.getReference() == null ? "All" : Reference
							.encode(record.getTitle()), Reference.encode(record
							.getName()));
		} else
			return String.format("%s%s", ref, record.getRelativeURI());

	}

}
