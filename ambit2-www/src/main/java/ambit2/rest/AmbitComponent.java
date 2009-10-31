package ambit2.rest;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import ambit2.workflow.ui.SetAndExecWorkflowTask;

/**
 * This is used as a servlet component instead of the core one, to be able to attach protocols 
 * @author nina
 *
 */
public class AmbitComponent extends Component {
		public AmbitComponent() {
			this(null);
		}
		public AmbitComponent(Context context) {
			super();
			this.getClients().add(Protocol.FILE);
			this.getClients().add(Protocol.HTTP);
			this.getClients().add(Protocol.HTTPS);

		    AmbitApplication application = new AmbitApplication();
		    application.setContext(context==null?getContext().createChildContext():context);
		    
		    // Attach the application to the component and start it
		    
		    getDefaultHost().attach(application);
		    getInternalRouter().attach("/",application);
			
		}
		
		

}
