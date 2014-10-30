package ambit2.rest.aa.basic;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.ui.UIResource;

public class UINoAAResource extends UIResource {

	@Override
	public String getTemplateName() {
		return "a/noaa.ftl";
	}
	@Override
	public boolean isHtmlbyTemplate() {
		return true;
	}
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		return super.get(variant);
	}
	@Override
	protected Representation delete() throws ResourceException {
		return super.get();
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("method",request.getMethod());
	}
}