package ambit2.rest.facet;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;

public class FacetHTMLReporter<Facet extends IFacet> extends QueryHTMLReporter<Facet, IQueryRetrieval<Facet>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public FacetHTMLReporter() {
		this(null);
	}
	public FacetHTMLReporter(Request baseRef) {
		super(baseRef,DisplayMode.singleitem,null);
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
			//w.write(AmbitResource.jsTableSorter("facet","pager"));
			w.write(String.format("<table class='datatable' id='facet' border=\"0\" cellpadding=\"0\" cellspacing=\"1\""));
			w.write(String.format("<caption CLASS=\"zebra\"></caption>",query.toString()));
			w.write(String.format("<thead><th>%s</th><th>Count</th><th></th></thead>",query.toString()));
			w.write("<tbody>");
			
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	}
	public void headerBeforeTable(Writer w, IQueryRetrieval<Facet> query) {
		
	}
	@Override
	public void footer(Writer w, IQueryRetrieval<Facet> query) {
		try {
			w.write("</tbody>");
			w.write(String.format("</table>"));
		} catch (Exception x) {}
		super.footer(w, query);
	}	
	@Override
	public Object processItem(Facet item) throws AmbitException  {
		try {
			output.write("<tr>");
			output.write("<td>");
			output.write(item.getValue().toString());
			output.write("</td>");
			
			output.write("<td>");
			String uri = uriReporter.getURI(item);
			String d = uri.indexOf("?")>0?"&":"?";
			
			output.write(String.format(
						"<a href=\"%s%spage=0&pagesize=100\">%d</a>",
						uri,d,
						item.getCount()));
			output.write("</td>");
			output.write("<td>");
			String subcategory = item.getSubCategoryURL(uriReporter.getBaseReference().toString());
			if (subcategory!=null)
				output.write(String.format(
							"<a href=\"%s\">%s</a>",
							subcategory,
							item.getSubcategoryTitle()==null?"Subcategory":item.getSubcategoryTitle()));
			else if (item.getSubcategoryTitle()!=null) output.write(item.getSubcategoryTitle());
			output.write("</td>");				
			output.write("</tr>");
			
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

}
