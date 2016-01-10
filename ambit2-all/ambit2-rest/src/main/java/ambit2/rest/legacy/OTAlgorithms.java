package ambit2.rest.legacy;

import org.restlet.data.Reference;

import ambit2.rest.OpenTox;

@Deprecated
public class OTAlgorithms extends OTProcessingContainers<OTAlgorithm> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8743429754894223373L;

	public static OTAlgorithms algorithms(String uri, String referer)
			throws Exception {
		return new OTAlgorithms(uri, referer);
	}

	public OTAlgorithms(String uri, String referer) {
		super(uri,referer);
	}

	@Override
	public OTAlgorithm createItem(Reference uri) throws Exception {
		return OTAlgorithm.algorithm(uri.toString(), referer);
	}

	@Override
	public OTContainers<OTAlgorithm> delete(OTAlgorithm item) throws Exception {
		throw new Exception("not implemented");
	}

	@Override
	protected String getParamName() throws Exception {
		return OpenTox.params.algorithm_uri.toString();
	}

}
