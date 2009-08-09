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
import org.openscience.cdk.CDKConstants;

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
			output.write("<div id=\"div-1\">");

		} catch (Exception x) {}		
	};
	public void footer(Writer output, Q query) {
		try {
			output.write("</div>");
			AmbitResource.writeHTMLFooter(output,
					"",
					uriReporter.getBaseReference()
					);
			getOutput().flush();			
		} catch (Exception x) {}		
	};

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public String toURI(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		b.append("<div id=\"div-1a\"><div id=\"div-1b1\">");
		
		b.append(String.format(
				"<a href=\"%s\"><img src=\"%s/diagram/png\" alt=\"%s\" title=\"%d\"/></a>", 
				w, w, w, record.getIdchemical()));
		b.append("<div id=\"div-1d\">");
		String[][] s = new String[][] {
			{"feature",CDKConstants.CASRN},
			{"feature",CDKConstants.NAMES},
			{"feature_definition",null}
			};
		for (String[] n:s)
		b.append(String.format("%s <a href=\"%s/%s/%s\" target=\"_blank\">%s<a><br>",n[0],w,n[0],n[1]==null?"":n[1],n[1]==null?"All available":n[1]));
		b.append("</div>");
		b.append("</div></div>");
		return b.toString();
	}		

}
