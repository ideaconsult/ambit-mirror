package ambit2.rest.facet;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class FacetHTMLReporter extends QueryHTMLReporter<IFacet, IQueryRetrieval<IFacet>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public FacetHTMLReporter() {
		this(null);
	}
	public FacetHTMLReporter(Request baseRef) {
		super(baseRef,false,null);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new FacetURIReporter<IQueryRetrieval<IFacet>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<IFacet> query) {
		super.header(w, query);
		try {
			w.write(String.format("<table>"));
		//	w.write(String.format("<caption>%s</caption>",query.toString()));
			
			
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	@Override
	public void footer(Writer w, IQueryRetrieval<IFacet> query) {
		try {
			w.write(String.format("</table>"));
		} catch (Exception x) {}
		super.footer(w, query);
	}	
	@Override
	public Object processItem(IFacet item) throws AmbitException  {
		try {
			output.write("<tr>");
			output.write("<td>");
			output.write(String.format(
						"<a href=\"%s\">%s&nbsp;(%d)</a>",
						uriReporter.getURI(item),
						item.getValue(),item.getCount()));
			output.write("</td>");
			output.write("<td>");
			String subcategory = item.getSubCategoryURL(uriReporter.getBaseReference().toString());
			if (subcategory!=null)
				output.write(String.format(
							"<a href=\"%s\">%s</a>",
							subcategory,
							"Subcategory"));
			output.write("</td>");				
			output.write("</tr>");
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

}
