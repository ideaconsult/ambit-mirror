package ambit2.rest.property;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitResource;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.query.QueryResource;

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
	public PropertyHTMLReporter(Request ref,boolean collapsed, ResourceDoc doc) {
		super(ref,collapsed,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new PropertyURIReporter(request,doc);
	}
	
	@Override
	public void header(Writer w, IQueryRetrieval<Property> query) {
		super.header(w, query);
		try {w.write(collapsed?"<h3>Feature</h3>":"<h3>Features</h3>");
			w.write(AmbitResource.jsTableSorter("features","pager"));
			w.write("<table width='80%' id='features' class=\"tablesorter\" border='0' cellpadding='0' cellspacing='1'><thead>");
			w.write("<tr><th width='5%'>Find</th><th width='25%'>Name</th><th width='5%'>Units</th><th width='30%'>Same as</th><th>Origin (Dataset, Model or Algorithm)</th><th>Origin Type</th><th>Values of type</th><th>Nominal values</th></tr></thead>");
			w.write("<tbody>");
		} catch (Exception x) {}
	}	

	@Override
	public Object processItem(Property item) throws AmbitException  {
		try {
			output.write("<tr>");
			output.write("<td width='5%'>");
			output.write(String.format("<a href='%s/%s?%s=%s&condition=regexp'><img src=\"%s/images/search.png\" border='0' alt='Find' title='Find property by name'></a>",
					uriReporter.getBaseReference(),
					OpenTox.URI.feature.toString(),
					QueryResource.search_param,
					Reference.encode(item.getName()),
					uriReporter.getBaseReference()));
			output.write("</td>");
			output.write("<td width='25%'  class=\"rowwhite\">");
			output.write(String.format(
						"<a href=\"%s\">%s</td><td width='5%%'>%s</a>",
						uriReporter.getURI(item),
						item.getName(),
						item.getUnits()));
			output.write("</td>");
			output.write("<td width='30%'>");
			output.write(String.format("<a href='%s/%s?sameas=%s'>%s</a>",
						uriReporter.getBaseReference(),
						OpenTox.URI.feature.toString(),
						Reference.encode(item.getLabel()),
						item.getLabel()));
			output.write("</td>");
			output.write("<td>");			
			String hasSource = PropertyRDFReporter.hasSourceURI(item, uriReporter);
			output.write(String.format("<a href='%s'>%s</a>",
					hasSource,hasSource
					));	
			output.write("</td>");
			output.write("<td>");			
			output.write(item.getReference().getType().name());	
			output.write("</td>");			
			
			output.write("<td>");
			output.write(item.getClazz()==null?"":
					String.class.equals(item.getClazz())?"String":
					Number.class.equals(item.getClazz())?"Numeric":item.getClazz().toString());
			output.write("</td>");			
			output.write("<td>");
			output.write(item.isNominal()?"YES":"NO");
			output.write("</td>");
			output.write("</tr>");
		} catch (Exception x) {
			Context.getCurrentLogger().warning(x.getMessage());
		}		
		return null;
	}

	@Override
	public void footer(Writer w, IQueryRetrieval<Property> query) {
		try {
			w.write("</tbody></table>");
		} catch (Exception x) {}
			super.footer(output, query);
	}


}
