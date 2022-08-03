package ambit2.rest;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.engine.security.SslContextFactory;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.util.Series;

import ambit2.base.config.AMBITConfig;
import ambit2.base.config.Preferences;
import ambit2.rest.aa.basic.UIBasicResource;
import ambit2.rest.aa.basic.UINoAAResource;
import ambit2.rest.aa.oidc.ChallengeAuthenticatorBearer;
import ambit2.rest.aa.opensso.BookmarksAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOAuthenticator;
import ambit2.rest.aa.opensso.OpenSSOAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOMethodAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOVerifierSetUser;
import ambit2.rest.aa.opensso.users.OpenSSOUserResource;
import ambit2.rest.admin.AdminResource;
import ambit2.rest.admin.PolicyResource;
import ambit2.rest.admin.SimpleGuard;
import ambit2.rest.admin.SimpleGuard.SimpleGuards;
import ambit2.rest.algorithm.MLResources;
import ambit2.rest.algorithm.chart.ChartResource;
import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.bookmark.OntoBucketResource;
import ambit2.rest.bundle.MyBundlesResource;
import ambit2.rest.bundle.user.DummyUserByURIResource;
import ambit2.rest.bundle.user.UserByURIResource;
import ambit2.rest.config.AMBITAppConfigInternal;
import ambit2.rest.config.AMBITAppConfigProperties;
import ambit2.rest.dataset.CollectionStructureResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.dataset.MissingFeatureValuesResource;
import ambit2.rest.dataset.filtered.DataAvailabilityResource;
import ambit2.rest.dataset.filtered.ExperimentsSearchResource;
import ambit2.rest.dataset.filtered.InterpretationResultSearchResource;
import ambit2.rest.dataset.filtered.StudySearchResource;
import ambit2.rest.dataset.filtered.SubstanceTypeSearchResource;
import ambit2.rest.facet.CompoundsByPropertyValueInDatasetResource;
import ambit2.rest.facet.DatasetChemicalsQualityStatsResource;
import ambit2.rest.facet.DatasetStrucTypeStatsResource;
import ambit2.rest.facet.DatasetStructureQualityStatsResource;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.facet.DatasetsByNamePrefixResource;
import ambit2.rest.help.HelpResource;
import ambit2.rest.loom.LoomResource;
import ambit2.rest.loom.LoomRouter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.pubchem.CSLSResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.ConsensusLabelQueryResource;
import ambit2.rest.query.ExactStructureQueryResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.query.StrucTypeQueryResource;
import ambit2.rest.report.ReportDatasetResource;
import ambit2.rest.routers.APIDocsRouter;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.routers.TaskRouter;
import ambit2.rest.routers.misc.AdminRouter;
import ambit2.rest.routers.misc.BookmarksRouter;
import ambit2.rest.routers.misc.ChartRouter;
import ambit2.rest.routers.misc.DepictDemoRouter;
import ambit2.rest.routers.misc.InvestigationRouter;
import ambit2.rest.routers.misc.ProxyRouter;
import ambit2.rest.routers.misc.UIRouter;
import ambit2.rest.routers.opentox.AlgorithmRouter;
import ambit2.rest.routers.opentox.BundleRouter;
import ambit2.rest.routers.opentox.CollectionsRouter;
import ambit2.rest.routers.opentox.CompoundInDatasetRouter;
import ambit2.rest.routers.opentox.CompoundsRouter;
import ambit2.rest.routers.opentox.DatasetsRouter;
import ambit2.rest.routers.opentox.FeaturesRouter;
import ambit2.rest.routers.opentox.ModelRouter;
import ambit2.rest.routers.opentox.SubstanceOwnerRouter;
import ambit2.rest.routers.opentox.SubstanceRouter;
import ambit2.rest.similarity.SimilarityMatrixResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.similarity.space.ChemicalSpaceResource;
import ambit2.rest.similarity.space.QMapDatasetResource;
import ambit2.rest.similarity.space.QMapResource;
import ambit2.rest.similarity.space.QMapSpaceResource;
import ambit2.rest.structure.CompoundLookup;
import ambit2.rest.structure.diagram.DepictionResource;
import ambit2.rest.structure.tautomers.QueryStructureRelationResource;
import ambit2.rest.structure.tautomers.QueryTautomersResource;
import ambit2.rest.substance.NMParserResource;
import ambit2.rest.substance.SubstanceLookup;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.owner.OwnerSubstanceFacetResource;
import ambit2.rest.substance.property.SubstanceCategoryProperty;
import ambit2.rest.substance.property.SubstancePropertyResource;
import ambit2.rest.substance.study.SubstanceStudyTableResource;
import ambit2.rest.substance.templates.AssayTemplateResource;
import ambit2.rest.substance.templates.AssayTemplatesFacetResource;
import ambit2.rest.task.TaskResource;
import ambit2.rest.task.WarmupTask;
import ambit2.rest.ui.API2docsResource;
import ambit2.rest.ui.UIResource;
import ambit2.rest.wrapper.WrappedService;
import ambit2.user.aa.AMBITLoginFormResource;
import ambit2.user.aa.AMBITLoginPOSTResource;
import ambit2.user.aa.AMBITLogoutPOSTResource;
import ambit2.user.groups.OrganisationRouter;
import ambit2.user.groups.ProjectRouter;
import ambit2.user.rest.UserRouter;
import ambit2.user.rest.resource.AMBITRegistrationNotifyResource;
import ambit2.user.rest.resource.MyAccountAppsResource;
import ambit2.user.rest.resource.MyAccountPwdResetResource;
import ambit2.user.rest.resource.MyAccountResource;
import ambit2.user.rest.resource.PwdForgottenConfirmResource;
import ambit2.user.rest.resource.PwdForgottenFailedResource;
import ambit2.user.rest.resource.PwdForgottenNotifyResource;
import ambit2.user.rest.resource.PwdForgottenResource;
import ambit2.user.rest.resource.RegistrationConfirmResource;
import ambit2.user.rest.resource.RegistrationResource;
import ambit2.user.rest.resource.Resources;
import net.idea.restnet.aa.local.UserLoginPOSTResource;
import net.idea.restnet.aa.local.UserLogoutPOSTResource;
import net.idea.restnet.db.aalocal.ChallengeAuthenticatorTokenLocal;

/**
 * AMBIT implementation of OpenTox REST services as described in
 * http://opentox.org/development/wiki/ http://opentox.org/wiki/1/Dataset
 * 
 * @author nina
 */

public class AmbitApplication extends AmbitFreeMarkerApplication<Object> {

	public AmbitApplication() {
		this(false);
	}

	public AmbitApplication(boolean standalone) {
		super(standalone);
		// getContext().getAttributes().put(ConfigProperties.class.getName(),properties_overridable);
	}

	@Override
	public Restlet createInboundRoot() {
		Restlet root = initInboundRoot();

		SimpleGuards guard = isSimpleGuardEnabled();

		if (!standalone && getProperties_internal().isWarmupEnabled()) // only
																		// servlets
																		// are
																		// lazy
			addTask("warmup", new WarmupTask("warmup", getProperties_internal().isOpenToxAAEnabled()),
					new Reference("riap://component"), "guest");

		if (guard != null) {
			logger.log(Level.INFO,
					String.format("Property %s set, %s guard enabled.", AMBITAppConfigProperties.GUARD_ENABLED, guard));
			String[] allowed = getProperties_internal().getGuardListAllowed();
			StringBuilder b = new StringBuilder();
			for (String ip : allowed) {
				b.append(ip);
				b.append(" ");
			}
			logger.log(Level.INFO, String.format("Property %s set, %s list allowed %s",
					AMBITAppConfigProperties.GUARD_LIST, guard, b.toString()));
			SimpleGuard theguard = guard.getGuard(allowed, logger);
			theguard.setNext(root);
			return theguard;
		} else
			return root;

	}

	public Restlet initInboundRoot() {
		initFreeMarkerConfiguration();

		String usersdbname = getProperties_overridable().getDBNameUsers();
		if (usersdbname == null)
			usersdbname = "ambit_users";
		getContext().getParameters().add(AMBITConfig.users_dbname.name(), usersdbname);

		Router router = new MyRouter(this.getContext()) {
			public void handle(Request request, Response response) {
				// to use within riap calls
				String rootUrl = getContext().getParameters().getFirstValue(BASE_URL);
				if ((rootUrl == null) && request.getRootRef().toString().startsWith("http")) {
					rootUrl = request.getRootRef().toString();
					getContext().getParameters().set(BASE_URL, rootUrl, true);
				}
				super.handle(request, response);
			};
		};

		router.attach("/api-docs", new APIDocsRouter(getContext()));
		router.attach("/openapi3", API2docsResource.class);

		/**
		 * Points to the Ontology service /sparqlendpoint
		 */
		// router.attach(SPARQLPointerResource.resource,
		// SPARQLPointerResource.class);

		/**
		 * * /admin Various admin tasks, like database creation
		 */

		Restlet adminRouter = createAdminRouter();
		if (getProperties_internal().isOpenToxAAEnabled()) {
			router.attach(String.format("/%s", AdminResource.resource),
					getProperties_internal().protectAdminResource() ? createProtectedResource(adminRouter, "admin")
							: adminRouter);
		} else
			router.attach(String.format("/%s", AdminResource.resource), adminRouter);

		/** /policy - used for testing only */
		router.attach(String.format("/%s", PolicyResource.resource), PolicyResource.class);

		/** /feature */
		FeaturesRouter featuresRouter = new FeaturesRouter(getContext());

		if (getProperties_internal().isOpenToxAAEnabled()) {
			if (getProperties_internal().protectFeatureResource())
				router.attach(PropertyResource.featuredef, createProtectedResource(featuresRouter, "feature"));
			else {
				Filter cauthN = new OpenSSOAuthenticator(getContext(), false, "opentox.org",
						new OpenSSOVerifierSetUser(false));
				cauthN.setNext(featuresRouter);
				router.attach(PropertyResource.featuredef, cauthN);
			}
		} else
			router.attach(PropertyResource.featuredef, featuresRouter);

		/** filter */
		// router.attach(FilteredDatasetResource.resource,
		// FilteredDatasetResource.class);

		Router similarityRouter = createSimilaritySearchRouter();

		Router smartsRouter = createSMARTSSearchRouter();
		/** /compound */
		CompoundsRouter compoundRouter = new CompoundsRouter(getContext(), featuresRouter, smartsRouter);
		if (getProperties_internal().isOpenToxAAEnabled()) {
			if (getProperties_internal().protectCompoundResource())
				router.attach(DataResources.compound_resource, createProtectedResource(compoundRouter, "compound"));
			else {
				Filter cauthN = new OpenSSOAuthenticator(getContext(), false, "opentox.org",
						new OpenSSOVerifierSetUser(false));
				cauthN.setNext(compoundRouter);
				router.attach(DataResources.compound_resource, cauthN);
			}
		} else
			router.attach(DataResources.compound_resource, compoundRouter);
		/**
		 * List of datasets /dataset , /datasets
		 */
		Router allDatasetsRouter = new MyRouter(getContext());
		allDatasetsRouter.attachDefault(DatasetsResource.class);

		if (getProperties_internal().isOpenToxAAEnabled())
			router.attach(DatasetsResource.datasets, createProtectedResource(allDatasetsRouter, "datasets"));
		else
			router.attach(DatasetsResource.datasets, allDatasetsRouter);

		// can reuse CompoundRouter from above
		CompoundInDatasetRouter cmpdRouter = new CompoundInDatasetRouter(getContext(), featuresRouter, smartsRouter);
		Router datasetRouter = new DatasetsRouter(getContext(), cmpdRouter, smartsRouter, similarityRouter);

		if (getProperties_internal().isOpenToxAAEnabled())
			router.attach(DatasetResource.dataset, createProtectedResource(datasetRouter, "dataset"));
		else
			router.attach(DatasetResource.dataset, datasetRouter);

		if (getProperties_internal().attachSubstanceRouter()) {
			Map<String, WrappedService<UsernamePasswordCredentials>> solrServices = getProperties().getSolrServices();

			/**
			 * /property/{id}
			 */
			if (getProperties_internal().isOpenToxAAEnabled()) {

				router.attach(Resources.bundle,
						createAuthenticatedOpenMethodResource(new BundleRouter(getContext(), null)));

				router.attach(Resources.collection_bundledrafts,
						createProtectedResource(new CollectionsRouter(getContext(), null)));

				router.attach(SubstanceResource.substance,
						createAuthenticatedOpenMethodResource(new SubstanceRouter(getContext())));

				if (getProperties_internal().attachSubstanceOwnerRouter())
					router.attach(OwnerSubstanceFacetResource.owner,
							createAuthenticatedOpenMethodResource(new SubstanceOwnerRouter(getContext())));

				if (getProperties_internal().attachInvestigationRouter()) {
					router.attach(SubstanceStudyTableResource.investigation,
							createAuthenticatedOpenMethodResource(new InvestigationRouter(getContext())));
				}

				router.attach(Resources.proxy,
						createAuthenticatedOpenMethodResource(new ProxyRouter(getContext()), Method.GET, Method.POST));

			} else {
				Filter authz = null;
				if (getProperties_internal().isDBAAEnabled())
					try {
						String dbname = getProperties().getDBNameAmbit();
						authz = UserRouter.createBundlePolicyAuthorizer(getContext(), dbname, usersdbname,
								AMBITAppConfigProperties.configProperties, getProperties().getBaseURLDepth());
					} catch (Exception x) {

					}
				router.attach(Resources.bundle, new BundleRouter(getContext(), authz));
				router.attach(Resources.collection_bundledrafts, new CollectionsRouter(getContext(), null));
				router.attach(SubstanceResource.substance, new SubstanceRouter(getContext()));
				if (getProperties_internal().attachSubstanceOwnerRouter())
					router.attach(OwnerSubstanceFacetResource.owner, new SubstanceOwnerRouter(getContext()));

				if (getProperties_internal().attachInvestigationRouter()) {
					router.attach(SubstanceStudyTableResource.investigation, new InvestigationRouter(getContext()));
				}

				if (solrServices != null)
					router.attach(Resources.proxy, new ProxyRouter(getContext()));
			}

			router.attach(SubstancePropertyResource.substanceproperty, SubstancePropertyResource.class);
			router.attach(
					String.format("%s/{%s}/{%s}/{%s}/{%s}", SubstancePropertyResource.substanceproperty,
							SubstancePropertyResource.topcategory, SubstancePropertyResource.endpointcategory,
							SubstancePropertyResource.endpoint, SubstancePropertyResource.substancepropertyid),
					SubstancePropertyResource.class);
			router.attach(
					String.format("%s/{%s}/{%s}", SubstancePropertyResource.substanceproperty,
							SubstancePropertyResource.topcategory, SubstancePropertyResource.endpointcategory),
					SubstanceCategoryProperty.class);

		}

		if (getProperties_internal().attachToxmatchRouter()) {
			router.attach(QMapSpaceResource.resource, QMapSpaceResource.class);
			router.attach(QMapResource.qmap, QMapResource.class);
			router.attach(String.format("%s/{%s}/metadata", QMapResource.qmap, QMapResource.qmapKey),
					QMapResource.class);
			router.attach(String.format("%s/{%s}", QMapResource.qmap, QMapResource.qmapKey), QMapDatasetResource.class);
		}

		// collections
		MyRouter collectionRouter = new MyRouter(getContext());
		collectionRouter.attach(String.format("/{%s}/{%s}", CollectionStructureResource.folderKey,
				CollectionStructureResource.datasetKey), CollectionStructureResource.class);
		if (getProperties_internal().isOpenToxAAEnabled()) {
			router.attach(CollectionStructureResource.collection,
					createProtectedResource(collectionRouter, "collection"));
		} else
			router.attach(CollectionStructureResource.collection, collectionRouter);

		if (getProperties_internal().isOpenToxAAEnabled()) {
			/** /algorithm */
			router.attach(MLResources.algorithm, createAuthenticatedOpenResource(new AlgorithmRouter(getContext())));
			/** /model */
			router.attach(MLResources.model_resource, createAuthenticatedOpenResource(new ModelRouter(getContext())));
			/** /task */
			router.attach(TaskResource.resource, new TaskRouter(getContext()));
			router.attach("/ui", createAuthenticatedOpenResource(new UIRouter(getContext())));
		} else {
			/** /algorithm */
			router.attach(MLResources.algorithm, new AlgorithmRouter(getContext()));
			/** /model */
			router.attach(MLResources.model_resource, new ModelRouter(getContext()));
			/** /task */
			router.attach(TaskResource.resource, new TaskRouter(getContext()));
			router.attach("/ui", new UIRouter(getContext()));

		}

		/**
		 * Queries /query
		 * 
		 */
		Router queryRouter = createQueryRouter();
		if (getProperties_internal().isOpenToxAAEnabled()) {
			router.attach(QueryResource.query_resource, createAuthenticatedOpenResource(queryRouter));
		} else
			router.attach(QueryResource.query_resource, queryRouter);

		queryRouter.attach(SmartsQueryResource.resource, smartsRouter);
		queryRouter.attach(SimilarityResource.resource, similarityRouter);
		queryRouter.attach(ExactStructureQueryResource.resource, ExactStructureQueryResource.class);

		/**
		 * /query/relation/dataset/has_tautomer?dataset_uri=
		 * /query/relation/compound/has_tautomer?dataset_uri=
		 */
		queryRouter.attach(QueryStructureRelationResource.resource, createRelationsRouter());

		/**
		 * API extensions from this point on
		 */

		/**
		 * Dataset reporting /report Practically same as /dataset , but allows POST , so
		 * that long URIs with features could be send
		 */
		router.attach(ReportDatasetResource.resource, ReportDatasetResource.class);
		/** /bookmark */
		router.attach(BookmarkResource.resource, createBookmarksRouter());
		/** /ontobucket */
		router.attach("/ontobucket", OntoBucketResource.class);

		/** /chart */
		router.attach(ChartResource.resource, new ChartRouter(getContext()));

		/**
		 * Demos
		 */
		if (getProperties_internal().attachDepictRouter()) {
			Router depict = new DepictDemoRouter(getContext());
			router.attach("/demo", depict);

			router.attach(DepictionResource.resource, DepictionResource.class);
			router.attach(String.format("%s/{%s}", DepictionResource.resource, DepictionResource.resourceKey),
					DepictionResource.class);
		}
		// router.attach("/datatemplate", InputTemplatesResource.class);
		// router.attach("/datatemplate/{idtemplate}",
		// InputTemplatesResource.class);
		router.attach(AssayTemplatesFacetResource.assaytemplate_key, AssayTemplateResource.class);
		router.attach(AssayTemplatesFacetResource.assaytemplate_filter, AssayTemplateResource.class);
		router.attach(AssayTemplatesFacetResource.assaytemplate_facet, AssayTemplatesFacetResource.class);

		router.attach(NMParserResource.resource, NMParserResource.class);
		/**
		 * Images, styles, favicons, applets
		 */
		attachStaticResources(router);

		router.attach("/chelp", HelpResource.class);
		router.attach("/chelp/{key}", HelpResource.class);

		// router.attach("/info", InfoResource.class);

		router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
		router.setRoutingMode(Router.MODE_BEST_MATCH);

		router.attach(LoomResource.resource, new LoomRouter(getContext()));

		try {
			// TODO use config file
			final TrustManager tm;

			tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// This will never throw an exception.
					// This doesn't check anything at all: it's insecure.
				}
			};

			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());

			SslContextFactory factory = new SslContextFactory() {
				public void init(Series<Parameter> parameters) {
				}

				public SSLContext createSslContext() {
					return sslContext;
				}
			};
			getContext().getAttributes().put("sslContextFactory", factory);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			});

		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
		}

		if (!getProperties_internal().isOpenToxAAEnabled()) {

			if (getProperties_internal().isDBAAEnabled()) {

				router.attach("/", AMBITLoginFormResource.class);
				router.attach("", AMBITLoginFormResource.class);

				logger.log(Level.INFO,
						String.format("Property %s set, DB AA enabled.", AMBITAppConfigInternal.DB_AA_ENABLED));

				/*
				 * /login
				 */
				router.attach(ambit2.user.rest.resource.Resources.login, AMBITLoginFormResource.class);
				/*
				 * /myaccount /myaccount/reset /user/{id}
				 */

				OrganisationRouter org_router = new OrganisationRouter(getContext());
				ProjectRouter projectRouter = new ProjectRouter(getContext());
				router.attach(Resources.project, projectRouter);
				router.attach(Resources.organisation, org_router);

				MyRouter myAccountRouter = new MyRouter(getContext());
				myAccountRouter.attachDefault(MyAccountResource.class);
				myAccountRouter.attach(ambit2.user.rest.resource.Resources.reset, MyAccountPwdResetResource.class);
				myAccountRouter.attach(Resources.apps, MyAccountAppsResource.class);
				myAccountRouter.attach(Resources.bundle, MyBundlesResource.class);

				myAccountRouter.attach("/users", UserByURIResource.class);

				router.attach(ambit2.user.rest.resource.Resources.myaccount, myAccountRouter);

				router.attach(ambit2.user.rest.resource.Resources.user,
						new UserRouter(getContext(), org_router, projectRouter));
				router.attach(ambit2.user.rest.resource.Resources.register, RegistrationResource.class);
				router.attach(String.format("%s%s", ambit2.user.rest.resource.Resources.register,
						ambit2.user.rest.resource.Resources.confirm), RegistrationConfirmResource.class);
				router.attach(String.format("%s%s", ambit2.user.rest.resource.Resources.register,
						ambit2.user.rest.resource.Resources.notify), AMBITRegistrationNotifyResource.class);

				/*
				 * /forgotten /forgotten/confirm /forgotten/notify /forgotten/failed
				 */
				router.attach(ambit2.user.rest.resource.Resources.forgotten, PwdForgottenResource.class);
				router.attach(String.format("%s%s", ambit2.user.rest.resource.Resources.forgotten,
						ambit2.user.rest.resource.Resources.confirm), PwdForgottenConfirmResource.class);
				router.attach(String.format("%s%s", ambit2.user.rest.resource.Resources.forgotten,
						ambit2.user.rest.resource.Resources.notify), PwdForgottenNotifyResource.class);
				router.attach(String.format("%s%s", ambit2.user.rest.resource.Resources.forgotten,
						ambit2.user.rest.resource.Resources.failed), PwdForgottenFailedResource.class);

				Router protectedRouter = new MyRouter(getContext());
				// protectedRouter.attach("/roles",
				// AMBITLoginFormResource.class);
				protectedRouter.attach(String.format("/%s", UserLoginPOSTResource.resource),
						AMBITLoginPOSTResource.class);
				protectedRouter.attach(String.format("/%s", UserLogoutPOSTResource.resource),
						AMBITLogoutPOSTResource.class);
				// protectedRouter.attach(NotificationResource.resourceKey,
				// NotificationResource.class);

				router.attach("/provider", protectedRouter);

				// optional challenge authenticator


				Filter dbAuth = UserRouter.createCookieAuthenticator(getContext(), getProperties(),
						AMBITAppConfigProperties.configProperties, true);

				Filter authz = UserRouter.createPolicyAuthorizer(getContext(), usersdbname,
						AMBITAppConfigProperties.configProperties, getProperties().getBaseURLDepth());
				
				dbAuth.setNext(authz);
				authz.setNext(router);
				// dbAuth.setNext(router);

				ChallengeAuthenticatorBearer bearerAuth = null;
				if (getProperties_internal().isOIDC_AA_Enabled())
					try {
						bearerAuth = new ChallengeAuthenticatorBearer(getContext(), true,
								getProperties().getOIDCrealm());
					} catch (Exception err) {
						err.printStackTrace();
						bearerAuth = null;
					}
				
				ChallengeAuthenticatorTokenLocal tokenAuth = new ChallengeAuthenticatorTokenLocal(getContext(), true,
							usersdbname, AMBITAppConfigProperties.configProperties);
				
				tokenAuth.setNext(dbAuth);
				if (bearerAuth== null) {
					return addOriginFilter(tokenAuth);
				} else {
					bearerAuth.setNext(tokenAuth);
					return addOriginFilter(bearerAuth);
				}
				
				
				

			} else if (getProperties_internal().isSimpleSecretAAEnabled()) {

				router.attach("/", UIResource.class);
				router.attach("", UIResource.class);

				router.attach(ambit2.user.rest.resource.Resources.login, UIBasicResource.class);
				router.attach(ambit2.user.rest.resource.Resources.myaccount, UIBasicResource.class);
				router.attach("/provider/signout", UIBasicResource.class);

				logger.log(Level.INFO,
						String.format("Property %s set, local AA enabled.", AMBITAppConfigInternal.LOCAL_AA_ENABLED));

				// for testing purposes only!
				router.attach(ambit2.user.rest.resource.Resources.myaccount + "/users", DummyUserByURIResource.class);

				return addOriginFilter(getBasicAuthFilter(router));
			} else {
				router.attach("/", UIResource.class);
				router.attach("", UIResource.class);

				router.attach(ambit2.user.rest.resource.Resources.login, UINoAAResource.class);
				router.attach(ambit2.user.rest.resource.Resources.myaccount, UINoAAResource.class);
				router.attach("/provider/signout", UINoAAResource.class);

				// for testing purposes only!
				router.attach(ambit2.user.rest.resource.Resources.myaccount + "/users", DummyUserByURIResource.class);

				getLogger().warning("Warning: No AA protection! All resources are open for GET, POST, PUT and DELETE!");
			}

		} else

		{

			// attach login

			Restlet login = createOpenSSOLoginRouter();

			router.attach("/", UIResource.class);
			router.attach("", UIResource.class);

			/*
			 * router.attach("/", login); router.attach("", login);
			 */

			/**
			 * OpenSSO login / logout Sets a cookie with OpenSSO token
			 */
			router.attach("/" + OpenSSOUserResource.resource, login);
			router.attach("/login", login);
			router.attach("/myaccount", login);
			router.attach("/provider/signout", login);

		} // OK, AA is already there

		return

		addOriginFilter(router);
	}

	protected Router createSimilaritySearchRouter() {
		Router similarity = new MyRouter(getContext());
		similarity.attachDefault(SimilarityResource.class);
		similarity.attach(SimilarityMatrixResource.resource, SimilarityMatrixResource.class);
		similarity.attach(ChemicalSpaceResource.resource, ChemicalSpaceResource.class);
		return similarity;
	}

	protected Router createSMARTSSearchRouter() {
		Router smartsRouter = new MyRouter(getContext());
		smartsRouter.attachDefault(SmartsQueryResource.class);
		smartsRouter.attach(SmartsQueryResource.resourceID, SmartsQueryResource.class);
		return smartsRouter;
	}

	protected Router createRelationsRouter() {
		Router relationsRouter = new MyRouter(getContext());
		relationsRouter.attachDefault(QueryStructureRelationResource.class);
		relationsRouter.attach(
				String.format("%s%s", OpenTox.URI.dataset.getURI(), QueryStructureRelationResource.resourceID),
				QueryStructureRelationResource.class);
		relationsRouter.attach(
				String.format("%s%s", OpenTox.URI.compound.getURI(), QueryStructureRelationResource.resourceID),
				QueryTautomersResource.class);
		return relationsRouter;
	}

	/**
	 * Everything under /query
	 * 
	 * @return
	 */
	protected Router createQueryRouter() {

		Router queryRouter = new MyRouter(getContext());
		queryRouter.attachDefault(QueryListResource.class);

		/**
		 * Quality labels
		 */
		queryRouter.attach(QLabelQueryResource.resource, QLabelQueryResource.class);
		queryRouter.attach(StrucTypeQueryResource.resource, StrucTypeQueryResource.class);

		queryRouter.attach(ConsensusLabelQueryResource.resource, ConsensusLabelQueryResource.class);

		queryRouter.attach(DatasetStructureQualityStatsResource.resource, DatasetStructureQualityStatsResource.class);
		queryRouter.attach(DatasetChemicalsQualityStatsResource.resource, DatasetChemicalsQualityStatsResource.class);
		queryRouter.attach(DatasetStrucTypeStatsResource.resource, DatasetStrucTypeStatsResource.class);

		queryRouter.attach(StudySearchResource.resource, StudySearchResource.class);

		/**
		 * Missing features /missingValues - there is /filter now, may be this is
		 * redundant ?
		 */
		queryRouter.attach(MissingFeatureValuesResource.resource, MissingFeatureValuesResource.class);

		/**
		 * Facets
		 */
		queryRouter.attach(CompoundsByPropertyValueInDatasetResource.resource,
				CompoundsByPropertyValueInDatasetResource.class);
		queryRouter.attach(DatasetsByEndpoint.resource, DatasetsByEndpoint.class);
		queryRouter.attach(DatasetsByNamePrefixResource.resource, DatasetsByNamePrefixResource.class);

		queryRouter.attach(ExperimentsSearchResource.resource, ExperimentsSearchResource.class);
		queryRouter.attach(InterpretationResultSearchResource.resource, InterpretationResultSearchResource.class);

		/**
		 * PubChem query
		 */
		Router pubchem = new MyRouter(getContext());
		queryRouter.attach(PubchemResource.resource, pubchem);
		pubchem.attachDefault(PubchemResource.class);
		pubchem.attach(PubchemResource.resourceID, PubchemResource.class);

		/**
		 * CIR query
		 */
		Router cir = new MyRouter(getContext());
		queryRouter.attach(CSLSResource.resource, cir);
		cir.attachDefault(CSLSResource.class);
		cir.attach(CSLSResource.resourceID, CSLSResource.class);
		cir.attach(CSLSResource.resourceID + CSLSResource.representationID, CSLSResource.class);

		/**
		 * Compound search /query/compound/lookup
		 */
		Router lookup = new MyRouter(getContext());
		queryRouter.attach(CompoundLookup.resource, lookup);
		lookup.attachDefault(CompoundLookup.class);
		lookup.attach(CompoundLookup.resourceID, CompoundLookup.class);
		lookup.attach(CompoundLookup.resourceID + CompoundLookup.representationID, CompoundLookup.class);

		Router slookup = new MyRouter(getContext());
		queryRouter.attach(OpenTox.URI.substance.getURI(), slookup);
		slookup.attachDefault(SubstanceLookup.class);
		slookup.attach("/{type}", SubstanceLookup.class);
		slookup.attach("/{type}/{subtype}", SubstanceLookup.class);
		slookup.attach("/{type}/{subtype}/{subsubtype}", SubstanceLookup.class);

		queryRouter.attach(SubstanceTypeSearchResource.resource, SubstanceTypeSearchResource.class);
		queryRouter.attach(DataAvailabilityResource.resource, DataAvailabilityResource.class);

		return queryRouter;
	}

	/**
	 * Check for OpenSSO token and set the user, if available but don't verify the
	 * policy
	 * 
	 * @return
	 */
	protected Restlet createAuthenticatedOpenResource(Router router) {
		Filter algAuthn = new OpenSSOAuthenticator(getContext(), false, "opentox.org",
				new OpenSSOVerifierSetUser(false));

		algAuthn.setNext(router);
		return algAuthn;
	}

	protected Restlet createAuthenticatedOpenMethodResource(Router router) {
		return createAuthenticatedOpenMethodResource(router, Method.GET);
	}

	protected Restlet createAuthenticatedOpenMethodResource(Router router, final Method... methods) {
		Filter authN = new OpenSSOAuthenticator(getContext(), false, "opentox.org", new OpenSSOVerifierSetUser(false));
		OpenSSOAuthorizer authZ = new OpenSSOMethodAuthorizer() {
			@Override
			protected boolean authorize(Request request, Response response) {
				for (Method method : methods)
					if (method.equals(request.getMethod()))
						return true;
				return super.authorize(request, response);
			}

		};

		// authZ.setPrefix(prefix);
		authN.setNext(authZ);
		authZ.setNext(router);

		return authN;
	}

	/**
	 * Resource /bookmark
	 * 
	 * @return
	 */
	protected Restlet createBookmarksRouter() {
		BookmarksRouter bookmarkRouter = new BookmarksRouter(getContext());

		Filter bookmarkAuth = new OpenSSOAuthenticator(getContext(), false, "opentox.org");
		Filter bookmarkAuthz = new BookmarksAuthorizer();
		bookmarkAuth.setNext(bookmarkAuthz);
		bookmarkAuthz.setNext(bookmarkRouter);
		return bookmarkAuth;
	}

	/**
	 * Resource /admin
	 * 
	 * @return
	 */
	protected Restlet createAdminRouter() {
		AdminRouter adminRouter = new AdminRouter(getContext());
		// DBCreateAllowedGuard sameIPguard = new DBCreateAllowedGuard();
		// sameIPguard.setNext(adminRouter);
		return adminRouter;
	}

	/**
	 * Standalone, for testing mainly
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Component component = boot(null, null, 8080);
		System.out.println("Press key to stop server");
		System.in.read();
		System.out.println("Stopping server");
		shutdown(component);

	}

	public static Component boot(String mysqluser, String mysqlpwd, int httpport) throws Exception {
		return boot(mysqluser, mysqlpwd, "ambit2", "localhost", "3306", httpport);
	}

	public static Component boot(String mysqluser, String mysqlpwd, String mysqlDatabase, String mysqlHost,
			String mysqlport, int httpport) throws Exception {

		Context context = new Context();
		if (mysqlDatabase != null)
			context.getParameters().add(Preferences.DATABASE, mysqlDatabase);
		if (mysqluser != null)
			context.getParameters().add(Preferences.USER, mysqluser);
		if (mysqlpwd != null)
			context.getParameters().add(Preferences.PASSWORD, mysqlpwd);
		if (mysqlport != null)
			context.getParameters().add(Preferences.PORT, mysqlport);
		if (mysqlHost != null)
			context.getParameters().add(Preferences.HOST, mysqlHost);

		Component component = new AmbitComponent(context, true);
		final Server server = component.getServers().add(Protocol.HTTP, httpport);
		Logger logger = Logger.getLogger(AmbitFreeMarkerApplication.class.getName());
		logger.log(Level.INFO, String.format("Server started on port %d", server.getPort()));

		component.start();
		return component;
	}

	/*
	 * @Override public ApplicationInfo getApplicationInfo(Request request, Response
	 * response) {
	 * 
	 * ApplicationInfo result = super.getApplicationInfo(request, response);
	 * 
	 * DocumentationInfo docInfo = new DocumentationInfo(getName());
	 * docInfo.setTitle(getName()); docInfo.setLanguage(Language.ENGLISH);
	 * docInfo.setTextContent(getDescription());
	 * 
	 * result.setDocumentation(docInfo);
	 * 
	 * return result; }
	 */

}
