package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Reference;

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
	public AlgorithmHTMLReporter(Reference ref, boolean collapsed) {
		super(ref);
		this.collapsed = collapsed;
	}
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", baseReference);//,"<meta http-equiv=\"refresh\" content=\"10\">");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Algorithm item, Writer output) {
		try {
			String t = super.getURI(item);
			if (collapsed)
				output.write(String.format("<a href='%s'>%s</a><br>", t,t));
			else
				output.write(String.format(
					"<form action=\"\" method=\"POST\"><a href='%s'>%s</a><input type=\"submit\" value=\"Create model\"></form>",
					t,t));
		} catch (Exception x) {
			
		}
	};
	@Override
	public void footer(Writer output, Iterator<Algorithm> query) {
		try {
			AmbitResource.writeHTMLFooter(output, AlgorithmCatalogResource.algorithm+"/rules", baseReference);
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
