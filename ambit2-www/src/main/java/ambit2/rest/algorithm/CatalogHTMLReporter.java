package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Reference;

import ambit2.rest.AmbitResource;

/**
 * HTML reporter for {@link AlgorithmResource}
 * @author nina
 *
 * @param <T>
 */
public class CatalogHTMLReporter<T> extends CatalogURIReporter<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public CatalogHTMLReporter(Reference ref) {
		super(ref);
	}
	@Override
	public void header(Writer output, Iterator<T> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", baseReference);//,"<meta http-equiv=\"refresh\" content=\"10\">");
		} catch (Exception x) {
			
		}
	}
	public void processItem(T item, Writer output) {
		try {
			String t = super.getURI(item);
			output.write(String.format("<a href='%s'>%s</a><br>", t,t));
		} catch (Exception x) {
			
		}
	};
	@Override
	public void footer(Writer output, Iterator<T> query) {
		try {
			AmbitResource.writeHTMLFooter(output, AlgorithmCatalogResource.algorithm, baseReference);
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
