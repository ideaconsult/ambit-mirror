package ambit2.rest.template;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.search.property.QueryOntology;
import ambit2.rest.AmbitResource;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

/**
 * Reporter for {@link Dictionary} or {@link Property}
 * @author nina
 *
 */
public class OntologyHTMLReporter extends QueryHTMLReporter<Object, QueryOntology> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4281274169475316720L;
	protected int count = 0;
	public OntologyHTMLReporter(Reference reference,boolean collapsed) {
		super(reference,collapsed);

	}	
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new OntologyURIReporter(reference);
	}
	
	public String toURI(Object record) {
		count++;
		if (count==1) return ""; 
		
		String w = uriReporter.getURI(record);
		
		return String.format(
				"<img src=\"%s/images/%s\">%s<a href=\"%s\">%s</a>&nbsp;<br>",

				uriReporter.getBaseReference().toString(),
				(record instanceof Dictionary)?"folder.png":"feature.png",
						(record instanceof Dictionary)?"":"&nbsp;Feature:&nbsp;",						

				w,
				(record instanceof Dictionary)?((Dictionary)record).getTemplate():record.toString()
						);

		
	}	
	@Override
	public void processItem(Object record, Writer writer) {

		try {
			writer.write(toURI(record));
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
			logger.error(x);
		}
	}
	public void header(Writer output, QueryOntology query) {
		try {
			count = 0;
			AmbitResource.writeHTMLHeader(output,
					query.toString()
					,
					uriReporter.getBaseReference()
					);
			output.write(String.format("<h4>%s</h4>",query.toString()));

		} catch (Exception x) {}		
	};
	public void footer(Writer output, QueryOntology query) {
		try {
			AmbitResource.writeHTMLFooter(output,
					"",
					uriReporter.getBaseReference()
					);
			getOutput().flush();			
		} catch (Exception x) {}		
	};


}
