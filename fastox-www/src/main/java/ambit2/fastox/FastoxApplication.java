package ambit2.fastox;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import ambit2.fastox.steps.WelcomeResource;
import ambit2.fastox.steps.step1.Step1Resource;
import ambit2.fastox.steps.step2.Step2Resource;
import ambit2.fastox.steps.step3.Step3Resource;
import ambit2.fastox.steps.step4.Step4Resource;
import ambit2.fastox.steps.step5.Step5Resource;
import ambit2.fastox.steps.step6.Step6Resource;

public class FastoxApplication extends Application {

	public FastoxApplication() {
		super();
		setName("Fastox");
	}
    @Override
    public Restlet createInboundRoot() {
        final Router router = new Router(getContext());
        router.attach(WelcomeResource.resource, WelcomeResource.class);
        router.attach("", WelcomeResource.class);
        router.attach(Step1Resource.resource, Step1Resource.class);
        router.attach(Step2Resource.resource, Step2Resource.class);
        router.attach(Step3Resource.resource, Step3Resource.class);
        router.attach(Step4Resource.resource, Step4Resource.class);
        router.attach(Step5Resource.resource, Step5Resource.class);
        router.attach(Step6Resource.resource, Step6Resource.class);
        
        router.attach(Step1Resource.resourceTab, Step1Resource.class);
        router.attach(Step2Resource.resourceTab, Step2Resource.class);
        router.attach(Step3Resource.resourceTab, Step3Resource.class);
        router.attach(Step4Resource.resourceTab, Step4Resource.class);
        router.attach(Step5Resource.resourceTab, Step5Resource.class);
        router.attach(Step6Resource.resourceTab, Step6Resource.class);   
        
       
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
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory jmeDir = new Directory(getContext(), "war:///jme");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 
//		 router.attach("/images/", imgDir);
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
