package ambit2.rest.ui;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.freemarker.FreeMarkerResource;
import freemarker.template.Configuration;
import net.idea.restnet.i.aa.OpenSSOCookie;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

/**
 * Swagger2
 * 
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

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));

	}

	@Override
	protected Representation getHTMLByTemplate(Variant variant)
			throws ResourceException {
		Map<String, Object> map = new HashMap<String, Object>();
		configureTemplateMap(map, getRequest(),
				(IFreeMarkerApplication) getApplication());
		return toRepresentation(map, getTemplateName(),
				MediaType.APPLICATION_JSON);
	}

	@Override
	protected Representation toRepresentation(Map<String, Object> map,
			String templateName, MediaType mediaType) {
		return new TemplateRepresentation(templateName,
				(Configuration) ((IFreeMarkerApplication) getApplication())
						.getConfiguration(), map, MediaType.APPLICATION_JSON);
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		setFrameOptions("SAMEORIGIN");
		return getHTMLByTemplate(variant);
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
}
