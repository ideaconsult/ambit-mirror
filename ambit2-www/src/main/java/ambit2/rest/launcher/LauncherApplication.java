package ambit2.rest.launcher;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import ambit2.rest.AmbitStatusService;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.RESTComponent;
import ambit2.rest.TaskApplication;
import ambit2.rest.task.LauncherResource;
import ambit2.rest.task.TaskResource;

public class LauncherApplication<USERID> extends TaskApplication<USERID>{
	public LauncherApplication() {
		setName("OpenTox launcher");
		setDescription("Wrapper for running remote OpenTox algorithms and models");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");
		setStatusService(new AmbitStatusService());
		/*
		setTunnelService(new TunnelService(true,true) {
			@Override
			public Filter createInboundFilter(Context context) {
				return new AmbitTunnelFilter(context);
			}
		});
		*/
		getTunnelService().setUserAgentTunnel(true);
		getMetadataService().setEnabled(true);
		getMetadataService().addExtension("sdf", ChemicalMediaType.CHEMICAL_MDLSDF, true);
		getMetadataService().addExtension("mol", ChemicalMediaType.CHEMICAL_MDLMOL, true);
		getMetadataService().addExtension("inchi", ChemicalMediaType.CHEMICAL_INCHI, true);
		getMetadataService().addExtension("cml", ChemicalMediaType.CHEMICAL_CML, true);
		getMetadataService().addExtension("smiles", ChemicalMediaType.CHEMICAL_SMILES, true);		
		
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new MyRouter(this.getContext());
		router.attach("/", LauncherResource.class);
		router.attach(OpenTox.URI.task.getURI(), TaskResource.class);
	     router.setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
	     router.setRoutingMode(Router.MODE_BEST_MATCH); 		
	     
		return router;
	}
	
	/**
	 * Standalone, for testing mainly
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        
        // Create a component
        Component component = new RESTComponent(null,new Application[] {new LauncherApplication()});
        final Server server = component.getServers().add(Protocol.HTTP, 8080);
        component.start();
   
        System.out.println("Server started on port " + server.getPort());
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        component.stop();
        System.out.println("Server stopped");
    }
}

/**
 * For backward compatibility with 2.0-M5 and before
 */
class MyRouter extends Router {
	public MyRouter(Context context) {
		 super(context);
	     setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
	     setRoutingMode(Router.MODE_BEST_MATCH); 
	}
}
