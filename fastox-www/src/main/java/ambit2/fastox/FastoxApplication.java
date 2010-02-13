package ambit2.fastox;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import ambit2.fastox.steps.WelcomeResource;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.WizardStep;

public class FastoxApplication extends Application {

	public FastoxApplication() {
		super();
		setName("ToxPredict");
	}
    @Override
    public Restlet createInboundRoot() {
        final Router router = new Router(getContext());
        router.attach(WelcomeResource.resource, WelcomeResource.class);
        router.attach("", WelcomeResource.class);
        
        Wizard wizard = Wizard.getInstance();
        for (int i=0; i < wizard.size();i++) {
        	WizardStep step = wizard.getStep(i);
        	router.attach(step.getResource(),step.getResourceClass());
        	if (i>0)
        		router.attach(step.getResourceTab(),step.getResourceClass());
        }
        
      
        /*
        // Add a route for user resources
        router.attach("/users/{username}", UserResource.class);

        // Add a route for user's bookmarks resources
        router.attach("/users/{username}/bookmarks", BookmarksResource.class);

        // Add a route for bookmark resources
        final TemplateRoute uriRoute = router.attach(
                "/users/{username}/bookmarks/{URI}", BookmarkResource.class);
        uriRoute.getTemplate().getVariables().put("URI",
                new Variable(Variable.TYPE_URI_ALL));
	*/
        
		/*
        router.attach("/images/",new Directory(getContext(),LocalReference.createFileReference(
                                 "/webapp/images")));		
        router.attach("/style/",new Directory(getContext(),LocalReference.createFileReference(
        						 "/webapp/style")));
        */
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory jmeDir = new Directory(getContext(), "war:///jme");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 
 		 router.attach("/images/", imgDir);
//		 router.attach("/jmol/", jmolDir);
		 router.attach("/jme/", jmeDir);
		 router.attach("/style/", styleDir);
		 //router.attach("/favicon.ico", FavIconResource.class);
		 //router.attach("/favicon.png", FavIconResource.class);        
        return router;
    }
    
    public static void main(String[] args) throws Exception {
        
        // Create a component
        Component component = new FastoxComponent();
        final Server server = component.getServers().add(Protocol.HTTP, 8081);
        component.start();
   
        System.out.println("Server started on port " + server.getPort());
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        component.stop();
        System.out.println("Server stopped");
    }
    	
}
