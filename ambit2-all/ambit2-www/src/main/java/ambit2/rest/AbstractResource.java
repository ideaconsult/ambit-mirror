package ambit2.rest;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;

public abstract class AbstractResource extends Resource {
	public String[] URI_to_handle() {
		return null;
	}
	public AbstractResource(Context context, Request request, Response response) {
		super(context,request,response);
	}
}
