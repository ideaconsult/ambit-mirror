package ambit2.rest;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;

import javax.security.auth.Subject;
import javax.sql.DataSource;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.ChallengeGuard;
import org.restlet.security.Enroler;
import org.restlet.security.MethodAuthorizer;
import org.restlet.security.UniformGuard;
import org.restlet.security.Verifier;

import ambit2.base.config.Preferences;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.rest.aa.DBVerifier;
import ambit2.rest.algorithm.AlgorithmCatalogResource;
import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.algorithm.descriptors.AlgorithmDescriptorTypesResource;
import ambit2.rest.algorithm.quantumchemical.Build3DResource;
import ambit2.rest.algorithm.util.AlgorithmUtilTypesResource;
import ambit2.rest.algorithm.util.Name2StructureResource;
import ambit2.rest.dataset.DatasetCompoundResource;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.dataset.QueryDatasetResource;
import ambit2.rest.fastox.FastToxStep1;
import ambit2.rest.fastox.FastToxStep2;
import ambit2.rest.fastox.KroesInput;
import ambit2.rest.fastox.KroesStep1;
import ambit2.rest.fastox.KroesStep2;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertiesByDatasetResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.propertyvalue.PropertyModelResource;
import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.PropertyQueryResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.queryresults.QueryResultsResource;
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
	protected Properties properties = null;

	//protected String connectionURI;
	protected DataSource datasource = null;

	public AmbitApplication() {
		super();
		tasks = new ConcurrentHashMap<UUID,Task<Reference>>();
		/*
		String tmpDir = System.getProperty("java.io.tmpdir");
        File logFile = new File(tmpDir,"ambit2-www.log");		
		System.setProperty("java.util.logging.config.file",logFile.getAbsolutePath());
		*/

		//connectionURI = null;
		setStatusService(new AmbitStatusService());
		getTaskService().setEnabled(true);
	}
	protected void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}	
	protected LoginInfo getLoginInfo() {
		loadProperties();
		LoginInfo li = new LoginInfo();
		String p = properties.getProperty("Database");
		li.setDatabase(p==null?"ambit2":p);
		p = properties.getProperty("Port");
		li.setPort(p==null?"3306":p);		
		p = properties.getProperty("User");
		li.setUser(p==null?"guest":p);			
		p = properties.getProperty("Password");
		li.setPassword(p==null?"guest":p);	
		return li;
	}
	protected String getConnectionURI() throws AmbitException {
		return getConnectionURI(null,null);
	}
	protected String getConnectionURI(Request request) throws AmbitException {
		if (request == null) return getConnectionURI(null,null);
		else if (request.getChallengeResponse()==null)
			return getConnectionURI(null,null);
		else try {
			return getConnectionURI(request.getChallengeResponse().getIdentifier(),
					new String(request.getChallengeResponse().getSecret()));
		} catch (Exception x) {
			return getConnectionURI(null,null);
		}
	}
	
	protected String getConnectionURI(String user,String password) throws AmbitException {
	
		try {
			LoginInfo li = getLoginInfo();
			
			if (getContext().getParameters().size()>0) {
				li.setDatabase(getContext().getParameters().getFirstValue(Preferences.DATABASE));
				li.setUser(getContext().getParameters().getFirstValue(Preferences.USER));
				li.setPassword(getContext().getParameters().getFirstValue(Preferences.PASSWORD));
				li.setHostname(getContext().getParameters().getFirstValue(Preferences.HOST));
				li.setPort(getContext().getParameters().getFirstValue(Preferences.PORT));
			}
			
			return DatasourceFactory.getConnectionURI(
	                li.getScheme(), li.getHostname(), li.getPort(), 
	                li.getDatabase(), user==null?li.getUser():user, password==null?li.getPassword():password); 
		} catch (Exception x) {
			throw new AmbitException(x);
		} 
				
	}
	
	@Override
	public Restlet createRoot() {
		Router router = new Router(this.getContext());
		router.attach("/help", AmbitResource.class);
		
		router.attach("/", AmbitResource.class);
		router.attach("", AmbitResource.class);	
		router.attach(FastToxStep1.resource, FastToxStep1.class);
		router.attach(FastToxStep2.resource, FastToxStep2.class);

		router.attach("/ttc", KroesStep1.class);
		router.attach("/ttc/step2", KroesStep2.class);
		router.attach("/ttc/input", KroesInput.class);
		
		
		router.attach(OntologyResource.resource, OntologyResource.class);
		router.attach(OntologyResource.resourceID, OntologyResource.class);

		router.attach(DatasetsResource.datasetID, DatasetsResource.class);
		router.attach(QueryDatasetResource.datasetName, QueryDatasetResource.class);
		
		DBVerifier verifier = new DBVerifier(this);

	 	router.attach(UserResource.resource, createGuard(UserResource.class,verifier,false));
	 	router.attach(UserResource.resourceID, createGuard(UserResource.class,verifier,false));
	 	router.attach(DatasetsResource.datasets, DatasetsResource.class);		
		router.attach(FeatureResource.CompoundFeaturedefID,FeatureResource.class);
		router.attach(FeatureResource.ConformerFeaturedefID,FeatureResource.class);

	 	/*
	 	router.attach(DatasetsResource.datasets, createGuard(DatasetsResource.class,verifierOptional,true));		
		router.attach(FeatureResource.CompoundFeaturedefID,createGuard(FeatureResource.class,verifierOptional,true));
		router.attach(FeatureResource.ConformerFeaturedefID,createGuard(FeatureResource.class,verifierOptional,true));
*/
		router.attach(DatasetCompoundResource.resource, DatasetCompoundResource.class);
		//router.attach(datasetID_structure_media, DatasetCompoundResource.class);
		
		router.attach(DatasetStructuresResource.resource, DatasetStructuresResource.class);
		
		//router.attach("/smiles/{smiles}"+fp,SimilarityResource.class);
		//router.attach("/smiles/{smiles}"+fp_dataset,SimilarityResource.class);
		router.attach(SimilarityResource.fp+"/smiles/{smiles}",SimilarityResource.class);
		router.attach(SimilarityResource.fp_dataset+"/smiles/{smiles}",SimilarityResource.class);		
		
		//router.attach("/cas/{cas}"+fp,SimilarityResource.class);
		//router.attach("/name/{name}"+fp,SimilarityResource.class);		
		router.attach(CompoundResource.compound,CompoundResource.class);
		router.attach(CompoundResource.compoundID,CompoundResource.class);
		router.attach(CompoundResource.compoundID_media, CompoundResource.class);		
		router.attach(ConformerResource.conformers,ConformerResource.class);
		router.attach(ConformerResource.conformerID,ConformerResource.class);
		router.attach(ConformerResource.conformerID_media, ConformerResource.class);		

		router.attach(PropertyValueResource.compoundFeature,PropertyValueResource.class);
		router.attach(PropertyValueResource.compoundFeatureName,PropertyValueResource.class);
		router.attach(PropertyValueResource.conformerFeatureName,PropertyValueResource.class);

		router.attach(PropertyValueResource.FeatureNameCompound,PropertyValueResource.class);
		router.attach(PropertyValueResource.FeatureNameConformer,PropertyValueResource.class);

		router.attach(PropertyTemplateResource.compoundTemplate,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.compoundTemplateID,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.conformerTemplateID,PropertyTemplateResource.class);

		router.attach(PropertyTemplateResource.TemplateIDCompound,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.TemplateIDConformer,PropertyTemplateResource.class);
		
		router.attach(PropertyModelResource.compoundModel,PropertyModelResource.class);
		router.attach(PropertyModelResource.compoundModelID,PropertyModelResource.class);
		router.attach(PropertyModelResource.conformerModelID,PropertyModelResource.class);
		
		router.attach(TupleResource.resource,TupleResource.class);
		router.attach(TuplePropertyValueResource.resourceCompoundID,TuplePropertyValueResource.class);
		router.attach(TuplePropertyValueResource.resourceConformerID,TuplePropertyValueResource.class);
		router.attach(TupleResource.resourceDataset,TupleResource.class);

		
		router.attach(ReferenceResource.referenceID,ReferenceResource.class);
		router.attach(ReferenceResource.reference,ReferenceResource.class);

		router.attach(PropertyResource.featuredef,PropertyResource.class);
		router.attach(PropertyResource.featuredefID,PropertyResource.class);
		router.attach(PropertyResource.CompoundFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.CompoundFeaturedefID,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedefID,PropertyResource.class);
		router.attach(PropertiesByDatasetResource.DatasetFeaturedef,PropertiesByDatasetResource.class);
		router.attach(PropertiesByDatasetResource.DatasetFeaturedefID,PropertiesByDatasetResource.class);
		
		router.attach(QueryResultsResource.resourceID,QueryResultsResource.class);
		router.attach(QueryResultsResource.resource,QueryResultsResource.class);
		
		router.attach(PubchemResource.resourceID,PubchemResource.class);
		router.attach(PubchemResource.resource,PubchemResource.class);
		
		router.attach("/depict/daylight",DaylightDepict.class);
		router.attach("/depict/cdk",CDKDepict.class);
		router.attach("/depict/cactvs",CSLSDepict.class);
		router.attach("/depict",AbstractDepict.class);
		router.attach("/name2structure",Name2StructureResource.class);	
		
		router.attach("/build3d/smiles/{smiles}",Build3DResource.class);	
		router.attach(PropertyQueryResource.property,PropertyQueryResource.class);
		
		router.attach(String.format("%s%s",QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
		router.attach(String.format("%s%s%s",DatasetsResource.datasetID,QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
				
		router.attach(SmartsQueryResource.smarts_resource,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.smartsID,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.dataset_smarts_resource,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.dataset_smarts_resource_id,SmartsQueryResource.class);
		
		router.attach(QueryResource.query_resource,QueryListResource.class);
		
		router.attach(AlgorithmCatalogResource.algorithm,AlgorithmCatalogResource.class);
		router.attach(String.format("%s/%s",AlgorithmCatalogResource.algorithm,AlgorithmCatalogResource.algorithmtypes.descriptorcalculation.toString()),
						AlgorithmDescriptorTypesResource.class);
		
		router.attach(String.format("%s/%s",AlgorithmCatalogResource.algorithm,"util"),
				AlgorithmUtilTypesResource.class);
		
		for (AlgorithmUtilTypesResource.utiltypes o : AlgorithmUtilTypesResource.utiltypes.values())
			router.attach(String.format("%s/%s/%s",
					AlgorithmCatalogResource.algorithm,
					"util",
					o.toString()
					),
					AlgorithmUtilTypesResource.class);
		
		for (AlgorithmDescriptorTypesResource.descriptortypes o : AlgorithmDescriptorTypesResource.descriptortypes.values())
			router.attach(String.format("%s/%s/%s/{%s}",
					AlgorithmCatalogResource.algorithm,
					AlgorithmCatalogResource.algorithmtypes.descriptorcalculation.toString(),
					o.toString(),
					AlgorithmDescriptorTypesResource.iddescriptor),
					AlgorithmDescriptorTypesResource.class);		
		
		router.attach(String.format("%s/rules/{%s}",AlgorithmCatalogResource.algorithm,AlgorithmResource.idalgorithm),AlgorithmResource.class);
		router.attach(String.format("%s/rules",AlgorithmCatalogResource.algorithm),AlgorithmResource.class);
		
		router.attach(ModelResource.resource,ModelResource.class);
		router.attach(ModelResource.resourceID,ModelResource.class);

		
		router.attach("/"+TaskResource.resource, TaskResource.class);
		router.attach(TaskResource.resourceID, TaskResource.class);
		/** 
        router.attach(
                "/images",
                new Directory(
                        getContext(),
                        LocalReference.createFileReference(
                                webRootPath + "/images")));		
                                */
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 Directory jsDir = new Directory(getContext(), "war:///js");

		 router.attach("/images/", imgDir);
		 router.attach("/jmol/", jmolDir);
		 router.attach("/style/", styleDir);
		 router.attach("/js/", jsDir);
		 router.attach("/favicon.ico", FavIconResource.class);
		 router.attach("/favicon.png", FavIconResource.class);



	     router.attach(SwitchUserResource.resource,createGuardGuest(SwitchUserResource.class));
	 	 
 
		 return router;
	}
	public synchronized Connection getConnection(String user, String password) throws AmbitException , SQLException{
		return getConnection(getConnectionURI(user, password));
	}
	public synchronized Connection getConnection() throws AmbitException , SQLException{
		//if (connectionURI == null)
		//	connectionURI = getConnectionURI();
		return getConnection(getConnectionURI(null));
	}

	public synchronized Connection getConnection(Request request) throws AmbitException , SQLException{
		//if (connectionURI == null)
		//	connectionURI = getConnectionURI();
		return getConnection(getConnectionURI(request));
	}
	public synchronized Connection getConnection(String connectionURI) throws AmbitException , SQLException{
		
		SQLException error = null;
		Connection c = null;
		for (int retry=0; retry< 3; retry++)
		try {
			c = DatasourceFactory.getDataSource(connectionURI).getConnection();
			Statement t = c.createStatement();
			ResultSet rs = t.executeQuery("SELECT 1");
			while (rs.next()) {rs.getInt(1);}
			rs.close();
			t.close();
			error= null;
			return c;
		} catch (SQLException x) {
			//TODO reinitialize the connection pool
			error = x;
			x.printStackTrace();
			//remove the connection from the pool
			try {if (c != null) c.close();} catch (Exception e) {}
		} finally {
			
		}
		if (error != null) throw error; else throw new SQLException("Can't establish connection "+connectionURI);
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
				String.format("%s/%s/%s", baseReference.toString(),TaskResource.resource,Reference.encode(uuid.toString())));
		task.setUri(ref);
		tasks.put(uuid,task);
		getTaskService().submit(futureTask);	
		return ref;
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
        Component component = new Component();
        final Server server = component.getServers().add(Protocol.HTTP, 8080);
       
        AmbitApplication application = new AmbitApplication();
        application.setContext(component.getContext().createChildContext());

        // Attach the application to the component and start it
        component.getDefaultHost().attach(application);
        component.start();

        System.out.println("Server started on port " + server.getPort());
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        component.stop();
        System.out.println("Server stopped");
    }
    	
    protected UniformGuard createGuard(Class clazz,Verifier verifier,boolean optional) {
    	
    	Enroler enroler = new Enroler() {
    		@Override
    		public void enrole(Subject subject) {
    			System.out.println(subject);
    			
    		}
    	};
    	/*
    	 * Simple authorizer
    	 */
    	MethodAuthorizer authorizer = new MethodAuthorizer();
    	authorizer.getAnonymousMethods().add(Method.GET);
    	authorizer.getAnonymousMethods().add(Method.HEAD);
    	authorizer.getAnonymousMethods().add(Method.OPTIONS);
    	authorizer.getAuthenticatedMethods().add(Method.GET);
    	authorizer.getAuthenticatedMethods().add(Method.PUT);
    	authorizer.getAuthenticatedMethods().add(Method.DELETE);
    	authorizer.getAuthenticatedMethods().add(Method.POST);
    	authorizer.getAuthenticatedMethods().add(Method.OPTIONS);
	        // Create a Guard
	     ChallengeGuard guard = new ChallengeGuard(getContext(),
	                ChallengeScheme.HTTP_BASIC, "ambit2");
	     ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(),optional,ChallengeScheme.HTTP_BASIC, "ambit2") {
	    	@Override
	    	protected boolean authenticate(Request request, Response response) {
	    		return super.authenticate(request, response);
	    	} 
	     };
	     guard.setAuthenticator(authenticator);
	     guard.getAuthenticator().setVerifier(verifier);
	     guard.getAuthenticator().setEnroler(enroler);
	     guard.setAuthorizer(authorizer);
		 guard.setNext(clazz);     
 		 
		 return guard;
    }
    
   protected UniformGuard createGuardGuest(Class clazz) {
    	
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
    		@Override
    		public void enrole(Subject subject) {
    			System.out.println(subject);
    			
    		}
    	};

	        // Create a Guard
	     ChallengeGuard guard = new ChallengeGuard(getContext(),
	                ChallengeScheme.HTTP_BASIC, "ambit2");
	     guard.setAuthenticator(new ChallengeAuthenticator(getContext(),ChallengeScheme.HTTP_BASIC, "ambit2") {

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
	
	     });
	     guard.getAuthenticator().setVerifier(verifier);
	     guard.getAuthenticator().setEnroler(enroler);
		 guard.setNext(clazz);     
 		 
		 return guard;
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

    Router securedRoute = new Router(getContext());

	securedRoute.add("/users",UsersResource.class);
	securedRoute.add("/user/{id}",UsersResource.class);
    Guard guard = new Guard(getContext(), ChallengeScheme.HTTP_BASIC, "whatever");
        guard.setNext(securedRoute);
    return guard;
  }
        */

