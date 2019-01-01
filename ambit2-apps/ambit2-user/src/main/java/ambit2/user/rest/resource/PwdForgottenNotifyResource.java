package ambit2.user.rest.resource;


public class PwdForgottenNotifyResource  extends AMBITRegistrationNotifyResource {
	
	public PwdForgottenNotifyResource() {
		super();
	}

	@Override
	public String getTemplateName() {
		return "a/pwd_forgotten_notify.ftl";
	}

	
	
}