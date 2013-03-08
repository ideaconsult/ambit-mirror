package ambit2.rest.ui;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.freemarker.FreeMarkerResource;

public class UIResource extends FreeMarkerResource {
	private static final String key = "key";
	private enum pages { 
			index, query, 
			uploadstruc, uploadprops, 
			predict, 
			login, register, myprofile, 
			uploadstruc_register,uploadprops_batch,uploadprops_biodata,
			createstruc
		}
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
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
		if (getClientInfo().getUser() == null) {
			OpenSSOUser ou = new OpenSSOUser();
			ou.setUseSecureCookie(useSecureCookie(getRequest()));
			getClientInfo().setUser(ou);
		}
        setTokenCookies(variant, useSecureCookie(getRequest()));
        configureTemplateMap(map);
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}
}
