package ambit2.rest.structure;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
Generates HTML file with links to structures . TODO - make use of a template engine 
 * @author nina
 *
 * @param <Q>
 */
public class QueryHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryReporter<IStructureRecord,Q, Writer> {
	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundURI ;
	protected ConformerURIReporter<IQueryRetrieval<IStructureRecord>> conformerURI;
	protected Reference reference;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;


	public QueryHTMLReporter(Reference reference) {
		super();
		this.reference = reference;
		compoundURI = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
		conformerURI = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
		
	}
	@Override
	public void processItem(IStructureRecord record, Writer writer) {

		try {
			writer.write(toURI(reference,record));
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
	public String toURI(Reference reference, IStructureRecord record) {
		StringWriter w = new StringWriter();
		compoundURI.processItem(record, w);
		return String.format(
				"<a href=\"%s\"><img src=\"%s\" alt=\"%s\" title=\"%s\"/></a>&nbsp;", 
				w.toString(), w.toString(), w.toString(), w.toString());
		
	}		

}
