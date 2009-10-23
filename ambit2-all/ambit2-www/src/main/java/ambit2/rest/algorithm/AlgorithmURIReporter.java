package ambit2.rest.algorithm;

import org.restlet.data.Request;

import ambit2.core.data.model.Algorithm;

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
		return String.format("%s%salgorithm/rules/%d",ref,"".equals(ref)?"":"/",item.getId());
	}
}
