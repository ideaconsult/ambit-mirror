package ambit2.rest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.MethodAuthorizer;
import org.restlet.security.Verifier;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;

import ambit2.base.config.Preferences;
import ambit2.rest.aa.DBVerifier;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.algorithm.quantumchemical.Build3DResource;
import ambit2.rest.algorithm.util.Name2StructureResource;
import ambit2.rest.dataset.DatasetCompoundResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.fastox.FastToxStep1;
import ambit2.rest.fastox.FastToxStep2;
import ambit2.rest.fastox.KroesInput;
import ambit2.rest.fastox.KroesStep1;
import ambit2.rest.fastox.KroesStep2;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertiesByDatasetResource;
import ambit2.rest.property.PropertyModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.pubchem.CSLSResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.rest.structure.diagram.CSLSDepict;
import ambit2.rest.structure.diagram.DaylightDepict;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskResource;
import ambit2.rest.template.OntologyResource;
import ambit2.rest.tuple.TuplePropertyValueResource;
import ambit2.rest.tuple.TupleResource;
import ambit2.rest.users.SwitchUserResource;
import ambit2.rest.users.UserResource;

/**
 * AMBIT implementation of OpenTox REST services as described in http://opentox.org/development/wiki/
 * http://opentox.org/wiki/1/Dataset
 * @author nina
 */

 /* 
 * http://www.slideshare.net/guest7d0e11/creating-a-web-of-data-with-restlet-presentation
 * http://stackoverflow.com/questions/810171/how-to-read-context-parameters-from-a-restlet
 *
 */
public class AmbitApplication extends Application {
	protected ConcurrentMap<UUID,Task<Reference>> tasks;

	protected long taskCleanupRate = 2L*60L*60L*1000L; //2h
	protected ExecutorService pool;

	public AmbitApplication() {
		super();
		setName("AMBIT REST services");
		setDescription("AMBIT implementation of OpenTox framework");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");		
		//pool = Executors.newFixedThreadPool(5);
		pool = Executors.newSingleThreadExecutor(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setDaemon(true);
				thread.setName(String.format("%s task executor",getName()));
				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
			            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
			            e.printStackTrace(new PrintWriter(stackTraceWriter));
						getLogger().severe(stackTraceWriter.toString());
					}
				});
				return thread;
			}
		});
			
	

		tasks = new ConcurrentHashMap<UUID,Task<Reference>>();
		/*
		String tmpDir = System.getProperty("java.io.tmpdir");
        File logFile = new File(tmpDir,"ambit2-www.log");		
		System.setProperty("java.util.logging.config.file",logFile.getAbsolutePath());
		*/

		//connectionURI = null;
		setStatusService(new AmbitStatusService());

		//getTaskService().setEnabled(true);
		
		setTunnelService(new TunnelService(true,true) {
			@Override
			public Filter createInboundFilter(Context context) {
				return new AmbitTunnelFilter(context);
			}
		});
		getTunnelService().setUserAgentTunnel(true);

		TimerTask cleanUpTasks  = new TimerTask() {
			
			@Override
			public void run() {
				cleanUpTasks();
				
			}
		};

	    Timer timer = new Timer();

	    timer.scheduleAtFixedRate(cleanUpTasks,taskCleanupRate,taskCleanupRate);

		Preferences.setProperty(Preferences.MAXRECORDS,"0");
		
	}
	@Override
	protected void finalize() throws Throwable {
		removeTasks();
		super.finalize();
	}


	@Override
	public Restlet createInboundRoot() {
		Router router = new MyRouter(this.getContext());
		router.attach("/help", AmbitResource.class);
		

		router.attach("", SmartsQueryResource.class);	
		router.attach("/", SmartsQueryResource.class);
		//test
		router.attach(RDFGraphResource.resource,RDFGraphResource.class);
		router.attach(RDFGraphResource.resource+"/test",OntologyPlayground.class);
		
		Router fastoxRouter = new MyRouter(getContext());
		router.attach(FastToxStep1.resource,fastoxRouter);
		fastoxRouter.attachDefault(FastToxStep1.class);
		fastoxRouter.attach("/step2", FastToxStep2.class);

		Router ttcRouter = new MyRouter(getContext());
		router.attach("/ttc",ttcRouter);
		ttcRouter.attachDefault(KroesStep1.class);
		ttcRouter.attach("/step2", KroesStep2.class);
		ttcRouter.attach("/input", KroesInput.class);
		
		
		router.attach(OntologyResource.resource, OntologyResource.class);
		router.attach(OntologyResource.resourceID, OntologyResource.class);
		router.attach(OntologyResource.resourceTree, OntologyResource.class);
		

		Router allDatasetsRouter = new MyRouter(getContext());
		allDatasetsRouter.attachDefault(DatasetsResource.class);
		router.attach(DatasetsResource.datasets, allDatasetsRouter);		

		
		Router datasetRouter = new MyRouter(getContext());
		datasetRouter.attachDefault(DatasetResource.class);
		//this is for backward compatibility
		router.attach(String.format("%s",DatasetResource.dataset), DatasetsResource.class);
		router.attach(String.format("%s/{%s}",DatasetResource.dataset,DatasetResource.datasetKey), datasetRouter);
		router.attach(String.format("%s/{%s}/metadata",DatasetResource.dataset,DatasetResource.datasetKey), DatasetsResource.class);
		
		DBVerifier verifier = new DBVerifier(this);
		/**
		 * /user
		 */
		Router usersRouter = new MyRouter(getContext());
		usersRouter.attachDefault(UserResource.class);
	 	
		/**
		 * /user/{userid}
		 */
		Router userRouter = new MyRouter(getContext());
		userRouter.attachDefault(UserResource.class);
	 	usersRouter.attach(UserResource.resourceID,userRouter);
	 	
	 	/**
	 	 *  authentication mandatory for users resource
	 	 */
		Filter guard = createGuard(verifier,false);
		
    	/*
    	 * Simple authorizer
    	 */
    	MethodAuthorizer authorizer = new MethodAuthorizer();
    	authorizer.getAnonymousMethods().add(Method.GET);
    	authorizer.getAnonymousMethods().add(Method.HEAD);
    	authorizer.getAnonymousMethods().add(Method.OPTIONS);
    	authorizer.getAuthenticatedMethods().add(Method.PUT);
    	authorizer.getAuthenticatedMethods().add(Method.DELETE);
    	authorizer.getAuthenticatedMethods().add(Method.POST);
    	authorizer.getAuthenticatedMethods().add(Method.OPTIONS);
    	
		authorizer.setNext(usersRouter);
		guard.setNext(authorizer);
	 	router.attach(UserResource.resource, guard);
	 	router.attach(UserResource.resource, usersRouter);

	 			
		router.attach(FeatureResource.CompoundFeaturedefID,FeatureResource.class);
		router.attach(FeatureResource.ConformerFeaturedefID,FeatureResource.class);

	 	/*
	 	router.attach(DatasetsResource.datasets, createGuard(DatasetsResource.class,verifierOptional,true));		
		router.attach(FeatureResource.CompoundFeaturedefID,createGuard(FeatureResource.class,verifierOptional,true));
		router.attach(FeatureResource.ConformerFeaturedefID,createGuard(FeatureResource.class,verifierOptional,true));
*/

		datasetRouter.attach(CompoundResource.compoundID, DatasetCompoundResource.class);
		//router.attach(datasetID_structure_media, DatasetCompoundResource.class);
		
		datasetRouter.attach(CompoundResource.compound, DatasetStructuresResource.class);
		
	
		
		//router.attach("/cas/{cas}"+fp,SimilarityResource.class);
		//router.attach("/name/{name}"+fp,SimilarityResource.class);	
		
		Router compoundsRouter = new MyRouter(getContext());
		compoundsRouter.attachDefault(CompoundResource.class);
		router.attach(CompoundResource.compound,compoundsRouter);
		
		Router compoundRouter = new MyRouter(getContext());
		compoundRouter.attachDefault(CompoundResource.class);
		compoundsRouter.attach(String.format("/{%s}",CompoundResource.idcompound),compoundRouter);
	
		Router conformersRouter = new MyRouter(getContext());
		conformersRouter.attachDefault(ConformerResource.class);
		compoundRouter.attach(ConformerResource.conformerKey,conformersRouter);		
		
		Router conformerRouter = new MyRouter(getContext());
		conformerRouter.attachDefault(ConformerResource.class);
		conformersRouter.attach(String.format("/{%s}",ConformerResource.idconformer),conformerRouter);	

		compoundRouter.attach(PropertyValueResource.featureKey,PropertyValueResource.class);

		Router featureByAlias = new MyRouter(getContext());
		featureByAlias.attachDefault(PropertyValueResource.class);
		
		compoundRouter.attach(String.format("%s/{name}",PropertyValueResource.featureKey),featureByAlias);
		conformerRouter.attach(String.format("%s/{name}",PropertyValueResource.featureKey),featureByAlias);
		
		//router.attach(PropertyValueResource.compoundFeatureName,PropertyValueResource.class);
		//router.attach(PropertyValueResource.conformerFeatureName,PropertyValueResource.class);
		
		router.attach(PropertyValueResource.FeatureNameCompound,PropertyValueResource.class);
		router.attach(PropertyValueResource.FeatureNameConformer,PropertyValueResource.class);

		Router templateRouter = new MyRouter(getContext());
		templateRouter.attachDefault(PropertyTemplateResource.class);
		templateRouter.attach(PropertyTemplateResource.resourceID,PropertyTemplateResource.class);
		
		compoundRouter.attach(PropertyTemplateResource.resource,templateRouter);
		conformerRouter.attach(PropertyTemplateResource.resource,templateRouter);

		
		router.attach(ModelResource.resource,ModelResource.class);
		router.attach(ModelResource.resourceID,ModelResource.class);
		
		router.attach(String.format("%s%s",ModelResource.resourceID,PropertyModelResource.resourceID),PropertyModelResource.class);
		
		Router tupleRouter = new MyRouter(getContext());
		tupleRouter.attachDefault(TupleResource.class);
		tupleRouter.attach(String.format("/{%s}", TupleResource.resourceKey),TuplePropertyValueResource.class);

		compoundRouter.attach(TupleResource.resourceTag,tupleRouter);
		conformerRouter.attach(TupleResource.resourceTag,tupleRouter);
		
		Router referenceRouter = new MyRouter(getContext());
		router.attach(ReferenceResource.reference,referenceRouter);
		referenceRouter.attachDefault(ReferenceResource.class);
		referenceRouter.attach(String.format("/{%s}",ReferenceResource.idreference),ReferenceResource.class);

		Router feature_def = new MyRouter(getContext());
		router.attach(PropertyResource.featuredef,feature_def);
		feature_def.attachDefault(PropertyResource.class);
		feature_def.attach(PropertyResource.featuredefID,PropertyResource.class);

		compoundRouter.attach(PropertyResource.featuredef,feature_def);
		conformerRouter.attach(PropertyResource.featuredef,feature_def);
		/*
		router.attach(PropertyResource.CompoundFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.CompoundFeaturedefID,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedefID,PropertyResource.class);
		*/
		
		//public final static String DatasetFeaturedefID = String.format("%s%s/{%s}",DatasetsResource.datasetID,featuredef,idfeaturedef);
		//public final static String DatasetFeaturedef = String.format("%s%s",DatasetsResource.datasetID,featuredef);
		datasetRouter.attach(PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.class);
		datasetRouter.attach(String.format("%s/{%s}",PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.idfeaturedef),PropertiesByDatasetResource.class);
		


		
				
		router.attach("/depict",AbstractDepict.class);
		router.attach("/depict/daylight",DaylightDepict.class);
		router.attach("/depict/cdk",CDKDepict.class);
		router.attach("/depict/cactvs",CSLSDepict.class);

		
		router.attach("/name2structure",Name2StructureResource.class);	
		
		router.attach(String.format("/%s",Build3DResource.resource),Build3DResource.class);	
		
		/**
		 * Queries
		 */
		Router queryRouter = new MyRouter(getContext());
		router.attach(QueryResource.query_resource,queryRouter);
		queryRouter.attachDefault(QueryListResource.class);
		queryRouter.attach(QLabelQueryResource.resource,QLabelQueryResource.class);
		
		datasetRouter.attach(String.format("%s%s",QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
				
		
		Router pubchem = new MyRouter(getContext());
		queryRouter.attach(PubchemResource.resource,pubchem);
		pubchem.attachDefault(PubchemResource.class);
		pubchem.attach(PubchemResource.resourceID,PubchemResource.class);

		Router csls = new MyRouter(getContext());
		queryRouter.attach(CSLSResource.resource,csls);
		csls.attachDefault(CSLSResource.class);
		csls.attach(CSLSResource.resourceID,CSLSResource.class);
		csls.attach(CSLSResource.resourceID+CSLSResource.representationID,CSLSResource.class);
		
		//router.attach("/smiles/{smiles}"+fp,SimilarityResource.class);
		//router.attach("/smiles/{smiles}"+fp_dataset,SimilarityResource.class);
		Router similarity = new MyRouter(getContext());
		queryRouter.attach(SimilarityResource.resource,similarity);
		similarity.attachDefault(SimilarityResource.class);
		//search dataset for similar compounds
		datasetRouter.attach(SimilarityResource.resource,similarity);	
		
		Router smartsRouter = new MyRouter(getContext());
		smartsRouter.attachDefault(SmartsQueryResource.class);
		smartsRouter.attach(SmartsQueryResource.resourceID,SmartsQueryResource.class);
		queryRouter.attach(SmartsQueryResource.resource,smartsRouter);
		
		datasetRouter.attach(SmartsQueryResource.resource,smartsRouter);
		compoundRouter.attach(SmartsQueryResource.resource,smartsRouter);
		
		
		Router algoRouter = new MyRouter(getContext());
		algoRouter.attachDefault(AllAlgorithmsResource.class);
		router.attach(AllAlgorithmsResource.algorithm,algoRouter);
		router.attach(AllAlgorithmsResource.resourceID,algoRouter);

		Router taskRouter = new MyRouter(getContext());
		taskRouter.attachDefault(TaskResource.class);
		taskRouter.attach(TaskResource.resourceID, TaskResource.class);
		router.attach(TaskResource.resource, taskRouter);		
		/*
        router.attach(
                "/images",
                new Directory(
                        getContext(),
                        LocalReference.createFileReference(
                                 "/webapps/images")));		
                               */
		
		 Directory metaDir = new Directory(getContext(), "war:///META-INF");
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory jmeDir = new Directory(getContext(), "war:///jme");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 Directory jsDir = new Directory(getContext(), "war:///js");

		 
		 router.attach("/meta/", metaDir);
		 router.attach("/images/", imgDir);
		 router.attach("/jmol/", jmolDir);
		 router.attach("/jme/", jmeDir);
		 router.attach("/style/", styleDir);
		 router.attach("/js/", jsDir);
		 router.attach("/favicon.ico", FavIconResource.class);
		 router.attach("/favicon.png", FavIconResource.class);



	     router.attach(SwitchUserResource.resource,createGuardGuest(SwitchUserResource.class));

	     router.setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
	     router.setRoutingMode(Router.MODE_BEST_MATCH); 
		 return router;
	}

	
	public Iterator<Task<Reference>> getTasks() {
		return tasks.values().iterator();
	}
	public synchronized Task<Reference> findTask(String id) {
		try {
		return tasks.get(UUID.fromString(id));
		} catch (Exception x) {
			System.out.println(x);
			return null;
		}
	}
	public synchronized void removeTask(String id) {
		try {
			tasks.remove(UUID.fromString(id));
		} catch (Exception x) {
			System.out.println(x);
			return;
		}
	}
	public synchronized Reference addTask(String taskName, Callable<Reference> callable, Reference baseReference) {
		if (callable == null) return null;
		FutureTask<Reference> futureTask = new FutureTask<Reference>(callable) {
			@Override
			protected void done() {
				super.done();
				//((AmbitApplication)getApplication()).getTasks().remove(this);
			}
			
			
		};		
		UUID uuid = UUID.randomUUID();
		Task<Reference> task = new Task<Reference>(futureTask);
		task.setName(taskName);
		Reference ref =	new Reference(
				String.format("%s%s/%s", baseReference.toString(),TaskResource.resource,Reference.encode(uuid.toString())));
		task.setUri(ref);
		tasks.put(uuid,task);
		//getTaskService().submit(futureTask);
		pool.submit(futureTask);
		return ref;
	}

	public void cleanUpTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			Task<Reference> task = tasks.get(key);
			try {
				if (task.isDone() && (task.isExpired(taskCleanupRate))) tasks.remove(key);
			} catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
	}	
	public void cancelTasks() {
		Iterator<Task<Reference>> i = getTasks();
		while (i.hasNext()) {
			Task<Reference> task = i.next();
			try {
			if (!task.isDone()) task.cancel(true);
			} catch (Exception x) {getLogger().warning(x.getMessage());}
		}
	}
	public void removeTasks() {
		cancelTasks();
		tasks.clear();
	}	
	/**
	 * Standalone, for testing mainly
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        
        // Create a component
        Component component = new AmbitComponent();
        final Server server = component.getServers().add(Protocol.HTTP, 8080);
        component.start();
   
        System.out.println("Server started on port " + server.getPort());
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        component.stop();
        System.out.println("Server stopped");
    }
    	
    protected ChallengeAuthenticator createGuard(Verifier verifier,boolean optional) {
    	
    	Enroler enroler = new Enroler() {
    		public void enrole(ClientInfo subject) {
    			System.out.println(subject);
    			
    		}
    	};
	        // Create a Guard
	     ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(),optional,ChallengeScheme.HTTP_BASIC, "ambit2") {
	    	@Override
	    	protected boolean authenticate(Request request, Response response) {
	    		return super.authenticate(request, response);
	    	} 
	     };
	     guard.setVerifier(verifier);
	     guard.setEnroler(enroler);
	     
		 return guard;
    }
    
   protected ChallengeAuthenticator createGuardGuest(Class clazz) {
    	
	     DBVerifier verifier = new DBVerifier(this) {
	        	@Override
	        	public int verify(Request request, Response response) {
	                if (request.getChallengeResponse() != null) { 
	                    String identifier = request.getChallengeResponse().getIdentifier();
	                    Object name = request.getAttributes().get("name");
	                    if ((identifier!=null) && (name != null) && (identifier.equals(name.toString())))
	                    	return RESULT_MISSING;
	                }
	                return super.verify(request, response);
	        	}
	     };
	     /*
	     verifier.getSecrets().put("nina", "nina".toCharArray());
	     verifier.getSecrets().put("guest", "guest".toCharArray());
	     verifier.getSecrets().put("admin", "admin".toCharArray());
	     */
    	Enroler enroler = new Enroler() {
    		public void enrole(ClientInfo subject) {
    			System.out.println(subject);
    			
    		}
    	};

	        // Create a Guard
    	ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(),ChallengeScheme.HTTP_BASIC, "ambit2") {

	    	@Override
	    	protected boolean authenticate(Request request, Response response) {
	    		/*
                request.getClientInfo().getSubject().getPrincipals().add(
                		new UserPrincipal("guest"));	

                request.getClientInfo().setAuthenticated(true);
                return true;
   */            
                return super.authenticate(request, response);
	    	}; 
	
	     };
	     guard.setVerifier(verifier);
	     guard.setEnroler(enroler);
		 guard.setNext(clazz);     
 		 
		 return guard;
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

}

/*
        RoleAuthorizer roleAuthorizer = new RoleAuthorizer();
        roleAuthorizer.getAuthorizedRoles().add(findRole("admin"));
        roleAuthorizer.setNext(new HelloWorldRestlet());
        
        MemoryRealm realm = new MemoryRealm();
        context.setEnroler(realm.getEnroler());
        context.setVerifier(realm.getVerifier());

        // Add users
        User stiger = new User("stiger", "pwd", "Scott", "Tiger",
                "scott.tiger@foobar.com");
        realm.getUsers().add(stiger);

        User larmstrong = new User("larmstrong", "pwd", "Louis", "Armstrong",
                "la@foobar.com");
        realm.getUsers().add(larmstrong);

        // Add groups
        Group employees = new Group("employees ", "All FooBar employees");
        employees.getMemberUsers().add(larmstrong);
        realm.getRootGroups().add(employees);

        Group contractors = new Group("contractors ", "All FooBar contractors");
        contractors.getMemberUsers().add(stiger);
        realm.getRootGroups().add(contractors);

        Group managers = new Group("managers", "All FooBar managers");
        realm.getRootGroups().add(managers);

        Group directors = new Group("directors ", "Top-level directors");
        directors.getMemberUsers().add(larmstrong);
        managers.getMemberGroups().add(directors);

        Group developers = new Group("developers", "All FooBar developers");
        realm.getRootGroups().add(developers);

        Group engineers = new Group("engineers", "All FooBar engineers");
        engineers.getMemberUsers().add(stiger);
        developers.getMemberGroups().add(engineers);

        // realm.map(customer1, app.getRole("user"));
        realm.map(managers, app.findRole("admin"));

        getDefaultHost().attach(app);
        getServers().add(Protocol.HTTP, RestletTestCase.TEST_PORT);
        
        ////
        ChallengeResponse#isAuthenticated
        public Restlet createRoot() {

    Router securedRoute = new MyRouter(getContext());

	securedRoute.add("/users",UsersResource.class);
	securedRoute.add("/user/{id}",UsersResource.class);
    Guard guard = new Guard(getContext(), ChallengeScheme.HTTP_BASIC, "whatever");
        guard.setNext(securedRoute);
    return guard;
  }
        */

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