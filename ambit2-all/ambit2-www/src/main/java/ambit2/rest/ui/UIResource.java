package ambit2.rest.ui;

import org.restlet.data.MediaType;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.freemarker.FreeMarkerResource;

public class UIResource extends FreeMarkerResource {
	public UIResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "query.ftl";
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
        getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
}
