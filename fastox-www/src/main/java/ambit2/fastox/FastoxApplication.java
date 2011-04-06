package ambit2.fastox;

import java.io.StringWriter;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import org.restlet.util.RouteList;

import ambit2.fastox.models.FeaturesResource;
import ambit2.fastox.models.ModelsResource;
import ambit2.fastox.models.ReportingResource;
import ambit2.fastox.steps.WelcomeResource;
import ambit2.fastox.task.ModelLauncherResource;
import ambit2.fastox.task.ToxPredictTaskResource;
import ambit2.fastox.users.IToxPredictUser;
import ambit2.fastox.users.UserResource;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.WizardMode;
import ambit2.fastox.wizard.WizardResource;
import ambit2.fastox.wizard.WizardStep;
import ambit2.rest.BotsGuard;
import ambit2.rest.SimpleTaskResource;
import ambit2.rest.TaskApplication;
import ambit2.rest.toxpredict.simple.SimplePredict;

public class FastoxApplication extends TaskApplication<IToxPredictUser> {
	
	public FastoxApplication() {
		super();
		setName("ToxPredict");
		
	}
    private ChallengeAuthenticator authenticatior;

    private ChallengeAuthenticator createAuthenticator() {

        ChallengeScheme challengeScheme = ChallengeScheme.HTTP_BASIC;
        String realm = "ToxPredict";

        // MapVerifier isn't very secure; see docs for alternatives
        MapVerifier verifier = new MapVerifier();
        verifier.getLocalSecrets().put("opentox", "opentox".toCharArray());

        ChallengeAuthenticator auth = new ChallengeAuthenticator(null, false, challengeScheme, realm);
        /*
            @Override
            protected boolean authenticate(Request request, Response response) {
                if (request.getChallengeResponse() == null) {
                    return false;
                } else {
                    return super.authenticate(request, response);
                }
            }
        };
        */
        auth.setVerifier(verifier);
        return auth;
    }
	
    @Override
    public Restlet createInboundRoot() {
    	
    	
        final Router router = new Router(getContext());
        router.attach(WelcomeResource.resource, WelcomeResource.class);
        router.attach("", WelcomeResource.class);

        router.attach(ModelLauncherResource.resource,ModelLauncherResource.class);
        router.attach(SimpleTaskResource.resource,ToxPredictTaskResource.class);
        router.attach(SimpleTaskResource.resource+SimpleTaskResource.resourceID,ToxPredictTaskResource.class);
        
        router.attach("/simple",SimplePredict.class);
        
        this.authenticatior = createAuthenticator();
        router.attach(String.format("/admin/{%s}",UserResource.resourceKey),authenticatior);
        authenticatior.setNext(AdminResource.class);        
        
        router.attach(String.format("/help/{%s}",UserResource.resourceKey),HelpResource.class);
        
        
        router.attach("/{x}", WelcomeResource.class); //this is a hack to avoid not-matching if navigated to /ToxPredict/whatever
        router.setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
        router.setRoutingMode(Router.MODE_BEST_MATCH); 
        
        
        
        Router userRouter = new Router(getContext());
        userRouter.setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
        userRouter.setRoutingMode(Router.MODE_BEST_MATCH); 
        router.attach(String.format("/%s/{%s}",UserResource.resource,UserResource.resourceKey),userRouter);
        //router.attach(String.format("/%s",UserResource.resource),usersRouter);
        userRouter.attachDefault(UserResource.class);
        
        userRouter.attach(ModelsResource.resource,ModelsResource.class);
        userRouter.attach(ReportingResource.resource,ReportingResource.class);
        userRouter.attach(String.format("%s/{%s}",ReportingResource.resource,ReportingResource.resourceType),
        				ReportingResource.class);
        userRouter.attach(FeaturesResource.resource,FeaturesResource.class);
        
       
        
        for (WizardMode mode : WizardMode.values()) {
            Wizard wizard = Wizard.getInstance(mode);
            for (int i=0; i < wizard.size();i++) {
            	WizardStep step = wizard.getStep(i);
            	
            	if (i>0) {
            		userRouter.attach(String.format("/{%s}%s",WizardResource.key,step.getResource()), step.getResourceClass());
            		userRouter.attach(String.format("/{%s}%s",WizardResource.key,step.getResourceTab()), step.getResourceClass());
            		
 //           		userRouter.attach(String.format("/%s%s",mode.getResource(),step.getResource()), step.getResourceClass());
   //         		userRouter.attach(String.format("/%s%s",mode.getResource(),step.getResourceTab()), step.getResourceClass());


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
		 Directory scriptsDir = new Directory(getContext(), "war:///scripts");
		 Directory jqueryDir = new Directory(getContext(), "war:///jquery");
		 Directory metaDir = new Directory(getContext(), "war:///META-INF");
		 
 		 router.attach("/images/", imgDir);
 		router.attach("/meta/", metaDir);
//		 router.attach("/jmol/", jmolDir);
		 router.attach("/jme/", jmeDir);
		 router.attach("/style/", styleDir);
		 router.attach("/scripts/", scriptsDir);
		 router.attach("/jquery/", jqueryDir);
		 //router.attach("/favicon.ico", FavIconResource.class);
		 //router.attach("/favicon.png", FavIconResource.class);      
		 StringWriter w = new StringWriter();
		 //printRoutes(router," ",w);
		 //System.out.println(w.toString());
		 
        BotsGuard botsGuard = new BotsGuard();
        botsGuard.setNext(router);

		 return botsGuard;
        
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
