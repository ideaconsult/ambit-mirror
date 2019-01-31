package ambit2.rest.ui;

import ambit2.rest.freemarker.FreeMarkerResource;

/**
 * Swagger2
 * @author nina
 *
 */
public class API2docsResource extends FreeMarkerResource {

	public API2docsResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
		public String getTemplateName() {
			return "apidocs2/api.ftl";
		}
}
