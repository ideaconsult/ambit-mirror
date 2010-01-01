package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;

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
	public CatalogHTMLReporter(Request request) {
		super(request);
	}
	@Override
	public void header(Writer output, Iterator<T> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest());//,"<meta http-equiv=\"refresh\" content=\"10\">");
		} catch (Exception x) {
			
		}
	}
	public void processItem(T item, Writer output) {
		try {
			String t = super.getURI(item);
			output.write(String.format("<a href='%s'>%s</a><br>", t,item));
		} catch (Exception x) {
			
		}
	};
	@Override
	public void footer(Writer output, Iterator<T> query) {
		try {
			AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
