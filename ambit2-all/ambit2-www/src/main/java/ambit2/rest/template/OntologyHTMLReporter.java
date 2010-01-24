package ambit2.rest.template;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.QueryOntology;
import ambit2.rest.AmbitResource;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RESTClient;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.reference.AbstractDOMParser;

/**
 * Reporter for {@link Dictionary} or {@link Property}
 * @author nina
 *
 */
public class OntologyHTMLReporter extends QueryHTMLReporter<Property, IQueryRetrieval<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4281274169475316720L;
	protected int count = 0;
	
	public OntologyHTMLReporter(Request reference,boolean collapsed) {
		super(reference,collapsed);

	}	
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new PropertyURIReporter(request);
	}
	
	public String toURI(Property record) {
		count++;
		//if (count==1) return ""; 
		
		String w = uriReporter.getURI(record);
		
		boolean isDictionary= record.getClazz().equals(Dictionary.class);
		return String.format(
				"<img src=\"%s/images/%s\">%s<a href=\"%s\">%s</a>&nbsp;<br>",

				uriReporter.getBaseReference().toString(),
				isDictionary?"folder.png":"feature.png",
				isDictionary?"":"&nbsp;Feature:&nbsp;",						
				w,
				isDictionary?((Dictionary)record).getTemplate():record.toString()
						);

		
	}	

	@Override
	public Object processItem(Property record) throws AmbitException  {

		try {

			if ( record.getClazz().equals(Dictionary.class) ){
				output.write(toURI(record));
				
				if (!collapsed) {
					AbstractDOMParser parser = new PropertyDOMParser(){
						@Override
						public void handleItem(Property item) throws AmbitException {
							processItem(item);
							
						}
					};				

					
					
					Reference newuri = new Reference(String.format("%s/view/tree", uriReporter.getURI(record)));
					if (!newuri.equals(uriReporter.getRequest().getOriginalRef())) {
						RESTClient client = new RESTClient(parser,null);
						client.process(newuri);
					}
					
							
				}				
				
			} else 
				output.write(toURI(record));
			if (!collapsed) {
/*
				String[] more = new String[] {
						"conformer/all",
						
						"feature/",
						"query/similar/",
						"query/smarts/",
						"model/",
						"dataset/"
						
						};
				for (String m:more)
					output.write(String.format("<a href='%s'>%s</a>&nbsp;",m,m));
*/
			}
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}
	public void header(Writer output, QueryOntology query) {
		try {
			count = 0;
			AmbitResource.writeHTMLHeader(output,
					query.toString()
					,
					uriReporter.getRequest()
					);
			output.write(String.format("<h4>%s</h4>",query.toString()));

		} catch (Exception x) {}		
	};
	public void footer(Writer output, QueryOntology query) {
		try {
			AmbitResource.writeHTMLFooter(output,
					"",
					uriReporter.getRequest()
					);
			output.flush();			
		} catch (Exception x) {}		
	};


}
