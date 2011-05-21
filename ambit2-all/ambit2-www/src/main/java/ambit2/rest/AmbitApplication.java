package ambit2.rest;
import java.io.StringWriter;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.Verifier;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;

import ambit2.base.config.Preferences;
import ambit2.rest.aa.DBVerifier;
import ambit2.rest.aa.opensso.BookmarksAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOAuthenticator;
import ambit2.rest.aa.opensso.OpenSSOAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOVerifierSetUser;
import ambit2.rest.aa.opensso.users.OpenSSOUserResource;
import ambit2.rest.admin.AdminResource;
import ambit2.rest.admin.DBCreateAllowedGuard;
import ambit2.rest.admin.PolicyResource;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.algorithm.chart.ChartResource;
import ambit2.rest.algorithm.util.Name2StructureResource;
import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.dataset.MissingFeatureValuesResource;
import ambit2.rest.dataset.filtered.FilteredDatasetResource;
import ambit2.rest.facet.CompoundsByPropertyValueInDatasetResource;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.facet.DatasetsByNamePrefixResource;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.pubchem.CSLSResource;
import ambit2.rest.pubchem.ChEBIResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.ExactStructureQueryResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.report.ReportDatasetResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.routers.misc.AdminRouter;
import ambit2.rest.routers.misc.BookmarksRouter;
import ambit2.rest.routers.misc.ChartRouter;
import ambit2.rest.routers.misc.DataEntryRouter;
import ambit2.rest.routers.misc.DepictDemoRouter;
import ambit2.rest.routers.opentox.AlgorithmRouter;
import ambit2.rest.routers.opentox.CompoundRouter;
import ambit2.rest.routers.opentox.CompoundsRouter;
import ambit2.rest.routers.opentox.DatasetsRouter;
import ambit2.rest.routers.opentox.FeaturesRouter;
import ambit2.rest.routers.opentox.ModelRouter;
import ambit2.rest.routers.opentox.TaskRouter;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.sparqlendpoint.SPARQLPointerResource;
import ambit2.rest.structure.CompoundLookup;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.rest.task.PolicyProtectedTask;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskResource;
import ambit2.rest.task.TaskResult;
import ambit2.rest.task.TaskStorage;
import ambit2.rest.users.SwitchUserResource;

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
public class AmbitApplication extends TaskApplication<String> {

	public AmbitApplication() {
		super();
		setName("AMBIT REST services");
		setDescription("AMBIT implementation of OpenTox framework");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");		

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
		getTunnelService().setExtensionsTunnel(false);

		Preferences.setProperty(Preferences.MAXRECORDS,"0");
		
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
		router.attach("/help", AmbitResource.class);
		
		router.attach("/", AmbitResource.class);
		router.attach("", AmbitResource.class);

		/**
		 *  Points to the Ontology service
		 *  /sparqlendpoint 
		 */
		router.attach(SPARQLPointerResource.resource, SPARQLPointerResource.class);
		
		/**		 *  /admin 
		 *  Various admin tasks, like database creation
		 */
		router.attach(String.format("/%s",AdminResource.resource),createAdminRouter());

		/** /policy - used for testing only  */
		router.attach(String.format("/%s",PolicyResource.resource),PolicyResource.class);		
		
		/** /feature */
		FeaturesRouter featuresRouter = new FeaturesRouter(getContext());
		router.attach(PropertyResource.featuredef,createProtectedResource(featuresRouter,"feature"));		

		//Filter openssoAuth = new OpenSSOAuthenticator(getContext(),false,"opentox.org");
		//Filter openssoAuthz = new OpenSSOAuthorizer();
		/** filter */
		router.attach(FilteredDatasetResource.resource,FilteredDatasetResource.class);
		/** /dataEntry */ 
		DataEntryRouter tupleRouter = new DataEntryRouter(getContext());
		/** Similarity search TODO: move it under /algorithm  */
		Router similarityRouter = createSimilaritySearchRouter();
		/**  SMARTS search.  TODO: move it under /algorithm  */
		Router smartsRouter = createSMARTSSearchRouter();
		/**  /compound  */
		CompoundsRouter compoundRouter = new CompoundsRouter(getContext(),featuresRouter,tupleRouter,smartsRouter);
		router.attach(CompoundResource.compound,createProtectedResource(compoundRouter,"compound"));
		
		/**
		 *  List of datasets 
		 *  /dataset , /datasets
		 */
		Router allDatasetsRouter = new MyRouter(getContext());
		allDatasetsRouter.attachDefault(DatasetsResource.class);
		
		router.attach(DatasetsResource.datasets, createProtectedResource(allDatasetsRouter,"datasets"));		

		Router datasetRouter = new DatasetsRouter(getContext(),tupleRouter, smartsRouter, similarityRouter);
		router.attach(DatasetResource.dataset,createProtectedResource(datasetRouter,"dataset"));
	

		/**  /algorithm  */
		router.attach(AllAlgorithmsResource.algorithm,createAuthenticatedOpenResource(new AlgorithmRouter(getContext())));
		/**  /model  */
		router.attach(ModelResource.resource,createAuthenticatedOpenResource(new ModelRouter(getContext())));
		/**  /task  */
		router.attach(TaskResource.resource, new TaskRouter(getContext()));
		
		/**
		 * Queries
		 *  /query
		 *  
		 */
		Router queryRouter = createQueryRouter();
		router.attach(QueryResource.query_resource,queryRouter);
		queryRouter.attach(SmartsQueryResource.resource,smartsRouter);
		queryRouter.attach(SimilarityResource.resource,similarityRouter);
		queryRouter.attach(ExactStructureQueryResource.resource,ExactStructureQueryResource.class);
		
		/**
		 *  API extensions from this point on
		 */

		/**
		 * OpenSSO login / logout
		 * Sets a cookie with OpenSSO token
		 */
		router.attach("/"+OpenSSOUserResource.resource,createOpenSSOLoginRouter() );
		
		/**
		 * Dataset reporting  /report
		 * Practically same as /dataset , but allows POST , so that long URIs with features could be send
		 */
		router.attach(ReportDatasetResource.resource,ReportDatasetResource.class);
		/**  /bookmark  */
		router.attach(BookmarkResource.resource,createBookmarksRouter());				
		/** /chart  */
		router.attach(ChartResource.resource,new ChartRouter(getContext()));
	
		/**
		 * Demos
		 */
		router.attach(AbstractDepict.resource,new DepictDemoRouter(getContext()));
		router.attach("/name2structure",Name2StructureResource.class);	
	
		/**
		 * Images, styles, favicons, applets
		 */
		attachStaticResources(router);


		 /**
		  * login/logout for local users . TODO refactor to use cookies as in /opentoxuser
		  */
	     router.attach(SwitchUserResource.resource,createGuardGuest(SwitchUserResource.class));

	     router.setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
	     router.setRoutingMode(Router.MODE_BEST_MATCH); 
	     
	     //StringWriter w = new StringWriter();
	     //AmbitApplication.printRoutes(router,">",w);
	     //System.out.println(w.toString());

		 return router;
	}
	
	protected Restlet createOpenSSOLoginRouter() {
		Filter userAuthn = new OpenSSOAuthenticator(getContext(),true,"opentox.org",new OpenSSOVerifierSetUser(false));
		userAuthn.setNext(OpenSSOUserResource.class);
		return userAuthn;
	}

	protected Restlet createProtectedResource(Router router) {
		return createProtectedResource(router,null);
	}
	protected Restlet createProtectedResource(Router router,String prefix) {
		Filter authN = new OpenSSOAuthenticator(getContext(),false,"opentox.org",new OpenSSOVerifierSetUser(false));
		OpenSSOAuthorizer authZ = new OpenSSOAuthorizer();
		authZ.setPrefix(prefix);
		authN.setNext(authZ);
		authZ.setNext(router);		
		return authN;
	}
	


	protected Router createSimilaritySearchRouter() {
		Router similarity = new MyRouter(getContext());
		similarity.attachDefault(SimilarityResource.class);
		return similarity;
	}
	protected Router createSMARTSSearchRouter() {
		Router smartsRouter = new MyRouter(getContext());
		smartsRouter.attachDefault(SmartsQueryResource.class);
		smartsRouter.attach(SmartsQueryResource.resourceID,SmartsQueryResource.class);
		return smartsRouter;
	}
	/**
	 * Everything under /query
	 * @return
	 */
	protected Router createQueryRouter() {
		
		Router queryRouter = new MyRouter(getContext());
		queryRouter.attachDefault(QueryListResource.class);
		
		/**
		 * Quality labels
		 */
		queryRouter.attach(QLabelQueryResource.resource,QLabelQueryResource.class);
		
		
		/**
		 * Missing features
		 * /missingValues  - there is  /filter now, may be this is redundant ?
		 */
		queryRouter.attach(MissingFeatureValuesResource.resource,MissingFeatureValuesResource.class);
		
		/**
		 * Facets
		 */
		queryRouter.attach(CompoundsByPropertyValueInDatasetResource.resource,CompoundsByPropertyValueInDatasetResource.class);
		queryRouter.attach(DatasetsByEndpoint.resource,DatasetsByEndpoint.class);
		queryRouter.attach(DatasetsByNamePrefixResource.resource,DatasetsByNamePrefixResource.class);
		
		/**
		 *  PubChem query
		 */
		Router pubchem = new MyRouter(getContext());
		queryRouter.attach(PubchemResource.resource,pubchem);
		pubchem.attachDefault(PubchemResource.class);
		pubchem.attach(PubchemResource.resourceID,PubchemResource.class);

		/**
		 * CIR query
		 */
		Router cir = new MyRouter(getContext());
		queryRouter.attach(CSLSResource.resource,cir);
		cir.attachDefault(CSLSResource.class);
		cir.attach(CSLSResource.resourceID,CSLSResource.class);
		cir.attach(CSLSResource.resourceID+CSLSResource.representationID,CSLSResource.class);

		/**
		 * ChEBI query
		 */
		Router chebi = new MyRouter(getContext());
		queryRouter.attach(ChEBIResource.resource,chebi);
		chebi.attachDefault(ChEBIResource.class);
		chebi.attach(ChEBIResource.resourceID,ChEBIResource.class);

		/**
		 * Compound search
		 * /query/compound/lookup
		 */
		Router lookup = new MyRouter(getContext());
		queryRouter.attach(CompoundLookup.resource,lookup);
		lookup.attachDefault(CompoundLookup.class);
		lookup.attach(CompoundLookup.resourceID,CompoundLookup.class);
		lookup.attach(CompoundLookup.resourceID+CompoundLookup.representationID,CompoundLookup.class);		
		
		return queryRouter;
	}

	/**
	 * Check for OpenSSO token and set the user, if available
	 * but don't verify the policy
	 * @return
	 */
	protected Restlet createAuthenticatedOpenResource(Router router) {
		Filter algAuthn = new OpenSSOAuthenticator(getContext(),false,"opentox.org",new OpenSSOVerifierSetUser(false));
		algAuthn.setNext(router);
		return algAuthn;
	}

	protected TaskStorage<String> createTaskStorage() {
		return new TaskStorage<String>(getName(),getLogger()) {
			@Override
			protected Task<TaskResult, String> createTask(String user) {

				return new PolicyProtectedTask(user) {
					@Override
					public synchronized void setPolicy() throws Exception {
							//skip policy for now
						super.setPolicy();
					}
				};
			}
		};
	}

	/**
	 * Resource /bookmark
	 * @return
	 */
	protected Restlet createBookmarksRouter() {
		BookmarksRouter bookmarkRouter = new BookmarksRouter(getContext());

		Filter bookmarkAuth = new OpenSSOAuthenticator(getContext(),false,"opentox.org");
		Filter bookmarkAuthz = new BookmarksAuthorizer();		
		bookmarkAuth.setNext(bookmarkAuthz);
		bookmarkAuthz.setNext(bookmarkRouter);
		return bookmarkAuth;
	}
	/**
	 * Resource /admin
	 * @return
	 */
	protected Restlet createAdminRouter() {
		AdminRouter adminRouter = new AdminRouter(getContext());
		DBCreateAllowedGuard sameIPguard = new DBCreateAllowedGuard();
		sameIPguard.setNext(adminRouter);
		return sameIPguard;
	}
	/**
	 *  /ontology RDF playground, not used currently
	 * @return
	 */
	protected Restlet createRDFPlayground() {
		//test removed, there is ontology service
		//router.attach(RDFGraphResource.resource,RDFGraphResource.class);
		//router.attach(RDFGraphResource.resource+"/test",OntologyPlayground.class);

		//router.attach(OntologyResource.resource, OntologyResource.class);
		//router.attach(OntologyResource.resourceID, OntologyResource.class);
		//router.attach(OntologyResource.resourceTree, OntologyResource.class);
		return null;
	}
	/**
	 * An attempt to retrieve datasets by an optimized query
	 * Not used currently 
	 * @return
	 */
	protected Restlet createFastDatasetResource() {
		/*
		Router fastDatasetRouter = new MyRouter(getContext());
		
		fastDatasetRouter.attachDefault(FastDatasetStructuresResource.class);
		router.attach(String.format("%s",FastDatasetStructuresResource.resource), FastDatasetStructuresResource.class);
		router.attach(String.format("%s/{%s}",FastDatasetStructuresResource.resource,DatasetResource.datasetKey), fastDatasetRouter);
		router.attach(String.format("%s/{%s}/metadata",FastDatasetStructuresResource.resource,DatasetResource.datasetKey), DatasetsResource.class);
		

		fastDatasetRouter.attach(PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.class);
		fastDatasetRouter.attach(String.format("%s/{%s}",PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.idfeaturedef),PropertiesByDatasetResource.class);
		*/
		return null;
	}
	/**
	 * Resource protection via local MySQL/ Ambit database users.
	 * Not used currenty;
	 * @return
	 */
	protected Restlet createLocalUsersGuard() {
		
		//
		
		/**
		//   These are users from the DB
		//  /user
		DBVerifier verifier = new DBVerifier(this);
		Router usersRouter = new MyRouter(getContext());
		usersRouter.attachDefault(UserResource.class);
		//   /user/{userid}
		Router userRouter = new MyRouter(getContext());
		userRouter.attachDefault(UserResource.class);
	 	usersRouter.attach(UserResource.resourceID,userRouter);
	 	//  authentication mandatory for users resource
		Filter guard = createGuard(verifier,false);
		// Simple authorizer
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
		*/
		return null;
	}
	
		
	/**
	 * Images, styles, icons
	 * Works if packaged as war only!
	 * @return
	 */
	protected void attachStaticResources(Router router) {
		/*  router.attach("/images",new Directory(getContext(), LocalReference.createFileReference("/webapps/images")));   */

		 Directory metaDir = new Directory(getContext(), "war:///META-INF");
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory jmeDir = new Directory(getContext(), "war:///jme");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 Directory jquery = new Directory(getContext(), "war:///jquery");

		 
		 router.attach("/meta/", metaDir);
		 router.attach("/images/", imgDir);
		 router.attach("/jmol/", jmolDir);
		 router.attach("/jme/", jmeDir);
		 router.attach("/jquery/", jquery);
		 router.attach("/style/", styleDir);
		 router.attach("/favicon.ico", FavIconResource.class);
		 router.attach("/favicon.png", FavIconResource.class);
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
   /*
	@Override
	public ApplicationInfo getApplicationInfo(Request request, Response response) {

	        ApplicationInfo result = super.getApplicationInfo(request, response);

	        DocumentationInfo docInfo = new DocumentationInfo(getName());
	        docInfo.setTitle(getName());
	        docInfo.setLanguage(Language.ENGLISH);
	        docInfo.setTextContent(getDescription());
	        
	        result.setDocumentation(docInfo);

	        return result;
    }
*/

}

