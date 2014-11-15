package ambit2.rest.reference;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.ILiteratureEntry;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;

public class ReferenceHTMLReporter extends QueryHTMLReporter<ILiteratureEntry, IQueryRetrieval<ILiteratureEntry>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public ReferenceHTMLReporter() {
		this(null,DisplayMode.table);
	}
	public ReferenceHTMLReporter(Request baseRef, DisplayMode _dmode) {
		super(baseRef,_dmode,null);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ILiteratureEntry> query) {
		super.header(w, query);
		try {w.write(String.format("<h3>Reference%s</h3>",_dmode.isCollapsed()?"s":""));} catch (Exception x) {}
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
			if (!_dmode.isCollapsed()) {
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
