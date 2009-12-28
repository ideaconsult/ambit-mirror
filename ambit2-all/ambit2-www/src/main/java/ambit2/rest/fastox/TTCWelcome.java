package ambit2.rest.fastox;

import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.CatalogResource;

public class TTCWelcome extends CatalogResource<String>{
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
		Response response) throws ResourceException {
		ArrayList<String> q = new ArrayList<String>();
		q.add(String.format("ttc/%s","step1"));	
		return q.iterator();
	}
}
