package ambit2.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Method;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.MapVerifier;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;

import ambit2.base.config.AMBITConfig;
import ambit2.base.config.Preferences;
import ambit2.rest.aa.UpdateAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOAuthenticator;
import ambit2.rest.aa.opensso.OpenSSOAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOVerifierSetUser;
import ambit2.rest.admin.SimpleGuard.SimpleGuards;
import ambit2.rest.freemarker.FreeMarkerApplication;
import ambit2.rest.freemarker.FreeMarkerStatusService;
import ambit2.rest.task.PolicyProtectedTask;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskStorage;
import ambit2.rest.wrapper.WrappedService;
import ambit2.user.rest.UserRouter;
import ambit2.user.rest.resource.DBRoles;
import net.idea.restnet.aa.opensso.policy.CallablePolicyCreator;
import net.idea.restnet.aa.opensso.users.OpenSSOUserResource;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.filter.RESTnetTunnelFilter;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskResult;

public class AmbitFreeMarkerApplication<O> extends FreeMarkerApplication<O> {
	public static final String BASE_URL = "BASE_URL";
	protected boolean insecure = true;
	protected Logger logger = Logger.getLogger(AmbitFreeMarkerApplication.class.getName());
	protected Hashtable<String, Properties> properties = new Hashtable<String, Properties>();
	public static final String OPENTOX_AA_ENABLED = "aa.enabled";
	public static final String LOCAL_AA_ENABLED = "aa.local.enabled";
	public static final String DB_AA_ENABLED = "aa.db.enabled";
	public static final String GUARD_ENABLED = "guard.enabled";
	public static final String GUARD_LIST = "guard.list";
	public static final String WARMUP_ENABLED = "warmup.enabled";
	public static final String ALLOWED_ORIGINS = "allowed.origins";
	public static final String BASEURL_DEPTH = "baseurl.depth";

	public static final String AJAX_TIMEOUT = "ajax.timeout";
	public static final String SIMILARITY_ORDER = "similarity.order";

	protected static final String identifierKey = "aa.local.admin.name";
	protected static final String identifierPass = "aa.local.admin.pass";
	protected static final String adminAAEnabled = "aa.admin";
	protected static final String compoundAAEnabled = "aa.compound";
	protected static final String featureAAEnabled = "aa.feature";
	protected static final String modelAAEnabled = "aa.model"; // ignored
	protected static final String version = "ambit.version";
	protected static final String version_build = "ambit.build";
	protected static final String version_timestamp = "ambit.build.timestamp";
	protected static final String ambitProperties = "ambit2/rest/config/ambit2.pref";
	protected static final String configProperties = "ambit2/rest/config/config.prop";
	protected static final String loggingProperties = "ambit2/rest/config/logging.prop";

	protected static final String attachDepict = "attach.depict";
	protected static final String attachSubstance = "attach.substance";
	protected static final String attachInvestigation = "attach.investigation";
	protected static final String attachSubstanceOwner = "attach.substanceowner";
	protected static final String attachToxmatch = "attach.toxmatch";
	protected static final String config_changeLineSeparators = "changeLineSeparators";
	protected static final String googleAnalytics = "google.analytics";

	protected static final String custom_search = "custom.search";
	protected static final String custom_title = "custom.title";
	protected static final String custom_description = "custom.description";
	protected static final String custom_license = "custom.license";
	protected static final String custom_logo = "custom.logo";
	protected static final String custom_query = "custom.query";
	protected static final String custom_structurequery = "custom.structurequery";
	protected static final String solr_service = "solr.service.%d";
	protected static final String solr_url = "solr.url.%d";
	protected static final String solr_basic_user = "solr.basic.user.%d";
	protected static final String solr_basic_password = "solr.basic.password.%d";
	protected static final String solr_filter = "solr.filter";

	protected boolean standalone = false;
	protected boolean openToxAAEnabled = false;
	protected boolean localAAEnabled = false;
	protected boolean dbAAEnabled = false;
	protected boolean warmupEnabled = false;

	protected String ajaxTimeout = "10000"; // msec
	protected boolean enableEmailVerification = true;
	protected int HOMEPAGE_DEPTH = 1;

	public int getHOMEPAGE_DEPTH() {
		return HOMEPAGE_DEPTH;
	}

	public void setHOMEPAGE_DEPTH(int hOMEPAGE_DEPTH) {
		HOMEPAGE_DEPTH = hOMEPAGE_DEPTH;
	}

	public boolean isEnableEmailVerification() {
		return enableEmailVerification;
	}

	public void setEnableEmailVerification(boolean enableEmailVerification) {
		this.enableEmailVerification = enableEmailVerification;
	}

	public String getAjaxTimeout() {
		return ajaxTimeout;
	}

	public void setAjaxTimeout(String ajaxTimeout) {
		this.ajaxTimeout = ajaxTimeout;
	}

	public boolean isSimilarityOrder() {
		return similarityOrder;
	}

	public void setSimilarityOrder(boolean similarityOrder) {
		this.similarityOrder = similarityOrder;
	}

	protected boolean similarityOrder = true;

	public AmbitFreeMarkerApplication(boolean standalone) {
		this.standalone = standalone;
		openToxAAEnabled = isOpenToxAAEnabled();
		localAAEnabled = isSimpleSecretAAEnabled();
		dbAAEnabled = isDBAAEnabled();
		warmupEnabled = isWarmupEnabled();
		changeLineSeparators = getConfigChangeLineSeparator();
		versionShort = readVersionShort();
		versionLong = readVersionLong();
		gaCode = readGACode();
		HOMEPAGE_DEPTH = getBaseURLDepth();

		setSimilarityOrder(getSimilarityOrderOption());
		ajaxTimeout = getAjaxTimeoutOption();
		setEnableEmailVerification(getEnableEmailVerificationOption());

		setProfile(getMenuProfile());

		setName(getPropertyWithDefault(custom_title, ambitProperties, "AMBIT"));
		setDescription(getPropertyWithDefault(custom_title, ambitProperties,
				"Chemical structures database, properties prediction & machine learning with OpenTox REST web services API"));
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");

		InputStream in = null;
		try {
			URL url = getClass().getClassLoader().getResource(loggingProperties);
			System.setProperty("java.util.logging.config.file", url.getFile());
			in = new FileInputStream(new File(url.getFile()));
			LogManager.getLogManager().readConfiguration(in);
			logger.log(Level.INFO, String.format("Logging configuration loaded from %s", url.getFile()));
		} catch (Exception x) {
			System.err.println("logging configuration failed " + x.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception x) {
			}
		}
		setStatusService(new FreeMarkerStatusService(this, getStatusReportLevel()));
		setTunnelService(new TunnelService(true, true) {
			@Override
			public Filter createInboundFilter(Context context) {
				return new RESTnetTunnelFilter(context);
			}
		});
		getTunnelService().setUserAgentTunnel(true);
		getTunnelService().setExtensionsTunnel(false);

		Preferences.setProperty(Preferences.MAXRECORDS, "0");

		getMetadataService().setEnabled(true);
		getMetadataService().addExtension("sdf", ChemicalMediaType.CHEMICAL_MDLSDF, true);
		getMetadataService().addExtension("mol", ChemicalMediaType.CHEMICAL_MDLMOL, true);
		getMetadataService().addExtension("inchi", ChemicalMediaType.CHEMICAL_INCHI, true);
		getMetadataService().addExtension("cml", ChemicalMediaType.CHEMICAL_CML, true);
		getMetadataService().addExtension("smiles", ChemicalMediaType.CHEMICAL_SMILES, true);
	}

	protected synchronized boolean isDBAAEnabled() {
		try {
			String aaadmin = getProperty(DB_AA_ENABLED, configProperties);
			return aaadmin == null ? null : Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {
			return false;
		}
	}

	protected synchronized int getBaseURLDepth() {
		try {
			String val = getProperty(BASEURL_DEPTH, ambitProperties);
			return val == null ? 1 : Integer.parseInt(val);
		} catch (Exception x) {
			return 1;
		}
	}

	protected synchronized boolean isSimpleSecretAAEnabled() {
		try {
			String aaadmin = getProperty(LOCAL_AA_ENABLED, configProperties);
			return aaadmin == null ? null : Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {
			return false;
		}
	}

	protected synchronized boolean isWarmupEnabled() {
		try {
			String warmup = getProperty(WARMUP_ENABLED, configProperties);
			return warmup == null ? null : Boolean.parseBoolean(warmup);
		} catch (Exception x) {
			return false;
		}
	}

	protected synchronized boolean getSimilarityOrderOption() {
		try {
			String order = getProperty(SIMILARITY_ORDER, ambitProperties);
			return order == null ? true : Boolean.parseBoolean(order);
		} catch (Exception x) {
			return true;
		}
	}

	protected synchronized boolean getEnableEmailVerificationOption() {
		try {
			String order = getProperty(AMBITConfig.enableEmailVerification.name(), configProperties);
			return order == null ? true : Boolean.parseBoolean(order);
		} catch (Exception x) {
			return true;
		}
	}

	protected synchronized String getAjaxTimeoutOption() {
		try {
			String order = getProperty(AJAX_TIMEOUT, ambitProperties);
			return order == null ? "10000" : order.trim();
		} catch (Exception x) {
			return "10000";
		}
	}

	protected synchronized SimpleGuards isSimpleGuardEnabled() {
		try {
			String guard = getProperty(GUARD_ENABLED, configProperties);
			return guard == null ? null : SimpleGuards.valueOf(guard);
		} catch (Exception x) {
			return null;
		}
	}

	protected String[] getGuardListAllowed() {
		try {
			String list = getProperty(GUARD_LIST, configProperties);
			return list.split(" ");
		} catch (Exception x) {
			return null;
		}
	}

	public synchronized String readVersionShort() {
		try {
			return getProperty(version, ambitProperties);
		} catch (Exception x) {
			return "Unknown";
		}
	}

	public synchronized String readVersionLong() {
		try {
			String v1 = getProperty(version, ambitProperties);
			String v2 = getProperty(version_build, ambitProperties);
			String v3 = getProperty(version_timestamp, ambitProperties);
			return String.format("%s r%s built %s", v1, v2, new Date(Long.parseLong(v3)));
		} catch (Exception x) {
			return "Unknown";
		}
	}

	public synchronized String readGACode() {
		try {
			String ga = getProperty(googleAnalytics, ambitProperties);
			if ("".equals(ga))
				return null;
			return ga;
		} catch (Exception x) {
			return null;
		}
	}

	protected synchronized String getAllowedOrigins() {
		try {
			return getProperty(ALLOWED_ORIGINS, configProperties);
		} catch (Exception x) {
			return null;
		}
	}

	protected synchronized boolean isOpenToxAAEnabled() {
		try {
			String aaadmin = getProperty(OPENTOX_AA_ENABLED, configProperties);
			return aaadmin == null ? null : Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {
			return false;
		}
	}

	protected synchronized boolean protectAdminResource() {
		try {
			String aaadmin = getProperty(adminAAEnabled, ambitProperties);
			return aaadmin == null ? null : Boolean.parseBoolean(aaadmin);
		} catch (Exception x) {
			return false;
		}
	}

	public synchronized String getMenuProfile() {
		String prefix = getProperty("ambit.profile", ambitProperties);
		if (prefix == null || "".equals(prefix) || prefix.contains("${"))
			prefix = "default";
		return prefix;
	}

	protected synchronized boolean protectCompoundResource() {
		try {
			String aacompound = getProperty(compoundAAEnabled, ambitProperties);
			return aacompound == null ? null : Boolean.parseBoolean(aacompound);
		} catch (Exception x) {
			return false;
		}
	}

	protected synchronized boolean protectFeatureResource() {
		try {
			String aafeature = getProperty(featureAAEnabled, ambitProperties);
			return aafeature == null ? null : Boolean.parseBoolean(aafeature);
		} catch (Exception x) {
			return false;
		}
	}

	protected synchronized boolean attachDepictRouter() {
		return getBooleanPropertyWithDefault(attachDepict, ambitProperties, true);
	}

	protected synchronized boolean attachSubstanceRouter() {
		return getBooleanPropertyWithDefault(attachSubstance, ambitProperties, true);
	}

	protected synchronized boolean attachInvestigationRouter() {
		return getBooleanPropertyWithDefault(attachInvestigation, ambitProperties, false);
	}

	public synchronized Map<String, WrappedService<UsernamePasswordCredentials>> getSolrServices() {
		try {
			Map<String, WrappedService<UsernamePasswordCredentials>> services = null;

			for (int i = 1; i < 10; i++) {
				String name = getPropertyWithDefault(String.format(solr_service, i), ambitProperties, null);
				if (name == null)
					continue;
				if (services == null)
					services = new Hashtable<String, WrappedService<UsernamePasswordCredentials>>();

				WrappedService<UsernamePasswordCredentials> solr = new WrappedService<>();
				solr.setName(name);
				solr.setURI(new URI(getPropertyWithDefault(String.format(solr_url, i), ambitProperties, null)));
				solr.setCredentials(new UsernamePasswordCredentials(
						getPropertyWithDefault(String.format(solr_basic_user, i), ambitProperties, null),
						getPropertyWithDefault(String.format(solr_basic_password, i), ambitProperties, null)));
				solr.setFilterConfig(getPropertyWithDefault(solr_filter, ambitProperties, null));
				services.put(name, solr);
			}

			return services;
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage());
			return null;
		}
	}

	protected synchronized boolean attachSubstanceOwnerRouter() {
		return getBooleanPropertyWithDefault(attachSubstanceOwner, ambitProperties, true);
	}

	protected synchronized boolean attachToxmatchRouter() {
		return getBooleanPropertyWithDefault(attachToxmatch, ambitProperties, true);
	}

	protected synchronized boolean getBooleanPropertyWithDefault(String name, String config, boolean defaultValue) {
		try {
			String attach = getProperty(name, config);
			if (attach != null && attach.startsWith("${"))
				return defaultValue;
			return attach == null ? defaultValue : Boolean.parseBoolean(attach);
		} catch (Exception x) {
			return defaultValue;
		}
	}

	protected synchronized String getProperty(String name, String config) {
		try {
			Properties p = properties.get(config);
			if (p == null) {
				p = new Properties();
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(config);
				p.load(in);
				in.close();
				properties.put(config, p);
			}
			return p.getProperty(name);

		} catch (Exception x) {
			return null;
		}
	}

	protected synchronized String getPropertyWithDefault(String name, String config, String defaultValue) {
		try {
			String value = getProperty(name, config);
			if (value == null)
				return defaultValue;
			else if (value != null && value.startsWith("${"))
				return defaultValue;
			else
				return value;
		} catch (Exception x) {
			return defaultValue;
		}
	}

	public synchronized String getCustomTitle() {
		return getPropertyWithDefault(custom_title, ambitProperties, "AMBIT");
	}

	public synchronized String getCustomDescription() {
		return getPropertyWithDefault(custom_description, ambitProperties,
				"Chemical structures database, properties prediction & machine learning with OpenTox REST web services API");
	}

	public synchronized String getCustomQuery() {
		return getPropertyWithDefault(custom_query, ambitProperties, "formaldehyde");
	}

	public synchronized String getCustomStructureQuery() {
		return getPropertyWithDefault(custom_structurequery, ambitProperties, "formaldehyde");
	}

	public synchronized String getCustomLogo() {
		String logo = getPropertyWithDefault(custom_logo, ambitProperties, null);
		if ("".equals(logo))
			logo = null;
		return logo;
	}

	public synchronized String getCustomLicense() {
		return getPropertyWithDefault(custom_license, ambitProperties, "AMBIT");
	}

	public synchronized String getSearchServiceURI() {
		String rootUrl = getContext().getParameters().getFirstValue(BASE_URL);
		return getPropertyWithDefault(custom_search, ambitProperties, rootUrl + "/ui/_search");
	}

	protected ConcurrentMap<String, char[]> getLocalSecrets() throws Exception {

		String identifier = getProperty(identifierKey, configProperties);
		String pass = getProperty(identifierPass, configProperties);
		if ((identifier == null) || "".equals(identifier) || identifier.indexOf("${") > -1)
			throw new Exception(
					String.format("Property %s not set. The web application will be READ ONLY!", identifierKey));
		if ((pass == null) || "".equals(pass) || pass.indexOf("${") > -1)
			throw new Exception(
					String.format("Property %s not set. The web application will be READ ONLY!", identifierKey));
		ConcurrentMap<String, char[]> localSecrets = new ConcurrentHashMap<String, char[]>();
		localSecrets.put(identifier, pass.toCharArray());
		return localSecrets;
	}

	/**
	 * Reads the status report level from ambit.pref ${ambit.report.level} If
	 * debug the status will include the stack trace
	 * 
	 * @return
	 */
	protected FreeMarkerStatusService.REPORT_LEVEL getStatusReportLevel() {
		try {
			FreeMarkerStatusService.REPORT_LEVEL aa = FreeMarkerStatusService.REPORT_LEVEL
					.valueOf(getProperty(FreeMarkerStatusService.report_level, ambitProperties));
			if ((getContext() != null) && (getContext().getParameters() != null)
					&& (getContext().getParameters().getFirstValue(FreeMarkerStatusService.report_level)) != null)
				aa = FreeMarkerStatusService.REPORT_LEVEL
						.valueOf(getContext().getParameters().getFirstValue(FreeMarkerStatusService.report_level));
			return aa;
		} catch (Exception x) {
		}
		return FreeMarkerStatusService.REPORT_LEVEL.production;
	}

	protected boolean getConfigChangeLineSeparator() {
		try {
			String attach = getProperty(config_changeLineSeparators, ambitProperties);
			return attach == null ? null : Boolean.parseBoolean(attach);
		} catch (Exception x) {
			return false;
		}
	}

	protected TaskStorage<O> createTaskStorage() {
		return new TaskStorage<O>(getName(), getLogger()) {

			@Override
			protected Task<ITaskResult, O> createTask(O user, ICallableTask callable) {

				return new PolicyProtectedTask(user, !(callable instanceof CallablePolicyCreator)) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -12811434343484170L;

					@Override
					public synchronized void setPolicy() throws Exception {

						super.setPolicy();
					}
				};
			}
		};
	}

	public enum _staticfile {
		meta {
			@Override
			public String getWarPath() {
				return "war:///META-INF";
			}
		},
		images, jmol, jme, jquery, style, report, ui_editor {
			@Override
			public String getPath() {
				return "/ui/png";
			}

			@Override
			public String getWarPath() {
				return "war:///editor/png";
			}
		},
		scripts;
		public String getWarPath() {
			return String.format("war:///%s", name());
		}

		public Directory getDirectory(Context context) {
			return new Directory(context, getWarPath());
		}

		public String getPath() {
			return String.format("/%s/", name());
		}
	};

	/**
	 * Images, styles, icons Works if packaged as war only!
	 * 
	 * @return
	 */
	protected void attachStaticResources(Router router) {

		for (_staticfile dir : _staticfile.values()) {
			router.attach(dir.getPath(), dir.getDirectory(getContext()));
		}

	}

	protected Restlet addOriginFilter(Restlet router) {
		String allowedOrigins = getAllowedOrigins();
		getLogger().info("CORS: Origin filter attached:\t" + allowedOrigins);
		OriginFilter originFilter = new OriginFilter(getContext(), allowedOrigins);
		originFilter.setNext(router);

		// StringWriter w = new StringWriter(); printRoutes(router, "\t", w);
		// System.out.println(w);

		return originFilter;
	}

	protected Filter getBasicAuthFilter(Router router) {
		ChallengeAuthenticator basicAuth = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC,
				"ambit2");
		// get from config file
		ConcurrentMap<String, char[]> localSecrets = null;
		try {
			localSecrets = getLocalSecrets();
		} catch (Exception x) {
			getLogger().log(Level.SEVERE, x.getMessage(), x);
			localSecrets = new ConcurrentHashMap<String, char[]>(); // empty
		}
		basicAuth.setVerifier(new MapVerifier(localSecrets) {
			@Override
			public int verify(Request request, Response response) {
				int result = super.verify(request, response);
				return Method.GET.equals(request.getMethod()) ? RESULT_VALID : result;
			}
		});
		basicAuth.setEnroler(new Enroler() {
			@Override
			public void enrole(ClientInfo clientInfo) {
				if (clientInfo.getUser() != null && clientInfo.isAuthenticated()) {
					// clientInfo.getRoles().add(UPDATE_ALLOWED);
					clientInfo.getRoles().add(DBRoles.adminRole);
					clientInfo.getRoles().add(DBRoles.datasetManager);
				}
			}
		});

		UpdateAuthorizer authorizer = new UpdateAuthorizer();
		authorizer.getAuthorizedRoles().add(DBRoles.adminRole);
		authorizer.getAuthorizedRoles().add(DBRoles.datasetManager);
		authorizer.setNext(router);
		basicAuth.setNext(authorizer);
		return basicAuth;
	}

	protected Restlet createOpenSSOLoginRouter() {
		Filter userAuthn = new OpenSSOAuthenticator(getContext(), true, "opentox.org",
				new OpenSSOVerifierSetUser(false));
		userAuthn.setNext(OpenSSOUserResource.class);
		return userAuthn;
	}

	protected Restlet createProtectedResource(Restlet router) {
		return createProtectedResource(router, null);
	}

	protected Restlet createDBProtectedResource(String usersdbname, Restlet router, String prefix) {
		String secret = getProperty(AMBITConfig.secret.name(), configProperties);
		int sessionLength = 1000 * 60 * 45; // 45 min in milliseconds
		try {
			sessionLength = Integer.parseInt(getProperty(AMBITConfig.sessiontimeout.name(), configProperties));
		} catch (Exception x) {
		}

		Filter dbAuth = UserRouter.createCookieAuthenticator(getContext(), usersdbname,
				"ambit2/rest/config/config.prop", secret, sessionLength);
		// UserAuthorizer authz = new UserAuthorizer();
		Filter authz = UserRouter.createPolicyAuthorizer(getContext(), usersdbname, "ambit2/rest/config/config.prop",
				getBaseURLDepth());
		dbAuth.setNext(authz);
		authz.setNext(router);
		return dbAuth;
	}

	protected Restlet createProtectedResource(Restlet router, String prefix) {
		Filter authN = new OpenSSOAuthenticator(getContext(), false, "opentox.org", new OpenSSOVerifierSetUser(false));
		OpenSSOAuthorizer authZ = new OpenSSOAuthorizer();
		authZ.setPrefix(prefix);
		authN.setNext(authZ);
		authZ.setNext(router);
		return authN;
	}

	public static String printRoutes(Restlet re, String delimiter, StringWriter b) {

		while (re != null) {

			b.append(re.getClass().toString());
			b.append('\t');
			if (re instanceof Finder) {
				b.append(((Finder) re).getTargetClass().getName());
				b.append('\n');
				re = null;
			} else if (re instanceof Filter)
				re = ((Filter) re).getNext();
			else if (re instanceof Router) {
				b.append('\n');
				RouteList list = ((Router) re).getRoutes();
				for (Route r : list)
					if (r instanceof TemplateRoute) {
						TemplateRoute tr = (TemplateRoute) r;
						b.append(delimiter);
						b.append(tr.getTemplate().getPattern());
						b.append('\t');
						b.append(tr.getTemplate().getVariableNames().toString());
						printRoutes(r.getNext(), '\t' + delimiter + tr.getTemplate().getPattern(), b);
					}

				break;
			} else {
				break;
			}

		}

		return b.toString();

	}

	public static void shutdown(Component component) throws Exception {
		component.stop();
		Logger logger = Logger.getLogger(AmbitFreeMarkerApplication.class.getName());
		logger.log(Level.INFO, "Server stopped");
	}

}
