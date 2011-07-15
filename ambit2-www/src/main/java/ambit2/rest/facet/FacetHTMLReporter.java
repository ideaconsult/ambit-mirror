package ambit2.rest.facet;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class FacetHTMLReporter<Facet extends IFacet> extends QueryHTMLReporter<Facet, IQueryRetrieval<Facet>> {
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
		return new FacetURIReporter(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<Facet> query) {
		super.header(w, query);
		
		try {
			headerBeforeTable(w,query);
			w.write(String.format("<table>"));
		//	w.write(String.format("<caption>%s</caption>",query.toString()));
			
			
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	public void headerBeforeTable(Writer w, IQueryRetrieval<Facet> query) {
		
	}
	@Override
	public void footer(Writer w, IQueryRetrieval<Facet> query) {
		try {
			w.write(String.format("</table>"));
		} catch (Exception x) {}
		super.footer(w, query);
	}	
	@Override
	public Object processItem(Facet item) throws AmbitException  {
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
