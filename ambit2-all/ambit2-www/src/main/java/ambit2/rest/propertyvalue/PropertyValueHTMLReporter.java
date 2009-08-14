package ambit2.rest.propertyvalue;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.PropertyValue;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.structure.CompoundHTMLReporter;

/**
 * Generates html out of proeprty/value pairs
 * @author nina
 *
 * @param <T>
 */
public class PropertyValueHTMLReporter<T> extends QueryHTMLReporter<T,IQueryRetrieval<T>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1971719947783649465L;
	protected CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	
	public PropertyValueHTMLReporter(Reference baseRef) {
		super(baseRef,true);
		cmp_reporter = new CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>>(baseRef,true);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new PropertyValueURIReporter(reference);
	}
	

	@Override
	public void processItem(T item, Writer output) {
		try {
			if (item instanceof IStructureRecord) {
				IStructureRecord v = (IStructureRecord) item;
				for (Property p : v.getProperties())
					output.write(String.format("%s = %s<br>",p,v.getProperty(p)));
			} else if (item instanceof PropertyValue) {
				PropertyValue v = (PropertyValue) item;
				output.write(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>",v.getProperty().getLabel(),v.getValue()));
			} else	output.write(item.toString());
			
		} catch (Exception x) {
			
		}
		
	}
	@Override
	public void header(Writer w, IQueryRetrieval<T> query) {
		super.header(w, query);
		try {
			
			//cmp_reporter.processItem(query.getFieldname(),w);
			w.write("<table>");
		} catch (Exception x) {}
	}
	@Override
	public void footer(Writer w, IQueryRetrieval<T> query) {
		try {
			w.write("</table>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	
}
