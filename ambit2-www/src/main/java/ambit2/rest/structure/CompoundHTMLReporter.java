package ambit2.rest.structure;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
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
	protected ConformerURIReporter<IQueryRetrieval<IStructureRecord>> conformerURI;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;


	public CompoundHTMLReporter(Reference reference) {
		super(reference);
		conformerURI = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(reference);		
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
	}
	@Override
	public void processItem(IStructureRecord record, Writer writer) {

		try {
			writer.write(toURI(record));
		} catch (Exception x) {
			logger.error(x);
		}
	}
	public void header(Writer output, Q query) {
		try {
			getOutput().write(String.format("<html><head><title>%s</title></head><body>","test"));
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
				"<a href=\"%s\"><img src=\"%s\" alt=\"%s\" title=\"%d\"/></a>&nbsp;", 
				w, w, w, record.getIdchemical());

		
	}		

}
