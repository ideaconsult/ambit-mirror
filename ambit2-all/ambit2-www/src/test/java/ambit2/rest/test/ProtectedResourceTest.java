package ambit2.rest.test;

import org.opentox.aa.opensso.OpenSSOToken;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.task.dsl.ClientResourceWrapper;
import ambit2.rest.task.dsl.IAuthToken;


public abstract class ProtectedResourceTest extends ResourceTest implements IAuthToken  {

	protected String getCreator() {
		if ((ssoToken!=null) && (ssoToken.getToken()!=null)) 
			return	OpenSSOServicesConfig.getInstance().getTestUser();
		else return "test";
	}
	@Override
	public void setUp() throws Exception {
		if (OpenSSOServicesConfig.getInstance().isEnabled()) {
			ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			if (ssoToken.login(
					OpenSSOServicesConfig.getInstance().getTestUser(),
					OpenSSOServicesConfig.getInstance().getTestUserPass()
					)) {
				ClientResourceWrapper.setTokenFactory(this);
			} else
				throw new Exception(String.format("Error logging to SSO (%s)",OpenSSOServicesConfig.getInstance().getTestUser()));
		}
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		try {
			ClientResourceWrapper.setTokenFactory(null);
			if (ssoToken!= null) ssoToken.logout();
		} catch (Exception x) {
		}
	}
	@Override
	public String getToken() {
		return ssoToken==null?null:ssoToken.getToken();
	}
}
