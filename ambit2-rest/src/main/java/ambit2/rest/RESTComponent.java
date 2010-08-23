package ambit2.rest;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.ext.wadl.WadlComponent;

public class RESTComponent extends WadlComponent {

	public RESTComponent(Context context,Application[] applications) {
		super();
		this.getClients().add(Protocol.FILE);
		this.getClients().add(Protocol.HTTP);
		this.getClients().add(Protocol.HTTPS);

		for (Application application: applications) {
			application.setContext(context==null?getContext().createChildContext():context);
		    getDefaultHost().attach(application);
		}
	    getInternalRouter().attach("/",applications[0]);					
	
	}

}