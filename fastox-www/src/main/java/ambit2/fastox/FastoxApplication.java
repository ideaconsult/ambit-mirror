package ambit2.fastox;

import java.io.StringWriter;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.util.RouteList;

import ambit2.fastox.steps.WelcomeResource;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.WizardResource;
import ambit2.fastox.wizard.WizardStep;
import ambit2.fastox.wizard.Wizard.WizardMode;

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
        router.attach(String.format("/{%s}", WizardResource.key), WelcomeResource.class);
        
        for (WizardMode mode : WizardMode.values()) {
            Wizard wizard = Wizard.getInstance(mode);
            for (int i=0; i < wizard.size();i++) {
            	WizardStep step = wizard.getStep(i);
            	
            	if (i>0) {
            		router.attach(String.format("/{%s}%s",WizardResource.key,step.getResource()), step.getResourceClass());
            		router.attach(String.format("/{%s}%s",WizardResource.key,step.getResourceTab()), step.getResourceClass());
            	}
            	
            }  
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
		 StringWriter w = new StringWriter();
		 printRoutes(router," ",w);
		 System.out.println(w.toString());
		 return router;
        
    }
    public static String printRoutes(Restlet re,String delimiter,StringWriter b) {
   		
 		while (re != null) {
 			
 			b.append(re.getClass().toString());
 			b.append('\t');
 			if (re instanceof Finder) {
 				b.append(((Finder)re).getTargetClass().getName());
 				b.append('\n');
 				re = null;
 			} else if (re instanceof Filter)
	 			re = ((Filter)re).getNext();
	 		else if (re instanceof Router) {
	 			b.append('\n');
	 			RouteList list = ((Router)re).getRoutes();
	 		 	for (Route r : list) { 
	 		 		
	 		 		b.append(delimiter);
	 		 		b.append(r.getTemplate().getPattern());
	 		 		b.append('\t');
	 		 		b.append(r.getTemplate().getVariableNames().toString());
	 		 		printRoutes(r.getNext(),'\t'+delimiter+r.getTemplate().getPattern(),b);
	 		 	}	
	 		 	
	 			break;
	 		} else {
	 			break;
	 		}
	 		
	 		
 		}

 		return b.toString();

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
