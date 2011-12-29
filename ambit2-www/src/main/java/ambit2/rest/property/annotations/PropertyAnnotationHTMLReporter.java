package ambit2.rest.property.annotations;

import java.io.Writer;

import org.opentox.rdf.OT;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.PropertyAnnotation;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitResource;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.DisplayMode;

/**
 * HTML for {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyAnnotationHTMLReporter extends QueryHTMLReporter<PropertyAnnotation, IQueryRetrieval<PropertyAnnotation>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3196496706491834527L;
	public PropertyAnnotationHTMLReporter(Request ref,DisplayMode _dmode, ResourceDoc doc) {
		super(ref,_dmode,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		PropertyAnnotationURIReporter r = new PropertyAnnotationURIReporter(request,doc);
		r.setPropertyOnly(true);
		return r;
	}
	
	@Override
	public void header(Writer w, IQueryRetrieval<PropertyAnnotation> query) {
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
			w.write("<h3>Additional information of a feature");
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
			w.write(AmbitResource.jsTableSorter("features","pager"));
			w.write("<table width='80%' id='features' class=\"tablesorter\" border='0' cellpadding='0' cellspacing='1'><thead>");
			w.write(String.format("<thead><tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr></thead>",
					"Feature",
					"Type",
					"Predicate",
					"Object"
					));
			w.write("<tbody>");
		} catch (Exception x) {}
	}	

	@Override
	public Object processItem(PropertyAnnotation item) throws AmbitException  {
		try {
			output.write("<tr>");
			String propertyURI = uriReporter.getURI(item);
			
			String object = item.getObject().toString();
			if (item.getType().equals(OT.OTClass.ModelConfidenceFeature.name())) {
				if (!object.startsWith("http")) object = String.format("%s%s",uriReporter.getBaseReference(),object);
			} 	
			
			output.write(String.format("<td><a href='%s' title='Feature'>%s</a></td><td>%s</td><td>%s</td><td>%s</td>",
					propertyURI,
					propertyURI,
					item.getType(),
					item.getPredicate(),
					object)
					);

			output.write("</tr>");
		} catch (Exception x) {
			Context.getCurrentLogger().warning(x.getMessage());
		}		
		return null;
	}

	@Override
	public void footer(Writer w, IQueryRetrieval<PropertyAnnotation> query) {
		try {
			w.write("</tbody></table>");
		} catch (Exception x) {}
			super.footer(output, query);
	}


}
