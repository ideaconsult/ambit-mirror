package ambit2.rest;
import java.io.InputStream;
import java.io.StringWriter;
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
import java.util.logging.Logger;

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
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.ChallengeGuard;
import org.restlet.security.Enroler;
import org.restlet.security.MethodAuthorizer;
import org.restlet.security.UniformGuard;
import org.restlet.security.Verifier;
import org.restlet.util.RouteList;

import ambit2.base.config.Preferences;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.rest.aa.DBVerifier;
import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.algorithm.quantumchemical.Build3DResource;
import ambit2.rest.algorithm.util.Name2StructureResource;
import ambit2.rest.dataset.DatasetCompoundResource;
import ambit2.rest.dataset.DatasetResource;
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
import ambit2.rest.property.PropertyModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.pubchem.CSLSResource;
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
		setName("AMBIT REST services");
		setDescription("AMBIT implementation of OpenTox framework");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");		
		tasks = new ConcurrentHashMap<UUID,Task<Reference>>();
		/*
		String tmpDir = System.getProperty("java.io.tmpdir");
        File logFile = new File(tmpDir,"ambit2-www.log");		
		System.setProperty("java.util.logging.config.file",logFile.getAbsolutePath());
		*/

		//connectionURI = null;
		setStatusService(new AmbitStatusService());

		getTaskService().setEnabled(true);
		getTunnelService().setUserAgentTunnel(true);
		
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

			if (getContext().getParameters().getFirstValue(Preferences.DATABASE)!=null)
				li.setDatabase(getContext().getParameters().getFirstValue(Preferences.DATABASE));
			if (getContext().getParameters().getFirstValue(Preferences.USER)!=null)
				li.setUser(getContext().getParameters().getFirstValue(Preferences.USER));
			if (getContext().getParameters().getFirstValue(Preferences.PASSWORD)!=null)
				li.setPassword(getContext().getParameters().getFirstValue(Preferences.PASSWORD));
			if (getContext().getParameters().getFirstValue(Preferences.HOST)!=null)
				li.setHostname(getContext().getParameters().getFirstValue(Preferences.HOST));
			if (getContext().getParameters().getFirstValue(Preferences.PORT)!=null)
				li.setPort(getContext().getParameters().getFirstValue(Preferences.PORT));
		
			//li.setDatabase("echa");
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
		

		router.attach("", SmartsQueryResource.class);	
		router.attach("/", SmartsQueryResource.class);
		//test
		router.attach(RDFGraphResource.resource,RDFGraphResource.class);
		router.attach(RDFGraphResource.resource+"/test",OntologyPlayground.class);
		
		Router fastoxRouter = new Router(getContext());
		router.attach(FastToxStep1.resource,fastoxRouter);
		fastoxRouter.attachDefault(FastToxStep1.class);
		fastoxRouter.attach("/step2", FastToxStep2.class);

		Router ttcRouter = new Router(getContext());
		router.attach("/ttc",ttcRouter);
		ttcRouter.attachDefault(KroesStep1.class);
		ttcRouter.attach("/step2", KroesStep2.class);
		ttcRouter.attach("/input", KroesInput.class);
		
		
		router.attach(OntologyResource.resource, OntologyResource.class);
		router.attach(OntologyResource.resourceID, OntologyResource.class);
		router.attach(OntologyResource.resourceTree, OntologyResource.class);
		

		Router allDatasetsRouter = new Router(getContext());
		allDatasetsRouter.attachDefault(DatasetsResource.class);
		router.attach(DatasetsResource.datasets, allDatasetsRouter);		

		
		Router datasetRouter = new Router(getContext());
		datasetRouter.attachDefault(DatasetResource.class);
		//this is for backward compatibility
		router.attach(String.format("%s",DatasetResource.dataset), DatasetsResource.class);
		router.attach(String.format("%s/{%s}",DatasetResource.dataset,DatasetResource.datasetKey), datasetRouter);
		router.attach(String.format("%s/{%s}/metadata",DatasetResource.dataset,DatasetResource.datasetKey), DatasetsResource.class);
		
		
		DBVerifier verifier = new DBVerifier(this);
		/**
		 * /user
		 */
		Router usersRouter = new Router(getContext());
		usersRouter.attachDefault(UserResource.class);
	 	
		/**
		 * /user/{userid}
		 */
		Router userRouter = new Router(getContext());
		userRouter.attachDefault(UserResource.class);
	 	usersRouter.attach(UserResource.resourceID,userRouter);
	 	
	 	/**
	 	 *  authentication mandatory for users resource
	 	 */
		Filter guard = createGuard(verifier,false);
		guard.setNext(usersRouter);
	 	router.attach(UserResource.resource, guard);

	 			
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
		
		Router compoundsRouter = new Router(getContext());
		compoundsRouter.attachDefault(CompoundResource.class);
		router.attach(CompoundResource.compound,compoundsRouter);
		
		Router compoundRouter = new Router(getContext());
		compoundRouter.attachDefault(CompoundResource.class);
		compoundsRouter.attach(String.format("/{%s}",CompoundResource.idcompound),compoundRouter);
	
		Router conformersRouter = new Router(getContext());
		conformersRouter.attachDefault(ConformerResource.class);
		compoundRouter.attach(ConformerResource.conformerKey,conformersRouter);		
		
		Router conformerRouter = new Router(getContext());
		conformerRouter.attachDefault(ConformerResource.class);
		conformersRouter.attach(String.format("/{%s}",ConformerResource.idconformer),conformerRouter);	

		compoundRouter.attach(PropertyValueResource.featureKey,PropertyValueResource.class);

		Router featureByAlias = new Router(getContext());
		featureByAlias.attachDefault(PropertyValueResource.class);
		
		compoundRouter.attach(String.format("%s/{name}",PropertyValueResource.featureKey),featureByAlias);
		conformerRouter.attach(String.format("%s/{name}",PropertyValueResource.featureKey),featureByAlias);
		
		//router.attach(PropertyValueResource.compoundFeatureName,PropertyValueResource.class);
		//router.attach(PropertyValueResource.conformerFeatureName,PropertyValueResource.class);
		
		router.attach(PropertyValueResource.FeatureNameCompound,PropertyValueResource.class);
		router.attach(PropertyValueResource.FeatureNameConformer,PropertyValueResource.class);

		Router templateRouter = new Router(getContext());
		templateRouter.attachDefault(PropertyTemplateResource.class);
		templateRouter.attach(PropertyTemplateResource.resourceID,PropertyTemplateResource.class);
		
		compoundRouter.attach(PropertyTemplateResource.resource,templateRouter);
		conformerRouter.attach(PropertyTemplateResource.resource,templateRouter);
		/*
		router.attach(PropertyTemplateResource.compoundTemplate,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.compoundTemplateID,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.conformerTemplateID,PropertyTemplateResource.class);

		router.attach(PropertyTemplateResource.TemplateIDCompound,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.TemplateIDConformer,PropertyTemplateResource.class);
		*/
		
		Router modelsRouter = new Router(getContext());
		modelsRouter.attachDefault(ModelResource.class);
		router.attach(ModelResource.resource,modelsRouter);
		
		Router modelRouter = new Router(getContext());
		modelRouter.attachDefault(ModelResource.class);
		modelsRouter.attach(String.format("/{%s}",ModelResource.resourceKey),modelRouter);
		
		modelsRouter.attach(String.format("/{%s}%s",ModelResource.resourceKey,PropertyModelResource.resourceID),PropertyModelResource.class);
		
		Router tupleRouter = new Router(getContext());
		tupleRouter.attachDefault(TupleResource.class);
		tupleRouter.attach(String.format("/{%s}", TupleResource.resourceKey),TuplePropertyValueResource.class);

		compoundRouter.attach(TupleResource.resourceTag,tupleRouter);
		conformerRouter.attach(TupleResource.resourceTag,tupleRouter);
		
		Router referenceRouter = new Router(getContext());
		router.attach(ReferenceResource.reference,referenceRouter);
		referenceRouter.attachDefault(ReferenceResource.class);
		referenceRouter.attach(String.format("/{%s}",ReferenceResource.idreference),ReferenceResource.class);

		Router feature_def = new Router(getContext());
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
		


		
		Router depictRouter = new Router();
		router.attach("/depict",depictRouter);
		depictRouter.attachDefault(AbstractDepict.class);
		depictRouter.attach("/daylight",DaylightDepict.class);
		depictRouter.attach("/cdk",CDKDepict.class);
		depictRouter.attach("/cactvs",CSLSDepict.class);

		
		router.attach("/name2structure",Name2StructureResource.class);	
		
		router.attach(String.format("/%s",Build3DResource.resource),Build3DResource.class);	
		router.attach(PropertyQueryResource.property,PropertyQueryResource.class);
		
		/**
		 * Queries
		 */
		Router queryRouter = new Router(getContext());
		router.attach(QueryResource.query_resource,queryRouter);
		queryRouter.attachDefault(QueryListResource.class);
		queryRouter.attach(QLabelQueryResource.resource,QLabelQueryResource.class);
		
		Router queryResults = new Router(getContext());
		queryResults.attachDefault(QueryResultsResource.class);
		queryResults.attach(QueryResultsResource.resourceID,QueryResultsResource.class);
		queryRouter.attach(QueryResultsResource.resource,queryResults);
		
		queryRouter.attach(QueryDatasetResource.datasetName, QueryDatasetResource.class);
		
		datasetRouter.attach(String.format("%s%s",QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
				
		Router pubchem = new Router(getContext());
		queryRouter.attach(PubchemResource.resource,pubchem);
		pubchem.attachDefault(PubchemResource.class);
		pubchem.attach(PubchemResource.resourceID,PubchemResource.class);

		Router csls = new Router(getContext());
		queryRouter.attach(CSLSResource.resource,csls);
		csls.attachDefault(CSLSResource.class);
		csls.attach(CSLSResource.resourceID,CSLSResource.class);
		csls.attach(CSLSResource.resourceID+CSLSResource.representationID,CSLSResource.class);
		
		//router.attach("/smiles/{smiles}"+fp,SimilarityResource.class);
		//router.attach("/smiles/{smiles}"+fp_dataset,SimilarityResource.class);
		Router similarity = new Router(getContext());
		queryRouter.attach(SimilarityResource.resource,similarity);
		similarity.attachDefault(SimilarityResource.class);
		//search dataset for similar compounds
		datasetRouter.attach(SimilarityResource.resource,similarity);	
		
		Router smartsRouter = new Router(getContext());
		smartsRouter.attachDefault(SmartsQueryResource.class);
		smartsRouter.attach(SmartsQueryResource.resourceID,SmartsQueryResource.class);
		queryRouter.attach(SmartsQueryResource.resource,smartsRouter);
		
		datasetRouter.attach(SmartsQueryResource.resource,smartsRouter);
		compoundRouter.attach(SmartsQueryResource.resource,smartsRouter);
		
		
		Router algoRouter = new Router(getContext());
		algoRouter.attachDefault(AlgorithmResource.class);
		router.attach(AlgorithmResource.algorithm,algoRouter);
		router.attach(AlgorithmResource.resourceID,algoRouter);

		Router taskRouter = new Router(getContext());
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
		
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory jmeDir = new Directory(getContext(), "war:///jme");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 Directory jsDir = new Directory(getContext(), "war:///js");

		 
		 
		 router.attach("/images/", imgDir);
		 router.attach("/jmol/", jmolDir);
		 router.attach("/jme/", jmeDir);
		 router.attach("/style/", styleDir);
		 router.attach("/js/", jsDir);
		 router.attach("/favicon.ico", FavIconResource.class);
		 router.attach("/favicon.png", FavIconResource.class);



	     router.attach(SwitchUserResource.resource,createGuardGuest(SwitchUserResource.class));
	     /*
	     StringWriter w = new StringWriter();
	     printRoutes(router, ">",w);
	     System.out.println(w);
	     */
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
			Logger.getLogger(getClass().getName()).severe(x.toString());
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
				String.format("%s%s/%s", baseReference.toString(),TaskResource.resource,Reference.encode(uuid.toString())));
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
    	
    protected UniformGuard createGuard(Verifier verifier,boolean optional) {
    	
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

    Router securedRoute = new Router(getContext());

	securedRoute.add("/users",UsersResource.class);
	securedRoute.add("/user/{id}",UsersResource.class);
    Guard guard = new Guard(getContext(), ChallengeScheme.HTTP_BASIC, "whatever");
        guard.setNext(securedRoute);
    return guard;
  }
        */

