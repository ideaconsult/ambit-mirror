package ambit2.rest;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

public class RESTComponent extends Component {

	public RESTComponent(Context context,Application[] applications) {
		super();
		this.getClients().add(Protocol.FILE);
		this.getClients().add(Protocol.HTTP);
		this.getClients().add(Protocol.HTTPS);
		this.getClients().add(Protocol.RIAP);
		
		getServers().add(Protocol.RIAP);
		
		for (Application application: applications) {
			application.setContext(context==null?getContext().createChildContext():context);
		    getDefaultHost().attach(application);
		}
	    getInternalRouter().attach("/",applications[0]);					
	
	}

}