package ambit2.rest.structure;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.AmbitResource;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

/**
Generates HTML file with links to structures . TODO - make use of a template engine 
 * @author nina
 *
 * @param <Q>
 */
public class CompoundHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryHTMLReporter<IStructureRecord,Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;

	public CompoundHTMLReporter(Reference reference,boolean collapsed,QueryURIReporter urireporter) {
		super(reference,collapsed);
		this.uriReporter = urireporter;

	}
	public CompoundHTMLReporter(Reference reference,boolean collapsed) {
		super(reference,collapsed);

	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
	}
	@Override
	public void processItem(IStructureRecord record, Writer writer) {

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
	public void header(Writer output, Q query) {
		try {
			AmbitResource.writeHTMLHeader(output,
					collapsed?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getBaseReference()
					);
			output.write(String.format("<h4>%s</h4>",query.toString()));

		} catch (Exception x) {}		
	};
	public void footer(Writer output, Q query) {
		try {
			getOutput().write("</body></html>");
			getOutput().flush();			
		} catch (Exception x) {}		
	};

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public String toURI(IStructureRecord record) {
		String w = uriReporter.getURI(record);

		return String.format(
				"<a href=\"%s\"><img src=\"%s/png\" alt=\"%s\" title=\"%d\"/></a>&nbsp;", 
				w, w, w, record.getIdchemical());

		
	}		

}
