package ambit2.rest.algorithm;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.reporters.CatalogURIReporter;

/**
 * Generates URI from algorithm ID
 * @author nina
 *
 */
public class AlgorithmURIReporter extends CatalogURIReporter<Algorithm> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4802490674242368703L;
	public AlgorithmURIReporter() {
	}
	public AlgorithmURIReporter(Request request) {
		super(request);
	}
	public String getURI(String ref, Algorithm item) {
		return String.format("%s%s/%s",
				ref,
				AllAlgorithmsResource.algorithm,
				item==null?"":Reference.encode(item.getId()));
	}
}
