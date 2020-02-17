package ambit2.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.MapVerifier;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;

import ambit2.base.config.Preferences;
import ambit2.rest.aa.UpdateAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOAuthenticator;
import ambit2.rest.aa.opensso.OpenSSOAuthorizer;
import ambit2.rest.aa.opensso.OpenSSOVerifierSetUser;
import ambit2.rest.admin.SimpleGuard.SimpleGuards;
import ambit2.rest.config.AMBITAppConfigInternal;
import ambit2.rest.config.AMBITAppConfigProperties;
import ambit2.rest.freemarker.FreeMarkerApplication;
import ambit2.rest.freemarker.FreeMarkerStatusService;
import ambit2.rest.task.PolicyProtectedTask;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskStorage;
import ambit2.user.rest.resource.DBRoles;
import net.idea.restnet.aa.opensso.policy.CallablePolicyCreator;
import net.idea.restnet.aa.opensso.users.OpenSSOUserResource;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskResult;

public class AmbitFreeMarkerApplication<O> extends FreeMarkerApplication<O> {
	public static final String BASE_URL = "BASE_URL";

	protected boolean insecure = true;
	protected Logger logger = Logger.getLogger(AmbitFreeMarkerApplication.class.getName());
	protected AMBITAppConfigInternal properties_internal = new AMBITAppConfigInternal();
	protected AMBITAppConfigProperties properties_overridable = new AMBITAppConfigProperties(null);

	protected boolean standalone = false;

	protected AMBITAppConfigInternal getProperties_internal() {
		return properties_internal;
	}

	public AMBITAppConfigProperties getProperties_overridable() {
		return properties_overridable;
	}

	public AMBITAppConfigProperties getProperties() {
		return properties_overridable;
	}

	public AmbitFreeMarkerApplication(boolean standalone) {
		super();
		this.standalone = standalone;
		/*
		 * 
		 * 
		 * HOMEPAGE_DEPTH = getBaseURLDepth();
		 * 
		 */

		setProfile(getProperties().getMenuProfile());
		gaCode = getProperties().readGACode();
		versionShort = getProperties().readVersionShort();
		versionLong = getProperties().readVersionLong();
		

		setName(getProperties().getCustomTitle());
		setDescription(getProperties().getCustomDescription());

		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");

		try {
			URL url = getClass().getClassLoader().getResource(AMBITAppConfigProperties.loggingProperties);
			System.setProperty("java.util.logging.config.file", url.getFile());
			try (InputStream in = new FileInputStream(new File(url.getFile()))) {
				LogManager.getLogManager().readConfiguration(in);
				logger.log(Level.INFO, String.format("Logging configuration loaded from %s", url.getFile()));
			}
		} catch (Exception x) {
			System.err.println("logging configuration failed " + x.getMessage());
		}
		setStatusService(new FreeMarkerStatusService(this, getStatusReportLevel()));
		setTunnelService(new TunnelService(true, true) {
			@Override
			public Filter createInboundFilter(Context context) {
				return new AmbitTunnelFilter(context);
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

	@Override
	public boolean isEnableEmailVerification() {
		return getProperties().getEnableEmailVerificationOption();
	}

	@Override
	public String getAjaxTimeout() {
		return getProperties().getAjaxTimeoutOption();
	}

	@Override
	public boolean isSendTokenAsCookie() {
		return getProperties_internal().isDBAAEnabled();
	}

	@Override
	public boolean isSimilarityOrder() {
		return getProperties().getSimilarityOrderOption();
	}

	protected synchronized SimpleGuards isSimpleGuardEnabled() {
		try {
			String guard = getProperties_internal().isSimpleGuardEnabled();
			return guard == null ? null : SimpleGuards.valueOf(guard);
		} catch (Exception x) {
			return null;
		}
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
					.valueOf(getProperties().getReportLevel());
			if ((getContext() != null) && (getContext().getParameters() != null)
					&& (getContext().getParameters().getFirstValue(FreeMarkerStatusService.report_level)) != null)
				aa = FreeMarkerStatusService.REPORT_LEVEL
						.valueOf(getContext().getParameters().getFirstValue(FreeMarkerStatusService.report_level));
			return aa;
		} catch (Exception x) {
		}
		return FreeMarkerStatusService.REPORT_LEVEL.production;
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
		String allowedOrigins = getProperties().getAllowedOrigins();
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
			localSecrets = getProperties().getLocalSecrets();
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
				for (Route r : list) {

					b.append(delimiter);
					b.append(r.getTemplate().getPattern());
					b.append('\t');
					b.append(r.getTemplate().getVariableNames().toString());
					printRoutes(r.getNext(), '\t' + delimiter + r.getTemplate().getPattern(), b);
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
