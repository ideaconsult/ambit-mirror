package ambit2.rest.property;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.property.annotations.PropertyAnnotationResource;
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
	public PropertyHTMLReporter(Request ref,DisplayMode _dmode, ResourceDoc doc,boolean headless) {
		super(ref,_dmode,doc,headless);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new PropertyURIReporter(request);
	}
	
	@Override
	public void header(Writer w, IQueryRetrieval<Property> query) {
		super.header(w, query);
		
		MediaType[] mimes = {

				MediaType.TEXT_URI_LIST,
				//MediaType.TEXT_CSV,
				MediaType.APPLICATION_RDF_XML,
				MediaType.TEXT_RDF_N3,
				};
		String[] image = {
				"link.png",
				//"excel.png",
				"rdf.gif",
				"rdf.gif"
				
		};		
			
		try {
			w.write(_dmode.isCollapsed()?"<h3>Feature":"<h3>Features");
			String q=uriReporter.getResourceRef().getQuery();

			for (int i=0;i<mimes.length;i++) {
				MediaType mime = mimes[i];
				w.write("&nbsp;");

				w.write(String.format(
						//"<a href=\"?%s%smedia=%s\">%s</a>",
						"<a href=\"?%s%smedia=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
						q==null?"":q,
						q==null?"":"&",
						Reference.encode(mime.getName()),
						uriReporter.getBaseReference().toString(),
						image[i],
						mime.getName(),
						mime
						));	
						

			}	
			w.write("</h3>");				
			//--------
			w.write("<table id='features' class=\"datatable\" border='0' cellpadding='0' cellspacing='1'><thead>");
			w.write("<tr><th width='5%'>Find</th><th width='25%'>Name</th><th width='5%'>Units</th><th width='30%'>Same as</th><th>Origin (Dataset, Model or Algorithm)</th><th>Origin Type</th><th>Values of type</th><th>Nominal values</th><th>More</th></tr></thead>");
			w.write("<tbody>");
		} catch (Exception x) {}
	}	

	@Override
	public Object processItem(Property item) throws AmbitException  {
		try {
			if (item==null) return item;
			String uri = uriReporter.getURI(item);
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
						uri,
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
			int index = hasSource.lastIndexOf("/");
			output.write(String.format("<a href='%s'>%s</a>",
					hasSource,index>0?hasSource.substring(index):hasSource
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
			output.write("<td>");
			if (item.getAnnotations()!=null)
				for (PropertyAnnotation a : item.getAnnotations())
					output.write(String.format("%s %s %s.<br>", a.getType(),a.getPredicate(),a.getObject()));
			output.write(String.format("<a href='%s%s'>More</a>",uri,PropertyAnnotationResource.annotation));
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
