package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Request;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.AmbitResource;

/**
 * Generates HTML output for {@link AlgorithmResource}
 * @author nina
 *
 */
public class AlgorithmHTMLReporter extends AlgorithmURIReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7544605965468875232L;
	protected boolean collapsed = false;
	public AlgorithmHTMLReporter(Request ref, boolean collapsed) {
		super(ref);
		this.collapsed = collapsed;
	}
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest());//,"<meta http-equiv=\"refresh\" content=\"10\">");
			output.write("<table>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Algorithm item, Writer output) {
		try {
			String t = super.getURI(item);
			if (collapsed)
				output.write(String.format("<tr><th align=\"left\"><a href='%s'>%s</a></th><td></td></tr>", t,item.getName()));
			else
				output.write(String.format(
					"<tr><form action=\"\" method=\"POST\"><tr><th><a href='%s'>%s</a></th><td><input type=\"submit\" value=\"Create model\"></td></form></tr>",
					t,item.getName()));
		} catch (Exception x) {
			
		}
	};
	@Override
	public void footer(Writer output, Iterator<Algorithm> query) {
		try {
			output.write("</table>");
			AmbitResource.writeHTMLFooter(output, AlgorithmResource.algorithm, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
