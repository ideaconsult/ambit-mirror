package ambit2.rest.config;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.http.auth.UsernamePasswordCredentials;

import ambit2.base.config.AMBITConfig;
import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.config.Preferences;
import ambit2.rest.freemarker.FreeMarkerStatusService;
import ambit2.rest.wrapper.WrappedService;

public class AMBITAppConfigProperties extends AMBITConfigProperties {
	
	public static final String GUARD_ENABLED = "guard.enabled";
	public static final String GUARD_LIST = "guard.list";
	public static final String ALLOWED_ORIGINS = "allowed.origins";
	public static final String BASEURL_DEPTH = "baseurl.depth";

	public static final String AJAX_TIMEOUT = "ajax.timeout";
	public static final String SIMILARITY_ORDER = "similarity.order";

	protected static final String identifierKey = "aa.local.admin.name";
	protected static final String identifierPass = "aa.local.admin.pass";
	protected static final String version = "ambit.version";
	protected static final String version_build = "ambit.build";
	protected static final String version_timestamp = "ambit.build.timestamp";

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
	protected static final String solr_toplevel = "solr.toplevel.%d";
	protected static final String solr_filter = "solr.filter";

	protected static final String map_folder = "map.folder";
	protected static final String jsonp = "jsonp";
	protected static final String database_create = "database.create";
	protected static final String rdf_writer = "rdf.writer";
	protected static final String dataset_members_prefix = "dataset.members.prefix";
	protected static final String service_ontology = "service.ontology";
	
	public AMBITAppConfigProperties() {
		super(null);
	}


	public AMBITAppConfigProperties(File overridePath) {
		super(overridePath);
	}
	
	public synchronized String getDBNameUsers() {
		return getPropertyWithDefault(AMBITConfig.Database.name(), configProperties, "ambit_users");
	}

	public synchronized String getDBNameAmbit() {
		return getPropertyWithDefault(AMBITConfig.Database.name(), ambitProperties, "ambit2");
	}	

	public synchronized int getBaseURLDepth() {
		try {
			String val = getProperty(BASEURL_DEPTH, ambitProperties);
			return val == null ? 1 : Integer.parseInt(val);
		} catch (Exception x) {
			return 1;
		}
	}

	public synchronized String getSecret() {
		return getPropertyWithDefault(AMBITConfig.secret.name(), configProperties, UUID.randomUUID().toString());
	}	
	
	public synchronized String isSimpleGuardEnabled() {
		return getPropertyWithDefault(GUARD_ENABLED, configProperties, null);
	}


	public synchronized boolean getSimilarityOrderOption() {
		return getBooleanPropertyWithDefault(SIMILARITY_ORDER,ambitProperties,true);		
	}
	
	public synchronized boolean isJSONP() {
		return getBooleanPropertyWithDefault(jsonp,ambitProperties,true);		
	}
	public synchronized boolean isDatasetMembersPrefix() {
		return getBooleanPropertyWithDefault(dataset_members_prefix,ambitProperties,false);		
	}
	public synchronized String getRDFwriter() {
		return getPropertyWithDefault(rdf_writer,ambitProperties,"jena");		
	}
	public synchronized String getOntologyService() {
		return getPropertyWithDefault(service_ontology,ambitProperties,null);		
	}	
	public synchronized boolean allowDatabaseCreate() {
		return getBooleanPropertyWithDefault(database_create,ambitProperties,true);		
	}
	
	public synchronized boolean getEnableEmailVerificationOption() {
		return getBooleanPropertyWithDefault(AMBITConfig.enableEmailVerification.name(), configProperties,true);
	}

	public synchronized String getAjaxTimeoutOption() {
		try {
			String order = getProperty(AJAX_TIMEOUT, ambitProperties);
			return order == null ? "10000" : order.trim();
		} catch (Exception x) {
			return "10000";
		}
	}


	public String[] getGuardListAllowed() {
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

	public synchronized String getMapFolder() {
		try {
			String v = getProperty(map_folder, ambitProperties);
			if ("".equals(v))
				return null;
			return v;
		} catch (Exception x) {
			return null;
		}
	}

	public synchronized String getAllowedOrigins() {
		try {
			return getProperty(ALLOWED_ORIGINS, configProperties);
		} catch (Exception x) {
			return null;
		}
	}

	public synchronized String getMenuProfile() {
		String prefix = getProperty("ambit.profile", ambitProperties);
		if (prefix == null || "".equals(prefix) || prefix.contains("${"))
			prefix = "default";
		return prefix;
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
	

	public boolean getConfigChangeLineSeparator() {
		return getBooleanPropertyWithDefault(config_changeLineSeparators,ambitProperties,false);
	}	

	public synchronized String getCustomLicense() {
		return getPropertyWithDefault(custom_license, ambitProperties, "AMBIT");
	}
	
	public synchronized String getReportLevel() {
		String defaults = "debug";
		String level = getPropertyWithDefault(FreeMarkerStatusService.report_level, ambitProperties, defaults);
		if ("".equals(level))
			level = defaults;
		return level;
	}

	public ConcurrentMap<String, char[]> getLocalSecrets() throws Exception {

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
				solr.setURI(
						new URI(getPropertyWithDefault(String.format(solr_url, i), ambitProperties, null)));
				solr.setCredentials(new UsernamePasswordCredentials(
						getPropertyWithDefault(String.format(solr_basic_user, i), ambitProperties, null),
						getPropertyWithDefault(String.format(solr_basic_password, i), ambitProperties,
								null)));
				try {
					solr.setDummyTopLevel(
						getPropertyWithDefault(String.format(solr_toplevel, i), ambitProperties, null));
				} catch (Exception x) {
					solr.setDummyTopLevel(null);
				}
				solr.setFilterConfig(getPropertyWithDefault(solr_filter, ambitProperties, null));

				services.put(name, solr);
			}

			return services;
		} catch (Exception x) {
			//logger.log(Level.WARNING, x.getMessage());
			return null;
		}
	}
	
	public synchronized String getSearchServiceURI(String rootURL) {
		
		return getPropertyWithDefault(custom_search, ambitProperties, rootURL + "/ui/_search");
	}	
}
