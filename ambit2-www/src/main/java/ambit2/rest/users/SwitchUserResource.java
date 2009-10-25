package ambit2.rest.users;

import java.security.Principal;
import java.util.Iterator;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Test setup for a protected resource
 * @author nina
 *
 */
public class SwitchUserResource extends  ServerResource {
	public static final String resource="/protected/{name}";

	@Override
	protected Representation get() throws ResourceException {
		try {
			Iterator<Principal> i = getRequest().getClientInfo().getSubject().getPrincipals().iterator();
			Principal p = null;
			while (i.hasNext()) { p = i.next(); break; }
			getResponse().redirectSeeOther(String.format("%s/user/%s",getRequest().getRootRef(),p.getName()));
			return null;
		} catch (Exception x) {
			getResponse().redirectSeeOther(String.format("%s/user/login",getRequest().getRootRef()));
			return null;
		}
	}
	/*
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		ArrayList<String> q = new ArrayList<String>();
		q.add(request.getClientInfo().getSubject().toString());

		
		Set<Principal> ps = request.getClientInfo().getSubject().getPrincipals();
		Iterator<Principal> i = ps.iterator();
		while (i.hasNext()) {
			Principal p = i.next();
			q.add(p.);
		}
		
		return q.iterator();
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return get(variant);
	}
	*/
}
