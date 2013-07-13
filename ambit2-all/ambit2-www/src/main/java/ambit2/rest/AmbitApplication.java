package ambit2.rest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.engine.security.SslContextFactory;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.MapVerifier;
import org.restlet.security.Role;
import org.restlet.security.RoleAuthorizer;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;
import org.restlet.util.Series;

import ambit2.base.config.Preferences;
import ambit2.rest.aa.opensso.BookmarksAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOAuthenticator;
import ambit2.rest.aa.opensso.OpenSSOAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOVerifierSetUser;
import ambit2.rest.aa.opensso.policy.CallablePolicyCreator;
import ambit2.rest.aa.opensso.users.OpenSSOUserResource;
import ambit2.rest.admin.AdminResource;
import ambit2.rest.admin.PolicyResource;
import ambit2.rest.admin.SimpleGuard;
import ambit2.rest.admin.SimpleGuard.SimpleGuards;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.algorithm.chart.ChartResource;
import ambit2.rest.algorithm.util.Name2StructureResource;
import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.dataset.CollectionStructureResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.dataset.MissingFeatureValuesResource;
import ambit2.rest.dataset.filtered.FilteredDatasetResource;
import ambit2.rest.facet.CompoundsByPropertyValueInDatasetResource;
import ambit2.rest.facet.DatasetChemicalsQualityStatsResource;
import ambit2.rest.facet.DatasetStrucTypeStatsResource;
import ambit2.rest.facet.DatasetStructureQualityStatsResource;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.facet.DatasetsByNamePrefixResource;
import ambit2.rest.freemarker.FreeMarkerApplication;
import ambit2.rest.help.HelpResource;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.pubchem.CSLSResource;
import ambit2.rest.pubchem.ChEBIResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.ConsensusLabelQueryResource;
import ambit2.rest.query.ExactStructureQueryResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.query.StrucTypeQueryResource;
import ambit2.rest.report.ReportDatasetResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.routers.misc.AdminRouter;
import ambit2.rest.routers.misc.BookmarksRouter;
import ambit2.rest.routers.misc.ChartRouter;
import ambit2.rest.routers.misc.DataEntryRouter;
import ambit2.rest.routers.misc.DepictDemoRouter;
import ambit2.rest.routers.misc.UIRouter;
import ambit2.rest.routers.opentox.AlgorithmRouter;
import ambit2.rest.routers.opentox.CompoundInDatasetRouter;
import ambit2.rest.routers.opentox.CompoundsRouter;
import ambit2.rest.routers.opentox.DatasetsRouter;
import ambit2.rest.routers.opentox.FeaturesRouter;
import ambit2.rest.routers.opentox.ModelRouter;
import ambit2.rest.routers.opentox.TaskRouter;
import ambit2.rest.similarity.SimilarityMatrixResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.similarity.space.ChemicalSpaceResource;
import ambit2.rest.similarity.space.QMapDatasetResource;
import ambit2.rest.similarity.space.QMapResource;
import ambit2.rest.sparqlendpoint.SPARQLPointerResource;
import ambit2.rest.structure.CompoundLookup;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.rest.structure.tautomers.QueryTautomersResource;
import ambit2.rest.task.ICallableTask;
import ambit2.rest.task.PolicyProtectedTask;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskResource;
import ambit2.rest.task.TaskResult;
import ambit2.rest.task.TaskStorage;
import ambit2.rest.task.WarmupTask;
import ambit2.rest.template.OntologyResource;
import ambit2.rest.ui.UIResource;

/**
 * AMBIT implementation of OpenTox REST services as described in http://opentox.org/development/wiki/
 * http://opentox.org/wiki/1/Dataset
 * @author nina
 */

public class AmbitApplication extends FreeMarkerApplication<String> {
	public static final String BASE_URL = "BASE_URL";
	public static final Role UPDATE_ALLOWED = new Role("AUTHOR","Update (PUT,POST,DELETE) allowed");
	protected boolean insecure = true;
	protected Logger logger = Logger.getLogger(AmbitApplication.class.getName());	 
	protected Hashtable<String,Properties> properties = new Hashtable<String, Properties>();
	public static final String OPENTOX_AA_ENABLED = "aa.enabled";
	public static final String LOCAL_AA_ENABLED = "aa.local.enabled";
	public static final String GUARD_ENABLED = "guard.enabled";
	public static final String GUARD_LIST = "guard.list";
	public static final String WARMUP_ENABLED = "warmup.enabled";
	
	static final String identifierKey = "aa.local.admin.name";
	static final String identifierPass = "aa.local.admin.pass";
	static final String adminAAEnabled = "aa.admin";
	static final String compoundAAEnabled = "aa.compound";
	static final String featureAAEnabled = "aa.feature";
	static final String modelAAEnabled = "aa.model"; //ignored
	static final String version = "ambit.version";
	static final String version_build = "ambit.build";
	static final String version_timestamp = "ambit.build.timestamp";
	static final String ambitProperties = "ambit2/rest/config/ambit2.pref";
	static final String configProperties = "ambit2/rest/config/config.prop";
	static final String loggingProperties = "ambit2/rest/config/logging.prop";
 
	
	protected boolean standalone = false;
	protected boolean openToxAAEnabled = false;
	protected boolean localAAEnabled = false;
	protected boolean warmupEnabled = false;

	
	public AmbitApplication() {
		this(false);
	}
	public AmbitApplication(boolean standalone) {
		super();
		this.standalone = standalone;
		openToxAAEnabled = isOpenToxAAEnabled();
		localAAEnabled = isSimpleSecretAAEnabled();
		warmupEnabled = isWarmupEnabled();
		versionShort = readVersionShort();
		versionLong = readVersionLong();
		
		setName("AMBIT REST services");
		setDescription("AMBIT implementation of OpenTox framework");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");		

		InputStream in = null;
		try {
			URL url = getClass().getClassLoader().getResource(loggingProperties);
			System.setProperty("java.util.logging.config.file", url.getFile());
			in = new FileInputStream(new File(url.getFile()));
			LogManager.getLogManager().readConfiguration(in);
			logger.log(Level.INFO,String.format("Logging configuration loaded from %s",url.getFile()));
		} catch (Exception x) {
			System.err.println("logging configuration failed "+ x.getMessage());
		} finally {
			try { if (in!=null) in.close(); } catch (Exception x) {}
		}
		setStatusService(new AmbitStatusService());
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
		Restlet root = initInboundRoot();
		SimpleGuards guard = isSimpleGuardEnabled();
		

		if (!standalone && warmupEnabled) //only servlets are lazy
		addTask("warmup",new WarmupTask("warmup",openToxAAEnabled), new Reference("riap://component"),"guest");
		
		if (guard != null) {
			logger.log(Level.INFO,String.format("Property %s set, %s guard enabled.", GUARD_ENABLED, guard));
			String[] allowed = getGuardListAllowed();
			StringBuilder b = new StringBuilder();
			for (String ip : allowed) { b.append(ip); b.append(" "); }
			logger.log(Level.INFO,String.format("Property %s set, %s list allowed %s", GUARD_LIST, guard, b.toString()));
			SimpleGuard theguard = guard.getGuard(allowed,logger);
			theguard.setNext(root);
			return theguard;
		} else
			return root;
	
	}

	public Restlet initInboundRoot() {
        initFreeMarkerConfiguration();
        
		Router router = new MyRouter(this.getContext()) {
			public void handle(Request request, Response response) {
				//to use within riap calls
				String rootUrl = getContext().getParameters().getFirstValue(BASE_URL); 
				if ((rootUrl == null) && request.getRootRef().toString().startsWith("http")) { 
                    rootUrl = request.getRootRef().toString(); 
                    getContext().getParameters().set(BASE_URL,rootUrl,true);
				}
				super.handle(request, response);
			};
		};		
		router.attach("/help", AmbitResource.class);
		
		/**
		 *  Points to the Ontology service
		 *  /sparqlendpoint 
		 */
		router.attach(SPARQLPointerResource.resource, SPARQLPointerResource.class);
		
		/**		 *  /admin 
		 *  Various admin tasks, like database creation
		 */
		
		Restlet adminRouter = createAdminRouter();
		router.attach(String.format("/%s",AdminResource.resource),protectAdminResource()?createProtectedResource(adminRouter,"admin"):adminRouter);

		/** /policy - used for testing only  */
		router.attach(String.format("/%s",PolicyResource.resource),PolicyResource.class);		
		
		/** /feature */
		FeaturesRouter featuresRouter = new FeaturesRouter(getContext());
		
		
		if (openToxAAEnabled) {
			if (protectFeatureResource())
				router.attach(PropertyResource.featuredef,createProtectedResource(featuresRouter,"feature"));
			else {
				Filter cauthN = new OpenSSOAuthenticator(getContext(),false,"opentox.org",new OpenSSOVerifierSetUser(false));
				cauthN.setNext(featuresRouter);		
				router.attach(PropertyResource.featuredef,cauthN);
			}
		} else 	router.attach(PropertyResource.featuredef,featuresRouter);
		

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
		if (openToxAAEnabled) {
			if (protectCompoundResource())
				router.attach(CompoundResource.compound,createProtectedResource(compoundRouter,"compound"));
			else {
				Filter cauthN = new OpenSSOAuthenticator(getContext(),false,"opentox.org",new OpenSSOVerifierSetUser(false));
				cauthN.setNext(compoundRouter);		
				router.attach(CompoundResource.compound,cauthN);
			}
		} else 	router.attach(CompoundResource.compound,compoundRouter);
		/**
		 *  List of datasets 
		 *  /dataset , /datasets
		 */
		Router allDatasetsRouter = new MyRouter(getContext());
		allDatasetsRouter.attachDefault(DatasetsResource.class);
		
		router.attach(DatasetsResource.datasets, createProtectedResource(allDatasetsRouter,"datasets"));		

		//can reuse CompoundRouter from above
		CompoundInDatasetRouter cmpdRouter = new CompoundInDatasetRouter(getContext(), featuresRouter, tupleRouter, smartsRouter);
		Router datasetRouter = new DatasetsRouter(getContext(),cmpdRouter, tupleRouter, smartsRouter, similarityRouter);
		router.attach(DatasetResource.dataset,createProtectedResource(datasetRouter,"dataset"));
		
		//qmap
		router.attach(QMapResource.qmap,QMapResource.class);
		router.attach(String.format("%s/{%s}/metadata",QMapResource.qmap,QMapResource.qmapKey),QMapResource.class);
		router.attach(String.format("%s/{%s}",QMapResource.qmap,QMapResource.qmapKey),QMapDatasetResource.class);

		//collections
		MyRouter collectionRouter = new MyRouter(getContext());
		collectionRouter.attach(String.format("/{%s}/{%s}",CollectionStructureResource.folderKey,CollectionStructureResource.datasetKey),CollectionStructureResource.class);
		router.attach(CollectionStructureResource.collection,createProtectedResource(collectionRouter,"collection"));

		/**  /algorithm  */
		//router.attach(AllAlgorithmsResource.algorithm,createProtectedResource(new AlgorithmRouter(getContext())));
		router.attach(AllAlgorithmsResource.algorithm,createAuthenticatedOpenResource(new AlgorithmRouter(getContext())));
		/**  /model  */
		router.attach(ModelResource.resource,createAuthenticatedOpenResource(new ModelRouter(getContext())));
		/**  /task  */
		router.attach(TaskResource.resource, new TaskRouter(getContext()));
		
		router.attach("/ui",createAuthenticatedOpenResource(new UIRouter(getContext())));		

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
		queryRouter.attach(QueryTautomersResource.resource,QueryTautomersResource.class);
		/**
		 *  API extensions from this point on
		 */


		
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
		Router depict = new DepictDemoRouter(getContext());
		router.attach(AbstractDepict.resource,depict);
		router.attach("/name2structure",Name2StructureResource.class);	
	
		router.attach(OntologyResource.resource,createRDFPlayground());
		/**
		 * Images, styles, favicons, applets
		 */
		attachStaticResources(router);

		router.attach("/chelp", HelpResource.class);
		router.attach("/chelp/{key}", HelpResource.class);

	     router.setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
	     router.setRoutingMode(Router.MODE_BEST_MATCH); 
	     
	    /*
	     StringWriter w = new StringWriter();
	     AmbitApplication.printRoutes(router,">",w);
	     System.out.println(w.toString());
	    */
	     
		 try {	
			 //TODO use config file
			 final TrustManager tm ;
			
				tm = new X509TrustManager() {
				    public void checkClientTrusted(X509Certificate[] chain,
				                    String authType)
				                    throws CertificateException {
				    }

				    public X509Certificate[] getAcceptedIssuers() {
				        return null;
				    }

				    public void checkServerTrusted(X509Certificate[] chain,
				                    String authType)
				                    throws CertificateException {
				        // This will never throw an exception.
				        // This doesn't check anything at all: it's insecure.
				    }
				};

				final SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[] { tm },  new java.security.SecureRandom());

				SslContextFactory factory = new SslContextFactory() {
				    public void init(Series<Parameter> parameters) { }
				    public SSLContext createSslContext() { return sslContext; }
				};
				getContext().getAttributes().put("sslContextFactory",factory);
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());


				HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { 
				    public boolean verify(String urlHostName, SSLSession session) {return true;} 
				}); 
				
		 } catch (Exception x) {
			 logger.log(Level.WARNING,x.getMessage(),x);
		 }	     
	     if (!isOpenToxAAEnabled()) {

				
	    	 if (isSimpleSecretAAEnabled()) {
		    	 
				 router.attach("/", UIResource.class);
				 router.attach("", UIResource.class);
				 
				 logger.log(Level.INFO,String.format("Property %s set, local AA enabled.", LOCAL_AA_ENABLED));
	    		 ChallengeAuthenticator basicAuth = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "ambit2");
	    		 //get from config file
	    		 ConcurrentMap<String, char[]> localSecrets = null;
	    		 try {
	    			 localSecrets = getLocalSecrets();
	    		 } catch (Exception x) {
	    			 getLogger().log(Level.SEVERE,x.getMessage(),x);
	    			 localSecrets  = new ConcurrentHashMap<String, char[]>(); //empty
	    		 }
	    		 basicAuth.setVerifier(new MapVerifier(localSecrets) {
	    			 @Override
	    			public int verify(Request request, Response response) {
	    				 int result = super.verify(request, response);
	    				 return Method.GET.equals(request.getMethod())?RESULT_VALID:result; 
	    			}
	    		 });
	    		 basicAuth.setEnroler(new Enroler() {
					@Override
					public void enrole(ClientInfo clientInfo) {
						if (clientInfo.isAuthenticated()) 
							clientInfo.getRoles().add(UPDATE_ALLOWED);
					}
				});

	    		 UpdateAuthorizer authorizer = new UpdateAuthorizer();
	    		 authorizer.getAuthorizedRoles().add(UPDATE_ALLOWED);
	    		 authorizer.setNext(router);
	    		 basicAuth.setNext(authorizer);
	    		 return basicAuth;
	    	 }
	    	 else {
	    		 getLogger().warning("Warning: No AA protection! All resources are open for GET, POST, PUT and DELETE!");
	    	 }
	     
	     
		} else {} 		//OK, AA is already there
	     
	     // attach login
	     
	 		Restlet login = createOpenSSOLoginRouter();
			router.attach("/", login);
			router.attach("", login);
			/**
			 * OpenSSO login / logout
			 * Sets a cookie with OpenSSO token
			 */
			router.attach("/"+OpenSSOUserResource.resource,login );

		 return router;
	}
	
	protected Restlet createOpenSSOLoginRouter() {
		Filter userAuthn = new OpenSSOAuthenticator(getContext(),true,"opentox.org",new OpenSSOVerifierSetUser(false));
		userAuthn.setNext(OpenSSOUserResource.class);
		return userAuthn;
	}

	protected Restlet createProtectedResource(Restlet router) {
		return createProtectedResource(router,null);
	}
	protected Restlet createProtectedResource(Restlet router,String prefix) {
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
		similarity.attach(SimilarityMatrixResource.resource,SimilarityMatrixResource.class);
		similarity.attach(ChemicalSpaceResource.resource,ChemicalSpaceResource.class);
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
		queryRouter.attach(StrucTypeQueryResource.resource,StrucTypeQueryResource.class);
		
		queryRouter.attach(ConsensusLabelQueryResource.resource,ConsensusLabelQueryResource.class);
		
		queryRouter.attach(DatasetStructureQualityStatsResource.resource,DatasetStructureQualityStatsResource.class);
		queryRouter.attach(DatasetChemicalsQualityStatsResource.resource,DatasetChemicalsQualityStatsResource.class);
		queryRouter.attach(DatasetStrucTypeStatsResource.resource,DatasetStrucTypeStatsResource.class);
		
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
			protected Task<TaskResult, String> createTask(String user,ICallableTask callable) {
				
				return new PolicyProtectedTask(user,!(callable instanceof CallablePolicyCreator)) {
					@Override
					public synchronized void setPolicy() throws Exception {

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
		//DBCreateAllowedGuard sameIPguard = new DBCreateAllowedGuard();
		//sameIPguard.setNext(adminRouter);
		return adminRouter;
	}
	/**
	 *  /ontology RDF playground, not used currently
	 * @return
	 */
	protected Restlet createRDFPlayground() {
		//test removed, there is ontology service
		//router.attach(RDFGraphResource.resource,RDFGraphResource.class);
		//router.attach(RDFGraphResource.resource+"/test",OntologyPlayground.class);
		Router router = new MyRouter(getContext());
		router.attachDefault(OntologyResource.class);
		router.attach(OntologyResource.resourceID, OntologyResource.class);
		router.attach(OntologyResource.resourceTree, OntologyResource.class);
		return router;
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
		 Directory metaDir = new Directory(getContext(), "war:///META-INF");
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory jmeDir = new Directory(getContext(), "war:///jme");
		 Directory styleDir = new Directory(getContext(), "war:///style");
		 Directory jquery = new Directory(getContext(), "war:///jquery");
		 Directory scriptsDir = new Directory(getContext(), "war:///scripts");
		 
		 router.attach("/meta/", metaDir);
		 router.attach("/images/", imgDir);
		 router.attach("/jmol/", jmolDir);
		 router.attach("/jme/", jmeDir);
		 router.attach("/jquery/", jquery);
		 router.attach("/style/", styleDir);
 		 router.attach("/scripts/", scriptsDir);

	}


	/**
	 * Standalone, for testing mainly
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        
        Component component = boot(null,null,8080);
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        shutdown(component);

    }
    
    public static Component boot(String mysqluser, String mysqlpwd, int httpport) throws Exception {
    	return boot(mysqluser,mysqlpwd,"ambit2","localhost","3306",httpport);
    }
    public static Component boot(String mysqluser, String mysqlpwd, 
    							 String mysqlDatabase, String mysqlHost, String mysqlport,
    							 int httpport) throws Exception {
    	

        Context context = new Context();
        if (mysqlDatabase!= null) context.getParameters().add(Preferences.DATABASE, mysqlDatabase);
        if (mysqluser!=null) context.getParameters().add(Preferences.USER, mysqluser);
        if (mysqlpwd!=null) context.getParameters().add(Preferences.PASSWORD, mysqlpwd);
        if (mysqlport!=null) context.getParameters().add(Preferences.PORT, mysqlport);
        if (mysqlHost!=null) context.getParameters().add(Preferences.HOST, mysqlHost);
            	
        Component component = new AmbitComponent(context,true);
        final Server server = component.getServers().add(Protocol.HTTP, httpport);
        Logger logger = Logger.getLogger(AmbitApplication.class.getName());	
        logger.log(Level.INFO,String.format("Server started on port %d",server.getPort()));

        component.start();
        return component;
    }
    
    public static void shutdown(Component component) throws Exception {
        component.stop();
        Logger logger = Logger.getLogger(AmbitApplication.class.getName());	
        logger.log(Level.INFO,"Server stopped");
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
   protected synchronized boolean isSimpleSecretAAEnabled()  {
		try {
			String aaadmin = getProperty(LOCAL_AA_ENABLED,configProperties);
			return aaadmin==null?null:Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {return false; }
	}
   
   protected synchronized boolean isWarmupEnabled()  {
		try {
			String warmup = getProperty(WARMUP_ENABLED,configProperties);
			return warmup==null?null:Boolean.parseBoolean(warmup);
		} catch (Exception x) {return false; }
	}	
   
	protected synchronized SimpleGuards isSimpleGuardEnabled()  {
		try {
			String guard = getProperty(GUARD_ENABLED,configProperties);
			return guard==null?null:SimpleGuards.valueOf(guard);
		} catch (Exception x) {return null; }
	}	
	
	protected String[] getGuardListAllowed()  {
		try {
			String list = getProperty(GUARD_LIST,configProperties);
			return list.split(" ");
		} catch (Exception x) {return null; }
	}	

	public synchronized String readVersionShort()  {
		try {
			return getProperty(version,ambitProperties);
		} catch (Exception x) {return "Unknown"; }
	}

	public synchronized String readVersionLong()  {
		try {
			String v1 = getProperty(version,ambitProperties);
			String v2 = getProperty(version_build,ambitProperties);
			String v3 = getProperty(version_timestamp,ambitProperties);
			return String.format("%s r%s built %s",v1,v2,new Date(Long.parseLong(v3)));
		} catch (Exception x) {return "Unknown"; }
	}
	protected synchronized boolean isOpenToxAAEnabled()  {
		try {
			String aaadmin = getProperty(OPENTOX_AA_ENABLED,configProperties);
			return aaadmin==null?null:Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {return false; }
	}	
	
	protected synchronized boolean protectAdminResource()  {
		try {
			String aaadmin = getProperty(adminAAEnabled,ambitProperties);
			return aaadmin==null?null:Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {return false; }
	}	

	protected synchronized boolean protectCompoundResource()  {
		try {
			String aacompound = getProperty(compoundAAEnabled,ambitProperties);
			return aacompound==null?null:Boolean.parseBoolean(aacompound);
		} catch (Exception x) {return false; }
	}
	
	protected synchronized boolean protectFeatureResource()  {
		try {
			String aafeature = getProperty(featureAAEnabled,ambitProperties);
			return aafeature==null?null:Boolean.parseBoolean(aafeature);
		} catch (Exception x) {return false; }
	}	
	protected synchronized String getProperty(String name,String config)  {
		try {
			Properties p = properties.get(config);
			if (p==null) {
				p = new Properties();
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(config);
				p.load(in);
				in.close();
				properties.put(config,p);
			}
			return p.getProperty(name);

		} catch (Exception x) {
			return null;
		}
	}	
	
	 protected ConcurrentMap<String, char[]>  getLocalSecrets() throws Exception {

		
		 String identifier = getProperty(identifierKey,configProperties);
		 String pass = getProperty(identifierPass,configProperties);
		 if ((identifier==null)||"".equals(identifier)||identifier.indexOf("${")>-1) 
			 			throw new Exception(String.format("Property %s not set. The web applicaiton will be READ ONLY!",identifierKey));
		 if ((pass==null)||"".equals(pass)||pass.indexOf("${")>-1) 
	 			throw new Exception(String.format("Property %s not set. The web applicaiton will be READ ONLY!",identifierKey));
		 ConcurrentMap<String, char[]> localSecrets = new ConcurrentHashMap<String, char[]>(); 
		 localSecrets.put(identifier,pass.toCharArray());
		 return localSecrets;
	 }
	
}

/**
 * Modified to allow GET always
 * @author nina
 *
 */
class UpdateAuthorizer extends RoleAuthorizer {

	public UpdateAuthorizer() {
		super();
	}

	@Override
	public boolean authorize(Request request, Response response) {
		if (Method.GET.equals(request.getMethod())) return true;
		else return super.authorize(request, response);
	}
	
}
