package ambit2.rest.users;

import java.security.Principal;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.Role;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.search.QueryUser;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.propertyvalue.PropertyValueReporter;
import ambit2.rest.query.QueryResource;


public class UserResource extends QueryResource<QueryUser, AmbitUser> {
	public final static String resource = "/user";
	protected final static String resourceKey = "id";
	public final static String login = "login";
	public final static String resourceID = String.format("/{%s}",resourceKey);
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

	}
	@Override
	public IProcessor<QueryUser, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		System.out.println(getRequest().getClientInfo());
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter());
			} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
				return new DocumentConvertor(new UsersDOMReporter(getRequest()));
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				QueryURIReporter reporter = getURUReporter(getRequest());
				reporter.setDelimiter("\n");
				return new StringConvertor(	reporter,MediaType.TEXT_URI_LIST);
			} else 
				return new OutputStreamConvertor(
						new UsersHTMLReporter(getRequest(),queryObject.getValue()==null),
						MediaType.TEXT_HTML);
	}
	@Override
	protected QueryURIReporter<AmbitUser, QueryUser> getURUReporter(
			Request baseReference) throws ResourceException {
		return new UsersURIReporter<QueryUser>(baseReference);
	}
	
	@Override
	protected QueryUser createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Object idref = request.getAttributes().get(resourceKey);
		try {
			if (idref==null) {
				Role role = new Role();
				role.setName("admin");
				if (getRequest().getClientInfo().isInRole(role))
					return new QueryUser();
				else throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
			} else {
				String username = Reference.decode(idref.toString());
				if (login.equals(username)) {
					Subject s = getRequest().getClientInfo().getSubject();
					Iterator<Principal> p = s.getPrincipals().iterator();
					while (p.hasNext()) {
						return new QueryUser(p.next().getName());
					}
					return null;
					/*
				} else if (logout.equals(username)) {
					getRequest().getClientInfo().setSubject(null);
					getRequest().getClientInfo().setAuthenticated(false);
					getRequest().setChallengeResponse(null);
					getResponse().setChallengeRequest(null);
					getResponse().setChallengeRequests(null);
					getResponse().redirectTemporary("/user/login");
					
					return null;*/
				} else  return new QueryUser(username);
			}

		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid user id %d",idref),x
					);
		}		

	}

}
