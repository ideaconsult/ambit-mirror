package ambit2.rest.legacy;

import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.rest.OpenTox;

/**
 * List of {@link OTFeature}
 * 
 * @author nina
 * 
 */
@Deprecated
public class OTFeatures extends OTProcessingContainers<OTFeature> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5711791565860384883L;

	public static OTFeatures features(String uri, String referer)
			throws Exception {
		return new OTFeatures(uri, referer);
	}

	public OTFeatures(String uri, String referer) {
		super(uri, referer);
	}

	@Override
	public OTFeature createItem(Reference uri) throws Exception {
		return OTFeature.feature(uri.toString(), referer);
	}

	@Override
	public OTContainers<OTFeature> delete(OTFeature item) throws Exception {
		throw new Exception("not implemented");
	}

	@Override
	protected String getParamName() throws Exception {
		return OpenTox.params.feature_uris.toString();
	}

	public Form getQuery(Form form) throws Exception {
		if (getItems() == null)
			return null;
		for (OTFeature feature : getItems()) {
			if (form == null)
				form = new Form();
			form.add(getParamName(), feature.getUri().toString());
		}
		return form;
	}

	public OTFeatures readRDF(String uri, String referer) throws Exception {
		try {

			RDFFeaturesIterator iterator = new RDFFeaturesIterator(uri, referer);
			while (iterator.hasNext())
				add(iterator.next());
			return this;
		} catch (Exception x) {
			throw new Exception(x.getMessage());
		} finally {
		}
	}

}
