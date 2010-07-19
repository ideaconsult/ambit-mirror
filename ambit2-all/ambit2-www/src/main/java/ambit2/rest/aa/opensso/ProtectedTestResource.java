package ambit2.rest.aa.opensso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.CatalogResource;

public class ProtectedTestResource extends CatalogResource<String> {
	public static final String resource = "/sso_protected";
	public static final String resourceKey = "id";
	protected List<String> list;
	public ProtectedTestResource() {
		super();
		list = new ArrayList<String>();
		list.add(String.format("sso_protected/%d",0));
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		Object key = request.getAttributes().get(resourceKey);
		if (key==null) return list.iterator();
		else {
			list.set(0,String.format("sso_protected/%s",key.toString()));
			return list.iterator();
		} 
	}
	

	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Object token = OpenSSOToken.getToken(getRequest());
		if (token==null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
		
		
		Object key = getRequest().getAttributes().get(resourceKey);
		if (key!=null) throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		
		Form form = new Form(entity);
		String value = form.getFirstValue("content");
		if (value == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"content=<int> expected");
		
		Reference resource = getRequest().getResourceRef().clone();
		resource.setQuery(""); 
		resource.addSegment(value);
		
		Status status =null;
		try {
			Integer.parseInt(value.trim());
			 status = OpenSSOToken.createPolicy(String.format("%s",resource.toString()),resource.toString(),"test",token.toString());
			return get(variant);
		} catch (ResourceException x) {
			throw x;
		} catch (NumberFormatException x) {
			 throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Not an integer "+value);
		} catch (Exception x) {
			x.printStackTrace();
			 throw new ResourceException(status,x.getMessage());
		}
	}
	

}
