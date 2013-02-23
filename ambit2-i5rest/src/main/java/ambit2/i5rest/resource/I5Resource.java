package ambit2.i5rest.resource;


import java.io.Serializable;

import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IProcessor;
import ambit2.i5rest.convertors.I5OutputStreamConvertor;
import ambit2.rest.AbstractResource;

public abstract class I5Resource<Content,T extends Serializable> extends 
					AbstractResource<Content,T,IProcessor<Content, Representation>> {

	protected final static Logger LOGGER = Logger.getLogger(I5OutputStreamConvertor.class);
	public static final String sessionParam = "session"; 
	protected Session session;
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		Form form = getRequest().getResourceRef().getQueryAsForm();
		String sparam = form.getFirstValue(sessionParam);
		if (sparam!=null) {
			Session session = new Session();
			session.setId(sparam);
			setSession(session);
		} else setSession(null);
	}


}
