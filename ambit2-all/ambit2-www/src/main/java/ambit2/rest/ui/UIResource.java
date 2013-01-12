package ambit2.rest.ui;

import org.restlet.data.MediaType;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.freemarker.FreeMarkerResource;

public class UIResource extends FreeMarkerResource {
	private static final String key = "key";
	private enum pages { index, query, uploadstruc, uploadprops, predict }
	public UIResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		Object ui = getRequest().getAttributes().get(key);
		try {
			return ui==null?"index.ftl":String.format("%s.ftl", pages.valueOf(ui.toString()).name());
		} catch (Exception x) { return "index.ftl";}
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
        getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
}
