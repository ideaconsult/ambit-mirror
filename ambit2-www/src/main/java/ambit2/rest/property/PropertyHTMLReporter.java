package ambit2.rest.property;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

/**
 * HTML for {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyHTMLReporter extends QueryHTMLReporter<Property, IQueryRetrieval<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3196496706491834527L;
	public PropertyHTMLReporter(Reference ref) {
		super(ref);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new PropertyURIReporter();
	}

	@Override
	public void processItem(Property item, Writer output) {
		try {
			output.write(String.format(
						"<a href=\"%s\">%s</a><br>",
						uriReporter.getURI(item),
						item.getName()));
				
		} catch (Exception x) {
			x.printStackTrace();
		}		
	}

}
