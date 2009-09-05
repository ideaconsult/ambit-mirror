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
	public PropertyHTMLReporter(Reference ref,boolean collapsed) {
		super(ref,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new PropertyURIReporter(reference);
	}

	@Override
	public void processItem(Property item, Writer output) {
		try {
			output.write(String.format(
						"<a href=\"%s\">%s %s</a>&nbsp;",
						uriReporter.getURI(item),
						item.getName(),
						item.getUnits()));
			if (!collapsed) {
				output.write(String.format("Reference: <a href='%s'>%s</a>",
						item.getReference().getURL(),
						item.getReference().getName()));
			}
			output.write("<br>");
		} catch (Exception x) {
			x.printStackTrace();
		}		
	}
	@Override
	public void header(Writer w, IQueryRetrieval<Property> query) {
		super.header(w, query);
		try {w.write(collapsed?"<h3>Feature definitions</h3>":"<h3>Feature definition</h3>");} catch (Exception x) {}
	}

}
