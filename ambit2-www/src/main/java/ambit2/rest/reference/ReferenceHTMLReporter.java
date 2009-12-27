package ambit2.rest.reference;

import java.io.Writer;

import org.restlet.data.Request;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

public class ReferenceHTMLReporter extends QueryHTMLReporter<ILiteratureEntry, IQueryRetrieval<ILiteratureEntry>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public ReferenceHTMLReporter() {
		this(null,true);
	}
	public ReferenceHTMLReporter(Request baseRef, boolean collapsed) {
		super(baseRef,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ILiteratureEntry> query) {
		super.header(w, query);
		try {w.write(String.format("<h3>Reference%s</h3>",collapsed?"s":""));} catch (Exception x) {}
	}
	@Override
	public Object processItem(ILiteratureEntry item) throws AmbitException  {
		try {
			output.write("<div>");
			output.write("Name: ");
			output.write(String.format(
						"<a href=\"%s\">%s</a>",
						uriReporter.getURI(item),
						item.getName()));
			if (!collapsed) {
				output.write("&nbsp;");
				output.write("URL: ");
				output.write(String.format(
						"<a href=\"%s\" target='_blank'>%s</a><br>",
						item.getURL(),
						item.getURL()));	
			}
			output.write("</div>");	
		} catch (Exception x) {
			
		}
		return null;
	}

}
