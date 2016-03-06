package ambit2.rest.aa.opensso.users;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.i.aa.OpenSSOCookie;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ambit2.base.config.AMBITConfig;
import ambit2.rest.StringConvertor;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.algorithm.CatalogResource;

public class OpenSSOUserResource extends CatalogResource<OpenSSOUser> {
	public static final String resource = "opentoxuser";

	public OpenSSOUserResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "login_opensso.ftl";
	}

	@Override
	protected Iterator<OpenSSOUser> createQuery(Context context,
			Request request, Response response) throws ResourceException {

		User user = request.getClientInfo().getUser();
		if (user == null) {
			user = new OpenSSOUser();
			((OpenSSOUser) user).setUseSecureCookie(useSecureCookie(request));
		}
		if (user instanceof OpenSSOUser)
			return new SingleItemIterator<OpenSSOUser>(((OpenSSOUser) user));
		else
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

	@Override
	protected void setTokenCookies(Variant variant, boolean secure) {
		User user = getRequest().getClientInfo().getUser();
		if (user instanceof OpenSSOUser) {
			if ((user == null) || (((OpenSSOUser) user).getToken() == null))
				super.setTokenCookies(variant, secure);
			else {
				OpenSSOCookie.setCookieSetting(this.getResponse()
						.getCookieSettings(), getToken(),
						useSecureCookie(getRequest()));
			}
		}
	}

	@Override
	public IProcessor<Iterator<OpenSSOUser>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		// if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(new OpenSSOUsersURIReporter(getRequest()) {
			/**
			     * 
			     */
			private static final long serialVersionUID = -19016700235414489L;

			@Override
			public void processItem(OpenSSOUser src, Writer output) {
				super.processItem(src, output);
				try {
					output.write('\n');
				} catch (Exception x) {
				}
			}
		}, MediaType.TEXT_URI_LIST);

	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if ((entity != null)
				&& MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			Form form = new Form(entity);

			try {
				OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig
						.getInstance().getOpenSSOService());
				String username = form.getFirstValue("user");
				String pass = form.getFirstValue("password");
				String redirect = form.getFirstValue("targetUri", true);

				if (ssoToken.login(username, pass)) {
					OpenSSOUser user = new OpenSSOUser();
					user.setToken(ssoToken.getToken());
					getRequest().getClientInfo().setUser(user);
					user.setIdentifier(username);
					OpenSSOCookie.setCookieSetting(this.getResponse()
							.getCookieSettings(), ssoToken.getToken(),
							useSecureCookie(getRequest()));

					if (redirect != null)
						this.getResponse().redirectSeeOther(redirect);
					else
						this.getResponse().redirectSeeOther(
								String.format("%s", getRequest().getRootRef()));
					return null;
					/*
					 * queryObject = createQuery(getContext(), getRequest(),
					 * getResponse());
					 * 
					 * return get(variant);
					 */
				} else {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings()
							.removeAll(OpenSSOCookie.CookieName);
					queryObject = createQuery(getContext(), getRequest(),
							getResponse());

					return get(variant);
				}

			} catch (Exception x) {
				throw new ResourceException(new Status(
						Status.SERVER_ERROR_BAD_GATEWAY, x));
			}
		} else
			throw new ResourceException(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);

	}

	@Override
	protected Representation delete() throws ResourceException {
		String token = getToken();
		if (token != null)
			try {
				OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig
						.getInstance().getOpenSSOService());
				if (ssoToken.logout()) {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings()
							.removeAll(OpenSSOCookie.CookieName);
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY, x);
			}
		return get();
	}

	/**
	 * Logout
	 */
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		String token = getToken();
		if (token != null)
			try {
				OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig
						.getInstance().getOpenSSOService());
				ssoToken.setToken(token);
				if (ssoToken.logout()) {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings()
							.removeAll(OpenSSOCookie.CookieName);
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY, x);
			}
		queryObject = createQuery(getContext(), getRequest(), getResponse());
		getRequest().getOriginalRef().setQuery(null);
		getResourceRef(getRequest()).setQuery(null);
		return get(variant);
	}

	protected boolean useSecureCookie(Request request) {
		boolean yes = super.useSecureCookie(request);
		if (yes)
			try {
				return ((OpenSSOUser) request.getClientInfo().getUser())
						.isUseSecureCookie();
			} catch (Exception x) {
				return yes;
			}
		else
			return false;
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		if (getClientInfo().getUser() != null) {
			// map.put("username", getClientInfo().getUser().getIdentifier());
			try {
				map.put("openam_token", ((OpenSSOUser) getClientInfo()
						.getUser()).getToken());
			} catch (Exception x) {
				map.remove("openam_token");
			}
		}
		try {
			map.put("openam_service", OpenSSOServicesConfig.getInstance()
					.getOpenSSOService());
		} catch (Exception x) {
			map.remove("openam_service");
		}
		map.put(AMBITConfig.creator.name(), "IdeaConsult Ltd.");
		map.put(AMBITConfig.ambit_root.name(), getRequest().getRootRef()
				.toString());
		map.put(AMBITConfig.ambit_version_short.name(), app.getVersionShort());
		map.put(AMBITConfig.ambit_version_short.name(), app.getVersionLong());
		map.put(AMBITConfig.googleAnalytics.name(), app.getGACode());
		map.put(AMBITConfig.menu_profile.name(), app.getProfile());

	}
}
