package ambit2.rest.algorithm;

import org.restlet.data.Reference;

import ambit2.core.data.model.Algorithm;

public class AlgorithmURIReporter extends CatalogURIReporter<Algorithm> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4802490674242368703L;
	public AlgorithmURIReporter() {
	}
	public AlgorithmURIReporter(Reference baseRef) {
		super(baseRef);
	}
	public String getURI(String ref, Algorithm item) {
		return String.format("%s%salgorithm/rules/%s",ref,"".equals(ref)?"":"/",item.toString());
	}
}
