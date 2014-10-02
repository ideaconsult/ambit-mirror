package ambit2.user.rest.resource;

import java.util.Map;

import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class MyAccountResource<T> extends UserDBResource<T> {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		editable = false;
		singleItem = true;
		setHtmlbyTemplate(true);
	}
	
	@Override
	public boolean isHtmlbyTemplate() {
		return freeMarkerSupport.isHtmlbyTemplate();
	}
	@Override
	public String getTemplateName() {
		return "a/myprofile_body.ftl";
	}
	
	
	@Override
	protected ReadUser createQuery(Context context, Request request, Response response)
			throws ResourceException {
		String search_name = null;
		Object search_value = null;

		try {
			search_name = "username";
			search_value = getClientInfo().getUser().getIdentifier();
		} catch (Exception x) {
			search_value = null;
			x.printStackTrace();
		}				
		if (search_value == null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,"Not logged in!");
		Object key = request.getAttributes().get(UserDBResource.resourceKey);		
		try {
			return getUserQuery(key,search_name,search_value);
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid protocol id %d",key),
					x
					);
		}
	} 
	

	@Override
	protected ReadUser<T> createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		if (Method.PUT.equals(method) || Method.DELETE.equals(method)) {
			return createQuery(context, request, response);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return null;
	}
	
	@Override
	protected Map<String, Object> getMap(Variant variant)
			throws ResourceException {
		Map<String, Object> map = super.getMap(variant);
		map.put("myprofile", true);
		return map;
	}
	
	
	@Override
	protected String getItemName(DBUser item) {
		return item==null?"":item.getUserName()==null?"":item.getUserName();
	}	
}
