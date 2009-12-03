package ambit2.rest.fastox;

import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.AlgorithmCatalogResource;

public class TTCWelcome extends AlgorithmCatalogResource<String>{
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
		Response response) throws ResourceException {
		ArrayList<String> q = new ArrayList<String>();
		q.add(String.format("ttc/%s","step1"));	
		return q.iterator();
	}
}
