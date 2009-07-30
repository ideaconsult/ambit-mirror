package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * Generates HTML representation of a resource
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryHTMLReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,Writer>  {
	protected QueryURIReporter uriReporter;
	/**
	 * 
	 */
	private static final long serialVersionUID = 16821411854045588L;
	
	/**
	 * 
	 */
	public QueryHTMLReporter() {
		this(null);
	}
	public QueryHTMLReporter(Reference baseRef) {
		uriReporter =  createURIReporter(baseRef);
	}
	protected abstract QueryURIReporter createURIReporter(Reference reference);
	
	@Override
	public void header(Writer w, Q query) {
		try {
			w.write(
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				);
			w.write(String.format("<html><head><title>%s</title></head>\n",query.toString()));
			w.write("<body>\n");

			w.write("<p>");
			} catch (IOException x) {}
	}
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n<table width='100%'>");
			output.write("<td colspan=\"2\" align=\"right\">");
			output.write("<font color='#D6DFF7'>");
			output.write("Developed by Ideaconsult Ltd. (2005-2009)"); 
			output.write("</font>");
			output.write("</td>");
			output.write("</tr>");
			output.write("<tr>");
			output.write("<td colspan=\"2\" align=\"right\">");
			output.write("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
			output.write("    <IMG SRC=\"images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">");
			output.write("  </A>&nbsp; ");

			output.write("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
			output.write("    <IMG SRC=\"images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">");
			output.write("  </A>");
			output.write("</td>");
			output.write("</tr>");
			output.write("</table>");
			output.write("</body>");
			output.write("</html>");
			getOutput().flush();
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		
	}	
}
